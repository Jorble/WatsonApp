package com.giant.watsonapp.models;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Jorble on 2017/7/28.
 */
@Entity
public class VrClassifier {
    @Id
    private Long id;
    @Unique
    private String classifierId;
    private String name;
    @Generated(hash = 1122624739)
    public VrClassifier(Long id, String classifierId, String name) {
        this.id = id;
        this.classifierId = classifierId;
        this.name = name;
    }
    @Generated(hash = 807716762)
    public VrClassifier() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getClassifierId() {
        return this.classifierId;
    }
    public void setClassifierId(String classifierId) {
        this.classifierId = classifierId;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
