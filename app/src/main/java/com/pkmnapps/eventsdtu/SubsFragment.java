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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SubsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SubsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SubsFragment extends Fragment {
    List<EventData> eventDataList = new ArrayList<>();
    List<String[]> subsData = new ArrayList<>();
    RecyclerView recyclerViewVertical,recyclerViewHorizontal;
    EventAdapter eventAdapter;
    SubsAdapter subsAdapter;
    List<String[]> subsList = new ArrayList<>();
    View view;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SubsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SubsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SubsFragment newInstance(String param1, String param2) {
        SubsFragment fragment = new SubsFragment();
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
        view = inflater.inflate(R.layout.fragment_subs, container, false);


        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        TextView textView = (TextView) view.findViewById(R.id.textView_all);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),AllViewActivity.class).putExtra("type","SubsSociety"));
            }
        });

        //clear previous data...
        subsData.clear();
        subsList.clear();
        eventDataList.clear();

        //add subs data...........
        recyclerViewHorizontal = (RecyclerView)view.findViewById(R.id.recycler_view_horizontal);
        subsAdapter = new SubsAdapter(subsData);
        RecyclerView.LayoutManager mLayoutManagerH = new LinearLayoutManager(view.getContext(),LinearLayoutManager.HORIZONTAL,false);
        recyclerViewHorizontal.setLayoutManager(mLayoutManagerH);
        recyclerViewHorizontal.setHasFixedSize(true);
        recyclerViewHorizontal.setItemAnimator(new DefaultItemAnimator());
        recyclerViewHorizontal.setAdapter(subsAdapter);
        recyclerViewHorizontal.setNestedScrollingEnabled(false);//for momentum scrolling

        prepareSubsData();
        //if u havent subscribed to any society
        if(subsAdapter.getItemCount()==0){
            //display no subs...
            recyclerViewHorizontal.setAdapter(new EmptyAdapter("No subscriptions"));
            //remove all button
            textView.setVisibility(View.GONE);
            textView.setOnClickListener(null);//clear up memory
        }


        //adding event data ........................
        recyclerViewVertical = (RecyclerView) view.findViewById(R.id.recycler_view_vertical);
        eventAdapter = new EventAdapter(eventDataList,getContext());

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerViewVertical.setLayoutManager(mLayoutManager);
        recyclerViewVertical.setItemAnimator(new DefaultItemAnimator());

        recyclerViewVertical.setAdapter(new LoadingAdapter());//loading bar displayed initially
        recyclerViewVertical.setNestedScrollingEnabled(false);//for momentum scrolling

        prepareEventData();
        //event data added...........................


        //sort event data according to subscribed societies
        //get a list of only names
        List<String> l = new ArrayList<>();
        for(String[] s : subsData){
            l.add(s[1]);// 1 id for names
        }
        eventAdapter.sortBySubscribedSociety(l); // 1 is for names

        //if no events are displayed
        if(eventAdapter.getItemCount()==0){
            //display no pinned events ...
            recyclerViewVertical.setAdapter(new EmptyAdapter("Events by your subscribed societies appear here"));
        }

    }

    public void prepareSubsData(){
        DBHelperSubs dbHelperSubs = new DBHelperSubs(getContext());

        subsData.addAll(dbHelperSubs.getAllSubsSociety());

        subsAdapter.notifyDataSetChanged();

    }


    private void prepareEventData(){


        if(EventSingleton.getInstance().getObjectListEvent()!=null){
            DBHelperPin dbHelperPin = new DBHelperPin(getContext());

            recyclerViewVertical.setAdapter(eventAdapter);
            recyclerViewVertical.addItemDecoration(new DividerItemDecoration(
                    recyclerViewVertical.getContext(), 1));

//            for ( Object o : EventSingleton.getInstance().getObjectListEvent()){
//
//                ArrayList<Object> arr = (ArrayList<Object>) o;
//                EventData e = new EventData(arr);
//                if (dbHelperPin.isInPinnedDataBase((String) arr.get(4)))
//                    e.setPinned(true);
//                eventDataList.add(e);
//                eventAdapter.notifyDataSetChanged();
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
                }
                eventDataList.add(e);
                eventAdapter.notifyDataSetChanged();
            }
            eventAdapter.updateSearchList();
            eventAdapter.setOriginalEventList();
        }
        else {//if data was not loaded before we need to load it here...
            FirebaseFirestore.getInstance().collection("/EventsPAS").document("EVENTS").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        recyclerViewVertical.setAdapter(eventAdapter);
                        recyclerViewVertical.addItemDecoration(new DividerItemDecoration(
                                recyclerViewVertical.getContext(), 1));

                        DBHelperPin dbHelperPin = new DBHelperPin(getContext());
                        DocumentSnapshot document = task.getResult();
                        //save this data to save read calls again and again
                        EventSingleton.getInstance().setObjectListEvent(document.getData());

//                        for (Object o : document.getData().values().toArray()) {
//
//                            ArrayList<Object> arr = (ArrayList<Object>) o;
//                            EventData e = new EventData(arr);
//                            if (dbHelperPin.isInPinnedDataBase((String) arr.get(4)))
//                                e.setPinned(true);
//                            eventDataList.add(e);
//                            eventAdapter.notifyDataSetChanged();
//
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
                            }
                            eventDataList.add(e);
                            eventAdapter.notifyDataSetChanged();
                        }
                        eventAdapter.updateSearchList();
                        eventAdapter.setOriginalEventList();

                    } else {
                        Toast.makeText(getContext(), "oops", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public class SubsAdapter extends RecyclerView.Adapter<SubsAdapter.MyViewHolder>{
        private List<String[]> imageLinks;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            public ImageButton button;

            public MyViewHolder(View view) {
                super(view);

                button = (ImageButton)view.findViewById(R.id.button_image_subs);


            }
        }


        public SubsAdapter(List<String[]> links) {
            this.imageLinks = links;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ll_subs_item, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            final Context context = holder.itemView.getContext();
            final String[] a = imageLinks.get(position);
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //open organiser page here
                    Intent intent = new Intent(view.getContext(),OrganiserActivity.class);
                    intent.putExtra("uniqueId",a[0]);//0 was uniqueID
                    view.getContext().startActivity(intent);
                }
            });
            final String imageLink = a[2];// 2nd is imagelink
            //image loading...
            if(imageLink!=null) {
                final DBHelperImages dbHelperImages = new DBHelperImages(context);

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference().child("Societies");
                final StorageReference imageRef = storageRef.child(imageLink);

                if (!dbHelperImages.isInImageDatabase(imageLink)) { //download data if not in database
                    final File localFile = new File(context.getCacheDir(), imageLink);//we store download link because if we change file on server it changes the link too

                    imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            //file downloaded now , open this in your imageview
                            Picasso.with(context).load(localFile).into(holder.button);
                            //save this to database now
                            dbHelperImages.insertImage(imageLink, imageLink);
                        }
                    });
                } else { // image is in database load it here
                    Toast.makeText(context, "database", Toast.LENGTH_SHORT).show();
                    File localFile = new File(context.getCacheDir(), imageLink);//we store download link because if we change file on server it changes the link too
                    Picasso.with(context).load(localFile).into(holder.button);
                }
            }
        }


        @Override
        public int getItemCount() {
            return imageLinks.size();
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
