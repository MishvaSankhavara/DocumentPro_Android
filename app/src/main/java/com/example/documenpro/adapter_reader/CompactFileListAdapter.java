package com.example.documenpro.adapter_reader;

import android.media.MediaScannerConnection;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.documenpro.GlobalConstant;
import com.example.documenpro.R;
import com.example.documenpro.db.DataUpdateEvent;
import com.example.documenpro.db.DbHelper;
import com.example.documenpro.listener.MoreListener;
import com.example.documenpro.listener.OnPdfClickListener;
import com.example.documenpro.model.Document;
import com.example.documenpro.model.PDFModel;
import com.docpro.scanner.result.ResultViewerActivity;
import com.example.documenpro.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;

public class CompactFileListAdapter extends RecyclerView.Adapter<CompactFileListAdapter.ViewHolder> {

    private DbHelper dbHelper_FileListAdapter2;
    private OnPdfClickListener mListener_FileListAdapter2;
    private ArrayList<PDFModel> originalData_FileListAdapter2;
    private ResultViewerActivity mContext_FileListAdapter2;

    public CompactFileListAdapter(ResultViewerActivity mContext,
                                  ArrayList<PDFModel> originalData,
                                  OnPdfClickListener mListener) {
        this.mContext_FileListAdapter2 = mContext;
        this.originalData_FileListAdapter2 = originalData;
        this.mListener_FileListAdapter2 = mListener;
        this.dbHelper_FileListAdapter2 = DbHelper.getInstance(mContext);
    }

    @Override
    public int getItemCount() {
        if (originalData_FileListAdapter2 == null) {
            return 0;
        } else {
            return originalData_FileListAdapter2.size();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,
                                 int position) {
        PDFModel pdfModel =
                originalData_FileListAdapter2.get(position);
        holder.bindPdf_FileListAdapter2(pdfModel);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pdf,
                        parent,
                        false);

        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final View lock_line_FileListAdapter2;
        private final AppCompatImageView ivLock_FileListAdapter2;
        private final AppCompatImageView btnFavorite_FileListAdapter2;
        private final AppCompatImageView imgMore_FileListAdapter2;
        private final TextView tvFileSize_FileListAdapter2;
        private final TextView tvDate_FileListAdapter2;
        private final TextView tvName_FileListAdapter2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivLock_FileListAdapter2 =
                    itemView.findViewById(R.id.iv_lock);

            lock_line_FileListAdapter2 =
                    itemView.findViewById(R.id.lock_line);

            tvName_FileListAdapter2 =
                    itemView.findViewById(R.id.tv_name);

            tvDate_FileListAdapter2 =
                    itemView.findViewById(R.id.tvFileDate);

            tvFileSize_FileListAdapter2 =
                    itemView.findViewById(R.id.tvFileSize);

            imgMore_FileListAdapter2 =
                    itemView.findViewById(R.id.item_pdf_more);

            btnFavorite_FileListAdapter2 =
                    itemView.findViewById(R.id.img_favorite);
        }

        public void bindPdf_FileListAdapter2(PDFModel pdfModel) {

            Document document = new Document();
            document.setFileUri(pdfModel.getFileUri());
            document.setFileName(pdfModel.getName());
            document.setLastModified(pdfModel.getLastModified());
            document.setLength(pdfModel.getLength());

            if (pdfModel.isProtected()) {
                lock_line_FileListAdapter2.setVisibility(View.VISIBLE);
                ivLock_FileListAdapter2.setVisibility(View.VISIBLE);
            } else {
                ivLock_FileListAdapter2.setVisibility(View.GONE);
                lock_line_FileListAdapter2.setVisibility(View.GONE);
            }

            tvName_FileListAdapter2.setText(pdfModel.getName());
            tvDate_FileListAdapter2.setText(
                    Utils.formatDateToHumanReadable(
                            pdfModel.getLastModified()));

            tvFileSize_FileListAdapter2.setText(
                    Formatter.formatFileSize(
                            mContext_FileListAdapter2,
                            pdfModel.getLength()));

            if (dbHelper_FileListAdapter2.isStared(
                    pdfModel.getFileUri())) {
                btnFavorite_FileListAdapter2
                        .setImageResource(R.drawable.ic_favorite);
            } else {
                btnFavorite_FileListAdapter2
                        .setImageResource(R.drawable.ic_favorite_unselect);
            }

            itemView.setOnClickListener(v -> {
                if (mListener_FileListAdapter2 != null) {
                    mListener_FileListAdapter2.onPdfClick(pdfModel);
                }
            });

            btnFavorite_FileListAdapter2.setOnClickListener(v -> {

                if (dbHelper_FileListAdapter2.isStared(
                        pdfModel.getFileUri())) {

                    btnFavorite_FileListAdapter2
                            .setImageResource(R.drawable.ic_favorite_unselect);

                    dbHelper_FileListAdapter2
                            .removeStaredDocument(pdfModel.getFileUri());

                } else {

                    dbHelper_FileListAdapter2
                            .addStaredDocument(pdfModel.getFileUri());

                    btnFavorite_FileListAdapter2
                            .setImageResource(R.drawable.ic_favorite);
                }
            });

            imgMore_FileListAdapter2.setOnClickListener(v ->
                    Utils.showMoreDialog(
                            mContext_FileListAdapter2,
                            document,
                            false,
                            new MoreListener() {

                                @Override
                                public void onRename(String newName) {

                                    final File fileOldPdfName =
                                            new File(pdfModel.getFileUri());

                                    final String replaceName =
                                            pdfModel.getFileUri()
                                                    .replace(
                                                            Utils.removeExtension(
                                                                    pdfModel.getName()),
                                                            newName);

                                    if (fileOldPdfName.renameTo(
                                            new File(replaceName))) {

                                        if (dbHelper_FileListAdapter2.isStared(
                                                fileOldPdfName.getAbsolutePath())) {

                                            dbHelper_FileListAdapter2
                                                    .updateStarred(
                                                            pdfModel.getFileUri(),
                                                            replaceName);

                                            EventBus.getDefault()
                                                    .post(new DataUpdateEvent.updateFav());
                                        }

                                        if (dbHelper_FileListAdapter2.isRecent(
                                                fileOldPdfName.getAbsolutePath())) {

                                            dbHelper_FileListAdapter2
                                                    .updateHistory(
                                                            pdfModel.getFileUri(),
                                                            replaceName);

                                            EventBus.getDefault()
                                                    .post(new DataUpdateEvent.updateRecent());
                                        }

                                        File newFile = new File(replaceName);

                                        PDFModel documentNew = new PDFModel();
                                        documentNew.setName(newFile.getName());
                                        documentNew.setFileUri(newFile.getAbsolutePath());
                                        documentNew.setLength(newFile.length());
                                        documentNew.setLastModified(newFile.lastModified());

                                        int position = getAdapterPosition();

                                        if (position != RecyclerView.NO_POSITION
                                                && position < originalData_FileListAdapter2.size()) {

                                            originalData_FileListAdapter2.set(position, documentNew);
                                            notifyItemChanged(position);

                                        } else {
                                            Log.e("AdapterError",
                                                    "Invalid position or arraylist size");
                                        }

                                    } else {
                                        Toast.makeText(
                                                mContext_FileListAdapter2,
                                                R.string.dialog_rename_fail,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onDelete() {

                                    File fileToDelete =
                                            new File(pdfModel.getFileUri());

                                    if (fileToDelete.exists()) {

                                        if (fileToDelete.delete()) {

                                            MediaScannerConnection.scanFile(
                                                    mContext_FileListAdapter2,
                                                    new String[]{pdfModel.getFileUri()},
                                                    null,
                                                    null);

                                            if (dbHelper_FileListAdapter2.isStared(
                                                    pdfModel.getFileUri())) {

                                                dbHelper_FileListAdapter2
                                                        .removeStaredDocument(
                                                                pdfModel.getFileUri());
                                            }

                                            if (dbHelper_FileListAdapter2.isRecent(
                                                    pdfModel.getFileUri())) {

                                                dbHelper_FileListAdapter2
                                                        .removeRecentDocument(
                                                                pdfModel.getFileUri());
                                            }

                                            int position = getAdapterPosition();

                                            try {
                                                originalData_FileListAdapter2.remove(position);
                                                notifyItemRemoved(position);
                                                notifyItemRangeChanged(
                                                        position,
                                                        originalData_FileListAdapter2.size());
                                            } catch (IndexOutOfBoundsException e) {
                                                Log.e(GlobalConstant.TAG_LOG,
                                                        "Error message",
                                                        e);
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onRemove() {
                                }
                            }));
        }
    }
}