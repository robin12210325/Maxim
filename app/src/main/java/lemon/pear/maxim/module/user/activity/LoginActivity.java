package lemon.pear.maxim.module.user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import lemon.pear.maxim.App;
import lemon.pear.maxim.Config;
import lemon.pear.maxim.R;
import lemon.pear.maxim.base.BasicActivity;
import lemon.pear.maxim.module.user.entity.MaximUser;
import lemon.pear.maxim.toolkit.KeyboardUtil;
import lemon.pear.maxim.toolkit.PreferenceUtil;
import lemon.pear.maxim.toolkit.StringUtil;
import lemon.pear.maxim.toolkit.ToastUtil;
import lemon.pear.maxim.widget.IconTextView;

/**
 * 登录页
 */

public class LoginActivity extends BasicActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.etPhone)
    EditText etPhone;
    @BindView(R.id.etSecret)
    EditText etSecret;
    @BindView(R.id.itSecret)
    IconTextView itSecret;
    @BindView(R.id.layRemember)
    LinearLayout layRemember;
    @BindView(R.id.itRemember)
    IconTextView itRemember;
    @BindView(R.id.tvRegister)
    TextView tvRegister;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindViews({R.id.layQQ, R.id.layWechat, R.id.layWeibo})
    LinearLayout[] layLogin;

    private boolean showCode = false;

    private boolean rememberUser = true;

    private String phone;

    private String secret;

    private Tencent mTencent;

    @Override
    protected int getLayoutId() {
        return R.layout.aty_login;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        mTencent = Tencent.createInstance("1105221867", this.getApplicationContext());
        initToolBar();
        initView();
    }

    private void initToolBar() {
        toolbar.setTitle(R.string.login);
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
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                phone = s.toString();
            }
        });
        etSecret.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                secret = s.toString();
            }
        });
        itSecret.setOnClickListener(this);
        layRemember.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        for (LinearLayout lay : layLogin) {
            lay.setOnClickListener(this);
        }
    }

    private void login() {
        if (StringUtil.isEmpty(phone)) {
            ToastUtil.showShortToast(activity, "请填写手机号码");
            etPhone.requestFocus();
            KeyboardUtil.showSoftInput(activity);
            return;
        }
        if (StringUtil.isEmpty(secret)) {
            ToastUtil.showShortToast(activity, "请填写密码");
            etSecret.requestFocus();
            KeyboardUtil.showSoftInput(activity);
            return;
        }
        validateUser();
    }

    private void validateUser() {
        BmobQuery<MaximUser> query0 = new BmobQuery<>();
        query0.addWhereEqualTo("phone", phone);
        BmobQuery<MaximUser> query1 = new BmobQuery<>();
        query1.addWhereEqualTo("secret", secret);
        //最后组装完整的and条件
        List<BmobQuery<MaximUser>> queries = new ArrayList<>();
        queries.add(query0);
        queries.add(query1);
        //查询符合整个and条件
        BmobQuery<MaximUser> query = new BmobQuery<>();
        query.and(queries);
        query.findObjects(new FindListener<MaximUser>() {
            @Override
            public void done(List<MaximUser> list, BmobException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        loginSuccess(list);
                    } else {
                        ToastUtil.showShortToast(activity, "账号或密码有误");
                    }
                } else {
                    Logger.d(e.getMessage());
                }
            }
        });
    }

    private void loginSuccess(List<MaximUser> list) {
        ToastUtil.showShortToast(activity, "登录成功");
        MaximUser user = list.get(0);
        saveUserLocal(user.getObjectId(), user.getName(),
                user.getFile() != null ? user.getFile().getFileUrl() : "");
        Intent intent = new Intent();
        intent.setAction(Config.LOGIN_ACTION);
        activity.sendBroadcast(intent);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                activity.finish();
            }
        }, 500);
    }

    private void saveUserLocal(String userId, String userName, String userPhoto) {
        App.getInstance().setUserId(userId);
        App.getInstance().setUserName(userName);
        App.getInstance().setUserPhoto(userPhoto);
        PreferenceUtil preferenceUtil = new PreferenceUtil(activity);
        if (rememberUser) {
            preferenceUtil.putBoolean("rememberUser", rememberUser);
            preferenceUtil.putString("userId", userId);
            preferenceUtil.putString("userName", userName);
            preferenceUtil.putString("userPhoto", userPhoto);
        } else {
            preferenceUtil.clear();
        }
    }

    private void qqLogin() {
        if (!mTencent.isSessionValid()) {
            mTencent.login(this, "get_user_info", new IUiListener() {
                @Override
                public void onComplete(Object o) {
                    Logger.d(o.toString());
                }

                @Override
                public void onError(UiError uiError) {
                    Logger.d(uiError.errorMessage);
                }

                @Override
                public void onCancel() {

                }
            });
        }
    }

    @Override
    protected void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.itSecret:
                showCode = !showCode;
                if (showCode) {
                    itSecret.setText(R.string.font_hide);
                    etSecret.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    etSecret.setSelection(etSecret.getText() != null ? etSecret.getText().length() : 0);
                } else {
                    itSecret.setText(R.string.font_show);
                    etSecret.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    etSecret.setSelection(etSecret.getText() != null ? etSecret.getText().length() : 0);
                }
                break;
            case R.id.layRemember:
                rememberUser = !rememberUser;
                if (rememberUser) {
                    itRemember.setText(R.string.font_checked);
                } else {
                    itRemember.setText(R.string.font_check);
                }
                break;
            case R.id.tvRegister:
                startActivity(new Intent(activity, RegisterActivity.class));
                break;
            case R.id.btnLogin:
                login();
                break;
            case R.id.layQQ:
                qqLogin();
                break;
            case R.id.layWechat:
                break;
            case R.id.layWeibo:
                break;
            default:
                break;
        }
    }

}
