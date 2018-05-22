package com.pkmnapps.eventsdtu;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by prerak on 14/4/18.
 */

public class EmptyImageAdapter extends RecyclerView.Adapter<EmptyImageAdapter.MyViewHolder> {

    int resId;

    public EmptyImageAdapter(int resId) {
        this.resId = resId;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView display;
        public MyViewHolder(View view) {
            super(view);
            display = (ImageView) view.findViewById(R.id.imageView_Empty);
            display.setBackgroundResource(resId);
        }
    }


    @Override
    public EmptyImageAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ll_empty_imageview, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(EmptyImageAdapter.MyViewHolder holder, int position) {


    }


    @Override
    public int getItemCount() {
        return 1;
    }
}
