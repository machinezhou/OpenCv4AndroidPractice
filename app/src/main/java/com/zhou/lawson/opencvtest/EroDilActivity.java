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

import static org.opencv.imgproc.Imgproc.MORPH_BLACKHAT;
import static org.opencv.imgproc.Imgproc.MORPH_CLOSE;
import static org.opencv.imgproc.Imgproc.MORPH_GRADIENT;
import static org.opencv.imgproc.Imgproc.MORPH_OPEN;
import static org.opencv.imgproc.Imgproc.MORPH_RECT;
import static org.opencv.imgproc.Imgproc.MORPH_TOPHAT;

/**
 * Created by lawson on 16/10/23.
 */

public final class EroDilActivity extends AppCompatActivity implements View.OnClickListener {

  private ImageView whiteBoard;

  private Bitmap bitmap;
  private Bitmap originalBitmap;
  private Mat inMat = new Mat();
  private Mat outMat = new Mat();
  private Mat kernelMat;
  private int currentFilter = R.id.button_original;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_ero_dil);

    init();
  }

  private void init() {
    whiteBoard = (ImageView) findViewById(R.id.white_board);
    findViewById(R.id.button_original).setOnClickListener(this);
    findViewById(R.id.button_erode).setOnClickListener(this);
    findViewById(R.id.button_dilate).setOnClickListener(this);
    findViewById(R.id.button_opening).setOnClickListener(this);
    findViewById(R.id.button_closing).setOnClickListener(this);
    findViewById(R.id.button_morphological_gradient).setOnClickListener(this);
    findViewById(R.id.button_top_hat).setOnClickListener(this);
    findViewById(R.id.button_black_hat).setOnClickListener(this);

    kernelMat = Imgproc.getStructuringElement(MORPH_RECT, new Size(2, 2));
    originalBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test);
    bitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
    whiteBoard.setImageBitmap(originalBitmap);
  }

  @Override public void onClick(View v) {
    int id = v.getId();
    if (currentFilter == id) {
      return;
    }
    switchFilter(id);
    currentFilter = id;
  }

  private void switchFilter(int filter) {
    Utils.bitmapToMat(originalBitmap, inMat);

    switch (filter) {
      case R.id.button_erode:
        /**
         * 矩形: MORPH_RECT 交叉形: MORPH_CROSS 椭圆形: MORPH_ELLIPSE
         * 第三个参数，其size越大，效果越明显，为1的时候没有效果，其值不能为null
         */
        Imgproc.erode(inMat, outMat, kernelMat);
        break;
      case R.id.button_dilate:
        /**
         * 矩形: MORPH_RECT 交叉形: MORPH_CROSS 椭圆形: MORPH_ELLIPSE
         * 第三个参数，其size越大，效果越明显，为1的时候没有效果，其值不能为null
         */
        Imgproc.dilate(inMat, outMat, kernelMat);
        break;
      case R.id.button_opening:
        Imgproc.morphologyEx(inMat, outMat, MORPH_OPEN, kernelMat);
        break;
      case R.id.button_closing:
        Imgproc.morphologyEx(inMat, outMat, MORPH_CLOSE, kernelMat);
        break;
      case R.id.button_morphological_gradient:
        Imgproc.morphologyEx(inMat, outMat, MORPH_GRADIENT, kernelMat);
        break;
      case R.id.button_top_hat:
        Imgproc.morphologyEx(inMat, outMat, MORPH_TOPHAT, kernelMat);
        break;
      case R.id.button_black_hat:
        Imgproc.morphologyEx(inMat, outMat, MORPH_BLACKHAT, kernelMat);
        break;
      case R.id.button_original:
      default:
        inMat.copyTo(outMat);
        break;
    }

    Utils.matToBitmap(outMat, bitmap);
    whiteBoard.setImageBitmap(bitmap);
  }

  @Override protected void onDestroy() {
    super.onDestroy();

    bitmap.recycle();
    bitmap = null;
    originalBitmap.recycle();
    originalBitmap = null;
  }
}
