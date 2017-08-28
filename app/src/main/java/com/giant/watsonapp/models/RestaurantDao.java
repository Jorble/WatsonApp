package com.giant.watsonapp.models;

import com.giant.watsonapp.utils.JdbcHelper;
import com.giant.watsonapp.utils.L;

import net.qiujuer.genius.kit.handler.Run;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jorble on 2017/8/24.
 */

public class RestaurantDao {

    private static final String QUERY_ALL = "SELECT * FROM restaurant LIMIT 0,10";

    private static final String FIELD_ID = "id";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_PRICE = "price";
    private static final String FIELD_STAR = "star";
    private static final String FIELD_LOCATION = "location";
    private static final String FIELD_IMGS = "imgs";
    private static final String FIELD_LAT = "lat";
    private static final String FIELD_LON = "lon";

    /**
     * 获取所有数据
     *
     * @return
     */
    public static void queryAll(DbCallBack cdf) {
        setCallBackFunction(cdf);

        List<Restaurant> mDatas = new ArrayList<>();

        if (cbFunction != null) {
            Thread th = new Thread(() -> {
                try {
                    List list = JdbcHelper.query(QUERY_ALL);
                    if (list == null) return;
                    for (Object o : list) {
                        //map转对象
                        HashMap hashMap = (HashMap) o;

                        Restaurant bean = new Restaurant();
                        bean.setId(hashMap.get(FIELD_ID).toString());
                        bean.setName(hashMap.get(FIELD_NAME).toString());
                        bean.setPrice(hashMap.get(FIELD_PRICE).toString());
                        bean.setStar(hashMap.get(FIELD_STAR).toString());
                        bean.setLocation(hashMap.get(FIELD_LOCATION).toString());
                        bean.setImgs(hashMap.get(FIELD_IMGS).toString());
                        bean.setLat(hashMap.get(FIELD_LAT).toString());
                        bean.setLon(hashMap.get(FIELD_LON).toString());
                        mDatas.add(bean);
                    }
                    // 异步进入主线程,无需等待
                    Run.onUiAsync(()->{
                        cbFunction.onSuccess(mDatas);
                    });
                } catch (SQLException e) {
                    e.printStackTrace();
                    L.i("连接不上数据库");
                    // 异步进入主线程,无需等待
                    Run.onUiAsync(()-> {
                        cbFunction.onFailed(e);
                    });
                }
            });
            th.start();
        }
    }

    private static DbCallBack cbFunction;

    /**
     * 回调函数接口
     *
     * @author Administrator
     */
    public static interface DbCallBack {
        void onSuccess(List<Restaurant> datas);

        void onFailed(Exception e);
    }

    /**
     * 设置回调函数
     *
     * @param cbFun
     */
    public static void setCallBackFunction(DbCallBack cbFun) {
        cbFunction = cbFun;
    }

}
