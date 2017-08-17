package com.giant.watsonapp.utils;

import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by Jorble on 2017/7/13.
 */

public class MediaPlayerUtils {

    private MediaPlayer mMediaPlayer;

    public MediaPlayerUtils(){
        initMediaplayer();
    }

    public MediaPlayer getmMediaPlayer() {
        return mMediaPlayer;
    }

    public void setmMediaPlayer(MediaPlayer mMediaPlayer) {
        this.mMediaPlayer = mMediaPlayer;
    }

    // 初始化播放器
    private void initMediaplayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mMediaPlayer = new MediaPlayer();
    }

    // 销毁音乐
    public void destoryMusic() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    // 暂停播放
    public void pauseMusic() {
        if (mMediaPlayer.isPlaying()) {// 正在播放
            mMediaPlayer.pause();// 暂停
        } else {// 没有播放
            mMediaPlayer.start();
        }
    }

    // 停止播放
    public void stopMusic() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
    }

    // 播放音乐
    public void playMusic(String mFileName) {
        try {
			/* 重置多媒体 */
            mMediaPlayer.reset();
			/* 读取mp3文件 */
            mMediaPlayer.setDataSource(mFileName);
			/* 准备播放 */
            mMediaPlayer.prepare();
			/* 开始播放 */
            mMediaPlayer.start();
			/* 是否单曲循环 */
            mMediaPlayer.setLooping(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
