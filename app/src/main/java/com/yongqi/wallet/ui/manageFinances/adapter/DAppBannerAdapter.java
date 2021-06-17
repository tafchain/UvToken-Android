package com.yongqi.wallet.ui.manageFinances.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.yongqi.wallet.ui.manageFinances.bean.DAppDataBean;
import com.yongqi.wallet.utils.GlideEngine;
import com.youth.banner.adapter.BannerAdapter;

import java.util.List;


public class DAppBannerAdapter extends BannerAdapter<DAppDataBean, DAppBannerAdapter.BannerViewHolder> {
    private final Context mContext;
    public DAppBannerAdapter(Context context, List<DAppDataBean> datas) {
        super(datas);
        this.mContext = context;
    }

    @Override
    public DAppBannerAdapter.BannerViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new BannerViewHolder(imageView);
    }

    @Override
    public void onBindView(DAppBannerAdapter.BannerViewHolder holder, DAppDataBean data, int position, int size) {
        GlideEngine.createGlideEngine().loadImage(mContext,data.getImg(),holder.imageView);
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public BannerViewHolder(@NonNull ImageView view) {
            super(view);
            this.imageView = view;
        }
    }
}
