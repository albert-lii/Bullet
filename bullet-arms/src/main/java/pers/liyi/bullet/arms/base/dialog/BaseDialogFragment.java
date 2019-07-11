package pers.liyi.bullet.arms.base.dialog;


import android.app.Dialog;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.FloatRange;
import androidx.fragment.app.DialogFragment;

/**
 * DialogFragment 的基类
 */
public class BaseDialogFragment extends DialogFragment {

    /**
     * 设置 dialog 的显示消失动画
     *
     * @param dialog    dialog
     * @param animStyle 动画样式
     */
    public void setDialogAnim(Dialog dialog, int animStyle) {
        if (dialog != null) {
            Window window = dialog.getWindow();
            window.setWindowAnimations(animStyle);
        }
    }

    /**
     * 设置 dialog 的宽高
     *
     * @param dialog
     * @param width
     * @param height
     */
    public void setDialogSize(Dialog dialog, int width, int height) {
        if (dialog != null) {
            dialog.getWindow().setLayout(width,height);
        }
    }

    /**
     * 设置 dialog 的宽高占屏比
     *
     * @param dialog
     * @param widthper  dialog 宽度的占屏比
     * @param heightper dialog 高度的占屏比
     */
    public void setDialogSizePercent(Dialog dialog, float widthper, float heightper) {
        if (dialog != null) {
            if (widthper < 0) {
                widthper = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
            if (heightper < 0) {
                heightper = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            setDialogSize(dialog,
                    widthper == ViewGroup.LayoutParams.WRAP_CONTENT ? ViewGroup.LayoutParams.WRAP_CONTENT : (int) (metrics.widthPixels * widthper),
                    heightper == ViewGroup.LayoutParams.WRAP_CONTENT ? ViewGroup.LayoutParams.WRAP_CONTENT : (int) (metrics.heightPixels * heightper));
        }
    }

    /**
     * 设置 dialog 的位置
     *
     * @param dialog
     * @param gravity
     */
    public void setDialogGravity(Dialog dialog, int gravity) {
        if (dialog != null) {
            Window window = dialog.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.gravity = gravity;
            window.setAttributes(lp);
        }
    }

    /**
     * 设置屏幕的背景透明度
     *
     * @param dialog
     * @param alpha  0-1（0: 屏幕完全透明，1: 背景最暗）
     */
    public void setVisibleAlpha(Dialog dialog, @FloatRange(from = 0.0, to = 1.0) float alpha) {
        if (dialog != null) {
            if (alpha < 0) {
                alpha = 0;
            }
            if (alpha > 1) {
                alpha = 1;
            }
            Window window = dialog.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount = alpha;
            lp.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(lp);
        }
    }
}
