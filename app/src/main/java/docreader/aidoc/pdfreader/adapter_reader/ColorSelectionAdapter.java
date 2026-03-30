package docreader.aidoc.pdfreader.adapter_reader;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import docreader.aidoc.pdfreader.R;
import docreader.aidoc.pdfreader.model_reader.CodeColorModel;

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

        CodeColorModel colorModel = colorModelArrayList_ColorSelection.get(position);

        int color = android.graphics.Color.parseColor(colorModel.getCodeColor_ColorModel());

        // Use setColor instead of setTint to avoid overwriting the stroke
        android.graphics.drawable.GradientDrawable gd = (android.graphics.drawable.GradientDrawable) holder.viewBg_ColorSelection.getBackground().mutate();
        gd.clearColorFilter();
        gd.setColor(color);

        // Selection state
        if (lastPost_ColorSelection == position) {
            holder.imgChoose_ColorSelection.setVisibility(View.VISIBLE);
            holder.viewWhiteBorder.setVisibility(View.VISIBLE);
            holder.viewGlow.setVisibility(View.VISIBLE);
            
            // Tint the glow with the same color but with transparency
            // For white, use a light grey glow to make it visible
            int glowColor;
            if (colorModel.isWhite_ColorModel()) {
                glowColor = android.graphics.Color.parseColor("#33A0A0A0"); // 20% alpha grey
                holder.imgChoose_ColorSelection.setColorFilter(android.graphics.Color.parseColor("#7B7B7B"));
            } else {
                glowColor = (color & 0x00FFFFFF) | 0x33000000; // 20% alpha
                holder.imgChoose_ColorSelection.clearColorFilter();
            }
            android.graphics.drawable.Drawable glowBg = holder.viewGlow.getBackground().mutate();
            glowBg.clearColorFilter();
            glowBg.setTint(glowColor);
            
            holder.viewWhiteBorder.getBackground().mutate(); // Just in case
        } else {
            holder.imgChoose_ColorSelection.setVisibility(View.GONE);
            holder.viewWhiteBorder.setVisibility(View.GONE);
            holder.viewGlow.setVisibility(View.GONE);
        }

        // Handle white color visibility (add a small border if needed)
        if (colorModel.isWhite_ColorModel()) {
            float density = holder.itemView.getContext().getResources().getDisplayMetrics().density;
            gd.setStroke((int)(1.5f * density), android.graphics.Color.parseColor("#A0A0A0"));
        } else {
            gd.setStroke(0, android.graphics.Color.TRANSPARENT);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int oldPos = lastPost_ColorSelection;
                lastPost_ColorSelection = holder.getAdapterPosition();
                notifyItemChanged(oldPos);
                notifyItemChanged(lastPost_ColorSelection);

                if (listener_ColorSelection != null) {
                    listener_ColorSelection.onColorChanged(colorModel.getCodeColor_ColorModel());
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
        View viewWhiteBorder;
        View viewGlow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            viewBg_ColorSelection = itemView.findViewById(R.id.imgColor);
            imgChoose_ColorSelection = itemView.findViewById(R.id.choose_color_iv1);
            viewWhiteBorder = itemView.findViewById(R.id.view_white_border);
            viewGlow = itemView.findViewById(R.id.view_selection_glow);
        }
    }
}