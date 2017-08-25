package com.giant.watsonapp.models;

/**
 * Created by Jorble on 2017/8/25.
 */

public class Introduction {
    private String id;
    private String section;
    private String texts;
    private String imgs;
    private String imgNames;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getTexts() {
        return texts;
    }

    public void setTexts(String texts) {
        this.texts = texts;
    }

    public String getImgs() {
        return imgs;
    }

    public void setImgs(String imgs) {
        this.imgs = imgs;
    }

    public String getImgNames() {
        return imgNames;
    }

    public void setImgNames(String imgNames) {
        this.imgNames = imgNames;
    }
}
