package com.project_test_run.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;


public class Mainmenu extends Activity implements OnClickListener {

    private Button btn_start;
    private Button btn_option;
    private Button btn_instruction;
    private Button btn_highscore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);// hide title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide top bar


        setContentView(R.layout.mainmenu);

        btn_start = (Button) findViewById(R.id.btn_start);
        btn_start.setOnClickListener(this);

        btn_option = (Button) findViewById(R.id.btn_option);
        btn_option.setOnClickListener(this);

        //btn_instruction = (Button) findViewById(R.id.btn_instruction);
        //btn_instruction.setOnClickListener(this);

        btn_highscore  = (Button) findViewById(R.id.btn_highscore);
        btn_highscore.setOnClickListener(this);
    }
    public void onClick(View v) {
        Intent intent = new Intent();

        if (v == btn_start) {
            intent.setClass(this, GamePageActivity.class);
        }
        else if(v == btn_option){
            intent.setClass(this, Option.class);
        }
        //else if(v == btn_instruction){
            //intent.setClass(this, Instructionpage.class);
        //}
        else if(v == btn_highscore){
            intent.setClass(this, Highscore.class);
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
