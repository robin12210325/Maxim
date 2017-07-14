package lemon.pear.maxim.toolkit.photo;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;

/**
 * 利用系统剪裁的图片剪裁
 */
public class SysPhotoCropper {

    private Activity mActivity;
    /**
     * 接口回调
     */
    private PhotoCropCallBack mPhotoCropCallBack;
    /**
     * 默认大小 PX单位
     */
    private int mDefaultSize = 640;
    /**
     * 请求相机
     */
    private static final int REQUEST_TYPE_CAMERA = 311;
    /**
     * 请求图库
     */
    private static final int REQUEST_TYPE_GALLERY = 312;
    /**
     * 剪裁
     */
    private static final int REQUEST_TYPE_CROP = 313;

    /**
     * 临时照片文件名称
     */
    private Uri mOutPutUri;

    public SysPhotoCropper(Activity activity, String fileName, PhotoCropCallBack cropCallBack) {
        this.mActivity = activity;
        this.mPhotoCropCallBack = cropCallBack;
        File tmpFile = new File(mActivity.getExternalCacheDir(), fileName);
        mOutPutUri = Uri.fromFile(tmpFile);
    }

    /**
     * 设置剪裁大小
     *
     * @param cropSize
     */
    public void setCropSize(int cropSize) {
        this.mDefaultSize = cropSize;
    }

    public void setOutPutUri(Uri uri) {
        this.mOutPutUri = uri;
    }


    /**
     * 相机拍照剪裁
     */
    public void cropForCamera() {
        if (!Environment.getExternalStorageState().equalsIgnoreCase(
                Environment.MEDIA_MOUNTED)) {
            mPhotoCropCallBack.onFailed("sdcard not found");
            return;
        }
        final Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mOutPutUri);
        mActivity.startActivityForResult(cameraIntent, REQUEST_TYPE_CAMERA);
    }

    /**
     * 图库选择剪裁
     */
    public void cropForGallery() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            mActivity.startActivityForResult(intent, REQUEST_TYPE_GALLERY);
        } catch (ActivityNotFoundException e) {
            mPhotoCropCallBack.onFailed("Gallery not found");
        }
    }

    private Intent getCropImageIntent(Uri photoUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", mDefaultSize);
        intent.putExtra("outputY", mDefaultSize);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mOutPutUri);
        return intent;
    }

    /**
     * 进行剪裁
     *
     * @param uri 输入
     */
    private void doCropPhoto(Uri uri) {
        try {
            final Intent intent = getCropImageIntent(uri);
            mActivity.startActivityForResult(intent, REQUEST_TYPE_CROP);
        } catch (Exception e) {
            mPhotoCropCallBack.onFailed("cannot crop image");
        }
    }

    /**
     * 处理OnActivityResult
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void handlerOnActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_TYPE_CAMERA:
                doCropPhoto(mOutPutUri);
                break;
            case REQUEST_TYPE_GALLERY:
                Uri imageUri = data.getData();
                doCropPhoto(imageUri);
                break;
            case REQUEST_TYPE_CROP:
                mPhotoCropCallBack.onPhotoCropped(mOutPutUri);
                break;
        }
    }

}
