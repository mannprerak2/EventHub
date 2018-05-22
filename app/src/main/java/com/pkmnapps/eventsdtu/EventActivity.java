package com.pkmnapps.eventsdtu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EventActivity extends AppCompatActivity {
    public String name,desc,location,imageLink;
    public String uniqueID,college,organiser;
    public Date date;
    public View v;
    public ImageView imageViewEvent;
    Context context = EventActivity.this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                //set visibilty of admob view first
                mAdView.setVisibility(View.VISIBLE);
                super.onAdLoaded();
            }
        });

        Bundle i = getIntent().getBundleExtra("eventData");

        final ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar2);
        final TextView textViewDesc = (TextView)findViewById(R.id.textView_this_event_desc);
        final TextView textViewLocaion = (TextView)findViewById(R.id.textView_this_event_location);
        final TextView textViewDate = (TextView)findViewById(R.id.textView_this_event_date);
        final TextView textViewBy = (TextView)findViewById(R.id.textView_this_event_by);
        final TextView textViewName = (TextView)findViewById(R.id.textView_this_event_name);
        imageViewEvent = (ImageView)findViewById(R.id.imageView_this_event);

        v = (View)findViewById(R.id.relative);

        uniqueID = i.getString("uniqueId");
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("/EventsPAS/EVENTS/EVENTSc").document(uniqueID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(),"success",Toast.LENGTH_SHORT).show();
                    //stop progress bar
                    progressBar.setVisibility(View.GONE);
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()) {
                        name = document.getString("name");
                        college = document.getString("college");
                        desc = document.getString("desc");
                        organiser = document.getString("organiser");
                        date = document.getDate("date");
                        imageLink = document.getString("image");
                        //set name
                        if(name!=null) {
                            textViewName.setText(name);
                        }
                        //set by
                        if(organiser!=null) {
                            textViewBy.setText(organiser);
                        }
                        //set date
                        if(date!=null) {
                            DateFormat sdf = new SimpleDateFormat("EEE, MMM d, hh:mm aaa", Locale.ENGLISH);
                            textViewDate.setText(sdf.format(date));
                        }
                        //set college
                        if(college !=null) {
                            textViewLocaion.setText(college);
                        }
                        //set description
                        if(desc!=null) {
                            textViewDesc.setText(desc);
                        }
                        if(imageLink!=null) {
                            //image loading...
                            final DBHelperImages dbHelperImages = new DBHelperImages(EventActivity.this);
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReference().child("Events");
                            final StorageReference imageRef = storageRef.child(imageLink);
                            if (!dbHelperImages.isInImageDatabase(imageLink)) { //download data if not in database
                                final File localFile = new File(getCacheDir(), imageLink);//we store download link because if we change file on server it changes the link too
                                imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        //file downloaded now , open this in your imageview
                                        Picasso.with(EventActivity.this).load(localFile).into(imageViewEvent);
                                        //set image click listener
                                        imageViewEvent.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent intent = new Intent(EventActivity.this, ImageViewFullscreen.class);
                                                intent.putExtra("image", imageLink);
                                                startActivity(intent);
                                            }
                                        });
                                        //save this to database now
                                        dbHelperImages.insertImage(imageLink, imageLink, date);
                                    }
                                });
                            } else { // image is in database load it here
                                final File localFile = new File(getCacheDir(), imageLink);//we store download link because if we change file on server it changes the link too
                                if (!localFile.exists()) {//files doesnt exist on cache but exists on sql database
                                    //remove the entry in database
                                    dbHelperImages.deleteImage(imageLink);
                                    //rerun downloader
                                    Toast.makeText(getApplicationContext(), "rerun", Toast.LENGTH_SHORT).show();
                                    imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                            //file downloaded now , open this in your imageview
                                            Picasso.with(EventActivity.this).load(localFile).into(imageViewEvent);
                                            //set image click listener
                                            imageViewEvent.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Intent intent = new Intent(EventActivity.this, ImageViewFullscreen.class);
                                                    intent.putExtra("image", imageLink);
                                                    startActivity(intent);
                                                }
                                            });
                                            //save this to database now
                                            dbHelperImages.insertImage(imageLink, imageLink, date);
                                        }
                                    });
                                } else {
                                    Picasso.with(EventActivity.this).load(localFile).into(imageViewEvent);
                                    //set image click listener
                                    imageViewEvent.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(EventActivity.this, ImageViewFullscreen.class);
                                            intent.putExtra("image", imageLink);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            }

                        }
                    }
                    else {
                        //hangle here if document does not exist
                        Toast.makeText(getApplicationContext(),"Event expired or Link is broken",Toast.LENGTH_LONG).show();
                        //open mainActivity here
                        startActivity(new Intent(EventActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                        finish();//kill this activity
                    }
                }
                else {
                    final Snackbar make =  Snackbar.make(v,"Network Error",Snackbar.LENGTH_INDEFINITE);
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


        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            case R.id.action_pin:
                final DBHelperPin dbHelperPin = new DBHelperPin(context);
                if(dbHelperPin.isInPinnedDataBase(uniqueID))//if it is already in database then...
                    Snackbar.make(v,"Event Already Pinned",Snackbar.LENGTH_LONG).setAction("Un-Pin", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dbHelperPin.deletePinnedEvent(uniqueID);
                            Snackbar.make(v, "Event Un-Pinned", Snackbar.LENGTH_SHORT).show();
                        }
                    }).show();
                else {
                    dbHelperPin.insertPinnedEvent(uniqueID);
                    Snackbar.make(v, "Event Pinned", Snackbar.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_share:
                //share
                    //show popup menu for sharing
                    PopupMenu popupMenu = new PopupMenu(EventActivity.this,findViewById(R.id.action_share));
                    MenuInflater inflater = popupMenu.getMenuInflater();
                    inflater.inflate(R.menu.menu_share_popup, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.share_image:
                                    shareImage();
                                    return true;
                                case R.id.share_desc:
                                    shareEventDesc();
                                    return true;
                                case R.id.share_link:
                                    shareEventLink();
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    popupMenu.show();

                return true;
            case R.id.action_reminder:
                setReminder();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void shareImage(){
        try {
            File newFile = new File(context.getCacheDir().getAbsolutePath(),imageLink);
            //File newFile = new File(imagePath);
            Uri contentUri = FileProvider.getUriForFile(context, "com.pkmnapps.eventsdtu.fileprovider", newFile);
            //share file
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM,contentUri);
            shareIntent.setType("image/png");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent,"Share via"));
        }
        catch (Exception e) { Toast.makeText(context, "An error occurred", Toast.LENGTH_LONG).show();
        }
    }

    public void shareEventDesc(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,desc);
        startActivity(Intent.createChooser(shareIntent,"Share via"));
    }

    public void shareEventLink(){
        final ProgressDialog dialog = ProgressDialog.show(EventActivity.this, "",
                "Generating Link...", true);

        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(new Uri.Builder().scheme("https").authority("eventhubweb.com").appendPath("eventHub").appendQueryParameter("event",uniqueID).build())
                .setDynamicLinkDomain("wc4z6.app.goo.gl")
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                // Open links with com.example.ios on iOS
                .setIosParameters(new DynamicLink.IosParameters.Builder("com.example.ios").build())
                .buildDynamicLink();

        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(dynamicLink.getUri())
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();

                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_TEXT,shortLink.toString());
                            startActivity(Intent.createChooser(shareIntent,"Share via"));
                        } else {
                            Toast.makeText(context, "Network Error", Toast.LENGTH_LONG).show();

                        }
                        dialog.dismiss();
                    }
                });

    }

    public void setReminder(){
        try {
            Calendar beginTime = Calendar.getInstance();
            beginTime.setTime(date);
            //beginTime.set(2012, 0, 19, 7, 30);
            Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                    .putExtra(CalendarContract.Events.TITLE, name)
                    .putExtra(CalendarContract.Events.DESCRIPTION, desc)
                    .putExtra(CalendarContract.Events.EVENT_LOCATION, college)
                    .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
            startActivity(intent);
        }catch (Exception e){
            Toast.makeText(context, "An error Occurred", Toast.LENGTH_LONG).show();
        }
}

}
