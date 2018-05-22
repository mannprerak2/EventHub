package com.pkmnapps.eventsdtu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

import mehdi.sakout.fancybuttons.FancyButton;

public class OrganiserActivity extends AppCompatActivity {
    String uniqueID,name,imagelink,desc;
    View v;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organiser);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name = getIntent().getStringExtra("name");
        toolbar.setTitle(name);
        desc = name;
        imagelink = getIntent().getStringExtra("image");
        uniqueID = getIntent().getStringExtra("uniqueId");
        v = (View)findViewById(R.id.linearLayout);

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        final ImageView imageViewSociety = (ImageView) findViewById(R.id.society_image_imageButton);
        final TextView textViewName = (TextView)findViewById(R.id.society_name_TextView);
        final TextView textViewDesc = (TextView)findViewById(R.id.society_desc_textView);
        final FancyButton viewEventsButton = (FancyButton)findViewById(R.id.society_viewEvents);
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("/EventsPAS/ORGANISERS/ORGANISERSc").document(uniqueID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()) {
                        //stop progress bar
                        progressBar.setVisibility(View.INVISIBLE);

                        final String imageLink = document.getString("image");
                        String facebook = document.getString("facebook");
                        String twitter = document.getString("twitter");
                        final String insta = document.getString("insta");
                        String github = document.getString("github");

                        final String name = document.getString("name");
                        desc = document.getString("desc");

                        //set name
                        if(name!=null) {
                            textViewName.setText(name);
                            getSupportActionBar().setTitle(name);
                        }
                        if(desc!=null)
                            textViewDesc.setText(desc);

                        //make view all events visible
                        viewEventsButton.setVisibility(View.VISIBLE);

                        viewEventsButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //open allview activity with society name filter
                                Intent intent = new Intent(OrganiserActivity.this,AllViewActivity.class);
                                //put extras in intent
                                intent.putExtra("type","AllEvents");
                                intent.putExtra("societyEvents",name);
                                startActivity(intent);
                            }
                        });
                        //send data to info for social media links
                        infoSetup(facebook,twitter,insta,github);

                        //image loading...
                        if(imageLink!=null) {
                            final DBHelperImages dbHelperImages = new DBHelperImages(OrganiserActivity.this);

                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReference().child("Societies");
                            final StorageReference imageRef = storageRef.child(imageLink);

                            if (!dbHelperImages.isInImageDatabase(imageLink)) { //download data if not in database
                                final File localFile = new File(getCacheDir(), imageLink);//we store download link because if we change file on server it changes the link too

                                imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        //file downloaded now , open this in your imageview
                                        Picasso.with(OrganiserActivity.this).load(localFile).into(imageViewSociety);
                                        imageViewSociety.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent intent = new Intent(OrganiserActivity.this,ImageViewFullscreen.class);
                                                intent.putExtra("image",imageLink);
                                                startActivity(intent);
                                            }
                                        });
                                        //save this to database now
                                        dbHelperImages.insertImage(imageLink, imageLink);
                                    }
                                });
                            } else { // image is in database load it here
                                final File localFile = new File(getCacheDir(), imageLink);//we store download link because if we change file on server it changes the link too
                                if(!localFile.exists()){//file not on cache but on database
                                    //delete database entry
                                    dbHelperImages.deleteImage(imageLink);
                                    //rerun downloader

                                    imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                            //file downloaded now , open this in your imageview
                                            Picasso.with(OrganiserActivity.this).load(localFile).into(imageViewSociety);
                                            imageViewSociety.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Intent intent = new Intent(OrganiserActivity.this,ImageViewFullscreen.class);
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
                                    Picasso.with(OrganiserActivity.this).load(localFile).into(imageViewSociety);
                                    imageViewSociety.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(OrganiserActivity.this, ImageViewFullscreen.class);
                                            intent.putExtra("image", imageLink);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            }
                        }
                    }
                }
                else {
                    final Snackbar make =  Snackbar.make(findViewById(R.id.linearLayout),"Network Error",Snackbar.LENGTH_INDEFINITE);
                    make.setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            make.dismiss();
                            //recreate activity
                            recreate();
                        }
                    }).show();                }
            }
        });
    }


    public void infoSetup(final String facebookLink, final String twitterLink, final String instaLink, final String githubLink){
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.socialLayout);

        //add a button for each social media if they are available

        if(facebookLink!=null) {
            View v = getLayoutInflater().inflate(R.layout.social_button, linearLayout, false);
            ImageButton imageButton;
            imageButton = (ImageButton) v.findViewById(R.id.button_social);
            imageButton.setBackground(getResources().getDrawable(R.drawable.facebook));//setting background
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickSocial(facebookLink);
                }
            });
            linearLayout.addView(v);
        }
        if(twitterLink!=null) {
            View v = getLayoutInflater().inflate(R.layout.social_button, linearLayout, false);
            ImageButton imageButton;
            imageButton = (ImageButton) v.findViewById(R.id.button_social);
            imageButton.setBackground(getResources().getDrawable(R.drawable.twitter));//setting background
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickSocial(twitterLink);
                }
            });
            linearLayout.addView(v);

        }
        if(instaLink!=null) {
            View v = getLayoutInflater().inflate(R.layout.social_button, linearLayout, false);
            ImageButton imageButton;
            imageButton = (ImageButton) v.findViewById(R.id.button_social);
            imageButton.setBackground(getResources().getDrawable(R.drawable.insta));//setting background
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickSocial(instaLink);
                }
            });
            linearLayout.addView(v);

        }
        if(githubLink!=null) {
            View v = getLayoutInflater().inflate(R.layout.social_button, linearLayout, false);
            ImageButton imageButton;
            imageButton = (ImageButton) v.findViewById(R.id.button_social);
            imageButton.setBackground(getResources().getDrawable(R.drawable.github));//setting background
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickSocial(githubLink);
                }
            });
            linearLayout.addView(v);

        }


    }

    private void clickSocial(String link){
        //open link
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(link));
            startActivity(i);
        }catch (Exception e){
            Toast.makeText(this,"Link broken",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_share:
                //share
                //show popup menu for sharing
                PopupMenu popupMenu = new PopupMenu(OrganiserActivity.this,findViewById(R.id.action_share));
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.menu_share_society, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.share_desc:
                                shareSocietyDesc();
                                return true;
                            case R.id.share_image:
                                shareImage();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
                return true;
            case R.id.action_subs:
                final DBHelperSubs dbHelperPin = new DBHelperSubs(OrganiserActivity.this);
                if(dbHelperPin.isInSubsDataBase(uniqueID))//if it is already in database then...
                    Snackbar.make(v,"Already Subscribed",Snackbar.LENGTH_LONG).setAction("Un-Subscribe", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dbHelperPin.deleteSubsSociety(uniqueID);
                            Snackbar.make(v, "Un-Subscribed", Snackbar.LENGTH_SHORT).show();
                        }
                    }).show();
                else {
                    dbHelperPin.insertSubsScoiety(uniqueID,name,imagelink);
                    Snackbar.make(v, "Subscribed", Snackbar.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_organiser_page, menu);
        return true;
    }


    public void shareImage(){
        try {
            File newFile = new File(this.getCacheDir().getAbsolutePath(),imagelink);
            //File newFile = new File(imagePath);
            Uri contentUri = FileProvider.getUriForFile(this, "com.pkmnapps.eventsdtu.fileprovider", newFile);
            //share file
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM,contentUri);
            shareIntent.setType("image/png");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent,"Share via"));
        }
        catch (Exception e) { Toast.makeText(this, "An error occurred", Toast.LENGTH_LONG).show();
        }
    }

    public void shareSocietyDesc() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, desc);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }
}
