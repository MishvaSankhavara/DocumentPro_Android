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
import com.example.documenpro.listener.DocumentClickListener;
import com.example.documenpro.listener.MoreListener;
import com.example.documenpro.model.Document;
import com.example.documenpro.utils.Utils;
import com.example.documenpro.viewmodel.FavoriteDataSingleton;
import com.example.documenpro.viewmodel.RecentDataSingleton;

import java.io.File;
import java.util.ArrayList;

public class FavoriteItemsAdapter extends RecyclerView.Adapter<FavoriteItemsAdapter.ViewHolder> {

    private final DocumentClickListener listener_FavoriteItems;
    private DatabaseHelper databaseHelper_FavoriteItems;
    private final ArrayList<Document> arrayList_FavoriteItems;
    private final Activity mContext_FavoriteItems;

    public FavoriteItemsAdapter(Activity mContext,
                                DocumentClickListener listener) {
        this.mContext_FavoriteItems = mContext;
        this.listener_FavoriteItems = listener;
        this.arrayList_FavoriteItems =
                DatabaseHelper.getInstance(mContext).getStarredDocuments_DatabaseHelper();
        this.databaseHelper_FavoriteItems =
                DatabaseHelper.getInstance(mContext);
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

        Document document = arrayList_FavoriteItems.get(position);

        holder.tvFileName_FavoriteItems.setText(document.getFileName());
        holder.tvFileDate_FavoriteItems.setText(
                Utils.formatDateToHumanReadable(document.getLastModified()));
        holder.tvFileSize_FavoriteItems.setText(
                Formatter.formatFileSize(
                        mContext_FavoriteItems,
                        document.getLength()));

        Glide.with(mContext_FavoriteItems)
                .load(document.getSrcImage())
                .into(holder.imgIcon_FavoriteItems);

        if (databaseHelper_FavoriteItems.isStared_DatabaseHelper(document.getFileUri())) {
            holder.imgFavorite_FavoriteItems.setFrame(50);
        } else {
            holder.imgFavorite_FavoriteItems.setFrame(0);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener_FavoriteItems != null) {
                listener_FavoriteItems.onDocument(document);
            }
        });

        holder.imgMore_FavoriteItems.setOnClickListener(v -> {
            Utils.showMoreDialog(mContext_FavoriteItems,
                    document,
                    false,
                    new MoreListener() {

                        @Override
                        public void onRename(String newName) {

                            final File fileOldPdfName =
                                    new File(document.getFileUri());

                            final String replaceName =
                                    document.getFileUri().replace(
                                            Utils.removeExtension(document.getFileName()),
                                            newName);

                            if (fileOldPdfName.renameTo(new File(replaceName))) {

                                if (databaseHelper_FavoriteItems.isRecent_DatabaseHelper(
                                        fileOldPdfName.getAbsolutePath())) {

                                    databaseHelper_FavoriteItems.updateHistory_DatabaseHelper(
                                            document.getFileUri(),
                                            replaceName);

                                    RecentDataSingleton.getInstance()
                                            .addRecentDocument(document);
                                }

                                databaseHelper_FavoriteItems.updateStaredDocument_DatabaseHelper(
                                        document.getFileUri(),
                                        replaceName);

                                File newFile = new File(replaceName);

                                Document documentNew = new Document();
                                documentNew.setFileName(newFile.getName());
                                documentNew.setFileUri(newFile.getAbsolutePath());
                                documentNew.setLength(newFile.length());
                                documentNew.setSrcImage(getDocumentSrc(newFile));
                                documentNew.setLastModified(newFile.lastModified());

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
                                        R.string.dialog_rename_fail,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onDelete() {

                            File fileToDelete =
                                    new File(document.getFileUri());

                            if (fileToDelete.exists()) {

                                if (fileToDelete.delete()) {

                                    MediaScannerConnection.scanFile(
                                            mContext_FavoriteItems,
                                            new String[]{document.getFileUri()},
                                            null,
                                            null);

                                    if (databaseHelper_FavoriteItems.isStared_DatabaseHelper(
                                            document.getFileUri())) {

                                        databaseHelper_FavoriteItems.removeStaredDocument_DatabaseHelper(
                                                document.getFileUri());

                                        FavoriteDataSingleton.getInstance()
                                                .removeFavoriteDocument(document);
                                    }

                                    if (databaseHelper_FavoriteItems.isRecent_DatabaseHelper(
                                            document.getFileUri())) {

                                        databaseHelper_FavoriteItems.removeRecentDocument_DatabaseHelper(
                                                document.getFileUri());
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
                        public void onRemove() {
                        }
                    });
        });

        holder.btnFavorite_FavoriteItems.setOnClickListener(v -> {

            if (databaseHelper_FavoriteItems.isStared_DatabaseHelper(document.getFileUri())) {

                holder.imgFavorite_FavoriteItems.setFrame(0);
                arrayList_FavoriteItems.remove(document);
                notifyItemRemoved(holder.getAdapterPosition());

                databaseHelper_FavoriteItems.removeStaredDocument_DatabaseHelper(
                        document.getFileUri());

                if (arrayList_FavoriteItems.isEmpty()) {
                    notifyDataSetChanged();
                }

            } else {

                databaseHelper_FavoriteItems.addStaredDocument_DatabaseHelper(
                        document.getFileUri());

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

            if (databaseHelper_FavoriteItems.isRecent_DatabaseHelper(document.getFileUri())) {
                RecentDataSingleton.getInstance()
                        .addRecentDocument(document);
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

            btnFavorite_FavoriteItems =
                    itemView.findViewById(R.id.ll_favorite);

            imgFavorite_FavoriteItems =
                    itemView.findViewById(R.id.lt_favorite);

            imgMore_FavoriteItems =
                    itemView.findViewById(R.id.iv_more);
        }
    }
}