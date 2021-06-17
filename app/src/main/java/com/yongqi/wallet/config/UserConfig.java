package com.yongqi.wallet.config;


import com.blankj.utilcode.util.SPUtils;
import com.yongqi.wallet.utils.UtilString;
import java.util.Arrays;
import java.util.List;

public class UserConfig {
    private volatile static UserConfig instance;

    private UserConfig() {

    }

    public static UserConfig getInstance() {
        if (instance == null) {
            synchronized (UserConfig.class) {
                if (instance == null) {
                    instance = new UserConfig();
                }
            }
        }
        return instance;
    }

    //用户是否登陆
    public boolean isLogin() {
        return UtilString.isNotBlank(SPUtils.getInstance().getString("token"));
    }

    public void exitLogin() {
        SPUtils.getInstance().remove("token");
        SPUtils.getInstance().remove("email");
        SPUtils.getInstance().remove("code");
    }


    public String getToken() {
        return SPUtils.getInstance().getString("token");
    }

    public void setToken(String token) {
        SPUtils.getInstance().put("token", token);
    }

    public List<String> getCollectCoin() {
        if (UtilString.isEmpty(SPUtils.getInstance().getString("collectCoin"))) {
            return null;
        }else {
            return Arrays.asList(SPUtils.getInstance().getString("collectCoin").split(","));
        }
    }

    public void addCollectCoin(String collectCoin) {
        if (UtilString.isEmpty(SPUtils.getInstance().getString("collectCoin"))) {
            SPUtils.getInstance().put("collectCoin", collectCoin.toUpperCase());
        }else {
            SPUtils.getInstance().put("collectCoin", SPUtils.getInstance().getString("collectCoin") + "," + collectCoin.toUpperCase());
        }
    }

    //移除收藏的币种
    public void removeCollectCoin(String collectCoin) {
        String collectionStr = SPUtils.getInstance().getString("collectCoin");
        if (UtilString.isEmpty(collectionStr)) {
            return;
        }
        if (collectionStr.contains(collectCoin)) {
            List<String> list =  Arrays.asList(collectionStr.split(","));
            //重新组装string
            String newCollectStr = "";
            for (int i = 0; i < list.size() ; i++) {
                if (!list.get(i).equals(collectCoin)) {
                    if (i == 0) {
                        newCollectStr = list.get(0);
                    }else {
                        newCollectStr = newCollectStr + "," + list.get(i);
                    }
                }
            }
            SPUtils.getInstance().put("collectCoin", newCollectStr);
        }
    }



}