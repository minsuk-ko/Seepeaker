package com.example.seepeaker.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.seepeaker.R;
import com.example.seepeaker.activity.TestActivity;
import com.example.seepeaker.activity.TestResultActivity;
import com.example.seepeaker.controller.UserController;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

// 하단 바의 시험 부분
public class TestFragment extends Fragment {

    FirebaseUser userAccount = FirebaseAuth.getInstance().getCurrentUser();
    String userId;
    private List<String> userList = new ArrayList<>();
    public TestFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        String cardViewButtonText = getResources().getString(R.string.cardview_button_change_activity_text);
        ViewPager2 viewPager = rootView.findViewById(R.id.viewpager2_home);
        TestFragment.Adapter cardViewAdapter = new TestFragment.Adapter(cardViewButtonText);
        viewPager.setAdapter(cardViewAdapter);
        viewPager.setPadding(30, 0, 30, 0);
        return rootView;
    }

    // RecyclerView Adapter
    class Adapter extends RecyclerView.Adapter<TestFragment.Adapter.AdapterViewHolder> {
        private String cardViewButtonText;

        public Adapter(String cardViewButtonText) {
            this.cardViewButtonText = cardViewButtonText;
        }

        @NonNull
        @Override
        public TestFragment.Adapter.AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_fragment_home_card, parent, false);
            Button buttonChangeActivity = view.findViewById(R.id.buttonChangeActivity);
            buttonChangeActivity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), TestActivity.class);
                    startActivity(intent);
                }
            });
            return new TestFragment.Adapter.AdapterViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TestFragment.Adapter.AdapterViewHolder holder, int position) {
            if (userAccount != null) {
                userId = userAccount.getUid();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference wordsCollection = db.collection("Users");
                wordsCollection.whereEqualTo("userId", userId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                String userName = documentSnapshot.getString("userName");
                                long avgScore = documentSnapshot.getLong("avgScore"); // firestore의 유형 number는 long 형식임.
                                long maxScore = documentSnapshot.getLong("maxScore"); // firestore의 유형 number는 long 형식임.
                                long minScore = documentSnapshot.getLong("minScore"); // firestore의 유형 number는 long 형식임.
                                String avgScoreToString = Long.toString(avgScore); // 저장된 데이터가 숫자라서 문자로 바꿔야함.
                                String maxScoreToString = Long.toString(maxScore);
                                String minScoreToString = Long.toString(minScore);
                                userList.add(userName);
                                userList.add(avgScoreToString);
                                userList.add(maxScoreToString);
                                userList.add(minScoreToString);
                            }
                            String userName = userList.get(0);
                            String avgScore = userList.get(1);
                            String maxScore = userList.get(2);
                            String minScore = userList.get(3);
                            holder.cardViewTitle.setText(userName);
                            holder.avgScore.setText(avgScore);
                            holder.maxScore.setText(maxScore);
                            holder.minScore.setText(minScore);
                        } else {
                            // 데이터가 없는 경우에 대한 처리
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 데이터를 가져오는 데 실패한 경우에 대한 처리
                    }
                });
            }
            holder.changeActivityButtonText.setText(cardViewButtonText);
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        public class AdapterViewHolder extends RecyclerView.ViewHolder {
            TextView cardViewTitle;
            TextView changeActivityButtonText;
            TextView avgScore;
            TextView maxScore;
            TextView minScore;

            public AdapterViewHolder(View view) {
                super(view);
                cardViewTitle = view.findViewById(R.id.cardViewTitle);
                avgScore = view.findViewById(R.id.avgScore);
                maxScore = view.findViewById(R.id.maxScore);
                minScore = view.findViewById(R.id.minScore);
                changeActivityButtonText = view.findViewById(R.id.buttonChangeActivity);
            }
        }
    }
}