package  com.yongqi.wallet.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.LanguageUtils
import com.blankj.utilcode.util.SPUtils
import com.yongqi.wallet.App.Companion.context
import com.yongqi.wallet.BuildConfig
import com.yongqi.wallet.api.NetApi
import com.yongqi.wallet.bean.ApiInResponse
import com.yongqi.wallet.bean.ResponseWrapper
import com.yongqi.wallet.net.APIClient
import com.yongqi.wallet.net.APIResponse
import com.yongqi.wallet.ui.main.bean.UploadDeviceInfoRequest
import com.yongqi.wallet.ui.manageFinances.bean.DAppBean
import com.yongqi.wallet.ui.manageFinances.request.DAppSearchRequest
import com.yongqi.wallet.utils.ActivityCollector
import com.yongqi.wallet.utils.EquipmentUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*


/**
 * 这是 “所有Activity的父类”
 */
// public final class
// P extends LoginPresenter                     vs     P: LoginPresenter
// P extends LoginPresenter & Serializable      vs     where P : IBasePresenter,  P: Serializable
abstract class BaseActivity<V : ViewDataBinding, VM : BaseViewModel<V>> : AppCompatActivity() {

    protected var binding: V? = null
    protected var viewModle: VM? = null

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        LanguageUtils.attachBaseContext(newBase)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


//        immersionBar {
//            fitsSystemWindows(true)//只适合纯色状态栏;解决状态栏和布局重叠问题，使用该属性,必须指定状态栏颜色
////            statusBarColor(R.color.transparent)
////            titleBar(view) //解决状态栏和布局重叠问题,可以为任意view，如果是自定义xml实现标题栏的话，标题栏根节点不能为RelativeLayout或者ConstraintLayout，以及其子类;适配渐变色状态栏、侧滑返回
//        }
//        ActivityCollector.addActivity(this)
        hideActionBar()
        ActivityCollector.addActivity(this)
        binding = DataBindingUtil.setContentView(this, getLayoutResource())
//        viewModle = TUtil.getT(this, 1)!!
        viewModle?.init(this, binding!!, this.baseContext)
//        initListener()//放到ViewModel中
        initData()
        if (SPUtils.getInstance().getLong("time",0L) == 0L || SPUtils.getInstance().getLong("time") - System.currentTimeMillis() > 24*60*1000) {
            upLoadDeviceInfo()
        }

    }


    private fun upLoadDeviceInfo() {
        val uploadDeviceInfoRequest = UploadDeviceInfoRequest()
        uploadDeviceInfoRequest.app_version = BuildConfig.VERSION_NAME
        uploadDeviceInfoRequest.brand = EquipmentUtil.getDeviceBrand()
        uploadDeviceInfoRequest.os_version = EquipmentUtil.getSystemVersion()
        uploadDeviceInfoRequest.phone_type = EquipmentUtil.getSystemModel()
        uploadDeviceInfoRequest.device_uuid = DeviceUtils.getAndroidID()

        context.let {
            APIClient.instance.instanceRetrofit(NetApi::class.java)
                .uploadDeviceInfo(BuildConfig.API_URL + "wallet/statistics/dailycount", uploadDeviceInfoRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : APIResponse<String>(it) {
                    override fun success(data: String?) {
                        SPUtils.getInstance().put("time",System.currentTimeMillis())
                        Log.e("upLoadDeviceInfo","success" + data!!)
                    }

                    override fun failure(errorMsg: String?) {
                        SPUtils.getInstance().put("time",System.currentTimeMillis())
                        Log.e("upLoadDeviceInfo","failure" + errorMsg!!)
                    }


                })
        }
    }

    abstract fun getLayoutResource():Int

//    abstract fun initListener()
    abstract fun initData()

    override fun onStart() {
        super.onStart()
    }

    override fun onRestart() {
        super.onRestart()
        //清楚截屏限制
//        window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
    }

    override fun onPause() {
        super.onPause()
//        if (ActivityUtils.isForeground(this)) {
//            window.setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE)
//        }
    }





    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.removeActivity(this)
        viewModle?.destroy()
    }

    // 类似于工具类函数
    /**
     * 暴漏给自己的孩子   隐藏ActionBar
     */
    fun hideActionBar() {
        // 任何 Java代码东西，必须用 ？ 允许为null，来接收
        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()
    }

    /**
     * 暴漏给自己的孩子   显示ActionBar
     */
    fun showActionBar() {
        supportActionBar?.show()
    }

     fun tag(): String? {
        return this.localClassName
    }

    // .......  同学们自己去完成

    // TODO 自己去扩展了，可以增加很多的功能
    //  .....
//    /**
//     *  弹出 加载框
//     */
//    fun showDialog(isShow: Boolean) {
//        if (!isShow) {
//            LoadingDialog.show(this)
//        }
//    }
//
//    /**
//     *  隐藏 加载框
//     */
//    fun hideDialog(isShow: Boolean) {
//        if (isShow) {
//            // 取消加载
//            LoadingDialog.cancel()
//        }
//    }

//    @TargetApi(Build.VERSION_CODES.N)
//    open fun updateResources(context: Context): Context {
//        val resources: Resources = context.resources
//        val language = SPUtils.getInstance().getString("language")
//        val locale: Locale = when (language) {
//            "en" -> {
//                Locale.ENGLISH // getSetLocale方法是获取新设置的语言
//            }
//            "zh" -> {
//                Locale.CHINESE // getSetLocale方法是获取新设置的语言
//            }
//            else -> {
//                Locale.CHINESE // getSetLocale方法是获取新设置的语言
//            }
//        }
//
//        val configuration: Configuration = resources.configuration
//        configuration.setLocale(locale)
//        configuration.setLocales(LocaleList(locale))
//        return context.createConfigurationContext(configuration)
//    }
}