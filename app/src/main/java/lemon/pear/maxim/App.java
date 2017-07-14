package lemon.pear.maxim;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;
import cn.jpush.android.api.JPushInterface;
import lemon.pear.maxim.service.InitService;
import lemon.pear.maxim.toolkit.PreferenceUtil;
import lemon.pear.maxim.toolkit.StringUtil;

/**
 * 自定义Application
 */

public class App extends Application {

    private String userId;

    private String userName;

    private String userPhoto;

    private static App instance;

    public App() {
        instance = this;
    }

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        InitService.actionInit(this);
        JPushInterface.init(this);
        initCloudData();
        recoverUser();
    }

    /**
     * 恢复用户信息
     */
    private void recoverUser() {
        PreferenceUtil preferenceUtil = new PreferenceUtil(this);
        boolean rememberUser = preferenceUtil.getBoolean("rememberUser", false);
        if (rememberUser) {
            userId = preferenceUtil.getString("userId");
            userName = preferenceUtil.getString("userName");
            userPhoto = preferenceUtil.getString("userPhoto");
        }
    }

    /**
     * 移动后端服务
     */
    private void initCloudData() {
        BmobConfig config = new BmobConfig.Builder(this)
                .setApplicationId("d80f10d37bcc5d57504286d753a0cea4")
                .setConnectTimeout(30)
                .setUploadBlockSize(1024 * 1024)
                .setFileExpiration(2500)
                .build();
        Bmob.initialize(config);
    }

    public boolean isLogin() {
        return !StringUtil.isEmpty(userId);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    /**
     * 获取版本号
     */
    public int getVersionCode() {
        int version = 0;
        try {
            PackageManager manager = getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            version = info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }
}
