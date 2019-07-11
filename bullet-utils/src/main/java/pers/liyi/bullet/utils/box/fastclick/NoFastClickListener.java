package pers.liyi.bullet.utils.box.fastclick;

import android.util.SparseArray;
import android.view.View;


public abstract class NoFastClickListener implements View.OnClickListener {
    private static final int DEF_MIN_INTERVAL_TIME = 1000;
    private SparseArray<Long> mLastClickViewArray = new SparseArray<>();

    @Override
    public void onClick(View v) {
        long currentTime = System.currentTimeMillis();
        long lastClickTime = mLastClickViewArray.get(v.getId(), -1L);
        if ((currentTime - lastClickTime) >= DEF_MIN_INTERVAL_TIME) {
            mLastClickViewArray.put(v.getId(), currentTime);
            onNoFastClick(v);
        }
    }

    public abstract void onNoFastClick(View v);
}
