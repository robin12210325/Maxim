package lemon.pear.maxim.toolkit;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

/**
 * 图片加载
 */

public class ImageLoader {
    public static void loadImage(Context context, String imageUrl, ImageView imageView) {
        Glide.with(context).load(imageUrl).into(imageView);
    }

    public static void loadImage(Context context, File imageFile, ImageView imageView) {
        Glide.with(context).load(imageFile).into(imageView);
    }

    public static void loadImage(Context context, int imageRes, ImageView imageView) {
        Glide.with(context).load(imageRes).into(imageView);
    }
}
