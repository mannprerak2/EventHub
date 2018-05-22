package com.pkmnapps.eventsdtu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by prerak on 10/2/18.
 */

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.MyViewHolder> {

    private List<EventData> eventList,originalEventList;
    private ArrayList<EventData> searchEventList;
    Context context;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, descp, college, date, organiser, category;
        public CheckBox star;
        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.textView_event_name);
            college = (TextView) view.findViewById(R.id.textView_event_location);
            descp = (TextView) view.findViewById(R.id.textView_event_descp);
            date = (TextView) view.findViewById(R.id.textView_event_date);
            organiser = (TextView)view.findViewById(R.id.textView_event_organiser);
            category = (TextView)view.findViewById(R.id.textView_category);
            star = (CheckBox) view.findViewById(R.id.button_pin);
        }
    }

    public EventAdapter(List<EventData> eventData,Context context) {
        this.eventList = eventData;
        this.context = context;
        this.searchEventList = new ArrayList<>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ll_event_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final EventData eventData = eventList.get(position);
        holder.title.setText(eventData.getNameSpan());
        holder.college.setText(eventData.getCollege());
        DateFormat sdf = new SimpleDateFormat("EEE, MMM d",Locale.US);
        holder.date.setText(sdf.format(eventData.getDate()));
        holder.organiser.setText(eventData.getOrganiser());
        if(eventData.getTech())
            holder.category.setText(R.string.techincal);
        else{
            holder.category.setText(R.string.cultural);
        }
        holder.star.setChecked(eventData.getPinned());
        holder.star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBHelperPin dbHelperPin = new DBHelperPin(context);
                if(holder.star.isChecked()){//save
                    dbHelperPin.insertPinnedEvent(eventData.getUniqueId());
                    Snackbar.make(holder.itemView,"Event Pinned",Snackbar.LENGTH_SHORT).show();
                }
                else{//remove
                    dbHelperPin.deletePinnedEvent(eventData.getUniqueId());
                    Snackbar.make(holder.itemView,"Event Un-Pinned",Snackbar.LENGTH_SHORT).show();
                }
                Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.shake_anim);
                holder.star.startAnimation(animation);
            }
        });
        //if post was pinned change color of star
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open event activity
                Intent intent = new Intent(view.getContext(),EventActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("uniqueId",eventData.getUniqueId());
                intent.putExtra("eventData",bundle);
                view.getContext().startActivity(intent);
            }
        });
        eventData.getDescSpan().setSpan(new BackgroundColorSpan(context.getResources().getColor(R.color.colorLightGrey)),0,eventData.getDesc().length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.descp.setText(eventData.getDescSpan());
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }


    public int search(String charText) {

            charText = charText.toLowerCase(Locale.getDefault());

            eventList.clear();

                for (EventData wp : searchEventList) {
                    Boolean found = false;
                    String name = wp.getName().toLowerCase(Locale.getDefault());
                    String desc = wp.getDesc().toLowerCase(Locale.getDefault());
                    if (name.contains(charText)) {
                        int startPos = name.indexOf(charText);
                        int endPos = startPos + charText.length();
                        Spannable span = Spannable.Factory.getInstance().newSpannable(wp.getName());
                        span.setSpan(new ForegroundColorSpan(Color.RED),startPos,endPos,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        wp.setName(span);
                        found = true;
                    }
                    if (desc.contains(charText)) {
                        int startPos = desc.indexOf(charText);
                        int endPos = startPos + charText.length();
                        Spannable span = Spannable.Factory.getInstance().newSpannable(wp.getDesc());
                        span.setSpan(new ForegroundColorSpan(Color.RED),startPos,endPos,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        wp.setDesc(span);
                        found=true;
                    }
                    if(found)
                        eventList.add(wp);

                }

            notifyDataSetChanged();
                if(eventList.size()>0)//if data is empty
                    return 1;
                else
                    return 0;

    }
    public void updateSearchList(){//run this to use search functionality...
        searchEventList.clear();
        searchEventList.addAll(eventList);
    }
    public void setOriginalEventList(){
        originalEventList = new ArrayList<>();
        originalEventList.addAll(eventList);
    }

    public List<EventData> getOriginalEventList() {
        return originalEventList;
    }

    public void sortByCollege(String college){
        eventList.clear();
        eventList.addAll(getOriginalEventList());
        List<EventData> a = new ArrayList<>();
        if(!Objects.equals(college, "-")) { //for reset
            for (int i = 0; i < eventList.size(); i++) {
                if (!Objects.equals(eventList.get(i).getCollege(), college))
                    a.add(eventList.get(i));
            }
        }
        eventList.removeAll(a);
        updateSearchList();
        notifyDataSetChanged();
    }

    public void sortBySociety(String society){
        eventList.clear();
        eventList.addAll(getOriginalEventList());
        List<EventData> a = new ArrayList<>();
        if(!Objects.equals(society, "-")) { //for reset
            for (int i = 0; i < eventList.size(); i++) {
                if (!Objects.equals(eventList.get(i).getOrganiser(), society))
                    a.add(eventList.get(i));
            }
        }
        eventList.removeAll(a);
        updateSearchList();
        notifyDataSetChanged();
        //change original list here
    }

    public void sortBySubscribedSociety(List<String> sub){
        if(getOriginalEventList()!=null) {
            eventList.clear();
            List<EventData> a = new ArrayList<>();
            a.addAll(getOriginalEventList());
            for (String s : sub) {//1st element is for name for sorting
                for (int i = 0; i < a.size(); i++) {
                    if (Objects.equals(a.get(i).getOrganiser(), s))
                        eventList.add(a.get(i));
                }
            }
            updateSearchList();
            notifyDataSetChanged();
        }
    }

    public void sortByTime(){//bubble sorting
        boolean swap;
        for(int j=0;j<eventList.size()-1;j++){
            swap=false;
            for(int i=0;i<eventList.size()-1;i++){
                if(eventList.get(i).getDate().after(eventList.get(i+1).getDate())){
                    //swap both
                    EventData e = eventList.get(i);
                    eventList.set(i,eventList.get(i+1));
                    eventList.set(i+1,e);
                    swap=true;
                }
            if(!swap)
                break;
            }
        }

    }

    public void sortByType(String type){
        for(EventData e:eventList){
            if(!Objects.equals(e.getTech(), type)){
                eventList.remove(e);
            }
        }
        updateSearchList();
        notifyDataSetChanged();
    }

    public void eventsToday(){
        eventList.clear();
        Calendar c = Calendar.getInstance();
        Calendar d = Calendar.getInstance();
        for(EventData e : searchEventList){
            c.setTime(e.getDate());
            if(c.get(Calendar.YEAR)==d.get(Calendar.YEAR) && c.get(Calendar.DAY_OF_YEAR)==d.get(Calendar.DAY_OF_YEAR)){
                eventList.add(e);
            }
        }
        notifyDataSetChanged();
        updateSearchList();

    }
    public void eventsThisWeek(){
        eventList.clear();
        Calendar c = Calendar.getInstance();
        Calendar d = Calendar.getInstance();
        for(EventData e : searchEventList){
            c.setTime(e.getDate());
            if(c.get(Calendar.YEAR)==d.get(Calendar.YEAR) && c.get(Calendar.WEEK_OF_YEAR)==d.get(Calendar.WEEK_OF_YEAR)){
                eventList.add(e);
            }
        }
        notifyDataSetChanged();
        updateSearchList();

    }
    public void eventsThisMonth(){
        eventList.clear();
        Calendar c = Calendar.getInstance();
        Calendar d = Calendar.getInstance();
        for(EventData e : searchEventList){
            c.setTime(e.getDate());
            if(c.get(Calendar.YEAR)==d.get(Calendar.YEAR) && c.get(Calendar.MONTH)==d.get(Calendar.MONTH)){
                eventList.add(e);
            }
        }
        notifyDataSetChanged();
        updateSearchList();

    }

    public void sortTech(Boolean tech){
        eventList.clear();
        for(EventData e : searchEventList){
            if(e.getTech()==tech)
                eventList.add(e);
        }
        notifyDataSetChanged();
        updateSearchList();
    }

    public void reset(){
        eventList.clear();
        eventList.addAll(getOriginalEventList());
        updateSearchList();
        notifyDataSetChanged();
    }




}
