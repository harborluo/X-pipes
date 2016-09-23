package com.harbor.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by harbor on 8/28/2016.
 */
public class ScoreCalculator {

    int headerPosition;
    int headImageResource;
    Integer[] data;
    private int numOfRows=3, numOfColumns=3;

    private List<Integer[]> animationTaskList = new ArrayList<>();

    /**/
    public ScoreCalculator(GameData data){

        this.headerPosition = data.getHeadPosition();
        this.headImageResource = data.getHeadImage();
        this.data = data.getData();
        this.numOfRows = data.getNumOfRows();
        this.numOfColumns = data.getNumOfColumns();

    }

    public List<Integer[]> getAnimationTaskList() {
        return animationTaskList;
    }

    public void execute() {

        int head_on_image = -1;

        String direction = "";

        int score = 0;

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

        //TODO add animation task here
        animationTaskList.add(new Integer[]{headerPosition, head_on_image, 0 });

        int rowPos = headerPosition/numOfColumns;
        int colPos = headerPosition%numOfColumns;

        // int pos = rowPos * numOfColumns + colPos;

        //Toast.makeText(this, "rowPos = "+rowPos+", colPos = "+colPos+", pos = "+pos , Toast.LENGTH_LONG).show();

        boolean nextPipePassed=true;

        while(nextPipePassed==true){

            switch (direction){
                case "d" :{
                    rowPos++;
                    if(rowPos==numOfRows){
                        nextPipePassed = false;
                    }
                }break;
                case "u" : {
                    rowPos--;
                    if(rowPos<0){
                        nextPipePassed = false;
                    }

                }break;
                case "l":{
                    colPos--;
                    if(colPos<0){
                        nextPipePassed = false;
                    }
                }break;
                case "r":{
                    colPos++;
                    if(colPos==numOfColumns){
                        nextPipePassed = false;
                    }
                }break;

            }

            if(nextPipePassed==false){
                break;
            }

            VerifyResult result = verifyNextPipe(direction, rowPos * numOfColumns + colPos);

            direction = result.getDirection();

            nextPipePassed = result.getScore()>0;

        }

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
                }
                else if(currentImage==R.mipmap.cross_horizontal_on){
                    targetImage = R.mipmap.cross_full_on;
                }
                else if(currentImage==R.mipmap.left_down){
                    targetImage = R.mipmap.left_down_on;
                    newDirection="l";
                }
                else if(currentImage==R.mipmap.right_down){
                    targetImage = R.mipmap.right_down_on;
                    newDirection="r";
                }
            }
            break;
            case "d":{
                if(currentImage==R.mipmap.vertical){
                    targetImage = R.mipmap.vertical_on;
                }
                else if(currentImage==R.mipmap.cross){
                    targetImage = R.mipmap.cross_vertical_on;
                }
                else if(currentImage==R.mipmap.cross_horizontal_on){
                    targetImage = R.mipmap.cross_full_on;
                }
                else if(currentImage==R.mipmap.left_up){
                    targetImage = R.mipmap.left_up_on;
                    newDirection="l";
                }
                else if(currentImage==R.mipmap.right_up){
                    targetImage = R.mipmap.right_up_on;
                    newDirection="r";
                }
            }
            break;
            case "l":{
                if(currentImage==R.mipmap.horizontal){
                    targetImage = R.mipmap.horizontal_on;
                }
                else if(currentImage==R.mipmap.cross){
                    targetImage = R.mipmap.cross_horizontal_on;
                }
                else if(currentImage==R.mipmap.cross_vertical_on){
                    targetImage = R.mipmap.cross_full_on;
                }
                else if(currentImage==R.mipmap.right_up){
                    targetImage = R.mipmap.right_up_on;
                    newDirection="u";
                }
                else if(currentImage==R.mipmap.right_down){
                    targetImage = R.mipmap.right_down_on;
                    newDirection="d";
                }
            }
            break;
            case "r":{
                if(currentImage==R.mipmap.horizontal){
                    targetImage = R.mipmap.horizontal_on;
                }
                else if(currentImage==R.mipmap.cross){
                    targetImage = R.mipmap.cross_horizontal_on;
                }
                else if(currentImage==R.mipmap.cross_vertical_on){
                    targetImage = R.mipmap.cross_full_on;
                }
                else if(currentImage==R.mipmap.left_up){
                    targetImage = R.mipmap.left_up_on;
                    newDirection="u";
                }
                else if(currentImage==R.mipmap.left_down){
                    targetImage = R.mipmap.left_down_on;
                    newDirection="d";
                }
            }
            break;

        }

        if(targetImage>-1){
            //TODO add animation task here
            score = targetImage==R.mipmap.cross_full_on?50:10;

            animationTaskList.add(new Integer[]{pos, targetImage, score});

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

