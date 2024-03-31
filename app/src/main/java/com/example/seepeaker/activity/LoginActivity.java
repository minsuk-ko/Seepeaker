package com.example.seepeaker.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.seepeaker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

// 툴바의 로그인 부분
public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();

        Toolbar toolbarLogin = findViewById(R.id.toolbar_login);
        Button buttonJoin = findViewById(R.id.button_join);
        Button buttonLogin = findViewById(R.id.button_login);
        EditText emailEditText = findViewById(R.id.edit_text_email);
        EditText passwordEditText = findViewById(R.id.edit_text_login_password);

        setSupportActionBar(toolbarLogin); // Activity 의 앱 바 설정
        ActionBar actionbarLogin = getSupportActionBar();
        actionbarLogin.setDisplayHomeAsUpEnabled(true); // Main Activity 로 되돌아 가기에 사용할 화살표를 표시함.

        buttonJoin.setOnClickListener(new View.OnClickListener() { // 가입 버튼
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Activity 꼬임? 방지 - 자세한 내용은 직접 찾아 볼것.
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); // Activity 가 스택에 남아 있으면 onCreate 를하지 않음.
                startActivity(intent);
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() { // 로그인 버튼
            @Override
            public void onClick(View v) {
                String accountEmail = emailEditText.getText().toString();
                String accountPassword = passwordEditText.getText().toString();

                firebaseAuth.signInWithEmailAndPassword(accountEmail, accountPassword)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    //FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                    Toast.makeText(getApplicationContext(), "환영합니다!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.putExtra("loginStateChange", true);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Activity 꼬임? 방지 - 자세한 내용은 직접 찾아 볼것.
                                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); // Activity 가 스택에 남아 있으면 onCreate 를하지 않음.
                                    startActivity(intent);
                                }

                                else {
                                    Toast.makeText(getApplicationContext(), "로그인을 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
            }
        });

    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Activity 꼬임? 방지 - 자세한 내용은 직접 찾아 볼것.
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); // Activity 가 스택에 남아 있으면 onCreate 를하지 않음.
        startActivity(intent);
        finish();
        return true;
    }
}