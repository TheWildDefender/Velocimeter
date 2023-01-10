package com.gmail.hecarson3.velocimeter;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class CompassInfo {

    private final Context context;
    private final SensorManager sensorManager;
    private final Sensor accelerometer;
    private final Sensor magnetometer;
    private final SensorEventListener accelerometerListener;
    private final SensorEventListener magnetometerListener;
    private float[] deviceAcceleration = new float[3];
    private float[] deviceMagneticField = new float[3];

    // debug
    private int c1 = 0, c2 = 0;

    public CompassInfo(Context context) {
        this.context = context;
        this.sensorManager = context.getSystemService(SensorManager.class);
        this.accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        this.accelerometerListener = new AccelerometerListener();
        this.magnetometerListener = new MagnetometerListener();
    }

    public float getHeading() {
        // R is a 3x3 rotation matrix that transforms device coordinates to world coordinates.
        // If E, N, G are orthonormal vectors in device coordinates that point east, north, and down (gravity), then:
        //     / Ex Ey Ez \
        // R = | Nx Ny Nz |
        //     \ Gx Gy Gz /
        float[] R = new float[9];
        float[] I = new float[9]; // values are discarded
        boolean success = SensorManager.getRotationMatrix(R, I, deviceAcceleration, deviceMagneticField);
        if (!success)
            return Float.NaN;

        // Project device y-axis onto the N-E plane to get heading vector.
        // H = proj_N<0, 1, 0> + proj_E<0, 1, 0>
        // = ( (<0, 1, 0> * N) / (N * N) ) * N + ( (<0, 1, 0> * E) / (E * E) ) * E
        // = (Ny / (N*N)) * N + (Ey / (E*E)) * E
        float[] E = new float[] { R[0], R[1], R[2] };
        float[] N = new float[] { R[3], R[4], R[5] };
        float c1 = N[1] / dotProduct(N, N);
        float c2 = E[1] / dotProduct(E, E);
        float[] H = {
                c1 * N[0] + c2 * E[0],
                c1 * N[1] + c2 * E[1],
                c1 * N[2] + c2 * E[2]
        };

        // Normalize heading vector
        float HMag = (float)Math.sqrt(
                H[0] * H[0] +
                H[1] * H[1] +
                H[2] * H[2]
        );
        H[0] /= HMag;
        H[1] /= HMag;
        H[2] /= HMag;

        // To get compass heading, find the angle between N and H. Since N and H are normalized,
        // N*H = cos(heading angle).
        float headingAngle = (float)Math.acos(dotProduct(N, H));

        // Use dot product with E to determine proper sign of heading angle
        if (dotProduct(H, E) < 0)
            headingAngle = -headingAngle;

        // debug
        float[] orientationValues = new float[3];
        SensorManager.getOrientation(R, orientationValues);
        float headingAngle2 = orientationValues[0]; // old heading

        return headingAngle;
    }

    private float dotProduct(float[] v1, float[] v2) {
        float prod = 0.0f;
        for (int i = 0; i < v1.length; ++i)
            prod += v1[i] * v2[i];
        return prod;
    }

    public void registerSensorListeners() {
        sensorManager.registerListener(accelerometerListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(magnetometerListener, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregisterSensorListeners() {
        sensorManager.unregisterListener(accelerometerListener, accelerometer);
        sensorManager.unregisterListener(magnetometerListener, magnetometer);
    }

    private class AccelerometerListener implements SensorEventListener {
        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {}

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            Log.i(null, "accelerometer event " + c1++);
            deviceAcceleration = sensorEvent.values;
        }
    }

    private class MagnetometerListener implements SensorEventListener {
        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {}

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            Log.i(null, "magnetometer event " + c2++);
            deviceMagneticField = sensorEvent.values;
        }
    }

}