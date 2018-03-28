package com.example.alexmelnikov.vocabra.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.alexmelnikov.vocabra.R;
import com.example.alexmelnikov.vocabra.model.Deck;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by AlexMelnikov on 28.03.18.
 */

public class DecksForTrainingAdapter extends RecyclerView.Adapter<DecksForTrainingAdapter.DecksViewHolder> {


    private static final String TAG = "MyTag";

    Context mContext;
    ArrayList<Deck> mData;

    public DecksForTrainingAdapter(Context mContext, ArrayList<Deck> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    class DecksViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.card_deck)
        RelativeLayout cvDeck;
        @BindView(R.id.tv_deck_name)
        TextView tvDeckName;
        @BindView(R.id.btn_train)
        ImageButton btnTrain;

        public DecksViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
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
        holder.tvDeckName.setText(deck.getName());

        final Drawable drawable = mContext.getResources().getDrawable(R.drawable.bg_card);
        drawable.setColorFilter(deck.getColor(), PorterDuff.Mode.SRC_ATOP);
        holder.cvDeck.setBackground(drawable);
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

}
