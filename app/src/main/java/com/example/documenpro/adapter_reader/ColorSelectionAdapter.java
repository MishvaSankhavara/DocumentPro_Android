package com.example.documenpro.adapter_reader;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.documenpro.R;
import com.example.documenpro.model_reader.CodeColorModel;

import java.util.ArrayList;

public class ColorSelectionAdapter extends RecyclerView.Adapter<ColorSelectionAdapter.ViewHolder> {

    int lastPost_ColorSelection;

    private final ColorChangedListener_ColorSelection listener_ColorSelection;

    private final ArrayList<CodeColorModel> colorModelArrayList_ColorSelection;

    public ColorSelectionAdapter(ArrayList<CodeColorModel> colorList,
                                 int lstPosition,
                                 ColorChangedListener_ColorSelection listener) {
        this.colorModelArrayList_ColorSelection = colorList;
        this.listener_ColorSelection = listener;
        this.lastPost_ColorSelection = lstPosition;
    }

    @Override
    public int getItemCount() {
        if (colorModelArrayList_ColorSelection == null) {
            return 0;
        } else {
            return colorModelArrayList_ColorSelection.size();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,
                                 int position) {

        CodeColorModel colorModel =
                colorModelArrayList_ColorSelection.get(position);

        if (colorModel.isWhite_ColorModel()) {
            holder.imgChoose_ColorSelection.setImageResource(
                    R.drawable.ic_color_choosed_main);
        } else {
            holder.imgChoose_ColorSelection.setImageResource(
                    R.drawable.ic_color_choosed);
        }

        holder.viewBg_ColorSelection.setBackgroundResource(
                colorModel.getIdSourceBg_ColorModel());

        if (lastPost_ColorSelection == position) {
            holder.imgChoose_ColorSelection.setVisibility(View.VISIBLE);
        } else {
            holder.imgChoose_ColorSelection.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                notifyItemChanged(lastPost_ColorSelection);
                notifyItemChanged(holder.getAdapterPosition());

                lastPost_ColorSelection = holder.getAdapterPosition();

                if (listener_ColorSelection != null) {
                    listener_ColorSelection.onColorChanged(
                            colorModel.getCodeColor_ColorModel());
                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_color, parent, false);

        return new ViewHolder(view);
    }

    public interface ColorChangedListener_ColorSelection {
        void onColorChanged(String var1_ColorSelection);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        AppCompatImageView imgChoose_ColorSelection;

        View viewBg_ColorSelection;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            viewBg_ColorSelection =
                    itemView.findViewById(R.id.imgColor);

            imgChoose_ColorSelection =
                    itemView.findViewById(R.id.choose_color_iv1);
        }
    }
}