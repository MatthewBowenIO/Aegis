package com.assembler.aegis.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.assembler.aegis.R;

import java.nio.ByteBuffer;

/**
 * Created by Matthew on 12/28/2014.
 */
public class EntropyActivity extends ActionBarActivity {
    byte entropy[] = new byte[16];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_capture_entropy);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        float xy = x * y;

        if (entropy.length < 16){
            byte floatBytes[] = ByteBuffer.allocate(4).putFloat(xy).array();
            for (byte Byte : floatBytes){
                if(entropy.length < 16)
                    entropy[entropy.length + 1] = Byte;
            }
        } else {
            SharedPreferences prefs = getSharedPreferences("AegisPrefs", MODE_PRIVATE);
            prefs.edit().putString("entropy", entropy.toString());
            Intent settingsIntent = new Intent(EntropyActivity.this, MainActivity.class);
            startActivityForResult(settingsIntent, 0);
        }

        return true;
    }
}
