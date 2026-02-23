package com.example.documenpro.adapter_reader;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.documenpro.R;

import java.util.List;

public class OnboardingScreenAdapter extends RecyclerView.Adapter<OnboardingScreenAdapter.ViewHolder> {

    public List<? extends View> list_OnboardingScreen;

    @Override
    public int getItemCount() {
        return list_OnboardingScreen.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,
                                 int position) {

        ViewGroup viewGroup_OnboardingScreen =
                holder.viewGroup_OnboardingScreen;

        viewGroup_OnboardingScreen.removeAllViews();
        viewGroup_OnboardingScreen.addView(
                list_OnboardingScreen.get(position));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_empty,
                        parent,
                        false);

        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ViewGroup viewGroup_OnboardingScreen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            viewGroup_OnboardingScreen =
                    itemView.findViewById(R.id.empty);
        }
    }
}