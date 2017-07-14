package lemon.pear.maxim.module.maxim.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lemon.pear.maxim.R;
import lemon.pear.maxim.module.maxim.entity.WickedModel;

/**
 * 自定义Adapter
 */

public class WickedAdapter extends RecyclerView.Adapter<WickedAdapter.ViewHolder> {

    private Context context;

    private List<WickedModel> dataList;

    public WickedAdapter(Context context, List<WickedModel> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_wicked, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tvFront.setText(dataList.get(position).getFront());
        holder.tvBehind.setText(dataList.get(position).getBehind());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvFront)
        TextView tvFront;
        @BindView(R.id.tvBehind)
        TextView tvBehind;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
