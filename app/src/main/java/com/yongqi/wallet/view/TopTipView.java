package com.yongqi.wallet.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.yongqi.wallet.R;

public class TopTipView extends RelativeLayout {
    private final Context mContext;
    public TopTipView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public TopTipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater.from(mContext).inflate(R.layout.view_top_tip, this);

    }
}
