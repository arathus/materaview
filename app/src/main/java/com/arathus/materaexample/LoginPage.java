package com.arathus.materaexample;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class LoginPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        DisplayMetrics deviceDisplayMetrics = new DisplayMetrics();

        // populate the DisplayMetrics object with the display characteristics
        getWindowManager().getDefaultDisplay().getMetrics(deviceDisplayMetrics);

        // get the width and height
        int screenHeight = deviceDisplayMetrics.heightPixels;

        ImageView img = findViewById(R.id.logo_img);

        img.getLayoutParams().width = (screenHeight / 4);
        img.getLayoutParams().height = (screenHeight / 4);
        img.requestLayout();

    }
}
