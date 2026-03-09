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

import java.io.File;
import java.util.ArrayList;

public class RecentFilesAdapter extends RecyclerView.Adapter<RecentFilesAdapter.ViewHolder> {

    private final DocClickListener listener_RecentFiles;
    private DatabaseHelper databaseHelper_RecentFiles;
    private final ArrayList<DocumentModel> arrayList_RecentFiles;
    private final Activity mContext_RecentFiles;

    public RecentFilesAdapter(Activity mContext, DocClickListener listener) {
        this.mContext_RecentFiles = mContext;
        this.listener_RecentFiles = listener;
        this.arrayList_RecentFiles = DatabaseHelper.getInstance(mContext).getRecentDocuments_DatabaseHelper();
        this.databaseHelper_RecentFiles = DatabaseHelper.getInstance(mContext);
    }

    @Override
    public int getItemCount() {
        if (arrayList_RecentFiles == null) {
            return 0;
        } else {
            return arrayList_RecentFiles.size();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DocumentModel document = arrayList_RecentFiles.get(position);

        holder.tvFileName_RecentFiles.setText(document.getFileName_DocModel());

        holder.tvFileDate_RecentFiles.setText(Utils.formatDateToHumanReadable(document.getLastModified_DocModel()));

        holder.tvFileSize_RecentFiles.setText(Formatter.formatFileSize(mContext_RecentFiles, document.getLength_DocModel()));

        Glide.with(mContext_RecentFiles).load(document.getSrcImage_DocModel()).into(holder.imgIcon_RecentFiles);

        if (databaseHelper_RecentFiles.isStared_DatabaseHelper(document.getFileUri_DocModel())) {
            holder.imgFavorite_RecentFiles.setFrame(50);
        } else {
            holder.imgFavorite_RecentFiles.setFrame(0);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener_RecentFiles != null) {
                listener_RecentFiles.onDocClick(document);
            }
        });

        holder.imgMore_RecentFiles.setOnClickListener(v -> {
            Utils.showMoreDialog(mContext_RecentFiles, document, true, new MoreClickListener() {

                @Override
                public void onRenameListener(String newName) {

                    final File fileOldPdfName = new File(document.getFileUri_DocModel());

                    final String replaceName = document.getFileUri_DocModel().replace(Utils.removeExtension(document.getFileName_DocModel()), newName);

                    if (fileOldPdfName.renameTo(new File(replaceName))) {

                        if (databaseHelper_RecentFiles.isStared_DatabaseHelper(fileOldPdfName.getAbsolutePath())) {

                            databaseHelper_RecentFiles.updateStaredDocument_DatabaseHelper(document.getFileUri_DocModel(), replaceName);

                            DataSingletonFavorite.getInstance().addToFavorites(document);
                        }

                        databaseHelper_RecentFiles.updateHistory_DatabaseHelper(document.getFileUri_DocModel(), replaceName);

                        File newFile = new File(replaceName);

                        DocumentModel documentNew = new DocumentModel();

                        documentNew.setFileName_DocModel(newFile.getName());

                        documentNew.setFileUri_DocModel(newFile.getAbsolutePath());

                        documentNew.setLength_DocModel(newFile.length());

                        documentNew.setSrcImage_DocModel(getDocumentSrc(newFile));

                        documentNew.setLastModified_DocModel(newFile.lastModified());

                        int position = holder.getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION && position < arrayList_RecentFiles.size()) {

                            arrayList_RecentFiles.set(holder.getAdapterPosition(), documentNew);

                            notifyItemChanged(holder.getAdapterPosition());

                        } else {
                            Log.e("AdapterError", "Invalid position");
                        }

                    } else {
                        Toast.makeText(mContext_RecentFiles, R.string.dialog_rename_failed, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onDeleteListener() {

                    File fileToDelete = new File(document.getFileUri_DocModel());

                    if (fileToDelete.exists() && fileToDelete.delete()) {

                        MediaScannerConnection.scanFile(mContext_RecentFiles, new String[]{document.getFileUri_DocModel()}, null, null);

                        if (databaseHelper_RecentFiles.isStared_DatabaseHelper(document.getFileUri_DocModel())) {

                            databaseHelper_RecentFiles.removeStaredDocument_DatabaseHelper(document.getFileUri_DocModel());

                            DataSingletonFavorite.getInstance().removeFromFavorites(document);
                        }

                        if (databaseHelper_RecentFiles.isRecent_DatabaseHelper(document.getFileUri_DocModel())) {

                            databaseHelper_RecentFiles.removeRecentDocument_DatabaseHelper(document.getFileUri_DocModel());
                        }

                        int position = holder.getAdapterPosition();

                        try {
                            arrayList_RecentFiles.remove(position);

                            notifyItemRemoved(position);

                            notifyItemRangeChanged(position, arrayList_RecentFiles.size());

                        } catch (IndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onRemoveListener() {

                    if (databaseHelper_RecentFiles.isRecent_DatabaseHelper(document.getFileUri_DocModel())) {

                        arrayList_RecentFiles.remove(document);

                        notifyItemRemoved(holder.getAdapterPosition());

                        databaseHelper_RecentFiles.removeRecentDocument_DatabaseHelper(document.getFileUri_DocModel());

                        if (arrayList_RecentFiles.isEmpty()) {
                            notifyDataSetChanged();
                        }
                    }
                }
            });
        });

        holder.btnFavorite_RecentFiles.setOnClickListener(v -> {

            if (databaseHelper_RecentFiles.isStared_DatabaseHelper(document.getFileUri_DocModel())) {

                holder.imgFavorite_RecentFiles.setFrame(0);

                databaseHelper_RecentFiles.removeStaredDocument_DatabaseHelper(document.getFileUri_DocModel());

                DataSingletonFavorite.getInstance().removeFromFavorites(document);

            } else {

                databaseHelper_RecentFiles.addStaredDocument_DatabaseHelper(document.getFileUri_DocModel());

                DataSingletonFavorite.getInstance().addToFavorites(document);

                holder.imgFavorite_RecentFiles.playAnimation();

                holder.imgFavorite_RecentFiles.addAnimatorListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(Animator animation) {

                        holder.btnFavorite_RecentFiles.setClickable(true);

                        holder.btnFavorite_RecentFiles.setFocusable(true);
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {

                        holder.btnFavorite_RecentFiles.setClickable(false);

                        holder.btnFavorite_RecentFiles.setFocusable(false);
                    }
                });
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_document, parent, false);

        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        LottieAnimationView imgFavorite_RecentFiles;
        LinearLayout btnFavorite_RecentFiles;
        ImageView imgMore_RecentFiles;
        ImageView imgIcon_RecentFiles;
        TextView tvFileDate_RecentFiles;
        TextView tvFileSize_RecentFiles;
        TextView tvFileName_RecentFiles;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvFileName_RecentFiles = itemView.findViewById(R.id.tv_name);

            tvFileSize_RecentFiles = itemView.findViewById(R.id.tvFileSize);

            tvFileDate_RecentFiles = itemView.findViewById(R.id.tvFileDate);

            imgIcon_RecentFiles = itemView.findViewById(R.id.iv_icon);

            btnFavorite_RecentFiles = itemView.findViewById(R.id.ll_favorite);

            imgFavorite_RecentFiles = itemView.findViewById(R.id.lt_favorite);

            imgMore_RecentFiles = itemView.findViewById(R.id.iv_more);
        }
    }
}