package com.zhou.lawson.opencvtest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.concurrent.TimeUnit;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static org.opencv.core.CvType.CV_32FC1;

/**
 * Created by lawson on 16/10/24.
 */

public final class Filter2DActivity extends AppCompatActivity {

  private ImageView whiteBoard;
  private TextView instructions;
  private Bitmap bitmap;
  private Bitmap originalBitmap;
  private Subscription subscription;

  Mat kernel;
  private Mat inMat = new Mat();
  private Mat outMat = new Mat();
  private int startSize = 3;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_filter_2d);

    init();
    startToChange();
  }

  private void init() {
    whiteBoard = (ImageView) findViewById(R.id.white_board);
    instructions = (TextView) findViewById(R.id.instructions);
    instructions.setText("原图");

    originalBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test_image_512);
    bitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
    whiteBoard.setImageBitmap(originalBitmap);
  }

  private void startToChange() {
    subscription = Observable.interval(2, TimeUnit.SECONDS)
        .take(5)
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<Long>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {
          }

          @Override public void onNext(Long aLong) {

            int size = startSize + (aLong.intValue() * 2);
            double element = 1 / Math.pow(size, 2);
            handleChange(size, element);
          }
        });
  }

  private void handleChange(int size, double element) {
    Utils.bitmapToMat(originalBitmap, inMat);
    kernel = new Mat(size, size, CV_32FC1, new Scalar(element));  //创建一个充满element值的矩阵
    Imgproc.filter2D(inMat, outMat, inMat.depth(), kernel);
    Utils.matToBitmap(outMat, bitmap);
    whiteBoard.setImageBitmap(bitmap);
    instructions.setText("核矩阵: " + size);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    if (subscription != null) {
      subscription.unsubscribe();
    }

    bitmap.recycle();
    bitmap = null;
    originalBitmap.recycle();
    originalBitmap = null;
  }
}
