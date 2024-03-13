package com.example.parky;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id =item.getItemId();

                if(id == R.id.profile){
                    loadFragment(new ProfileFragment(), 1);
                }
                else if(id == R.id.addPost){
                    loadFragment(new AddPostFragment(), 1);
                }
                else{
                    loadFragment(new DashboardFragment(), 0);
                }

                return true;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.home);
    }

    public void loadFragment(Fragment f, int flag)
    {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if(flag == 0) {
            ft.replace(R.id.container, f);
            fm.popBackStack("root_fragment",FragmentManager.POP_BACK_STACK_INCLUSIVE);
            ft.addToBackStack("root_fragment");
        }else {
            ft.replace(R.id.container, f);
            ft.addToBackStack(null);  // This makes sure to manage the back stack of the fragments
        }
        ft.commit();
    }
}