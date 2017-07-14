package lemon.pear.maxim.module.maxim.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import lemon.pear.maxim.App;
import lemon.pear.maxim.Config;
import lemon.pear.maxim.R;
import lemon.pear.maxim.base.BasicActivity;
import lemon.pear.maxim.listener.OnItemClickListener;
import lemon.pear.maxim.listener.RecyclerScrollListener;
import lemon.pear.maxim.module.maxim.adapter.CommentAdapter;
import lemon.pear.maxim.module.maxim.adapter.StoreAdapter;
import lemon.pear.maxim.module.maxim.entity.CommentModel;
import lemon.pear.maxim.module.maxim.entity.MaximModel;
import lemon.pear.maxim.module.maxim.entity.MaximStore;
import lemon.pear.maxim.module.user.entity.MaximUser;
import lemon.pear.maxim.toolkit.IntentHelper;
import lemon.pear.maxim.toolkit.KeyboardUtil;
import lemon.pear.maxim.toolkit.StringUtil;
import lemon.pear.maxim.toolkit.ToastUtil;
import lemon.pear.maxim.module.user.activity.LoginActivity;
import lemon.pear.maxim.widget.IconTextView;

public class DetailActivity extends BasicActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tvMaxim)
    TextView tvMaxim;
    @BindView(R.id.rvComment)
    RecyclerView rvComment;
    @BindView(R.id.tvComment)
    IconTextView tvComment;
    @BindView(R.id.tvSend)
    IconTextView tvSend;
    @BindView(R.id.etComment)
    TextView etComment;
    @BindView(R.id.svDetail)
    ScrollView svDetail;

    private String title;//标题

    private String objectId;//MaximId

    private Menu menu;//头部菜单

    private String storeId;//收藏ID

    private DetailBroadCast userBroadCast;//用户状态广播

    private boolean doAttention;//执行收藏操作

    private String comment;//评论内容

    private int pageNumber = Config.PAGE_START;

    private CommentAdapter dataAdapter;

    private List<CommentModel> dataList;

    private boolean loadComplete;//是否加载完毕

    private boolean showComment;//是否显示评论

    @Override
    protected int getLayoutId() {
        return R.layout.aty_detail;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        userBroadCast = new DetailBroadCast();
        getBundle();
        initView();
        loadData();
        loadNewComment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_store, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_store:
                storeMaxim();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Config.LOGIN_ACTION);//登录
        activity.registerReceiver(userBroadCast, intentFilter);
    }

    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            title = bundle.getString(IntentHelper.MAXIM_TITLE);
            objectId = bundle.getString(IntentHelper.OBJECT_ID);
        }
    }

    private void initToolBar() {
        toolbar.setTitle(title);
        toolbar.setNavigationIcon(R.drawable.pic_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initView() {
        initToolBar();
        tvComment.setOnClickListener(this);
        tvSend.setOnClickListener(this);
        etComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                comment = s.toString();
            }
        });
        dataList = new ArrayList<>();
        dataAdapter = new CommentAdapter(activity, dataList);
        dataAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
            }

            @Override
            public void onItemLongClick(int position) {

            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        rvComment.setLayoutManager(linearLayoutManager);
        rvComment.setAdapter(dataAdapter);
        rvComment.addOnScrollListener(new RecyclerScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore() {
                if (loadComplete) {
                    return;
                }
                pageNumber++;
                loadData();
            }
        });
    }

    private void loadData() {
        if (!StringUtil.isEmpty(objectId)) {
            BmobQuery<MaximModel> query = new BmobQuery<>();
            query.getObject(objectId, new QueryListener<MaximModel>() {
                @Override
                public void done(MaximModel object, BmobException e) {
                    if (e == null) {
                        tvMaxim.setText(object.getDetail());
                    } else {
                        Logger.d("失败：" + e.getMessage());
                    }
                }
            });
            checkStore();
        }
    }

    /**
     * 检查是否收藏
     */
    private void checkStore() {
        if (App.getInstance().isLogin()) {
            BmobQuery<MaximStore> query = new BmobQuery<>();
            MaximModel model = new MaximModel();
            model.setObjectId(objectId);
            query.addWhereEqualTo("maximModel", new BmobPointer(model));
            query.include("maximUser");
            query.findObjects(new FindListener<MaximStore>() {

                @Override
                public void done(List<MaximStore> object, BmobException e) {
                    if (e == null) {
                        String userId = App.getInstance().getUserId();
                        for (MaximStore store : object) {
                            if (store.getMaximUser().getObjectId().equals(userId)) {
                                menu.getItem(0).setIcon(R.drawable.pic_store);
                                storeId = store.getObjectId();
                                break;
                            }
                        }
                    } else {
                        Logger.d(e.getMessage());
                    }
                }

            });
        }
    }

    /**
     * 收藏功能
     */
    private void storeMaxim() {
        if (App.getInstance().isLogin()) {
            MaximStore store = new MaximStore();
            if (StringUtil.isEmpty(storeId)) {//还没有收藏
                //MaximUser
                MaximUser maximUser = new MaximUser();
                maximUser.setObjectId(App.getInstance().getUserId());
                store.setMaximUser(maximUser);
                //MaximModel
                MaximModel maximModel = new MaximModel();
                maximModel.setObjectId(objectId);
                store.setMaximModel(maximModel);
                //MaximTitle
                store.setMaximTitle(title);
                store.save(new SaveListener<String>() {
                    @Override
                    public void done(String objectId, BmobException e) {
                        if (e == null) {
                            ToastUtil.showShortToast(activity, "收藏成功");
                            menu.getItem(0).setIcon(R.drawable.pic_store);
                            storeId = objectId;
                            doAttention = true;
                        } else {
                            Logger.d(e.getMessage());
                        }
                    }
                });
            } else {
                store.setObjectId(storeId);
                store.delete(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            ToastUtil.showShortToast(activity, "取消成功");
                            menu.getItem(0).setIcon(R.drawable.pic_unstore);
                            storeId = "";
                            doAttention = true;
                        } else {
                            Logger.d(e.getMessage());
                        }
                    }
                });
            }
        } else {
            startActivity(new Intent(activity, LoginActivity.class));
        }
    }

    /**
     * 提交评论
     */
    private void submitComment() {
        if (App.getInstance().isLogin()) {
            if (StringUtil.isEmpty(comment)) {
                ToastUtil.showShortToast(activity, "请输入内容");
                etComment.requestFocus();
                KeyboardUtil.showSoftInput(activity);
                return;
            }
            CommentModel data = new CommentModel();
            //MaximUser
            MaximUser maximUser = new MaximUser();
            maximUser.setObjectId(App.getInstance().getUserId());
            data.setMaximUser(maximUser);
            //MaximModel
            MaximModel maximModel = new MaximModel();
            maximModel.setObjectId(objectId);
            data.setMaximModel(maximModel);
            //MaximTitle
            data.setComment(comment);
            data.save(new SaveListener<String>() {
                @Override
                public void done(String objectId, BmobException e) {
                    if (e == null) {
                        ToastUtil.showShortToast(activity, "评论成功");
                        etComment.setText(comment = "");
                        showComment = true;//显示评论
                        loadNewComment();
                    } else {
                        Logger.d(e.getMessage());
                    }
                }
            });
        } else {
            startActivity(new Intent(activity, LoginActivity.class));
        }
    }

    private void loadNewComment() {
        loadComplete = false;
        pageNumber = Config.PAGE_START;
        loadComment();
    }

    /**
     * 加载评论
     */
    private void loadComment() {
        if (App.getInstance().isLogin()) {
            BmobQuery<CommentModel> query = new BmobQuery<>();
            MaximModel model = new MaximModel();
            model.setObjectId(objectId);
            query.addWhereEqualTo("maximModel", new BmobPointer(model));
            query.include("maximUser");
            query.setLimit(Config.PAGE_SIZE);
            query.order("-createdAt");
            query.setSkip(pageNumber * Config.PAGE_SIZE);
            query.findObjects(new FindListener<CommentModel>() {
                @Override
                public void done(List<CommentModel> object, BmobException e) {
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
                    dataAdapter.notifyDataSetChanged();
                    scrollToComment();
                }
            });
        }
    }

    /**
     * 滑动到评论
     */
    private void scrollToComment() {
        if (showComment) {
            svDetail.post(new Runnable() {
                @Override
                public void run() {
                    int[] location = new int[2];
                    rvComment.getLocationOnScreen(location);
                    int offset = tvMaxim.getHeight();
                    svDetail.smoothScrollTo(0, offset);
                }
            });
        } else {
            svDetail.post(new Runnable() {
                @Override
                public void run() {
                    svDetail.smoothScrollTo(0, 0);
                }
            });
        }
    }

    @Override
    protected void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.tvComment:
                showComment = !showComment;
                scrollToComment();
                break;
            case R.id.tvSend:
                submitComment();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (doAttention) {
            setResult(RESULT_OK);
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (userBroadCast != null) {
            activity.unregisterReceiver(userBroadCast);
        }
        super.onDestroy();
    }

    public class DetailBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == Config.LOGIN_ACTION) {//登录成功
                checkStore();
            }
        }
    }
}
