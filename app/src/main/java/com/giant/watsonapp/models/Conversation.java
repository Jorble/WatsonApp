package com.giant.watsonapp.models;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Jorble on 2017/7/25.
 */

@Entity
public class Conversation {
    @Unique
    private String id;
    private String content;
    private String detail;
    private String img;
    private String url;
    @Generated(hash = 376114013)
    public Conversation(String id, String content, String detail, String img,
            String url) {
        this.id = id;
        this.content = content;
        this.detail = detail;
        this.img = img;
        this.url = url;
    }
    @Generated(hash = 1893991898)
    public Conversation() {
    }
    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getDetail() {
        return this.detail;
    }
    public void setDetail(String detail) {
        this.detail = detail;
    }
    public String getImg() {
        return this.img;
    }
    public void setImg(String img) {
        this.img = img;
    }
    public String getUrl() {
        return this.url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
}
