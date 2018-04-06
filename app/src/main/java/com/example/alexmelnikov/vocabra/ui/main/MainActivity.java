package com.example.alexmelnikov.vocabra.ui.main;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.transition.Fade;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.transition.AutoTransition;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.afollestad.materialdialogs.util.DialogUtils;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.alexmelnikov.vocabra.R;
import com.example.alexmelnikov.vocabra.model.Translation;
import com.example.alexmelnikov.vocabra.ui.BaseActivity;
import com.example.alexmelnikov.vocabra.ui.SnackBarActionHandler;
import com.example.alexmelnikov.vocabra.ui.deck_add.DeckAddFragment;
import com.example.alexmelnikov.vocabra.ui.decks_for_train.DecksForTrainingFragment;
import com.example.alexmelnikov.vocabra.ui.translator.TranslatorFragment;
import com.example.alexmelnikov.vocabra.ui.cardbrowser.CardBrowserFragment;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;


import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;


public class MainActivity extends BaseActivity implements MainView {
    private CardBrowserFragment cardBrowserFragment;
    private TranslatorFragment translatorFragment;

    private static final String TAG = "MyTag";

    @InjectPresenter
    MainPresenter presenter;

    @BindView(R.id.bottom_nav_bar) BottomNavigationViewEx bottomNavBar;

    private Snackbar snack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        Log.d(TAG, "onCreate: " + R.color.colorPrimary);

        initViews();

        if (savedInstanceState == null) {
            bottomNavBar.setSelectedItemId(R.id.word_browser_menu_item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*if (bottomNavBar.getVisibility() == View.GONE)
           showBottomNavigationBar();*/
    }


    public void initViews() {
        bottomNavBar.enableAnimation(false);
        bottomNavBar.enableShiftingMode(false);
        bottomNavBar.enableItemShiftingMode(false);
        bottomNavBar.setTextVisibility(false);

        bottomNavBar.setOnNavigationItemSelectedListener(new BottomNavigationViewEx.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                presenter.bottomNavigationClick(bottomNavBar.getMenuItemPosition(item));

                //Starting intent to send realm db via mail
                if (bottomNavBar.getMenuItemPosition(item) == 3) {
                    exportDatabase();
                }

                return true;

            }
        });
    }


    @Override
    public void replaceFragmentNavigationBar(int index, int previousIndex) {
        Fragment fragment;
        FragmentTransaction fts = getSupportFragmentManager().beginTransaction();

        if (index == 0) {
            fragment = new CardBrowserFragment();
        } else if (index == 1) {
            fragment = TranslatorFragment.newInstance(null, false, false);
        } else if (index == 2) {
            fragment = new DecksForTrainingFragment();
        } else {
            fragment = new CardBrowserFragment();
        }


        if (index == previousIndex) {
            fts.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        }

        fts.replace(R.id.fragment_container, fragment).commit();
    }

    /** @param actionId helps to understand which message calls onSnackbarEvent in case if
     *                   one fragment calls multiple different messages with action;
     *                   should equal 0 if withAction is false*/
    @Override
    public void showMessage(int actionId, String message, boolean withAction, SnackBarActionHandler presenter, String actionText) {
        if (withAction) {
            snack = Snackbar.make(findViewById(R.id.main_coordinator_layout), message, Snackbar.LENGTH_LONG);
        } else {
            snack = Snackbar.make(findViewById(R.id.main_coordinator_layout), message, Snackbar.LENGTH_SHORT);
        }

        //Setting margins to display snackbar above the navigation bar
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)
                snack.getView().getLayoutParams();
        params.setMargins(0, 0, 0, bottomNavBar.getItemHeight()-4);
        snack.getView().setLayoutParams(params);

        if (withAction) {
            snack.setAction(actionText, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    presenter.onSnackbarEvent(actionId);
                }
            });
        }

        snack.show();

    }

    @Override
    public void hideBottomNavigationBar() {
        bottomNavBar.setVisibility(View.GONE);
    }

    @Override
    public void showBottomNavigationBar() {
        bottomNavBar.animate().y(2405.0f).withEndAction(new Runnable() {
            @Override
            public void run() {
                bottomNavBar.setVisibility(View.VISIBLE);
                bottomNavBar.animate().yBy(-100).setDuration(200);
            }
        });
    }


    public void exportDatabase() {

        // init realm
        Realm realm = Realm.getDefaultInstance();

        File exportRealmFile = null;
        // get or create an "export.realm" file
        exportRealmFile = new File(this.getExternalCacheDir(), "export.realm");

        // if "export.realm" already exists, delete
        exportRealmFile.delete();

        // copy current realm to "export.realm"
        realm.writeCopyTo(exportRealmFile);


        realm.close();

        // init email intent and add export.realm as attachment
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, "melnikov.ws@gmail.com");
        intent.putExtra(Intent.EXTRA_SUBJECT, "My Database");
        intent.putExtra(Intent.EXTRA_TEXT, "realm database file");
        Uri u = Uri.fromFile(exportRealmFile);
        intent.putExtra(Intent.EXTRA_STREAM, u);

        // start email intent
        startActivity(Intent.createChooser(intent, "title"));
    }

}
