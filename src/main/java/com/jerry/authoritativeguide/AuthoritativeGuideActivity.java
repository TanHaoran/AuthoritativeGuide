package com.jerry.authoritativeguide;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AuthoritativeGuideActivity extends AppCompatActivity {

    private ListView mListView;

    private String[] mTitles = {"GeoQuiz"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authoritative_guide);

        mListView = (ListView) findViewById(R.id.lv_chapter);

        mListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mTitles));

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }
}
