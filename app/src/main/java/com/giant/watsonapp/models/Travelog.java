package com.giant.watsonapp.models;

import java.io.Serializable;

/**
 * Created by Jorble on 2017/8/23.
 */

public class Travelog implements Serializable {

    private String id;
    private String name;
    private String html;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }
}
