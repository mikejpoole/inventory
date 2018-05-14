package com.example.android.inventory.helper;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class BookImageHelper {

    public static void loadImage(Context context, ImageView iv){
        String url = "https://raw.githubusercontent.com/bumptech/glide/master/static/glide_logo.png";
        Log.i("Book helper","Getting image from " + url);

        Glide.with(context)
//                .downloadOnly()
                .load(url)

//                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
//                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)

//                .onlyRetrieveFromCache(true)
//                .placeholder(R.drawable.placeholder_image)
//                .error(R.drawable.image_placeholder)

                .into(iv);

        Log.i("Book helper","Finished getting image");
    }
}
