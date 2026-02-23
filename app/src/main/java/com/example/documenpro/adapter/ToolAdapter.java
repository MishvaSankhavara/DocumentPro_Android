package com.example.documenpro.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.documenpro.GlobalConstant;
import com.example.documenpro.R;
import com.example.documenpro.listener.OnToolClickListener;
import com.example.documenpro.model.Tools;

import java.util.ArrayList;

public class ToolAdapter extends RecyclerView.Adapter<ToolAdapter.ViewHolder> {
    private final OnToolClickListener listener;
    private final ArrayList<Tools> arrayList;
    private final Context mContext;

    public ToolAdapter(Context mContext, OnToolClickListener listener) {
        this.listener = listener;
        this.mContext = mContext;
        this.arrayList = GlobalConstant.setToolsList();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tools, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tools toolType = arrayList.get(position);
        float scale = mContext.getResources().getDisplayMetrics().density;
        int padding = (int) (toolType.getPadding() * scale + 0.5f);
        holder.imgTool.setImageResource(toolType.getIcRes());
        holder.imgTool.setPadding(padding, padding, padding, padding);
        holder.tvTool.setText(mContext.getResources().getString(toolType.getNameTool()));
        holder.imgTool.setOnClickListener(view -> {
            if (listener != null) {
                listener.onTool(toolType);
            }
        });
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
        private final AppCompatImageView imgTool;
        private final TextView tvTool;
        private TextView tvToolDes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgTool = itemView.findViewById(R.id.iv_tools_icon);
            tvTool = itemView.findViewById(R.id.tvTools);
        }
    }
}
