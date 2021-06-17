package com.yongqi.wallet.config;

import com.blankj.utilcode.util.SPUtils;

public class WalletConfig {
    private volatile static WalletConfig instance;

    private WalletConfig() {

    }

    public static WalletConfig getInstance() {
        if (instance == null) {
            synchronized (WalletConfig.class) {
                if (instance == null) {
                    instance = new WalletConfig();
                }
            }
        }
        return instance;
    }

    public String getWalletId() {
        return SPUtils.getInstance().getString("walletId");
    }

    public void setWalletId(String walletId) {
        SPUtils.getInstance().put("walletId", walletId);
    }
}
