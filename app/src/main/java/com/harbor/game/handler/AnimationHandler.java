package com.harbor.game.handler;

import android.os.Handler;
import android.os.Message;

/**
 * Created by harbor on 8/30/2016.
 */
public class AnimationHandler extends Handler {

    boolean terminate = false;

    public boolean isTerminate() {
        return terminate;
    }

    @Override

    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        terminate=true;
    }


}
