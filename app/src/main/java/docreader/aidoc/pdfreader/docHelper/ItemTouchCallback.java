package docreader.aidoc.pdfreader.docHelper;

import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import docreader.aidoc.pdfreader.clickListener.ItemTouchAdapter;

public class ItemTouchCallback extends ItemTouchHelper.Callback {

    public void clearView(@NonNull RecyclerView recyclerView_Callback, @NonNull RecyclerView.ViewHolder viewHolder_Callback) {

        super.clearView(recyclerView_Callback, viewHolder_Callback);
        viewHolder_Callback.itemView.setAlpha(1.0f);

        if (viewHolder_Callback instanceof TouchHelperViewHolder) {
            ((TouchHelperViewHolder) viewHolder_Callback).onItemClear_TouchHelper();
        }
    }

    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder_Callback, int i_Callback) {

        if (i_Callback != 0 && (viewHolder_Callback instanceof TouchHelperViewHolder)) {

            ((TouchHelperViewHolder) viewHolder_Callback).onItemSelected_TouchHelper();
        }

        super.onSelectedChanged(viewHolder_Callback, i_Callback);
    }

    public void onChildDraw(@NonNull Canvas canvas_Callback, @NonNull RecyclerView recyclerView_Callback, @NonNull RecyclerView.ViewHolder viewHolder_Callback, float f_Callback, float f2_Callback, int i_Callback, boolean z_Callback) {

        if (i_Callback == 1) {
            viewHolder_Callback.itemView.setAlpha(1.0f - (Math.abs(f_Callback) / ((float) viewHolder_Callback.itemView.getWidth())));
            viewHolder_Callback.itemView.setTranslationX(f_Callback);
            return;
        }

        super.onChildDraw(canvas_Callback, recyclerView_Callback, viewHolder_Callback, f_Callback, f2_Callback, i_Callback, z_Callback);
    }

    public void onSwiped(RecyclerView.ViewHolder viewHolder_Callback, int i) {
        this.mAdapter_Callback.itemDismiss(viewHolder_Callback.getAdapterPosition());
    }

    public boolean onMove(@NonNull RecyclerView recyclerView_Callback, RecyclerView.ViewHolder viewHolder_Callback, RecyclerView.ViewHolder viewHolder2_Callback) {

        if (viewHolder_Callback.getItemViewType() != viewHolder2_Callback.getItemViewType()) {
            return false;
        }

        this.mAdapter_Callback.itemMove(viewHolder_Callback.getAdapterPosition(), viewHolder2_Callback.getAdapterPosition());

        return true;
    }

    public int getMovementFlags(RecyclerView recyclerView_Callback, @NonNull RecyclerView.ViewHolder viewHolder_Callback) {

        if (recyclerView_Callback.getLayoutManager() instanceof GridLayoutManager) {

            return makeMovementFlags(15, 0);
        }

        return makeMovementFlags(3, 48);
    }

    public ItemTouchCallback(ItemTouchAdapter itemTouchHelperAdapter) {
        this.mAdapter_Callback = itemTouchHelperAdapter;
    }

    public boolean isLongPressDragEnabled() {
        return true;
    }

    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    private final ItemTouchAdapter mAdapter_Callback;
}