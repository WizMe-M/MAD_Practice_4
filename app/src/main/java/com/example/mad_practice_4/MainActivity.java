package com.example.mad_practice_4;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String OUTPUT_TEMPLATE = "X:%.0f;\nY:%.0f;\nZ:%.0f";
    private static final int ROTATION_WAIT_TIME_MS = 100;

    public SensorManager sensorManager;
    public Sensor gyroscope;
    public SensorEventListener sensorEventListener;
    public TextView coordinatesTextView;
    public long previousMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //получаем гироскоп
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        coordinatesTextView = findViewById(R.id.rotateCoordinates);
        previousMillis = 0;

        //устанавливаем обработчики события изменения показаний датчика
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) return;

                if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                    long now = System.currentTimeMillis();

                    //отрисовываем каждые 100 мс
                    if (now - previousMillis > ROTATION_WAIT_TIME_MS) {
                        previousMillis = now;
                        float x, y, z;

                        float[] rotationMatrix = new float[16];
                        SensorManager.getRotationMatrixFromVector(
                                rotationMatrix, event.values);
                        float[] remappedRotationMatrix = new float[16];
                        SensorManager.remapCoordinateSystem(rotationMatrix,
                                SensorManager.AXIS_X,
                                SensorManager.AXIS_Z,
                                remappedRotationMatrix
                        );

                        float[] orientations = new float[3];
                        SensorManager.getOrientation(remappedRotationMatrix, orientations);
                        for(int i = 0; i <3; i++) {
                            orientations[i] = (float)(Math.toDegrees(orientations[i]));
                        }
                        x = orientations[0];
                        y = orientations[1];
                        z = orientations[2];

                        String output = String.format(OUTPUT_TEMPLATE, x, y, z);

                        coordinatesTextView.setText(output);
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorEventListener, gyroscope,
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
    }
}