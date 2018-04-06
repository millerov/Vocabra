package com.example.alexmelnikov.vocabra.ui.training;

import android.animation.Animator;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.renderscript.RenderScript;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.text.Layout;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.androidanimations.library.sliders.SlideInDownAnimator;
import com.daimajia.androidanimations.library.sliders.SlideOutLeftAnimator;
import com.daimajia.androidanimations.library.sliders.SlideOutRightAnimator;
import com.daimajia.androidanimations.library.sliders.SlideOutUpAnimator;
import com.example.alexmelnikov.vocabra.R;
import com.example.alexmelnikov.vocabra.VocabraApp;
import com.example.alexmelnikov.vocabra.adapter.DecksSpinnerAdapter;
import com.example.alexmelnikov.vocabra.data.DecksRepository;
import com.example.alexmelnikov.vocabra.model.Card;
import com.example.alexmelnikov.vocabra.model.Deck;
import com.example.alexmelnikov.vocabra.model.Language;
import com.example.alexmelnikov.vocabra.ui.BaseFragment;
import com.example.alexmelnikov.vocabra.ui.main.MainActivity;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;
import java.util.List;
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

    @BindView(R.id.btn_hard) RelativeLayout btnHard;
    @BindView(R.id.tv_good_info) TextView tvGoodInfo;
    @BindView(R.id.tv_easy_info) TextView tvEasyInfo;
    @BindView(R.id.tv_forgot_info) TextView tvForgotInfo;
    @BindView(R.id.tv_hard_info) TextView tvHardInfo;
    @BindView(R.id.sv_front_back) ScrollView svFrontBack;

    EditText etDialogFront;
    EditText etDialogBack;
    EditText etDialogContext;
    TextView tvSpinnerHint;
    Spinner mDialogSpinDecks;
    TextInputLayout mDialogTilFront;
    TextInputLayout mDialogTilBack;
    TextInputLayout mDialogTilContext;

    private int currentConfig;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_training, container, false);
        ButterKnife.bind(this, view);

        if (getActivity() != null)
            currentConfig = getActivity().getResources().getConfiguration().orientation;

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


        Disposable optionHardButton = RxView.clicks(btnHard)
                .subscribe(o -> {
                    mTrainingPresenter.optionHardPicked();
                });

        Disposable editButton = RxView.clicks(btnEdit)
                .subscribe(o -> mTrainingPresenter.editCardRequest(1));

        Disposable frontLayout = RxView.clicks(rlFront)
                .subscribe(o -> mTrainingPresenter.editCardRequest(2));

        Disposable backLayout = RxView.clicks(rlBack)
                .subscribe(o -> mTrainingPresenter.editCardRequest(3));


        mDisposable.addAll(backButton, prevButton, showBackButton, optionEasyButton, optionGoodButton,
                optionForgotButton, optionHardButton, editButton, backLayout);
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
    public void showFront(String front, boolean firstAttach) {
        ViewGroup.LayoutParams ls = svFrontBack.getLayoutParams();
        ls.height += TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
        svFrontBack.setLayoutParams(ls);

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
    public void showBack(String back, @Nullable String context) {
        ViewGroup.LayoutParams ls = svFrontBack.getLayoutParams();
        ls.height -= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());;
        svFrontBack.setLayoutParams(ls);

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
                svFrontBack.scrollTo(0, rlBack.getTop());
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
     //   btnForgotForHard.setClickable(false);
    }

    @Override
    public void enableButtonsAfterAnimation() {
        btnShowBack.setClickable(true);
        btnEasy.setClickable(true);
        btnGood.setClickable(true);
        btnForgot.setClickable(true);
        btnHard.setClickable(true);
        btnToPrev.setClickable(true);
     //   btnForgotForHard.setClickable(true);
    }


    private void expandButtonsLayout(boolean withHardBtn) {
        if (currentConfig == Configuration.ORIENTATION_PORTRAIT) {
            double reqViewHeightDouble = rlButtons.getHeight() * 2.7;
            int requiredViewHeight = (int) reqViewHeightDouble;
            ResizeHeightAnimation animation = new ResizeHeightAnimation(rlButtons, requiredViewHeight, rlButtons.getHeight());
            animation.setDuration(300);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationEnd(Animation animation) {

                    btnEasy.setVisibility(View.VISIBLE);
                    btnGood.setVisibility(View.VISIBLE);
                    btnForgot.setVisibility(View.VISIBLE);
                    if (withHardBtn)
                        btnHard.setVisibility(View.VISIBLE);
                    btnShowBack.setVisibility(View.GONE);


                    showOptionButtonsWithAnimation(withHardBtn);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationStart(Animation animation) {
                }
            });
            rlButtons.startAnimation(animation);
        } else {
            btnEasy.setVisibility(View.VISIBLE);
            btnGood.setVisibility(View.VISIBLE);
            btnForgot.setVisibility(View.VISIBLE);
            if (withHardBtn)
                btnHard.setVisibility(View.VISIBLE);
            btnShowBack.setVisibility(View.GONE);


            showOptionButtonsWithAnimation(withHardBtn);
        }
    }

    private void narrowButtonsLayout(boolean withHardBtn) {
        if (currentConfig == Configuration.ORIENTATION_PORTRAIT) {
            double reqViewHeightDouble = rlButtons.getHeight() / 2.7;
            int requiredViewHeight = (int) reqViewHeightDouble;
            ResizeHeightAnimation animation = new ResizeHeightAnimation(rlButtons, requiredViewHeight, rlButtons.getHeight());
            animation.setDuration(300);
            rlButtons.startAnimation(animation);
        }
    }

    private void showOptionButtonsWithAnimation(boolean withHardBtn) {
        if (currentConfig == Configuration.ORIENTATION_PORTRAIT) {
            btnEasy.postDelayed(new Runnable() {
                @Override
                public void run() {
                    btnEasy.animate().yBy((btnEasy.getHeight() * -1) - ((ViewGroup.MarginLayoutParams) btnEasy.getLayoutParams()).topMargin).setDuration(200);
                    if (withHardBtn)
                        btnHard.animate().yBy((btnHard.getHeight()) + ((ViewGroup.MarginLayoutParams) btnHard.getLayoutParams()).topMargin).setDuration(200);
                    btnForgot.animate().yBy((btnForgot.getHeight()) + ((ViewGroup.MarginLayoutParams) btnForgot.getLayoutParams()).topMargin).setDuration(200)
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    if (withHardBtn) {
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
                                                        enableButtonsAfterAnimation();
                                                    }
                                                }, 200);

                                            }
                                        }, 320);
                                    } else {
                                        enableButtonsAfterAnimation();
                                    }
                                }
                            });
                }
            }, 100);
        } else {
            enableButtonsAfterAnimation();
        }
    }

    private void hideOptionButtonsWithAnimation(boolean withHardBtn) {
        if (currentConfig == Configuration.ORIENTATION_PORTRAIT) {
            btnEasy.animate().yBy((btnEasy.getHeight()) + ((ViewGroup.MarginLayoutParams) btnEasy.getLayoutParams()).topMargin).setDuration(200);
            if (withHardBtn)
                btnHard.animate().yBy((btnForgot.getHeight() * -1) - ((ViewGroup.MarginLayoutParams) btnHard.getLayoutParams()).topMargin).setDuration(200);
            btnForgot.animate().yBy((btnForgot.getHeight() * -1) - ((ViewGroup.MarginLayoutParams) btnForgot.getLayoutParams()).topMargin).setDuration(200)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            if (withHardBtn) {
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
                                                enableButtonsAfterAnimation();
                                            }
                                        }, 30);
                                    }
                                }, 30);
                            } else {
                                enableButtonsAfterAnimation();
                            }
                            btnEasy.setVisibility(View.GONE);
                            btnGood.setVisibility(View.GONE);
                            btnForgot.setVisibility(View.GONE);
                            if (withHardBtn)
                                btnHard.setVisibility(View.GONE);
                            btnShowBack.setVisibility(View.VISIBLE);
                            narrowButtonsLayout(withHardBtn);
                        }
                    });
        } else {
            enableButtonsAfterAnimation();
            btnEasy.setVisibility(View.GONE);
            btnGood.setVisibility(View.GONE);
            btnForgot.setVisibility(View.GONE);
            if (withHardBtn)
                btnHard.setVisibility(View.GONE);
            btnShowBack.setVisibility(View.VISIBLE);
            narrowButtonsLayout(withHardBtn);
        }
    }


    @Override
    public void showEditCardDialog(int methodIndex, Card card, ArrayList<Deck> decks) {
        MaterialDialog dialog =
                new MaterialDialog.Builder(getActivity())
                        .title("Изменение карточки")
                        .customView(R.layout.dialog_add_card, true)
                        .positiveText("Сохранить")
                        .negativeText(android.R.string.cancel)
                        .autoDismiss(false)
                        .onNegative(((dialog1, which) -> {
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                            dialog1.dismiss();
                        }))
                        .onPositive((dialog1, which) -> {
                            if (etDialogFront.getText().toString().trim().isEmpty())
                                mDialogTilFront.setError("Введите слово или фразу");
                            if (etDialogBack.getText().toString().trim().isEmpty())
                                mDialogTilBack.setError("Введите слово или фразу");

                            if (!etDialogFront.getText().toString().trim().isEmpty() && !etDialogBack.getText().toString().trim().isEmpty()) {
                                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                                mTrainingPresenter.editCardRequest(etDialogFront.getText().toString(), etDialogBack.getText().toString(),
                                        etDialogContext.getText().toString());
                                dialog1.dismiss();
                            }
                        })
                        .build();

        etDialogFront = (EditText) dialog.getView().findViewById(R.id.et_front);
        etDialogBack = (EditText) dialog.getView().findViewById(R.id.et_back);
        etDialogContext = (EditText) dialog.getView().findViewById(R.id.et_context);
        tvSpinnerHint = (TextView) dialog.getView().findViewById(R.id.tv_spinner_hint);
        mDialogSpinDecks = (Spinner) dialog.getView().findViewById(R.id.spin_decks);
        mDialogTilFront = (TextInputLayout) dialog.getView().findViewById(R.id.input_layout_front);
        mDialogTilBack = (TextInputLayout) dialog.getView().findViewById(R.id.input_layout_back);
        mDialogTilContext = (TextInputLayout) dialog.getView().findViewById(R.id.input_layout_context);

        tvSpinnerHint.setVisibility(View.GONE);
        mDialogSpinDecks.setVisibility(View.GONE);

        etDialogFront.setInputType(InputType.TYPE_CLASS_TEXT);
        etDialogBack.setInputType(InputType.TYPE_CLASS_TEXT);
        etDialogContext.setInputType(InputType.TYPE_CLASS_TEXT);
        etDialogFront.setText(card.getFront());
        etDialogBack.setText(card.getBack());
        if (!card.getCardContext().isEmpty())
            etDialogContext.setText(card.getCardContext());
        mDialogTilFront.setHint("Передняя сторона");
        mDialogTilBack.setHint("Задняя сторона");
        if (card.getCardContext().isEmpty())
            mDialogTilContext.setError("Контекст поможет новому слову лучше отложиться в памяти");
        if (methodIndex == 3)
            etDialogBack.requestFocus();
        else
            etDialogFront.requestFocus();

        etDialogFront.setSelection(etDialogFront.getText().length());
        etDialogBack.setSelection(etDialogBack.getText().length());
        mDialogSpinDecks.setAdapter(new DecksSpinnerAdapter(getActivity(), decks, false));

        int index = 0;
        for (int i = 0; i < mDialogSpinDecks.getCount(); i++) {
            if (mDialogSpinDecks.getItemAtPosition(i).toString().equals(card.getDeck().getName())) {
                index = i;
                break;
            }
        }
        mDialogSpinDecks.setSelection(index);

        dialog.show();
    }

    @Override
    public void fillEditedCardTextViews(String front, String back, String context) {
        tvFront.setText(front);
        tvBack.setText(back);
        tvContext.setText(context);
        if (context.isEmpty()) {
            layoutCounters.setVisibility(View.VISIBLE);
            tvDeckName.setVisibility(View.VISIBLE);
            tvContext.setVisibility(View.GONE);
        } else if (rlBack.getVisibility() == View.VISIBLE &&
                tvContext.getVisibility() == View.GONE) {
            layoutCounters.setVisibility(View.GONE);
            tvDeckName.setVisibility(View.GONE);
            tvContext.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void showCardAlreadyExistsMessage(String deckName) {
        ((MainActivity)getActivity()).showMessage(0, "Такая карточка уже существует в колоде " + deckName, false, null, null);
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
