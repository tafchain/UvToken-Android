package com.yongqi.wallet.walletconnect;


import androidx.fragment.app.FragmentActivity;

/**
 * Created by zero on 2018/10/22.
 */

public interface ScanQRContract {
    interface IMvpScanQRView {
        FragmentActivity getActivity();
        void doClickBackAction();
    }

    interface IMvpScanQRPresenter {
    }
}
