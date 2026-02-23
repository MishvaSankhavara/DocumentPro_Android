package com.example.documenpro.adapter_reader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;
import com.example.documenpro.R;
import com.example.documenpro.clickListener.OnRemovePhotoListener;
import com.example.documenpro.model_reader.PhotoModel;

import java.util.ArrayList;
import java.util.Collections;

public class PhotoSelectionAdapter
        extends RecyclerView.Adapter<PhotoSelectionAdapter.ViewHolder>
        implements DraggableItemAdapter<PhotoSelectionAdapter.ViewHolder> {

    private final OnRemovePhotoListener mListener_PhotoSelection;
    private final ArrayList<PhotoModel> arrayListPhoto_PhotoSelection;
    private final Context mContext_PhotoSelection;

    public PhotoSelectionAdapter(Context mContext,
                                 ArrayList<PhotoModel> arrayListPhoto,
                                 OnRemovePhotoListener listener) {
        this.mContext_PhotoSelection = mContext;
        this.arrayListPhoto_PhotoSelection = arrayListPhoto;
        this.mListener_PhotoSelection = listener;
        setHasStableIds(true);
    }

    @Override
    public int getItemCount() {
        if (arrayListPhoto_PhotoSelection == null) {
            return 0;
        } else {
            return arrayListPhoto_PhotoSelection.size();
        }
    }

    @Override
    public boolean onCheckCanStartDrag(@NonNull ViewHolder holder,
                                       int position,
                                       int x,
                                       int y) {
        return true;
    }

    @Nullable
    @Override
    public ItemDraggableRange onGetItemDraggableRange(
            @NonNull ViewHolder holder,
            int position) {
        return null;
    }

    @Override
    public void onMoveItem(int fromPosition,
                           int toPosition) {
        Collections.swap(arrayListPhoto_PhotoSelection,
                fromPosition,
                toPosition);
    }

    @Override
    public boolean onCheckCanDrop(int draggingPosition,
                                  int dropPosition) {
        return true;
    }

    @Override
    public void onItemDragStarted(int position) {
        notifyDataSetChanged();
    }

    @Override
    public void onItemDragFinished(int fromPosition,
                                   int toPosition,
                                   boolean result) {
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,
                                 int position) {

        PhotoModel photo =
                arrayListPhoto_PhotoSelection.get(position);

        holder.tvNumber_PhotoSelection
                .setText(String.valueOf(holder.getAdapterPosition()));

        Glide.with(mContext_PhotoSelection)
                .load(photo.getFilePath_PhotoModel())
                .into(holder.imgPhoto_PhotoSelection);

        holder.imgRemove_PhotoSelection
                .setOnClickListener(view -> {
                    if (mListener_PhotoSelection != null) {
                        mListener_PhotoSelection.onRemoveListener(
                                holder.getAdapterPosition());
                    }
                });
    }

    @Override
    public long getItemId(int position) {
        return arrayListPhoto_PhotoSelection
                .get(position)
                .getId_PhotoModel();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_photo,
                        parent,
                        false);

        return new ViewHolder(view);
    }

    public static class ViewHolder
            extends AbstractDraggableItemViewHolder {

        AppCompatTextView tvNumber_PhotoSelection;
        FrameLayout loadingView_PhotoSelection;
        AppCompatImageView imgRemove_PhotoSelection;
        AppCompatImageView imgPhoto_PhotoSelection;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgPhoto_PhotoSelection =
                    itemView.findViewById(R.id.pageIv);

            imgRemove_PhotoSelection =
                    itemView.findViewById(R.id.removeIv);

            tvNumber_PhotoSelection =
                    itemView.findViewById(R.id.pageIndex);

            loadingView_PhotoSelection =
                    itemView.findViewById(R.id.progressFl);
        }
    }
}