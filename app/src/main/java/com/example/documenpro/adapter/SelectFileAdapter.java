package com.example.documenpro.adapter;

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
import com.example.documenpro.listener.ItemSelectListener;
import com.example.documenpro.model.Document;
import com.example.documenpro.utils.Utils;

import java.util.ArrayList;

public class SelectFileAdapter extends RecyclerView.Adapter<SelectFileAdapter.ViewHolder> {
    private final Context mContext;
    private final ArrayList<Document> arrayList;

    private final ItemSelectListener listener;
    public boolean isSelectedAll = false;

    public SelectFileAdapter(Context mContext, ArrayList<Document> arrayList, ItemSelectListener listener) {
        this.mContext = mContext;
        this.arrayList = arrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_document_select, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Document document = arrayList.get(position);
        holder.tvFileName.setText(document.getFileName());
        holder.tvFileDate.setText(Utils.formatDateToHumanReadable(document.getLastModified()));
        holder.tvFileSize.setText(Formatter.formatFileSize(mContext, document.getLength()));
        Glide.with(mContext).load(document.getSrcImage()).into(holder.imgIcon);
        holder.imgCheck.setClickable(false);
        holder.imgCheck.setFocusable(false);
        holder.itemView.setOnClickListener(v -> {
            document.setChecked(!document.isChecked());
            if (document.isChecked()) {
                holder.imgCheck.setImageResource(R.drawable.ic_check);
            } else {
                holder.imgCheck.setImageResource(R.drawable.ic_un_check);
            }
            if (listener != null) {
                listener.onItemSelect();
            }
        });
        holder.imgCheck.setImageResource(document.isChecked() ? R.drawable.ic_check : R.drawable.ic_un_check);
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
        TextView tvFileName;
        TextView tvFileSize;
        TextView tvFileDate;
        ImageView imgIcon;

        ImageView imgCheck;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFileName = itemView.findViewById(R.id.tv_name);
            tvFileSize = itemView.findViewById(R.id.tvFileSize);
            tvFileDate = itemView.findViewById(R.id.tvFileDate);
            imgIcon = itemView.findViewById(R.id.iv_icon);
            imgCheck = itemView.findViewById(R.id.iv_select);
        }
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

    public ArrayList<Document> getSelected() {
        ArrayList<Document> selected = new ArrayList<>();
        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).isChecked()) {
                selected.add(arrayList.get(i));
            }
        }
        return selected;
    }
}
