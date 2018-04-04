package com.example.alexmelnikov.vocabra.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.alexmelnikov.vocabra.R;
import com.example.alexmelnikov.vocabra.VocabraApp;
import com.example.alexmelnikov.vocabra.data.CardsRepository;
import com.example.alexmelnikov.vocabra.model.Card;
import com.example.alexmelnikov.vocabra.model.Deck;
import com.example.alexmelnikov.vocabra.ui.decks_for_train.DecksForTrainingPresenter;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by AlexMelnikov on 28.03.18.
 */

public class DecksForTrainingAdapter extends RecyclerView.Adapter<DecksForTrainingAdapter.DecksViewHolder> {


    private static final String TAG = "MyTag";

    Context mContext;
    ArrayList<Deck> mData;
    DecksForTrainingPresenter presenter;
    int totalCounter;

    @Inject
    CardsRepository mCardsRep;

    public DecksForTrainingAdapter(Context mContext, ArrayList<Deck> mData, DecksForTrainingPresenter presenter) {
        VocabraApp.getAppComponent().inject(this);
        this.mContext = mContext;
        this.mData = mData;
        this.presenter = presenter;
        totalCounter = 0;
    }

    class DecksViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.card_deck)
        RelativeLayout rlDeck;
        @BindView(R.id.tv_deck_name)
        TextView tvDeckName;
        @BindView(R.id.btn_train)
        ImageButton btnTrain;
        @BindView(R.id.tv_new_counter)
        TextView tvNewCouner;
        @BindView(R.id.tv_ready_counter)
        TextView tvReadyCounter;


        public DecksViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            rlDeck.setOnClickListener(view -> presenter.deckSelectRequestFromRecycler(mData.get(mData.size() - getAdapterPosition() - 1), rlDeck, rlDeck.getTransitionName()));
            btnTrain.setOnClickListener(view -> presenter.deckSelectRequestFromRecycler(mData.get(mData.size() - getAdapterPosition() - 1), rlDeck, rlDeck.getTransitionName()));
        }
    }

    @Override
    public DecksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DecksViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_deck_for_training, parent, false));
    }

    @Override
    public void onBindViewHolder(DecksViewHolder holder, int position) {
        Deck deck = mData.get(mData.size() - position - 1);
        ArrayList<Card> newCards = mCardsRep.getNewCardsByDeckDB(deck);
        ArrayList<Card> readyForTrainOldCards = mCardsRep.getOldReadyForTrainCardsByDeckDB(deck);
        holder.tvDeckName.setText(deck.getName());
        holder.rlDeck.setTransitionName("transitionView" + position);
        holder.tvNewCouner.setText(newCards.size() + "");
        holder.tvReadyCounter.setText(readyForTrainOldCards.size() + "");
        totalCounter += newCards.size();
        totalCounter += readyForTrainOldCards.size();



        final Drawable drawable = mContext.getResources().getDrawable(R.drawable.bg_card);
        drawable.setColorFilter(deck.getColor(), PorterDuff.Mode.SRC_ATOP);
        holder.rlDeck.setBackground(drawable);

        if (position == mData.size() - 1)
            presenter.setupCounterRequest(totalCounter);


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public long getItemId(int position) {
        return mData.get(mData.size() - position - 1).hashCode();
    }

    public void replaceData(ArrayList<Deck> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public int getTotalCounter() {
        return totalCounter;
    }

}
