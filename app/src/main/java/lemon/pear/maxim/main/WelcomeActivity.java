package lemon.pear.maxim.main;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import lemon.pear.maxim.App;
import lemon.pear.maxim.Config;
import lemon.pear.maxim.R;
import lemon.pear.maxim.entity.AppVersion;
import lemon.pear.maxim.toolkit.ToastUtil;

/**
 * 欢迎页
 */

public class WelcomeActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1://更新进度
                    int progress = msg.arg1;
                    if (progressDialog != null) {
                        progressDialog.setProgress(progress);
                    }
                    if (progress == 100) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        installApk();//安装apk文件
                    }
                    break;
                case 2://下载异常
                    progressDialog.dismiss();
                    ToastUtil.showShortToast(WelcomeActivity.this, R.string.update_down_error);
                    skipNextPage();
                default:
                    break;
            }
        }
    };

    private void setFullScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setFullScreen();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_welcome);
        checkUpdate();
    }

    /**
     * 跳转下一页
     */
    private void skipNextPage() {
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        WelcomeActivity.this.finish();
    }

    /**
     * 检查更新
     */
    private void checkUpdate() {
        final int versionCode = App.getInstance().getVersionCode();
        BmobQuery<AppVersion> query = new BmobQuery<>();
        query.order("-code");
        query.findObjects(new FindListener<AppVersion>() {
            @Override
            public void done(List<AppVersion> object, BmobException e) {
                if (e == null) {
                    if (object != null && object.size() > 0) {
                        AppVersion version = object.get(0);
                        if (version.getCode() > versionCode) {//有新版本
                            if (version.isForce()) {
                                downloadApk(version.getUrl());
                            } else {
                                showUpdateDialog(version.getUrl(), version.getContent());
                            }
                        } else {//无新版本
                            skipNextPage();
                        }
                    }
                } else {//检查更新失败
                    ToastUtil.showShortToast(WelcomeActivity.this, R.string.update_error);
                    skipNextPage();
                }
            }
        });
    }

    /**
     * 更新提示Dialog
     */
    private void showUpdateDialog(final String urlStr, final String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.update_new);
        builder.setMessage(getString(R.string.update_content) + content);
        builder.setPositiveButton(R.string.update_now, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadApk(urlStr);
            }
        });
        builder.setNegativeButton(R.string.update_later, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                skipNextPage();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    /**
     * 下载文件Dialog
     */
    private void downloadApk(final String url) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.update_downing));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.show();
        downFile(url);
    }

    /**
     * 下载文件
     */
    private void downFile(final String urlStr) {
        new Thread() {
            public void run() {
                OutputStream output = null;
                try {
                    URL url = new URL(urlStr);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    int length = conn.getContentLength();
                    // 取得inputStream，并将流中的信息写入
                    InputStream input = conn.getInputStream();
                    if (input != null) {
                        File file = new File(
                                Environment.getExternalStorageDirectory(),
                                Config.packageName);
                        output = new FileOutputStream(file);
                        int count = 0;
                        int read;
                        byte[] buffer = new byte[512];
                        while ((read = input.read(buffer)) != -1) {
                            output.write(buffer, 0, read);
                            count += read;
                            Message msg = mHandler.obtainMessage();
                            int result = count * 100 / length;
                            msg.what = 1;
                            msg.arg1 = result;
                            Logger.d(result);
                            mHandler.sendMessage(msg);
                        }
                        output.flush();
                    }
                    output.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg = mHandler.obtainMessage();
                    msg.what = 2;
                    mHandler.sendMessage(msg);
                }
            }
        }.start();
    }

    /**
     * 安装apk文件
     */
    private void installApk() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(Environment
                        .getExternalStorageDirectory(), Config.packageName)),
                "application/vnd.android.package-archive");
        startActivity(intent);
        finish();
    }
}
