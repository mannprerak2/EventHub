package com.pkmnapps.eventsdtu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
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
import java.util.Objects;

/**
 * Created by prerak on 1/3/18.
 */

public class OrganiserAdapter extends RecyclerView.Adapter<OrganiserAdapter.MyViewHolder> {

    private List<OrganiserData> organiserDataList, originalOrganiserDataList;
    private ArrayList<OrganiserData> searchSocietyList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, descp, location;
        public CheckBox subs;
        public ImageView imageView;
        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.textView_society_name);
            location = (TextView) view.findViewById(R.id.textView_society_location);
            descp = (TextView) view.findViewById(R.id.textView_society_descp);
            subs = (CheckBox) view.findViewById(R.id.button_pin);
            imageView = (ImageView)view.findViewById(R.id.imageView_society);
        }
    }


    public OrganiserAdapter(List<OrganiserData> organiserData) {
        this.organiserDataList = organiserData;
        this.searchSocietyList = new ArrayList<>();

    }

    @Override
    public OrganiserAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        if(viewType==0) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ll_society_item_r, parent, false);
        }
        else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ll_society_item_l, parent, false);
        }

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final OrganiserAdapter.MyViewHolder holder, int position) {
        final OrganiserData organiserData = organiserDataList.get(position);
        holder.title.setText(organiserData.getNameSpan());
        holder.location.setText(organiserData.getCollege());
        holder.descp.setText(organiserData.getDesc());
        holder.subs.setChecked(organiserData.getSubscribed());
        //if post was pinned change color of star
        holder.subs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBHelperSubs dbHelperSubs = new DBHelperSubs(view.getContext());
                if(holder.subs.isChecked()){//for true
                    dbHelperSubs.insertSubsScoiety(organiserData.getUniqueId(), organiserData.getName(), organiserData.getLogoLink());
                    Snackbar.make(holder.itemView,"Subscribed",Snackbar.LENGTH_SHORT).show();
                }
                else {//for false
                    dbHelperSubs.deleteSubsSociety(organiserData.getUniqueId());
                    Snackbar.make(holder.itemView,"Un-Subscribed",Snackbar.LENGTH_SHORT).show();
                }
                Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.shake_anim);
                holder.subs.startAnimation(animation);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open event activity
                Intent intent = new Intent(view.getContext(),OrganiserActivity.class);
                intent.putExtra("uniqueId", organiserData.getUniqueId());
                intent.putExtra("name", organiserData.getName());
                intent.putExtra("image",organiserData.getLogoLink());
                view.getContext().startActivity(intent);
            }
        });

        //download logo and set it in imageView
        //image loading...
        final Context context = holder.itemView.getContext();
        final String imageLink = organiserData.getLogoLink();
        final DBHelperImages dbHelperImages = new DBHelperImages(context);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("Societies");
        final StorageReference imageRef = storageRef.child(imageLink);

        if(!dbHelperImages.isInImageDatabase(imageLink)) { //download data if not in database
            final File localFile = new File(context.getCacheDir(), imageLink);//we store download link because if we change file on server it changes the link too

            imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    //file downloaded now , open this in your imageview
                    Picasso.with(context).load(localFile).into(holder.imageView);
                    //save this to database now
                    dbHelperImages.insertImage(imageLink, imageLink);
                }
            });
        }
        else{ // image is in database load it here
            final File localFile = new File(context.getCacheDir(), imageLink);//we store download link because if we change file on server it changes the link too
            if(!localFile.exists()){//file doesnt exists on cache but is there on database
                //delete entry from database
                dbHelperImages.deleteImage(imageLink);
                //rerun downloaded
                Toast.makeText(holder.itemView.getContext(),"rerun",Toast.LENGTH_SHORT).show();
                imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        //file downloaded now , open this in your imageview
                        Picasso.with(context).load(localFile).into(holder.imageView);
                        //save this to database now
                        dbHelperImages.insertImage(imageLink, imageLink);
                    }
                });
            }
            else
            Picasso.with(context).load(localFile).into(holder.imageView);
        }


    }


    @Override
    public int getItemCount() {
        return organiserDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        return position % 2 * 2;
    }
    public int filter(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());

        organiserDataList.clear();
//        if (charText.length() == 0) {
//            organiserDataList.addAll(searchSocietyList);
//        } else {
            for (OrganiserData wp : searchSocietyList) {
                String name=wp.getName().toLowerCase(Locale.getDefault());
                if (name.contains(charText)) {
                    int startPos = name.indexOf(charText);
                    int endPos = startPos + charText.length();
                    Spannable span = Spannable.Factory.getInstance().newSpannable(wp.getName());
                    span.setSpan(new ForegroundColorSpan(Color.RED),startPos,endPos,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    wp.setName(span);
                    organiserDataList.add(wp);
                }

            //}
        }
        notifyDataSetChanged();
            if(organiserDataList.size()>0)
                return 1;
            else
                return 0;

    }
    public void updateSearchList(){//run this to use search functionality...
        searchSocietyList.clear();
        searchSocietyList.addAll(organiserDataList);
    }
    public void setOriginalSocietyDataList(){
        originalOrganiserDataList = new ArrayList<>();
        originalOrganiserDataList.addAll(organiserDataList);
    }

    public List<OrganiserData> getOriginalOrganiserDataList() {
        return originalOrganiserDataList;
    }

    public void sortByCollege(String college){
        organiserDataList.clear();
        organiserDataList.addAll(getOriginalOrganiserDataList());
        List<OrganiserData> a = new ArrayList<>();
        if(!Objects.equals(college, "-")) { //for reset
            for (int i = 0; i < organiserDataList.size(); i++) {
                if (!Objects.equals(organiserDataList.get(i).getCollege(), college))
                    a.add(organiserDataList.get(i));
            }
        }
        organiserDataList.removeAll(a);
        updateSearchList();
        notifyDataSetChanged();
    }
}
