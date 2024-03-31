package com.example.seepeaker.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.seepeaker.R;

// 툴바의 알림 부분    * 현재는 사용하지 않음 *
public class AlarmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        Toolbar toolbarAlarm = findViewById(R.id.toolbar_alarm);
        setSupportActionBar(toolbarAlarm); // Activity 의 앱 바 설정
        ActionBar actionbarAlarm = getSupportActionBar();
        actionbarAlarm.setDisplayHomeAsUpEnabled(true); // Main Activity 로 되돌아 가기에 사용할 화살표를 표시함.
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(AlarmActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Activity 꼬임 방지 - 자세한 내용은 검색 추천.
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); // Activity 가 스택에 남아 있으면 onCreate 를하지 않음.
        startActivity(intent);
        finish();
        return true;
    }

}