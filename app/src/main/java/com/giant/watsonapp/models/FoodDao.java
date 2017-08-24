package com.giant.watsonapp.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jorble on 2017/8/24.
 */

public class FoodDao {

    /**
     * 获取所有数据
     * @return
     */
    public static List<Food> queryAll(){
        List<Food> mDatas = new ArrayList<>();
        for(int i=0;i<5;i++){
            Food bean=new Food();
            bean.setId("00"+i);
            bean.setRestaurantId("00"+1);
            bean.setName("法式乳鸽");
            bean.setImg("https://dimg03.c-ctrip.com/images/10020f0000007girr3BFD_D_750_375_Q80.jpg");
            mDatas.add(bean);
        }
        return mDatas;
    }

    /**
     * 获取某餐厅的美食
     * @return
     */
    public static List<Food> queryByRestId(String restId){
        List<Food> mDatas = new ArrayList<>();
        for(Food item:queryAll()){
            if(item.getRestaurantId().equals(restId)) {
                mDatas.add(item);
            }
        }
        return mDatas;
    }
}
