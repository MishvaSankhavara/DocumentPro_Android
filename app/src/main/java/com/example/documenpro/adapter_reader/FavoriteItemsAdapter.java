package com.example.documenpro.adapter_reader;

import static com.example.documenpro.utils.Utils.getDocumentSrc;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.media.MediaScannerConnection;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.documenpro.R;
import com.example.documenpro.database.DatabaseHelper;
import com.example.documenpro.clickListener.DocClickListener;
import com.example.documenpro.clickListener.MoreClickListener;
import com.example.documenpro.model_reader.DocumentModel;
import com.example.documenpro.utils.Utils;
import com.example.documenpro.viewmodel.DataSingletonFavorite;
import com.example.documenpro.viewmodel.DataSingletonRecent;

import java.io.File;
import java.util.ArrayList;

public class FavoriteItemsAdapter extends RecyclerView.Adapter<FavoriteItemsAdapter.ViewHolder> {

    private final DocClickListener listener_FavoriteItems;
    private DatabaseHelper databaseHelper_FavoriteItems;
    private final ArrayList<DocumentModel> arrayList_FavoriteItems;
    private final Activity mContext_FavoriteItems;

    public FavoriteItemsAdapter(Activity mContext,
                                DocClickListener listener) {
        this.mContext_FavoriteItems = mContext;
        this.listener_FavoriteItems = listener;
        this.databaseHelper_FavoriteItems =
                DatabaseHelper.getInstance(mContext);
        this.arrayList_FavoriteItems = new ArrayList<>();
        refreshData();
    }

    public void refreshData() {
        this.arrayList_FavoriteItems.clear();
        this.arrayList_FavoriteItems.addAll(databaseHelper_FavoriteItems.getStarredDocuments_DatabaseHelper());
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (arrayList_FavoriteItems == null) {
            return 0;
        } else {
            return arrayList_FavoriteItems.size();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,
                                 int position) {

        DocumentModel document = arrayList_FavoriteItems.get(position);

        holder.tvFileName_FavoriteItems.setText(document.getFileName_DocModel());
        holder.tvFileDate_FavoriteItems.setText(
                Utils.formatDateToHumanReadable(document.getLastModified_DocModel()));
        holder.tvFileSize_FavoriteItems.setText(
                Formatter.formatFileSize(
                        mContext_FavoriteItems,
                        document.getLength_DocModel()));

        Glide.with(mContext_FavoriteItems)
                .load(document.getSrcImage_DocModel())
                .into(holder.imgIcon_FavoriteItems);

        String fileName = document.getFileName_DocModel().toLowerCase();
        int bgColor = ContextCompat.getColor(mContext_FavoriteItems, R.color.app_background); // Default

        if (fileName.endsWith(".pdf")) {
            bgColor = ContextCompat.getColor(mContext_FavoriteItems, R.color.pdf_card_bg);
        } else if (fileName.endsWith(".doc") || fileName.endsWith(".docx")) {
            bgColor = ContextCompat.getColor(mContext_FavoriteItems, R.color.word_card_bg);
        } else if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx") || fileName.endsWith(".csv")) {
            bgColor = ContextCompat.getColor(mContext_FavoriteItems, R.color.excel_card_bg);
        } else if (fileName.endsWith(".ppt") || fileName.endsWith(".pptx")) {
            bgColor = ContextCompat.getColor(mContext_FavoriteItems, R.color.ppt_card_bg);
        }

        holder.cvIcon_FavoriteItems.setCardBackgroundColor(bgColor);

        if (databaseHelper_FavoriteItems.isStared_DatabaseHelper(document.getFileUri_DocModel())) {
            holder.imgFavorite_FavoriteItems.setFrame(50);
        } else {
            holder.imgFavorite_FavoriteItems.setFrame(0);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener_FavoriteItems != null) {
                listener_FavoriteItems.onDocClick(document);
            }
        });

        holder.imgMore_FavoriteItems.setOnClickListener(v -> {
            Utils.showMoreDialog(mContext_FavoriteItems,
                    document,
                    false,
                    new MoreClickListener() {

                        @Override
                        public void onRenameListener(String newName) {

                            final File fileOldPdfName =
                                    new File(document.getFileUri_DocModel());

                            final String replaceName =
                                    document.getFileUri_DocModel().replace(
                                            Utils.removeExtension(document.getFileName_DocModel()),
                                            newName);

                            if (fileOldPdfName.renameTo(new File(replaceName))) {

                                if (databaseHelper_FavoriteItems.isRecent_DatabaseHelper(
                                        fileOldPdfName.getAbsolutePath())) {

                                    databaseHelper_FavoriteItems.updateHistory_DatabaseHelper(
                                            document.getFileUri_DocModel(),
                                            replaceName);

                                    DataSingletonRecent.getInstance()
                                            .addToRecent(document);
                                }

                                databaseHelper_FavoriteItems.updateStaredDocument_DatabaseHelper(
                                        document.getFileUri_DocModel(),
                                        replaceName);

                                File newFile = new File(replaceName);

                                DocumentModel documentNew = new DocumentModel();
                                documentNew.setFileName_DocModel(newFile.getName());
                                documentNew.setFileUri_DocModel(newFile.getAbsolutePath());
                                documentNew.setLength_DocModel(newFile.length());
                                documentNew.setSrcImage_DocModel(getDocumentSrc(newFile));
                                documentNew.setLastModified_DocModel(newFile.lastModified());

                                int pos = holder.getAdapterPosition();

                                if (pos != RecyclerView.NO_POSITION
                                        && pos < arrayList_FavoriteItems.size()) {

                                    arrayList_FavoriteItems.set(pos, documentNew);
                                    notifyItemChanged(pos);

                                } else {
                                    Log.e("AdapterError",
                                            "Invalid position or arraylist size");
                                }

                            } else {
                                Toast.makeText(
                                        mContext_FavoriteItems,
                                        R.string.dialog_rename_failed,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onDeleteListener() {

                            File fileToDelete =
                                    new File(document.getFileUri_DocModel());

                            if (fileToDelete.exists()) {

                                if (fileToDelete.delete()) {

                                    MediaScannerConnection.scanFile(
                                            mContext_FavoriteItems,
                                            new String[]{document.getFileUri_DocModel()},
                                            null,
                                            null);

                                    if (databaseHelper_FavoriteItems.isStared_DatabaseHelper(
                                            document.getFileUri_DocModel())) {

                                        databaseHelper_FavoriteItems.removeStaredDocument_DatabaseHelper(
                                                document.getFileUri_DocModel());

                                        DataSingletonFavorite.getInstance()
                                                .removeFromFavorites(document);
                                    }

                                    if (databaseHelper_FavoriteItems.isRecent_DatabaseHelper(
                                            document.getFileUri_DocModel())) {

                                        databaseHelper_FavoriteItems.removeRecentDocument_DatabaseHelper(
                                                document.getFileUri_DocModel());
                                    }

                                    int pos = holder.getAdapterPosition();

                                    try {
                                        arrayList_FavoriteItems.remove(pos);
                                        notifyItemRemoved(pos);
                                        notifyItemRangeChanged(
                                                pos,
                                                arrayList_FavoriteItems.size());
                                    } catch (IndexOutOfBoundsException exception) {
                                        exception.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onRemoveListener() {
                        }
                    });
        });

        holder.btnFavorite_FavoriteItems.setOnClickListener(v -> {

            if (databaseHelper_FavoriteItems.isStared_DatabaseHelper(document.getFileUri_DocModel())) {

                holder.imgFavorite_FavoriteItems.setFrame(0);
                arrayList_FavoriteItems.remove(document);
                notifyItemRemoved(holder.getAdapterPosition());

                databaseHelper_FavoriteItems.removeStaredDocument_DatabaseHelper(
                        document.getFileUri_DocModel());

                if (arrayList_FavoriteItems.isEmpty()) {
                    notifyDataSetChanged();
                }

            } else {

                databaseHelper_FavoriteItems.addStaredDocument_DatabaseHelper(
                        document.getFileUri_DocModel());

                holder.imgFavorite_FavoriteItems.playAnimation();

                holder.imgFavorite_FavoriteItems.addAnimatorListener(
                        new AnimatorListenerAdapter() {

                            @Override
                            public void onAnimationStart(Animator animation) {
                                holder.btnFavorite_FavoriteItems.setClickable(false);
                                holder.btnFavorite_FavoriteItems.setFocusable(false);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                holder.btnFavorite_FavoriteItems.setClickable(true);
                                holder.btnFavorite_FavoriteItems.setFocusable(true);
                            }
                        });
            }

            if (databaseHelper_FavoriteItems.isRecent_DatabaseHelper(document.getFileUri_DocModel())) {
                DataSingletonRecent.getInstance()
                        .addToRecent(document);
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_document, parent, false);

        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        LottieAnimationView imgFavorite_FavoriteItems;
        LinearLayout btnFavorite_FavoriteItems;
        ImageView imgMore_FavoriteItems;
        ImageView imgIcon_FavoriteItems;
        CardView cvIcon_FavoriteItems;
        TextView tvFileDate_FavoriteItems;
        TextView tvFileSize_FavoriteItems;
        TextView tvFileName_FavoriteItems;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvFileName_FavoriteItems =
                    itemView.findViewById(R.id.tv_name);

            tvFileSize_FavoriteItems =
                    itemView.findViewById(R.id.tvFileSize);

            tvFileDate_FavoriteItems =
                    itemView.findViewById(R.id.tvFileDate);

            imgIcon_FavoriteItems =
                    itemView.findViewById(R.id.iv_icon);

            cvIcon_FavoriteItems =
                    itemView.findViewById(R.id.cv_icon);

            btnFavorite_FavoriteItems =
                    itemView.findViewById(R.id.ll_favorite);

            imgFavorite_FavoriteItems =
                    itemView.findViewById(R.id.lt_favorite);

            imgMore_FavoriteItems =
                    itemView.findViewById(R.id.iv_more);
        }
    }
}