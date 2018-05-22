package com.pkmnapps.eventsdtu;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {
    LinearLayout linearLayout;
    int total=3,current=1;
    Button skip,next,back;
    List<View> viewList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        linearLayout = (LinearLayout)findViewById(R.id.introLayout);
        skip = (Button)findViewById(R.id.skipButton);
        next = (Button)findViewById(R.id.nextButton);
        back = (Button)findViewById(R.id.backButton);

        //add views...
        viewList = new ArrayList<>();
        viewList.add(getLayoutInflater().inflate(R.layout.intro_view_1,null));
        viewList.add(getLayoutInflater().inflate(R.layout.intro_view_2,null));
        viewList.add(getLayoutInflater().inflate(R.layout.intro_view_3,null));


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextPressed();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BackPressed();
            }
        });
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SkipPressed();
            }
        });

        addViewToIntro(viewList.get(0));//initially setting view

    }


    void addViewToIntro(View v){
        linearLayout.removeAllViews();
        linearLayout.addView(v);
    }

    void SkipPressed(){
        startActivity(new Intent(IntroActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

    }

    void nextPressed(){
        if(current==total) {
            SkipPressed();
        }
        else{
            current++;
            addViewToIntro(viewList.get(current-1));
            back.setVisibility(View.VISIBLE);
            if(current==total){
                next.setText(R.string.done);
                skip.setVisibility(View.INVISIBLE);
            }

        }
    }


    void BackPressed() {
        if(current==1){
            //do nothing
        }
        else {
            current--;
            addViewToIntro(viewList.get(current - 1));
            if(current==1){
                back.setVisibility(View.INVISIBLE);
            }
            else if(current==2){
                next.setText(R.string.next);
                skip.setVisibility(View.VISIBLE);
            }

        }
    }
}
