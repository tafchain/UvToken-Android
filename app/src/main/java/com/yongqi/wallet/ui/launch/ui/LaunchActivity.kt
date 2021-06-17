package com.yongqi.wallet.ui.launch.ui

import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import api.ParamCallback
import api.ParamRequest
import api.ParamResponse
import com.azhon.appupdate.config.UpdateConfiguration
import com.azhon.appupdate.listener.OnDownloadListenerAdapter
import com.azhon.appupdate.manager.DownloadManager
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.ToastUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.shehuan.nicedialog.BaseNiceDialog
import com.shehuan.nicedialog.NiceDialog
import com.shehuan.nicedialog.ViewConvertListener
import com.shehuan.nicedialog.ViewHolder
import com.yongqi.wallet.App.Companion.context
import com.yongqi.wallet.BuildConfig
import com.yongqi.wallet.R
import com.yongqi.wallet.api.NetApi
import com.yongqi.wallet.base.BaseActivity
import com.yongqi.wallet.bean.VersionUpgradeRequest
import com.yongqi.wallet.bean.VersionUpgradeResponse
import com.yongqi.wallet.bean.Wallet
import com.yongqi.wallet.config.AppConst
import com.yongqi.wallet.databinding.ActivityLaunchBinding
import com.yongqi.wallet.db.wallet.WalletRepository
import com.yongqi.wallet.net.APIClient
import com.yongqi.wallet.net.APIResponse
import com.yongqi.wallet.ui.launch.viewModel.LaunchViewModel
import com.yongqi.wallet.ui.main.ui.HomePageActivity
import com.yongqi.wallet.ui.vm.HttpApiHelper
import com.yongqi.wallet.ui.wallet.ui.ImportOrCreateWalletActivity
import com.yongqi.wallet.utils.CompareVersion
import com.yongqi.wallet.utils.SdkUtils
import com.yongqi.wallet.utils.Uv1Helper
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_launch.*
import sdk.Sdk
import uv1.Uv1


/**
 * 1、Android app数据存哪安全
 * 2、room;
 *
 */
class LaunchActivity : BaseActivity<ActivityLaunchBinding, LaunchViewModel>() {


    /**
     * 未用到的Sdk方法 TODO:
     * 1、createMnemonic(CreateMnemonicRequest var0, CreateMnemonicCallback var1)
     * 2、findMnemonic(FindMnemonicRequest var0, FindMnemonicCallback var1)
     * 3、verifyMnemonic(VerifyMnemonicRequest var0, VerifyMnemonicCallback var1)
     * 4、addCoinTypes(AddCoinTypeRequest var0, AddCoinTypeCallback var1);
     * 5、removeKey(RemoveKeyRequest var0, RemoveKeyCallback var1);
     */
    override fun getLayoutResource(): Int = R.layout.activity_launch


    override fun initData() {
        immersionBar {
            titleBar(ll) //解决状态栏和布局重叠问题,可以为任意view，如果是自定义xml实现标题栏的话，标题栏根节点不能为RelativeLayout或者ConstraintLayout，以及其子类;适配渐变色状态栏、侧滑返回
        }
        /**
         * 设计稿中logo的marginTop高度占手机屏幕的高度
         */
        val ratio: Float = 196f / 667
        val screenHight = ScreenUtils.getScreenHeight()
//        val screenWidth = ScreenUtils.getScreenWidth().toFloat()
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        lp.gravity = Gravity.CENTER_HORIZONTAL
        val marginTop: Int = (ratio * screenHight).toInt()
        lp.setMargins(0, marginTop, 0, 0)
        ivLogo.layoutParams = lp
    }



    override fun onResume() {
        super.onResume()
        SPUtils.getInstance().put("isShowBackup", true)
        val paramRequest = ParamRequest()
        if (BuildConfig.FLAVOR == "online"||BuildConfig.FLAVOR == "onlineuvtest") {
            paramRequest.net = "main"
        }
        if (BuildConfig.FLAVOR == "devtest"||BuildConfig.FLAVOR == "devuvtest") {
            paramRequest.net = "regtest"
        }
        SdkUtils.paramConfig()
        Uv1Helper.paramConfig(this,paramRequest,object :Uv1Helper.ResponseDataCallback<ParamResponse> {
            override fun onSuccess(data: ParamResponse?) {
                Log.e("paramResponse",data?.net + data?.httpUrl)
                SPUtils.getInstance().put("isDev", data?.net)
            }

            override fun onError(e: Exception?) {
                ToastUtils.showShort(e?.localizedMessage)
            }

        })
        updateVersion(true)
    }


    private fun toHomePage(){
        val walletRepository = WalletRepository(this@LaunchActivity)
        val wallets: List<Wallet?>? = walletRepository.getAll()
        val handler = Handler()
        if (wallets.isNullOrEmpty()) {//1、如果数据库中存在钱包,则进入首页;2、否则进入创建和导入钱包页面
            handler.postDelayed({
                startActivity(Intent().setClass(this, ImportOrCreateWalletActivity::class.java))
                finish()
            }, 1000)
        } else {
            handler.postDelayed({
                startActivity(Intent(this, HomePageActivity::class.java))
                finish()
            }, 1000)
        }
    }

    /**
     * 版本更新
     */
    private fun updateVersion(isShowDialog: Boolean) {
        HttpApiHelper.versionUpgrade(this,successC= {versionUpgradeResponse->
            Log.e(tag(), "onResponse==${versionUpgradeResponse.toString()}")
            val lastVersion = versionUpgradeResponse?.last_version
            val forceUpgrade = versionUpgradeResponse?.force_upgrade
            val content = versionUpgradeResponse?.content
            val content_en = versionUpgradeResponse?.content_en
            val appLink = versionUpgradeResponse?.app_link
            val currentVersion = AppConst.getVersionName().replace(".", "")//当前版本号
            val newVersion = lastVersion?.replace(".", "")//最新版本号

            if (!newVersion.isNullOrEmpty() && currentVersion.isNotEmpty()) {
                if (CompareVersion.compareVersion(lastVersion,AppConst.getVersionName()) == 1) {
                    if (!appLink.isNullOrEmpty()) {
                        showVersionUpdateDialog(
                            lastVersion,
                            content,
                            content_en,
                            forceUpgrade,
                            appLink
                        )
                    } else {
                        toHomePage()
                    }
                } else {
                    toHomePage()
                }
            } else {
                toHomePage()
            }

        },
        failureC = {errorMsg->
            toHomePage()
        })
    }


    private var pbDownload: ProgressBar? = null

    /**
     * 版本更新弹窗
     * (lastVersion,forceUpgrade,content,appLink)
     */
    fun showVersionUpdateDialog(
        lastVersion: String?,
        updateContent: String?,
        updateContentEn:String?,
        forceUpgrade: Boolean?,
        appLink: String?
    ) {
        NiceDialog.init()
            .setLayoutId(R.layout.new_version_update_dialog)     //设置dialog布局文件
//                    .setTheme(R.style.MyDialog) // 设置dialog主题，默认主题继承自Theme.AppCompat.Light.Dialog
            .setConvertListener(object : ViewConvertListener() {
                override fun convertView(holder: ViewHolder?, dialog: BaseNiceDialog?) {

                    val tvVersionCode = holder?.getView<TextView>(R.id.tvVersionCode)
                    val content = holder?.getView<TextView>(R.id.tvContent)
                    val forceUpdate = holder?.getView<Button>(R.id.btnForceUpdate)//强更
                    val llUpdate = holder?.getView<LinearLayout>(R.id.llUpdate)//非强
                    val cancel = holder?.getView<TextView>(R.id.tvCancel)
                    val update = holder?.getView<TextView>(R.id.tvUpdate)
                    val downloadText = holder?.getView<TextView>(R.id.tvDownloadText)//下载进度条
                    pbDownload = holder?.getView<ProgressBar>(R.id.pbDownload)!!
                    tvVersionCode?.text = "V$lastVersion"
                    if (SPUtils.getInstance().getString("language","简体中文") == "简体中文") {
                        content?.text = "$updateContent"
                    }else{
                        content?.text = "$updateContentEn"
                    }
                    if (forceUpgrade!!) {//强更
                        llUpdate?.visibility = View.GONE
                        forceUpdate?.setOnClickListener {
                            downloadApk(appLink)
                            forceUpdate?.visibility = View.GONE
                            downloadText?.visibility = View.VISIBLE
                            pbDownload?.visibility = View.VISIBLE
                        }
                    } else {//非强
                        forceUpdate?.visibility = View.GONE
                        cancel?.setOnClickListener {
                            dialog?.dismiss()
                        }
                        update?.setOnClickListener {
                            downloadApk(appLink)
                            llUpdate?.visibility = View.GONE
                            downloadText?.visibility = View.VISIBLE
                            pbDownload?.visibility = View.VISIBLE
                        }
                    }
                }
            })
            .setDimAmount(0.3f)     //调节灰色背景透明度[0-1]，默认0.5f
//                    .setGravity(Gravity.CENTER)     //可选，设置dialog的位置，默认居中，可通过系统Gravity的类的常量修改，例如Gravity.BOTTOM（底部），Gravity.Right（右边），Gravity.BOTTOM|Gravity.Right（右下）
            .setMargin(38)     //dialog左右两边到屏幕边缘的距离（单位：dp），默认0dp
//                    .setWidth(270)     //dialog宽度（单位：dp），默认为屏幕宽度，-1代表WRAP_CONTENT
//            .setHeight(159)     //dialog高度（单位：dp），默认为WRAP_CONTENT
            .setOutCancel(false)     //点击dialog外是否可取消，默认true
            //.setAnimStyle(R.style.EnterExitAnimation)     //设置dialog进入、退出的自定义动画；根据设置的Gravity，默认提供了左、上、右、下位置进入退出的动画
            .show(supportFragmentManager)     //显示dialog
    }

    private fun downloadApk(appLink: String?) {
        /*
         * 整个库允许配置的内容
         * 非必选
         */
        val configuration = UpdateConfiguration() //输出错误日志
            .setEnableLog(true) //设置自定义的下载
            //.setHttpManager()
            //下载完成自动跳动安装页面
            .setJumpInstallPage(true) //设置对话框背景图片 (图片规范参照demo中的示例图)
            //.setDialogImage(R.drawable.ic_dialog)
            //设置按钮的颜色
            //.setDialogButtonColor(Color.parseColor("#E743DA"))
            //设置对话框强制更新时进度条和文字的颜色
            //.setDialogProgressBarColor(Color.parseColor("#E743DA"))
            //设置按钮的文字颜色
            .setDialogButtonTextColor(Color.WHITE) //设置是否显示通知栏进度
//            .setShowNotification(true) //设置是否提示后台下载toast
//            .setShowBgdToast(false) //设置强制更新
//            .setForcedUpgrade(false) //设置对话框按钮的点击监听
//            .setButtonClickListener(this) //设置下载过程的监听
            .setOnDownloadListener(listenerAdapter)
        val manager = DownloadManager.getInstance(this)
        manager.setApkName("UvToken.apk")
            .setApkUrl(appLink)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setShowNewerToast(true)
            .setConfiguration(configuration)
//            .setApkVersionCode(2)
//            .setApkVersionName("2.1.8")
//            .setApkSize("20.4")
//            .setApkDescription(getString(R.string.dialog_msg))
//            .setApkMD5("DC501F04BBAA458C9DC33008EFED5E7F")
            .download()
    }

    private val listenerAdapter: OnDownloadListenerAdapter = object : OnDownloadListenerAdapter() {
        /**
         * 下载中
         *
         * @param max      总进度
         * @param progress 当前进度
         */
        override fun downloading(max: Int, progress: Int) {
            val curr = (progress / max.toDouble() * 100.0).toInt()
            pbDownload?.max = 100
            pbDownload?.progress = curr
        }
    }


}