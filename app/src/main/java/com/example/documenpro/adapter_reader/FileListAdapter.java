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

import java.io.File;
import java.util.ArrayList;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {

    private final DocClickListener listener_FileList;
    DatabaseHelper databaseHelper_FileList;
    private ArrayList<DocumentModel> arrayList_FileList;
    private final Activity mContext_FileList;

    public FileListAdapter(Activity mContext,
                           ArrayList<DocumentModel> arrayList,
                           DocClickListener listener) {
        this.mContext_FileList = mContext;
        this.arrayList_FileList = arrayList;
        this.listener_FileList = listener;
        this.databaseHelper_FileList = DatabaseHelper.getInstance(mContext);
    }

    @Override
    public int getItemCount() {
        return arrayList_FileList.size();
    }

    public void filter(ArrayList<DocumentModel> list) {
        arrayList_FileList = list;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,
                                 int position) {
        DocumentModel document = arrayList_FileList.get(position);
        holder.bindDocument_FileList(document);
    }

    public void setData(ArrayList<DocumentModel> dataList) {
        this.arrayList_FileList = dataList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_document, parent, false);

        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LottieAnimationView imgFavorite_FileList;
        LinearLayout btnFavorite_FileList;
        ImageView imgMore_FileList;
        ImageView imgIcon_FileList;
        TextView tvFileDate_FileList;
        TextView tvFileSize_FileList;
        TextView tvFileName_FileList;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvFileName_FileList =
                    itemView.findViewById(R.id.tv_name);

            tvFileSize_FileList =
                    itemView.findViewById(R.id.tvFileSize);

            tvFileDate_FileList =
                    itemView.findViewById(R.id.tvFileDate);

            imgIcon_FileList =
                    itemView.findViewById(R.id.iv_icon);

            btnFavorite_FileList =
                    itemView.findViewById(R.id.ll_favorite);

            imgFavorite_FileList =
                    itemView.findViewById(R.id.lt_favorite);

            imgMore_FileList =
                    itemView.findViewById(R.id.iv_more);
        }

        public void bindDocument_FileList(DocumentModel document_FileList) {

            tvFileName_FileList.setText(document_FileList.getFileName_DocModel());

            tvFileDate_FileList.setText(
                    Utils.formatDateToHumanReadable(
                            document_FileList.getLastModified_DocModel()));

            tvFileSize_FileList.setText(
                    Formatter.formatFileSize(
                            mContext_FileList,
                            document_FileList.getLength_DocModel()));

            Glide.with(mContext_FileList)
                    .load(document_FileList.getSrcImage_DocModel())
                    .into(imgIcon_FileList);

            if (databaseHelper_FileList.isStared_DatabaseHelper(
                    document_FileList.getFileUri_DocModel())) {
                imgFavorite_FileList.setFrame(50);
            } else {
                imgFavorite_FileList.setFrame(0);
            }

            itemView.setOnClickListener(v -> {
                if (listener_FileList != null) {
                    listener_FileList.onDocClick(document_FileList);
                }
            });

            imgMore_FileList.setOnClickListener(v ->
                    Utils.showMoreDialog(
                            mContext_FileList,
                            document_FileList,
                            false,
                            new MoreClickListener() {

                                @Override
                                public void onRenameListener(String newName) {

                                    final File fileOldPdfName =
                                            new File(document_FileList.getFileUri_DocModel());

                                    final String replaceName =
                                            document_FileList.getFileUri_DocModel()
                                                    .replace(
                                                            Utils.removeExtension(
                                                                    document_FileList.getFileName_DocModel()),
                                                            newName);

                                    if (fileOldPdfName.renameTo(
                                            new File(replaceName))) {

                                        if (databaseHelper_FileList.isStared_DatabaseHelper(
                                                fileOldPdfName.getAbsolutePath())) {

                                            databaseHelper_FileList.updateStaredDocument_DatabaseHelper(
                                                    document_FileList.getFileUri_DocModel(),
                                                    replaceName);
                                        }

                                        if (databaseHelper_FileList.isRecent_DatabaseHelper(
                                                fileOldPdfName.getAbsolutePath())) {

                                            databaseHelper_FileList.updateHistory_DatabaseHelper(
                                                    document_FileList.getFileUri_DocModel(),
                                                    replaceName);
                                        }

                                        File newFile = new File(replaceName);

                                        DocumentModel documentNew = new DocumentModel();
                                        documentNew.setFileName_DocModel(newFile.getName());
                                        documentNew.setFileUri_DocModel(newFile.getAbsolutePath());
                                        documentNew.setLength_DocModel(newFile.length());
                                        documentNew.setSrcImage_DocModel(getDocumentSrc(newFile));
                                        documentNew.setLastModified_DocModel(newFile.lastModified());

                                        int position = getAdapterPosition();

                                        if (position != RecyclerView.NO_POSITION
                                                && position < arrayList_FileList.size()) {

                                            arrayList_FileList.set(position, documentNew);
                                            notifyItemChanged(position);

                                        } else {
                                            Log.e("AdapterError",
                                                    "Invalid position or arraylist size");
                                        }

                                    } else {
                                        Toast.makeText(
                                                mContext_FileList,
                                                R.string.dialog_rename_failed,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onDeleteListener() {

                                    File fileToDelete =
                                            new File(document_FileList.getFileUri_DocModel());

                                    if (fileToDelete.exists()) {

                                        if (fileToDelete.delete()) {

                                            MediaScannerConnection.scanFile(
                                                    mContext_FileList,
                                                    new String[]{
                                                            document_FileList.getFileUri_DocModel()},
                                                    null,
                                                    null);

                                            if (databaseHelper_FileList.isStared_DatabaseHelper(
                                                    document_FileList.getFileUri_DocModel())) {

                                                databaseHelper_FileList.removeStaredDocument_DatabaseHelper(
                                                        document_FileList.getFileUri_DocModel());
                                            }

                                            if (databaseHelper_FileList.isRecent_DatabaseHelper(
                                                    document_FileList.getFileUri_DocModel())) {

                                                databaseHelper_FileList.removeRecentDocument_DatabaseHelper(
                                                        document_FileList.getFileUri_DocModel());
                                            }

                                            int position = getAdapterPosition();

                                            try {
                                                arrayList_FileList.remove(position);
                                                notifyItemRemoved(position);
                                                notifyItemRangeChanged(
                                                        position,
                                                        arrayList_FileList.size());
                                            } catch (IndexOutOfBoundsException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onRemoveListener() {
                                }
                            }));

            btnFavorite_FileList.setOnClickListener(v -> {

                if (databaseHelper_FileList.isStared_DatabaseHelper(
                        document_FileList.getFileUri_DocModel())) {

                    imgFavorite_FileList.setFrame(0);
                    databaseHelper_FileList.removeStaredDocument_DatabaseHelper(
                            document_FileList.getFileUri_DocModel());

                } else {

                    databaseHelper_FileList.addStaredDocument_DatabaseHelper(
                            document_FileList.getFileUri_DocModel());

                    imgFavorite_FileList.playAnimation();

                    imgFavorite_FileList.addAnimatorListener(
                            new AnimatorListenerAdapter() {

                                @Override
                                public void onAnimationStart(Animator animation) {
                                    btnFavorite_FileList.setClickable(false);
                                    btnFavorite_FileList.setFocusable(false);
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    btnFavorite_FileList.setClickable(true);
                                    btnFavorite_FileList.setFocusable(true);
                                }
                            });
                }
            });
        }
    }
}