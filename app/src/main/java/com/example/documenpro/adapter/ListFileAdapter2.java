package com.example.documenpro.adapter;

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

public class ListFileAdapter2 extends RecyclerView.Adapter<ListFileAdapter2.ViewHolder> {

    private ResultViewerActivity mContext;
    private ArrayList<PDFModel> originalData;
    private OnPdfClickListener mListener;
    private DbHelper dbHelper;

    public ListFileAdapter2(ResultViewerActivity mContext, ArrayList<PDFModel> originalData,
            OnPdfClickListener mListener) {
        this.mContext = mContext;
        this.originalData = originalData;
        this.mListener = mListener;
        this.dbHelper = DbHelper.getInstance(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pdf, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PDFModel pdfModel = originalData.get(position);
        holder.bindPdf(pdfModel);
    }

    @Override
    public int getItemCount() {
        if (originalData == null) {
            return 0;
        } else {
            return originalData.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvDate;
        private final TextView tvFileSize;
        private final AppCompatImageView imgMore;
        private final AppCompatImageView btnFavorite;
        private final AppCompatImageView ivLock;
        private final View lock_line;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivLock = itemView.findViewById(R.id.iv_lock);
            lock_line = itemView.findViewById(R.id.lock_line);
            tvName = itemView.findViewById(R.id.tv_name);
            tvDate = itemView.findViewById(R.id.tvFileDate);
            tvFileSize = itemView.findViewById(R.id.tvFileSize);
            imgMore = itemView.findViewById(R.id.item_pdf_more);
            btnFavorite = itemView.findViewById(R.id.img_favorite);
        }

        public void bindPdf(PDFModel pdfModel) {
            Document document = new Document();
            // document.setAbsolutePath(pdfModel.getAbsolutePath());
            document.setFileUri(pdfModel.getFileUri());
            document.setFileName(pdfModel.getName());
            // document.setCreateAt(pdfModel.getCreateAt());
            document.setLastModified(pdfModel.getLastModified());
            document.setLength(pdfModel.getLength());
            if (pdfModel.isProtected()) {
                lock_line.setVisibility(View.VISIBLE);
                ivLock.setVisibility(View.VISIBLE);
            } else {
                ivLock.setVisibility(View.GONE);
                lock_line.setVisibility(View.GONE);
            }
            tvName.setText(pdfModel.getName());
            tvDate.setText(Utils.formatDateToHumanReadable(pdfModel.getLastModified()));
            tvFileSize.setText(Formatter.formatFileSize(mContext, pdfModel.getLength()));
            if (dbHelper.isStared(pdfModel.getFileUri())) {
                btnFavorite.setImageResource(R.drawable.ic_favorite);
            } else {
                btnFavorite.setImageResource(R.drawable.ic_favorite_unselect);
            }
            itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onPdfClick(pdfModel);
                }
            });
            btnFavorite.setOnClickListener(v -> {
                if (dbHelper.isStared(pdfModel.getFileUri())) {
                    btnFavorite.setImageResource(R.drawable.ic_favorite_unselect);
                    dbHelper.removeStaredDocument(pdfModel.getFileUri());
                } else {
                    dbHelper.addStaredDocument(pdfModel.getFileUri());
                    btnFavorite.setImageResource(R.drawable.ic_favorite);
                }
            });
            imgMore.setOnClickListener(v -> {
                Utils.showMoreDialog(mContext, document, false, new MoreListener() {
                    @Override
                    public void onRename(String newName) {
                        final File fileOldPdfName = new File(pdfModel.getFileUri());
                        final String replaceName = pdfModel.getFileUri()
                                .replace(Utils.removeExtension(pdfModel.getName()), newName);
                        if (fileOldPdfName.renameTo(new File(replaceName))) {
                            if (dbHelper.isStared(fileOldPdfName.getAbsolutePath())) {
                                dbHelper.updateStarred(pdfModel.getFileUri(), replaceName);
                                EventBus.getDefault().post(new DataUpdateEvent.updateFav());
                            }
                            if (dbHelper.isRecent(fileOldPdfName.getAbsolutePath())) {
                                dbHelper.updateHistory(pdfModel.getFileUri(), replaceName);
                                EventBus.getDefault().post(new DataUpdateEvent.updateRecent());
                            }
                            File newFile = new File(replaceName);
                            PDFModel documentNew = new PDFModel();
                            documentNew.setName(newFile.getName());
                            documentNew.setFileUri(newFile.getAbsolutePath());
                            documentNew.setLength(newFile.length());
                            documentNew.setLastModified(newFile.lastModified());
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION && position < originalData.size()) {
                                originalData.set(getAdapterPosition(), documentNew);
                                notifyItemChanged(getAdapterPosition());
                            } else {
                                Log.e("AdapterError", "Invalid position or arraylist size");
                            }
                        } else {
                            Toast.makeText(mContext, R.string.dialog_rename_fail, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onDelete() {
                        File fileToDelete = new File(pdfModel.getFileUri());
                        if (fileToDelete.exists()) {
                            if (fileToDelete.delete()) {
                                MediaScannerConnection.scanFile(mContext, new String[] { pdfModel.getFileUri() }, null,
                                        null);
                                if (dbHelper.isStared(pdfModel.getFileUri())) {
                                    dbHelper.removeStaredDocument(pdfModel.getFileUri());
                                }
                                if (dbHelper.isRecent(pdfModel.getFileUri())) {
                                    dbHelper.removeRecentDocument(pdfModel.getFileUri());

                                }
                                int position = getAdapterPosition();
                                try {
                                    originalData.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, originalData.size());
                                } catch (IndexOutOfBoundsException e) {
                                    Log.e(GlobalConstant.TAG_LOG, "Error message", e);
                                }
                            }
                        }
                    }

                    @Override
                    public void onRemove() {

                    }
                });
            });

        }

    }
}
