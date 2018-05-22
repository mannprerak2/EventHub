package com.pkmnapps.eventsdtu;

import android.content.Context;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PinnedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PinnedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PinnedFragment extends Fragment {

    List<EventData> eventDataList = new ArrayList<>();
    RecyclerView recyclerView;
    EventAdapter eventAdapter;
    View view;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public PinnedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PinnedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PinnedFragment newInstance(String param1, String param2) {
        PinnedFragment fragment = new PinnedFragment();
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
        view =inflater.inflate(R.layout.fragment_pinned, container, false);

        // Inflate the layout for this fragment
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        //clear previous data
        eventDataList.clear();

        eventAdapter = new EventAdapter(eventDataList,getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(new LoadingAdapter());//loading bar displayed initially

        recyclerView.setNestedScrollingEnabled(false);//for momentum scrolling

        prepareEventData();

        if(eventAdapter.getItemCount()==0){
            //display no pinned events ...
            recyclerView.setAdapter(new EmptyAdapter("No Pinned Events"));
        }
    }

    //    private void prepareEventData(){
//        final DBHelperPin dbHelper = new DBHelperPin(getContext());
//        for(final String id : dbHelper.getAllImages()){//running for each entry in pinned database
//            FirebaseFirestore.getInstance().collection("/EventsPAS/EVENTS/EVENTSc").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    if(task.isSuccessful()){
//                        recyclerViewVertical.setAdapter(eventAdapter);
//                        recyclerViewVertical.addItemDecoration(new DividerItemDecoration(
//                                recyclerViewVertical.getContext(),1));
//                            DocumentSnapshot document = task.getResult();
//
//                            if(document!=null && document.exists()) {
//                                EventData e = document.toObject(EventData.class);
//                                e.setUniqueId(document.getId());
//                                if (dbHelper.isInImageDatabase(document.getId()))
//                                    e.setPinned(true);
//                                eventDataList.add(e);
//                                eventAdapter.notifyDataSetChanged();
//                            }
//                            else{
//                                //remove document from pinned database
//                                dbHelper.deleteImage(id);
//                                Toast.makeText(getContext(),"not found - "+id,Toast.LENGTH_SHORT).show();
//                            }
//
//                    }
//                    else {
//                        Toast.makeText(getContext(),"oops",Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//        }
//    }

    private void prepareEventData(){


        if(EventSingleton.getInstance().getObjectListEvent()!=null){
            recyclerView.setAdapter(eventAdapter);
            recyclerView.addItemDecoration(new DividerItemDecoration(
                    recyclerView.getContext(), 1));
            DBHelperPin dbHelperPin = new DBHelperPin(getContext());
//            for ( Object o : EventSingleton.getInstance().getObjectListEvent()){
//                ArrayList<Object> arr = (ArrayList<Object>) o;
//                EventData e = new EventData(arr);
//                if (dbHelperPin.isInPinnedDataBase((String) arr.get(4))) {
//                    e.setPinned(true);
//                    eventDataList.add(e);
//                    eventAdapter.notifyDataSetChanged();
//                }
//            }
            Object[] values = EventSingleton.getInstance().getObjectListEvent().values().toArray();
            String[] keys = EventSingleton.getInstance().getObjectListEvent().keySet().toArray(new String[0]);
            for(int i=0;i<keys.length;i++){
                String uniqueId = keys[i];
                HashMap<String,Object> map = (HashMap<String,Object>)values[i];
                map.put("uniqueid",uniqueId);
                EventData e = new EventData(map);
                if(dbHelperPin.isInPinnedDataBase(uniqueId)){
                    e.setPinned(true);
                    eventDataList.add(e);
                }
                eventAdapter.notifyDataSetChanged();
            }
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
                        //save this data to save read calls again and again
                        EventSingleton.getInstance().setObjectListEvent(document.getData());

//                        for (Object o : document.getData().values().toArray()) {
//
//                            ArrayList<Object> arr = (ArrayList<Object>) o;
//                            EventData e = new EventData(arr);
//                            if (dbHelperPin.isInPinnedDataBase((String) arr.get(4))) {
//                                e.setPinned(true);
//                                eventDataList.add(e);
//                                eventAdapter.notifyDataSetChanged();
//                            }
//                        }
                        Object[] values = document.getData().values().toArray();
                        String[] keys = document.getData().keySet().toArray(new String[0]);
                        for(int i=0;i<keys.length;i++){
                            String uniqueId = keys[i];
                            HashMap<String,Object> map = (HashMap<String,Object>)values[i];
                            map.put("uniqueid",uniqueId);
                            EventData e = new EventData(map);
                            if(dbHelperPin.isInPinnedDataBase(uniqueId)){
                                e.setPinned(true);
                                eventDataList.add(e);
                            }
                            eventAdapter.notifyDataSetChanged();
                        }


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
