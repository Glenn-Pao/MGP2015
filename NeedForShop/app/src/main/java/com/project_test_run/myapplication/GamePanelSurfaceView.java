package com.project_test_run.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Vibrator;
import android.preference.DialogPreference;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.content.DialogInterface;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.EditText;
import android.text.InputFilter;
import android.text.InputType;

/**
 * Created by teo on 23/11/2015.
 */
public class GamePanelSurfaceView extends SurfaceView implements SurfaceHolder.Callback{
    // Implement this interface to receive information about changes to the surface.

    private GameThread myThread = null; // Thread to control the rendering

    // 1a) Variables used for background rendering
    private Bitmap bg, scaledbg;
    // 1b) Define Screen width and Screen height as integer
    int Screenwidth, Screenheight;
    // 1c) Variables for defining background start and end point
    private short bgX = 0,bgY = 0;
    // 4a) bitmap array to stores 4 images of the spaceship
    private Bitmap[] Spaceship = new Bitmap[4];
    // 4b) Variable as an index to keep track of the spaceship images
    MediaPlayer bgm;
    int SCORE = 0;
    boolean Jump = false;
    boolean Ground = true;
    Paint paint = new Paint();
    private short mX= 10, mY= 100;
    private SpriteAnimation stone_anim;
    private SpriteAnimation character;
    private boolean movecharacter = false;
    private SoundPool sounds;
    private int soundcorrect,soundwrong,soundbonus;
    private boolean pausepress = true;
    private Objects Pause1;
    private Objects Pause2;
    int action;
    // Variables for FPS
    public float FPS;
    float deltaTime;
    long dt;
    public boolean showAlert = false;
    AlertDialog.Builder alert = null;
    Activity activityTracker;
    public boolean showed = false;
    private Alert AlertObj;
    public Vibrator v;
    // Variable for Game State check
    private short GameState;

    //constructor for this GamePanelSurfaceView class
    public GamePanelSurfaceView (Context context, Activity activity){

        // Context is the current state of the application/object
        super(context);

        //To track an activity
        activityTracker = activity;
        // Adding the callback (this) to the surface holder to intercept events
        getHolder().addCallback(this);

        // 1d) Set information to get screen size
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        Screenwidth = metrics.widthPixels;
        Screenheight = metrics.heightPixels;
        // 1e)load the image when this class is being instantiated
        // 1e)load the image when this class is being instantiated
        bg = BitmapFactory.decodeResource(getResources(),R.drawable.office_background);
        scaledbg = Bitmap.createScaledBitmap(bg, Screenwidth, Screenheight, true);
        // 4c) Load the images of the spaceships
        Spaceship[0] = BitmapFactory.decodeResource(getResources(),R.drawable.ship2_1);
        Spaceship[1] = BitmapFactory.decodeResource(getResources(), R.drawable.ship2_2);
        Spaceship[2] = BitmapFactory.decodeResource(getResources(),R.drawable.ship2_3);
        Spaceship[3] = BitmapFactory.decodeResource(getResources(),R.drawable.ship2_4);

        stone_anim = new SpriteAnimation(BitmapFactory.decodeResource(getResources(), R.drawable.flystone),320,64,5,5);
        character = new SpriteAnimation(BitmapFactory.decodeResource(getResources(),R.drawable.character),500,64,5,7);//10,10 = speed

        stone_anim.setX(600);
        character.setX(300);
        character.setY(100);
        // Create the game loop thread
        myThread = new GameThread(getHolder(), this);
        //load audio clip
        sounds  = new SoundPool(2, AudioManager.STREAM_MUSIC,0);
        soundcorrect=sounds.load(context,R.raw.correct,1);
        soundwrong = sounds.load(context,R.raw.incorrect,1);
        bgm = MediaPlayer.create(context, R.raw.background_music);
        Pause1 = new Objects(BitmapFactory.decodeResource(getResources(),R.drawable.pause),72,72);
        Pause2 = new Objects(BitmapFactory.decodeResource(getResources(),R.drawable.pause1),72,72);
        // Make the GamePanel focusable so it can handle events
        setFocusable(true);
        //Create Alert dialog
        AlertObj = new Alert(this);
        alert = new AlertDialog.Builder(getContext());
        //Allow player to input their names
        final EditText input = new EditText(getContext());
        //define the input method where enter key is disabled
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        //Define max of 20 characters to be entered for name field
        int maxlength = 20;
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(maxlength);
        input.setFilters(FilterArray);
        //Setup the alert dialog
        alert.setCancelable(false);
        alert.setView(input);
        alert.setPositiveButton("ok", new DialogInterface.OnClickListener()
        {
            //do something when the button is clicked
            public void onClick(DialogInterface arg0,int arg1){
                Intent intent = new Intent();
                intent.setClass(getContext(),Splashpage.class);
                activityTracker.startActivity(intent);
            }
        });
    }
    public void RenderPause(Canvas canvas)
    {
        canvas.drawBitmap(Pause1.getBitmap(),Pause1.getX(),Pause2.getY(),null);
        if(pausepress == true)
        {
            canvas.drawBitmap(Pause2.getBitmap(),Pause2.getX(),Pause2.getY(),null);
        }
        else
        {
            canvas.drawBitmap(Pause1.getBitmap(),Pause1.getX(),Pause1.getY(),null);
            pausepress = false;
        }
    }

    //must implement inherited abstract methods
    public void surfaceCreated(SurfaceHolder holder){
        // Create the thread
        if (!myThread.isAlive()){
            myThread = new GameThread(getHolder(), this);
            myThread.startRun(true);
            myThread.start();
            bgm.setVolume(50.8f, 50.8f);
            bgm.start();

        }
    }

    public void surfaceDestroyed(SurfaceHolder holder){
        // Destroy the thread
        if (myThread.isAlive()){
            myThread.startRun(false);
            bgm.stop();
            bgm.release();
            sounds.unload(soundcorrect);
            sounds.unload(soundwrong);
            sounds.release();
        }
        boolean retry = true;
        while (retry) {
            try {
                myThread.join();
                retry = false;
            }
            catch (InterruptedException e)
            {
            }
        }
    }

    public boolean CheckCollision(int x1,int y1,int w1, int h1,int x2, int y2, int w2,int h2)
    {
        if(     (x1 < x2 + w2) &&
                (x1 + w1 > x2) &&
                (y1 < y2 + h2) &&
                (h1 + y1 > y2)  )
        {
            return true;
        }
        return false;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){

    }

    public void RenderGameplay(Canvas canvas) {
        // 2) Re-draw 2nd image after the 1st image ends
        if(canvas == null){
            return;
        }
        canvas.drawBitmap(scaledbg, bgX, bgY, null);
        canvas.drawBitmap(scaledbg, bgX + Screenwidth, bgY, null);

        paint.setARGB(255, 0, 0, 0);
        paint.setStrokeWidth(100);
        paint.setTextSize(30);
        canvas.drawText("FPS: " + FPS, 130, 75, paint);
        canvas.drawText("Score: "+ SCORE,130,25,paint);
        stone_anim.draw(canvas);
        character.draw(canvas);
        // Bonus) To print FPS on the screen
        //String f_Fps = String.valueOf(FPS);
        //canvas.drawText(f_Fps,100,100,null);
        RenderPause(canvas);
    }


    //Update method to update the game play
    public void update(float dt, float fps){
        FPS = fps;
        switch (GameState) {
            case 0: {
                // 3) Update the background to allow panning effect
                bgX -= 500 *dt;
                if(bgX < - Screenwidth)
                {
                    bgX = 0;
                }

                character.update(System.currentTimeMillis());
                stone_anim.update(System.currentTimeMillis());
                if(showAlert == true && showed==false)
                {
                    showed = true;
                    alert.setMessage("GameOver");
                    AlertObj.RunAlert();
                    showAlert = false;
                    showed = false;
                }

                if(CheckCollision(mX, mY, stone_anim.getSpriteWidth(), character.getSpriteHeight(), stone_anim.getX(), stone_anim.getY(), stone_anim.getSpriteWidth(), stone_anim.getSpriteHeight())) {
                    sounds.play(soundcorrect,10.0f,10.0f,0,0,1.5f);
                    SCORE += 100;
                    startVibrate();
                    if(SCORE >=10000){
                       // showAlert = true;
                    }
                }
                break;
            }

        }

    }

    // Rendering is done on Canvas
    public void doDraw(Canvas canvas){
        switch (GameState)
        {
            case 0:
                RenderGameplay(canvas);
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // 5) In event of touch on screen, the spaceship will relocate to the point of touch
        int action = event.getAction();
        // 5) In event of touch on screen, the spaceship will relocate to the point of touch
        short X = (short) event.getX();
        short Y = (short) event.getY();

        //Doing a drag motion
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            {
                mX = (short) (X - character.getSpriteWidth()/2);//Spaceship[SpaceshipIndex].getWidth() / 2);
                mY = (short) (Y - character.getSpriteWidth()/2);//Spaceship[SpaceshipIndex].getHeight() / 2);

                if (CheckCollision(mX, mY,  stone_anim.getSpriteWidth(),stone_anim.getSpriteHeight(), X, Y, 0, 0))
                {
                    movecharacter = true;
                }
                else
                {
                    movecharacter = false;
                }
                if (CheckCollision(Pause1.getX(), Pause1.getY(), Pause1.getWidth(), Pause1.getHeight(), X, Y, 0, 0))
                {
                    if (!pausepress)
                    {
                        pausepress = true;
                        myThread.pause();
                        sounds.play(soundcorrect, 1.0f, 1.0f, -1, 0, 1.5f);
                        bgm.pause();
                    }
                    else
                    {
                        pausepress = false;
                        myThread.unPause();
                        sounds.play(soundcorrect, 1.0f, 1.0f, -1, 0, 1.5f);
                        bgm.start();
                    }
                }
            }
            break;

            case MotionEvent.ACTION_MOVE:
            {
                mX = (short) (X - character.getSpriteWidth()/2);//Spaceship[SpaceshipIndex].getWidth() / 2);
                mY = (short) (Y - character.getSpriteWidth()/2);//Spaceship[SpaceshipIndex].getHeight() / 2);
                character.setX(mX);
                character.setY(mY);
            }
            break;
        }
        return true;
        //  return super.onTouchEvent(event);
        //}
    }
    public void startVibrate()
    {
        long pattern[] = {0,200,500};
        v= (Vibrator)getContext().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(pattern,-1);
    }
    public void stopVibrate()
    {
        v.cancel();
    }
}
