package com.giant.watsonapp;

import android.app.Application;
import android.text.TextUtils;

import com.giant.watsonapp.models.Conversation;
import com.giant.watsonapp.models.GreenDaoManager;
import com.giant.watsonapp.utils.ACache;
import com.giant.watsonapp.utils.JdbcUtil;
import com.giant.watsonapp.utils.L;

import java.util.ArrayList;
import java.util.List;


public class App extends Application {

    private static ACache mAcache;

    @Override
    public void onCreate() {
        super.onCreate();

        //缓存
        mAcache=ACache.get(this);

        //Green Dao
        GreenDaoManager.setupDatabase(this,"watsonapp.db");

//        initDbData();

    }

    /**
     * 获取本地缓存文件
     * @return
     */
    public static ACache getAcache() {
        return mAcache;
    }

    /**
     * 获取缓存key-value，如果有值则返回，如果没有则返回默认值并保存
     * @param key
     * @param index 默认值
     * @return
     */
    public static String getCacheString(String key,String index){
        String value=mAcache.getAsString(key);
        if(TextUtils.isEmpty(value)){
            mAcache.put(key,index);
            return index;
        }
        return value;
    }

}
