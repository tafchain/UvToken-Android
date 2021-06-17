package com.yongqi.wallet.ui.scan.ui

import android.content.Intent
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.Gravity
import android.view.View
import api.ValidateAddressRequest
import api.ValidateAddressResponse
import cn.bingoogolapple.qrcode.core.QRCodeView
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.SPUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.yongqi.wallet.R
import com.yongqi.wallet.base.BaseActivity
import com.yongqi.wallet.databinding.ActivityScanQrBinding
import com.yongqi.wallet.ui.scan.viewModel.ScanQrViewModel
import com.yongqi.wallet.utils.GlideEngine
import com.yongqi.wallet.utils.Uv1Helper
import kotlinx.android.synthetic.main.activity_scan_qr.*


class ScanQrActivity : BaseActivity<ActivityScanQrBinding, ScanQrViewModel>(), QRCodeView.Delegate {

    override fun getLayoutResource(): Int = R.layout.activity_scan_qr

    var mIntentType:String = ""
    var coinType:String = ""
    var coinTag:String = ""
    override fun initData() {
        if (null != intent.extras) {
            mIntentType = intent.extras!!.getString("intent_type","")
            coinType = intent.extras!!.getString("coinType","")
            coinTag = intent.extras!!.getString("coinTag","")

        }
        immersionBar {
            statusBarDarkFont(true) //状态栏字体是深色，不写默认为亮色
            titleBar(v) //解决状态栏和布局重叠问题,可以为任意view，如果是自定义xml实现标题栏的话，标题栏根节点不能为RelativeLayout或者ConstraintLayout，以及其子类;适配渐变色状态栏、侧滑返回
        }
        ivClose.setOnClickListener(onClickListener)
        tvPhotoAlbum.setOnClickListener(onClickListener)
        zxingview.setDelegate(this)
    }

    val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.ivClose -> {
                finish()
            }
            R.id.tvPhotoAlbum -> {//集成对二维码图片的解析功能
                selectFormAlbum()
            }
        }
    }



    private fun selectFormAlbum() {
        PictureSelector.create(this)
            .openGallery(PictureMimeType.ofAll())//从相册选择 相册 媒体类型 PictureMimeType.ofAll()、ofImage()、ofVideo()、ofAudio()
            .selectionMode(PictureConfig.SINGLE)//单选or多选 PictureConfig.SINGLE PictureConfig.MULTIPLE
            .compress(true)
            .loadImageEngine(GlideEngine.createGlideEngine())
            .forResult(object : OnResultCallbackListener<LocalMedia?> {
                override fun onResult(result: List<LocalMedia?>) {
                    LogUtils.e("result:${result.toString()}")
                    val localMedia = result[0]
                    val fileName = localMedia?.fileName//IMG_20201204_100439.jpg
                    val realPath =
                        localMedia?.realPath///storage/emulated/0/DCIM/Camera/IMG_20201204_100439.jpg
                    val compressPath =
                        localMedia?.compressPath//storage/emulated/0/PictureSelector/CameraImage/PictureSelector_20191112_135334.JPEG
                    val path = localMedia?.path //content://media/external/file/52
                    LogUtils.e("path:$path;compressPath:$compressPath")

                    if (compressPath.isNullOrEmpty()) {
                        return
                    }
                    // 本来就用到 QRCodeView 时可直接调 QRCodeView 的方法，走通用的回调
                    zxingview.decodeQRCode(compressPath)
                }

                override fun onCancel() {

                }
            })
    }


    override fun onStart() {
        super.onStart()
        zxingview.startCamera() // 打开后置摄像头开始预览，但是并未开始识别
//        mZXingView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT); // 打开前置摄像头开始预览，但是并未开始识别
        zxingview.startSpotAndShowRect() // 显示扫描框，并开始识别


    }

    override fun onStop() {
        zxingview.stopCamera() // 关闭摄像头预览，并且隐藏扫描框
        super.onStop()
    }

    override fun onDestroy() {
        zxingview.onDestroy() // 销毁二维码扫描控件
        super.onDestroy()
    }


    private fun vibrate() {
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(500)
    }

    /**
     * 处理扫描结果
     * @param result 摄像头扫码时只要回调了该方法 result 就一定有值，不会为 null。解析本地图片或 Bitmap 时 result 可能为 null
     */
    override fun onScanQRCodeSuccess(result: String?) {
        vibrate()
        if (result.isNullOrEmpty())return
        Log.e(tag(), "onScanQRCodeSuccess:$result")
        if (result.startsWith("wc")) {
            SPUtils.getInstance().put("wcContent", result)
            finish()
            return
        }
        if (mIntentType == "intent_type_transfer") {
            isAddressIllegal(result)
        }else{
            startActivity(Intent(this,ScanResultActivity::class.java)
                .putExtra("address",result))
        }
    }

    /**
     * 摄像头环境亮度发生变化
     *
     * @param isDark 是否变暗
     */
    override fun onCameraAmbientBrightnessChanged(isDark: Boolean) {
//        Log.e(tag(), "onCameraAmbientBrightnessChanged:$isDark")
    }

    /**
     * 处理打开相机出错
     */
    override fun onScanQRCodeOpenCameraError() {
//        Log.e(tag(), "onScanQRCodeOpenCameraError")
    }


    /**
     * 校验输入的地址是否合法
     */
    private fun isAddressIllegal(result: String) {
        val validateAddressRequest = ValidateAddressRequest()
        validateAddressRequest.address = result
        if (coinType == "BTC" || coinTag == "OMNI") {
            validateAddressRequest.coinType = "BTC"
        }
        if (coinType == "ETH" || coinTag == "ERC20") {
            validateAddressRequest.coinType = "ETH"
        }
        Uv1Helper.validateAddress(this, validateAddressRequest, object : Uv1Helper.ResponseDataCallback<ValidateAddressResponse> {
                override fun onSuccess(data: ValidateAddressResponse?) {
                    if (data?.valid!!) {
                        val bundle = Bundle()
                        bundle.putString("address",result)
                        intent.putExtras(bundle)
                        setResult(1,intent)
                        finish()
                    } else {
                        //zxingview.startSpotAndShowRect() // 显示扫描框，并开始识别
                        ToastUtils.make().show(R.string.invalid_address)
                        val bundle = Bundle()
                        bundle.putString("address",result)
                        intent.putExtras(bundle)
                        setResult(1,intent)
                        finish()
                    }
                }

                override fun onError(e: java.lang.Exception?) {
                    //zxingview.startSpotAndShowRect() // 显示扫描框，并开始识别
                    ToastUtils.make().show(R.string.invalid_address)
                    val bundle = Bundle()
                    bundle.putString("address",result)
                    intent.putExtras(bundle)
                    setResult(1,intent)
                    finish()
                }

            })
    }





}