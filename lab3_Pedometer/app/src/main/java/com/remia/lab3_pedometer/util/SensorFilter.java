package com.remia.lab3_pedometer.util;

/**
 * Created by 23533 on 2018/4/19.
 */

public class SensorFilter {
    private SensorFilter() {
    }

    public static float sum(float[] array) {
        float res = 0;
        for (int i = 0; i < array.length; i++) {
            res += array[i];
        }
        return res;
    }

    public static float[] cross(float[] arrayA, float[] arrayB) {
        float[] resArray = new float[3];
        resArray[0] = arrayA[1] * arrayB[2] - arrayA[2] * arrayB[1];
        resArray[1] = arrayA[2] * arrayB[0] - arrayA[0] * arrayB[2];
        resArray[2] = arrayA[0] * arrayB[1] - arrayA[1] * arrayB[0];
        return resArray;
    }

    public static float norm(float[] array) {
        float res = 0;
        for (int i = 0; i < array.length; i++) {
            res += array[i] * array[i];
        }
        return (float) Math.sqrt(res);
    }


    public static float dot(float[] a, float[] b) {
        float res = a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
        return res;
    }

    public static float[] normalize(float[] a) {
        float[] res = new float[a.length];
        float norm = norm(a);
        for (int i = 0; i < a.length; i++) {
            res[i] = a[i] / norm;
        }
        return res;
    }
}
