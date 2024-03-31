package com.example.seepeaker.activity;

import android.content.Intent;
import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.seepeaker.R;
import com.example.seepeaker.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseDB = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        EditText emailEditText = findViewById(R.id.edit_text_email);
        EditText idEditText = findViewById(R.id.edit_text_id);
        EditText passwordEditText = findViewById(R.id.edit_text_login_password);
        Button joinButton = findViewById(R.id.button_join);

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String accountEmail = emailEditText.getText().toString();
                String accountPassword = passwordEditText.getText().toString();
                String accountName = idEditText.getText().toString();

                firebaseAuth.createUserWithEmailAndPassword(accountEmail, accountPassword)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                    User newUser = new User();
                                    newUser.setUserId(firebaseUser.getUid());
                                    newUser.setUserEmail(firebaseUser.getEmail());
                                    newUser.setUserName(accountName);
                                    newUser.setUserPassword(accountPassword);
                                    firebaseDB.collection("Users").document(firebaseUser.getUid()).set(newUser);
                                    Toast.makeText(getApplicationContext(), "가입에 성공하였습니다. 로그인을 진행해주세요.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Activity 꼬임? 방지 - 자세한 내용은 직접 찾아 볼것.
                                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); // Activity 가 스택에 남아 있으면 onCreate 를하지 않음.
                                    startActivity(intent);
                                }
                                else { Toast.makeText(getApplicationContext(), "회원가입을 실패하였습니다.", Toast.LENGTH_SHORT).show(); }
                            }
                        });

            }

        });

    }
}
