package com.example.documenpro.adapter;

import static android.view.View.GONE;

import android.annotation.SuppressLint;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.documenpro.R;
import com.example.documenpro.listener.ItemTouchHelperAdapter;
import com.example.documenpro.listener.OnRemovePdfItem;
import com.example.documenpro.listener.OnStartDragListener;
import com.example.documenpro.model.PDFModel;
import com.example.documenpro.ui.activities.MergeReorderActivity;
import com.example.documenpro.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;

public class ReorderMergeAdapter extends RecyclerView.Adapter<ReorderMergeAdapter.ViewHolder> implements ItemTouchHelperAdapter {
    public MergeReorderActivity mActivity;
    public OnStartDragListener mDragStartListener;
    public ArrayList<PDFModel> pdfModels;
    public OnRemovePdfItem listener;

    public ReorderMergeAdapter(MergeReorderActivity mActivity, OnStartDragListener mDragStartListener, ArrayList<PDFModel> pdfModels, OnRemovePdfItem listener) {
        this.mActivity = mActivity;
        this.mDragStartListener = mDragStartListener;
        this.pdfModels = pdfModels;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pdf_merge, parent, false));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PDFModel pdfModel = pdfModels.get(position);

        holder.ivLock.setVisibility(GONE);
        holder.lock_line.setVisibility(GONE);
        holder.tvName.setText(pdfModel.getName());
        holder.tvDate.setText(Utils.formatDateToHumanReadable(pdfModel.getLastModified()));
        holder.tvFileSize.setText(Formatter.formatFileSize(mActivity, pdfModel.getLength()));
        holder.ivDrag.setOnTouchListener((view, motionEvent) -> {
            if (MotionEventCompat.getActionMasked(motionEvent) != 0) {
                return false;
            }
            mDragStartListener.onStartDrag(holder);
            return false;
        });
        holder.ivRemove.setOnClickListener(view -> {
            if (listener != null) {
                listener.removePdfItem(pdfModel, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (pdfModels == null) {
            return 0;
        } else {
            return pdfModels.size();
        }
    }

    @Override
    public void onItemDismiss(int i) {

    }

    @Override
    public void onItemMove(int i, int i2) {
        Collections.swap(this.pdfModels, i, i2);
        notifyItemMoved(i, i2);
//        mActivity.saveList();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView tvName;
        private final TextView tvDate;
        private final TextView tvFileSize;
        AppCompatImageView ivRemove;
        AppCompatImageView ivDrag;
        private final AppCompatImageView ivLock;
        private final View lock_line;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivLock = itemView.findViewById(R.id.iv_lock);
            lock_line = itemView.findViewById(R.id.lock_line);
            tvName = itemView.findViewById(R.id.tv_name);
            tvDate = itemView.findViewById(R.id.tvFileDate);
            tvFileSize = itemView.findViewById(R.id.tvFileSize);
            ivRemove = itemView.findViewById(R.id.item_pdf_remove);
            ivDrag = itemView.findViewById(R.id.item_drag_iv);
        }
    }
}
