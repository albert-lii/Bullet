package pers.liyi.bullet.utils.box;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.app.NotificationManagerCompat;


public class ToastUtils {
    private static final int INVALID_VAL = -1;
    private static final int DEFAULT_COLOR = 0xFEFFFFFF;
    private static final String NULL = "null";
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());


    private static IToast sToast;
    // taost 的位置
    private static int sGravity = INVALID_VAL;
    // toast 在 x 轴上的位移
    private static int sXOffset = INVALID_VAL;
    // toast 在 y 轴上的位移
    private static int sYOffset = INVALID_VAL;
    // toast 的背景色
    private static int sBgColor = DEFAULT_COLOR;
    // toast 的背景
    private static int sBgResource = INVALID_VAL;
    // taost 的文字颜色
    private static int sTextColor = DEFAULT_COLOR;
    // toast 的字体大小
    private static int sTextSize = INVALID_VAL;

    /**
     * 设置 toast 的显示位置
     *
     * @param gravity
     * @param xOffset
     * @param yOffset
     */
    public static void setGravity(int gravity, int xOffset, int yOffset) {
        sGravity = gravity;
        sXOffset = xOffset;
        sYOffset = yOffset;
    }

    /**
     * 设置 toast 的背景色
     *
     * @param backgroundColor
     */
    public static void setBgColor(@ColorInt int backgroundColor) {
        sBgColor = backgroundColor;
    }

    /**
     * 设置 toast 的背景
     *
     * @param bgResource
     */
    public static void setBgResource(@DrawableRes int bgResource) {
        sBgResource = bgResource;
    }

    /**
     * 设置 toast 的文字颜色
     *
     * @param textColor
     */
    public static void setTextColor(@ColorInt int textColor) {
        sTextColor = textColor;
    }

    /**
     * 设置 toast 的文字大小
     *
     * @param textSize
     */
    public static void setTextSize(int textSize) {
        sTextSize = textSize;
    }

    /**
     * 短时间显示 toast
     *
     * @param context
     * @param text
     */
    public static void show(@NonNull Context context, CharSequence text) {
        show(context, text == null ? NULL : text, Toast.LENGTH_SHORT);
    }

    /**
     * 短时间显示 toast
     *
     * @param context
     * @param resId
     */
    public static void show(@NonNull Context context, @StringRes int resId) {
        show(context, resId, Toast.LENGTH_SHORT);
    }

    /**
     * 显示 toast
     *
     * @param context
     * @param resId
     * @param duration
     */
    public static void show(@NonNull Context context, @StringRes int resId, int duration) {
        show(context, context.getResources().getText(resId), duration);
    }

    /**
     * 短时间显示 toast
     *
     * @param context
     * @param format
     * @param args
     */
    public static void show(@NonNull Context context, @Nullable String format, Object... args) {
        show(context, format, Toast.LENGTH_SHORT, args);
    }

    /**
     * 显示 toast
     *
     * @param context
     * @param format
     * @param duration
     * @param args
     */
    public static void show(@NonNull Context context, String format, int duration, Object... args) {
        String text;
        if (format == null) {
            text = NULL;
        } else {
            text = String.format(format, args);
            if (text == null) {
                text = NULL;
            }
        }
        show(context, text, duration);
    }

    /**
     * 短时间显示 toast
     *
     * @param context
     * @param resId
     * @param args
     */
    public static void show(@NonNull Context context, @StringRes int resId, Object... args) {
        show(context, resId, Toast.LENGTH_SHORT, args);
    }

    /**
     * 显示 toast
     *
     * @param context
     * @param resId
     * @param duration
     * @param args
     */
    public static void show(@NonNull Context context, @StringRes int resId, int duration, Object... args) {
        show(context, String.format(context.getResources().getString(resId), args), duration);
    }

    /**
     * 短时间显示自定义 toast
     *
     * @param context
     * @param layoutId
     * @return
     */
    public static View showCustom(@NonNull Context context, @LayoutRes int layoutId) {
        return showCustom(context, layoutId, Toast.LENGTH_SHORT);
    }

    /**
     * 显示自定义 toast
     *
     * @param context
     * @param layoutId
     * @param duration
     * @return
     */
    public static View showCustom(@NonNull Context context, @LayoutRes int layoutId, int duration) {
        final View view = getView(context, layoutId);
        show(context, view, duration);
        return view;
    }

    /**
     * 短时间显示自定义 toast
     *
     * @param context
     * @param view
     * @return toast 的 view 视图
     */
    public static View showCustom(@NonNull Context context, View view) {
        return showCustom(context, view, Toast.LENGTH_SHORT);
    }

    /**
     * 显示自定义 toast
     *
     * @param context
     * @param view
     * @param duration
     * @return toast 的 view 视图
     */
    public static View showCustom(@NonNull Context context, View view, int duration) {
        show(context, view, duration);
        return view;
    }

    /**
     * 取消 toast 显示
     */
    public static void cancel() {
        if (sToast != null) {
            sToast.cancel();
        }
    }

    /**
     * 显示 toast
     *
     * @param context
     * @param text
     * @param duration
     */
    public static void show(@NonNull final Context context, final CharSequence text, final int duration) {
        runOnUiThread(new Runnable() {
            @SuppressLint("ShowToast")
            @Override
            public void run() {
                cancel();
                final CharSequence msg = (text == null ? NULL : text);
                sToast = ToastFactory.makeToast(context, msg, duration);
                final TextView tvMessage = (TextView) sToast.getView().findViewById(android.R.id.message);
                if (sTextColor != INVALID_VAL) {
                    tvMessage.setTextColor(sTextColor);
                }
                if (sTextSize != INVALID_VAL) {
                    tvMessage.setTextSize(sTextSize);
                }
                if (sGravity != INVALID_VAL && sXOffset != INVALID_VAL && sYOffset != INVALID_VAL) {
                    sToast.setGravity(sGravity, sXOffset, sYOffset);
                }
                setToastBgWhenDefault(tvMessage);
                sToast.show();
            }
        });
    }

    /**
     * 显示 toast
     *
     * @param context
     * @param view     toast 的 view
     * @param duration
     */
    private static void show(@NonNull final Context context, final View view, final int duration) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cancel();
                sToast = ToastFactory.newToast(context);
                sToast.setView(view);
                sToast.setDuration(duration);
                if (sGravity != INVALID_VAL && sXOffset != INVALID_VAL && sYOffset != INVALID_VAL) {
                    sToast.setGravity(sGravity, sXOffset, sYOffset);
                }
                setToastBg();
                sToast.show();
            }
        });
    }

    private static void runOnUiThread(Runnable runnable){
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            HANDLER.post(runnable);
        }
    }

    /**
     * 设置 toast 的背景
     * <p>仅当 toast 使用的是默认布局时才能使用此方法</p>
     *
     * @param tvMessage 在 toast 的默认布局中的 textview
     */
    private static void setToastBgWhenDefault(TextView tvMessage) {
        if (sToast == null) return;
        View toastView = sToast.getView();
        if (sBgResource != INVALID_VAL) {
            toastView.setBackgroundResource(sBgResource);
            tvMessage.setBackgroundColor(Color.TRANSPARENT);
        } else if (sBgColor != DEFAULT_COLOR) {
            Drawable tvBg = toastView.getBackground();
            Drawable messageBg = tvMessage.getBackground();
            if (tvBg != null && messageBg != null) {
                tvBg.setColorFilter(new PorterDuffColorFilter(sBgColor, PorterDuff.Mode.SRC_IN));
                tvMessage.setBackgroundColor(Color.TRANSPARENT);
            } else if (tvBg != null) {
                tvBg.setColorFilter(new PorterDuffColorFilter(sBgColor, PorterDuff.Mode.SRC_IN));
            } else if (messageBg != null) {
                messageBg.setColorFilter(new PorterDuffColorFilter(sBgColor, PorterDuff.Mode.SRC_IN));
            } else {
                toastView.setBackgroundColor(sBgColor);
            }
        }
    }

    /**
     * Set the background of the toast
     * <p>Execute this method only when toast uses the custom layout </p>
     */
    private static void setToastBg() {
        if (sToast == null) return;
        View toastView = sToast.getView();
        if (sBgResource != INVALID_VAL) {
            toastView.setBackgroundResource(sBgResource);
        } else if (sBgColor != DEFAULT_COLOR) {
            Drawable background = toastView.getBackground();
            if (background != null) {
                background.setColorFilter(new PorterDuffColorFilter(sBgColor, PorterDuff.Mode.SRC_IN));
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    toastView.setBackground(new ColorDrawable(sBgColor));
                } else {
                    toastView.setBackgroundDrawable(new ColorDrawable(sBgColor));
                }
            }
        }
    }

    /**
     * 获取 toast 的 view
     *
     * @param context
     * @param layoutId
     * @return toast 的 view 视图
     */
    private static View getView(@NonNull Context context, @LayoutRes int layoutId) {
        LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflate != null ? inflate.inflate(layoutId, null) : null;
    }

    public static class ToastFactory {

        static IToast makeToast(Context context, CharSequence text, int duration) {
            if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                return new SystemToast(makeNormalToast(context, text, duration));
            }
            return new ToastWithoutNotification(context, makeNormalToast(context, text, duration));
        }

        static IToast newToast(Context context) {
            if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                return new SystemToast(new Toast(context));
            }
            return new ToastWithoutNotification(context, new Toast(context));
        }

        private static Toast makeNormalToast(Context context, CharSequence text, int duration) {
            @SuppressLint("ShowToast")
            Toast toast = Toast.makeText(context, "", duration);
            toast.setText(text);
            return toast;
        }
    }

    public static class SystemToast extends AbsToast {

        SystemToast(Toast toast) {
            super(toast);
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
                try {
                    // noinspection JavaReflectionMemberAccess
                    Field mTNField = Toast.class.getDeclaredField("mTN");
                    mTNField.setAccessible(true);
                    Object mTN = mTNField.get(toast);
                    Field mTNmHandlerField = mTNField.getType().getDeclaredField("mHandler");
                    mTNmHandlerField.setAccessible(true);
                    Handler tnHandler = (Handler) mTNmHandlerField.get(mTN);
                    mTNmHandlerField.set(mTN, new SafeHandler(tnHandler));
                } catch (Exception ignored) {/**/}
            }
        }

        @Override
        public void show() {
            mToast.show();
        }

        @Override
        public void cancel() {
            mToast.cancel();
        }

        @Override
        public void setView(View view) {
            mToast.setView(view);
        }

        @Override
        public View getView() {
            return mToast.getView();
        }

        @Override
        public void setDuration(int duration) {
            mToast.setDuration(duration);
        }

        @Override
        public void setGravity(int gravity, int xOffset, int yOffset) {
            mToast.setGravity(gravity, xOffset, yOffset);
        }

        @Override
        public void setText(int resId) {
            mToast.setText(resId);
        }

        @Override
        public void setText(CharSequence s) {
            mToast.setText(s);
        }

        static class SafeHandler extends Handler {
            private Handler impl;

            SafeHandler(Handler impl) {
                this.impl = impl;
            }

            @Override
            public void handleMessage(Message msg) {
                impl.handleMessage(msg);
            }

            @Override
            public void dispatchMessage(Message msg) {
                try {
                    impl.dispatchMessage(msg);
                } catch (Exception e) {
                    LogUtils.e("ToastUtils", e.toString());
                }
            }
        }
    }

    static class ToastWithoutNotification extends AbsToast {
        private WindowManager mWM;
        private View mView;
        private WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
        private Context mContext;

        ToastWithoutNotification(Context context, Toast toast) {
            super(toast);
            mContext = context;
        }

        @Override
        public void show() {
            mView = mToast.getView();
            if (mView == null) return;
            Context context = mToast.getView().getContext();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) {
                mWM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                mParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            } else {
                Context topActivityOrApp = mContext;
                if (topActivityOrApp instanceof Activity) {
                    mWM = ((Activity) topActivityOrApp).getWindowManager();
                }
                mParams.type = WindowManager.LayoutParams.LAST_APPLICATION_WINDOW;
            }

            final Configuration config = context.getResources().getConfiguration();
            final int gravity;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                gravity = Gravity.getAbsoluteGravity(mToast.getGravity(), config.getLayoutDirection());
            } else {
                gravity = mToast.getGravity();
            }

            mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            mParams.format = PixelFormat.TRANSLUCENT;
            mParams.windowAnimations = android.R.style.Animation_Toast;
            mParams.setTitle("ToastWithoutNotification");
            mParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
            mParams.packageName = context.getPackageName();

            mParams.gravity = gravity;
            if ((gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.FILL_HORIZONTAL) {
                mParams.horizontalWeight = 1.0f;
            }
            if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.FILL_VERTICAL) {
                mParams.verticalWeight = 1.0f;
            }
            mParams.x = mToast.getXOffset();
            mParams.y = mToast.getYOffset();
            mParams.horizontalMargin = mToast.getHorizontalMargin();
            mParams.verticalMargin = mToast.getVerticalMargin();

            try {
                mWM.addView(mView, mParams);
            } catch (Exception ignored) { /**/ }

            HANDLER.postDelayed(new Runnable() {
                @Override
                public void run() {
                    cancel();
                }
            }, mToast.getDuration() == Toast.LENGTH_SHORT ? 2000 : 3500);
        }

        @Override
        public void cancel() {
            try {
                if (mWM != null) {
                    mWM.removeViewImmediate(mView);
                }
            } catch (Exception ignored) { /**/ }
            mView = null;
            mWM = null;
            mToast = null;
        }
    }

    static abstract class AbsToast implements IToast {

        Toast mToast;

        AbsToast(Toast toast) {
            mToast = toast;
        }

        @Override
        public void setView(View view) {
            mToast.setView(view);
        }

        @Override
        public View getView() {
            return mToast.getView();
        }

        @Override
        public void setDuration(int duration) {
            mToast.setDuration(duration);
        }

        @Override
        public void setGravity(int gravity, int xOffset, int yOffset) {
            mToast.setGravity(gravity, xOffset, yOffset);
        }

        @Override
        public void setText(int resId) {
            mToast.setText(resId);
        }

        @Override
        public void setText(CharSequence s) {
            mToast.setText(s);
        }
    }

    interface IToast {
        void show();

        void cancel();

        void setView(View view);

        View getView();

        void setDuration(int duration);

        void setGravity(int gravity, int xOffset, int yOffset);

        void setText(@StringRes int resId);

        void setText(CharSequence s);
    }
}

