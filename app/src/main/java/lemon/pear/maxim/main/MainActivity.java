package lemon.pear.maxim.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import lemon.pear.maxim.App;
import lemon.pear.maxim.Config;
import lemon.pear.maxim.R;
import lemon.pear.maxim.adapter.MenuAdapter;
import lemon.pear.maxim.adapter.TabPagerAdapter;
import lemon.pear.maxim.base.BasicActivity;
import lemon.pear.maxim.entity.MenuModel;
import lemon.pear.maxim.module.maxim.activity.SearchActivity;
import lemon.pear.maxim.module.maxim.fragment.MaximFragment;
import lemon.pear.maxim.module.maxim.fragment.WickedFragment;
import lemon.pear.maxim.listener.OnItemClickListener;
import lemon.pear.maxim.module.maxim.activity.StoreActivity;
import lemon.pear.maxim.module.setup.activity.SetupActivity;
import lemon.pear.maxim.toolkit.PreferenceUtil;
import lemon.pear.maxim.toolkit.StringUtil;
import lemon.pear.maxim.toolkit.ToastUtil;
import lemon.pear.maxim.module.user.activity.LoginActivity;
import lemon.pear.maxim.module.user.activity.PersonalActivity;
import lemon.pear.maxim.widget.CircleImageView;

/**
 * 主界面
 */
public class MainActivity extends BasicActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.layMenu)
    RelativeLayout layMenu;
    @BindView(R.id.layUser)
    RelativeLayout layUser;
    @BindView(R.id.ivUserPhoto)
    CircleImageView ivUserPhoto;
    @BindView(R.id.tvUserName)
    TextView tvUserName;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private UserBroadCast userBroadCast;//用户状态广播

    private boolean isQuit;//是否退出

    @Override
    protected int getLayoutId() {
        return R.layout.aty_main;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        userBroadCast = new UserBroadCast();
        initToolBar();
        initContent();
        initMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                startActivity(new Intent(activity, SearchActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Config.LOGIN_ACTION);//登录成功
        intentFilter.addAction(Config.PERSON_MODIFY);//信息修改
        intentFilter.addAction(Config.LOGOUT_ACTION);//退出登录
        activity.registerReceiver(userBroadCast, intentFilter);
    }

    private void initToolBar() {
        toolbar.setTitle(R.string.app_name);
        toolbar.setNavigationIcon(R.drawable.pic_menu);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });
    }

    private void initContent() {
        tabLayout.setupWithViewPager(viewPager);
        String[] titles = new String[]{getString(R.string.tab_motto),
                getString(R.string.tab_rule), getString(R.string.tab_effect), getString(R.string.tab_wicked)};
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(MaximFragment.getInstance(1));
        fragments.add(MaximFragment.getInstance(2));
        fragments.add(MaximFragment.getInstance(3));
        fragments.add(WickedFragment.getInstance());
        viewPager.setAdapter(new TabPagerAdapter(
                getSupportFragmentManager(), fragments, titles));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        viewPager.setCurrentItem(0);
    }

    private void refreshMenuLayout() {
        PreferenceUtil preferenceUtil = new PreferenceUtil(this);
        int themePosition = preferenceUtil.getInt(Config.THEME, -1);
        if (themePosition != -1) {
            switch (themePosition) {
                case 1:
                    layMenu.setBackgroundResource(R.drawable.wall_first);
                    break;
                case 2:
                    layMenu.setBackgroundResource(R.drawable.wall_second);
                    break;
                case 3:
                    layMenu.setBackgroundResource(R.drawable.wall_third);
                    break;
                case 4:
                    layMenu.setBackgroundResource(R.drawable.wall_four);
                    break;
            }
        }
    }

    private void initMenu() {
        refreshUserInfo();
        refreshMenuLayout();
        layUser.setOnClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        List<MenuModel> menuList = new ArrayList<>();
        String[] menuIcon = new String[]{getString(R.string.font_store),
                getString(R.string.font_setup)};
        String[] menuText = new String[]{getString(R.string.store),
                getString(R.string.setup)};
        for (int i = 0; i < menuIcon.length; i++) {
            MenuModel model = new MenuModel();
            model.setMenuIcon(menuIcon[i]);
            model.setMenuText(menuText[i]);
            menuList.add(model);
        }
        MenuAdapter menuAdapter = new MenuAdapter(activity, menuList);
        menuAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                switch (position) {
                    case 0://收藏
                        if (App.getInstance().isLogin()) {
                            startActivity(new Intent(activity, StoreActivity.class));
                        } else {
                            startActivity(new Intent(activity, LoginActivity.class));
                        }
                        break;
                    case 1://设置
                        startActivity(new Intent(activity, SetupActivity.class));
                        break;
                }
            }

            @Override
            public void onItemLongClick(int position) {
            }
        });
        recyclerView.setAdapter(menuAdapter);
    }

    /**
     * 刷新侧边栏用户信息
     */
    private void refreshUserInfo() {
        String userName = App.getInstance().getUserName();
        if (StringUtil.isEmpty(App.getInstance().getUserId())) {
            tvUserName.setText(R.string.user_empty);
        } else {
            tvUserName.setText(StringUtil.isEmpty(userName) ? getString(R.string.user_login) : userName);
        }
        String photoUrl = App.getInstance().getUserPhoto();
        String fileName = App.getInstance().getUserId() + ".jpg";
        if (!StringUtil.isEmpty(photoUrl)) {
            File photoFile = new File(activity.getExternalCacheDir(), fileName);
            if (photoFile.exists()) {//文件存在
                ivUserPhoto.setImageURI(null);
                ivUserPhoto.setImageURI(Uri.fromFile(photoFile));
            } else {
                downloadFile(photoUrl, fileName);
            }
        } else {
            ivUserPhoto.setImageResource(R.drawable.icon_user);
        }
    }

    private void downloadFile(String photoUrl, String fileName) {
        BmobFile file = new BmobFile();
        file.setUrl(photoUrl);
        File saveFile = new File(activity.getExternalCacheDir(), fileName);
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
            public void onProgress(Integer value, long networkSpeed) {
            }
        });
    }

    @Override
    protected void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.layUser:
                if (App.getInstance().isLogin()) {
                    startActivity(new Intent(activity, PersonalActivity.class));
                } else {
                    startActivity(new Intent(activity, LoginActivity.class));
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (userBroadCast != null) {
            activity.unregisterReceiver(userBroadCast);
        }
        super.onDestroy();
    }

    public class UserBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshUserInfo();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawers();
            return;
        }
        if (!isQuit) {
            ToastUtil.showShortToast(activity, "再按一次退出程序");
            isQuit = true;
            //这段代码意思是,在两秒钟之后isQuit会变成false
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        isQuit = false;
                    }
                }
            }).start();
        } else {
            System.exit(0);
        }
    }

}
