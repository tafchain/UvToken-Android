package com.yongqi.wallet.walletconnect;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.fragment.app.FragmentManager;

import com.blankj.utilcode.util.JsonUtils;
import com.google.gson.Gson;
import com.yongqi.wallet.ui.transaction.ui.ExchangeDetailActivity;
import com.yongqi.wallet.utils.DialogUtils;
import com.yongqi.wallet.view.LoadingDialog;
import com.yongqi.wallet.walletconnect.controller.MixProtocolController;
import com.yongqi.wallet.walletconnect.log.LogCallBack;
import com.yongqi.wallet.walletconnect.utils.JsonUtil;

public class WalletConnectHelperManager {
    private static final String TAG = "WalletConnectHelper";
    private Context mContext;
    private static final WalletConnectHelperManager walletConnectHelper = new WalletConnectHelperManager();

    private static final String CUSTOM_METHOD_PERSONAL_SIGN = "personal_sign";

    private WalletConnectHelperManager() {
    }

    public static WalletConnectHelperManager getInstance() {
        return walletConnectHelper;
    }

    public void initWalletConnect(Context context) {
        mContext = context;
        WalletConnectManager.getInstance().init(context, new WallConnectInfoConfig()
                .setClientId(UserManager.getRandomUUID())
                .setName(CommonInfo.APP_NAME)
                .setUrl(CommonInfo.APP_URL)
                .setIcon(CommonInfo.APP_ICON_URL)
                .setDescription(CommonInfo.APP_DESCRIPTION)
                .setLogCallBack(logCallBack)
                .setWalletConnectCallBack(walletConnectCallBack));
    }

    private void savePeerMeta(Session.PeerMeta peerMeta) {
        if (null == peerMeta) {
            return;
        }
        SharedPreferenceManager.setWalletConnectPeerMeta(JsonUtil.gsonToJson(peerMeta));
    }

    private Session.PeerMeta getPeerMeta() {
        Session.PeerMeta peerMeta = JsonUtil.gsonParse(SharedPreferenceManager.getWalletConnectPeerMeta(), Session.PeerMeta.class);
        if (null == peerMeta) {
            peerMeta = new Session.PeerMeta();
        }
        return peerMeta;
    }

    private LogCallBack logCallBack = new LogCallBack() {
        @Override
        public void e(String tag, String log) {
            Log.e(tag, log);
        }

        @Override
        public void i(String tag, String log) {
            Log.i(tag, log);
        }
    };

    private WalletConnectCallBack walletConnectCallBack = new WalletConnectCallBack() {
        @Override
        public void onSessionRequest(Session.MethodCall.SessionRequest sessionRequest) {
            Log.e(TAG, "in onSessionRequest");
            LoadingDialog.INSTANCE.cancel();
            savePeerMeta(sessionRequest.getPeer().getMeta());
            WalletConnectManager.getInstance().approveSession(MixProtocolController.ETHAddress);
        }

        @Override
        public void onSessionUpdate(Session.MethodCall.SessionUpdate sessionUpdate) {
        }

        @Override
        public void onSendTransaction(Session.MethodCall.SendTransaction sendTransaction) {
            Log.e("dsfsdfsdfsdf",new Gson().toJson(sendTransaction));

            Intent intent = new Intent(mContext,ExchangeDetailActivity.class);
            intent.putExtra("from",sendTransaction.getFrom());
            intent.putExtra("to",sendTransaction.getTo());
            intent.putExtra("value",sendTransaction.getValue());
            intent.putExtra("gasLimit",sendTransaction.getGasLimit());
            intent.putExtra("gasPrice",sendTransaction.getGasPrice());
            intent.putExtra("nonce",sendTransaction.getNonce());
            intent.putExtra("data",sendTransaction.getData());
            intent.putExtra("id",sendTransaction.getId());
            intent.putExtra("gas",sendTransaction.getGas());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            Log.e(TAG, "in onSendTransaction ==" + new Gson().toJson(sendTransaction));

        }

        @Override
        public void onSignMessage(Session.MethodCall.SignMessage signMessage) {
            Log.e(TAG, "in onSignMessage");
        }

        @Override
        public void onCustom(Session.MethodCall.Custom custom) {
            Log.e(TAG, "in onCustom");
        }

        @Override
        public void onResponse(Session.MethodCall.Response response) {

        }
    };
}
