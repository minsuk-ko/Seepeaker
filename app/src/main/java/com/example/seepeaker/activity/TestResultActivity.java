package com.example.seepeaker.activity;

import static java.lang.Thread.sleep;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArraySet;

import com.example.seepeaker.CustomListViewAdapter;
import com.example.seepeaker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

// TestActivity의 결과를 보여 주는 부분
public class TestResultActivity extends AppCompatActivity {

    private List<String> wordList = new ArrayList<>();
    private List<Boolean> isCorrectList = new ArrayList<>();
    FirebaseUser userAccount = FirebaseAuth.getInstance().getCurrentUser();
    String userId; // 유저Id
    long testId; // 테스트 Id

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_notes);
        ListView listView = findViewById(R.id.listView);

        testId = getIntent().getLongExtra("testId",1); // Test Activity에서 엑티비티 시작할때 가져온 시험 번호
        userId = userAccount.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference questionsCollection = db.collection("Questions");
        questionsCollection.whereEqualTo("userId", userId).whereEqualTo("testId", testId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        String word = documentSnapshot.getString("word");
                        boolean isCorrect = documentSnapshot.getBoolean("isCorrect");
                        wordList.add(word);
                        isCorrectList.add(isCorrect);
                    }
                }
                String[] rowTitle = new String[wordList.size()];
                String[] rowData = new String[wordList.size()];
                for (int i = 0; i < wordList.size(); i++) {
                    rowTitle[i] = wordList.get(i);
                    if (isCorrectList.get(i)){
                        rowData[i] = "정답";
                    }
                    else {
                        rowData[i] = "오답";
                    }
                }
                CustomListViewAdapter adapter = new CustomListViewAdapter(TestResultActivity.this, rowTitle, rowData);
                listView.setAdapter(adapter);
                int totalHeight = 0;
                for (int i = 0; i < adapter.getCount(); i++) {
                    View listItem = adapter.getView(i, null, listView);
                    listItem.measure(
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                    );
                    totalHeight += listItem.getMeasuredHeight();
                }

                // 계산된 높이를 ListView의 레이아웃 높이로 설정
                listView.getLayoutParams().height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
                listView.requestLayout();

            }
        });

    }


}