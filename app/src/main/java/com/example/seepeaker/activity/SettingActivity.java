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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

// 툴바의 설정 부분    * 현재는 사용하지 않음 *
public class SettingActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        Toolbar toolbarSetting = findViewById(R.id.toolbar_setting);
        setSupportActionBar(toolbarSetting); // Activity 의 앱 바 설정
        ActionBar actionbarSetting = getSupportActionBar();
        actionbarSetting.setDisplayHomeAsUpEnabled(true); // Main Activity 로 되돌아 가기에 사용할 화살표를 표시함.

        if (currentUser != null) {
            String userId = currentUser.getUid();
            deleteUserDataFromFireStore(userId);
        } else {
            // 사용자가 로그인되어 있지 않은 경우
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(SettingActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Activity 꼬임? 방지 - 자세한 내용은 직접 찾아 볼 것.
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); // Activity 가 스택에 남아 있으면 onCreate 를 하지 않음.
        startActivity(intent);
        finish();
        return true;
    }

    private void deleteUserDataFromFireStore(String userId){
        CollectionReference userCollection = db.collection("Users");

        userCollection.document(userId).collection("Questions").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                document.getReference().delete();
                            }
                        } else {
                            // 데이터 삭제 실패
                        }
                    }
                });
    }
}
