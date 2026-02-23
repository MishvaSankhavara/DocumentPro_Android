package com.example.documenpro.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.documenpro.R;
import com.example.documenpro.listener.ThumbnailClickListener;
import com.example.documenpro.model.PDFPage;

import java.util.ArrayList;

public class ThumbnailPdfAdapter extends RecyclerView.Adapter<ThumbnailPdfAdapter.ViewHolder> {

    public Context mContext;
    private final ArrayList<PDFPage> arrayList;
    private final ThumbnailClickListener mListener;

    public boolean isSelectedAll = false;


    public ThumbnailPdfAdapter(Context mContext, ArrayList<PDFPage> arrayList, ThumbnailClickListener listener) {
        this.mContext = mContext;
        this.arrayList = arrayList;
        this.mListener = listener;
    }

    @NonNull
    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_preview, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PDFPage pdfPage = arrayList.get(position);
        Glide.with(mContext).load(pdfPage.getThumbnailUri()).into(holder.imgThumbnail);
        holder.tvPageNumber.setText(String.valueOf(pdfPage.getPageNumber()));

        if (pdfPage.isChecked()) {
            holder.ivBorder.setVisibility(View.VISIBLE);
        } else {
            holder.ivBorder.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(view -> {
            pdfPage.setChecked(!pdfPage.isChecked());
            holder.ivBorder.setVisibility(pdfPage.isChecked() ? View.VISIBLE : View.GONE);
            if (mListener != null) {
                mListener.onChoosePdfSplit();
            }
        });

    }

    public void setSelectedAll() {
        for (int i = 0; i < arrayList.size(); i++) {
            arrayList.get(i).setChecked(true);
        }
        isSelectedAll = true;
        notifyDataSetChanged();
    }

    public void setUnSelectedAll() {
        for (int i = 0; i < arrayList.size(); i++) {
            arrayList.get(i).setChecked(false);
        }
        isSelectedAll = false;
        notifyDataSetChanged();
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
        AppCompatImageView ivBorder;
        AppCompatTextView tvPageNumber;
        AppCompatImageView imgThumbnail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivBorder = itemView.findViewById(R.id.pdf_select_foreground);
            tvPageNumber = itemView.findViewById(R.id.page_count);
            imgThumbnail = itemView.findViewById(R.id.item_icon);

        }
    }

    public ArrayList<Integer> getPageNumbers() {
        ArrayList<Integer> pageNumbers = new ArrayList<>();
        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).isChecked()) {
                pageNumbers.add(i);
            }
        }
        return pageNumbers;
    }

    public ArrayList<PDFPage> getSelected() {
        ArrayList<PDFPage> selected = new ArrayList<>();
        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).isChecked()) {
                selected.add(arrayList.get(i));
            }

        }
        return selected;
    }
}
