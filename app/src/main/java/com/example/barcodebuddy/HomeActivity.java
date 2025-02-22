package com.example.barcodebuddy;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private BottomNavigationView nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        nav = findViewById(R.id.btm_nav);

        loadFragment(new HomeFragment(), "Home");

        nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    loadFragment(new HomeFragment(), "Home");
                } else if (item.getItemId() == R.id.blog) {
                    loadFragment(new BlogFragment(), "Blog");
                } else if (item.getItemId() == R.id.notification_user) {
                    loadFragment(new UserNotificationFragment(), "Notifications");
                } else {
                    loadFragment(new UserFragment(), "Profile");
                }
                return true;
            }
        });
    }


    private void loadFragment(Fragment fragment, String title) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();

        if (toolbar != null) {
            toolbar.setTitle(title);
            toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
            toolbar.setBackgroundColor(getResources().getColor(R.color.light_green));
        }
    }
}
