package lemon.pear.maxim.module.maxim.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import lemon.pear.maxim.Config;
import lemon.pear.maxim.R;
import lemon.pear.maxim.module.maxim.adapter.MaximAdapter;
import lemon.pear.maxim.base.BasicFragment;
import lemon.pear.maxim.module.maxim.entity.MaximModel;
import lemon.pear.maxim.listener.OnItemClickListener;
import lemon.pear.maxim.listener.RecyclerScrollListener;
import lemon.pear.maxim.toolkit.IntentHelper;

/**
 * 正能量
 */

public class MaximFragment extends BasicFragment {
    @BindView(R.id.dataEmpty)
    TextView dataEmpty;
    @BindView(R.id.dataView)
    RecyclerView dataView;

    private int pageNumber;

    private int maximType;//类型

    private MaximAdapter dataAdapter;

    private List<MaximModel> dataList;

    private boolean loadComplete;//是否加载完毕

    public static Fragment getInstance(int type) {
        Fragment fragment = new MaximFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(IntentHelper.MAXIM_TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frg_maxim;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        getBundle();
        initView();
        loadNewData();
    }

    private void getBundle() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            maximType = bundle.getInt(IntentHelper.MAXIM_TYPE);
        }
    }

    private void initView() {
        dataList = new ArrayList<>();
        dataAdapter = new MaximAdapter(activity, dataList);
        dataAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                MaximModel data = dataList.get(position);
                IntentHelper.showMaximActivity(activity, data.getObjectId(), data.getTitle());
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

    /**
     * 查询数据
     */
    private void loadData() {
        BmobQuery<MaximModel> query = new BmobQuery<>();
        query.addWhereEqualTo(Config.MaximType, maximType);
        query.setLimit(Config.PAGE_SIZE);
        query.setSkip(pageNumber * Config.PAGE_SIZE);
        query.order("-createdAt");
        query.findObjects(new FindListener<MaximModel>() {
            @Override
            public void done(List<MaximModel> object, BmobException e) {
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
}
