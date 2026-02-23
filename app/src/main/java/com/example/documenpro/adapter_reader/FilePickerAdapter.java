package com.example.documenpro.adapter_reader;

import android.content.Context;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.documenpro.R;
import com.example.documenpro.clickListener.OnItemSelectListener;
import com.example.documenpro.model_reader.DocumentModel;
import com.example.documenpro.utils.Utils;

import java.util.ArrayList;

public class FilePickerAdapter extends RecyclerView.Adapter<FilePickerAdapter.ViewHolder> {

    public boolean isSelectedAll_FilePicker = false;
    private final OnItemSelectListener listener_FilePicker;
    private final ArrayList<DocumentModel> arrayList_FilePicker;
    private final Context mContext_FilePicker;

    public ArrayList<DocumentModel> getSelected_FilePicker() {
        ArrayList<DocumentModel> selected_FilePicker = new ArrayList<>();
        for (int i = 0; i < arrayList_FilePicker.size(); i++) {
            if (arrayList_FilePicker.get(i).isChecked_DocModel()) {
                selected_FilePicker.add(arrayList_FilePicker.get(i));
            }
        }
        return selected_FilePicker;
    }

    public void setUnSelectedAll_FilePicker() {
        for (int i = 0; i < arrayList_FilePicker.size(); i++) {
            arrayList_FilePicker.get(i).setChecked_DocModel(false);
        }
        isSelectedAll_FilePicker = false;
        notifyDataSetChanged();
    }

    public void setSelectedAll_FilePicker() {
        for (int i = 0; i < arrayList_FilePicker.size(); i++) {
            arrayList_FilePicker.get(i).setChecked_DocModel(true);
        }
        isSelectedAll_FilePicker = true;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (arrayList_FilePicker == null) {
            return 0;
        } else {
            return arrayList_FilePicker.size();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentModel document = arrayList_FilePicker.get(position);

        holder.imgCheck_FilePicker.setImageResource(
                document.isChecked_DocModel() ? R.drawable.ic_check : R.drawable.ic_un_check
        );

        holder.tvFileSize_FilePicker.setText(
                Formatter.formatFileSize(mContext_FilePicker, document.getLength_DocModel())
        );

        holder.tvFileDate_FilePicker.setText(
                Utils.formatDateToHumanReadable(document.getLastModified_DocModel())
        );

        holder.tvFileName_FilePicker.setText(document.getFileName_DocModel());

        Glide.with(mContext_FilePicker)
                .load(document.getSrcImage_DocModel())
                .into(holder.imgIcon_FilePicker);

        holder.imgCheck_FilePicker.setClickable(false);
        holder.imgCheck_FilePicker.setFocusable(false);

        holder.itemView.setOnClickListener(v -> {
            document.setChecked_DocModel(!document.isChecked_DocModel());

            if (document.isChecked_DocModel()) {
                holder.imgCheck_FilePicker.setImageResource(R.drawable.ic_check);
            } else {
                holder.imgCheck_FilePicker.setImageResource(R.drawable.ic_un_check);
            }

            if (listener_FilePicker != null) {
                listener_FilePicker.onItemSelected();
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_document_select, parent, false);
        return new ViewHolder(view);
    }

    public FilePickerAdapter(Context mContext, ArrayList<DocumentModel> arrayList, OnItemSelectListener listener) {
        this.listener_FilePicker = listener;
        this.arrayList_FilePicker = arrayList;
        this.mContext_FilePicker = mContext;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgCheck_FilePicker;
        ImageView imgIcon_FilePicker;
        TextView tvFileDate_FilePicker;
        TextView tvFileSize_FilePicker;
        TextView tvFileName_FilePicker;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCheck_FilePicker = itemView.findViewById(R.id.iv_select);
            imgIcon_FilePicker = itemView.findViewById(R.id.iv_icon);
            tvFileDate_FilePicker = itemView.findViewById(R.id.tvFileDate);
            tvFileSize_FilePicker = itemView.findViewById(R.id.tvFileSize);
            tvFileName_FilePicker = itemView.findViewById(R.id.tv_name);
        }
    }
}