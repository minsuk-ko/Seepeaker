package com.example.seepeaker.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

// 오답노트 부분 (MainActivity의 UserFragment 부분의 "오답 노트"를 누르면 나오는 Activity)
public class ReviewNotesActivity extends AppCompatActivity {

    private List<String> wordList = new ArrayList<>();

    FirebaseUser userAccount = FirebaseAuth.getInstance().getCurrentUser();

    String userId; // 유저Id
    TextView userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_notes);
        ListView listView = findViewById(R.id.listView);
        userName = findViewById(R.id.userName);


        userId = userAccount.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference questionsCollection = db.collection("Questions");
        questionsCollection.whereEqualTo("userId", userId).whereEqualTo("isCorrect", false).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        String word = documentSnapshot.getString("word");
                        wordList.add(word);
                    }
                }
                HashSet<String> set = new HashSet<String>(wordList);
                String[] rowTitle = new String[set.size()];
                String[] rowData = new String[set.size()];
                int counter = 0;
                for (String title : set) {
                    rowTitle[counter] = title;
                    String textNumber = Integer.toString(Collections.frequency(wordList, title));
                    rowData[counter] = textNumber + "번";
                    counter++;
                }
                CustomListViewAdapter adapter = new CustomListViewAdapter(ReviewNotesActivity.this, rowTitle, rowData);
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