package com.remia.lab3_pedometer.util;

/**
 * Created by 23533 on 2018/4/19.
 */

public class StepDetector {
    private static final int ACCEL_SIZE = 50;
    private static final int VEL_SIZE = 10;

    // change this threshold according to your sensitivity preferences
    private static final float STEP_THRESHOLD = 50f;

    private static final int STEP_DELAY_NS = 250000000;

    private int accelCounter = 0;
    private float[] accelX = new float[ACCEL_SIZE];
    private float[] accelY = new float[ACCEL_SIZE];
    private float[] accelZ = new float[ACCEL_SIZE];
    private int velCounter = 0;
    private float[] velRing = new float[VEL_SIZE];
    private long lastStepTimeNs = 0;
    private float oldvEstimate = 0;
    private float vEstimate;
    private int stepNums = 0;


    public void updateAccel(long timeNs, float x, float y, float z) {
        float[] currentAccel = new float[3];
        currentAccel[0] = x;
        currentAccel[1] = y;
        currentAccel[2] = z;

        accelCounter++;
        accelX[accelCounter % ACCEL_SIZE] = currentAccel[0];
        accelY[accelCounter % ACCEL_SIZE] = currentAccel[1];
        accelZ[accelCounter % ACCEL_SIZE] = currentAccel[2];

        float[] worldZ = new float[3];
        worldZ[0] = SensorFilter.sum(accelX) / Math.min(accelCounter, ACCEL_SIZE);
        worldZ[1] = SensorFilter.sum(accelY) / Math.min(accelCounter, ACCEL_SIZE);
        worldZ[2] = SensorFilter.sum(accelZ) / Math.min(accelCounter, ACCEL_SIZE);

        float normalization_factor = SensorFilter.norm(worldZ);

        worldZ[0] = worldZ[0] / normalization_factor;
        worldZ[1] = worldZ[1] / normalization_factor;
        worldZ[2] = worldZ[2] / normalization_factor;

        float currentZ = SensorFilter.dot(worldZ, currentAccel) - normalization_factor;
        velCounter++;
        velRing[velCounter % VEL_SIZE] = currentZ;

        vEstimate = SensorFilter.sum(velRing);

        if (vEstimate > STEP_THRESHOLD && oldvEstimate <= STEP_THRESHOLD
                && (timeNs - lastStepTimeNs > STEP_DELAY_NS)) {
            stepNums ++;
            lastStepTimeNs = timeNs;
        }
        oldvEstimate = vEstimate;
    }

    public int getStepNums(){
        return stepNums;
    }
    public float getaccl(){
        return vEstimate;
    }
}
