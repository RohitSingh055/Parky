package com.example.parky;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.home);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            Double latitude = getIntent().getExtras().getDouble("Latitude");
            Double longitude = getIntent().getExtras().getDouble("Longitude");
            Log.d("meow", bundle.toString());
            Toast.makeText(this, bundle.toString(), Toast.LENGTH_SHORT).show();
            Bundle args = new Bundle();
            args.putDouble("Latitude", latitude);
            args.putDouble("Longitude", longitude);

            loadFragment(new AddPostFragment(), 0, args);
            bottomNavigationView.setSelectedItemId(R.id.addPost);
        }


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id =item.getItemId();

                if(id == R.id.profile){
                    loadFragment(new ProfileFragment(), 1, null);
                }
                else if(id == R.id.addPost){
                    loadFragment(new AddPostFragment(), 1, null);
                }
                else{
                    loadFragment(new DashboardFragment(), 0, null);
                }

                return true;
            }
        });
    }

    public void loadFragment(Fragment f, int flag, Bundle args)
    {
        if(args!=null) {
            f.setArguments(args);
        }

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