package com.example.documenpro.adapter_reader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.documenpro.GlobalConstant;
import com.example.documenpro.R;
import com.example.documenpro.SharedPreferenceUtils;
import com.example.documenpro.listener.LanguageListener;
import com.example.documenpro.model.Language;

import java.util.ArrayList;

public class LanguageListAdapter extends RecyclerView.Adapter<LanguageListAdapter.ViewHolder> {

    private final LanguageListener listener_LanguageList;
    private final Context mContext_LanguageList;
    private int selectedItem_LanguageList;
    private final ArrayList<Language> languages_LanguageList;

    public LanguageListAdapter(Context mContext,
                               LanguageListener mListener) {
        this.mContext_LanguageList = mContext;
        this.languages_LanguageList =
                GlobalConstant.createArrayLanguage();
        this.selectedItem_LanguageList =
                SharedPreferenceUtils.getInstance(mContext)
                        .getInt(GlobalConstant.LANGUAGE_KEY_NUMBER, 0);
        this.listener_LanguageList = mListener;
    }

    @Override
    public int getItemCount() {
        if (languages_LanguageList == null) {
            return 0;
        } else {
            return languages_LanguageList.size();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,
                                 int position) {
        holder.bind(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_language_activity,
                        parent,
                        false);
        return new ViewHolder(view);
    }

    public void setSelectedItem_LanguageList(int position) {
        selectedItem_LanguageList = position;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        ImageView imgFlag_LanguageList;
        ImageView imgChoice_LanguageList;
        TextView tvLang_LanguageList;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgFlag_LanguageList =
                    itemView.findViewById(R.id.img_flag);

            tvLang_LanguageList =
                    itemView.findViewById(R.id.tv_name);

            imgChoice_LanguageList =
                    itemView.findViewById(R.id.iv_selected_state);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();

            if (position != RecyclerView.NO_POSITION) {

                setSelectedItem_LanguageList(position);

                if (listener_LanguageList != null) {
                    listener_LanguageList.onLangChoice(position);
                }
            }
        }

        public void bind(int position) {

            Language language =
                    languages_LanguageList.get(position);

            tvLang_LanguageList.setText(
                    language.getNameLanguage());

            Glide.with(mContext_LanguageList)
                    .load(language.getImgResource())
                    .into(imgFlag_LanguageList);

            if (selectedItem_LanguageList == position) {
                Glide.with(mContext_LanguageList)
                        .load(R.drawable.ic_choice)
                        .into(imgChoice_LanguageList);
            } else {
                Glide.with(mContext_LanguageList)
                        .load(R.drawable.ic_not_choice)
                        .into(imgChoice_LanguageList);
            }
        }
    }
}