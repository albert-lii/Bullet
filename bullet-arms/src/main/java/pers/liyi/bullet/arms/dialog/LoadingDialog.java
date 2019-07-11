package pers.liyi.bullet.arms.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.lang.reflect.Field;

import pers.liyi.bullet.arms.R;
import pers.liyi.bullet.arms.base.dialog.BaseDialogFragment;
import pers.liyi.bullet.arms.widget.CircleProgressBar;


/**
 * 网络请求的加载进度框
 */
public class LoadingDialog extends BaseDialogFragment {
    private CircleProgressBar progressBar;
    private TextView tv_msg;
    private String mProgressBarText;
    private String mMsg;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getContext(), R.style.bulletArms_dialog_common_withoutDim);
        dialog.setContentView(R.layout.bullet_arms_dlg_loading);
        dialog.setCanceledOnTouchOutside(false);

        progressBar = dialog.findViewById(R.id.progressbar);
        tv_msg=dialog.findViewById(R.id.tv_msg);

        if(!TextUtils.isEmpty(mProgressBarText)){
            progressBar.setText(mProgressBarText);
        }
        if (!TextUtils.isEmpty(mMsg)) {
            tv_msg.setText(mMsg);
            tv_msg.setVisibility(View.VISIBLE);
        }else {
            tv_msg.setVisibility(View.GONE);
        }
        progressBar.startSpinning();
        return dialog;
    }

    public void setMessage(String msg) {
        this.mMsg = msg;
        if(tv_msg!=null){
            if (!TextUtils.isEmpty(msg)) {
                tv_msg.setText(msg);
            }
        }
    }

    public void setProgressBarText(String text){
        this.mProgressBarText=text;
        if(progressBar!=null){
            if (!TextUtils.isEmpty(text)) {
                progressBar.setText(text);
            }
        }
    }

    public void setProgress(int progress, String text) {
        if (progressBar != null) {
            progressBar.setProgress(progress);
            if (!TextUtils.isEmpty(text)) {
                progressBar.setText(text);
            }
        }
    }

    public void show(FragmentManager manager) {
        show(manager, this.getClass().getSimpleName());
    }

    @Override
    public void show(FragmentManager manager, String tag) {
//        super.show(manager, tag);
        try {
            Field mDismissed = this.getClass().getSuperclass().getDeclaredField("mDismissed");
            Field mShownByMe = this.getClass().getSuperclass().getDeclaredField("mShownByMe");
            mDismissed.setAccessible(true);
            mShownByMe.setAccessible(true);
            mDismissed.setBoolean(this, false);
            mShownByMe.setBoolean(this, true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    public void close() {
        if (progressBar != null) {
            progressBar.stopSpinning();
        }
        dismissAllowingStateLoss();
    }
}
