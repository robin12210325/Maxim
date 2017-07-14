package lemon.pear.maxim.module.user.activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.io.File;

import butterknife.BindView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import lemon.pear.maxim.App;
import lemon.pear.maxim.Config;
import lemon.pear.maxim.R;
import lemon.pear.maxim.base.BasicActivity;
import lemon.pear.maxim.module.user.entity.MaximUser;
import lemon.pear.maxim.toolkit.KeyboardUtil;
import lemon.pear.maxim.toolkit.PreferenceUtil;
import lemon.pear.maxim.toolkit.StringUtil;
import lemon.pear.maxim.toolkit.ToastUtil;
import lemon.pear.maxim.toolkit.photo.PhotoCropCallBack;
import lemon.pear.maxim.toolkit.photo.SysPhotoCropper;
import lemon.pear.maxim.widget.CircleImageView;
import lemon.pear.maxim.widget.IconTextView;

/**
 * 个人信息页
 */

public class PersonalActivity extends BasicActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.ivUserPhoto)
    CircleImageView ivUserPhoto;
    @BindView(R.id.etUserName)
    EditText etUserName;
    @BindView(R.id.layUserSex)
    RelativeLayout layUserSex;
    @BindView(R.id.txtUserSex)
    IconTextView txtUserSex;
    @BindView(R.id.etUserEmail)
    EditText etUserEmail;

    @BindView(R.id.btnSave)
    Button btnSave;
    @BindView(R.id.btnLogout)
    Button btnLogout;

    private SysPhotoCropper sysPhotoCropper;

    private String fileName;

    private String photoUrl;//头像url

    private String userName;//昵称

    private String userEmail;//邮箱

    private int userSex;//性别

    @Override
    protected int getLayoutId() {
        return R.layout.aty_personal;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        initToolBar();
        initView();
        loadData();
    }

    private void initToolBar() {
        toolbar.setTitle(R.string.personal);
        toolbar.setNavigationIcon(R.drawable.pic_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        ivUserPhoto.setOnClickListener(this);
        String name = App.getInstance().getUserName();
        userName = StringUtil.isEmpty(name) ? getString(R.string.user_empty) : name;
        etUserName.setText(userName);
        fileName = App.getInstance().getUserId() + ".jpg";
        sysPhotoCropper = new SysPhotoCropper(this, fileName, new PhotoCropCallBack() {
            @Override
            public void onFailed(String message) {
                Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPhotoCropped(final Uri uri) {
                ivUserPhoto.setImageURI(null);
                ivUserPhoto.setImageURI(uri);
                showUploadDialog(getRealFilePath(activity, uri));
            }
        });
        layUserSex.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        etUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                userName = s.toString();
            }
        });
        etUserEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                userEmail = s.toString();
            }
        });
    }

    private void loadData() {
        BmobQuery<MaximUser> query = new BmobQuery<>();
        query.getObject(App.getInstance().getUserId(), new QueryListener<MaximUser>() {

            @Override
            public void done(MaximUser data, BmobException e) {
                if (e == null) {
                    refreshView(data);
                } else {
                    Logger.d(e.getMessage());
                }
            }
        });
    }

    private void refreshView(MaximUser data) {
        photoUrl = data.getFile().getUrl();
        userSex = data.getSex();
        txtUserSex.setText(userSex == 0 ? R.string.font_female : R.string.font_male);
        if (!StringUtil.isEmpty(photoUrl)) {
            File photoFile = new File(activity.getExternalCacheDir(), fileName);
            if (photoFile.exists()) {//文件存在
                ivUserPhoto.setImageURI(null);
                ivUserPhoto.setImageURI(Uri.fromFile(photoFile));
            } else {
                downloadFile(data.getFile());
            }
        }
        etUserEmail.setText(userEmail = data.getEmail());
    }

    /**
     * 选择性别Dialog
     */
    private void showSexDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String[] items = new String[]{"女", "男"};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                userSex = which;
                txtUserSex.setText(which == 0 ? R.string.font_female : R.string.font_male);
            }
        });
        builder.show();
    }

    /**
     * 更新用户信息
     */
    private void updateUser() {
        if (StringUtil.isEmpty(userName)) {
            ToastUtil.showShortToast(activity, "请填写昵称");
            etUserName.requestFocus();
            KeyboardUtil.showSoftInput(activity);
            return;
        }
        final MaximUser user = new MaximUser();
        user.setObjectId(App.getInstance().getUserId());
        user.setName(userName);
        user.setSex(userSex);
        user.setEmail(userEmail);
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    ToastUtil.showShortToast(activity, "保存成功");
                    PreferenceUtil preferenceUtil = new PreferenceUtil(activity);
                    preferenceUtil.putString("userName", userName);
                    App.getInstance().setUserName(userName);
                    Intent intent = new Intent();
                    intent.setAction(Config.PERSON_MODIFY);
                    activity.sendBroadcast(intent);
                } else {
                    Logger.d(e.getMessage());
                }
            }
        });
    }

    /**
     * 下载文件
     */
    private void downloadFile(BmobFile file) {
        File saveFile = new File(activity.getExternalCacheDir(), file.getFilename());
        file.download(saveFile, new DownloadFileListener() {

            @Override
            public void onStart() {
            }

            @Override
            public void done(String savePath, BmobException e) {
                if (e == null) {
                    File photoFile = new File(savePath);
                    ivUserPhoto.setImageURI(Uri.fromFile(photoFile));
                    Logger.d("新文件下载成功");
                } else {
                    Logger.d(e.getMessage());
                }
            }

            @Override
            public void onProgress(Integer value, long newworkSpeed) {
            }
        });
    }

    /**
     * 选择图片获取Dialog
     */
    private void showPhotoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String[] items = new String[]{"拍照", "相册"};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteOldPhoto();//删除旧文件
                switch (which) {
                    case 0:
                        sysPhotoCropper.cropForCamera();
                        break;
                    case 1:
                        sysPhotoCropper.cropForGallery();
                        break;
                }
            }
        });
        builder.show();
    }

    /**
     * 删除旧的文件
     */
    private void deleteOldPhoto() {
        BmobFile file = new BmobFile();
        if (!StringUtil.isEmpty(photoUrl)) {
            file.setUrl(photoUrl);
            file.delete(new UpdateListener() {

                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        File photoFile = new File(activity.getExternalCacheDir(), fileName);
                        if (photoFile.exists()) {//文件存在
                            photoFile.delete();
                        }
                        Logger.d("删除旧的成功");
                    } else {
                        Logger.d(e.getMessage());
                    }
                }
            });
        }
    }

    /**
     * 显示上传Dialog
     */
    private void showUploadDialog(final String path) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("正在上传中,请稍后...");/*设置进度对话框的内容*/
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);/*设置进度对话框的样式*/
        dialog.setMax(100);/*设置进度对话框的最大进度*/
        dialog.setCancelable(false);
        dialog.show();/*在show时, 别忘了初始化进度*/
        final BmobFile bmobFile = new BmobFile(new File(path));
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                MaximUser user = new MaximUser();
                user.setFile(bmobFile);
                user.update(App.getInstance().getUserId(), new UpdateListener() {

                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            ToastUtil.showShortToast(activity, "上传成功");
                            Intent intent = new Intent();
                            intent.setAction(Config.PERSON_MODIFY);
                            activity.sendBroadcast(intent);
                        } else {
                            Logger.d(e.getMessage());
                        }
                    }
                });
                dialog.dismiss();
            }

            @Override
            public void onProgress(Integer value) {
                super.onProgress(value);
                dialog.setProgress(value);
            }
        });
    }

    /**
     * 根据URI获取图片路径
     */
    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri,
                    new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * 退出登录
     */
    private void logout() {
        App.getInstance().setUserId("");
        App.getInstance().setUserName("");
        App.getInstance().setUserPhoto("");
        Intent intent = new Intent();
        intent.setAction(Config.LOGOUT_ACTION);
        sendBroadcast(intent);
        finish();
    }

    @Override
    protected void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.ivUserPhoto:
                showPhotoDialog();
                break;
            case R.id.layUserSex:
                showSexDialog();
                break;
            case R.id.btnSave:
                updateUser();
                break;
            case R.id.btnLogout:
                logout();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        sysPhotoCropper.handlerOnActivityResult(requestCode, resultCode, data);
    }
}
