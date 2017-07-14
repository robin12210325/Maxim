package lemon.pear.maxim.module.setup.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import lemon.pear.maxim.Config;
import lemon.pear.maxim.R;
import lemon.pear.maxim.base.BasicActivity;
import lemon.pear.maxim.listener.OnItemClickListener;
import lemon.pear.maxim.main.MainActivity;
import lemon.pear.maxim.module.setup.adapter.WallpaperAdapter;
import lemon.pear.maxim.module.setup.entity.ImageModel;
import lemon.pear.maxim.toolkit.PreferenceUtil;

/**
 * 设置壁纸
 */

public class WallpaperActivity extends BasicActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.dataView)
    RecyclerView dataView;

    private List<ImageModel> dataList;

    private WallpaperAdapter dataAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.aty_wallpaper;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        initToolBar();
        initView();
        loadData();
    }

    private void initToolBar() {
        toolbar.setTitle(R.string.wallpaper);
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
        dataList = new ArrayList<>();
        dataAdapter = new WallpaperAdapter(activity, dataList);
        dataView.setLayoutManager(new GridLayoutManager(activity, 3));
        dataView.setAdapter(dataAdapter);
        dataAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                PreferenceUtil preferenceUtil = new PreferenceUtil(activity);
                if (position == 0) {
                    preferenceUtil.remove(Config.THEME);
                } else {
                    preferenceUtil.putInt(Config.THEME, position);
                }
                Intent intent = new Intent(activity, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
                activity.finish();
            }

            @Override
            public void onItemLongClick(int position) {
            }
        });
    }

    private void loadData() {
        String[] names = new String[]{"默认", "样式一", "样式二", "样式三", "样式四"};
        int[] resources = new int[]{R.drawable.wall_default, R.drawable.wall_first, R.drawable.wall_second,
                R.drawable.wall_third, R.drawable.wall_four};
        for (int i = 0; i < names.length; i++) {
            ImageModel data = new ImageModel();
            data.setCustom(true);
            data.setResource(resources[i]);
            data.setName(names[i]);
            dataList.add(data);
        }
        dataAdapter.notifyDataSetChanged();
    }

    @Override
    protected void widgetClick(View v) {

    }
}
