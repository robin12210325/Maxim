package lemon.pear.maxim.module.maxim.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
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
import lemon.pear.maxim.base.BasicActivity;
import lemon.pear.maxim.module.maxim.entity.MaximModel;
import lemon.pear.maxim.listener.OnItemClickListener;
import lemon.pear.maxim.toolkit.IntentHelper;
import lemon.pear.maxim.toolkit.KeyboardUtil;
import lemon.pear.maxim.toolkit.StringUtil;

/**
 * 搜索页
 */

public class SearchActivity extends BasicActivity {

    @BindView(R.id.tvCancel)
    TextView tvCancel;
    @BindView(R.id.etSearch)
    EditText etSearch;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private int pageNumber = Config.PAGE_START;

    private MaximAdapter dataAdapter;

    private List<MaximModel> dataList;

    @Override
    protected int getLayoutId() {
        return R.layout.aty_search;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        initSearch();
        initList();
    }

    private void initSearch() {
        tvCancel.setOnClickListener(this);
        etSearch.requestFocus();
        etSearch.setSingleLine(true);
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchText = etSearch.getText().toString();
                    if (!StringUtil.isEmpty(searchText)) {
                        KeyboardUtil.hideSoftInput(activity);
                        loadData(searchText);
                    } else {
                        dataList.clear();
                        dataAdapter.notifyDataSetChanged();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void initList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        dataList = new ArrayList<>();
        dataAdapter = new MaximAdapter(activity, dataList);
        recyclerView.setAdapter(dataAdapter);
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
    }

    private void loadData(String searchText) {
        BmobQuery<MaximModel> query = new BmobQuery<>();
        query.addWhereEqualTo(Config.MaximTitle, searchText);
        query.setLimit(Config.PAGE_SIZE);
        query.findObjects(new FindListener<MaximModel>() {
            @Override
            public void done(List<MaximModel> object, BmobException e) {
                if (e == null) {
                    if (pageNumber == Config.PAGE_START) {
                        dataList.clear();
                    }
                    dataList.addAll(object);
                    dataAdapter.notifyDataSetChanged();
                } else {
                    Logger.d("失败：" + e.getMessage());
                }
            }
        });
    }

    @Override
    protected void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.tvCancel:
                finish();
                break;
        }
    }
}
