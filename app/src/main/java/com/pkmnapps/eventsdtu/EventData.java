package com.pkmnapps.eventsdtu;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by prerak on 10/2/18.
 */

public class EventData{
    private String uniqueId;
    private String location;
    private Spannable name,desc;
    private Date date;
    private String college,organiser;
    private Boolean tech; //type can be Cultural or Technical
    private Boolean pinned = false;

    public EventData() {
    }

    public EventData(String name, String desc, String location, Date date, String college, String organiser) {
        this.name = Spannable.Factory.getInstance().newSpannable(name);
        this.desc = Spannable.Factory.getInstance().newSpannable(desc);
        this.location = location;
        this.date = date;
        this.pinned = false;
        this.organiser = organiser;
        this.college = college;
    }

    public EventData(String name, String desc, String location, Date date, String college, String organiser, Boolean pinned) {
        this.name = Spannable.Factory.getInstance().newSpannable(name);
        this.desc = Spannable.Factory.getInstance().newSpannable(desc);
        this.location = location;
        this.date = date;
        this.pinned = false;
        this.organiser = organiser;
        this.college = college;
        this.pinned = pinned;
    }

    public EventData(Date date,String desc,String location, String name,String uniqueId){
        this.date = date;
        this.desc = Spannable.Factory.getInstance().newSpannable(desc);
        this.location = location;
        this.name = Spannable.Factory.getInstance().newSpannable(name);
        this.uniqueId = uniqueId;
        this.pinned = false;
    }

    public EventData(HashMap<String,Object> map) {
        this.uniqueId = (String)map.get("uniqueid");
        this.desc = Spannable.Factory.getInstance().newSpannable((String)map.get("desc"));
        this.name = Spannable.Factory.getInstance().newSpannable((String)map.get("name"));
        this.date = (Date) map.get("date");
        this.college = (String)map.get("college");
        this.organiser = (String)map.get("organiser");
        this.tech = (Boolean)map.get("tech");
    }

    public String getName() {
        return name.toString();
    }
    public Spannable getNameSpan(){return name;}
    public void setName(String name) {
        this.name = Spannable.Factory.getInstance().newSpannable(name);
    }
    public void setName(Spannable spannable){
        this.name = spannable;
    }

    public String getDesc() {
        return desc.toString();
    }

    public Spannable getDescSpan(){return desc;}

    public void setDesc(Spannable desc) {
        this.desc = desc;
    }

    public void setDesc(String desc) {
        this.desc = Spannable.Factory.getInstance().newSpannable(desc);
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Boolean getPinned() {
        return pinned;
    }

    public void setPinned(Boolean pinned) {
        this.pinned = pinned;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getOrganiser() {
        return organiser;
    }

    public void setOrganiser(String organiser) {
        this.organiser = organiser;
    }

    public Boolean getTech() {
        return tech;
    }

    public void setTech(Boolean type) {
        this.tech = type;
    }
}
