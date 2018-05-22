package com.pkmnapps.eventsdtu;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;


public class DashFragment extends Fragment {

    View view;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public DashFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static DashFragment newInstance(String param1, String param2) {
        DashFragment fragment = new DashFragment();
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
        view =inflater.inflate(R.layout.fragment_dash, container, false);
        // Inflate the layout for this fragment



        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FancyButton eventsButton = (FancyButton)view.findViewById(R.id.eventsButton);
        eventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //load all events activity here
                Intent intent = new Intent(getContext(),AllViewActivity.class);
                //put extras in intent
                intent.putExtra("type","AllEvents");
                startActivity(intent);
            }
        });

        FancyButton societyButton = (FancyButton) view.findViewById(R.id.societiesButton);
        societyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //load all societies activity here
                Intent intent = new Intent(getContext(),AllViewActivity.class);
                //put extras in intent
                intent.putExtra("type","AllSocieties");
                startActivity(intent);
            }
        });

        FancyButton collegeButton = (FancyButton) view.findViewById(R.id.collegesButton);
        collegeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //load all societies activity here
                Intent intent = new Intent(getContext(),AllViewActivity.class);
                //put extras in intent
                intent.putExtra("type","AllColleges");
                startActivity(intent);
            }
        });

        FancyButton subscriptions = (FancyButton) view.findViewById(R.id.subscribedButton);
        subscriptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),AllViewActivity.class).putExtra("type","SubsSociety"));

            }
        });
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
