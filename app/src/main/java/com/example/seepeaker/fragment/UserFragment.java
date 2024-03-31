package com.example.seepeaker.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.seepeaker.CustomListViewAdapter;
import com.example.seepeaker.R;
import com.example.seepeaker.activity.ReviewNotesActivity;
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

// 하단 바의 유저 부분
public class UserFragment extends Fragment {

    FirebaseUser userAccount = FirebaseAuth.getInstance().getCurrentUser();
    String userId;
    String reviewNotesExplanation = "오답 노트를 보려면 이곳을 눌러주세요";
    private List<String> userList = new ArrayList<>();

    public UserFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user, container, false);

        // 리스트 뷰에 넣을 데이터 가져오기
        String[] rowTitle = getResources().getStringArray(R.array.user_info_arrays);
        String[] rowData = new String[6];
        ListView listView = rootView.findViewById(R.id.listView);

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
                            String userEmail = documentSnapshot.getString("userEmail");
                            long avgScore = documentSnapshot.getLong("avgScore"); // firestore의 유형 number는 long 형식임.
                            long maxScore = documentSnapshot.getLong("maxScore"); // firestore의 유형 number는 long 형식임.
                            long minScore = documentSnapshot.getLong("minScore"); // firestore의 유형 number는 long 형식임.
                            long testTime = documentSnapshot.getLong("testTime"); // firestore의 유형 number는 long 형식임.
                            String avgScoreToString = Long.toString(avgScore); // 저장된 데이터가 숫자라서 문자로 바꿔야함.
                            String maxScoreToString = Long.toString(maxScore);
                            String minScoreToString = Long.toString(minScore);
                            String testTimeToString = Long.toString(testTime);
                            userList.add(userName);
                            userList.add(userEmail);
                            userList.add(avgScoreToString);
                            userList.add(maxScoreToString);
                            userList.add(minScoreToString);
                            userList.add(testTimeToString);

                        }
                        String userName = userList.get(0);
                        String userEmail = userList.get(1);
                        String avgScore = userList.get(2);
                        String maxScore = userList.get(3);
                        String minScore = userList.get(4);
                        String testTime = userList.get(5);
                        TextView cardViewTitle;
                        cardViewTitle = rootView.findViewById(R.id.cardViewTitle);
                        cardViewTitle.setText(userName);
                        rowData[0] = userEmail;
                        rowData[1] = reviewNotesExplanation;
                        rowData[2] = avgScore;
                        rowData[3] = maxScore;
                        rowData[4] = minScore;
                        rowData[5] = testTime;
                        CustomListViewAdapter adapter = new CustomListViewAdapter(requireContext(), rowTitle, rowData);
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

        // 리스트 뷰에서 사용할 어뎁터 가져오기



        // 리스트 뷰의 아이템(각각의 행)을 누를시 작동
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 오답 노트 보기가 클릭 됬을시 오답노트(Review Notes) Activity 로 전환
                if (rowTitle[position].equals("오답 노트 보기")) {
                    Toast.makeText(requireContext(), "오답 노트 보기", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(requireContext(), ReviewNotesActivity.class);
                    startActivity(intent);
                }
            }
        });

        // 개별 아이템(각각의 행)의 높이를 기준으로 전체 높이를 계산 여백? 방지


        return rootView;
    }
}

