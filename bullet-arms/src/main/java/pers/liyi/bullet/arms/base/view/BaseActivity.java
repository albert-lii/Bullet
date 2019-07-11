package pers.liyi.bullet.arms.base.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.Transition;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import pers.liyi.bullet.arms.dialog.LoadingDialog;
import pers.liyi.bullet.utils.box.ToastUtils;


public abstract class BaseActivity extends AppCompatActivity {
    private LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beforeLayout(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }
        setContentView(getLayoutId());
        initView();
    }

    protected void beforeLayout(Bundle savedInstanceState) {

    }

    protected abstract int getLayoutId();

    protected abstract void initView();

    public <VM extends ViewModel> VM createViewModel(Class<VM> clz) {
        return ViewModelProviders.of(this).get(clz);
    }

    public void navigateNoAnimTo(Activity activity) {
        Intent intent = new Intent(this, activity.getClass());
        startActivity(intent);
    }

    public void navigateTo(Activity activity) {
        navigateV21To(activity);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void navigateV21To(Activity activity) {
        startActivity(new Intent(this, activity.getClass()),
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void navigateV21ShareTo(Activity activity, View sharedElement, String sharedElementName) {
        startActivity(new Intent(this, activity.getClass()),
                ActivityOptions.makeSceneTransitionAnimation(this, sharedElement, sharedElementName).toBundle());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void navigateV21ShareTo(Activity activity, Pair<View, String>... sharedElements) {
        startActivity(new Intent(this, activity.getClass()),
                ActivityOptions.makeSceneTransitionAnimation(this, sharedElements).toBundle());
    }

    protected void setWindowSlideTranstion() {
        setWindowSlideTranstion(300);
    }

    /**
     * 设置页面滑动动画
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void setWindowSlideTranstion(long duration) {
        setWindowTranstion(new Slide(Gravity.RIGHT).setDuration(duration), new Slide(Gravity.RIGHT).setDuration(duration));
    }

    /**
     * 设置页面切换动画
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void setWindowTranstion(Transition enter, Transition exit) {
        getWindow().setEnterTransition(enter);
        getWindow().setExitTransition(exit);
    }

    public void showToast(@StringRes int stringId) {
        ToastUtils.show(this.getApplicationContext(), stringId);
    }

    public void showToast(String text) {
        ToastUtils.show(this.getApplicationContext(), text);
    }

    public void showLoading() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog();
        }
        mLoadingDialog.show(getSupportFragmentManager());
    }

    public void showLoading(String msg) {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog();
        }
        mLoadingDialog.setMessage(msg);
        mLoadingDialog.show(getSupportFragmentManager());
    }

    public void closeLoading() {
        if (mLoadingDialog != null && mLoadingDialog.getDialog() != null) {
            if (mLoadingDialog.getDialog().isShowing()) {
                mLoadingDialog.close();
            }
            mLoadingDialog = null;
        }
    }

    public LoadingDialog getLoading() {
        return mLoadingDialog;
    }
}
