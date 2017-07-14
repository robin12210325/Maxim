package lemon.pear.maxim.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * 所有Fragment的基类
 */
public abstract class BasicFragment extends Fragment implements OnClickListener {

    protected FragmentActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        afterCreate(savedInstanceState);
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
