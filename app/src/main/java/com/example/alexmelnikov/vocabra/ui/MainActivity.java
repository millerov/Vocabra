package com.example.alexmelnikov.vocabra.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.alexmelnikov.vocabra.R;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;


import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.bottom_nav_bar) BottomNavigationViewEx bottomNavBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        initViews();

        if (savedInstanceState == null) {
            bottomNavBar.setSelectedItemId(R.id.word_browser_menu_item);
        }
    }

    public void initViews() {
        bottomNavBar.enableAnimation(false);
        bottomNavBar.enableShiftingMode(false);
        bottomNavBar.enableItemShiftingMode(false);
        bottomNavBar.setTextVisibility(false);

        bottomNavBar.setOnNavigationItemSelectedListener(new BottomNavigationViewEx.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.word_browser_menu_item:
                        WordBrowserFragment wbf = new WordBrowserFragment();
                        getFragmentManager().beginTransaction()
                                .add(R.id.fragment_container, wbf).commit();
                        break;
                    case R.id.translator_menu_item:
                        TranslatorFragment tf = new TranslatorFragment();
                        getFragmentManager().beginTransaction()
                                .add(R.id.fragment_container, tf).commit();
                        break;
                    case 3:
                        break;
                }
                return true;
            }
        });
    }


}
