package com.zhou.lawson.opencvtest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

/**
 * Created by lawson on 16/10/26.
 */

public final class AffineActivity extends AppCompatActivity implements View.OnClickListener {

  private ImageView whiteBoard;

  private Bitmap bitmap;
  private Bitmap originalBitmap;
  private Mat inMat = new Mat();
  private int currentFilter = R.id.button_original;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_affine);

    init();
  }

  private void init() {
    whiteBoard = (ImageView) findViewById(R.id.white_board);
    findViewById(R.id.button_original).setOnClickListener(this);
    findViewById(R.id.button_warp_affine).setOnClickListener(this);
    findViewById(R.id.button_rotate_matrix_2d).setOnClickListener(this);

    originalBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test);
    bitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
    whiteBoard.setImageBitmap(originalBitmap);
  }

  private void switchFilter(int filter) {
    Utils.bitmapToMat(originalBitmap, inMat);

    int rows = inMat.rows();
    int cols = inMat.cols();

    Mat warpDst = Mat.zeros(rows, cols, inMat.type());
    Mat warpRotateDst = new Mat();

    MatOfPoint2f srcTri =
        new MatOfPoint2f(new Point(0, 0), new Point(cols - 1, 0), new Point(0, rows - 1));
    MatOfPoint2f dstTri =
        new MatOfPoint2f(new Point(0, rows * 0.33), new Point(cols * 0.85, rows * 0.25),
            new Point(cols * 0.15, rows * 0.7));

    Imgproc.warpAffine(inMat, warpDst, Imgproc.getAffineTransform(srcTri, dstTri), warpDst.size());

    if (R.id.button_warp_affine == filter) {
      Utils.matToBitmap(warpDst, bitmap);
    } else {
      Point center = new Point(warpDst.cols() / 2, warpDst.rows() / 2);
      double angle = -50.0;
      double scale = 0.6;
      Imgproc.warpAffine(warpDst, warpRotateDst, Imgproc.getRotationMatrix2D(center, angle, scale),
          warpDst.size());
      Utils.matToBitmap(warpRotateDst, bitmap);
    }
    whiteBoard.setImageBitmap(bitmap);
    currentFilter = filter;
  }

  @Override public void onClick(View v) {
    int id = v.getId();
    if (currentFilter == id) {
      return;
    }
    if (R.id.button_original == id) {
      whiteBoard.setImageBitmap(originalBitmap);
      currentFilter = id;
    } else {
      switchFilter(id);
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    bitmap.recycle();
    bitmap = null;
    originalBitmap.recycle();
    originalBitmap = null;
  }
}
