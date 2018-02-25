package com.example.alexmelnikov.vocabra.ui;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.internal.BottomNavigationPresenter;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.example.alexmelnikov.vocabra.R;
import com.example.alexmelnikov.vocabra.Screens;
import com.example.alexmelnikov.vocabra.VocabraApp;
import com.example.alexmelnikov.vocabra.common.BackButtonListener;
import com.example.alexmelnikov.vocabra.common.RouterProvider;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;


import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.terrakok.cicerone.Navigator;
import ru.terrakok.cicerone.NavigatorHolder;
import ru.terrakok.cicerone.Router;
import ru.terrakok.cicerone.commands.Back;
import ru.terrakok.cicerone.commands.Command;
import ru.terrakok.cicerone.commands.Replace;
import ru.terrakok.cicerone.commands.SystemMessage;

public class MainActivity extends MvpAppCompatActivity implements MainView, RouterProvider {
    private WordBrowserFragment wordBrowserFragment;
    private TranslatorFragment translatorFragment;

    @Inject
    Router router;

    @Inject
    NavigatorHolder navigatorHolder;

    @InjectPresenter
    MainPresenter presenter;

    @ProvidePresenter
    public MainPresenter createBottomNavigationPresenter() {
        return new MainPresenter(router);
    }

    @BindView(R.id.bottom_nav_bar) BottomNavigationViewEx bottomNavBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        VocabraApp.INSTANCE.getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        initViews();
        initContainers();

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
                        presenter.onTabWordBrowserClick();
                        break;
                    case R.id.translator_menu_item:
                        presenter.onTabTranslatorClick();
                        break;
                }
                return true;
            }
        });
    }

    private void initContainers() {
        FragmentManager fm = getFragmentManager();

        wordBrowserFragment = (WordBrowserFragment) fm.findFragmentByTag("WORD_BROWSER");
        if (wordBrowserFragment == null) {
            wordBrowserFragment = WordBrowserFragment.getNewInstance("WORD_BROWSER");
            fm.beginTransaction()
                    .add(R.id.fragment_container, wordBrowserFragment, "WORD_BROWSER")
                    .detach(wordBrowserFragment).commit();
        }

        translatorFragment = (TranslatorFragment) fm.findFragmentByTag("TRANSLATOR");
        if (translatorFragment == null) {
            translatorFragment = TranslatorFragment.getNewInstance("TRANSLATOR");
            fm.beginTransaction()
                    .add(R.id.fragment_container, translatorFragment, "TRANSLATOR")
                    .detach(translatorFragment).commit();
        }
    }


    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        navigatorHolder.setNavigator(navigator);
    }

    @Override
    protected void onPause() {
        navigatorHolder.removeNavigator();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        android.app.Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null
                && fragment instanceof BackButtonListener
                && ((BackButtonListener) fragment).onBackPressed()) {
            return;
        } else {
            presenter.onBackPressed();
        }
    }


    private Navigator navigator = new Navigator() {
        @Override
        public void applyCommands(Command[] commands) {
            Log.d("MyTag", "apply all commands");
            for (Command command : commands) applyCommand(command);
        }

        private void applyCommand(Command command) {
            Log.d("MyTag", "applying command");
            if (command instanceof Back) {
                finish();
            } else if (command instanceof SystemMessage) {
                Toast.makeText(MainActivity.this, ((SystemMessage) command).getMessage(), Toast.LENGTH_SHORT).show();
            } else if (command instanceof Replace) {
                FragmentManager fm = getFragmentManager();

                switch (((Replace) command).getScreenKey()) {
                    case Screens.WORD_BROWSER_SCREEN:
                        fm.beginTransaction()
                                .detach(translatorFragment)
                                .attach(wordBrowserFragment)
                                .commit();
                        break;
                    case Screens.TRANSLATOR_SCREEN:
                        fm.beginTransaction()
                                .detach(wordBrowserFragment)
                                .attach(translatorFragment)
                                .commit();
                        break;
                }
            }
        }
    };

    @Override
    public Router getRouter() {
        return router;
    }
}
