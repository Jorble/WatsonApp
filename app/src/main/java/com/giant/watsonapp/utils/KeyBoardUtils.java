package com.giant.watsonapp.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Jorble on 2017/6/22.
 */

public class KeyBoardUtils {

    /**
     * 打开软键盘
     */
    public static void openKeyboard(final Activity context) {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 800);
    }

    /**
     * 打开软键盘(需要注意的是，在此之前必须让edittext获取焦点，不然也是无效的。)
     */
    public static void openKeyboard(final Activity context,final View edittext) {
        edittext.requestFocus(); //edittext是一个EditText控件
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edittext,InputMethodManager.SHOW_FORCED);
            }
        }, 800);
    }

    /**
     * 关闭软键盘
     * @param context
     */
    public static void closeKeyboard(Activity context) {
        View view = context.getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
