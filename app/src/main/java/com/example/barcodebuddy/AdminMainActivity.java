package com.example.barcodebuddy;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class AdminMainActivity extends AppCompatActivity {
BottomNavigationView navAdmin;
LinearLayout container;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_main);

        navAdmin = findViewById(R.id.nav_admin);
        container = findViewById(R.id.container);

        loadFragment(new AdminHomeFragment());

        navAdmin.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()== R.id.home_admin){
                    loadFragment(new AdminHomeFragment());
                }
                else if(item.getItemId() == R.id.status_admin){
                    loadFragment(new AdminStatusFragment());
                }
                else{
                    loadFragment(new AdminProfileFragment());
                }

                return true;
            }
        });


    }
    public void loadFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container , fragment).commit();
    }
}