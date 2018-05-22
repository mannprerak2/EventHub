package com.pkmnapps.eventsdtu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

public class FilterActivity extends AppCompatActivity {
    Date afterDate=null,beforeDate=null;
    Button apply;

    CheckBox today, week, month;
    RadioGroup radioGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        switch (getIntent().getStringExtra("type")) {//get from which allviewactivity screen type this was opened ( event, society, college, or subcribed society)
            case "e"://event
                setupEventViews();
                break;
            case "s"://society
                setupSocietyViews();
                break;
            case "sl"://subscribed society
                setupSubsSocietyViews();
                break;
            default:

                break;
        }
        //initialize views common to all below

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Toast.makeText(FilterActivity.this,"Filters not applied",Toast.LENGTH_SHORT).show();
                finish();
                return true;
            case R.id.reset:
                ResetFilters();
                return true;
            default:
            return super.onOptionsItemSelected(item);
        }
    }


    private void setupEventViews(){
        setContentView(R.layout.activity_filter_event);

        radioGroup = (RadioGroup)findViewById(R.id.radiogroup);

        today = (CheckBox)findViewById(R.id.checkBox_today);
        week = (CheckBox)findViewById(R.id.checkBox_this_week);
        month = (CheckBox)findViewById(R.id.checkBox_this_month);

        apply = (Button) findViewById(R.id.applyFilters);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();//put data to be sent back in here
                Bundle bundle = new Bundle();
                bundle.putBoolean("today",today.isChecked());
                bundle.putBoolean("week",week.isChecked());
                bundle.putBoolean("month",month.isChecked());
                if(radioGroup.getCheckedRadioButtonId()!=R.id.allRadio) {
                    bundle.putBoolean("category",true);
                    if (radioGroup.getCheckedRadioButtonId() == R.id.technicalRadio)
                        bundle.putBoolean("tech", true);
                    else if (radioGroup.getCheckedRadioButtonId() == R.id.culturalRadio)
                        bundle.putBoolean("tech", false);
                }
                intent.putExtra("filters",bundle);
                setResult(RESULT_OK,intent);
                Toast.makeText(FilterActivity.this,"Filters applied",Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        setAppliedFilters();
    }
    private void setupSocietyViews(){
        setContentView(R.layout.activity_filter_society);

    }
    private void setupSubsSocietyViews() {
        setContentView(R.layout.activity_filter_society);
    }

    private void ResetFilters(){
        //reset categories

        //reset time
        week.setChecked(false);
        today.setChecked(false);
        month.setChecked(false);
        radioGroup.check(R.id.allRadio);

        apply.callOnClick();
    }

    private void setAppliedFilters(){
        List<String> filterList = getIntent().getStringArrayListExtra("filterList");

        if(filterList!=null){
            if(filterList.contains(" This Month ")){
                month.setChecked(true);
            }
            else if(filterList.contains(" This Week ")){
                week.setChecked(true);
            }
            else if(filterList.contains(" Today ")){
                today.setChecked(true);
            }

            if(filterList.contains(" Technical ")){
                radioGroup.check(R.id.technicalRadio);
            }
            else if(filterList.contains(" Cultural ")){
                radioGroup.check(R.id.culturalRadio);
            }
        }
    }
}
