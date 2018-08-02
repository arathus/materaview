package com.arathus.materaexample;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.arathus.matera.MateraView;
import com.arathus.matera.elements.CornerElement;

public class AdvancedCodeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_code);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        LinearLayout constraint = findViewById(R.id.container);

        new MateraView.Builder(this)
                .setNumberOfLayers(4)
                .setParentView(constraint)
                .setStyle(MateraView.FRAME_STRICT)
                .setFirstCornerToDraw(CornerElement.TOP_LEFT)
                .setSecondCornerToDraw(CornerElement.BOTTOM_RIGHT)
                .setColor("#0277BD")
                .build();

    }

}
