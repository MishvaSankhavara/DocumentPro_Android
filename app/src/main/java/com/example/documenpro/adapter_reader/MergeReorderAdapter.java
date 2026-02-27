package com.example.documenpro.adapter_reader;

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
import com.example.documenpro.clickListener.ItemTouchAdapter;
import com.example.documenpro.clickListener.OnRemovePdfItemListener;
import com.example.documenpro.clickListener.OnDragStartListener;
import com.example.documenpro.model_reader.PDFReaderModel;
import com.example.documenpro.ui.activities.ReorderMergePdfActivity;
import com.example.documenpro.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;

public class MergeReorderAdapter extends RecyclerView.Adapter<MergeReorderAdapter.ViewHolder> implements ItemTouchAdapter {

    public OnRemovePdfItemListener listener_MergeReorder;
    public ArrayList<PDFReaderModel> pdfModels_MergeReorder;
    public OnDragStartListener mDragStartListener_MergeReorder;
    public ReorderMergePdfActivity mActivity_MergeReorder;

    @Override
    public void itemMove(int i, int i2) {
        Collections.swap(this.pdfModels_MergeReorder, i, i2);
        notifyItemMoved(i, i2);
    }

    @Override
    public void itemDismiss(int i) {

    }

    @Override
    public int getItemCount() {
        if (pdfModels_MergeReorder == null) {
            return 0;
        } else {
            return pdfModels_MergeReorder.size();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PDFReaderModel pdfModel_MergeReorder = pdfModels_MergeReorder.get(position);

        holder.ivLock_MergeReorder.setVisibility(GONE);
        holder.lock_line_MergeReorder.setVisibility(GONE);
        holder.tvName_MergeReorder.setText(pdfModel_MergeReorder.getName_PDFModel());
        holder.tvDate_MergeReorder.setText(Utils.formatDateToHumanReadable(pdfModel_MergeReorder.getLastModified_PDFModel()));
        holder.tvFileSize_MergeReorder.setText(Formatter.formatFileSize(mActivity_MergeReorder, pdfModel_MergeReorder.getLength_PDFModel()));

        holder.ivRemove_MergeReorder.setOnClickListener(view -> {
            if (listener_MergeReorder != null) {
                listener_MergeReorder.removePdfItemListener(pdfModel_MergeReorder, holder.getAdapterPosition());
            }
        });

        holder.ivDrag_MergeReorder.setOnTouchListener((view, motionEvent) -> {
            if (MotionEventCompat.getActionMasked(motionEvent) != 0) {
                return false;
            }
            mDragStartListener_MergeReorder.onDragStart(holder);
            return false;
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pdf_merge, parent, false));
    }

    public MergeReorderAdapter(ReorderMergePdfActivity mActivity, OnDragStartListener mDragStartListener, ArrayList<PDFReaderModel> pdfModels, OnRemovePdfItemListener listener) {
        this.listener_MergeReorder = listener;
        this.pdfModels_MergeReorder = pdfModels;
        this.mDragStartListener_MergeReorder = mDragStartListener;
        this.mActivity_MergeReorder = mActivity;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final View lock_line_MergeReorder;
        private final AppCompatImageView ivLock_MergeReorder;
        AppCompatImageView ivDrag_MergeReorder;
        AppCompatImageView ivRemove_MergeReorder;
        private final TextView tvFileSize_MergeReorder;
        private final TextView tvDate_MergeReorder;
        AppCompatTextView tvName_MergeReorder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDrag_MergeReorder = itemView.findViewById(R.id.item_drag_iv);
            ivRemove_MergeReorder = itemView.findViewById(R.id.item_pdf_remove);
            tvFileSize_MergeReorder = itemView.findViewById(R.id.tvFileSize);
            tvDate_MergeReorder = itemView.findViewById(R.id.tvFileDate);
            tvName_MergeReorder = itemView.findViewById(R.id.tv_name);
            lock_line_MergeReorder = itemView.findViewById(R.id.lock_line);
            ivLock_MergeReorder = itemView.findViewById(R.id.iv_lock);
        }
    }
}