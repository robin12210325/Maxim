package lemon.pear.maxim.toolkit;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

/**
 * Description lemon.pear.maxim.tool
 * Created on 2017/5/31.
 */

public class ScreenUtil {

    private static DisplayMetrics getDisplayMetrics(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }

    /**
     * 获取屏幕密度
     */
    public static float getScreenDensity(Context context) {
        return getDisplayMetrics(context).density;
    }

    /**
     * 获取屏幕宽度PX
     */
    public static int getScreenWidthPx(Context context) {
        return getDisplayMetrics(context).widthPixels;
    }

    /**
     * 获取屏幕宽度DP
     */
    public static float getScreenWidthDp(Context context) {
        DisplayMetrics displayMetrics = getDisplayMetrics(context);
        return displayMetrics.widthPixels / displayMetrics.density;
    }

    /**
     * 获取屏幕高度PX
     */
    public static int getScreenHeightPx(Context context) {
        return getDisplayMetrics(context).heightPixels;
    }

    /**
     * 获取屏幕高度DP
     */
    public static float getScreenHeightDp(Context context) {
        DisplayMetrics displayMetrics = getDisplayMetrics(context);
        return displayMetrics.heightPixels / displayMetrics.density;
    }

    /**
     * 获得状态栏的高度PX
     */
    public static int getStatusHeight(Context context) {
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").
                    get(object).toString());
            statusHeight = context.getResources().getDimensionPixelOffset(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    public int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 获取当前屏幕截图，包含状态栏
     */
    public static Bitmap snapShotWithStatus(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        int width = getScreenWidthPx(activity);
        int height = getScreenHeightPx(activity);
        Bitmap bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
        view.destroyDrawingCache();
        return bp;
    }

    /**
     * 获取当前屏幕截图，不包含状态栏
     */
    public static Bitmap snapShotWithoutStatus(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        int width = getScreenWidthPx(activity);
        int height = getScreenHeightPx(activity);
        Bitmap bp = Bitmap.createBitmap(bmp, 0, statusBarHeight,
                width, height - statusBarHeight);
        view.destroyDrawingCache();
        return bp;
    }
}
