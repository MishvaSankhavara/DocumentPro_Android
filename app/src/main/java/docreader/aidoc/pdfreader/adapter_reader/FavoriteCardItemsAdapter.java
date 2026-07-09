package docreader.aidoc.pdfreader.adapter_reader;

import android.app.Activity;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

import docreader.aidoc.pdfreader.R;
import docreader.aidoc.pdfreader.clickListener.DocClickListener;
import docreader.aidoc.pdfreader.database.DatabaseHelper;
import docreader.aidoc.pdfreader.model_reader.DocumentModel;
import docreader.aidoc.pdfreader.utils.Utils;

import java.util.ArrayList;

public class FavoriteCardItemsAdapter extends RecyclerView.Adapter<FavoriteCardItemsAdapter.ViewHolder> {

    private final DocClickListener listener;
    private final DatabaseHelper databaseHelper;
    private final ArrayList<DocumentModel> arrayList;
    private final Activity context;

    public FavoriteCardItemsAdapter(Activity context, DocClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.databaseHelper = DatabaseHelper.getInstance(context);
        this.arrayList = new ArrayList<>();
        refreshData();
    }

    public void refreshData() {
        this.arrayList.clear();
        this.arrayList.addAll(databaseHelper.getStarredDocuments_DatabaseHelper());
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return arrayList == null ? 0 : arrayList.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentModel document = arrayList.get(position);

        holder.tvName.setText(document.getFileName_DocModel());
        holder.tvSize.setText(Formatter.formatFileSize(context, document.getLength_DocModel()));

        holder.ivIcon.setImageResource(document.getSrcImage_DocModel());

        String fileName = document.getFileName_DocModel().toLowerCase();
        int bgColor = ContextCompat.getColor(context, R.color.app_background); // Default

        if (fileName.endsWith(".pdf")) {
            bgColor = ContextCompat.getColor(context, R.color.pdf_card_bg);
        } else if (fileName.endsWith(".doc") || fileName.endsWith(".docx")) {
            bgColor = ContextCompat.getColor(context, R.color.word_card_bg);
        } else if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx") || fileName.endsWith(".csv")) {
            bgColor = ContextCompat.getColor(context, R.color.excel_card_bg);
        } else if (fileName.endsWith(".ppt") || fileName.endsWith(".pptx")) {
            bgColor = ContextCompat.getColor(context, R.color.ppt_card_bg);
        }

        holder.cvIconContainer.setCardBackgroundColor(bgColor);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDocClick(document);
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cvIconContainer;
        ImageView ivIcon;
        ImageView ivStar;
        TextView tvName;
        TextView tvSize;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cvIconContainer = itemView.findViewById(R.id.cv_icon_container);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            ivStar = itemView.findViewById(R.id.iv_star);
            tvName = itemView.findViewById(R.id.tv_name);
            tvSize = itemView.findViewById(R.id.tv_size);
        }
    }
}
