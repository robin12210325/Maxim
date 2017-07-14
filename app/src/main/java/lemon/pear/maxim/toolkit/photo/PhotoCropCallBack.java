package lemon.pear.maxim.toolkit.photo;

import android.net.Uri;

/**
 * 图片剪裁回调
 */
public interface PhotoCropCallBack {
    void onFailed(String message);

    void onPhotoCropped(Uri uri);
}
