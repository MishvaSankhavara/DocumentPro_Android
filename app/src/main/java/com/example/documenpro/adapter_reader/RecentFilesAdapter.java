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
import com.example.documenpro.db.DbHelper;
import com.example.documenpro.listener.DocumentClickListener;
import com.example.documenpro.listener.MoreListener;
import com.example.documenpro.model.Document;
import com.example.documenpro.utils.Utils;
import com.example.documenpro.viewmodel.FavoriteDataSingleton;

import java.io.File;
import java.util.ArrayList;

public class RecentFilesAdapter extends RecyclerView.Adapter<RecentFilesAdapter.ViewHolder> {

    private final DocumentClickListener listener_RecentFiles;
    private DbHelper dbHelper_RecentFiles;
    private final ArrayList<Document> arrayList_RecentFiles;
    private final Activity mContext_RecentFiles;

    public RecentFilesAdapter(Activity mContext, DocumentClickListener listener) {
        this.mContext_RecentFiles = mContext;
        this.listener_RecentFiles = listener;
        this.arrayList_RecentFiles = DbHelper.getInstance(mContext).getRecentDocuments();
        this.dbHelper_RecentFiles = DbHelper.getInstance(mContext);
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

        Document document = arrayList_RecentFiles.get(position);

        holder.tvFileName_RecentFiles.setText(document.getFileName());

        holder.tvFileDate_RecentFiles.setText(Utils.formatDateToHumanReadable(document.getLastModified()));

        holder.tvFileSize_RecentFiles.setText(Formatter.formatFileSize(mContext_RecentFiles, document.getLength()));

        Glide.with(mContext_RecentFiles).load(document.getSrcImage()).into(holder.imgIcon_RecentFiles);

        if (dbHelper_RecentFiles.isStared(document.getFileUri())) {
            holder.imgFavorite_RecentFiles.setFrame(50);
        } else {
            holder.imgFavorite_RecentFiles.setFrame(0);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener_RecentFiles != null) {
                listener_RecentFiles.onDocument(document);
            }
        });

        holder.imgMore_RecentFiles.setOnClickListener(v -> {
            Utils.showMoreDialog(mContext_RecentFiles, document, true, new MoreListener() {

                @Override
                public void onRename(String newName) {

                    final File fileOldPdfName = new File(document.getFileUri());

                    final String replaceName = document.getFileUri().replace(Utils.removeExtension(document.getFileName()), newName);

                    if (fileOldPdfName.renameTo(new File(replaceName))) {

                        if (dbHelper_RecentFiles.isStared(fileOldPdfName.getAbsolutePath())) {

                            dbHelper_RecentFiles.updateStaredDocument(document.getFileUri(), replaceName);

                            FavoriteDataSingleton.getInstance().addFavoriteDocument(document);
                        }

                        dbHelper_RecentFiles.updateHistory(document.getFileUri(), replaceName);

                        File newFile = new File(replaceName);

                        Document documentNew = new Document();

                        documentNew.setFileName(newFile.getName());

                        documentNew.setFileUri(newFile.getAbsolutePath());

                        documentNew.setLength(newFile.length());

                        documentNew.setSrcImage(getDocumentSrc(newFile));

                        documentNew.setLastModified(newFile.lastModified());

                        int position = holder.getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION && position < arrayList_RecentFiles.size()) {

                            arrayList_RecentFiles.set(holder.getAdapterPosition(), documentNew);

                            notifyItemChanged(holder.getAdapterPosition());

                        } else {
                            Log.e("AdapterError", "Invalid position");
                        }

                    } else {
                        Toast.makeText(mContext_RecentFiles, R.string.dialog_rename_fail, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onDelete() {

                    File fileToDelete = new File(document.getFileUri());

                    if (fileToDelete.exists() && fileToDelete.delete()) {

                        MediaScannerConnection.scanFile(mContext_RecentFiles, new String[]{document.getFileUri()}, null, null);

                        if (dbHelper_RecentFiles.isStared(document.getFileUri())) {

                            dbHelper_RecentFiles.removeStaredDocument(document.getFileUri());

                            FavoriteDataSingleton.getInstance().removeFavoriteDocument(document);
                        }

                        if (dbHelper_RecentFiles.isRecent(document.getFileUri())) {

                            dbHelper_RecentFiles.removeRecentDocument(document.getFileUri());
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
                public void onRemove() {

                    if (dbHelper_RecentFiles.isRecent(document.getFileUri())) {

                        arrayList_RecentFiles.remove(document);

                        notifyItemRemoved(holder.getAdapterPosition());

                        dbHelper_RecentFiles.removeRecentDocument(document.getFileUri());

                        if (arrayList_RecentFiles.isEmpty()) {
                            notifyDataSetChanged();
                        }
                    }
                }
            });
        });

        holder.btnFavorite_RecentFiles.setOnClickListener(v -> {

            if (dbHelper_RecentFiles.isStared(document.getFileUri())) {

                holder.imgFavorite_RecentFiles.setFrame(0);

                dbHelper_RecentFiles.removeStaredDocument(document.getFileUri());

                FavoriteDataSingleton.getInstance().removeFavoriteDocument(document);

            } else {

                dbHelper_RecentFiles.addStaredDocument(document.getFileUri());

                FavoriteDataSingleton.getInstance().addFavoriteDocument(document);

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