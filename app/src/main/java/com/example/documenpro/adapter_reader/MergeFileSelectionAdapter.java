package com.example.documenpro.adapter_reader;

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

public class MergeFileSelectionAdapter extends RecyclerView.Adapter<MergeFileSelectionAdapter.ViewHolder> {

    private final MergeChooseListener mListenerMergeFileSelection;
    private final ArrayList<PDFModel> originalDataMergeFileSelection;
    private final Context mContextMergeFileSelection;

    public MergeFileSelectionAdapter(Context mContext,
                                     ArrayList<PDFModel> originalData,
                                     MergeChooseListener listener) {
        this.mContextMergeFileSelection = mContext;
        this.originalDataMergeFileSelection = originalData;
        this.mListenerMergeFileSelection = listener;
    }

    public ArrayList<PDFModel> getSelected_MergeFileSelection() {
        ArrayList<PDFModel> selected = new ArrayList<>();
        for (int i = 0; i < originalDataMergeFileSelection.size(); i++) {
            if (originalDataMergeFileSelection.get(i).isChecked()) {
                selected.add(originalDataMergeFileSelection.get(i));
            }
        }
        return selected;
    }

    @Override
    public int getItemCount() {
        if (originalDataMergeFileSelection == null) {
            return 0;
        } else {
            return originalDataMergeFileSelection.size();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        PDFModel pdfModel = originalDataMergeFileSelection.get(position);

        holder.tvName_MergeFileSelection.setText(pdfModel.getName());
        holder.ivLock_MergeFileSelection.setVisibility(GONE);
        holder.lock_line_MergeFileSelection.setVisibility(GONE);
        holder.cbSelect_MergeFileSelection.setVisibility(View.VISIBLE);

        holder.tvDate_MergeFileSelection.setText(
                Utils.formatDateToHumanReadable(pdfModel.getLastModified()));

        holder.tvFileSize_MergeFileSelection.setText(
                Formatter.formatFileSize(
                        mContextMergeFileSelection,
                        pdfModel.getLength()));

        holder.cbSelect_MergeFileSelection.setChecked(pdfModel.isChecked());

        holder.rootView_MergeFileSelection.setBackground(
                pdfModel.isChecked()
                        ? ResourcesCompat.getDrawable(
                        mContextMergeFileSelection.getResources(),
                        R.drawable.bg_selected,
                        null)
                        : ResourcesCompat.getDrawable(
                        mContextMergeFileSelection.getResources(),
                        R.drawable.bg_transparent,
                        null)
        );

        holder.cbSelect_MergeFileSelection.setClickable(false);
        holder.cbSelect_MergeFileSelection.setFocusable(false);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pdfModel.setChecked(!pdfModel.isChecked());
                holder.cbSelect_MergeFileSelection.setChecked(pdfModel.isChecked());

                holder.rootView_MergeFileSelection.setBackground(
                        pdfModel.isChecked()
                                ? ResourcesCompat.getDrawable(
                                mContextMergeFileSelection.getResources(),
                                R.drawable.bg_selected,
                                null)
                                : ResourcesCompat.getDrawable(
                                mContextMergeFileSelection.getResources(),
                                R.drawable.bg_transparent,
                                null)
                );

                if (mListenerMergeFileSelection != null) {
                    mListenerMergeFileSelection.onMergeChoose(holder.getAdapterPosition());
                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pdf_select, parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final View lock_line_MergeFileSelection;
        private final AppCompatImageView ivLock_MergeFileSelection;
        private final AppCompatCheckBox cbSelect_MergeFileSelection;
        private final TextView tvFileSize_MergeFileSelection;
        private final TextView tvDate_MergeFileSelection;
        private final TextView tvName_MergeFileSelection;
        ConstraintLayout rootView_MergeFileSelection;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivLock_MergeFileSelection =
                    itemView.findViewById(R.id.iv_lock);

            lock_line_MergeFileSelection =
                    itemView.findViewById(R.id.lock_line);

            rootView_MergeFileSelection =
                    itemView.findViewById(R.id.root);

            tvName_MergeFileSelection =
                    itemView.findViewById(R.id.tv_name);

            tvDate_MergeFileSelection =
                    itemView.findViewById(R.id.tvFileDate);

            tvFileSize_MergeFileSelection =
                    itemView.findViewById(R.id.tvFileSize);

            cbSelect_MergeFileSelection =
                    itemView.findViewById(R.id.iv_select);
        }
    }
}