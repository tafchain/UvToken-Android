package com.yongqi.wallet.utils;

import com.yongqi.wallet.bean.Wallet;
import com.yongqi.wallet.config.WalletConst;
import com.yongqi.wallet.db.wallet.WalletRepository;

public class WalletUtils {
    /**
     * 获取钱包钱包类型 1:多链 2:单链
     */
    public static int getWalletType(WalletRepository walletRepository, String walletId) {
        Wallet wallet = walletRepository.getWalletById(walletId);
        if (wallet.getType().equals("Multi")) {
            return WalletConst.WALLET_TYPE_MULTI;
        }else {
            return WalletConst.WALLET_TYPE_SINGLE;
        }
    }
}
