package com.zhou.lawson.opencvtest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import java.util.Random;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import static org.opencv.core.Core.BORDER_CONSTANT;
import static org.opencv.core.Core.BORDER_REPLICATE;

/**
 * Created by lawson on 16/10/24.
 */

public final class BorderActivity extends AppCompatActivity implements View.OnClickListener {

  private ImageView whiteBoard;

  private Bitmap bitmap;
  private Bitmap originalBitmap;
  private Mat inMat = new Mat();
  private Mat outMat = new Mat();
  private int currentType = R.id.button_original;

  final Random random = new Random();

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_border);

    init();
  }

  private void init() {
    whiteBoard = (ImageView) findViewById(R.id.white_board);
    findViewById(R.id.button_original).setOnClickListener(this);
    findViewById(R.id.button_border_constant).setOnClickListener(this);
    findViewById(R.id.button_border_replicate).setOnClickListener(this);

    originalBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test_image_512);
    bitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
    //此处需要设置imageview或其父view的背景，否则添加的border显示不出
    whiteBoard.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray));
    whiteBoard.setImageBitmap(originalBitmap);
  }

  private void switchType(int type) {
    Utils.bitmapToMat(originalBitmap, inMat);

    int topBottom = (int) (0.05 * inMat.rows());
    int leftRight = (int) (0.05 * inMat.cols());
    //opencv4android并不提供RNG类，因此需要java.util.Random代劳
    int r = random.nextInt(256);
    int g = random.nextInt(256);
    int b = random.nextInt(256);
    Core.copyMakeBorder(inMat, outMat, topBottom, topBottom, leftRight, leftRight, type,
        new Scalar(r, g, b));
    bitmap = Bitmap.createBitmap(outMat.width(), outMat.height(), bitmap.getConfig());
    Utils.matToBitmap(outMat, bitmap);
    whiteBoard.setImageBitmap(bitmap);
  }

  @Override public void onClick(View v) {
    int id = v.getId();
    if (currentType == id) {
      return;
    }

    switch (id) {
      case R.id.button_border_constant:
        switchType(BORDER_CONSTANT);
        break;
      case R.id.button_border_replicate:
        switchType(BORDER_REPLICATE);
        break;
      case R.id.button_original:
      default:
        whiteBoard.setImageBitmap(originalBitmap);
        break;
    }

    currentType = id;
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    bitmap.recycle();
    bitmap = null;
    originalBitmap.recycle();
    originalBitmap = null;
  }
}
