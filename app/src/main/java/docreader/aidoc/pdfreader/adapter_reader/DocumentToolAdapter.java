package docreader.aidoc.pdfreader.adapter_reader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import docreader.aidoc.pdfreader.AppGlobalConstants;
import docreader.aidoc.pdfreader.R;
import docreader.aidoc.pdfreader.clickListener.OnToolTapListener;
import docreader.aidoc.pdfreader.model_reader.ToolsModel;
import com.google.android.material.card.MaterialCardView;

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

        holder.tvTool_DocumentTool.setText(
                mContext_DocumentTool.getResources().getString(toolType.getNameTool_toolModel()));

        holder.imgTool_DocumentTool.setImageResource(toolType.getIcRes_toolModel());
        // Background is now handled via XML using app_card_bg for theme support

        holder.itemView.setOnClickListener(view -> {
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

    public DocumentToolAdapter(Context mContext, ArrayList<ToolsModel> list, OnToolTapListener listener) {
        this.mContext_DocumentTool = mContext;
        this.arrayList_DocumentTool = list;
        this.listener_DocumentTool = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTool_DocumentTool;
        private final AppCompatImageView imgTool_DocumentTool;
        private final MaterialCardView cvToolBg_DocumentTool;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTool_DocumentTool = itemView.findViewById(R.id.tv_tools_name);
            imgTool_DocumentTool = itemView.findViewById(R.id.iv_tools_icon);
            cvToolBg_DocumentTool = itemView.findViewById(R.id.cv_tools_bg);
        }
    }
}