package com.example.documenpro.adapter_reader;

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
import com.example.documenpro.clickListener.OnToolTapListener;
import com.example.documenpro.model_reader.ToolsModel;

import java.util.ArrayList;

public class DocumentToolAdapter extends RecyclerView.Adapter<DocumentToolAdapter.ViewHolder> {

    private final Context mContext_DocumentTool;
    private final ArrayList<ToolsModel> arrayList_DocumentTool;
    private final OnToolTapListener listener_DocumentTool;

    @Override
    public int getItemCount() {
        if (arrayList_DocumentTool == null) {
            return 0;
        } else {
            return arrayList_DocumentTool.size();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ToolsModel toolType = arrayList_DocumentTool.get(position);
        float scale = mContext_DocumentTool.getResources().getDisplayMetrics().density;
        int padding = (int) (toolType.getPadding_toolModel() * scale + 0.5f);

        holder.tvTool_DocumentTool.setText(
                mContext_DocumentTool.getResources().getString(toolType.getNameTool_toolModel())
        );

        holder.imgTool_DocumentTool.setPadding(padding, padding, padding, padding);
        holder.imgTool_DocumentTool.setImageResource(toolType.getIcRes_toolModel());

        holder.imgTool_DocumentTool.setOnClickListener(view -> {
            if (listener_DocumentTool != null) {
                listener_DocumentTool.onToolTap(toolType);
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tools, parent, false);
        return new ViewHolder(view);
    }

    public DocumentToolAdapter(Context mContext, OnToolTapListener listener) {
        this.mContext_DocumentTool = mContext;
        this.arrayList_DocumentTool = GlobalConstant.setToolsList();
        this.listener_DocumentTool = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTool_DocumentTool;
        private final AppCompatImageView imgTool_DocumentTool;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTool_DocumentTool = itemView.findViewById(R.id.tvTools);
            imgTool_DocumentTool = itemView.findViewById(R.id.iv_tools_icon);
        }
    }
}