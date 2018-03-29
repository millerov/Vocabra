package com.example.alexmelnikov.vocabra.ui.training;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.alexmelnikov.vocabra.R;
import com.example.alexmelnikov.vocabra.model.Deck;
import com.example.alexmelnikov.vocabra.ui.BaseFragment;
import com.example.alexmelnikov.vocabra.ui.main.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by AlexMelnikov on 29.03.18.
 */


public class TrainingFragment extends BaseFragment implements TrainingView {

    private static final String TAG = "MyTag";

    @InjectPresenter
    TrainingPresenter mTrainingPresenter;

    @BindView(R.id.rl_deck)
    RelativeLayout rlDeck;
    @BindView(R.id.tv_deck_name)
    TextView tvDeckName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_training, container, false);
        ButterKnife.bind(this, view);

        Bundle args = getArguments();
        if (args != null) {
            String transitionName = args.getString("transitionName");
            Deck deck = (Deck) args.getSerializable("deck");
            rlDeck.setTransitionName(transitionName);

            if (deck != null) {
                tvDeckName.setText(deck.getName());

                final Drawable drawable = getActivity().getResources().getDrawable(R.drawable.bg_card);
                drawable.setColorFilter(deck.getColor(), PorterDuff.Mode.SRC_ATOP);
                rlDeck.setBackground(drawable);
            }

        } else {
            Log.d(TAG, "onCreateView: null args");
        }

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void attachInputListeners() {
        
    }

    @Override
    public void detachInputListeners() {

    }

    @Override
    public boolean onBackPressed() {
        ((MainActivity) getActivity()).showBottomNavigationBar();
        return super.onBackPressed();
    }
}
