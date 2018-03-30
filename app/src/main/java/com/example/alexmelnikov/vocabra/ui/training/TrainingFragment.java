package com.example.alexmelnikov.vocabra.ui.training;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.androidanimations.library.sliders.SlideInDownAnimator;
import com.example.alexmelnikov.vocabra.R;
import com.example.alexmelnikov.vocabra.VocabraApp;
import com.example.alexmelnikov.vocabra.data.DecksRepository;
import com.example.alexmelnikov.vocabra.model.Deck;
import com.example.alexmelnikov.vocabra.ui.BaseFragment;
import com.example.alexmelnikov.vocabra.ui.main.MainActivity;
import com.jakewharton.rxbinding2.view.RxView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    @BindView(R.id.layout_buttons) RelativeLayout rlButtons;
    @BindView(R.id.btn_back) ImageButton btnBack;
    @BindView(R.id.btn_more) ImageButton btnMore;
    @BindView(R.id.btn_show_back) Button btnShowBack;
    @BindView(R.id.btn_easy) Button btnEasy;
    @BindView(R.id.btn_good) Button btnGood;
    @BindView(R.id.btn_forgot) Button btnForgot;

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
            Log.d(TAG, "onCreateView: null args");
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
                .subscribe(o -> mTrainingPresenter.showBackRequest());

        mDisposable.addAll(backButton, moreButton, showBackButton);
    }

    @Override
    public void detachInputListeners() {
        mDisposable.clear();
    }

    @Override
    public void showFrontView(boolean firstAttach) {
        if (firstAttach) {
            rlFront.postDelayed(new Runnable() {
                @Override
                public void run() {
                    YoYo.with(new SlideInDownAnimator())
                            .interpolate(new AccelerateDecelerateInterpolator())
                            .duration(500)
                            .playOn(rlFront);
                    rlFront.setVisibility(View.VISIBLE);
                }
            }, 270);
        } else {
            rlFront.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void showBackView() {
                YoYo.with(new SlideInDownAnimator())
                        .interpolate(new AccelerateDecelerateInterpolator())
                        .duration(500)
                        .playOn(rlBack);
                rlBack.setVisibility(View.VISIBLE);
    }

    @Override
    public void expandButtonsLayout() {
        double reqViewHeightDouble = rlButtons.getHeight() * 2.7;
        int requiredViewHeight = (int) reqViewHeightDouble;
        ResizeAnimation animation = new ResizeAnimation(rlButtons, requiredViewHeight, rlButtons.getHeight());
        animation.setDuration(300);
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationEnd(Animation animation) {
                btnShowBack.setVisibility(View.GONE);
                btnEasy.setVisibility(View.VISIBLE);
                btnGood.setVisibility(View.VISIBLE);
                btnForgot.setVisibility(View.VISIBLE);
                showButtonsWithAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationStart(Animation animation) {}

        });
        rlButtons.startAnimation(animation);

    }

    @Override
    public void narrowButtonLayout() {

    }

    private void showButtonsWithAnimation() {
        btnEasy.postDelayed(new Runnable() {
            @Override
            public void run() {
                btnEasy.animate().yBy((btnEasy.getHeight() * -1) - ((ViewGroup.MarginLayoutParams) btnEasy.getLayoutParams()).topMargin).setDuration(200);
                btnForgot.animate().yBy((btnForgot.getHeight()) + ((ViewGroup.MarginLayoutParams) btnForgot.getLayoutParams()).topMargin).setDuration(200);
            }
        }, 100);
    }





    @Override
    public boolean onBackPressed() {
        ((MainActivity) getActivity()).showBottomNavigationBar();
        return super.onBackPressed();
    }

    private void closeFragment() {
        ((MainActivity) getActivity()).showBottomNavigationBar();
        getFragmentManager().popBackStack();
    }

}
