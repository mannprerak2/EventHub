package com.pkmnapps.eventsdtu;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CollegeActivity extends AppCompatActivity implements
                CollegeEventsFragment.OnFragmentInteractionListener,
                CollegeSocietiesFragment.OnFragmentInteractionListener{

    String uniqueId;
    String imageLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_college);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        TabLayout tabs = (TabLayout) findViewById(R.id.result_tabs);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        uniqueId = getIntent().getStringExtra("uniqueid");

        final ImageView imageViewEvent = (ImageView)findViewById(R.id.backdrop);
        //setting null backdrop image
        imageViewEvent.setImageDrawable(getResources().getDrawable(R.drawable.f7));

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("/EventsPAS/COLLEGES/COLLEGESc").document(uniqueId);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        imageLink = document.getString("image");

                        //image loading...
                        final DBHelperImages dbHelperImages = new DBHelperImages(CollegeActivity.this);
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReference().child("Colleges");
                        final StorageReference imageRef = storageRef.child(imageLink);
                        if(!dbHelperImages.isInImageDatabase(imageLink)) { //download data if not in database
                            final File localFile = new File(getCacheDir(), imageLink);//we store download link because if we change file on server it changes the link too
                            imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    //file downloaded now , open this in your imageview
                                    Picasso.with(CollegeActivity.this).load(localFile).into(imageViewEvent);
                                    //set image click listener
                                    imageViewEvent.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(CollegeActivity.this,ImageViewFullscreen.class);
                                            intent.putExtra("image",imageLink);
                                            startActivity(intent);
                                        }
                                    });
                                    //save this to database now
                                    dbHelperImages.insertImage(imageLink, imageLink);
                                }
                            });
                        }
                        else{ // image is in database load it here
                            final File localFile = new File(getCacheDir(), imageLink);//we store download link because if we change file on server it changes the link too
                            if(!localFile.exists()){//files doesnt exist on cache but exists on sql database
                                //remove the entry in database
                                dbHelperImages.deleteImage(imageLink);
                                //rerun downloader
                                Toast.makeText(getApplicationContext(),"rerun",Toast.LENGTH_SHORT).show();
                                imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        //file downloaded now , open this in your imageview
                                        Picasso.with(CollegeActivity.this).load(localFile).into(imageViewEvent);
                                        //set image click listener
                                        imageViewEvent.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent intent = new Intent(CollegeActivity.this,ImageViewFullscreen.class);
                                                intent.putExtra("image",imageLink);
                                                startActivity(intent);
                                            }
                                        });
                                        //save this to database now
                                        dbHelperImages.insertImage(imageLink, imageLink);
                                    }
                                });
                            }
                            else {
                                Picasso.with(CollegeActivity.this).load(localFile).into(imageViewEvent);
                                //set image click listener
                                imageViewEvent.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(CollegeActivity.this, ImageViewFullscreen.class);
                                        intent.putExtra("image", imageLink);
                                        startActivity(intent);
                                    }
                                });
                            }
                        }


                    }
                }
                else {
                    final Snackbar make =  Snackbar.make(viewPager,"Network Error",Snackbar.LENGTH_INDEFINITE);
                    make.setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            make.dismiss();
                            //recreate activity
                            recreate();
                        }
                    }).show();

                }
            }
        });

        // Set up the ViewPager with the sections adapter.
        setupViewPager(viewPager);

        // Set Tabs inside Toolbar
        tabs.setupWithViewPager(viewPager);

    }

    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {

        CollegeActivity.Adapter adapter = new CollegeActivity.Adapter(getSupportFragmentManager());
        adapter.addFragment(new CollegeSocietiesFragment(),"SOCIETIES");
        adapter.addFragment(new CollegeEventsFragment(),"EVENTS");
        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
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


    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }


        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            return inflater.inflate(R.layout.fragment_college, container, false);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
