package com.pkmnapps.eventsdtu;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;


    int SPLASH_TIME_OUT = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);



        mAuth = FirebaseAuth.getInstance();

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        mAuth.signInAnonymously()
                .addOnCompleteListener(SplashActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            //get dynamic link data if there...
                                    //start mainactivity now
                            if(Objects.equals(getIntent().getAction(), Intent.ACTION_VIEW)){//for launched by firebase link
                                FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
                                        .addOnCompleteListener(new OnCompleteListener<PendingDynamicLinkData>() {
                                            @Override
                                            public void onComplete(@NonNull Task<PendingDynamicLinkData> task) {
                                                if(task.isSuccessful()){
                                                    //open eventactivity with this data
                                                    String uniqueId;
                                                    uniqueId = task.getResult().getLink().getQueryParameter("event");
                                                    if(uniqueId!=null) {//for event
                                                        Toast.makeText(SplashActivity.this, uniqueId, Toast.LENGTH_SHORT).show();
                                                        //launch eventActivity
                                                        //open event activity
                                                        Intent intent = new Intent(SplashActivity.this, EventActivity.class);
                                                        Bundle bundle = new Bundle();
                                                        bundle.putString("uniqueId", uniqueId);
                                                        intent.putExtra("eventData", bundle);
                                                        startActivity(intent);
                                                        //kill this activity
                                                        finish();
                                                    }
                                                    else{//will only run if link wasn't made properly
                                                        Toast.makeText(SplashActivity.this,"Link not constructed properly",Toast.LENGTH_SHORT).show();
                                                        //open mainActivity instead
                                                        startActivity(new Intent(SplashActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                                        finish();
                                                    }
                                                }
                                                else{
                                                    //for task failed
                                                    Toast.makeText(SplashActivity.this,"Error loading link",Toast.LENGTH_SHORT).show();
                                                    //open mainActivity instead
                                                    startActivity(new Intent(SplashActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                                    finish();
                                                }
                                            }
                                        });
                            }
                            else {//for launch by launcher or any other method

                                    new Handler().postDelayed(new Runnable() {
                                        /*
                                         * Showing splash screen with a timer. This will be useful when you
                                         * want to show case your app logo / company
                                         */
                                        @Override
                                        public void run() {
                                            // This method will be executed once the timer is over
                                            // Start your app main activity
                                            startActivity(new Intent(SplashActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                            // close this activity
                                            finish();
                                        }
                                    }, SPLASH_TIME_OUT);
                                    //delete cached files
                                    new DBHelperImages(SplashActivity.this).deleteImagesByTimeColumn();
                                }

                        }
                        else {
                            final Context context = SplashActivity.this;
                            // If sign in fails, display a message to the user.

                            AlertDialog.Builder builder;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                            } else {
                                builder = new AlertDialog.Builder(context);
                            }
                            builder.setTitle("Authentication Error")
                                    .setMessage("A network connection is required for first time to run. Retry?")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(SplashActivity.this,SplashActivity.class));
                                        finish();
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                        // ...
                    }
                });


    }
}
