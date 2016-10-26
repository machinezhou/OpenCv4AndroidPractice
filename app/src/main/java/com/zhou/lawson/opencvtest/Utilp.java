package com.zhou.lawson.opencvtest;

import android.util.Log;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.Arrays;
import org.opencv.core.Mat;

/**
 * Created by lawson on 16/10/24.
 */

public class Utilp {
  public static String printMat(Mat src) {

    int rows = src.rows();
    int cols = src.cols();

    StringBuilder log = new StringBuilder("Mat [ ");
    log.append(rows).append("*").append(cols).append("*").append(src.type()).append("\r\n");
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        log.append("row : ").append(i).append("col : ").append(j).append(" ");
        log.append(Arrays.toString(src.get(i, j))).append("\r\n");
      }
    }
    log.append(" ]");
    Log.e("LOG ", log.toString());
    return log.toString();
  }

  public static void printArray(double[] array) {
    StringBuilder log =
        new StringBuilder("Array total ").append(array.length).append(" [").append("\n\r");
    for (double aTemp : array) {
      log.append(aTemp).append("\r\n");
    }
    log.append(" ]");
    Log.e("LOG ", log.toString());
  }

  public static String printArray(float[] array) {
    StringBuilder log =
        new StringBuilder("Array total ").append(array.length).append(" [").append("\n\r");
    for (float aTemp : array) {
      log.append(aTemp).append("\r\n");
    }
    log.append(" ]");
    Log.e("LOG ", log.toString());
    return log.toString();
  }

  public static void writeTxtFile(String content, String path) {
    String strContent = content + "\n";
    try {
      File file = new File(path);
      if (!file.exists()) {
        Log.d("TestFile", "Create the file:" + path);
        file.createNewFile();
      }
      RandomAccessFile raf = new RandomAccessFile(file, "rw");
      raf.seek(file.length());
      raf.write(strContent.getBytes());
      raf.close();
    } catch (Exception e) {
      Log.e("TestFile", "Error on write File.");
    }
  }
}
