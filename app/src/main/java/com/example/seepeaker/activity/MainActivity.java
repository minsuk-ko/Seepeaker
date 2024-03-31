package com.example.seepeaker.activity;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.seepeaker.R;
import com.example.seepeaker.controller.UserController;
import com.example.seepeaker.fragment.HomeFragment;
import com.example.seepeaker.fragment.TestFragment;
import com.example.seepeaker.fragment.ToolsFragment;
import com.example.seepeaker.fragment.UserFragment;
import com.example.seepeaker.model.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

// 메인
public class MainActivity extends AppCompatActivity {
    LinearLayout home_layout;
    BottomNavigationView bottomNavigationView;

    UserController UserController;

    private void init() {
        home_layout = findViewById(R.id.home_layout);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();
        SettingListener();
        UserController = new UserController();
        bottomNavigationView.setSelectedItemId(R.id.tab_home);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        UserController userController = new UserController(); // 유저 값을 읽어 오거나 쓰는 객체

        // Show or hide the "login" and "logout" items based on the state
        MenuItem loginItem = menu.findItem(R.id.login);
        MenuItem logoutItem = menu.findItem(R.id.logout);

        if (UserController.isUserLogin()) {
            loginItem.setVisible(false);
            logoutItem.setVisible(true);
        } else {
            loginItem.setVisible(true);
            logoutItem.setVisible(false);
        }



        return true;
    }

    private void SettingListener() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new TabSelectedListener());
    }

    class TabSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            int itemId = menuItem.getItemId();
            if (itemId == R.id.tab_home) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.home_layout, new HomeFragment())
                        .commit();
                return true;
            }
            else if (itemId == R.id.tab_practice) {
                if (UserController.isUserLogin()) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.home_layout, new TestFragment())
                            .commit();
                    return true;
                }
                else {
                    showLoginDialog();
                    return true;
                }
            }
            else if (itemId == R.id.tab_user) {
                if (UserController.isUserLogin()) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.home_layout, new UserFragment())
                            .commit();
                    return true;
                }
                else {
                    showLoginDialog();
                    return true;
                }
            }
            else if (itemId == R.id.tab_tools) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.home_layout, new ToolsFragment())
                        .commit();
                return true;
            }

            return false;
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.login) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            return true;
        }
        if (item.getItemId() == R.id.logout) {
            showLogoutDialog();
            return true;
        }
        /*if (item.getItemId() == R.id.settings) { // 설정 부분 (사용 안함)
            startActivity(new Intent(getApplicationContext(), SettingActivity.class));
            return true;
        }*/
        /*if (item.getItemId() == R.id.alarm) { // 알림 부분 (사용 안함)
            startActivity(new Intent(getApplicationContext(), AlarmActivity.class));
            return true;
        }*/
        else { return super.onOptionsItemSelected(item); }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // Check if there is a state change notification from LoginActivity
        if (intent.getBooleanExtra("loginStateChange", false)) {
            // Refresh the options menu to reflect the updated state
            invalidateOptionsMenu();
        }
    }

    private void showLoginDialog() {
        View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_login_ask, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(dialogView);

        Button loginButton = dialogView.findViewById(R.id.dialog_login_button);
        Button cancelButton = dialogView.findViewById(R.id.dialog_cancel_button);

        final AlertDialog alertDialog = builder.create();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle OK button click
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                alertDialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { alertDialog.dismiss(); }
        });

        alertDialog.show();
    }

    private void showLogoutDialog() {
        View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_logout_ask, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(dialogView);

        Button logoutButton = dialogView.findViewById(R.id.dialog_logout_button);
        Button cancelButton = dialogView.findViewById(R.id.dialog_cancel_button);

        final AlertDialog alertDialog = builder.create();

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                invalidateOptionsMenu();
                alertDialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { alertDialog.dismiss(); }
        });

        alertDialog.show();
    }

}