package kh.com.ilost.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import kh.com.ilost.R;
import kh.com.ilost.fragments.FragmentHome;
import kh.com.ilost.fragments.FragmentMessage;
import kh.com.ilost.fragments.SettingFragment;

public class MainActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener {


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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sign_in:
                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                break;
            case R.id.menu_profile:
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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
        } else if(itemId == R.id.navigation_settings){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_layout, new SettingFragment()).commit();
        }
        return true;
    }


    private void onHomeClick() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_layout, new FragmentHome()).commit();
    }

}
