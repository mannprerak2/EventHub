package com.pkmnapps.eventsdtu;

import android.text.Spannable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by prerak on 1/3/18.
 */

public class CollegeData {

    private String uniqueId;
    private String fullName, location, ImageLink;
    private Spannable name;

    public CollegeData() {
    }

    public CollegeData(String name, String fullName, String location) {
        this.name = Spannable.Factory.getInstance().newSpannable(name);
        this.fullName = fullName;
        this.location = location;
    }

    public CollegeData(ArrayList<Object> arrayList){
        this.name = Spannable.Factory.getInstance().newSpannable((String)arrayList.get(0));
        this.fullName = (String)arrayList.get(2);
        this.uniqueId = (String)arrayList.get(1);
    }

    public CollegeData(HashMap<String,Object> map){
        this.name = Spannable.Factory.getInstance().newSpannable((String)map.get("name"));
        this.fullName = (String)map.get("fullname");
        this.uniqueId = (String)map.get("uniqueid");
        this.ImageLink = (String)map.get("image");
    }



    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
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


    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageLink() {
        return ImageLink;
    }

    public void setImageLink(String imageLink) {
        ImageLink = imageLink;
    }
}
