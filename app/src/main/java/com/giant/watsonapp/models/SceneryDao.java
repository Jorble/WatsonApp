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

public class SceneryDao {

    private static final String QUERY_ALL = "SELECT * FROM scenery LIMIT 0,10";

    private static final String FIELD_ID = "id";
    private static final String FIELD_TITLE = "title";
    private static final String FIELD_MSG = "message";
    private static final String FIELD_IMG = "img";
    private static final String FIELD_URL = "url";
    private static final String FIELD_LAT = "lat";
    private static final String FIELD_LON = "lon";

    /**
     * 获取所有数据
     *
     * @return
     */
    public static void queryAll(DbCallBack cdf) {
        setCallBackFunction(cdf);

        List<Scenery> mDatas = new ArrayList<>();

        if (cbFunction != null) {
            Thread th = new Thread(() -> {
                try {
                    List list = JdbcHelper.query(QUERY_ALL);
                    if (list == null) return;
                    for (Object o : list) {
                        //map转对象
                        HashMap hashMap = (HashMap) o;

                        Scenery bean = new Scenery();
                        bean.setId(hashMap.get(FIELD_ID).toString());
                        bean.setImg(hashMap.get(FIELD_IMG).toString());
                        bean.setLat(hashMap.get(FIELD_LAT).toString());
                        bean.setLon(hashMap.get(FIELD_LON).toString());
                        bean.setMessage(hashMap.get(FIELD_MSG).toString());
                        bean.setTitle(hashMap.get(FIELD_TITLE).toString());
                        bean.setUrl(hashMap.get(FIELD_URL).toString());
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
        void onSuccess(List<Scenery> datas);

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
