package com.pkmnapps.eventsdtu;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by prerak on 13/3/18.
 */

public class EmptyAdapter extends RecyclerView.Adapter<EmptyAdapter.MyViewHolder> {

    String displayStr;

    public EmptyAdapter(String display) {
        this.displayStr = display;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView display;
        public MyViewHolder(View view) {
            super(view);
            display = (TextView)view.findViewById(R.id.textView_display);
            display.setText(displayStr);
        }
    }


    @Override
    public EmptyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ll_empty_display_string, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(EmptyAdapter.MyViewHolder holder, int position) {

    }


    @Override
    public int getItemCount() {
        return 1;
    }
}

