package com.example.documenpro.photoPickReader.engineReader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;

public class GlideEngineManager implements ImageEngineManager {

    @Override
    public void loadImage_ImageEngine(Context context_GlideEngine, int resizeX_GlideEngine, int resizeY_GlideEngine, ImageView imageView_GlideEngine, Uri uri_GlideEngine) {
        Glide.with(context_GlideEngine).load(uri_GlideEngine).apply(new RequestOptions().override(resizeX_GlideEngine, resizeY_GlideEngine).priority(Priority.HIGH).fitCenter()).into(imageView_GlideEngine);
    }

    @Override
    public void loadThumbnail_ImageEngine(Context context_GlideEngine, int resize_GlideEngine, Drawable placeholder_GlideEngine, ImageView imageView_GlideEngine, Uri uri_GlideEngine) {
        Glide.with(context_GlideEngine).asBitmap().load(uri_GlideEngine).apply(new RequestOptions().override(resize_GlideEngine, resize_GlideEngine).placeholder(placeholder_GlideEngine).centerCrop()).into(imageView_GlideEngine);
    }

    @Override
    public void loadGifImage_ImageEngine(Context context_GlideEngine, int resizeX_GlideEngine, int resizeY_GlideEngine, ImageView imageView_GlideEngine, Uri uri_GlideEngine) {
        Glide.with(context_GlideEngine).asGif().load(uri_GlideEngine).apply(new RequestOptions().override(resizeX_GlideEngine, resizeY_GlideEngine).priority(Priority.HIGH).fitCenter()).into(imageView_GlideEngine);
    }

    @Override
    public void loadGifThumbnail_ImageEngine(Context context_GlideEngine, int resize_GlideEngine, Drawable placeholder_GlideEngine, ImageView imageView_GlideEngine, Uri uri_GlideEngine) {
        Glide.with(context_GlideEngine).asBitmap().load(uri_GlideEngine).apply(new RequestOptions().override(resize_GlideEngine, resize_GlideEngine).placeholder(placeholder_GlideEngine).centerCrop()).into(imageView_GlideEngine);
    }


}
