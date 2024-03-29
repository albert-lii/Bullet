package pers.liyi.bullet.arms.base.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Dialog 的基类
 */
public class BaseDialog extends Dialog {

    public BaseDialog(@NonNull Context context) {
        super(context);
    }

    public BaseDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected BaseDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    /**
     * 设置 dialog 的显示消失动画
     *
     * @param animStyle 动画样式
     */
    public void setDialogAnim(int animStyle) {
        Window window = getWindow();
        window.setWindowAnimations(animStyle);
    }

    /**
     * 设置 dialog 的宽高
     *
     * @param width
     * @param height
     */
    public void setDialogSize(int width, int height) {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = width;
        lp.height = height;
        window.setAttributes(lp);
    }

    /**
     * 设置 dialog 的宽高占屏比
     *
     * @param widthper  dialog 宽度的占屏比
     * @param heightper dialog 高度的占屏比
     */
    public void setDialogSizePercent(float widthper, float heightper) {
        if (widthper < 0) {
            widthper = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        if (heightper < 0) {
            heightper = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        DisplayMetrics metrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        setDialogSize(
                widthper == ViewGroup.LayoutParams.WRAP_CONTENT ? ViewGroup.LayoutParams.WRAP_CONTENT : (int) (metrics.widthPixels * widthper),
                heightper == ViewGroup.LayoutParams.WRAP_CONTENT ? ViewGroup.LayoutParams.WRAP_CONTENT : (int) (metrics.heightPixels * heightper));
    }

    /**
     * 设置 dialog 的位置
     *
     * @param gravity
     */
    public void setDialogGravity(int gravity) {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = gravity;
        window.setAttributes(lp);
    }

    /**
     * 设置屏幕的背景透明度
     *
     * @param bgAlpha 0-1（0：屏幕完全透明，1：背景最暗）
     */
    public void setVisibleAlpha(@FloatRange(from = 0.0, to = 1.0) float bgAlpha) {
        if (bgAlpha < 0) {
            bgAlpha = 0;
        }
        if (bgAlpha > 1) {
            bgAlpha = 1;
        }
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.dimAmount = bgAlpha;
        lp.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(lp);
    }
}
