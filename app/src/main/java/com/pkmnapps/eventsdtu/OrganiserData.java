package com.pkmnapps.eventsdtu;

import android.text.Spannable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by prerak on 1/3/18.
 */

public class OrganiserData {
    private String uniqueId;
    private String desc,location,logoLink;
    private Spannable name;
    private String college;
    private Boolean subscribed = false;


    public OrganiserData() {
    }

    public OrganiserData(String name, String desc, String location, String college, String uniqueId) {
        this.name = Spannable.Factory.getInstance().newSpannable(name);
        this.desc = desc;
        this.location = location;
        this.college = college;
        this.uniqueId = uniqueId;
        subscribed = false;
    }
    public OrganiserData(String name, String desc, String location, String college, String uniqueId, Boolean subscribed) {
        this.name = Spannable.Factory.getInstance().newSpannable(name);;
        this.desc = desc;
        this.location = location;
        this.college = college;
        this.uniqueId = uniqueId;
        this.subscribed = subscribed;
    }
    public OrganiserData(ArrayList<Object> arrayList){
        this.desc = (String) arrayList.get(1);
        this.college = (String)arrayList.get(2);
        this.name = Spannable.Factory.getInstance().newSpannable((String)arrayList.get(0));
        this.uniqueId = (String)arrayList.get(3);
        this.logoLink = (String)arrayList.get(4);
        this.subscribed = false;
    }
    public OrganiserData(HashMap<String,Object> map){
        this.desc = (String) map.get("desc");
        this.college = (String)map.get("college");
        this.name = Spannable.Factory.getInstance().newSpannable((String)map.get("name"));
        this.uniqueId = (String)map.get("uniqueid");
        this.logoLink = (String)map.get("logolink");
        this.subscribed = false;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public Boolean getSubscribed() {
        return subscribed;
    }

    public void setSubscribed(Boolean subscribed) {
        this.subscribed = subscribed;
    }

    public String getLogoLink() {
        return logoLink;
    }

    public void setLogoLink(String logoLink) {
        this.logoLink = logoLink;
    }
}
