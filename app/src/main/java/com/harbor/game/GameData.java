package com.harbor.game;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by harbor on 8/30/2016.
 */
public class GameData implements Serializable{

    private int nextPipe = R.mipmap.blank;

    public int getNextPipe() {
        return nextPipe;
    }

    public void setNextPipe(int nextPipe) {
        this.nextPipe = nextPipe;
    }

    private String name = null;

    private int totalScore = 0;

    private Date dateCreated = new Date();

    public String getDateCreated(){
        DateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
        return format.format(this.dateCreated);
    }

    public int getTotalScore(){
        return this.totalScore;
    }

    public void addTotalScore(int score){
        this.totalScore= this.totalScore + score;
    }

    public String getName() {

        if(this.name==null){
            DateFormat format = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss");
            name = format.format(new Date())+".pipe";
        }

        return name;
    }

    public boolean isOver(){
        if(getSecondRemain()>0) {
            return false;
        }
        return !isPassed();
    }

    public void setName(String name) {
        this.name = name;
    }

    private int[] headImages = {
            R.mipmap.head_left,
            R.mipmap.head_right,
            R.mipmap.head_up,
            R.mipmap.head_down
    };

    private int secondRemain = 0;

//    private int pipeWidth = 0;

    private int numOfRows = 3, numOfColumns = 3;

    private int[] data;

    private int level;

    private int headPosition=0, headImage=0;

    private int missionCount = 15;

    public int getMissionCount() {
        return missionCount;
    }

    public int getSecondRemain() {
        return secondRemain ;
    }

    public void decreaseSecondRemain() {
        if(this.secondRemain<0){
            return;
        }
        this.secondRemain --;
    }



    public int getNumOfRows() {
        return numOfRows;
    }

    public int getNumOfColumns() {
        return numOfColumns;
    }

    public int[] getData() {
        return data;
    }

    public int getLevel() {
        return level;
    }

    private void generateHeadPipe() {

        Random rand = new Random();
     //   int[] result = new int[2];

        int img = headImages[rand.nextInt(headImages.length)];

        int row = rand.nextInt(numOfRows);
        int col = rand.nextInt(numOfColumns);

        int pos = col + row * numOfColumns;

        switch (img) {
            case R.mipmap.head_up: {
                if (row == 0) {
                    //first row assigned
                    pos += numOfColumns;
                }
            }
            break;
            case R.mipmap.head_down: {
                if (row == numOfRows - 1) {
                    //last row assigned
                    pos -= numOfColumns;
                }
            }
            break;
            case R.mipmap.head_left: {
                if (col == 0) {
                    pos++;
                }
            }
            break;
            case R.mipmap.head_right: {
                if (col == numOfColumns - 1) {
                    pos--;
                }
            }
            break;

        }

      //  result[1] = headImageIndex;

        data[pos] = img;

        this.headImage = img;
        this.headPosition = pos;

       // return result;
    }

    public int getHeadPosition() {
        return headPosition;
    }

    public int getHeadImage() {
        return headImage;
    }

    public void setDataImage(int pos, int imageResourceId){
        this.data[pos] = imageResourceId;
    }

    private int wrenchCount = 3;

    public int getWrenchCount() {
        return wrenchCount;
    }

    public void decreaseWrenchCount(){
        this.wrenchCount--;
    }

    public GameData(int level, int numOfRows, int numOfColumns) {
        this.numOfRows = numOfRows;
        this.numOfColumns = numOfColumns;
        this.level = level;

        this.secondRemain = 45 + (level - 1) * 15;
        this.wrenchCount = 3 + level -1;
        this.missionCount = 15 + (level -1 ) * 5;

        data = new int[numOfRows * numOfColumns];

        for (int i = 0; i < data.length; i++) {
            data[i] = R.mipmap.blank;
        }

        generateHeadPipe();

    }

    private boolean passed = false;

    public void setPassed(boolean b) {
        passed=b;
    }

    public boolean isPassed() {
        return passed;
    }

    public GameData nextLevel() {
        GameData next = new GameData(this.level+1,this.numOfRows,this.getNumOfColumns());
        next.addTotalScore(this.totalScore);
        next.setName(this.getName());
        return next;
    }
}
