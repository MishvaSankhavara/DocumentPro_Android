package com.example.documenpro.ui.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class EmptyStateRecyclerView extends RecyclerView {

    private View emptyView;

    final private AdapterDataObserver adapterDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            updateEmptyState();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            updateEmptyState();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            updateEmptyState();
        }
    };

    public EmptyStateRecyclerView(Context context) {
        super(context);
    }

    public EmptyStateRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptyStateRecyclerView(Context context, AttributeSet attrs,
                                  int defStyle) {
        super(context, attrs, defStyle);
    }

    private void updateEmptyState() {
        if (emptyView != null && getAdapter() != null) {
            final boolean isEmpty =
                    getAdapter().getItemCount() == 0;
            emptyView.setVisibility(isEmpty ? VISIBLE : GONE);
            setVisibility(isEmpty ? GONE : VISIBLE);
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(adapterDataObserver);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(adapterDataObserver);
        }

        updateEmptyState();
    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
        updateEmptyState();
    }
}