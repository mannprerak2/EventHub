package com.pkmnapps.eventsdtu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import mehdi.sakout.fancybuttons.FancyButton;

public class AllViewActivity extends AppCompatActivity {
    String titleHeader ="";

    List<EventData> eventDataList = new ArrayList<>();
    List<OrganiserData> organiserDataList = new ArrayList<>();
    List<CollegeData> collegeDataList = new ArrayList<>();

    EventAdapter eventAdapter;
    OrganiserAdapter organiserAdapter;
    CollegeAdapter collegeAdapter;

    SearchView search;

    RecyclerView recyclerView;
    BottomSheetLayout bottomSheet;
    String type;
    Boolean searchBool;
    LinearLayout filterView;

    List<String> filterStrings = new ArrayList<>();

    Boolean todayB,weekB,monthB,tech,category,filter=false;

    Intent filterData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        type = i.getStringExtra("type");
        searchBool = i.getBooleanExtra("search",false);
        switch (type) {
            case "AllEvents":
                titleHeader = "Events";
                type = "e";
                break;
            case "AllSocieties":
                titleHeader = "Societies";
                type = "s";
                break;
            case "AllColleges":
                titleHeader = "Colleges";
                type = "c";
                break;
            case "SubsSociety":
                titleHeader = "Subscribed societies";
                type = "sl";
                break;
            default:
                Toast.makeText(this, "Bad intent", Toast.LENGTH_SHORT).show();
                break;
        }


         recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(true);//for momentum scrolling

        recyclerView.setAdapter(new LoadingAdapter());//loading bar displayed initially


    }

    @Override
    protected void onStart() {
        super.onStart();

        switch (type) {
            case "e":
                if(societyEventSetup()) {//doesnt show filter tab if its societyevents
                    //show filterStrings recyclerview
                    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayoutFilter);
                    linearLayout.setVisibility(View.VISIBLE);
                    filterView = (LinearLayout) findViewById(R.id.filterViews);
                    final FancyButton filter = (FancyButton) findViewById(R.id.filterButton);
                    filter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(AllViewActivity.this, FilterActivity.class);
                            intent.putExtra("type", type);//tells it which search we need
                            intent.putExtra("filterList", (ArrayList<String>) filterStrings);
                            if (Objects.equals(type, "e")) //eventListFilter
                                startActivityForResult(intent, 1);
                            else if (Objects.equals(type, "s") || Objects.equals(type, "sl"))//societyListFilter
                                startActivityForResult(intent, 2);
                        }
                    });
                }
                else{
                    titleHeader="Events by "+getIntent().getStringExtra("societyEvents");
                }
                prepareEventData(this);
                break;
            case "s":
                prepareSocietyData(this);
                break;
            case "c":
                prepareCollegeData(this);
                break;
            case "sl":
                prepareSubsData(this);
        }

        getSupportActionBar().setTitle(titleHeader);

    }

    private void prepareSubsData(final Context context) {
        organiserDataList.clear();
        organiserAdapter = new OrganiserAdapter(organiserDataList);

        if(EventSingleton.getInstance().getObjectListSociety()!=null){
            DBHelperSubs dbHelper = new DBHelperSubs(context);

            recyclerView.setAdapter(organiserAdapter);
            recyclerView.addItemDecoration(new DividerItemDecoration(
                    recyclerView.getContext(), 1));

//            for ( Object o : EventSingleton.getInstance().getObjectListSociety()){
//
//                ArrayList<Object> arr = (ArrayList<Object>) o;
//                OrganiserData e = new OrganiserData(arr);
//                if (dbHelper.isInSubsDataBase((String) arr.get(3))) {
//                    e.setSubscribed(true);
//                    organiserDataList.add(e);
//                    organiserAdapter.notifyDataSetChanged();
//                }
//            }
            Object[] values = EventSingleton.getInstance().getObjectListSociety().values().toArray();
            String[] keys = EventSingleton.getInstance().getObjectListSociety().keySet().toArray(new String[0]);
            for(int i=0;i<keys.length;i++){
                String uniqueId = keys[i];
                HashMap<String,Object> map = (HashMap<String,Object>)values[i];
                map.put("uniqueid",uniqueId);
                OrganiserData e = new OrganiserData(map);
                if(dbHelper.isInSubsDataBase(uniqueId)){
                    e.setSubscribed(true);
                    organiserDataList.add(e);
                    organiserAdapter.notifyDataSetChanged();
                }
            }
            organiserAdapter.updateSearchList();
            organiserAdapter.setOriginalSocietyDataList();

        }
        else {//if data was not loaded before we need to load it here...
            FirebaseFirestore.getInstance().collection("/EventsPAS").document("ORGANISERS").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        recyclerView.setAdapter(organiserAdapter);
                        recyclerView.addItemDecoration(new DividerItemDecoration(
                                recyclerView.getContext(), 1));

                        DBHelperSubs dbHelper = new DBHelperSubs(context);
                        DocumentSnapshot document = task.getResult();
                        //save this data to save read calls again and again
                        EventSingleton.getInstance().setObjectListSociety(document.getData());

//                        for (Object o : document.getData().values().toArray()) {
//                            ArrayList<Object> arr = (ArrayList<Object>) o;
//                            OrganiserData e = new OrganiserData(arr);
//                            if (dbHelper.isInSubsDataBase((String) arr.get(3))) {
//                                e.setSubscribed(true);
//                                organiserDataList.add(e);
//                                organiserAdapter.notifyDataSetChanged();
//                            }
//                        }
                        Object[] values = document.getData().values().toArray();
                        String[] keys = document.getData().keySet().toArray(new String[0]);
                        for(int i=0;i<keys.length;i++){
                            String uniqueId = keys[i];
                            HashMap<String,Object> map = (HashMap<String,Object>)values[i];
                            map.put("uniqueid",uniqueId);
                            OrganiserData e = new OrganiserData(map);
                            if(dbHelper.isInSubsDataBase(uniqueId)){
                                e.setSubscribed(true);
                                organiserDataList.add(e);
                                organiserAdapter.notifyDataSetChanged();
                            }
                        }
                        organiserAdapter.updateSearchList();
                        organiserAdapter.setOriginalSocietyDataList();

                    } else {
                        Toast.makeText(context, "oops", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void prepareCollegeData(final Context context){
        collegeDataList.clear();
        collegeAdapter = new CollegeAdapter(collegeDataList);

        if(EventSingleton.getInstance().getObjectListCollege()!=null){
            DBHelperSubs dbHelper = new DBHelperSubs(context);

            recyclerView.setAdapter(collegeAdapter);
            recyclerView.addItemDecoration(new DividerItemDecoration(
                    recyclerView.getContext(), 1));

            Object[] values = EventSingleton.getInstance().getObjectListCollege().values().toArray();
            String[] keys = EventSingleton.getInstance().getObjectListCollege().keySet().toArray(new String[0]);
            for(int i=0;i<keys.length;i++){
                String uniqueId = keys[i];
                HashMap<String,Object> map = (HashMap<String,Object>)values[i];
                map.put("uniqueid",uniqueId);
                CollegeData e = new CollegeData(map);

                collegeDataList.add(e);
                collegeAdapter.notifyDataSetChanged();
            }

            collegeAdapter.updateSearchList();
            collegeAdapter.setOriginalCollegeList();


        }
        else {//if data was not loaded before we need to load it here...
            FirebaseFirestore.getInstance().collection("/EventsPAS").document("COLLEGES").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        recyclerView.setAdapter(collegeAdapter);
                        recyclerView.addItemDecoration(new DividerItemDecoration(
                                recyclerView.getContext(), 1));

                        DBHelperSubs dbHelper = new DBHelperSubs(context);
                        DocumentSnapshot document = task.getResult();
                        //save this data to save read calls again and again
                        EventSingleton.getInstance().setObjectListCollege(document.getData());

                        Object[] values = document.getData().values().toArray();
                        String[] keys = document.getData().keySet().toArray(new String[0]);
                        for(int i=0;i<keys.length;i++){
                            String uniqueId = keys[i];
                            HashMap<String,Object> map = (HashMap<String,Object>)values[i];
                            map.put("uniqueid",uniqueId);
                            CollegeData e = new CollegeData(map);

                            collegeDataList.add(e);
                            collegeAdapter.notifyDataSetChanged();
                        }
                        collegeAdapter.updateSearchList();
                        collegeAdapter.setOriginalCollegeList();

                        } else {
                        Toast.makeText(context, "oops", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void prepareSocietyData(final Context context){
        organiserDataList.clear();
        organiserAdapter = new OrganiserAdapter(organiserDataList);

        if(EventSingleton.getInstance().getObjectListSociety()!=null){
            DBHelperSubs dbHelper = new DBHelperSubs(context);

            recyclerView.setAdapter(organiserAdapter);
            recyclerView.addItemDecoration(new DividerItemDecoration(
                    recyclerView.getContext(), 1));

            Object[] values = EventSingleton.getInstance().getObjectListSociety().values().toArray();
            String[] keys = EventSingleton.getInstance().getObjectListSociety().keySet().toArray(new String[0]);
            for(int i=0;i<keys.length;i++){
                String uniqueId = keys[i];
                HashMap<String,Object> map = (HashMap<String,Object>)values[i];
                map.put("uniqueid",uniqueId);
                OrganiserData e = new OrganiserData(map);
                if(dbHelper.isInSubsDataBase(uniqueId)){
                    e.setSubscribed(true);
                }
                organiserDataList.add(e);
                organiserAdapter.notifyDataSetChanged();
            }
            organiserAdapter.updateSearchList();
            organiserAdapter.setOriginalSocietyDataList();


        }
        else {//if data was not loaded before we need to load it here...
            FirebaseFirestore.getInstance().collection("/EventsPAS").document("ORGANISERS").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        recyclerView.setAdapter(organiserAdapter);
                        recyclerView.addItemDecoration(new DividerItemDecoration(
                                recyclerView.getContext(), 1));

                        DBHelperSubs dbHelper = new DBHelperSubs(context);
                        DocumentSnapshot document = task.getResult();
                        //save this data to save read calls again and again
                        EventSingleton.getInstance().setObjectListSociety(document.getData());

                        Object[] values = document.getData().values().toArray();
                        String[] keys = document.getData().keySet().toArray(new String[0]);
                        for(int i=0;i<keys.length;i++){
                            String uniqueId = keys[i];
                            HashMap<String,Object> map = (HashMap<String,Object>)values[i];
                            map.put("uniqueid",uniqueId);
                            OrganiserData e = new OrganiserData(map);
                            if(dbHelper.isInSubsDataBase(uniqueId)){
                                e.setSubscribed(true);
                            }
                            organiserDataList.add(e);
                            organiserAdapter.notifyDataSetChanged();
                        }
                        organiserAdapter.updateSearchList();
                        organiserAdapter.setOriginalSocietyDataList();


                    } else {
                        Toast.makeText(context, "oops", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    private void prepareEventData(final Context context){
        eventDataList.clear();
        eventAdapter = new EventAdapter(eventDataList,context);


        if(EventSingleton.getInstance().getObjectListEvent()!=null){
            DBHelperPin dbHelperPin = new DBHelperPin(context);
            recyclerView.setAdapter(eventAdapter);
            recyclerView.addItemDecoration(new DividerItemDecoration(
                    recyclerView.getContext(), 1));

            Object[] values = EventSingleton.getInstance().getObjectListEvent().values().toArray();
            String[] keys = EventSingleton.getInstance().getObjectListEvent().keySet().toArray(new String[0]);
            for(int i=0;i<keys.length;i++){
                String uniqueId = keys[i];
                HashMap<String,Object> map = (HashMap<String,Object>)values[i];
                map.put("uniqueid",uniqueId);
                EventData e = new EventData(map);
                if(dbHelperPin.isInPinnedDataBase(uniqueId)){
                    e.setPinned(true);
                }
                eventDataList.add(e);
                eventAdapter.notifyDataSetChanged();
            }

            eventAdapter.updateSearchList();
            eventAdapter.setOriginalEventList();

            IfOnlySocietyEvent();
            if(filter)
                ApplyEventFilters(filterData);
        }
        else {//if data was not loaded before we need to load it here...
            FirebaseFirestore.getInstance().collection("/EventsPAS").document("EVENTS").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        recyclerView.setAdapter(eventAdapter);
                        recyclerView.addItemDecoration(new DividerItemDecoration(
                                recyclerView.getContext(), 1));

                        DBHelperPin dbHelperPin = new DBHelperPin(context);
                        DocumentSnapshot document = task.getResult();

                        //save data to save read call again and again
                        EventSingleton.getInstance().setObjectListEvent(document.getData());

                        Object[] values = document.getData().values().toArray();
                        String[] keys = document.getData().keySet().toArray(new String[0]);
                        for(int i=0;i<keys.length;i++){
                            String uniqueId = keys[i];
                            HashMap<String,Object> map = (HashMap<String,Object>)values[i];
                            map.put("uniqueid",uniqueId);
                            EventData e = new EventData(map);
                            if(dbHelperPin.isInPinnedDataBase(uniqueId)){
                                e.setPinned(true);
                            }
                            eventDataList.add(e);
                            eventAdapter.notifyDataSetChanged();
                        }
                        eventAdapter.updateSearchList();
                        eventAdapter.setOriginalEventList();

                        IfOnlySocietyEvent();
                        if(filter)
                            ApplyEventFilters(filterData);

                    } else {
                        Toast.makeText(context, "oops", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_all_view, menu);

        search = (SearchView) menu.findItem(R.id.search).getActionView();


        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                switch (type) {
                    case "e"://event
                        if(eventAdapter.search(query)==0)
                            recyclerView.setAdapter(new EmptyAdapter("No results found"));
                        else
                            recyclerView.setAdapter(eventAdapter);
                        break;
                    case "s"://society
                        if(organiserAdapter.filter(query)==0)
                            recyclerView.setAdapter(new EmptyAdapter("No results found"));
                        else
                        recyclerView.setAdapter(organiserAdapter);
                        break;
                    case "c"://college
                        if(collegeAdapter.filter(query)==0)
                            recyclerView.setAdapter(new EmptyAdapter("No results found"));
                        else
                        recyclerView.setAdapter(collegeAdapter);
                        break;
                    case "sl":
                        if(organiserAdapter.filter(query)==0)
                            recyclerView.setAdapter(new EmptyAdapter("No results found"));
                        else
                            recyclerView.setAdapter(organiserAdapter);
                        break;
                    default:

                        break;
                }
                return true;
            }

        });
        if(searchBool)
            search.setIconified(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //result from search activity

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) { //events
            if(resultCode == RESULT_OK) {
                filterData = data;
                filter = true;
                //filtering done in prepare event data
            }
        }
        else if (requestCode == 2) {//society / subscribed societies
            if(resultCode == RESULT_OK) {
                ApplySocietyFilters(data);
            }
        }
    }

    public void EventFilter(){
        LayoutInflater inflater = getLayoutInflater();
        final View filterLayout = inflater.inflate(R.layout.filter_event_layout,null);
        bottomSheet.showWithSheetView(filterLayout);

        final Spinner spinner_society = (Spinner)filterLayout.findViewById(R.id.spinner_society);
        final Spinner spinner_college = (Spinner)filterLayout.findViewById(R.id.spinner_college);

        String[] colleges = new String[]{"-","DTU","NSIT","IIIT"};
        String[] societies = new String[]{"-","CSI","VIBE","blockedcoders","NSS","Madhurima"};

        ArrayAdapter<String> adapter_college = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, colleges);
        adapter_college.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> adapter_society = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, societies);
        adapter_society.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_college.setAdapter(adapter_college);
        spinner_society.setAdapter(adapter_society);

        //set on selection here
        spinner_college.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //apply changes to list
                String college = spinner_college.getSelectedItem().toString();
                eventAdapter.sortByCollege(college);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //do nothing
            }
        });

        spinner_society.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //apply changes to list

                String society = spinner_society.getSelectedItem().toString();
                eventAdapter.sortBySociety(society);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //do nothing
            }
        });
    }

    private void SocietyFilter() {
        LayoutInflater inflater = getLayoutInflater();
        final View filterLayout = inflater.inflate(R.layout.filter_society_layout,null);
        bottomSheet.showWithSheetView(filterLayout);

        final Spinner spinner_college = (Spinner)filterLayout.findViewById(R.id.spinner_college);

        String[] colleges = new String[]{"-","DTU","NSIT","IIIT"};

        ArrayAdapter<String> adapter_college = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, colleges);
        adapter_college.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        spinner_college.setAdapter(adapter_college);

        //set on selection here
        spinner_college.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //apply changes to list
                String college = spinner_college.getSelectedItem().toString();
                organiserAdapter.sortByCollege(college);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //do nothing
            }
        });

    }

    private void ApplyEventFilters(Intent data){
        filterStrings.clear();

        Bundle bundle = data.getBundleExtra("filters");

        setBools(bundle);

        //reset events in eventadapter
        eventAdapter.reset();

        if(monthB){
            eventAdapter.eventsThisMonth();
            filterStrings.add(" This Month ");
        }
        else if(weekB){
            eventAdapter.eventsThisWeek();
            filterStrings.add(" This Week ");
        }
        else if(todayB){
            eventAdapter.eventsToday();
            filterStrings.add(" Today ");
        }
        if(category) {
            if (tech) {
                filterStrings.add(" Technical ");
                eventAdapter.sortTech(tech);
            }
            else {
                eventAdapter.sortTech(tech);
                filterStrings.add(" Cultural ");
            }
        }
        removeAllFilterViews();

        for(String s: filterStrings){
            addViewsFilter(s);
        }

    }
    private void ApplySocietyFilters(Intent data){

    }

    private void addViewsFilter(String s){
        FancyButton filterButton = new FancyButton(this);
        filterButton.setBackgroundColor(Color.parseColor("#4090ff"));
        filterButton.setFocusBackgroundColor(Color.parseColor("#5474b8"));
        filterButton.setRadius(100);
        filterButton.setPadding(10,2,10,2);
        filterButton.setTextGravity(View.TEXT_ALIGNMENT_CENTER);
        filterButton.setText(s);
        filterView.addView(filterButton);
    }
    private void removeAllFilterViews(){
        filterView.removeAllViews();
    }


    private void setBools(Bundle bundle){
        monthB = bundle.getBoolean("month");
        weekB = bundle.getBoolean("week");
        todayB = bundle.getBoolean("today");
        category = bundle.getBoolean("category");
        tech = bundle.getBoolean("tech");
    }

    private void IfOnlySocietyEvent(){
        String name = getIntent().getStringExtra("societyEvents");
        if(name!=null){
            //sort by society
            eventAdapter.sortBySociety(name);
        }
    }

    private boolean societyEventSetup(){
        String name = getIntent().getStringExtra("societyEvents");
        return name == null;
    }
}
