package lemon.pear.maxim.module.setup.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lemon.pear.maxim.R;
import lemon.pear.maxim.listener.OnItemClickListener;
import lemon.pear.maxim.module.setup.entity.ImageModel;
import lemon.pear.maxim.toolkit.ImageLoader;

/**
 * 设置壁纸适配器
 */

public class WallpaperAdapter extends RecyclerView.Adapter<WallpaperAdapter.ViewHolder> {

    private Context context;

    private List<ImageModel> dataList;

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public WallpaperAdapter(Context context, List<ImageModel> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public WallpaperAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_wallpaper, parent, false);
        return new WallpaperAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WallpaperAdapter.ViewHolder holder, final int position) {
        ImageModel data = dataList.get(position);
        holder.tvWallpaper.setText(data.getName());
        if (data.isCustom()) {
            ImageLoader.loadImage(context, data.getResource() , holder.ivWallpaper);
        } else {
            ImageLoader.loadImage(context, new File(data.getPath()) , holder.ivWallpaper);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        @BindView(R.id.ivWallpaper)
        ImageView ivWallpaper;
        @BindView(R.id.tvWallpaper)
        TextView tvWallpaper;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
