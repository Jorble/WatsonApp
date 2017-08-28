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

public class ConversationDao {

    private static final String QUERY_ALL = "SELECT * FROM conversation LIMIT 0,20";

    private static final String QUERY_BY_RESTID = "SELECT * FROM conversation WHERE id = ";

    private static final String FIELD_ID = "id";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_CONTENT = "content";
    private static final String FIELD_DETAIL = "detail";
    private static final String FIELD_IMG = "img";
    private static final String FIELD_URL = "url";

    /**
     * 获取所有数据
     *
     * @return
     */
    public static void queryAll(DbCallBack cdf) {
        setCallBackFunction(cdf);

        List<Conversation> mDatas = new ArrayList<>();

        if (cbFunction != null) {
            Thread th = new Thread(() -> {
                try {
                    List list = JdbcHelper.query(QUERY_ALL);
                    if (list == null) return;
                    for (Object o : list) {
                        //map转对象
                        HashMap hashMap = (HashMap) o;

                        Conversation bean = new Conversation();
                        bean.setId(hashMap.get(FIELD_ID).toString());
                        bean.setName(hashMap.get(FIELD_NAME).toString());
                        bean.setUrl(hashMap.get(FIELD_URL).toString());
                        bean.setContent(hashMap.get(FIELD_CONTENT).toString());
                        bean.setImg(hashMap.get(FIELD_IMG).toString());
                        bean.setDetail(hashMap.get(FIELD_DETAIL).toString());
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

    /**
     * 获取某餐厅的美食
     *
     * @return
     */
    public static void queryById(String fieldId, DbCallBack cdf) {
        setCallBackFunction(cdf);

        List<Conversation> mDatas = new ArrayList<>();

        if (cbFunction != null) {
            Thread th = new Thread(() -> {
                try {
                    List list = JdbcHelper.query(QUERY_BY_RESTID +"'"+fieldId+"'");
                    if (list == null) return;
                    for (Object o : list) {
                        //map转对象
                        HashMap hashMap = (HashMap) o;
                        Conversation bean = new Conversation();
                        bean.setId(hashMap.get(FIELD_ID).toString());
                        bean.setName(hashMap.get(FIELD_NAME).toString());
                        bean.setDetail(hashMap.get(FIELD_DETAIL).toString());
                        bean.setContent(hashMap.get(FIELD_CONTENT).toString());
                        bean.setUrl(hashMap.get(FIELD_URL).toString());
                        bean.setImg(hashMap.get(FIELD_IMG).toString());
                        mDatas.add(bean);
                    }
                    // 异步进入主线程,无需等待
                    Run.onUiAsync(() -> {
                        cbFunction.onSuccess(mDatas);
                    });
                } catch (SQLException e) {
                    e.printStackTrace();
                    L.i("连接不上数据库");
                    // 异步进入主线程,无需等待
                    Run.onUiAsync(() -> {
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
        void onSuccess(List<Conversation> datas);

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
