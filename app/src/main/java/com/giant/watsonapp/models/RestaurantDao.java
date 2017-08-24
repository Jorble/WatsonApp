package com.giant.watsonapp.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jorble on 2017/8/24.
 */

public class RestaurantDao {

    /**
     * 获取所有数据
     * @return
     */
    public static List<Restaurant> queryAll(){
        List<Restaurant> mDatas = new ArrayList<>();
        for(int i=0;i<10;i++){
            Restaurant bean=new Restaurant();
            bean.setId("00"+i);
            bean.setName("海角轩");
            bean.setPrice("¥715");
            bean.setStar("4.4");
            bean.setLocation("三亚市大东海旅游区榆海路12号（近大东海）");
            bean.setImgs("https://dimg02.c-ctrip.com/images/100q0f0000007ghq83E85_D_750_500_Q80.jpg"+";"
                    +"https://dimg05.c-ctrip.com/images/100q0f0000007ghq92E66_D_750_500_Q80.jpg"+";"
                    +"https://dimg09.c-ctrip.com/images/100u0f0000007gim1E5D7_D_750_500_Q80.jpg");
            mDatas.add(bean);
        }
        return mDatas;
    }

}
