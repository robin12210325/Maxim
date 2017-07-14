package lemon.pear.maxim.module.user.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import lemon.pear.maxim.Config;
import lemon.pear.maxim.R;
import lemon.pear.maxim.base.BasicActivity;
import lemon.pear.maxim.module.user.entity.MaximUser;
import lemon.pear.maxim.toolkit.KeyboardUtil;
import lemon.pear.maxim.toolkit.StringUtil;
import lemon.pear.maxim.toolkit.ToastUtil;

/**
 * 注册页
 */

public class RegisterActivity extends BasicActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.etPhone)
    EditText etPhone;
    @BindView(R.id.tvValidate)
    TextView tvValidate;
    @BindView(R.id.etValidate)
    EditText etValidate;
    @BindView(R.id.etSecret)
    EditText etSecret;
    @BindView(R.id.btnRegister)
    Button btnRegister;

    private String phone;//手机号

    private String validate;//验证码

    private String secret;//密码

    private int intervalTime = 60;//验证间隔时间

    private Timer timer = new Timer();

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                tvValidate.setText(intervalTime + "s重试");
                if (intervalTime == 0) {
                    timer.cancel();
                    tvValidate.setEnabled(true);
                    tvValidate.setClickable(true);
                    tvValidate.setText(R.string.validate);
                }
            } else if (msg.what == 2) {
                ToastUtil.showShortToast(activity, (String) msg.obj);
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.aty_register;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        initToolBar();
        initView();
        registerCallback();
    }

    private void initToolBar() {
        toolbar.setTitle(R.string.register);
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
        etValidate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                validate = s.toString();
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
        tvValidate.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    private void registerCallback() {
        EventHandler eh = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {//回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        Logger.d(data.toString());
                        createNewUser();//验证成功,添加新用户
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        Logger.d(data.toString());
                    }
                } else if (result == SMSSDK.RESULT_ERROR) {
                    Logger.d(data.toString());
                    Message msg = myHandler.obtainMessage();
                    msg.what = 2;
                    msg.obj = data.toString();
                    myHandler.sendMessage(msg);
                }
            }
        };
        SMSSDK.registerEventHandler(eh); //注册短信回调
    }

    private void createNewUser() {
        BmobQuery<MaximUser> query = new BmobQuery<>();
        query.addWhereEqualTo("phone", phone);
        query.findObjects(new FindListener<MaximUser>() {
            @Override
            public void done(List<MaximUser> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        ToastUtil.showShortToast(activity, R.string.registered);
                        activity.finish();
                    } else {
                        addNewUser();
                    }
                } else {
                    Logger.d(e.getMessage());
                }
            }
        });
    }

    private void addNewUser() {
        MaximUser user = new MaximUser();
        user.setPhone(phone);
        user.setSecret(secret);
        user.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if (e == null) {
                    ToastUtil.showShortToast(activity, R.string.register_success);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            activity.finish();
                        }
                    }, 500);
                } else {
                    Logger.d(e.getMessage());
                }
            }
        });
    }

    /**
     * 获取手机验证码
     */
    private void getValidate() {
        if (StringUtil.isEmpty(phone)) {
            ToastUtil.showShortToast(activity, "请填写手机号码");
            etPhone.requestFocus();
            KeyboardUtil.showSoftInput(activity);
            return;
        }
        validateTimer();//计时器
        SMSSDK.getVerificationCode(Config.CHINA_CODE, phone);
    }

    private void register() {
        if (StringUtil.isEmpty(phone)) {
            ToastUtil.showShortToast(activity, "请填写手机号码");
            etPhone.requestFocus();
            KeyboardUtil.showSoftInput(activity);
            return;
        }
        if (StringUtil.isEmpty(validate)) {
            ToastUtil.showShortToast(activity, "请填写验证码");
            etValidate.requestFocus();
            KeyboardUtil.showSoftInput(activity);
            return;
        }
        if (StringUtil.isEmpty(secret)) {
            ToastUtil.showShortToast(activity, "请填写密码");
            etSecret.requestFocus();
            KeyboardUtil.showSoftInput(activity);
            return;
        }
        SMSSDK.submitVerificationCode(Config.CHINA_CODE, phone, validate);
    }

    /**
     * 计时器
     */
    private void validateTimer() {
        tvValidate.setClickable(false);
        tvValidate.setEnabled(false);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                intervalTime--;
                myHandler.sendMessage(myHandler.obtainMessage(1));
            }
        };
        timer.schedule(task, 1000, 1000/* 表示1秒之後，每隔1秒執行一次 */);
    }

    @Override
    protected void widgetClick(View v) {
        KeyboardUtil.hideSoftInput(activity);
        switch (v.getId()) {
            case R.id.btnRegister:
                register();
                break;
            case R.id.tvValidate:
                getValidate();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }
}
