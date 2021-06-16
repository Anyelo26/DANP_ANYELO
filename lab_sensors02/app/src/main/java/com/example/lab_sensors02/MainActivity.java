package com.example.lab_sensors02;

import androidx.appcompat.app.AppCompatActivity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor sensor_gravedad;
    private Sensor magnetometro;
    private float[] valores_magnetometro;
    private float[] valores_gravedad;
    private TextView text_valores_gravedad,text_valores_magnetometro,text_valores_orientacion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text_valores_gravedad = findViewById(R.id.gravedad);
        text_valores_magnetometro = findViewById(R.id.magnetometro);
        text_valores_orientacion= findViewById(R.id.orientacion);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor_gravedad = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        magnetometro = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if(sensor_gravedad==null){
            text_valores_gravedad.setText("Usted no cuenta con sensor de gravedad");
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
                case Sensor.TYPE_GRAVITY: {
                    mensaje += "\n x: " + event.values[0];
                    mensaje += "\n y: " + event.values[1] ;
                    mensaje += "\n z: " + event.values[2] ;
                    valores_gravedad = event.values.clone();
                    text_valores_gravedad.setText(mensaje);
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
        if (valores_gravedad != null && valores_magnetometro != null){
            Matriz_Rotacion = new float[16];
            boolean flag;
            flag =  SensorManager.getRotationMatrix(Matriz_Rotacion,null,valores_gravedad,valores_magnetometro);
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
        if (Math.abs(pitch) <= 20){
            if(roll<=0) {
                txt += "\nHorizontal basico";
            }else{
                txt += "\nHorizontal invertido";
            }
        }else{
            if(pitch <= 0){
                txt += "\nVertical basico";
            }else {
                txt += "\nVertical invertido";
            }
        }
        text_valores_orientacion.setText(txt);
    }
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor_gravedad, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetometro, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }


}