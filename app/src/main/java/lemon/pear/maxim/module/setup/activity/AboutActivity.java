package lemon.pear.maxim.module.setup.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import butterknife.BindView;
import lemon.pear.maxim.R;
import lemon.pear.maxim.base.BasicActivity;

/**
 * 关于页
 */

public class AboutActivity extends BasicActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected int getLayoutId() {
        return R.layout.aty_about;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        initToolBar();
        initView();
    }

    private void initToolBar() {
        toolbar.setTitle(R.string.about);
        toolbar.setNavigationIcon(R.drawable.pic_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView(){

    }

    @Override
    protected void widgetClick(View v) {

    }
}
