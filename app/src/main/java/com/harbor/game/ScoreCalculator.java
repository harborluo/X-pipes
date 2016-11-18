package com.harbor.game;

import android.util.Log;

import com.harbor.game.util.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by harbor on 8/28/2016.
 */
public class ScoreCalculator {

    int headerPosition;
    int headImageResource;
    int[] data;
    private int numOfRows=3, numOfColumns=3;

    Integer[] possiblePipes = {
            R.mipmap.right_down,
            R.mipmap.right_up,
            R.mipmap.left_down,
            R.mipmap.left_up,
            R.mipmap.vertical,
            R.mipmap.horizontal,
            R.mipmap.cross
    };

    public Integer[] getPossiblePipes() {
        return possiblePipes;
    }

    /**
     * Integer[]{pos, targetImage, score}
     */
    private List<int[]> animationTaskList = new ArrayList<>();

    /**/
    public ScoreCalculator(GameData game){

        this.headerPosition = game.getHeadPosition();
        this.headImageResource = game.getHeadImage();

        this.data = game.getData().clone();

        this.numOfRows = game.getNumOfRows();
        this.numOfColumns = game.getNumOfColumns();

    }

    public List<int[]> getAnimationTaskList() {
        return animationTaskList;
    }

    private boolean nextPipeBlocked = false;

    public boolean isNextPipeBlocked() {
        return nextPipeBlocked;
    }

    public void execute() {

        int head_on_image = -1;

        String direction = "";

//        int score = 0;

        switch (headImageResource){
            case R.mipmap.head_down :{
                head_on_image = R.mipmap.head_down_on;
                direction = "d";
            }
            break;
            case R.mipmap.head_up :{
                head_on_image = R.mipmap.head_up_on;
                direction = "u";
            }
            break;
            case R.mipmap.head_left :{
                head_on_image = R.mipmap.head_left_on;
                direction = "l";
            }
            break;
            case R.mipmap.head_right :{
                head_on_image = R.mipmap.head_right_on;
                direction = "r";
            }
            break;
        }

        //   ((ImageView) gridView.findViewById(headPosition[0])).setImageResource(head_on_image);
        // soundPool.play(soundResources.get(3),1, 1, 0, 0, 1);
        data[headerPosition] = head_on_image;

        //add animation task here
        animationTaskList.add(new int[]{headerPosition, head_on_image, 0 });

        int rowPos = headerPosition/numOfColumns;
        int colPos = headerPosition%numOfColumns;

        // int pos = rowPos * numOfColumns + colPos;

        //Toast.makeText(this, "rowPos = "+rowPos+", colPos = "+colPos+", pos = "+pos , Toast.LENGTH_LONG).show();

        boolean nextPipeFound=true;

        while(nextPipeFound==true){

            switch (direction){
                case "d" :{
                    rowPos++;
                    if(rowPos==numOfRows){
                        nextPipeFound = false;
                    }
                }break;
                case "u" : {
                    rowPos--;
                    if(rowPos<0){
                        nextPipeFound = false;
                    }

                }break;
                case "l":{
                    colPos--;
                    if(colPos<0){
                        nextPipeFound = false;
                    }
                }break;
                case "r":{
                    colPos++;
                    if(colPos==numOfColumns){
                        nextPipeFound = false;
                    }
                }break;

            }

            if(nextPipeFound==false){
                nextPipeBlocked = true;
                break;
            }

            VerifyResult result = verifyNextPipe(direction, rowPos * numOfColumns + colPos);
            direction = result.getDirection();
            nextPipeFound = result.getScore()>0;
        }

        if(nextPipeBlocked==true){
            return;
        }

        Log.i("ScoreCalculator", "execute: direction = "+ direction
                             + ", rowPos = " + rowPos
                             + ", colPos = " + colPos
                             + ", nextPipeBlocked = " + nextPipeBlocked);

        Set<Integer> pipes = new HashSet<>();

        for(int p : possiblePipes){
            pipes.add(p);
        }

        //Log.i("ScoreCalculator", "possiblePipes size before : " + pipes.size() );

        //TODO calculate possible pipe needed by player

        switch (direction){
            case "d" :{
                pipes.remove(R.mipmap.left_down);
                pipes.remove(R.mipmap.right_down);
                pipes.remove(R.mipmap.horizontal);
            }break;
            case "u" : {
                pipes.remove(R.mipmap.left_up);
                pipes.remove(R.mipmap.right_up);
                pipes.remove(R.mipmap.horizontal);

            }break;
            case "l":{
                pipes.remove(R.mipmap.left_down);
                pipes.remove(R.mipmap.left_up);
                pipes.remove(R.mipmap.vertical);
            }break;
            case "r":{
                pipes.remove(R.mipmap.right_up);
                pipes.remove(R.mipmap.right_down);
                pipes.remove(R.mipmap.vertical);
            }break;

        }

        //Log.i("ScoreCalculator", "possiblePipes size before edge : " + pipes.size() );

        //edge hits
        if(rowPos==numOfRows-1 && !direction.equals("u")){
            Log.i("ScoreCalculator", "down denied");
            pipes.remove(R.mipmap.vertical);
            pipes.remove(R.mipmap.left_down);
            pipes.remove(R.mipmap.right_down);
            pipes.remove(R.mipmap.cross);
        }

        if(rowPos==0&& !direction.equals("d")){
            Log.i("ScoreCalculator", "up denied");
            pipes.remove(R.mipmap.vertical);
            pipes.remove(R.mipmap.left_up);
            pipes.remove(R.mipmap.right_up);
            pipes.remove(R.mipmap.cross);
        }

        if(colPos==0&& !direction.equals("r")){
            Log.i("ScoreCalculator", "left denied");
            pipes.remove(R.mipmap.horizontal);
            pipes.remove(R.mipmap.left_up);
            pipes.remove(R.mipmap.left_down);
            pipes.remove(R.mipmap.cross);
        }

        if(colPos==numOfColumns-1&& !direction.equals("l")){
            Log.i("ScoreCalculator", "right denied");
            pipes.remove(R.mipmap.horizontal);
            pipes.remove(R.mipmap.right_up);
            pipes.remove(R.mipmap.right_down);
            pipes.remove(R.mipmap.cross);
        }

        possiblePipes = pipes.toArray(new Integer[pipes.size()]);

        Log.i("ScoreCalculator", " possible pipes ("+pipes.size()+") : " + Utils.pipes2String(possiblePipes));

    }

    private VerifyResult verifyNextPipe(String direction, int pos){

        int score = 0;
        int targetImage = -1;

        Integer currentImage = data[pos];

        String newDirection = direction;

        switch(direction){
            case "u":{
                if(currentImage==R.mipmap.vertical){
                    targetImage = R.mipmap.vertical_on;
                }else if(currentImage==R.mipmap.cross){
                    targetImage = R.mipmap.cross_vertical_on;
                }else if(currentImage==R.mipmap.cross_horizontal_on){
                    targetImage = R.mipmap.cross_full_on;
                }else if(currentImage==R.mipmap.left_down){
                    targetImage = R.mipmap.left_down_on;
                    newDirection="l";
                }else if(currentImage==R.mipmap.right_down){
                    targetImage = R.mipmap.right_down_on;
                    newDirection="r";
                }else if(currentImage!=R.mipmap.blank){
                    nextPipeBlocked = true;
                }
            }
            break;
            case "d":{
                if(currentImage==R.mipmap.vertical){
                    targetImage = R.mipmap.vertical_on;
                }else if(currentImage==R.mipmap.cross){
                    targetImage = R.mipmap.cross_vertical_on;
                }else if(currentImage==R.mipmap.cross_horizontal_on){
                    targetImage = R.mipmap.cross_full_on;
                }else if(currentImage==R.mipmap.left_up){
                    targetImage = R.mipmap.left_up_on;
                    newDirection="l";
                }else if(currentImage==R.mipmap.right_up){
                    targetImage = R.mipmap.right_up_on;
                    newDirection="r";
                }else if(currentImage!=R.mipmap.blank){
                    nextPipeBlocked = true;
                }
            }
            break;
            case "l":{
                if(currentImage==R.mipmap.horizontal){
                    targetImage = R.mipmap.horizontal_on;
                }else if(currentImage==R.mipmap.cross){
                    targetImage = R.mipmap.cross_horizontal_on;
                }else if(currentImage==R.mipmap.cross_vertical_on){
                    targetImage = R.mipmap.cross_full_on;
                }else if(currentImage==R.mipmap.right_up){
                    targetImage = R.mipmap.right_up_on;
                    newDirection="u";
                }else if(currentImage==R.mipmap.right_down){
                    targetImage = R.mipmap.right_down_on;
                    newDirection="d";
                }else if(currentImage!=R.mipmap.blank){
                    nextPipeBlocked = true;
                }
            }
            break;
            case "r":{
                if(currentImage==R.mipmap.horizontal){
                    targetImage = R.mipmap.horizontal_on;
                }else if(currentImage==R.mipmap.cross){
                    targetImage = R.mipmap.cross_horizontal_on;
                }else if(currentImage==R.mipmap.cross_vertical_on){
                    targetImage = R.mipmap.cross_full_on;
                }else if(currentImage==R.mipmap.left_up){
                    targetImage = R.mipmap.left_up_on;
                    newDirection="u";
                }else if(currentImage==R.mipmap.left_down){
                    targetImage = R.mipmap.left_down_on;
                    newDirection="d";
                }else if(currentImage!=R.mipmap.blank){
                    nextPipeBlocked = true;
                }
            }
            break;

        }

        if(targetImage>-1){
            //add animation task here
            score = targetImage==R.mipmap.cross_full_on?50:10;

            animationTaskList.add(new int[]{pos, targetImage, score});

            data[pos] = targetImage;

        }

        return new VerifyResult(score, newDirection);
    }

}

class VerifyResult{
    private int score;
    private String direction;

    public VerifyResult(int score, String direction){
        this.direction = direction;
        this.score=score;
    }

    public int getScore() {
        return score;
    }

    public String getDirection() {
        return direction;
    }
}

