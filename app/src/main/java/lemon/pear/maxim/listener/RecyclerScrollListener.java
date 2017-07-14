package lemon.pear.maxim.listener;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * recyclerView加载更多接口
 */
public abstract class RecyclerScrollListener extends
        RecyclerView.OnScrollListener {

    private LinearLayoutManager mLinearLayoutManager;

    //在屏幕可见的Item中的第一个
    private int firstVisibleItem;

    //在屏幕上可见的item数量
    private int visibleItemCount;

    //已经加载出来的Item的数量
    private int totalItemCount;

    //主要用来存储上一个totalItemCount
    private int previousTotal = 0;

    //是否正在上拉数据
    private boolean loading = true;

    public RecyclerScrollListener(
            LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

        if (loading) {
            if (totalItemCount > previousTotal) {
                //说明数据已经加载结束
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading && totalItemCount - visibleItemCount <= firstVisibleItem) {
            onLoadMore();
            loading = true;
        }
    }

    public abstract void onLoadMore();
}