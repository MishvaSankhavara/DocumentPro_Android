package docreader.aidoc.pdfreader.adapter_reader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import docreader.aidoc.pdfreader.R;
import docreader.aidoc.pdfreader.clickListener.OnThumbnailClickListener;
import docreader.aidoc.pdfreader.model_reader.PDFPageModel;

import java.util.ArrayList;

public class PdfPreviewThumbnailAdapter extends RecyclerView.Adapter<PdfPreviewThumbnailAdapter.ViewHolder> {

    public boolean isSelectedAll_PdfPreview = false;
    private final OnThumbnailClickListener mListener_PdfPreview;
    private final ArrayList<PDFPageModel> arrayList_PdfPreview;
    public Context mContext_PdfPreview;

    public ArrayList<PDFPageModel> getSelected_PdfPreview() {
        ArrayList<PDFPageModel> selected_PdfPreview = new ArrayList<>();
        for (int i = 0; i < arrayList_PdfPreview.size(); i++) {
            if (arrayList_PdfPreview.get(i).isChecked_PDFPageModel()) {
                selected_PdfPreview.add(arrayList_PdfPreview.get(i));
            }
        }
        return selected_PdfPreview;
    }

    public ArrayList<Integer> getPageNumbers_PdfPreview() {
        ArrayList<Integer> pageNumbers_PdfPreview = new ArrayList<>();
        for (int i = 0; i < arrayList_PdfPreview.size(); i++) {
            if (arrayList_PdfPreview.get(i).isChecked_PDFPageModel()) {
                pageNumbers_PdfPreview.add(i);
            }
        }
        return pageNumbers_PdfPreview;
    }

    @Override
    public int getItemCount() {
        if (arrayList_PdfPreview == null) {
            return 0;
        } else {
            return arrayList_PdfPreview.size();
        }
    }

    public void setUnSelectedAll_PdfPreview() {
        for (int i = 0; i < arrayList_PdfPreview.size(); i++) {
            arrayList_PdfPreview.get(i).setChecked_PDFPageModel(false);
        }
        isSelectedAll_PdfPreview = false;
        notifyDataSetChanged();
    }

    public void setSelectedAll_PdfPreview() {
        for (int i = 0; i < arrayList_PdfPreview.size(); i++) {
            arrayList_PdfPreview.get(i).setChecked_PDFPageModel(true);
        }
        isSelectedAll_PdfPreview = true;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PDFPageModel pdfPage = arrayList_PdfPreview.get(position);

        Glide.with(mContext_PdfPreview)
                .load(pdfPage.getThumbnailUri_PDFPageModel())
                .into(holder.imgThumbnail_PdfPreview);

        holder.tvPageNumber_PdfPreview.setText(String.valueOf(pdfPage.getPageNumber_PDFPageModel()));

        if (pdfPage.isChecked_PDFPageModel()) {
            holder.ivBorder_PdfPreview.setVisibility(View.VISIBLE);
        } else {
            holder.ivBorder_PdfPreview.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(view -> {
            pdfPage.setChecked_PDFPageModel(!pdfPage.isChecked_PDFPageModel());
            holder.ivBorder_PdfPreview.setVisibility(
                    pdfPage.isChecked_PDFPageModel() ? View.VISIBLE : View.GONE
            );

            if (mListener_PdfPreview != null) {
                mListener_PdfPreview.onChoosePdfSplitListener();
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_preview, parent, false)
        );
    }

    public PdfPreviewThumbnailAdapter(Context mContext, ArrayList<PDFPageModel> arrayList, OnThumbnailClickListener listener) {
        this.mListener_PdfPreview = listener;
        this.arrayList_PdfPreview = arrayList;
        this.mContext_PdfPreview = mContext;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        AppCompatImageView imgThumbnail_PdfPreview;
        AppCompatTextView tvPageNumber_PdfPreview;
        AppCompatImageView ivBorder_PdfPreview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumbnail_PdfPreview = itemView.findViewById(R.id.item_icon);
            tvPageNumber_PdfPreview = itemView.findViewById(R.id.page_count);
            ivBorder_PdfPreview = itemView.findViewById(R.id.pdf_select_foreground);
        }
    }
}