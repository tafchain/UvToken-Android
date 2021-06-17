package com.yongqi.wallet.walletconnect.controller;

import android.content.res.Resources;

import com.yongqi.wallet.walletconnect.ScanQRContract;


/**
 * Created by zero on 2018/12/7.
 */

public abstract class ScanQRController {

    protected ScanQRContract.IMvpScanQRView mvpView;
    protected ScanQRContract.IMvpScanQRPresenter presenter;
    protected Resources resources;

    public ScanQRController(ScanQRContract.IMvpScanQRView mvpView, ScanQRContract.IMvpScanQRPresenter presenter) {
        this.mvpView = mvpView;
        this.presenter = presenter;
        this.resources = mvpView.getActivity().getResources();
    }

    public abstract void handleAnalyzeOnSuccess(String result);

    public abstract boolean needSelectAlbum();

}
