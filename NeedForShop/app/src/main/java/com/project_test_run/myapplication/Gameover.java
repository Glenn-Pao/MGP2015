package com.project_test_run.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;


/**
 * Created by glen on 17/11/2015.
 */
public class Gameover extends Activity implements OnClickListener{
    private Button btn_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);// hide title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide top bar


        setContentView(R.layout.gameover);

        btn_menu = (Button) findViewById(R.id.btn_menu);
        btn_menu.setOnClickListener(this);

    }
    public void onClick(View v) {
        Intent intent = new Intent();

        if (v == btn_menu) {
            intent.setClass(this, Mainmenu.class);
        }
        startActivity(intent);
    }

    protected void onPause(){
        super.onPause();
    }

    protected void onStop(){
        super.onStop();
    }

    protected void onDestroy(){
        super.onDestroy();
    }
}
