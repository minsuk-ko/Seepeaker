package com.example.seepeaker.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.seepeaker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

// 발음도우미 부분 (MainActivity의 ToolsFragment 부분의 "발음 방법 도우미 열기"를 누르면 나오는 Activity)
public class PronunciationHelpActivity extends AppCompatActivity {
    //Consonant은 자음, Vowe는 모음임
    Button buttonConsonantImage, buttonVoweImage, buttonReviewNoteReset;
    ImageView imageViewConsonant, imageViewVowe;
    TextView textViewConsonant, textViewVowe;
    FirebaseAuth mAuth= FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pronunciation_help);


        buttonConsonantImage = findViewById(R.id.buttonConsonantImageOpen);
        buttonVoweImage = findViewById(R.id.buttonVoweImageOpen);
        imageViewConsonant = findViewById(R.id.imageConsonant);
        imageViewVowe = findViewById(R.id.imageVowe);
        textViewConsonant = findViewById(R.id.textConsonant);
        textViewVowe = findViewById(R.id.textVowe);
        buttonReviewNoteReset = findViewById(R.id.buttonReviewNoteReset);

        //자음 버튼 클릭시 이미지 및 텍스트 보이기
        buttonConsonantImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((imageViewConsonant.getVisibility() == View.VISIBLE) && (textViewConsonant.getVisibility() == View.VISIBLE)) {
                    imageViewConsonant.setVisibility(View.INVISIBLE);
                } else {
                    imageViewConsonant.setVisibility(View.VISIBLE);
                    textViewConsonant.setVisibility(View.VISIBLE);
                }
            }
        });

        //모음 버튼 클릭시 이미지 및 텍스트 보이기
        buttonVoweImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((imageViewVowe.getVisibility() == View.VISIBLE) && (textViewVowe.getVisibility() == View.VISIBLE)) {
                    imageViewVowe.setVisibility(View.INVISIBLE);
                } else {
                    imageViewVowe.setVisibility(View.VISIBLE);
                    textViewVowe.setVisibility(View.VISIBLE);
                }
            }
        });

        buttonReviewNoteReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    deleteUserDataFromFireStore(userId);

                }
                else{}
            }
        });
    }

    private void deleteUserDataFromFireStore(String userId) {
        CollectionReference userCollection = db.collection("Users");

        userCollection.document(userId).collection("Questions").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                document.getReference().delete();
                            }
                        } else {

                        }
                    }
                });
    }
}


