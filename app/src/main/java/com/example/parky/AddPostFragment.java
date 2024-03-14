package com.example.parky;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class AddPostFragment extends Fragment {

    private static final int PERMISSION_REQUEST_LOCATION = 1;
    boolean isDenied = false;
    LocationManager lm;
    public AddPostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_add_post, container, false);

        // findviewbyid is used by addin .view in starting
        EditText locality = view.findViewById(R.id.locality);

        locality.setOnClickListener(v -> {
            if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(getActivity(), new String[]
                        {Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_LOCATION);
            }
            else {
                startNextActivity();
            }
        });

        return view;
    }

    private void startNextActivity() {
        startActivity(new Intent(getActivity(), MapActivity.class));
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission granted, start the next activity
                startNextActivity();
            } else {
                // Location permission denied, handle it gracefully (e.g., show a message)
                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show());
            }

        }

    }


}