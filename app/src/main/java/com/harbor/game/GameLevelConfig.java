package com.harbor.game;

/**
 * Created by harbor on 11/25/2016.
 */
public class GameLevelConfig {

    private int level;

    private boolean locked;

    private int highestScore;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public int getHighestScore() {
        return highestScore;
    }

    public void setHighestScore(int highestScore) {
        this.highestScore = highestScore;
    }

    public GameLevelConfig(int level, boolean locked, int highestScore) {
        this.level = level;
        this.locked = locked;
        this.highestScore = highestScore;
    }
}
