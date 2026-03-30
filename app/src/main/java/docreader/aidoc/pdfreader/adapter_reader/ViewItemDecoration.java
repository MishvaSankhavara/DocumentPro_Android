package docreader.aidoc.pdfreader.adapter_reader;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewItemDecoration extends RecyclerView.ItemDecoration {

    private final int space_ViewItemDecoration;

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

        outRect.left = space_ViewItemDecoration;
        outRect.right = space_ViewItemDecoration;
        outRect.bottom = (int) (space_ViewItemDecoration / 1.2);
        outRect.top = 0;
    }

    public ViewItemDecoration(int space) {
        this.space_ViewItemDecoration = space;
    }
}