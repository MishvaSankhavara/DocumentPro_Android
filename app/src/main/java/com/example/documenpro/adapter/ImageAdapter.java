package com.example.documenpro.adapter;

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

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    public Activity mContext;
    public ArrayList<File> arrayList;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_pdf, parent, false));
    }

    public ImageAdapter(Activity mContext, ArrayList<File> arrayList) {
        this.mContext = mContext;
        this.arrayList = arrayList;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        File file = arrayList.get(position);
        Glide.with(mContext).load(file.getPath()).into(holder.imgPhoto);
        holder.btnDelete.setOnClickListener(view -> {
            Utils.showConfirmDialog(mContext, GlobalConstant.DIALOG_CONFIRM_DELETE, new OnConfirmListener() {
                @Override
                public void onConfirm() {
                    File fileDelete = new File(file.getAbsolutePath());
                    fileDelete.delete();
                    MediaScannerConnection.scanFile(mContext, new String[]{fileDelete.getAbsolutePath()}, null, null);
                    arrayList.remove(holder.getAdapterPosition());
                    notifyItemRemoved(holder.getAdapterPosition());
                    notifyItemRangeChanged(holder.getAdapterPosition(), arrayList.size());
                }


            });
        });
        holder.btnShare.setOnClickListener(view -> Utils.shareImage(mContext, file.getPath()));
    }

    @Override
    public int getItemCount() {
        if (arrayList == null) {
            return 0;
        } else {
            return arrayList.size();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView btnShare;
        AppCompatImageView btnDelete;
        AppCompatImageView imgPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnShare = itemView.findViewById(R.id.btnShare);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            imgPhoto = itemView.findViewById(R.id.pageIv);
        }
    }
}
