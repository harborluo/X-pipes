package com.harbor.game.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.harbor.game.GameData;
import com.harbor.game.R;
import com.harbor.game.ScoreCalculator;
import com.harbor.game.handler.AnimationHandler;
import com.harbor.game.service.MusicService;
import com.harbor.game.util.Utils;
import com.harbor.game.widget.ImageAdapter;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GameActivity extends Activity implements View.OnClickListener {

    private static String TAG = "GameActivity";

//    private Handler mainHandler= new Handler();

    public Integer[] pipeImages = {
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

        soundPool = new SoundPool(5, AudioManager.STREAM_SYSTEM, 5);
        soundResources.put(1, soundPool.load(this, R.raw.pipe_dropped, 1));
        soundResources.put(2, soundPool.load(this, R.raw.pipe_pick_up, 1));
        soundResources.put(3, soundPool.load(this, R.raw.single_pipe_passed, 1));
        soundResources.put(4, soundPool.load(this, R.raw.double_pipe_passed, 1));

        String fileName = getIntent().getStringExtra("fileName");

        DisplayMetrics displayMetrics = this.getApplicationContext().getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        if(fileName==null||fileName.length()==0){
            int numOfRows = 3, numOfColumns = 3;
            pipeWidth = screenWidth / numOfColumns;

            while (pipeWidth > 90) {
                numOfColumns++;
                pipeWidth = screenWidth / numOfColumns;
            }

            numOfRows = screenHeight/pipeWidth -1;

//            while (screenHeight - numOfRows * pipeWidth > pipeWidth * 1.5) {
//                numOfRows++;
//            }
            gameData = new GameData(1, numOfRows, numOfColumns);
        }else{
            gameData = (GameData)  Utils.readObject(Utils.getDefaultFilePath() + File.separator + fileName);

            if(gameData.getSecondRemain()==0){

                if(gameData.isPassed()==false){

                    //TODO show game over dialog here
                    Toast.makeText(this, "GAME OVER", Toast.LENGTH_LONG).show();
                 //  return ;
                }else{
                    //TODO popup dialog to next level
                    gameData = gameData.nextLevel();
                   // gameData.setName(fileName);
                }

            }

            pipeWidth = screenWidth / gameData.getNumOfColumns();

        }

        initGame(!(gameData.getSecondRemain()==0 && gameData.isPassed()==false));

    }

    private void initGame(boolean startTimer){

        DisplayMetrics displayMetrics = this.getApplicationContext().getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        gridView.setNumColumns(gameData.getNumOfColumns());
        gridView.setAdapter(new ImageAdapter(this, this, pipeWidth, gameData.getData()));

        game_required_pipe_count.setText(gameData.getMissionCount()+"");

        generateNextPipe();

        LinearLayout panelButtonBar = (LinearLayout) findViewById(R.id.buttonBar);
        panelButtonBar.setLayoutParams(new LinearLayout.LayoutParams(screenWidth, screenHeight - pipeWidth * gameData.getNumOfRows()));

        ImageView wrenchImageView = (ImageView) findViewById(R.id.wrench_image);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(pipeWidth * 8 / 10, pipeWidth * 8 / 10);
        wrenchImageView.setLayoutParams(layoutParams);

        timeRemainTextView.setText(gameData.getSecondRemain()+"");
        wrenchTextView.setText(new String(gameData.getWrenchCount()+ ""));

        if(startTimer==false){
            return;
        }

        timer = new CountDownTimer(gameData.getSecondRemain() * 1000, 1000) {
            @Override
            public void onFinish() {
                gameData.decreaseSecondRemain();
                timeRemainTextView.setText(gameData.getSecondRemain() + "");
                Intent intent = new Intent(GameActivity.this, MusicService.class);
                stopService(intent);
                calculateScore();
            }

            @Override
            public void onTick(long millisUntilFinished) {
                gameData.decreaseSecondRemain();
                timeRemainTextView.setText("" + gameData.getSecondRemain());
                if (GameActivity.this.isFinishing()) {
                    this.cancel();
                }

                if (gameData.getSecondRemain() == 10) {
                    Intent intent = new Intent(GameActivity.this, MusicService.class);
                    intent.putExtra("music", R.raw.hurry_count_down);
                    startService(intent);
                }

            }

        };

        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("music", R.raw.smooth_count_down);
        startService(intent);

        timer.start();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void generateNextPipe() {
        Random random = new Random();
        int nextImage = pipeImages[random.nextInt(pipeImages.length)];
        nextPipe.setImageResource(nextImage);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(pipeWidth *7/10, pipeWidth *7/10);
        nextPipe.setLayoutParams(layoutParams);
        nextPipe.setTag(nextImage);
    }

    @Override
    public void onClick(View view) {

        if (gameData.getSecondRemain() == 0) {
            return;
        }

        ImageView imageView = (ImageView) view;

        Integer currentImage = (Integer) imageView.getTag();

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

    public void calculateScore() {

        ScoreCalculator calculator = new ScoreCalculator(gameData);

        calculator.execute();
        final List<Integer[]> animationTaskList = calculator.getAnimationTaskList();

        gameData.setPassed(animationTaskList.size()-gameData.getMissionCount()>0);

        //play animation
        Runnable runnable = new Runnable() {
            int i = 0;
            int total = 0;

            public void run() {

                if(animationHandler.isTerminate()==true){
                    return;
                }

                Integer[] task = animationTaskList.get(i);

                ImageView imgView = (ImageView) gridView.findViewById(task[0]);

                imgView.setImageResource(task[1]);
                int soundId = task[2]==50?soundResources.get(4):soundResources.get(3);
                soundPool.play(soundId, 0.7f, 0.7f, 0, 0, 1);

                total += task[2];

                if (task[2] > 0 ) {
                    int req = Integer.parseInt(game_required_pipe_count.getText().toString());

                    if (req > 0) {
                        req--;
                        game_required_pipe_count.setText(req + "");
                    }
                }

                gameScoreTextView.setText(total+"");

                i++;
                if (i > animationTaskList.size() - 1 ) {
                   // Toast.makeText(GameActivity.this, "Your score is: " + total, Toast.LENGTH_LONG).show();

                    if(animationTaskList.size() -  gameData.getMissionCount() > 0){

                        gameData = gameData.nextLevel();
                        Toast.makeText(GameActivity.this, "Get ready for next level "+gameData.getLevel(), Toast.LENGTH_LONG).show();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                initGame(true);
                            }
                        },3000);


                    }else{
                        Toast.makeText(GameActivity.this, "GAME OVER", Toast.LENGTH_LONG).show();
                    }

                    return;
                }
                animationHandler.postDelayed(this, 800);  //for interval...
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

        String path = Utils.getDefaultFilePath();

        Utils.saveObject(this.gameData,   path + File.separator + gameData.getName());

    }
}

