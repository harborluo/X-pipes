package com.harbor.game.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import com.harbor.game.R;

/**
 * Created by harbor on 9/2/2016.
 */
public class SelectDialog extends AlertDialog {

    public SelectDialog(Context context, int theme) {
        super(context, theme);
    }

    public SelectDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_dialog);
    }
}
