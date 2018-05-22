package com.pkmnapps.eventsdtu;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by prerak on 1/3/18.
 */

public class LoadingAdapter extends RecyclerView.Adapter<LoadingAdapter.MyViewHolder> {

    public LoadingAdapter() {
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(View view) {
            super(view);
        }
    }


    @Override
    public LoadingAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ll_loader, parent, false);

        return new LoadingAdapter.MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(LoadingAdapter.MyViewHolder holder, int position) {

    }


    @Override
    public int getItemCount() {
        return 1;
    }
}
