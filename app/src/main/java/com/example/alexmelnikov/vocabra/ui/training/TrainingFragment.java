package com.example.alexmelnikov.vocabra.ui.training;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.androidanimations.library.sliders.SlideInDownAnimator;
import com.daimajia.androidanimations.library.sliders.SlideOutLeftAnimator;
import com.daimajia.androidanimations.library.sliders.SlideOutRightAnimator;
import com.example.alexmelnikov.vocabra.R;
import com.example.alexmelnikov.vocabra.VocabraApp;
import com.example.alexmelnikov.vocabra.data.DecksRepository;
import com.example.alexmelnikov.vocabra.model.Deck;
import com.example.alexmelnikov.vocabra.ui.BaseFragment;
import com.example.alexmelnikov.vocabra.ui.main.MainActivity;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Created by AlexMelnikov on 29.03.18.
 */


public class TrainingFragment extends BaseFragment implements TrainingView {

    private static final String TAG = "MyTag";

    @InjectPresenter
    TrainingPresenter mTrainingPresenter;

    @Inject
    DecksRepository mDecksRep;

    @BindView(R.id.rl_deck) RelativeLayout rlDeck;
    @BindView(R.id.tv_deck_name) TextView tvDeckName;
    @BindView(R.id.tv_ready_counter) TextView tvReadyCounter;
    @BindView(R.id.tv_new_counter) TextView tvNewCounter;
    @BindView(R.id.rl_front) RelativeLayout rlFront;
    @BindView(R.id.rl_back) RelativeLayout rlBack;
    @BindView(R.id.tv_front) TextView tvFront;
    @BindView(R.id.tv_back) TextView tvBack;
    @BindView(R.id.layout_buttons) RelativeLayout rlButtons;
    @BindView(R.id.btn_back) ImageButton btnBack;
    @BindView(R.id.btn_more) ImageButton btnMore;
    @BindView(R.id.btn_show_back) Button btnShowBack;
    @BindView(R.id.btn_easy) RelativeLayout btnEasy;
    @BindView(R.id.btn_good) RelativeLayout btnGood;
    @BindView(R.id.btn_forgot) RelativeLayout btnForgot;
    @BindView(R.id.btn_hard) RelativeLayout btnHard;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_training, container, false);
        ButterKnife.bind(this, view);

        ((MainActivity) getActivity()).hideBottomNavigationBar();

        Bundle args = getArguments();
        if (args != null) {
            String transitionName = args.getString("transitionName");
            int deckId = args.getInt("deckId");
            int newCount = args.getInt("newCount");
            int oldReadyCount = args.getInt("oldReadyCount");
            Deck deck = mDecksRep.getDeckById(deckId);

            rlDeck.setTransitionName(transitionName);

            tvNewCounter.setText(newCount + "");
            tvReadyCounter.setText(oldReadyCount + "");

            if (deck != null) {
                tvDeckName.setText(deck.getName());

                final Drawable drawable = getActivity().getResources().getDrawable(R.drawable.bg_training_card);
                drawable.setColorFilter(deck.getColor(), PorterDuff.Mode.SRC_ATOP);
                rlDeck.setBackground(drawable);
            }

            mTrainingPresenter.setupDeck(deck);
        } else {
            Log.e(TAG, "onCreateView: null args");
        }

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VocabraApp.getAppComponent().inject(this);
    }

    @Override
    public void attachInputListeners() {
        Disposable backButton = RxView.clicks(btnBack)
                .subscribe(o -> closeFragment());

        Disposable moreButton = RxView.clicks(btnMore)
                .subscribe(o -> mTrainingPresenter.moreButtonPressed());

        Disposable showBackButton = RxView.clicks(btnShowBack)
                .subscribe(o -> {
                    btnShowBack.setClickable(false);
                    disableButtonsWhileAnimating();
                    mTrainingPresenter.showBackRequest();
                }
                    );

        Disposable optionEasyButton = RxView.clicks(btnEasy)
                .subscribe(o -> {
                    disableButtonsWhileAnimating();
                    mTrainingPresenter.optionEasyPicked();
                });

        Disposable optionGoodButton = RxView.clicks(btnGood)
                .subscribe(o -> {
                    disableButtonsWhileAnimating();
                    mTrainingPresenter.optionGoodPicked();
                });

        Disposable optionForgotButton = RxView.clicks(btnForgot)
                .subscribe(o -> {
                    disableButtonsWhileAnimating();
                    mTrainingPresenter.optionForgotPicked();
                });

        mDisposable.addAll(backButton, moreButton, showBackButton, optionEasyButton, optionGoodButton,
                optionForgotButton);
    }

    @Override
    public void detachInputListeners() {
        mDisposable.clear();
    }

    @Override
    public void showFront(String front, boolean firstAttach) {
        Log.d(TAG, "showFront: " + firstAttach);
        if (firstAttach) {
            rlFront.postDelayed(new Runnable() {
                @Override
                public void run() {
                    YoYo.with(new SlideInDownAnimator())
                            .interpolate(new AccelerateDecelerateInterpolator())
                            .duration(500)
                            .playOn(rlFront);
                    rlFront.setVisibility(View.VISIBLE);
                    tvFront.setText(front);
                }
            }, 270);
        } else {
            rlFront.postDelayed(new Runnable() {
                @Override
                public void run() {
                    YoYo.with(new SlideInDownAnimator())
                            .interpolate(new AccelerateDecelerateInterpolator())
                            .duration(500)
                            .playOn(rlFront);
                    rlFront.setVisibility(View.VISIBLE);
                    tvFront.setText(front);
                }
            }, 520);
        }
    }

    @Override
    public void showBack(String back) {
        YoYo.with(new SlideInDownAnimator())
                .interpolate(new AccelerateDecelerateInterpolator())
                .duration(500)
                .playOn(rlBack);
        rlBack.setVisibility(View.VISIBLE);
        tvBack.setText(back);
    }

    @Override
    public void hideCurrentFrontAndBack() {
        YoYo.with(new SlideOutLeftAnimator())
                .interpolate(new AccelerateDecelerateInterpolator())
                .duration(500)
                .playOn(rlFront);
        YoYo.with(new SlideOutRightAnimator())
                .interpolate(new AccelerateDecelerateInterpolator())
                .duration(500)
                .playOn(rlBack);
    }

    @Override
    public void showOptions(boolean withHardBtn) {
      //  btnShowBack.setClickable(false);
        expandButtonsLayout(withHardBtn);
    }

    @Override
    public void hideOptions(boolean withHardBtn) {
/*        btnEasy.setClickable(false);
        btnGood.setClickable(false);
        btnForgot.setClickable(false);
        if (withHardBtn)
            btnHard.setClickable(false);*/
        hideOptionButtonsWithAnimation(withHardBtn);
    }

    private void disableButtonsWhileAnimating() {
        btnEasy.setClickable(false);
        btnGood.setClickable(false);
        btnForgot.setClickable(false);
        btnHard.setClickable(false);
    }


    private void expandButtonsLayout(boolean withHardBtn) {
        double reqViewHeightDouble = rlButtons.getHeight() * 2.7;
        int requiredViewHeight = (int) reqViewHeightDouble;
        ResizeHeightAnimation animation = new ResizeHeightAnimation(rlButtons, requiredViewHeight, rlButtons.getHeight());
        animation.setDuration(300);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                btnShowBack.setVisibility(View.GONE);
                btnEasy.setVisibility(View.VISIBLE);
                btnGood.setVisibility(View.VISIBLE);
                btnForgot.setVisibility(View.VISIBLE);
                if (withHardBtn)
                    btnHard.setVisibility(View.VISIBLE);
                showOptionButtonsWithAnimation(withHardBtn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationStart(Animation animation) {}
        });
        rlButtons.startAnimation(animation);

    }

    private void narrowButtonsLayout(boolean withHardBtn) {
        double reqViewHeightDouble= rlButtons.getHeight() / 2.7;
        int requiredViewHeight = (int) reqViewHeightDouble;
        ResizeHeightAnimation animation = new ResizeHeightAnimation(rlButtons, requiredViewHeight, rlButtons.getHeight());
        animation.setDuration(300);
        rlButtons.startAnimation(animation);
    }

    private void showOptionButtonsWithAnimation(boolean withHardBtn) {
        btnEasy.postDelayed(new Runnable() {
            @Override
            public void run() {
                btnEasy.animate().yBy((btnEasy.getHeight() * -1) - ((ViewGroup.MarginLayoutParams) btnEasy.getLayoutParams()).topMargin).setDuration(200);
                if (withHardBtn)
                    btnHard.animate().yBy((btnForgot.getHeight()) + ((ViewGroup.MarginLayoutParams) btnForgot.getLayoutParams()).topMargin).setDuration(200);
                btnForgot.animate().yBy((btnForgot.getHeight()) + ((ViewGroup.MarginLayoutParams) btnForgot.getLayoutParams()).topMargin).setDuration(200)
                        .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        ResizeButtonsWidthWithAnimation animation = new ResizeButtonsWidthWithAnimation(btnForgot, btnHard, (btnEasy.getWidth() / 2) - 20, btnForgot.getWidth());
                        animation.setDuration(200);
                        btnForgot.startAnimation(animation);
                        btnForgot.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                btnForgot.animate().xBy((btnEasy.getWidth() / -4) - 10).setDuration(150);
                                btnHard.animate().xBy((btnEasy.getWidth() / 4) + 10).setDuration(150);
                                btnShowBack.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        btnEasy.setClickable(true);
                                        btnGood.setClickable(true);
                                        btnForgot.setClickable(true);
                                        if (withHardBtn)
                                            btnHard.setClickable(true);
                                    }
                                }, 200);

                            }
                        }, 320);
                    }
                });
            }
        }, 100);
    }

    private void hideOptionButtonsWithAnimation(boolean withHardBtn) {
        btnEasy.animate().yBy((btnEasy.getHeight()) + ((ViewGroup.MarginLayoutParams) btnEasy.getLayoutParams()).topMargin).setDuration(200);
        if (withHardBtn)
            btnHard.animate().yBy((btnForgot.getHeight() * -1) - ((ViewGroup.MarginLayoutParams) btnHard.getLayoutParams()).topMargin).setDuration(200);
        btnForgot.animate().yBy((btnForgot.getHeight() * -1) - ((ViewGroup.MarginLayoutParams) btnForgot.getLayoutParams()).topMargin).setDuration(200)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        ResizeButtonsWidthWithAnimation animation = new ResizeButtonsWidthWithAnimation(btnForgot, btnHard, (btnEasy.getWidth() * 2) + 20, btnForgot.getWidth());
                        animation.setDuration(1);
                        btnForgot.startAnimation(animation);
                        btnForgot.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                btnForgot.animate().xBy((btnEasy.getWidth() / 4) + 10).setDuration(10);
                                btnHard.animate().xBy((btnEasy.getWidth() / -4) - 10).setDuration(10);
                                btnShowBack.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        btnShowBack.setClickable(true);
                                    }
                                }, 30);
                            }
                        }, 30);

                        btnEasy.setVisibility(View.GONE);
                        btnGood.setVisibility(View.GONE);
                        btnForgot.setVisibility(View.GONE);
                        if (withHardBtn)
                            btnHard.setVisibility(View.GONE);
                        btnShowBack.setVisibility(View.VISIBLE);
                        narrowButtonsLayout(withHardBtn);
                    }
                });

    }


    @Override
    public boolean onBackPressed() {
        ((MainActivity) getActivity()).showBottomNavigationBar();
        return super.onBackPressed();
    }

    //Used on left upper corner back ImageButton click
    private void closeFragment() {
        ((MainActivity) getActivity()).showBottomNavigationBar();
        getFragmentManager().popBackStack();
    }

}