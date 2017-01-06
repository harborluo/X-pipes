package com.harbor.game.activity;

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
import com.harbor.game.util.DBHelper;
import com.harbor.game.util.Utils;
import com.harbor.game.widget.DialogButtonListener;
import com.harbor.game.widget.DialogMonitor;
import com.harbor.game.widget.ImageAdapter;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GameActivity extends AbstractActivity implements View.OnClickListener, DialogButtonListener, DialogMonitor {

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

    Integer[] pipeImages = {
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

    private TextView wrenchTextView = null;

    CountDownTimer gameTimer = null;

    DBHelper dbHelper = null;

    private TextView gameLevelTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        dbHelper = new DBHelper(this);

        wrenchTextView = (TextView) findViewById(R.id.wrench_count);
        game_required_pipe_count = (TextView) findViewById(R.id.game_required_pipe_count);
        gridView = (GridView) findViewById(R.id.pipe_container);
        timeRemainTextView = (TextView) findViewById(R.id.game_time_remain);
        nextPipe = (ImageView) findViewById(R.id.next_pipe_image);

        gameScoreTextView = (TextView) findViewById(R.id.game_score) ;
        gameLevelTextView = (TextView) findViewById(R.id.game_level);

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

        this.gameData = (GameData) getIntent().getSerializableExtra("gameData");
       // this.gameData = getIntent().getParcelableExtra("gameDate");

        if(gameData==null){

            int defaultLevel = getIntent().getIntExtra("gameLevel",0);

            String mode="Challenge";

            if(defaultLevel==0){
                mode="Level";
                defaultLevel = 1;
            }

            int numOfRows , numOfColumns = 3;
            pipeWidth = screenWidth / numOfColumns;

            while (pipeWidth > MAX_PIPE_WIDTH) {
                numOfColumns++;
                pipeWidth = screenWidth / numOfColumns;
            }

            numOfRows = screenHeight/pipeWidth -1;

            gameData = new GameData(defaultLevel, numOfRows, numOfColumns, mode);

        }else{

           //gameData.resetSecondRemain(30);
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

        gameLevelTextView.setText(""+gameData.getLevel());

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

        if(gameTimer!=null){

            gameTimer.cancel();
        }

        gameTimer = initTimer(animationOn);

        playMusic(gameData.getSecondRemain()>10? R.raw.smooth_count_down:R.raw.hurry_count_down);
    }

    private CountDownTimer initTimer(final boolean animationOn ){
        return new CountDownTimer(gameData.getSecondRemain() * 1000, 1000) {
            @Override
            public void onFinish() {
                Log.i(TAG, "Game time run out: ");
                gameData.decreaseSecondRemain();
                timeRemainTextView.setText(gameData.getSecondRemain() + "");
                //playMusic(-1);
                stopMusic();
                calculateScore( animationOn);
            }

            @Override
            public void onTick(long millisUntilFinished) {

                if (GameActivity.this.isFinishing()) {
                    this.cancel();
                }

                gameData.decreaseSecondRemain();
                timeRemainTextView.setText("" + gameData.getSecondRemain());

                Log.i(TAG, "onTick: seconds remain is "+gameData.getSecondRemain());
                if (gameData.getSecondRemain() == 10) {
                    Log.i(TAG, "onTick: change background music as  hurry_count_down");
                    GameActivity.this.playMusic( R.raw.hurry_count_down);
                }

            }

        };

    }

    private void generateNextPipe() {

        int nextImage;
        if(gameData.getNextPipe()!=R.mipmap.blank){
            //load next pipe from save game data
            nextImage = gameData.getNextPipe();
            gameData.setNextPipe(R.mipmap.blank);
        }else{
            //Log.i(TAG, "generateNextPipe: " + pipeImages.length );

            Random random = new Random();
            nextImage = pipeImages[random.nextInt(pipeImages.length)];

            Log.i("GameActivity", "generateNextPipe : possible pipes ("+pipeImages.length+") : " + Utils.pipes2String(pipeImages)
                    + ", result = " + Utils.pipes2String(new Integer[]{nextImage}));
        }

        nextPipe.setImageResource(nextImage);
        nextPipe.setScaleType(ImageView.ScaleType.FIT_XY);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(pipeWidth *8/10, pipeWidth *8/10);
        nextPipe.setLayoutParams(layoutParams);
        nextPipe.setTag(nextImage);
    }

    public void refreshMissionCount(){

        int requiredCount = gameData.getMissionCount();

      //  Log.i(TAG, "refreshMissionCount: before head["+ gameData.getHeadPosition()+"]="+gameData.getHeadImage());


        List<int[]> taskList = gameData.getAnimationTaskList();

        if(taskList.isEmpty()){

            ScoreCalculator calculator = new ScoreCalculator(gameData);
            calculator.execute();
            taskList=calculator.getAnimationTaskList();

            //set the next possible pipes base on the calculation
            this.pipeImages = calculator.getPossiblePipes();
            Log.i("GameActivity", " refreshMissionCount : pipes ("+pipeImages.length+") : " + Utils.pipes2String(pipeImages));

            if(calculator.isNextPipeBlocked() && gameData.getWrenchCount()==0 && gameData.getSecondRemain() > 0){
                //TODO prompt message about game over and set seconds remain to zero immediately

                //start game calculation immediately
                final int seconds = gameData.getSecondRemain();

                CountDownTimer zeroTimer =  new CountDownTimer(seconds*50, 50) {
                    @Override
                    public void onTick(long l) {
                        Log.i(TAG, "onTick: remain: "+l);
                        timeRemainTextView.setText(gameData.getSecondRemain() + "");
                        gameData.decreaseSecondRemain();
                    }

                    @Override
                    public void onFinish() {
                        gameTimer.onFinish();
                    }
                };
                //gameData.dropSecondRemain();
                zeroTimer.start();
            }
        }

        //Toast.makeText(this, "next pipe is block = " + calculator.isNexbPipeBlocked(), Toast.LENGTH_SHORT).show();

        Log.i(TAG, "refreshMissionCount: requiredCount, = "+ requiredCount+", finishedCount = "+(taskList.size()-1));
        int remain = requiredCount - taskList.size() + 1;

        Log.i(TAG, "getAnimationTaskList size is : " + taskList.size());
        int progress = (taskList.size()-1) * 100 / gameData.getMissionCount() ;

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

        refreshMissionCount();

        if (currentImage == R.mipmap.blank) {

            //drop the pie to current grid.
            playSound(1);

            int nextImage = (int) nextPipe.getTag();
            imageView.setImageResource(nextImage);
            imageView.setTag(nextImage);
            generateNextPipe();

            gameData.setDataImage(view.getId(), nextImage);

        } else if (reduceWrenchCount() == true) {

            //pick up the current pipe
            nextPipe.setImageResource(currentImage);
            nextPipe.setTag(currentImage);
            imageView.setImageResource(R.mipmap.blank);
            imageView.setTag(R.mipmap.blank);
            gameData.setDataImage(view.getId(), R.mipmap.blank);

            playSound(2);

        }

        refreshMissionCount();

    }

    AnimationHandler animationHandler = new AnimationHandler();

    private boolean reduceWrenchCount() {

        if (gameData.getWrenchCount() == 0) {
            return true; //for test only
//            return false;
        }

        gameData.decreaseWrenchCount();
        wrenchTextView.setText(new String(gameData.getWrenchCount() + ""));
        return true;
    }

    public void calculateScore( final boolean animationOn) {

        if(gameData.getAnimationTaskList().isEmpty()){
            ScoreCalculator calculator = new ScoreCalculator(gameData);
            calculator.execute();
            gameData.setAnimationTaskList(calculator.getAnimationTaskList());
        }else{
            gameScoreTextView.setText(gameData.getTotal()+"");
        }

        final List<int[]> animationTaskList = gameData.getAnimationTaskList();

        Log.i(TAG, "calculateScore: animationTaskList size is : " + animationTaskList.size());

        gameData.setPassed(animationTaskList.size()-gameData.getMissionCount()>0);

        if(animationOn){
            for(int[] task : animationTaskList){
                gameData.addTotalScore(task[2]);
            }
        }

//        if(animationOn==true){
//            saveGame();
//        }

        final boolean globalAnimationOn  = isGameAnimationOn();

        animationHandler.resetTerminate();

        //play animation
        Runnable drawConnectedPipe = new Runnable() {

            int currentPipeIndex = gameData.getCurrentPipeIndex();
            int total = gameData.getTotal();

            public void run() {

                if(animationHandler.isTerminate()==true){
                    saveGame();
                    return;
                }

                if (currentPipeIndex > animationTaskList.size() - 1 ) {
                    saveGame();
                    boolean gamePassed = animationTaskList.size() -  gameData.getMissionCount() > 0;
                    String message = gamePassed ? getResources().getString(R.string.game_message_level_pass) : getResources().getString(R.string.game_message_level_fail);
                    message = MessageFormat.format(message,gameData.getLevel(),animationTaskList.size()-1, total);
                    String[] buttonTexts = {gamePassed?getResources().getString(R.string.game_text_dialog_button_next_level):
                            getResources().getString(R.string.game_text_dialog_button_play_again),
                            getResources().getString(R.string.game_text_dialog_button_back)};

                    Utils.showDialog(GameActivity.this,GameActivity.this,message, buttonTexts);
                    return;
                }

                int[] task = animationTaskList.get(currentPipeIndex);

                ImageView imgView = (ImageView) gridView.findViewById(task[0]);

                if(imgView!=null){
                    imgView.setImageResource(task[1]);
                }

               // if(animationOn == true && globalAnimationOn == true){
                if( globalAnimationOn == true){
                //if(isGameSoundOn()){
                    int soundId = task[2]==50?soundResources.get(4):soundResources.get(3);
                    soundPool.play(soundId, 0.7f, 0.7f, 0, 0, 1);
                }

                total += task[2];

                gameScoreTextView.setText(total+"");

                currentPipeIndex++;

                gameData.setDataImage(task[0], task[1]);
                gameData.setCurrentPipeIndex(currentPipeIndex);
                gameData.setTotal(total);

                animationHandler.postDelayed(this, globalAnimationOn ? 800 : 0);  //for interval...
               // animationHandler.postDelayed(this, animationOn && globalAnimationOn ? 800 : 0);  //for interval...
//                animationHandler.postDelayed(this, GameActivity.this.isGameSoundOn() ? 800 : 0);  //for interval...
            }
        };
        animationHandler.postDelayed(drawConnectedPipe, 0); //for initial delay..
    }

    @Override
    public void onBackPressed() {
        stopMusic();
        gameTimer.cancel();

        super.onBackPressed();

        //send message to stop the running handler
        animationHandler.sendEmptyMessage(0);

        saveGame();

    }

    private void saveGame(){

//        String path = Utils.getDefaultFilePath();
//        gameData.setNextPipe((int) this.nextPipe.getTag());
//        Utils.saveObject(this.gameData,   path + File.separator + gameData.getName());

        dbHelper.saveGame(gameData);

    }

    @Override
    public void buttonClicked(String buttonText) {

        this.onClose();

        Log.i(TAG, "buttonClicked: "+buttonText);

        if(buttonText.equals(getResources().getString(R.string.game_text_dialog_button_back))){
            this.finish();
            return;
        }else if(buttonText.equals(getResources().getString(R.string.game_text_dialog_button_play_again))){

//            String path = Utils.getDefaultFilePath();
//            File file = new File(path + File.separator + gameData.getName());
//            if(file.exists()){
//                file.delete();
//            }

            //Start a new game
            gameData = gameData.cloneLevel();
                    //new GameData(1, gameData.getNumOfRows(), gameData.getNumOfColumns());
            initGame(true,true);
        }else if(buttonText.equals(getResources().getString(R.string.game_text_dialog_button_next_level))){
           // initGame(this.gameData.isOver()!=false);
            gameData = gameData.nextLevel();
            initGame(true,true);

        }else if(buttonText.equals(getResources().getString(R.string.game_text_dialog_button_continue))){
            //Game pauses, only start up music service is enough
            playMusic(gameData.getSecondRemain()>10? R.raw.smooth_count_down:R.raw.hurry_count_down);
        }

        if(gameTimer!=null){
            gameTimer.cancel();
        }

        gameTimer = initTimer(true);
        gameTimer.start();
        GAME_PAUSED=false;
    }

    boolean GAME_PAUSED = false;

    @Override
    protected void onPause() {
        super.onPause();

        GAME_PAUSED = true;
        Log.i(TAG, "onPause event fired, save game automatically." );

        stopMusic();
        gameTimer.cancel();
//        timer = initTimer(true);
//        saveGame();
        animationHandler.sendEmptyMessage(0);

    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i(TAG, "onResume called.");

        if(GAME_PAUSED==true){
            //wait for the user to click the continue button.
            Utils.showDialog(GameActivity.this, GameActivity.this,
                    getResources().getString(R.string.game_message_level_pause),
                    getResources().getString(R.string.game_text_dialog_button_continue),
                    getResources().getString(R.string.game_text_dialog_button_back));
            return;
        }

        playMusic(gameData.getSecondRemain()>10? R.raw.smooth_count_down:R.raw.hurry_count_down);

//        timer.cancel();
//        timer = initTimer(true);
        gameTimer.start();
    }

    private void playSound(final int soundKey){

        if(isGameSoundOn()==false){
            return;
        }

        new Handler().postDelayed(new Runnable() {
            public void run() {
                soundPool.play(soundResources.get(soundKey), 0.7f, 0.7f, 0, 0, 1);
            }
        }, 0);
    }

}