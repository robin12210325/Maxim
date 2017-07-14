package lemon.pear.maxim.module.maxim.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lemon.pear.maxim.R;
import lemon.pear.maxim.listener.OnItemClickListener;
import lemon.pear.maxim.module.maxim.entity.CommentModel;
import lemon.pear.maxim.module.maxim.entity.MaximStore;
import lemon.pear.maxim.toolkit.ImageLoader;
import lemon.pear.maxim.toolkit.StringUtil;

/**
 * 自定义Adapter
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context context;

    private List<CommentModel> dataList;

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public CommentAdapter(Context context, List<CommentModel> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        CommentModel data = dataList.get(position);
        String userName = data.getMaximUser().getName();
        if (data.getMaximUser().getFile() != null) {
            String photoUrl = data.getMaximUser().getFile().getUrl();
            if (!StringUtil.isEmpty(photoUrl)) {
                ImageLoader.loadImage(context, photoUrl, holder.ivUser);
            } else {
                holder.ivUser.setImageResource(R.drawable.icon_user);
            }
        }
        holder.tvName.setText(userName);
        holder.tvComment.setText(data.getComment());
        holder.layMaxim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivUser)
        ImageView ivUser;
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.tvComment)
        TextView tvComment;
        @BindView(R.id.layMaxim)
        RelativeLayout layMaxim;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
