package com.hotpatch.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
    private Calculator mCal;
    private Button mBtnCalculate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnCalculate = (Button)findViewById(R.id.btnCalculate);

        mBtnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCal == null) {
                    mCal = new Calculator();
                }

                Toast.makeText(MainActivity.this, String.valueOf(mCal.calculate()), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
