package kh.com.ilost.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import kh.com.ilost.R;
import kh.com.ilost.fragments.FragmentHome;
import kh.com.ilost.fragments.FragmentMessage;

public class MainActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener {

    FirebaseUser firebaseUser;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);


        onHomeClick();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.navigation_home) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_layout, new FragmentHome()).commit();
        } else if (itemId == R.id.navigation_message) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_layout, new FragmentMessage()).commit();
        }
        return true;
    }


    private void onHomeClick() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_layout, new FragmentHome()).commit();
    }

}
