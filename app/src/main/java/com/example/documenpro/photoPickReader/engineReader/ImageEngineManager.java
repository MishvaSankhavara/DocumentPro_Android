package com.example.documenpro.photoPickReader.engineReader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

public interface ImageEngineManager {

    void loadImage_ImageEngine(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri);

    void loadThumbnail_ImageEngine(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri);

    void loadGifImage_ImageEngine(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri);

    void loadGifThumbnail_ImageEngine(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri);


}
