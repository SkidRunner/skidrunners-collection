package com.skidrunner.mygame;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.content.pm.ActivityInfo;

public class MainActivity extends Activity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(
			WindowManager.LayoutParams.FLAG_FULLSCREEN,
       		WindowManager.LayoutParams.FLAG_FULLSCREEN |
			WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED |
			WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setRequestedOrientation(
			ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        setContentView(R.layout.activity_main);
    }
}
