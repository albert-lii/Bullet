package pers.liyi.bullet.utils.box.fastclick;


public class FastClickUtils {
    // 两次点击之间最少间隔时间
    private static final long DEF_MIN_INTERVAL_TIME = 1000;
    // 上次点击的时间
    private static long sLastClickTime = 0;
    // 上次点击的控件的 id
    private static int sLastClickViewId = -1;

    /**
     * 判断两次点击之间的间隔是否太短
     */
    public static boolean isFastClick() {
        return isFastClick(-1, DEF_MIN_INTERVAL_TIME);
    }

    /**
     * 判断两次点击之间的间隔是否太短
     */
    public static boolean isFastClick(int clickViewId) {
        return isFastClick(clickViewId, DEF_MIN_INTERVAL_TIME);
    }

    /**
     * 判断两次点击之间的间隔是否太短
     */
    public static boolean isFastClick(int clickViewId, long interval) {
        boolean isFastClick = false;
        long currentClickTime = System.currentTimeMillis();
        if (sLastClickViewId == clickViewId && (currentClickTime - sLastClickTime) < interval) {
            isFastClick = true;
        }
        sLastClickTime = currentClickTime;
        sLastClickViewId = clickViewId;
        return isFastClick;
    }
}
