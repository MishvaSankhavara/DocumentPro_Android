package docreader.aidoc.pdfreader.adapter_reader;

import android.content.Context;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import docreader.aidoc.pdfreader.R;
import docreader.aidoc.pdfreader.clickListener.PdfSelectionListener;
import docreader.aidoc.pdfreader.model_reader.PDFReaderModel;
import docreader.aidoc.pdfreader.utils.Utils;

import java.util.ArrayList;

public class FileSelectionAdapter extends RecyclerView.Adapter<FileSelectionAdapter.ViewHolder> {

    private final PdfSelectionListener mListenerFileSelection;
    private final ArrayList<PDFReaderModel> originalDataFileSelection;
    private final Context mContextFileSelection;

    public FileSelectionAdapter(Context mContext,
                                ArrayList<PDFReaderModel> originalData,
                                PdfSelectionListener mListener) {
        this.mContextFileSelection = mContext;
        this.originalDataFileSelection = originalData;
        this.mListenerFileSelection = mListener;
    }

    @Override
    public int getItemCount() {
        if (originalDataFileSelection == null) {
            return 0;
        } else {
            return originalDataFileSelection.size();
        }
    }

    public void updateData(PDFReaderModel pdfModelFileSelection,
                           int positionFileSelection) {
        originalDataFileSelection.set(positionFileSelection,
                pdfModelFileSelection);
        notifyItemChanged(positionFileSelection);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,
                                 int position) {

        PDFReaderModel pdfModelFileSelection =
                originalDataFileSelection.get(position);

        if (pdfModelFileSelection.isProtected_PDFModel()) {
            holder.ivPdfFileSelection.setImageResource(
                    R.drawable.ic_pdf_no);
            holder.ivLockFileSelection.setVisibility(View.VISIBLE);
        } else {
            holder.ivPdfFileSelection.setImageResource(
                    R.drawable.ic_pdf_2);
            holder.ivLockFileSelection.setVisibility(View.GONE);
        }

        holder.btnFavoriteFileSelection.setVisibility(View.GONE);
        holder.imgMoreFileSelection.setVisibility(View.INVISIBLE);

        holder.tvDateFileSelection.setText(
                Utils.formatDateToHumanReadable(
                        pdfModelFileSelection.getLastModified_PDFModel()));

        holder.tvFileSizeFileSelection.setText(
                Formatter.formatFileSize(
                        mContextFileSelection,
                        pdfModelFileSelection.getLength_PDFModel()));

        holder.tvNameFileSelection.setText(
                pdfModelFileSelection.getName_PDFModel());

        holder.itemView.setOnClickListener(view -> {
            if (mListenerFileSelection != null) {
                mListenerFileSelection.onPdfSelect(
                        pdfModelFileSelection,
                        holder.getAdapterPosition());
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_pdf,
                        parent,
                        false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final AppCompatImageView ivLockFileSelection;
        private final AppCompatImageView ivPdfFileSelection;

        private final LinearLayout btnFavoriteFileSelection;
        private final AppCompatImageView imgMoreFileSelection;

        private final TextView tvFileSizeFileSelection;
        private final TextView tvDateFileSelection;
        private final TextView tvNameFileSelection;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNameFileSelection =
                    itemView.findViewById(R.id.tv_name);

            tvDateFileSelection =
                    itemView.findViewById(R.id.tvFileDate);

            tvFileSizeFileSelection =
                    itemView.findViewById(R.id.tvFileSize);

            imgMoreFileSelection =
                    itemView.findViewById(R.id.iv_more);

            ivLockFileSelection =
                    itemView.findViewById(R.id.ivLock);

            ivPdfFileSelection =
                    itemView.findViewById(R.id.iv_icon);

            btnFavoriteFileSelection =
                    itemView.findViewById(R.id.ll_favorite);
        }
    }
}