package com.arathus.materaexample;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.arathus.matera.MateraView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        android.support.constraint.ConstraintLayout constraintLayout = findViewById(R.id.container);
        // constraintLayout.addView(new MateraView.Builder(this).setBackgroundColor("#FFFFFF").build(), new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT));


        new MateraView.Builder(this).setParentView(constraintLayout).build();


    }

    public void buttonClick(View view) {

        switch (view.getId()) {

            case R.id.button1:
                Intent i = new Intent(MainActivity.this, LoginPage.class);
                startActivity(i);
                break;

        }

    }
}
