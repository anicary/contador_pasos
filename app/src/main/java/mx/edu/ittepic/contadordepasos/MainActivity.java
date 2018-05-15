package mx.edu.ittepic.contadordepasos;



import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.prefs.Preferences;

public class MainActivity extends AppCompatActivity  implements SensorEventListener {
    private TextView textView;
    private int numSteps;

    SensorManager sensorManager;
    Sensor countSensor;
    Button[] options;
    boolean isPause=false;
    public final static String PREFS_NAME = "stepcounter_prefs";
    CountDownTimer relog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView=(TextView)findViewById(R.id.cnt);
        options= new Button[2];
        options[0]=(Button)findViewById(R.id.Puase);
        options[1]=(Button)findViewById(R.id.Reset);

        textView.setText(""+  getStepCount(MainActivity.this));

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        try{
            countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            if (countSensor != null) {
                Toast.makeText(this, "Inico de contador de pasos", Toast.LENGTH_LONG).show();
                sensorManager.registerListener(MainActivity.this, countSensor, SensorManager.SENSOR_DELAY_UI);
            } else {
                Toast.makeText(this, "Este dispositivo no es compatible", Toast.LENGTH_LONG).show();
            }
        }catch (NullPointerException e){
            Toast.makeText(this, "Este dispositivo no es compatible", Toast.LENGTH_LONG).show();
        }

        options[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPause){

                    options[0].setText("Pausar");
                    isPause=false;
                    sensorManager.registerListener(MainActivity.this, countSensor, SensorManager.SENSOR_DELAY_UI);
                }else
                {
                    options[0].setText("Contar");
                    isPause=true;
                    sensorManager.unregisterListener(MainActivity.this,sensorManager.getDefaultSensor((Sensor.TYPE_STEP_COUNTER)));
                }
            }
        });
        options[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResetCounter();
            }
        });
        relog= new CountDownTimer(5000,100) {
            @Override
            public void onTick(long l) {
                textView.setText(""+  getStepCount(MainActivity.this));
            }

            @Override
            public void onFinish() {
                start();
            }
        }.start();
    }
    public static String getStepCount(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return String.format("%,d", (prefs.getInt("stepCount", 0) - prefs.getInt("stepCountSubtract", 0)));
    }
    public static void resetStepCount(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putInt("stepCountSubtract", prefs.getInt("stepCount", 0));
        prefsEditor.apply();

    }

    public void ResetCounter(){
        resetStepCount(this);
        textView.setText("0");
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(!isPause){
            SharedPreferences prefs = this.getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor prefsEditor = prefs.edit();
            prefsEditor.putInt("stepCount", (int) sensorEvent.values[0]);
            prefsEditor.apply();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
