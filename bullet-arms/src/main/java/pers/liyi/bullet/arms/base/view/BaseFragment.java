package pers.liyi.bullet.arms.base.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import pers.liyi.bullet.arms.dialog.LoadingDialog;
import pers.liyi.bullet.utils.box.ToastUtils;


public abstract class BaseFragment extends Fragment {
    private View mContentView;

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
        return mContentView;
    }

    protected abstract int getLayoutId();

    protected abstract void initView();

    public <VM extends ViewModel> VM createViewModel(Class<VM> clz) {
        return ViewModelProviders.of(this).get(clz);
    }

    public View getContentView() {
        return mContentView;
    }

    public void showToast(@StringRes int stringId) {
        ToastUtils.show(getActivity().getApplicationContext(), stringId);
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
