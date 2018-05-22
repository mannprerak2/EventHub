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
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FeaturedHomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FeaturedHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeaturedHomeFragment extends Fragment {


    List<EventData> eventDataList = new ArrayList<>();
    RecyclerView recyclerView;
    EventAdapter eventAdapter;
    SliderLayout sliderLayout;
    View view;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FeaturedHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FeaturedHomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FeaturedHomeFragment newInstance(String param1, String param2) {
        FeaturedHomeFragment fragment = new FeaturedHomeFragment();
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
        view =inflater.inflate(R.layout.fragment_featured_home, container, false);
        sliderLayout = (SliderLayout)view.findViewById(R.id.slider);
        sliderLayout.stopAutoCycle();//keep cycle at halt initially
        prepareFeaturedData();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();


        //clear previous data...
        eventDataList.clear();



        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        eventAdapter = new EventAdapter(eventDataList,getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(new LoadingAdapter());//loading bar displayed initially

        recyclerView.setFocusable(false); // for keeping image slider at top
        recyclerView.setNestedScrollingEnabled(false);//for momentum scrolling


        prepareEventData();

    }

    private void prepareFeaturedData(){

        if(EventSingleton.getInstance().getFeaturedList()!=null){

                featuredImagesDownloader(EventSingleton.getInstance().getFeaturedList());

        }
        else{//download data from net as list is empty
            FirebaseFirestore.getInstance().collection("/EventsPAS").document("FEATURED").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        //put ids to string list
                        Map<String,Object> map = task.getResult().getData();

                        Map<String,String> featuredList =new HashMap<>();
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            if(entry.getValue() instanceof String){
                                featuredList.put(entry.getKey(), (String) entry.getValue());
                            }
                        }

                        //download them all and display them
                        featuredImagesDownloader(featuredList);

                        //put these in instance
                        EventSingleton.getInstance().setFeaturedList(featuredList);

                    } else {
                        Toast.makeText(getContext(), "oops", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    private void featuredImagesDownloader(Map<String,String> imageList){
        //loop through images
        final Context context = getContext();
        for(final String id:imageList.keySet()){
            //image loading...
            final String filename = imageList.get(id);//change the extension accordingly
            final DBHelperImages dbHelperImages = new DBHelperImages(context);

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference().child("Events");
            final StorageReference imageRef = storageRef.child(filename);

            sliderLayout.startAutoCycle();

                if(context!=null) {//if activity was changed while we downloaded
                    if (!dbHelperImages.isInImageDatabase(filename)) { //download data if not in database
                        final File localFile = new File(context.getCacheDir(), filename);//we store download link because if we change file on server it changes the link too
                        imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                if(context!=null) {
                                    //file downloaded now , load in slider
                                    DefaultSliderView defaultSliderView = new DefaultSliderView(context);
                                    defaultSliderView.image(localFile).setScaleType(BaseSliderView.ScaleType.CenterCrop);
                                    defaultSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                        @Override
                                        public void onSliderClick(BaseSliderView slider) {
                                            //open the activity here
                                            Intent intent = new Intent(view.getContext(),EventActivity.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("uniqueId",id);
                                            intent.putExtra("eventData",bundle);
                                            view.getContext().startActivity(intent);
                                        }
                                    });
                                    sliderLayout.addSlider(defaultSliderView);
                                    //save this to database now
                                    dbHelperImages.insertImage(filename, filename);
                                }
                            }
                        });
                    } else { // image is in database load it here
                        final File localFile = new File(context.getCacheDir(), filename);//we store download link because if we change file on server it changes the link too
                        if (!localFile.exists()) {//file doesn't exist on cache storage
                            //delete entry from image database
                            dbHelperImages.deleteImage(filename);
                            //rerun downloader
                            Toast.makeText(getContext(),"rerun",Toast.LENGTH_SHORT).show();
                            imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    if(context!=null) {
                                        //file downloaded now , load in slider
                                        DefaultSliderView defaultSliderView = new DefaultSliderView(context);
                                        defaultSliderView.image(localFile).setScaleType(BaseSliderView.ScaleType.CenterCrop);
                                        defaultSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                            @Override
                                            public void onSliderClick(BaseSliderView slider) {
                                                //open the activity here
                                                Intent intent = new Intent(view.getContext(),EventActivity.class);
                                                Bundle bundle = new Bundle();
                                                bundle.putString("uniqueId",id);
                                                intent.putExtra("eventData",bundle);
                                                view.getContext().startActivity(intent);
                                            }
                                        });
                                        sliderLayout.addSlider(defaultSliderView);
                                        //save this to database now
                                        dbHelperImages.insertImage(filename, filename);
                                    }
                                }
                            });
                        } else {//file exists on device load it offline
                            DefaultSliderView defaultSliderView = new DefaultSliderView(context);
                            defaultSliderView.image(localFile).setScaleType(BaseSliderView.ScaleType.CenterCrop);
                            defaultSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {
                                    //open the activity here
                                    Intent intent = new Intent(view.getContext(), EventActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("uniqueId", id);
                                    intent.putExtra("eventData", bundle);
                                    view.getContext().startActivity(intent);
                                }
                            });
                            sliderLayout.addSlider(defaultSliderView);
                        }
                    }
                }

            }

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
            //sort by time
            eventAdapter.sortByTime();
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
                        //sort by time
                        eventAdapter.sortByTime();
                    } else {
                        Toast.makeText(getContext(), "Network Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

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
        //sliderLayout.stopAutoCycle();
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
