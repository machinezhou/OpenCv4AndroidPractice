package com.zhou.lawson.opencvtest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.SeekBar;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import static org.opencv.imgproc.Imgproc.COLOR_BGRA2GRAY;

/**
 * Created by lawson on 16/10/25.
 */

public final class CannyActivity extends AppCompatActivity
    implements SeekBar.OnSeekBarChangeListener {

  public static final int RATIO = 3;

  private ImageView whiteBoard;

  private Mat inMat = new Mat();
  private Bitmap bitmap;
  private Bitmap originalBitmap;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_canny);

    init();
  }

  private void init() {
    whiteBoard = (ImageView) findViewById(R.id.white_board);
    ((SeekBar) findViewById(R.id.seek_bar)).setOnSeekBarChangeListener(this);

    originalBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test_image_512);
    bitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
    whiteBoard.setImageBitmap(originalBitmap);
  }

  private void canny(int threshold) {
    Utils.bitmapToMat(originalBitmap, inMat);
    Mat inTemp = new Mat();
    Imgproc.cvtColor(inMat, inTemp, COLOR_BGRA2GRAY);
    Mat edges = new Mat();
    Imgproc.blur(inTemp, edges, new Size(3, 3));
    Imgproc.Canny(edges, edges, threshold, threshold * RATIO, 3, false);
    Mat outMat = new Mat(edges.size(), edges.type(), Scalar.all(0));
    inMat.copyTo(outMat, edges);

    Utils.matToBitmap(outMat, bitmap);
    whiteBoard.setImageBitmap(bitmap);
  }

  @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

  }

  @Override public void onStartTrackingTouch(SeekBar seekBar) {

  }

  @Override public void onStopTrackingTouch(SeekBar seekBar) {
    canny(seekBar.getProgress());
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    bitmap.recycle();
    bitmap = null;
    originalBitmap.recycle();
    originalBitmap = null;
  }
}
