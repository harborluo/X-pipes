package com.harbor.game.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.harbor.game.GameData;
import com.harbor.game.R;
import com.harbor.game.ScoreCalculator;
import com.harbor.game.handler.AnimationHandler;
import com.harbor.game.service.MusicService;
import com.harbor.game.util.Utils;
import com.harbor.game.widget.DialogButtonListener;
import com.harbor.game.widget.DialogMonitor;
import com.harbor.game.widget.ImageAdapter;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GameActivity extends Activity implements View.OnClickListener, DialogButtonListener, DialogMonitor {

    private static String TAG = "GameActivity";

    private int MAX_PIPE_WIDTH = 100;

    private boolean dialogShow = false;

    @Override
    public void onShow() {
        dialogShow = true;
    }

    @Override
    public boolean isDialogPrompted() {
        return dialogShow;
    }

    @Override
    public void onClose() {
        dialogShow =false;
    }

    //    private Handler mainHandler= new Handler();

    public int[] pipeImages = {
            R.mipmap.cross,
            R.mipmap.right_down,
            R.mipmap.right_up,
            R.mipmap.left_down,
            R.mipmap.left_up,
            R.mipmap.vertical,
            R.mipmap.horizontal,
            R.mipmap.cross
    };

    private ImageView nextPipe = null;

    private GridView gridView = null;

    private int pipeWidth = 0;

    private TextView timeRemainTextView;

    private SoundPool soundPool;

    private HashMap<Integer, Integer> soundResources = new HashMap<>();

    private TextView gameScoreTextView;

    private TextView game_required_pipe_count;

    private GameData gameData = null;

    TextView wrenchTextView = null;

    CountDownTimer timer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        wrenchTextView = (TextView) findViewById(R.id.wrench_count);
        game_required_pipe_count = (TextView) findViewById(R.id.game_required_pipe_count);
        gridView = (GridView) findViewById(R.id.pipe_container);
        timeRemainTextView = (TextView) findViewById(R.id.game_time_remain);
        nextPipe = (ImageView) findViewById(R.id.next_pipe_image);
        gameScoreTextView = (TextView) findViewById(R.id.game_score) ;

        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new  SoundPool.Builder().setAudioAttributes(attributes).setMaxStreams(10).build();
        //new SoundPool(1, AudioManager.STREAM_SYSTEM, 0);

        soundResources.put(1, soundPool.load(this, R.raw.pipe_dropped, 1));
        soundResources.put(2, soundPool.load(this, R.raw.pipe_pick_up, 1));
        soundResources.put(3, soundPool.load(this, R.raw.single_pipe_passed, 1));
        soundResources.put(4, soundPool.load(this, R.raw.double_pipe_passed, 1));

        DisplayMetrics displayMetrics = this.getApplicationContext().getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

       // this.gameData = (GameData) getIntent().getSerializableExtra("gameDate");
        this.gameData = getIntent().getParcelableExtra("gameDate");

        if(gameData==null){

            int numOfRows , numOfColumns = 3;
            pipeWidth = screenWidth / numOfColumns;

            while (pipeWidth > MAX_PIPE_WIDTH) {
                numOfColumns++;
                pipeWidth = screenWidth / numOfColumns;
            }

            numOfRows = screenHeight/pipeWidth -1;

            gameData = new GameData(1, numOfRows, numOfColumns);
        }else{

            pipeWidth = screenWidth / gameData.getNumOfColumns();

        }

        Log.i(TAG, "onCreate: pipeWidth = " + pipeWidth+", rows = " +gameData.getNumOfRows()+", cols = "+gameData.getNumOfColumns());

        initGame(true, gameData.getSecondRemain()>0);

    }

    private void initGame(boolean startTimer, final boolean animationOn){

        Log.i(TAG, "initGame: head["+ gameData.getHeadPosition()+"]="+gameData.getHeadImage());

        DisplayMetrics displayMetrics = this.getApplicationContext().getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        gridView.setNumColumns(gameData.getNumOfColumns());
        gridView.setAdapter(new ImageAdapter(this, this, pipeWidth, gameData.getData()));

       // game_required_pipe_count.setText(gameData.getMissionCount()+"");
        refreshMissionCount();

        generateNextPipe();

        LinearLayout panelButtonBar = (LinearLayout) findViewById(R.id.buttonBar);
        int panelHeight = screenHeight - pipeWidth * gameData.getNumOfRows();
        panelButtonBar.setLayoutParams(new LinearLayout.LayoutParams(screenWidth, panelHeight));

        Log.i(TAG, "initGame: panelHeight = "+ panelHeight);

        ImageView wrenchImageView = (ImageView) findViewById(R.id.wrench_image);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(pipeWidth * 8 / 10, pipeWidth * 8 / 10);
        wrenchImageView.setLayoutParams(layoutParams);

        timeRemainTextView.setText(gameData.getSecondRemain()+"");
        wrenchTextView.setText(new String(gameData.getWrenchCount()+ ""));

        if(startTimer==false){
            return;
        }

        GameActivity.this.gameScoreTextView.setText("0");

        timer = new CountDownTimer(gameData.getSecondRemain() * 1000, 1000) {
            @Override
            public void onFinish() {
                Log.i(TAG, "Game time onFinish: ");
                gameData.decreaseSecondRemain();
                timeRemainTextView.setText(gameData.getSecondRemain() + "");
                Intent intent = new Intent(GameActivity.this, MusicService.class);
                stopService(intent);
                calculateScore( animationOn);
            }

            @Override
            public void onTick(long millisUntilFinished) {
                gameData.decreaseSecondRemain();
                timeRemainTextView.setText("" + gameData.getSecondRemain());
                if (GameActivity.this.isFinishing()) {
                    this.cancel();
                }

                if (gameData.getSecondRemain() == 10) {
                    Utils.startMusiceService(GameActivity.this, R.raw.hurry_count_down);
                }

            }

        };

        Utils.startMusiceService(GameActivity.this, gameData.getSecondRemain()>10? R.raw.smooth_count_down:R.raw.hurry_count_down);
//
//        timer.start();

    }

    private void generateNextPipe() {

        int nextImage;
        if(gameData.getNextPipe()!=R.mipmap.blank){
            nextImage = gameData.getNextPipe();
            gameData.setNextPipe(R.mipmap.blank);
        }else{
            Random random = new Random();
             nextImage = pipeImages[random.nextInt(pipeImages.length)];
        }


        nextPipe.setImageResource(nextImage);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(pipeWidth *7/10, pipeWidth *7/10);
        nextPipe.setLayoutParams(layoutParams);
        nextPipe.setTag(nextImage);
    }

    public void refreshMissionCount(){

        int requiredCount = gameData.getMissionCount();

      //  Log.i(TAG, "refreshMissionCount: before head["+ gameData.getHeadPosition()+"]="+gameData.getHeadImage());

        ScoreCalculator calculator = new ScoreCalculator(gameData);

        calculator.execute();

        Log.i(TAG, "refreshMissionCount: requiredCount, = "+ requiredCount+", finishedCount = "+(calculator.getAnimationTaskList().size()-1));

        int remain = requiredCount - calculator.getAnimationTaskList().size() + 1;

        Log.i(TAG, "getAnimationTaskList size is : " + calculator.getAnimationTaskList().size());

        int progress = (calculator.getAnimationTaskList().size()-1) * 100 / gameData.getMissionCount() ;

        gameData.setProgress(progress);

        if(remain < 0){
            remain = 0;
        }

        game_required_pipe_count.setText("" + remain);

    }

    @Override
    public void onClick(View view) {

        if (gameData.getSecondRemain() == 0) {
            return;
        }

        ImageView imageView = (ImageView) view;

        int currentImage = (int) imageView.getTag();

        if (currentImage == R.mipmap.head_down
                || currentImage == R.mipmap.head_up
                || currentImage == R.mipmap.head_left
                || currentImage == R.mipmap.head_right) {
            return;
        }

        if (currentImage == R.mipmap.blank) {

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    soundPool.play(soundResources.get(1), 0.7f, 0.7f, 0, 0, 1);
                }
            }, 0);

            int nextImage = (int) nextPipe.getTag();
            imageView.setImageResource(nextImage);
            imageView.setTag(nextImage);
            generateNextPipe();

            gameData.setDataImage(view.getId(),nextImage);

        } else if (reduceWrenchCount() == true) {

            //pick up the current pipe
            nextPipe.setImageResource(currentImage);
            nextPipe.setTag(currentImage);
            imageView.setImageResource(R.mipmap.blank);
            imageView.setTag(R.mipmap.blank);
            gameData.setDataImage(view.getId(), R.mipmap.blank);

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    soundPool.play(soundResources.get(2),  0.7f, 0.7f, 0, 0, 1);
                }
            }, 0);

        }

        refreshMissionCount();

    }

    AnimationHandler animationHandler = new AnimationHandler();

    private boolean reduceWrenchCount() {

        if (gameData.getWrenchCount()> 0) {
            gameData.decreaseWrenchCount();
            wrenchTextView.setText(new String(gameData.getWrenchCount() + ""));
            return true;
        }
        return false;
    }

    public void calculateScore( final boolean animationOn) {

        ScoreCalculator calculator = new ScoreCalculator(gameData);

        calculator.execute();
        final List<int[]> animationTaskList = calculator.getAnimationTaskList();

        gameData.setPassed(animationTaskList.size()-gameData.getMissionCount()>0);

        if(animationOn){
            for(int[] task : animationTaskList){
                gameData.addTotalScore(task[2]);
            }
        }

        if(animationOn==true){
            saveGame();
        }

        //play animation
        Runnable runnable = new Runnable() {
            int i = 0;
            int total = 0;

            public void run() {

                if(animationHandler.isTerminate()==true){
                    return;
                }

                int[] task = animationTaskList.get(i);

                ImageView imgView = (ImageView) gridView.findViewById(task[0]);

                if(imgView!=null){
                    imgView.setImageResource(task[1]);
                }

                int soundId = task[2]==50?soundResources.get(4):soundResources.get(3);

                if(animationOn==true){
                    soundPool.play(soundId, 0.7f, 0.7f, 0, 0, 1);
                }

                total += task[2];

                gameScoreTextView.setText(total+"");

                i++;

                if (i > animationTaskList.size() - 1 ) {

                    if(animationTaskList.size() -  gameData.getMissionCount() > 0){
                        Utils.showDialog(GameActivity.this,GameActivity.this, "Level succeeded, try next level "+(gameData.getLevel()+1),"Next level","Back");
                    }else{
                        Utils.showDialog(GameActivity.this,GameActivity.this,"Game over, your score is " + total+".","Play again","Back");
                    }

                    return;
                }
                animationHandler.postDelayed(this, animationOn ? 800 : 0);  //for interval...
            }
        };
        animationHandler.postDelayed(runnable, 0); //for initial delay..
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(GameActivity.this, MusicService.class);
        stopService(intent);
        timer.cancel();

        super.onBackPressed();

        //send message to stop the running handler

        animationHandler.sendEmptyMessage(0);

        saveGame();

    }

    private void saveGame(){

        String path = Utils.getDefaultFilePath();
        gameData.setNextPipe((int) this.nextPipe.getTag());
        Utils.saveObject(this.gameData,   path + File.separator + gameData.getName());

    }

    @Override
    public void buttonClicked(String buttonText) {

        onClose();

        Log.i(TAG, "buttonClicked: "+buttonText);

        if(buttonText.equals("Back")){
            this.finish();
            return;
        }else if("Play again".equals(buttonText)){

            String path = Utils.getDefaultFilePath();
            File file = new File(path + File.separator + gameData.getName());
            if(file.exists()){
                file.delete();
            }

            //Start a new game
            this.gameData = new GameData(1, gameData.getNumOfRows(), gameData.getNumOfColumns());
            initGame(true,true);
        }else if("Next level".equals(buttonText)){
           // initGame(this.gameData.isOver()!=false);
            gameData = gameData.nextLevel();
            initGame(true,true);

        }else if("Continue".equals(buttonText)){
            //Game pauses, only start up music service is enough
            Utils.startMusiceService(this, gameData.getSecondRemain()>10? R.raw.smooth_count_down:R.raw.hurry_count_down);
        }
        timer.start();
        GAME_PAUSED=false;
    }

    boolean GAME_PAUSED = false;

    @Override
    protected void onPause() {
        super.onPause();

        GAME_PAUSED = true;

        Log.i(TAG, "onPause event fired, save game automatically." );

        //stop background music
        Intent intent = new Intent(GameActivity.this, MusicService.class);
        stopService(intent);
        timer.cancel();

        saveGame();

     //   animationHandler.sendEmptyMessage(0);

    }

    @Override
    protected void onResume() {

        Log.i(TAG, "onResume called.");

        super.onResume();

        if(GAME_PAUSED==true){
            //wait for the user to click the continue button.
            Utils.showDialog(GameActivity.this,GameActivity.this,"Game paused", "Continue", "Back");
            return;
        }

        Utils.startMusiceService(this, gameData.getSecondRemain()>10? R.raw.smooth_count_down:R.raw.hurry_count_down);

        timer.start();
    }

}