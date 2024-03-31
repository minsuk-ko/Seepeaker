package com.example.seepeaker.controller;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;

import com.example.seepeaker.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;



////////////////
/////// 지워두 상관 없음...
////////////////

public class UserController2 {

    FirebaseUser userAccount;

    private FirebaseFirestore firebaseDB = FirebaseFirestore.getInstance();
    private CollectionReference CollectionReference = firebaseDB.collection("Users");

    final AtomicReference<List<User>> myData = new AtomicReference<>(new ArrayList<>());
    private User user;

    public UserController2(User user){
        this.user = user;
    }
    public UserController2(){ }
    public void updateUserInfo(String name, String email, String password, int minScore, int maxScore, int avgScore, int testTime) {
        user.setUserName(name);
        user.setUserEmail(email);
        user.setUserPassword(password);
        user.setMinScore(minScore);
        user.setMaxScore(maxScore);
        user.setAvgScore(avgScore);
        user.setTestTime(testTime);

        // Firebase 또는 다른 저장소에 업데이트하는 로직 필요
    }

    public void updateUserInfo(String userID, String fieldName, String fieldValue) { // 수정할 필드값이 String형식인 경우
        // userID는 수정할 유저 ID값(= 특정 유저의 정보를 담은 테이블명), fieldName은 수정할 필드의 이름, fieldValue = 수정할 필드의 값
        // name을 제외한 값은 FirebaseAuth를 통해 따로 수정이 필요하기에 if문으로 나눔
        if(fieldName == "userName") {
            insertStringValue(userID, fieldName, fieldValue);
        }

        else if(fieldName == "userEmail") {
            insertStringValue(userID, fieldName, fieldValue);
            //FirebaseAuth를 통한 수정 부분 필요
        }

        else if(fieldName == "userPassword") {
            insertStringValue(userID, fieldName, fieldValue);
            //FirebaseAuth를 통한 수정 부분 필요
        }

        else { // 어느것 하나 fieldName이 일치 하는게 없는 경우
            Log.d(TAG, "잘못된 필드 이름입니다!");
        }

    }

    public void updateUserInfo(String userID, String fieldName, int fieldValue) { // 수정할 필드값이 int형식인 경우
        // userID는 수정할 유저 ID값(= 특정 유저의 정보를 담은 테이블명), fieldName은 수정할 필드의 이름, fieldValue = 수정할 필드의 값

        if(fieldName == "minScore" || fieldName == "maxScore" || fieldName == "avgScore" || fieldName == "testTime") {
            insertIntValue(userID, fieldName, fieldValue); // fieldName이 위의 4가지 중 하나인 경우 에만 실행
        }

        else { // 어느것 하나 fieldName이 일치 하는게 없는 경우
            Log.d(TAG, "잘못된 필드 이름입니다!");
        }

    }

    public void selectUserInfo(String userID, String fieldName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference myCollection = db.collection("Users");
        //DocumentReference documentReference = myCollection.document(userID);
        myCollection.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        List<User> newData = querySnapshot.toObjects(User.class);
                        myData.set(newData);
                        Log.d(TAG, "Document :: " + myData.get());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure( Exception e) {
                        Log.e("Firestore", "Error getting documents: ", e);
                    }
                });

        /*
                .addOnCompleteListener(task-> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                if (document.contains(fieldName)) {
                                    Log.d(TAG, "DocumentSnapshot fieldName data: " + fieldName);
                                    String returnValue = document.getString(fieldName);
                                    myData = returnValue;
                                    Log.d(TAG, "DocumentSnapshot returnValue data: " + returnValue);
                                } else {
                                    Log.d(TAG, fieldName + " field does not exist in the document");
                                }
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());

                        }
                });*/
    }

    public List<User> processData() {
        if (myData.get() != null) {
            Log.e("Firestore", "Yes User Document:"+ myData.get() );
        } else {
            Log.e("Firestore", "NO User Document:");
        }
        return myData.get();
    }

    public void insertStringValue(String userID, String fieldName, String fieldValue) {
        CollectionReference.document(userID).update(fieldName,fieldValue)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "필드의 값이 성공적으로 업데이트 되었습니다!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.w(TAG, "업데이트에 실패하였습니다!", e);
                    }
                });
    }

    public void insertIntValue(String userID, String fieldName, int fieldValue) {
        CollectionReference.document(userID).update(fieldName,fieldValue)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "필드의 값이 성공적으로 업데이트 되었습니다!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.w(TAG, "업데이트에 실패하였습니다!", e);
                    }
                });
    }

    public boolean isUserLogin() { //로그인 상태 확인 부분
        userAccount = FirebaseAuth.getInstance().getCurrentUser();
        if (userAccount != null) {
            return true;
        }
        else { return false; }
    }

    public String getUserID() {
        String returnVal;
        if (userAccount != null) {
            return returnVal = userAccount.getUid();
        }
        else {
            return returnVal = "";
        }
    }


}

