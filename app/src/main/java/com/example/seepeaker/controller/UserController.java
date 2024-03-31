package com.example.seepeaker.controller;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/////////////
///// 유저 값을 읽어 오거나 쓰는 객체
////////////

public class UserController {

    FirebaseUser userAccount;

    private FirebaseFirestore firebaseDB = FirebaseFirestore.getInstance();
    private CollectionReference CollectionReference = firebaseDB.collection("Users");

    public UserController(){ }

    public void updateUserInfo(String userID, String fieldName, String fieldValue) {
        if(fieldName == "userName") {
            insertStringValue(userID, fieldName, fieldValue);
        }

        else if(fieldName == "userEmail") {
            insertStringValue(userID, fieldName, fieldValue);
        }

        else if(fieldName == "userPassword") {
            insertStringValue(userID, fieldName, fieldValue);
        }

        else {
            Log.d(TAG, "잘못된 필드 이름입니다!");
        }

    }

    public void updateUserInfo(String userID, String fieldName, int fieldValue) {
        if(fieldName == "minScore" || fieldName == "maxScore" || fieldName == "avgScore" || fieldName == "testTime") {
            insertIntValue(userID, fieldName, fieldValue);
        }

        else {
            Log.d(TAG, "잘못된 필드 이름입니다!");
        }

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

    public Task<String> selectUserInfo(String userID, String fieldName) {  //요거는 작동안함...
        final TaskCompletionSource<String> tcs = new TaskCompletionSource<>();

        CollectionReference.document(userID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                if (document.contains(fieldName)) {
                                    String returnVal = document.getString(fieldName);
                                    Log.d(TAG, "DocumentSnapshot Field data: " + returnVal);
                                    tcs.setResult(returnVal);
                                } else {
                                    Log.d(TAG, fieldName + " field does not exist in the document");
                                    tcs.setResult(null); // Pass null to indicate the field is not present
                                }
                            } else {
                                Log.d(TAG, "No such document");
                                tcs.setResult(null); // Pass null to indicate document does not exist
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                            tcs.setResult(null); // Pass null to indicate failure
                        }
                    }
                });

        return tcs.getTask();
    }

    public boolean isUserLogin() { //로그인 상태 확인 부분
        userAccount = FirebaseAuth.getInstance().getCurrentUser();
        if (userAccount != null) {
            return true;
        }
        else { return false; }
    }


}

