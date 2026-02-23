package com.example.documenpro.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.documenpro.R;
import com.example.documenpro.listener.ChoosePdfListener;
import com.example.documenpro.model.PDFModel;
import com.example.documenpro.utils.Utils;

import java.util.ArrayList;

public class ChooseFileAdapter extends RecyclerView.Adapter<ChooseFileAdapter.ViewHolder> {
    private final Context mContext;
    private final ArrayList<PDFModel> originalData;
    private final ChoosePdfListener mListener;

    public ChooseFileAdapter(Context mContext, ArrayList<PDFModel> originalData, ChoosePdfListener mListener) {
        this.mContext = mContext;
        this.originalData = originalData;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pdf_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PDFModel pdfModel = originalData.get(position);
        if (pdfModel.isProtected()) {
            holder.ivPdf.setImageResource(R.drawable.ic_pdf_no);
            holder.ivLock.setVisibility(View.VISIBLE);
        } else {
            holder.ivPdf.setImageResource(R.drawable.ic_pdf_2);
            holder.ivLock.setVisibility(View.GONE);
        }
        holder.btnFavorite.setVisibility(View.GONE);
        holder.imgMore.setVisibility(View.INVISIBLE);
        holder.tvDate.setText(Utils.formatDateToHumanReadable(pdfModel.getLastModified()));
        holder.tvFileSize.setText(Formatter.formatFileSize(mContext, pdfModel.getLength()));
        holder.tvName.setText(pdfModel.getName());
        holder.itemView.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onPdfChoose(pdfModel, holder.getAdapterPosition());
            }
        });
    }
    public void updateData(PDFModel pdfModel, int position) {
        originalData.set(position, pdfModel);
        notifyItemChanged(position);


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
        private final TextView tvName;
        private final TextView tvDate;
        private final TextView tvFileSize;
        private final AppCompatImageView imgMore;
        private final LinearLayout btnFavorite;

        private final AppCompatImageView ivPdf;
        private final AppCompatImageView ivLock;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvDate = itemView.findViewById(R.id.tvFileDate);
            tvFileSize = itemView.findViewById(R.id.tvFileSize);
            imgMore = itemView.findViewById(R.id.iv_more);
            ivLock = itemView.findViewById(R.id.ivLock);
            ivPdf = itemView.findViewById(R.id.iv_icon);
            btnFavorite = itemView.findViewById(R.id.ll_favorite);
        }
    }
}
