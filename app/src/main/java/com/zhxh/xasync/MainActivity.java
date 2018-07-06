package com.zhxh.xasync;

import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import static android.os.Looper.loop;
import static android.os.Looper.prepare;

public class MainActivity extends AppCompatActivity {

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        prepare();
                        TextView tv = new TextView(MainActivity.this);
                        tv.setText("不是通过UI Thread");
                        tv.setTextColor(Color.RED);

                        WindowManager windowManager = MainActivity.this.getWindowManager();
                        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                                600, 100, 0, 0, WindowManager.LayoutParams.FIRST_SUB_WINDOW,
                                WindowManager.LayoutParams.TYPE_TOAST, PixelFormat.OPAQUE);
                        windowManager.addView(tv, params);
                        loop();
                    }
                }.start();
            }
        });


    }
}
