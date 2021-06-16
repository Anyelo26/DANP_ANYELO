package com.example.lab_sensors01;

import androidx.appcompat.app.AppCompatActivity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor acelerometro;
    private Sensor magnetometro;
    private float[] valores_magnetometro;
    private float[] valores_acelerometro;
    private TextView text_valores_acelerometro,text_valores_magnetometro,text_valores_orientacion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text_valores_acelerometro = findViewById(R.id.acelerometro);
        text_valores_magnetometro = findViewById(R.id.magnetometro);
        text_valores_orientacion= findViewById(R.id.orientacion);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometro = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if(acelerometro==null){
            text_valores_acelerometro.setText("Usted no cuenta con acelerometro");
        }
        if(magnetometro==null){
            text_valores_magnetometro.setText("Usted no cuenta con magnetometro");
        }

    }
    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public final void onSensorChanged(SensorEvent event) {
        float[] matriz_rotacion;
        String mensaje = "";
        synchronized (this) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER: {
                    mensaje += "\n x: " + event.values[0] + " m/s^2";
                    mensaje += "\n y: " + event.values[1] + " m/s^2";
                    mensaje += "\n z: " + event.values[2] + " m/s^2";
                    valores_acelerometro = event.values.clone();
                    text_valores_acelerometro.setText(mensaje);
                    matriz_rotacion =  GenerarMatrizRotacion();
                    if (matriz_rotacion != null){
                        determinarOrientacion(matriz_rotacion);
                    }
                    break;
                }
                case Sensor.TYPE_MAGNETIC_FIELD: {
                    mensaje += "\n x: " + event.values[0];
                    mensaje += "\n y: " + event.values[1];
                    mensaje += "\n z: " + event.values[2];
                    valores_magnetometro = event.values.clone();
                    matriz_rotacion =  GenerarMatrizRotacion();
                    text_valores_magnetometro.setText(mensaje);
                    if (matriz_rotacion != null){
                        determinarOrientacion(matriz_rotacion);
                    }
                    break;
                }
            }
        }
    }
    private float[] GenerarMatrizRotacion() {
        float[] Matriz_Rotacion = null;
        if (valores_acelerometro != null && valores_magnetometro != null){
            Matriz_Rotacion = new float[16];
            boolean flag;
            flag =  SensorManager.getRotationMatrix(Matriz_Rotacion,null,valores_acelerometro,valores_magnetometro);
            if (!flag){
                Matriz_Rotacion = null;
            }
        }
        return Matriz_Rotacion;
    }

    private void determinarOrientacion(float[] rotationMatrix) {
        float[] valores_orientacion = new float[3];
        SensorManager.getOrientation(rotationMatrix, valores_orientacion);
        double azimuth = Math.toDegrees(valores_orientacion[0]);
        double pitch = Math.toDegrees(valores_orientacion[1]);
        double roll = Math.toDegrees(valores_orientacion[2]);
        String txt = "";
        txt += "\n z: " + azimuth;
        txt += "\n y: " + pitch;
        txt += "\n x: " + roll;
        text_valores_orientacion.setText(txt);
    }
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetometro, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }


}