package  com.yongqi.wallet.base

import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.yongqi.wallet.utils.ActivityCollector

/**
 * 这是 “所有Activity的父类”
 */
// public final class
// P extends LoginPresenter                     vs     P: LoginPresenter
// P extends LoginPresenter & Serializable      vs     where P : IBasePresenter,  P: Serializable
abstract class BaseActivity2<V : ViewDataBinding> : AppCompatActivity() {

    protected var binding: V? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        immersionBar {
//            fitsSystemWindows(true)//只适合纯色状态栏;解决状态栏和布局重叠问题，使用该属性,必须指定状态栏颜色
////            statusBarColor(R.color.transparent)
////            titleBar(view) //解决状态栏和布局重叠问题,可以为任意view，如果是自定义xml实现标题栏的话，标题栏根节点不能为RelativeLayout或者ConstraintLayout，以及其子类;适配渐变色状态栏、侧滑返回
//        }
//        ActivityCollector.addActivity(this)
        hideActionBar()
        binding = DataBindingUtil.setContentView(this, getLayoutResource())
        ActivityCollector.addActivity(this)
        initData()
    }

    abstract fun getLayoutResource():Int

//    abstract fun initListener()
    abstract fun initData()

    override fun onStart() {
        super.onStart()
    }


    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.removeActivity(this)
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