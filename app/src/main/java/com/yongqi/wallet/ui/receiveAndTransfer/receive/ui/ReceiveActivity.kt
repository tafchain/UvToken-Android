package com.yongqi.wallet.ui.receiveAndTransfer.receive.ui

import android.content.ContentResolver
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder
import com.afollestad.materialdialogs.DialogBehavior
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.ModalDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.IntentUtils
import com.blankj.utilcode.util.ToastUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.shehuan.nicedialog.BaseNiceDialog
import com.shehuan.nicedialog.NiceDialog
import com.shehuan.nicedialog.ViewConvertListener
import com.shehuan.nicedialog.ViewHolder
import com.yongqi.wallet.R
import com.yongqi.wallet.base.BaseActivity
import com.yongqi.wallet.databinding.ActivityReceiveBinding
import com.yongqi.wallet.ui.receiveAndTransfer.receive.viewModel.ReceiveViewModel
import com.yongqi.wallet.utils.DirUtils.createSharedImageDir
import io.reactivex.Observable.create
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_receive.*
import kotlinx.android.synthetic.main.common_title.*


class ReceiveActivity : BaseActivity<ActivityReceiveBinding, ReceiveViewModel>() {

    override fun getLayoutResource(): Int = R.layout.activity_receive

    var coinType = ""
    var coinTag = ""
    var address = ""
    private val shareImagePath = createSharedImageDir() + "/shareImage.jpg"

    override fun initData() {
        immersionBar {
//            fitsSystemWindows(true)//只适合纯色状态栏;解决状态栏和布局重叠问题，使用该属性,必须指定状态栏颜色
//            statusBarColor(R.color.transparent)
            statusBarDarkFont(true) //状态栏字体是深色，不写默认为亮色
            titleBar(iTitle) //解决状态栏和布局重叠问题,可以为任意view，如果是自定义xml实现标题栏的话，标题栏根节点不能为RelativeLayout或者ConstraintLayout，以及其子类;适配渐变色状态栏、侧滑返回
        }
        ivBack.setOnClickListener(onClickListener)
        coinType = intent.getStringExtra("coinType").toString()
        coinTag = intent.getStringExtra("coinTag").toString()
        address = intent.getStringExtra("address").toString()
        if(coinTag.isNullOrEmpty()){
            tvTitle.text = "$coinType${getString(R.string.receive)}"
        }else{
            tvTitle.text = "$coinType($coinTag)${getString(R.string.receive)}"
        }

        tvRight.visibility = View.VISIBLE
        tvRight.text = getString(R.string.share_it)
        tvRight.setOnClickListener(onClickListener)
        tvCopy.setOnClickListener(onClickListener)

        setImageBitmap(ivQrCode)



        tvReceiveAddress.text = address
    }

    private val onClickListener = View.OnClickListener {
        when (it.id) {
            R.id.ivBack -> {
                finish()
            }

            R.id.tvRight -> {//share_dialog
                showShareDialog(BottomSheet(LayoutMode.MATCH_PARENT))

            }
            R.id.tvCopy -> {
                ClipboardUtils.copyText(tvReceiveAddress.text)
                ToastUtils.make().show("${getString(R.string.copy_success)}")
            }
        }
    }


    private fun showShareDialog(dialogBehavior: DialogBehavior = ModalDialog) {
        NiceDialog.init()
            .setLayoutId(R.layout.share_dialog)     //设置dialog布局文件
//                    .setTheme(R.style.MyDialog) // 设置dialog主题，默认主题继承自Theme.AppCompat.Light.Dialog
            .setConvertListener(object : ViewConvertListener() {
                override fun convertView(holder: ViewHolder?, dialog: BaseNiceDialog?) {
                    val ivQr = holder?.getView<ImageView>(R.id.ivQr)
                    val tvReceiveAddress2 =holder?.getView<TextView>(R.id.tvReceiveAddress2)
                    val llSharedView = holder?.getView<LinearLayout>(R.id.llSharedView)

                    tvReceiveAddress2?.text = address

                    ivQr?.let { setImageBitmap(it) }
                    val tvShare = holder?.getView<TextView>(R.id.tvShare)
                    val tvCancel = holder?.getView<TextView>(R.id.tvCancel)
                    tvShare?.setOnClickListener { //TODO
                        var view2Bitmap = llSharedView?.let { it1 -> com.yongqi.wallet.utils.ImageUtils.createBitmap2(it1) }
                        var save = ImageUtils.save(view2Bitmap, shareImagePath, Bitmap.CompressFormat.JPEG, true)
                        startActivity(IntentUtils.getShareImageIntent(shareImagePath))
                        dialog?.dismiss()
                    }
                    tvCancel?.setOnClickListener {
                        dialog?.dismiss()
                    }
                }
            })
            .setDimAmount(0.3f)     //调节灰色背景透明度[0-1]，默认0.5f
//                    .setGravity(Gravity.CENTER)     //可选，设置dialog的位置，默认居中，可通过系统Gravity的类的常量修改，例如Gravity.BOTTOM（底部），Gravity.Right（右边），Gravity.BOTTOM|Gravity.Right（右下）
//            .setMargin(38)     //dialog左右两边到屏幕边缘的距离（单位：dp），默认0dp
//                    .setWidth(270)     //dialog宽度（单位：dp），默认为屏幕宽度，-1代表WRAP_CONTENT
//            .setHeight(159)     //dialog高度（单位：dp），默认为WRAP_CONTENT
            .setOutCancel(false)     //点击dialog外是否可取消，默认true
//            .setAnimStyle()     //设置dialog进入、退出的自定义动画；根据设置的Gravity，默认提供了左、上、右、下位置进入退出的动画
            .show(supportFragmentManager)     //显示dialog

    }


    private fun  setImageBitmap(iv: ImageView){
        create(ObservableOnSubscribe<Bitmap> {
            var qrBitmap = QRCodeEncoder.syncEncodeQRCode(
                address,
                BGAQRCodeUtil.dp2px(this, 240f),
                Color.parseColor("#000000"),
                Color.WHITE,
                BitmapFactory.decodeResource(resources,R.mipmap.pic_share_logo)
            )
            it.onNext(qrBitmap)
            it.onComplete()

        }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())//操作UI主要在UI线程
            .subscribe(object : Observer<Bitmap> {
                override fun onComplete() {
                    Log.e("qrBitmap", "onComplete")
                }

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: Bitmap) {
                    if (t != null) {
                        iv.setImageBitmap(t)
                    }
                }

                override fun onError(e: Throwable) {
                }
            })

    }

    private fun getResourcesUri(@DrawableRes id: Int): String? {//TODO  将布局转成图片,获取到图片path
        val resources: Resources = resources
        val uriPath = ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                resources.getResourcePackageName(id) + "/" +
                resources.getResourceTypeName(id) + "/" +
                resources.getResourceEntryName(id)
        Toast.makeText(this, "Uri:$uriPath", Toast.LENGTH_SHORT).show()
        return uriPath
    }


}