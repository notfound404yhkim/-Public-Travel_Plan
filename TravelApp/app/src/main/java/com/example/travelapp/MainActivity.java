package com.example.travelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    MainFragment mainFragment;
    MapFragment mapFragment;
    CommunityFragment communityFragment;
    ProfileFragment profileFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainFragment = new MainFragment();
        mapFragment = new MapFragment();
        communityFragment = new CommunityFragment();
        profileFragment = new ProfileFragment();

        getSupportActionBar().setTitle("여행 시작과 끝");

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.menu_main){
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout,mainFragment).commit();
                }
                if(item.getItemId()==R.id.menu_map){
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout,mapFragment).commit();
                }
                if(item.getItemId()==R.id.menu_community){
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout,communityFragment).commit();
                }
                if(item.getItemId()==R.id.menu_profile){
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout,profileFragment).commit();
                }
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.menu_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

}