package com.pkmnapps.eventsdtu;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by prerak on 13/3/18.
 */

public class EventSingleton {

    private static EventSingleton eventsInstance = null;

    private Map<String,Object> objectListEvent = null;//give big objects e1, e2, e3
    private Map<String,Object> objectListSociety = null;
    private Map<String,Object> objectListCollege = null;
    private Map<String,String> featuredList = null;

    private EventSingleton(){
        this.objectListEvent = null;

    }

    public static EventSingleton getInstance(){

        if(eventsInstance==null)
            eventsInstance = new EventSingleton();

        return eventsInstance;
    }

    public Map<String,Object> getObjectListEvent() {
        return objectListEvent;
    }

    public void setObjectListEvent(Map<String,Object> objectListEvent) {
        this.objectListEvent = objectListEvent;
    }

    public Map<String, Object> getObjectListSociety() {
        return objectListSociety;
    }

    public void setObjectListSociety(Map<String, Object> objectListSociety) {
        this.objectListSociety = objectListSociety;
    }

    public Map<String, Object> getObjectListCollege() {
        return objectListCollege;
    }

    public void setObjectListCollege(Map<String, Object> objectListCollege) {
        this.objectListCollege = objectListCollege;
    }

    public Map<String,String> getFeaturedList() {
        return featuredList;
    }

    public void setFeaturedList(Map<String,String> featuredList) {
        this.featuredList = featuredList;
    }
}
