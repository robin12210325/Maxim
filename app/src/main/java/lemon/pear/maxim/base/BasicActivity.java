package lemon.pear.maxim.base;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.ButterKnife;
import lemon.pear.maxim.Config;
import lemon.pear.maxim.R;
import lemon.pear.maxim.toolkit.PreferenceUtil;

/**
 * 所有Activity的基类
 */

public abstract class BasicActivity extends AppCompatActivity implements View.OnClickListener {

    protected BasicActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initTheme();
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        afterCreate(savedInstanceState);
    }

    private void initTheme() {
        PreferenceUtil preferenceUtil = new PreferenceUtil(this);
        int themePosition = preferenceUtil.getInt(Config.THEME, -1);
        if (themePosition != -1) {
            switch (themePosition) {
                case 1:
                    setTheme(R.style.AppFirst);
                    break;
                case 2:
                    setTheme(R.style.AppSecond);
                    break;
                case 3:
                    setTheme(R.style.AppThird);
                    break;
                case 4:
                    setTheme(R.style.AppFour);
                    break;
            }
        }
    }

    /**
     * 获取布局
     **/
    protected abstract int getLayoutId();

    /**
     * 布局创建之后
     **/
    protected abstract void afterCreate(Bundle savedInstanceState);

    /**
     * View点击
     **/
    protected abstract void widgetClick(View v);

    @Override
    public void onClick(View v) {
        widgetClick(v);
    }
}
