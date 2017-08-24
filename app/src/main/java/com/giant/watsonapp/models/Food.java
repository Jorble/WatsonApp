package com.giant.watsonapp.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Jorble on 2017/8/23.
 */

public class Food implements Serializable {

    private String id;
    private String restaurantId;
    private String name;
    private String img;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
