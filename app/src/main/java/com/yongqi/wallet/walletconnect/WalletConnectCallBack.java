package com.yongqi.wallet.walletconnect;


public interface WalletConnectCallBack {
    void onSessionRequest(Session.MethodCall.SessionRequest sessionRequest);
    void onSessionUpdate(Session.MethodCall.SessionUpdate sessionUpdate);
    void onSendTransaction(Session.MethodCall.SendTransaction sendTransaction);
    void onSignMessage(Session.MethodCall.SignMessage signMessage);
    void onCustom(Session.MethodCall.Custom custom);
    void onResponse(Session.MethodCall.Response response);
}
