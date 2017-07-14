package lemon.pear.maxim.module.maxim.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import lemon.pear.maxim.App;
import lemon.pear.maxim.Config;
import lemon.pear.maxim.R;
import lemon.pear.maxim.module.maxim.adapter.StoreAdapter;
import lemon.pear.maxim.base.BasicActivity;
import lemon.pear.maxim.module.maxim.entity.MaximModel;
import lemon.pear.maxim.module.maxim.entity.MaximStore;
import lemon.pear.maxim.listener.OnItemClickListener;
import lemon.pear.maxim.listener.RecyclerScrollListener;
import lemon.pear.maxim.module.user.entity.MaximUser;
import lemon.pear.maxim.toolkit.IntentHelper;

public class StoreActivity extends BasicActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.dataEmpty)
    TextView dataEmpty;
    @BindView(R.id.dataView)
    RecyclerView dataView;

    private int pageNumber = Config.PAGE_START;

    private StoreAdapter dataAdapter;

    private List<MaximStore> dataList;

    private boolean loadComplete;//是否加载完毕

    @Override
    protected int getLayoutId() {
        return R.layout.aty_store;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        initToolBar();
        initList();
        loadNewData();
    }

    private void initToolBar() {
        toolbar.setTitle(R.string.store);
        toolbar.setNavigationIcon(R.drawable.pic_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initList() {
        dataList = new ArrayList<>();
        dataAdapter = new StoreAdapter(activity, dataList);
        dataAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                MaximStore data = dataList.get(position);
                MaximModel model = data.getMaximModel();
                IntentHelper.showMaximActivity(activity, model.getObjectId(), data.getMaximTitle());
            }

            @Override
            public void onItemLongClick(int position) {

            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        dataView.setLayoutManager(linearLayoutManager);
        dataView.setAdapter(dataAdapter);
        dataView.addOnScrollListener(new RecyclerScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore() {
                if (loadComplete) {
                    return;
                }
                pageNumber++;
                loadData();
            }
        });
        dataEmpty.setOnClickListener(this);
    }

    private void loadNewData() {
        loadComplete = false;
        pageNumber = Config.PAGE_START;
        dataView.setVisibility(View.GONE);
        dataEmpty.setVisibility(View.VISIBLE);
        dataEmpty.setText(R.string.data_load);
        loadData();
    }

    private void loadData() {
        BmobQuery<MaximStore> query = new BmobQuery<>();
        MaximUser user = new MaximUser();
        user.setObjectId(App.getInstance().getUserId());
        query.addWhereEqualTo("maximUser", user);
        query.setLimit(Config.PAGE_SIZE);
        query.order("-createdAt");
        query.setSkip(pageNumber * Config.PAGE_SIZE);
        query.findObjects(new FindListener<MaximStore>() {
            @Override
            public void done(List<MaximStore> object, BmobException e) {
                if (e == null) {
                    if (pageNumber == Config.PAGE_START) {
                        dataList.clear();
                    }
                    if (object != null && object.size() > 0) {
                        dataList.addAll(object);
                    } else {
                        loadComplete = true;
                    }
                } else {
                    Logger.d("失败：" + e.getMessage());
                }
                checkDataList(e);
            }
        });
    }

    private void checkDataList(BmobException e) {
        if (e == null) {
            if (dataList.size() > 0) {
                dataView.setVisibility(View.VISIBLE);
                dataEmpty.setVisibility(View.GONE);
                dataAdapter.notifyDataSetChanged();
            } else {
                dataView.setVisibility(View.GONE);
                dataEmpty.setVisibility(View.VISIBLE);
                dataEmpty.setText(R.string.data_empty);
            }
        } else {
            dataView.setVisibility(View.GONE);
            dataEmpty.setVisibility(View.VISIBLE);
            dataEmpty.setText(R.string.data_error);
        }
    }

    @Override
    protected void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.dataEmpty:
                loadData();
                break;
            default:
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            loadNewData();
        }
    }
}
