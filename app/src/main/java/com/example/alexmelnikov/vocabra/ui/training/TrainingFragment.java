package com.example.alexmelnikov.vocabra.ui.training;

import android.animation.Animator;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.renderscript.RenderScript;
import android.support.annotation.Nullable;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.androidanimations.library.sliders.SlideInDownAnimator;
import com.daimajia.androidanimations.library.sliders.SlideOutLeftAnimator;
import com.daimajia.androidanimations.library.sliders.SlideOutRightAnimator;
import com.daimajia.androidanimations.library.sliders.SlideOutUpAnimator;
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
    @BindView(R.id.layout_counters) LinearLayout layoutCounters;
    @BindView(R.id.tv_ready_counter) TextView tvReadyCounter;
    @BindView(R.id.tv_new_counter) TextView tvNewCounter;
    @BindView(R.id.rl_front) RelativeLayout rlFront;
    @BindView(R.id.rl_back) RelativeLayout rlBack;
    @BindView(R.id.tv_front) TextView tvFront;
    @BindView(R.id.tv_back) TextView tvBack;
    @BindView(R.id.tv_context) TextView tvContext;
    @BindView(R.id.layout_buttons) RelativeLayout rlButtons;
    @BindView(R.id.btn_back) ImageButton btnBack;
    @BindView(R.id.btn_edit) ImageButton btnEdit;
    @BindView(R.id.btn_to_previous) ImageButton btnToPrev;
    @BindView(R.id.btn_show_back) Button btnShowBack;
    @BindView(R.id.btn_easy) RelativeLayout btnEasy;
    @BindView(R.id.btn_good) RelativeLayout btnGood;
    @BindView(R.id.btn_forgot) RelativeLayout btnForgot;

    @BindView(R.id.layout_forgot_and_hard_btns) LinearLayout layout_hard_mode_btns;
    @BindView(R.id.btn_forgot_for_hard) RelativeLayout btnForgotForHard;
    @BindView(R.id.btn_hard) RelativeLayout btnHard;
    @BindView(R.id.tv_good_info) TextView tvGoodInfo;
    @BindView(R.id.tv_easy_info) TextView tvEasyInfo;
    @BindView(R.id.tv_forgot_info) TextView tvForgotInfo;
    @BindView(R.id.tv_hard_info) TextView tvHardInfo;
    @BindView(R.id.sv_front_back) ScrollView svFrontBack;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_training, container, false);
        ButterKnife.bind(this, view);

        ((MainActivity) getActivity()).hideBottomNavigationBar();

        Bundle args = getArguments();
        if (args != null) {
            String transitionName = args.getString("transitionName");
            int deckId = args.getInt("deckId");
            Deck deck = mDecksRep.getDeckById(deckId);

            rlDeck.setTransitionName(transitionName);

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

        Disposable prevButton = RxView.clicks(btnToPrev)
                .subscribe(o -> {
                    mTrainingPresenter.returnToPreviousCardRequest();
                });

        Disposable showBackButton = RxView.clicks(btnShowBack)
                .subscribe(o -> {
                    mTrainingPresenter.showBackRequest();
                }
                    );

        Disposable optionEasyButton = RxView.clicks(btnEasy)
                .subscribe(o -> {
                    mTrainingPresenter.optionEasyPicked();
                });

        Disposable optionGoodButton = RxView.clicks(btnGood)
                .subscribe(o -> {
                    mTrainingPresenter.optionGoodPicked();
                });

        Disposable optionForgotButton = RxView.clicks(btnForgot)
                .subscribe(o -> {
                    mTrainingPresenter.optionForgotPicked();
                });

        Disposable optionForgotForHardButton = RxView.clicks(btnForgotForHard)
                .subscribe(o -> {
                    mTrainingPresenter.optionForgotPicked();
                });

        Disposable optionHardButton = RxView.clicks(btnHard)
                .subscribe(o -> {
                    mTrainingPresenter.optionHardPicked();
                });


        mDisposable.addAll(backButton, prevButton, showBackButton, optionEasyButton, optionGoodButton,
                optionForgotButton, optionForgotForHardButton, optionHardButton);
    }

    @Override
    public void detachInputListeners() {
        mDisposable.clear();
    }

    @Override
    public void fillCounters(int newCardsCount, int oldReadyCardsCount) {
        tvNewCounter.setText(newCardsCount + "");
        tvReadyCounter.setText(oldReadyCardsCount + "");
    }

    @Override
    public void showFront(String front, boolean firstAttach) {
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
    public void fillOptionsTextViews(boolean withHardBtn, String easyTime, String goodTime,
                                     String forgotTime, String hardTime) {
        tvGoodInfo.setText(goodTime);
        tvEasyInfo.setText(easyTime);
        tvForgotInfo.setText(forgotTime);
        if (withHardBtn)
            tvHardInfo.setText(hardTime);
    }

    @Override
    public void updatePreviousButton(boolean isEnabled) {
        btnToPrev.setEnabled(isEnabled);
    }

    @Override
    public void showBack(String back, @Nullable String context) {
        YoYo.with(new SlideInDownAnimator())
                .interpolate(new AccelerateDecelerateInterpolator())
                .duration(500)
                .playOn(rlBack);
        rlBack.setVisibility(View.VISIBLE);
        tvBack.setText(back);
         if (!context.isEmpty()) {
             layoutCounters.setVisibility(View.GONE);
             tvDeckName.setVisibility(View.GONE);
             tvContext.setVisibility(View.VISIBLE);
             tvContext.setText(context);
             tvContext.setSelected(true);
         }
        svFrontBack.postDelayed(new Runnable() {
            @Override
            public void run() {
                svFrontBack.fullScroll(View.FOCUS_DOWN);
            }
        }, 100);

    }

    //animateUp is true when method called after getPreviousCard request
    @Override
    public void hideCurrentFrontAndBack(boolean animateUp, boolean onlyFront) {
        if (!animateUp) {
            YoYo.with(new SlideOutLeftAnimator())
                    .interpolate(new AccelerateDecelerateInterpolator())
                    .duration(500)
                    .playOn(rlFront);
            if (!onlyFront) {
                YoYo.with(new SlideOutRightAnimator())
                        .interpolate(new AccelerateDecelerateInterpolator())
                        .duration(500)
                        .playOn(rlBack);
            }
        } else {
            YoYo.with(new SlideOutUpAnimator())
                    .interpolate(new AccelerateDecelerateInterpolator())
                    .duration(500)
                    .playOn(rlFront);
            if (!onlyFront) {
                YoYo.with(new SlideOutUpAnimator())
                        .interpolate(new AccelerateDecelerateInterpolator())
                        .duration(500)
                        .playOn(rlBack);
            }
        }
        if (tvContext.getVisibility() == View.VISIBLE) {
            tvContext.setText("");
            layoutCounters.setVisibility(View.VISIBLE);
            tvDeckName.setVisibility(View.VISIBLE);
            tvContext.setVisibility(View.GONE);
        }
        svFrontBack.postDelayed(new Runnable() {
            @Override
            public void run() {
                svFrontBack.fullScroll(View.FOCUS_UP);
            }
        }, 100);
    }

    @Override
    public void showFrontAndBackOfPrevCard(String front, String back, @Nullable String context) {

    }

    @Override
    public void showOptions(boolean withHardBtn) {
        expandButtonsLayout(withHardBtn);
    }

    @Override
    public void hideOptions(boolean withHardBtn) {
        hideOptionButtonsWithAnimation(withHardBtn);
    }


    //Disable buttons to avoid animation errors
    @Override
    public void disableButtonsWhileAnimating() {
        btnShowBack.setClickable(false);
        btnEasy.setClickable(false);
        btnGood.setClickable(false);
        btnForgot.setClickable(false);
        btnHard.setClickable(false);
        btnToPrev.setClickable(false);
        btnForgotForHard.setClickable(false);
    }

    @Override
    public void enableButtonsAfterAnimation() {
        btnShowBack.setClickable(true);
        btnEasy.setClickable(true);
        btnGood.setClickable(true);
        btnForgot.setClickable(true);
        btnHard.setClickable(true);
        btnToPrev.setClickable(true);
        btnForgotForHard.setClickable(true);
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
                if (withHardBtn) {
                    layout_hard_mode_btns.setVisibility(View.VISIBLE);
                    btnForgot.setVisibility(View.GONE);
                } else {
                    btnForgot.setVisibility(View.VISIBLE);
                }
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
                    layout_hard_mode_btns.animate().yBy((btnHard.getHeight()) + ((ViewGroup.MarginLayoutParams) btnHard.getLayoutParams()).topMargin).setDuration(200);
                btnForgot.animate().yBy((btnForgot.getHeight()) + ((ViewGroup.MarginLayoutParams) btnForgot.getLayoutParams()).topMargin).setDuration(200)
                        .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        enableButtonsAfterAnimation();
                    }
                });
            }
        }, 100);
    }

    private void hideOptionButtonsWithAnimation(boolean withHardBtn) {
        btnEasy.animate().yBy((btnEasy.getHeight()) + ((ViewGroup.MarginLayoutParams) btnEasy.getLayoutParams()).topMargin).setDuration(200);
        if (withHardBtn)
            layout_hard_mode_btns.animate().yBy((btnHard.getHeight() * -1) - ((ViewGroup.MarginLayoutParams) btnHard.getLayoutParams()).topMargin).setDuration(200);
        btnForgot.animate().yBy((btnForgot.getHeight() * -1) - ((ViewGroup.MarginLayoutParams) btnForgot.getLayoutParams()).topMargin).setDuration(200)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        enableButtonsAfterAnimation();
                        btnEasy.setVisibility(View.GONE);
                        btnGood.setVisibility(View.GONE);
                        if (withHardBtn)
                            layout_hard_mode_btns.setVisibility(View.GONE);
                        else
                            btnForgot.setVisibility(View.GONE);
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
    @Override
    public void closeFragment() {
        ((MainActivity) getActivity()).showBottomNavigationBar();
        getFragmentManager().popBackStack();
    }

}
