package com.harbor.game;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by harbor on 8/30/2016.
 */
public class GameData implements Serializable {

    private int id = -1;

    private String name = null;

    private int totalScore = 0;

    private Date dateCreated = new Date();

    private int nextPipe = R.mipmap.blank;

    private int currentPipeIndex = 0;

    private int total = 0;

    private List<int[]> animationTaskList = new ArrayList<>();

    private int secondRemain = 0;

    private int progress=0;

    private int numOfRows = 3, numOfColumns = 3;

    private int[] data;

    private int level;

    private int headPosition=0, headImage=0;

    private int missionCount = 15;

    private boolean passed = false;

    public int getCurrentPipeIndex() {
        return currentPipeIndex;
    }

    public void setCurrentPipeIndex(int currentPipeIndex) {
        this.currentPipeIndex = currentPipeIndex;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public List<int[]> getAnimationTaskList() {
        return animationTaskList;
    }

    public void setAnimationTaskList(List<int[]> animationTaskList) {
        this.animationTaskList = animationTaskList;
    }

    public int getNextPipe() {
        return nextPipe;
    }

    public void setNextPipe(int nextPipe) {
        this.nextPipe = nextPipe;
    }

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

    public int getMissionCount() {
        return missionCount;
    }

    public int getSecondRemain() {
        return secondRemain ;
    }

    public void decreaseSecondRemain() {
        if(this.secondRemain<1){
            return;
        }
        this.secondRemain --;
    }

    /**
     * for test only
     * @param seconds
     */
    public void resetSecondRemain(int seconds) {
        this.secondRemain =seconds;
    }

    public void dropSecondRemain() {
        this.secondRemain=0;
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
        if(this.wrenchCount<1){
            return;
        }
        this.wrenchCount--;
    }

    public GameData(int level, int numOfRows, int numOfColumns) {
        this.numOfRows = numOfRows;
        this.numOfColumns = numOfColumns;
        this.level = level;

        this.secondRemain = 60 + (level - 1) * 15;
        this.wrenchCount = 3 + level - 1;
        this.missionCount = 15 + (level -1 ) * 5;

        data = new int[numOfRows * numOfColumns];

        for (int i = 0; i < data.length; i++) {
            data[i] = R.mipmap.blank;
        }

        generateHeadPipe();

    }

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
        next.currentPipeIndex = 0;
        next.total = 0;
        next.animationTaskList = new ArrayList<>();
        return next;
    }

    /**
     * try the current level again
     * @return
     */
    public GameData cloneLevel() {
        GameData next = new GameData(this.level,this.numOfRows,this.getNumOfColumns());
        //next.addTotalScore(this.totalScore);
        //next.totalScore -= next.total;
        next.totalScore = this.totalScore - this.total;
        next.setName(this.getName());
        next.currentPipeIndex = 0;
        next.total = 0;
        next.animationTaskList = new ArrayList<>();
        return next;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress>100?100:progress;
    }

    public String toJson(){

        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("id", id);
            jsonObj.put("name", name);
            jsonObj.put("totalScore", totalScore);
            jsonObj.put("dateCreated", getDateCreated());
            jsonObj.put("nextPipe", nextPipe);
            jsonObj.put("currentPipeIndex", currentPipeIndex);
            jsonObj.put("total", total);
            jsonObj.put("secondRemain",secondRemain);
            jsonObj.put("progress",progress);
            jsonObj.put("numOfRows",numOfRows);
            jsonObj.put("numOfColumns",numOfColumns);
            jsonObj.put("level",level);
            jsonObj.put("headPosition",headPosition);
            jsonObj.put("headImage",headImage);
            jsonObj.put("missionCount",missionCount);
            jsonObj.put("passed",passed);

            JSONArray dataArray = new JSONArray();
            for (int pipe : data ) {
                dataArray.put(pipe);
            }
            jsonObj.put("data", dataArray);

            JSONArray animationTaskListArray = new JSONArray();
            for(int[] task : animationTaskList){
                JSONArray rowArray = new JSONArray();
                for(int i : task){
                    rowArray.put(i);
                }
                animationTaskListArray.put(rowArray);
            }
            jsonObj.put("animationTaskList",animationTaskListArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObj.toString();
    }

    /**
     *
     * @param jsonString
     */
    public GameData(String jsonString){

        try{
            JSONObject jsonObject = new JSONObject(jsonString);
            id = jsonObject.getInt("id");
            name = jsonObject.getString("name");
            totalScore  = jsonObject.getInt("totalScore");
            DateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
            dateCreated = format.parse(jsonObject.getString("dateCreated"));
            nextPipe = jsonObject.getInt("nextPipe");
            currentPipeIndex = jsonObject.getInt("currentPipeIndex");
            total = jsonObject.getInt("total");
            secondRemain = jsonObject.getInt("secondRemain");
            progress = jsonObject.getInt("progress");
            numOfRows = jsonObject.getInt("numOfRows");
            numOfColumns = jsonObject.getInt("numOfColumns");
            level = jsonObject.getInt("level");
            headPosition = jsonObject.getInt("headPosition");
            headImage = jsonObject.getInt("headImage");
            missionCount = jsonObject.getInt("missionCount");
            passed = jsonObject.getBoolean("passed");

            JSONArray dataArray = jsonObject.getJSONArray("data");
            int size = dataArray.length();
            data = new int[size];
            for(int i=0;i<size;i++){
                data[i] = dataArray.getInt(i);
            }

            JSONArray animationTaskListArray = jsonObject.getJSONArray("animationTaskList");
            size = animationTaskListArray.length();
            animationTaskList = new ArrayList<>(size);
            for(int i=0;i<size;i++){
                JSONArray rowArray = animationTaskListArray.getJSONArray(i);
                int[] row = new int[rowArray.length()];
                for(int j=0;j<row.length;j++){
                    row[j] = rowArray.getInt(j);
                }
                animationTaskList.add(row);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
