package pers.liyi.bullet.arms.base.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import pers.liyi.bullet.arms.dialog.LoadingDialog;
import pers.liyi.bullet.utils.box.ToastUtils;


/**
 * 用于和 ViewPager 结合使用时的懒加载模式
 */
public abstract class BaseLazyFragment extends Fragment {
    private View mContentView;
    // 当前 Fragment 是否可见
    private boolean isVisible = false;
    // 是否与 View 建立起映射关系
    private boolean isInitView = false;
    // 是否是第一次加载数据
    public boolean isFirstLoad = true;

    private LoadingDialog mLoadingDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 避免重复加载 UI
        if (mContentView == null) {
            mContentView = inflater.inflate(getLayoutId(), container, false);
        }
        ViewGroup parent = (ViewGroup) mContentView.getParent();
        if (parent != null) {
            parent.removeView(mContentView);
        }
        initView();
        isInitView = true;
        lazyLoadData();
        return mContentView;
    }

    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void initData();

    public <VM extends ViewModel> VM createViewModel(Class<VM> clz) {
        return ViewModelProviders.of(this).get(clz);
    }

    public View getContentView() {
        return mContentView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        // 懒加载，针对 ViewPager 的预加载
        if (isVisibleToUser) {
            isVisible = true;
            lazyLoadData();
        } else {
            isVisible = false;
        }
    }

    private void lazyLoadData() {
        if (!isFirstLoad || !isVisible || !isInitView) return;
        initData();
        isFirstLoad = false;
    }

    public void showToast(String text) {
        ToastUtils.show(getActivity().getApplicationContext(), text);
    }

    public void showLoading() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog();
        }
        mLoadingDialog.show(getActivity().getSupportFragmentManager());
    }

    public void showLoading(String msg){
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog();
        }
        mLoadingDialog.setMessage(msg);
        mLoadingDialog.show(getActivity().getSupportFragmentManager());
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
