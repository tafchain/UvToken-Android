package com.yongqi.wallet.view;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lxj.xpopup.core.PositionPopupView;
import com.yongqi.wallet.R;
import com.yongqi.wallet.config.UserConfig;
import com.yongqi.wallet.ui.quotes.ui.FreeFragment;
import com.yongqi.wallet.utils.DialogUtils;
import com.yongqi.wallet.utils.MyToastUtil;

import java.util.Collection;
import java.util.List;

public class MarketPopupView extends PositionPopupView {

    private String mCoinName;
    private FreeFragment mFreeFragment;
    private Context mContext;
    public MarketPopupView(@NonNull Context context,String coinName) {
        super(context);
        this.mCoinName = coinName;
        this.mContext = context;
    }

    public MarketPopupView(@NonNull Context context, String coinName, FreeFragment freeFragment) {
        super(context);
        this.mContext = context;
        this.mCoinName = coinName.toUpperCase();
        this.mFreeFragment= freeFragment;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.layout_popup_market;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        TextView  tv_name = findViewById(R.id.tv_name);
        List<String> list = UserConfig.getInstance().getCollectCoin();
        if (CollectionUtils.isEmpty(list)) {
            tv_name.setText(R.string.add_optional);
        }else {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).equals(mCoinName)) {
                    tv_name.setText(R.string.cancel_select);
                }
            }
        }

        tv_name.setOnClickListener(v -> {
            if (tv_name.getText().equals("取消自选") || tv_name.getText().equals("Cancel")) {
                UserConfig.getInstance().removeCollectCoin(mCoinName);
                ToastUtils.showShort(R.string.has_cancel);
                if (null != mFreeFragment) {
                    mFreeFragment.getQuotes();
                }
            }else {
                UserConfig.getInstance().addCollectCoin(mCoinName);
                ToastUtils.showShort(R.string.has_add);
                if (null != mFreeFragment) {
                    mFreeFragment.getQuotes();
                }
            }
            dismiss();
        });
    }
}
