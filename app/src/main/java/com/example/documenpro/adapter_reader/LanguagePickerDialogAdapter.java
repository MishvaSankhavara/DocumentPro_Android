package com.example.documenpro.adapter_reader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.documenpro.GlobalConstant;
import com.example.documenpro.R;
import com.example.documenpro.SharedPreferenceUtils;
import com.example.documenpro.clickListener.OnPdfItemClickListener;
import com.example.documenpro.model_reader.LanguageModel;

import java.util.ArrayList;

public class LanguagePickerDialogAdapter extends RecyclerView.Adapter<LanguagePickerDialogAdapter.ViewHolder> {

    int lastPost_LanguagePickerDialog;
    private final OnPdfItemClickListener mListener_LanguagePickerDialog;
    private final ArrayList<LanguageModel> languages_LanguagePickerDialog;

    public LanguagePickerDialogAdapter(Context mContext,
                                       OnPdfItemClickListener mListener) {
        this.languages_LanguagePickerDialog =
                GlobalConstant.createArrayLanguage();
        this.mListener_LanguagePickerDialog = mListener;
        this.lastPost_LanguagePickerDialog =
                SharedPreferenceUtils.getInstance(mContext)
                        .getInt(GlobalConstant.LANGUAGE_KEY_NUMBER, 0);
    }

    @Override
    public int getItemCount() {
        if (languages_LanguagePickerDialog == null) {
            return 0;
        } else {
            return languages_LanguagePickerDialog.size();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,
                                 int position) {

        LanguageModel language =
                languages_LanguagePickerDialog.get(position);

        holder.imgFlag_LanguagePickerDialog
                .setImageResource(language.getImgResource_LanModel());

        holder.tvLang_LanguagePickerDialog
                .setText(language.getNameLanguage_LanModel());

        if (lastPost_LanguagePickerDialog == position) {
            holder.imgChoice_LanguagePickerDialog
                    .setImageResource(R.drawable.ic_choice);
        } else {
            holder.imgChoice_LanguagePickerDialog
                    .setImageResource(R.drawable.ic_not_choice);
        }

        holder.itemView.setOnClickListener(view -> {

            notifyItemChanged(lastPost_LanguagePickerDialog);
            notifyItemChanged(holder.getAdapterPosition());

            lastPost_LanguagePickerDialog =
                    holder.getAdapterPosition();

            if (mListener_LanguagePickerDialog != null) {
                mListener_LanguagePickerDialog
                        .onItemSelect(holder.getAdapterPosition());
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_language_dialog,
                        parent,
                        false);

        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgFlag_LanguagePickerDialog;
        ImageView imgChoice_LanguagePickerDialog;
        TextView tvLang_LanguagePickerDialog;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgFlag_LanguagePickerDialog =
                    itemView.findViewById(R.id.img_flag);

            tvLang_LanguagePickerDialog =
                    itemView.findViewById(R.id.tv_name);

            imgChoice_LanguagePickerDialog =
                    itemView.findViewById(R.id.iv_selected_state);
        }
    }
}