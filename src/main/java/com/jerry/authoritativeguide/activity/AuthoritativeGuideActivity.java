package com.jerry.authoritativeguide.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jerry.authoritativeguide.R;

public class AuthoritativeGuideActivity extends BaseActivity {

    private ListView mListView;

    private String[] mTitles = {"GeoQuiz", "CrimeIntent", "BeatBox", "NerdLauncher"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authoritative_guide);

        mListView = (ListView) findViewById(R.id.lv_chapter);

        mListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mTitles));

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                switch (position) {
                    case 0:
                        intent.setClass(AuthoritativeGuideActivity.this, QuizActivity.class);
                        break;
                    case 1:
                        intent.setClass(AuthoritativeGuideActivity.this, CrimeListActivity.class);
                        break;
                    case 2:
                        intent.setClass(AuthoritativeGuideActivity.this, BeatBoxActivity.class);
                        break;
                    case 3:
                        intent.setClass(AuthoritativeGuideActivity.this, NerdLauncherActivity.class);
                        break;
                    default:
                        break;
                }
                startActivity(intent);
            }
        });
    }
}
