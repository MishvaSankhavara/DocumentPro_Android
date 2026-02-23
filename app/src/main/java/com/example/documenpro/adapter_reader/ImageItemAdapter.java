package com.example.documenpro.adapter_reader;

import android.app.Activity;
import android.media.MediaScannerConnection;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.documenpro.GlobalConstant;
import com.example.documenpro.R;
import com.example.documenpro.listener.OnConfirmListener;
import com.example.documenpro.utils.Utils;

import java.io.File;
import java.util.ArrayList;

public class ImageItemAdapter extends RecyclerView.Adapter<ImageItemAdapter.ViewHolder> {

    public ArrayList<File> arrayList_ImageItem;
    public Activity mContext_ImageItem;

    public ImageItemAdapter(Activity mContext,
                            ArrayList<File> arrayList) {
        this.mContext_ImageItem = mContext;
        this.arrayList_ImageItem = arrayList;
    }

    @Override
    public int getItemCount() {
        if (arrayList_ImageItem == null) {
            return 0;
        } else {
            return arrayList_ImageItem.size();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,
                                 int position) {

        File file = arrayList_ImageItem.get(position);

        Glide.with(mContext_ImageItem)
                .load(file.getPath())
                .into(holder.imgPhoto_ImageItem);

        holder.btnDelete_ImageItem.setOnClickListener(view ->
                Utils.showConfirmDialog(
                        mContext_ImageItem,
                        GlobalConstant.DIALOG_CONFIRM_DELETE,
                        new OnConfirmListener() {
                            @Override
                            public void onConfirm() {

                                File fileDelete =
                                        new File(file.getAbsolutePath());

                                fileDelete.delete();

                                MediaScannerConnection.scanFile(
                                        mContext_ImageItem,
                                        new String[]{fileDelete.getAbsolutePath()},
                                        null,
                                        null);

                                arrayList_ImageItem.remove(
                                        holder.getAdapterPosition());

                                notifyItemRemoved(
                                        holder.getAdapterPosition());

                                notifyItemRangeChanged(
                                        holder.getAdapterPosition(),
                                        arrayList_ImageItem.size());
                            }
                        }));

        holder.btnShare_ImageItem.setOnClickListener(view ->
                Utils.shareImage(
                        mContext_ImageItem,
                        file.getPath()));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {

        return new ViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_image_pdf,
                                parent,
                                false));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        AppCompatImageView imgPhoto_ImageItem;
        AppCompatImageView btnDelete_ImageItem;
        AppCompatImageView btnShare_ImageItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            btnShare_ImageItem =
                    itemView.findViewById(R.id.btnShare);

            btnDelete_ImageItem =
                    itemView.findViewById(R.id.btnDelete);

            imgPhoto_ImageItem =
                    itemView.findViewById(R.id.pageIv);
        }
    }
}