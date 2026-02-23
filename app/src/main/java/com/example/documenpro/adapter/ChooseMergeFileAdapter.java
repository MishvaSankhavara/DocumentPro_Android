package com.example.documenpro.adapter;

import static android.view.View.GONE;

import android.content.Context;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.documenpro.R;
import com.example.documenpro.listener.MergeChooseListener;
import com.example.documenpro.model.PDFModel;
import com.example.documenpro.utils.Utils;

import java.util.ArrayList;

public class ChooseMergeFileAdapter extends RecyclerView.Adapter<ChooseMergeFileAdapter.ViewHolder> {
    private final Context mContext;
    private final ArrayList<PDFModel> originalData;
    private final MergeChooseListener mListener;
    public boolean isSelectedAll = false;

    public ChooseMergeFileAdapter(Context mContext, ArrayList<PDFModel> originalData, MergeChooseListener listener) {
        this.mContext = mContext;
        this.originalData = originalData;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pdf_select, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PDFModel pdfModel = originalData.get(position);
        holder.tvName.setText(pdfModel.getName());
        holder.ivLock.setVisibility(GONE);
        holder.lock_line.setVisibility(GONE);
//        holder.btnFavorite.setVisibility(View.INVISIBLE);
//        holder.imgMore.setVisibility(View.GONE);
        holder.cbSelect.setVisibility(View.VISIBLE);
        holder.tvDate.setText(Utils.formatDateToHumanReadable(pdfModel.getLastModified()));
        holder.tvFileSize.setText(Formatter.formatFileSize(mContext, pdfModel.getLength()));
        holder.cbSelect.setChecked(pdfModel.isChecked());
        holder.rootView.setBackground(pdfModel.isChecked()
                ? ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.bg_selected, null)
                : ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.bg_transparent, null));


        holder.cbSelect.setClickable(false);
        holder.cbSelect.setFocusable(false);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdfModel.setChecked(!pdfModel.isChecked());
                holder.cbSelect.setChecked(pdfModel.isChecked());
                holder.rootView.setBackground(pdfModel.isChecked()
                        ? ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.bg_selected, null)
                        : ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.bg_transparent, null));

                if (mListener != null) {
                    mListener.onMergeChoose(holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (originalData == null) {
            return 0;
        } else {
            return originalData.size();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout rootView;
        private final TextView tvName;
        private final TextView tvDate;
        private final TextView tvFileSize;
        private final AppCompatCheckBox cbSelect;
        private final AppCompatImageView ivLock;
        private final View lock_line;
//        private final AppCompatImageView imgMore;
//        private final LinearLayout btnFavorite;
//        private final AppCompatImageView ivPdf;
//        private final AppCompatImageView ivLock;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivLock = itemView.findViewById(R.id.iv_lock);
            lock_line = itemView.findViewById(R.id.lock_line);
            rootView = itemView.findViewById(R.id.root);
            tvName = itemView.findViewById(R.id.tv_name);
            tvDate = itemView.findViewById(R.id.tvFileDate);
            tvFileSize = itemView.findViewById(R.id.tvFileSize);
            cbSelect = itemView.findViewById(R.id.iv_select);
//            imgMore = itemView.findViewById(R.id.item_pdf_more);
//            btnFavorite = itemView.findViewById(R.id.img_favorite);
//            ivLock = itemView.findViewById(R.id.ivLock);
//            ivPdf = itemView.findViewById(R.id.iv_icon);
        }
    }


    public void setSelectedAll() {
        for (int i = 0; i < originalData.size(); i++) {
            originalData.get(i).setChecked(true);
        }
        isSelectedAll = true;
        notifyDataSetChanged();
    }

    public void setUnSelectedAll() {
        for (int i = 0; i < originalData.size(); i++) {
            originalData.get(i).setChecked(false);
        }
        isSelectedAll = false;
        notifyDataSetChanged();
    }

    public ArrayList<PDFModel> getSelected() {
        ArrayList<PDFModel> selected = new ArrayList<>();
        for (int i = 0; i < originalData.size(); i++) {
            if (originalData.get(i).isChecked()) {
                selected.add(originalData.get(i));
            }
        }
        return selected;
    }
}
