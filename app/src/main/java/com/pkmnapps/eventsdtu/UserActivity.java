package com.pkmnapps.eventsdtu;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

public class UserActivity extends AppCompatActivity {

    List<EventData> eventDataList = new ArrayList<>();
    RecyclerView recyclerView;
    EventAdapter eventAdapter;

    String CHANNEL_ID = "channelId";
    String contentTitle,contentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        FancyButton fb = (FancyButton)findViewById(R.id.user_rate_us);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File[] dir = getCacheDir().listFiles();
                Toast.makeText(UserActivity.this,String.valueOf(dir.length), Toast.LENGTH_SHORT).show();

                contentTitle = "EVENTHUB ROXX";
                contentText = "Some text to display";

                PendingIntent contentIntent = PendingIntent.getActivity(UserActivity.this, 0,
                        new Intent(UserActivity.this, SplashActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);



                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(UserActivity.this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_star_border_black_24dp)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.f1))
                        .setContentTitle(contentTitle)
                        .setContentText(contentText)
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(contentText))
                        .setContentIntent(contentIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // Create the NotificationChannel, but only on API 26+ because
                    // the NotificationChannel class is new and not in the support library
                    CharSequence name = CHANNEL_ID;
                    String description = "description";
                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
                    channel.setDescription(description);
                    // Register the channel with the system
                    notificationManager.createNotificationChannel(channel);
                }

                notificationManager.notify(1, mBuilder.build());
            }
        });

        FancyButton shareApp = (FancyButton)findViewById(R.id.user_share);
        shareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //share app link here
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT,"https://wc4z6.app.goo.gl/eventHub");
                startActivity(Intent.createChooser(shareIntent,"Share via"));
            }
        });

        FancyButton aboutUs = (FancyButton)findViewById(R.id.user_about_us);
        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserActivity.this,IntroActivity.class));
            }
        });

}



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // back icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

