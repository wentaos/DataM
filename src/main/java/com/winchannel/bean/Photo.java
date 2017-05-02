package com.winchannel.bean;

import java.io.Serializable;

/**
 * Created by qiaoboxiang on 2017/4/26.
 */
public class Photo implements Serializable{
    private static final long serialVersionUID = 3466625674488254661L;

    private Long id;
    private String imgId;
    private String imgUrl;
    private String imgAbsPath;
    private String funcCode;

    public Photo(){}

    public Photo(Long id, String imgId, String imgUrl, String imgAbsPath) {
        this.id = id;
        this.imgId = imgId;
        this.imgUrl = imgUrl;
        this.imgAbsPath = imgAbsPath;
    }

    public String getFuncCode() {
        return funcCode;
    }

    public void setFuncCode(String funcCode) {
        this.funcCode = funcCode;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getImgAbsPath() {
        return imgAbsPath;
    }

    public void setImgAbsPath(String imgAbsPath) {
        this.imgAbsPath = imgAbsPath;
    }
}
