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
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import static org.opencv.imgproc.Imgproc.COLOR_BGR2BGRA;
import static org.opencv.imgproc.Imgproc.COLOR_BGRA2BGR;

/**
 * Created by lawson on 16/10/22.
 */

public final class SmoothingProActivity extends AppCompatActivity implements View.OnClickListener {

  private ImageView whiteBoard;

  private Bitmap bitmap;
  private Bitmap originalBitmap;
  private Mat inMat = new Mat();
  private Mat outMat = new Mat();
  private int currentFilter = R.id.button_original;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_smoothing);

    init();
  }

  private void init() {
    whiteBoard = (ImageView) findViewById(R.id.white_board);
    findViewById(R.id.button_original).setOnClickListener(this);
    findViewById(R.id.button_homogeneous_blur).setOnClickListener(this);
    findViewById(R.id.button_gaussian_blur).setOnClickListener(this);
    findViewById(R.id.button_median_blur).setOnClickListener(this);
    findViewById(R.id.button_bilateral_blur).setOnClickListener(this);

    originalBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test);
    bitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
    whiteBoard.setImageBitmap(originalBitmap);
  }

  private void switchFilter(int filter) {
    Utils.bitmapToMat(originalBitmap, inMat);

    switch (filter) {
      case R.id.button_homogeneous_blur:
        Imgproc.blur(inMat, outMat, new Size(3, 3));  // size的值越小，模糊程度越低
        break;
      case R.id.button_median_blur:
        Imgproc.medianBlur(inMat, outMat, 3); //ksize越小，模糊程度越低
        break;
      case R.id.button_gaussian_blur:
        Imgproc.GaussianBlur(inMat, outMat, new Size(3, 3), 0);  //ksize越小，模糊程度越低
        break;
      case R.id.button_bilateral_blur:
        Mat inTemp = new Mat();
        Imgproc.cvtColor(inMat, inTemp, COLOR_BGRA2BGR);
        Mat outTemp = new Mat(inTemp.size(), inTemp.type());
        /**
         * 由于源码中存在断言代码，因此限制如下：
         * <code>
         *   CV_Assert( (src.type() == CV_8UC1 || src.type() == CV_8UC3) &&
         * src.type() == dst.type() && src.size() == dst.size() &&
         * src.data != dst.data );
         *   <code/>
         *
         *   且distance为奇数
         */
        Imgproc.bilateralFilter(inTemp, outTemp, 13, 20, 20); //后三个值都会呈现值越小，卡通效果越小的效果
        Imgproc.cvtColor(outTemp, outMat, COLOR_BGR2BGRA);
        break;
      case R.id.button_original:
      default:
        inMat.copyTo(outMat);
        break;
    }

    Utils.matToBitmap(outMat, bitmap);
    whiteBoard.setImageBitmap(bitmap);

    currentFilter = filter;
  }

  @Override public void onClick(View v) {
    int id = v.getId();
    if (currentFilter == id) {
      return;
    }
    switchFilter(id);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    bitmap.recycle();
    bitmap = null;
    originalBitmap.recycle();
    originalBitmap = null;
  }
}
