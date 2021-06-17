package com.yongqi.wallet.ui.createWallet.dialog;

import android.view.View;
import android.widget.TextView;
import com.shehuan.nicedialog.BaseNiceDialog;
import com.shehuan.nicedialog.ViewHolder;
import com.yongqi.wallet.R;

public class CustomTipDialog extends BaseNiceDialog {
    private int mContent;
    private int mTitle;
    private boolean mIsNeedCancel = true;
    private int mConfirmText;

    private ResponseDataCallback mResponseDataCallback;

    public interface ResponseDataCallback {
        void onConfirm();
        void onCancel();
    }

    public static CustomTipDialog newInstance() {
        return new CustomTipDialog();
    }

    @Override
    public int intLayoutId() {
        return R.layout.layout_tip;
    }

    public void setContent(int content) {
        this.mContent = content;
    }

    public void setTitle(int title) {
        this.mTitle = title;
    }

    public void setNeedCancel(Boolean isNeedCancel) {
        this.mIsNeedCancel = isNeedCancel;
    }

    public void setConfirmText(int confirmText) {
        this.mConfirmText = confirmText;
    }

    public void setOnClickResultListener(ResponseDataCallback responseDataCallback) {
        this.mResponseDataCallback = responseDataCallback;
    }

    @Override
    public void convertView(ViewHolder holder, BaseNiceDialog dialog) {
        TextView tvContent = holder.getView(R.id.tv_content);
        tvContent.setText(mContent);

        TextView tvTitle = holder.getView(R.id.tv_title);
        if (mTitle != 0) {
            tvTitle.setText(mTitle);
        }

        TextView tvCancel = holder.getView(R.id.tvCancel);
        if (mIsNeedCancel) {
            tvCancel.setVisibility(View.VISIBLE);
        }else {
            tvCancel.setVisibility(View.GONE);
        }
        tvCancel.setOnClickListener(v -> {
            if (null != mResponseDataCallback) {
                mResponseDataCallback.onCancel();
            }
            dismissAllowingStateLoss();
        });

        TextView tvConfirm = holder.getView(R.id.tvAgree);
        if (mConfirmText != 0) {
            tvConfirm.setText(mConfirmText);
        }
        tvConfirm.setOnClickListener(v -> {
            if (null != mResponseDataCallback) {
                mResponseDataCallback.onConfirm();
            }
            dismissAllowingStateLoss();
        });


    }
}
