package com.yongqi.wallet.ui.manageFinances.bean;

public class DAppDataBean {

    /**
     * img : http://192.168.120.110:3000/imgs/icon_wscp.jpg
     * name : 无损彩票
     * desc : 不会损失本金的彩票
     * url : https://app.pooltogether.com/?hbg%3D290c59%26hfg%3Dffffff%26lbg%3D290c59%26lfg%3Dd9e3f0%26bbg%3D290c59%26sfg%3D1
     */

    private String img;
    private String name;
    private String name_en;
    private String desc;
    private String url;
    private String language;

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    public String getName_en() {
        return name_en;
    }
}
