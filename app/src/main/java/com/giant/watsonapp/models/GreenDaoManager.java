package com.giant.watsonapp.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.giant.watsonapp.models.db.gen.DaoMaster;
import com.giant.watsonapp.models.db.gen.DaoSession;

/**
 * Created by Jorble on 2017/7/25.
 */

public class GreenDaoManager {

    private static DaoSession daoSession;

    /**
     * 配置数据库
     */
    public static void setupDatabase(Context context, String dbName) {
        //创建数据库shop.db"
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, dbName, null);
        //获取可写数据库
        SQLiteDatabase db = helper.getWritableDatabase();
        //获取数据库对象
        DaoMaster daoMaster = new DaoMaster(db);
        //获取Dao对象管理者
        daoSession = daoMaster.newSession();
    }

    public static DaoSession getDaoInstant() {
        return daoSession;
    }
}
