package com.saihtoo.workouttimermobileapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Chronometer myTime;
    EditText inputText;
    TextView displayText;

    Boolean watchState;
    String workoutType, recordedTime;
    long extraTime, currentTime;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputText = findViewById(R.id.inputEditText);
        displayText = findViewById(R.id.displayTextView);

        myTime = findViewById(R.id.chronometer);
        watchState = false;
        extraTime = 0L;

        sharedPreferences = getSharedPreferences("Info", MODE_PRIVATE);
        displayText.setText(String.format("Time: %s || Type: %s", sharedPreferences.getString("Previous Time", ""),
                sharedPreferences.getString("User Input", "")));

        if (savedInstanceState != null) {
            workoutType = savedInstanceState.getString("Workout Type");
            extraTime = savedInstanceState.getLong("Extra Time");
            watchState = savedInstanceState.getBoolean("Timer State");
            currentTime = savedInstanceState.getLong("Current Time");
            recordedTime = savedInstanceState.getString("Recorded Time");
            if (!watchState) {
                myTime.setBase(SystemClock.elapsedRealtime() - extraTime);
            } else {
                myTime.setBase(currentTime);
                myTime.start();
            }
        }
    }

    public void startTimer(View view) {
        if (!watchState) {
            myTime.setBase(SystemClock.elapsedRealtime() - extraTime);
            myTime.start();
            watchState = true;
        }
    }

    public void pauseTimer(View view) {
        if (watchState) {
            myTime.stop();
            extraTime = SystemClock.elapsedRealtime() - myTime.getBase();
            watchState = false;
        }
    }

    public void stopTimer(View view) {
        workoutType = inputText.getText().toString();
        recordedTime = myTime.getText().toString();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("User Input", workoutType);
        editor.putString("Previous Time", recordedTime);
        editor.apply();
        displayText.setText(String.format("Time: %s || Type: %s", recordedTime, workoutType));
        myTime.stop();
        myTime.setBase(SystemClock.elapsedRealtime());
        extraTime = 0L;
        watchState = false;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("Workout Type", workoutType);
        outState.putLong("Extra Time", extraTime);
        outState.putBoolean("Timer State", watchState);
        outState.putString("Recorded Time", recordedTime);
        currentTime = myTime.getBase();
        outState.putLong("Current Time", currentTime);
    }
}