package com.pkmnapps.eventsdtu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by prerak on 1/3/18.
 */

public class CollegeAdapter extends RecyclerView.Adapter<CollegeAdapter.MyViewHolder> {
    Context context;

    private List<CollegeData> collegeList,originalCollegeList;
    private ArrayList<CollegeData> searchCollegeList;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, fullName;
        public ImageView imageViewEvent;
        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.textView_college_name);
            fullName = (TextView) view.findViewById(R.id.textView_college_fullName);
            imageViewEvent = (ImageView)view.findViewById(R.id.imageView_College);
        }
    }


    public CollegeAdapter(List<CollegeData> collegeList) {
        this.collegeList = collegeList;
        this.searchCollegeList = new ArrayList<>();
    }

    @Override
    public CollegeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ll_college_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CollegeAdapter.MyViewHolder holder, int position) {
        final CollegeData collegeData = collegeList.get(position);
        holder.name.setText(collegeData.getNameSpan());
        holder.fullName.setText(collegeData.getFullName());

        context = holder.itemView.getContext();
        //if post was pinned change color of star
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open event activity
                Intent intent = new Intent(view.getContext(),CollegeActivity.class);
                intent.putExtra("uniqueid",collegeData.getUniqueId());
                intent.putExtra("name",collegeData.getName());
                view.getContext().startActivity(intent);
            }
        });
        //image loading
        final String imageLink = collegeData.getImageLink();

        //image loading...
        final DBHelperImages dbHelperImages = new DBHelperImages(context);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("Colleges");
        final StorageReference imageRef = storageRef.child(imageLink);
        if(!dbHelperImages.isInImageDatabase(imageLink)) { //download data if not in database
            final File localFile = new File(context.getCacheDir(), imageLink);//we store download link because if we change file on server it changes the link too
            imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    //file downloaded now , open this in your imageview
                    Picasso.with(context).load(localFile).into(holder.imageViewEvent);
                    //save this to database now
                    dbHelperImages.insertImage(imageLink, imageLink);
                }
            });
        }
        else{ // image is in database load it here
            final File localFile = new File(context.getCacheDir(), imageLink);//we store download link because if we change file on server it changes the link too
            if(!localFile.exists()){//files doesnt exist on cache but exists on sql database
                //remove the entry in database
                dbHelperImages.deleteImage(imageLink);
                //rerun downloader
                Toast.makeText(context,"rerun",Toast.LENGTH_SHORT).show();
                imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        //file downloaded now , open this in your imageview
                        Picasso.with(context).load(localFile).into(holder.imageViewEvent);
                        //save this to database now
                        dbHelperImages.insertImage(imageLink, imageLink);
                    }
                });
            }
            else {
                Picasso.with(context).load(localFile).into(holder.imageViewEvent);
            }
        }
    }


    @Override
    public int getItemCount() {
        return collegeList.size();
    }

    public int filter(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());

        collegeList.clear();
//        if (charText.length() == 0) {
//            collegeList.addAll(searchCollegeList);
//        } else {
            for (CollegeData wp : searchCollegeList) {
                String name=wp.getName().toLowerCase(Locale.getDefault());
                if (name.contains(charText)) {
                    int startPos = name.indexOf(charText);
                    int endPos = startPos + charText.length();
                    Spannable span = Spannable.Factory.getInstance().newSpannable(wp.getName());
                    span.setSpan(new ForegroundColorSpan(Color.RED),startPos,endPos,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    wp.setName(span);
                    collegeList.add(wp);
                }
            }
        //}
        notifyDataSetChanged();
        if(collegeList.size()>0)
            return 1;
        else
            return 0;

    }
    public void updateSearchList(){//run this to use search functionality...
        searchCollegeList.clear();
        searchCollegeList.addAll(collegeList);
    }
    public void setOriginalCollegeList(){
        originalCollegeList = collegeList;
    }

}
