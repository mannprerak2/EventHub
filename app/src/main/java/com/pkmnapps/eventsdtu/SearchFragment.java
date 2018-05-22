package com.pkmnapps.eventsdtu;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import mehdi.sakout.fancybuttons.FancyButton;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    List<EventData> eventDataList = new ArrayList<>();
    RecyclerView recyclerView;
    EventAdapter eventAdapter;
    View view;
    SearchView searchView;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search, container, false);

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

        //clear previous data
        eventDataList.clear();

        searchView = (SearchView)view.findViewById(R.id.search);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                eventAdapter.search(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(eventAdapter.search(s)==0)
                    recyclerView.setAdapter(new EmptyAdapter("No results found"));
                else
                    recyclerView.setAdapter(eventAdapter);

                if(Objects.equals(s, "")){
                    //change adapter here
                    recyclerView.setAdapter(new EmptyImageAdapter(R.drawable.ic_search_black_24dp));
                }

                    return true;
            }
        });


        FancyButton societySearch = (FancyButton)view.findViewById(R.id.searchSociety);
        societySearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //load all societies activity here
                Intent intent = new Intent(getContext(),AllViewActivity.class);
                //put extras in intent
                intent.putExtra("type","AllSocieties");
                intent.putExtra("search",true);
                startActivity(intent);
            }
        });

        FancyButton collegeSearch = (FancyButton)view.findViewById(R.id.searchCollege);
        collegeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //load all societies activity here
                Intent intent = new Intent(getContext(),AllViewActivity.class);
                //put extras in intent
                intent.putExtra("type","AllColleges");
                intent.putExtra("search",true);
                startActivity(intent);
            }
        });



        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);


        eventAdapter = new EventAdapter(eventDataList,getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(new LoadingAdapter());//loading bar displayed initially
        recyclerView.setNestedScrollingEnabled(false);//for momentum scrolling


        prepareEventData();

    }

    private void prepareEventData(){


        if(EventSingleton.getInstance().getObjectListEvent()!=null){
            DBHelperPin dbHelperPin = new DBHelperPin(getContext());
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
            searchView.setQuery(searchView.getQuery(),true);

        }
        else {//if data was not loaded before we need to load it here...
            FirebaseFirestore.getInstance().collection("/EventsPAS").document("EVENTS").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        recyclerView.setAdapter(eventAdapter);
                        recyclerView.addItemDecoration(new DividerItemDecoration(
                                recyclerView.getContext(), 1));

                        DBHelperPin dbHelperPin = new DBHelperPin(getContext());
                        DocumentSnapshot document = task.getResult();
                        //save data to save read call again and again
                        EventSingleton.getInstance().setObjectListEvent(document.getData());

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

                        searchView.setQuery(searchView.getQuery(),true);
                    } else {
                        Toast.makeText(getContext(), "oops", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
