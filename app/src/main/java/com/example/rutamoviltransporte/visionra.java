package com.example.rutamoviltransporte;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import in.unicodelabs.kdgaugeview.KdGaugeView;

public class visionra extends AppCompatActivity {

    KdGaugeView speedoMeterView;
    EditText editText;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visionra);
        speedoMeterView = findViewById(R.id.speedMeter);
        editText =findViewById(R.id.editText);
        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int speed = Integer.parseInt(editText.getText().toString());
                speedoMeterView.setSpeed(speed);
            }
        });
    }
}
