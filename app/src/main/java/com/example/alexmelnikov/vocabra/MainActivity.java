package com.example.alexmelnikov.vocabra;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.alexmelnikov.vocabra.ui.WordBrowserFragment;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;


import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.bottom_nav_bar) BottomNavigationViewEx bnve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        bnve.enableAnimation(false);
        bnve.enableShiftingMode(false);
        bnve.enableItemShiftingMode(false);
        bnve.setTextVisibility(false);

        WordBrowserFragment wbf = new WordBrowserFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, wbf).commit();
    }


}
