package com.zhou.lawson.opencvtest;

import android.util.Log;
import java.util.Arrays;
import org.opencv.core.Mat;

/**
 * Created by lawson on 16/10/24.
 */

public class Utilp {
  public static void printMat(Mat src) {
    for (int i = 0; i < src.width(); i++) {
      for (int j = 0; j < src.height(); j++) {
        Log.e("--->>>>   mat  array   ", Arrays.toString(src.get(i, j)) + "");
      }
    }
  }
}
