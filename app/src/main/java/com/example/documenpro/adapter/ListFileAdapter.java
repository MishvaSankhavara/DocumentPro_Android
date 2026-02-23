package com.example.documenpro.adapter;

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

import java.io.File;
import java.util.ArrayList;

public class ListFileAdapter extends RecyclerView.Adapter<ListFileAdapter.ViewHolder> {
    private final Activity mContext;

    private ArrayList<Document> arrayList;
    DbHelper dbHelper;
    private final DocumentClickListener listener;

    public ListFileAdapter(Activity mContext, ArrayList<Document> arrayList, DocumentClickListener listener) {
        this.mContext = mContext;
        this.arrayList = arrayList;
        this.listener = listener;
        this.dbHelper = DbHelper.getInstance(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_document, parent, false);
        return new ViewHolder(view);
    }

    public void setData(ArrayList<Document> dataList) {
        this.arrayList = dataList;
        notifyDataSetChanged(); // Thông báo cho RecyclerView biết dữ liệu đã thay đổi
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Document document = arrayList.get(position);
        holder.bindDocument(document);
    }

    public void filter(ArrayList<Document> list) {
        arrayList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {

        return arrayList.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFileName;
        TextView tvFileSize;
        TextView tvFileDate;
        ImageView imgIcon;
        ImageView imgMore;
        LinearLayout btnFavorite;
        LottieAnimationView imgFavorite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFileName = itemView.findViewById(R.id.tv_name);
            tvFileSize = itemView.findViewById(R.id.tvFileSize);
            tvFileDate = itemView.findViewById(R.id.tvFileDate);
            imgIcon = itemView.findViewById(R.id.iv_icon);
            btnFavorite = itemView.findViewById(R.id.ll_favorite);
            imgFavorite = itemView.findViewById(R.id.lt_favorite);
            imgMore = itemView.findViewById(R.id.iv_more);
        }

        public void bindDocument(Document document) {
            tvFileName.setText(document.getFileName());
            tvFileDate.setText(Utils.formatDateToHumanReadable(document.getLastModified()));
            tvFileSize.setText(Formatter.formatFileSize(mContext, document.getLength()));
            Glide.with(mContext).load(document.getSrcImage()).into(imgIcon);
            if (dbHelper.isStared(document.getFileUri())) {
                imgFavorite.setFrame(50);
            } else {
                imgFavorite.setFrame(0);
            }
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDocument(document);
                }
            });
            imgMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.showMoreDialog(mContext, document, false, new MoreListener() {
                        @Override
                        public void onRename(String newName) {
                            final File fileOldPdfName = new File(document.getFileUri());
                            final String replaceName = document.getFileUri().replace(Utils.removeExtension(document.getFileName()), newName);
                            if (fileOldPdfName.renameTo(new File(replaceName))) {
                                if (dbHelper.isStared(fileOldPdfName.getAbsolutePath())) {
                                    dbHelper.updateStaredDocument(document.getFileUri(), replaceName);
                                }
                                if (dbHelper.isRecent(fileOldPdfName.getAbsolutePath())) {
                                    dbHelper.updateHistory(document.getFileUri(), replaceName);
                                }
                                File newFile = new File(replaceName);
                                Document documentNew = new Document();
                                documentNew.setFileName(newFile.getName());
                                documentNew.setFileUri(newFile.getAbsolutePath());
                                documentNew.setLength(newFile.length());
                                documentNew.setSrcImage(getDocumentSrc(newFile));
                                documentNew.setLastModified(newFile.lastModified());
                                int position = getAdapterPosition();
                                if (position != RecyclerView.NO_POSITION && position < arrayList.size()) {
                                    arrayList.set(getAdapterPosition(), documentNew);
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
                            File fileToDelete = new File(document.getFileUri());
                            if (fileToDelete.exists()) {
                                if (fileToDelete.delete()) {
                                    MediaScannerConnection.scanFile(mContext, new String[]{document.getFileUri()}, null, null);
                                    if (dbHelper.isStared(document.getFileUri())) {
                                        dbHelper.removeStaredDocument(document.getFileUri());
//                                    mContext.sendBroadcast(new Intent(GlobalConstant.ACTION_UPDATE_FAVORITE_FRAGMENT));
                                    }
                                    if (dbHelper.isRecent(document.getFileUri())) {
                                        dbHelper.removeRecentDocument(document.getFileUri());
                                    }
                                    int position = getAdapterPosition();
                                    try {
                                        arrayList.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, arrayList.size());
                                    } catch (IndexOutOfBoundsException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }


                        }

                        @Override
                        public void onRemove() {

                        }
                    });
                }
            });

            btnFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dbHelper.isStared(document.getFileUri())) {
                        imgFavorite.setFrame(0);
                        dbHelper.removeStaredDocument(document.getFileUri());
                    } else {
                        dbHelper.addStaredDocument(document.getFileUri());
                        imgFavorite.playAnimation();
                        imgFavorite.addAnimatorListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                btnFavorite.setClickable(true);
                                btnFavorite.setFocusable(true);
                            }

                            @Override
                            public void onAnimationStart(Animator animation) {
                                super.onAnimationStart(animation);
                                btnFavorite.setClickable(false);
                                btnFavorite.setFocusable(false);
                            }
                        });
                    }
                }
            });
        }
    }
}
