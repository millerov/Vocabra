package com.example.alexmelnikov.vocabra.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alexmelnikov.vocabra.R;
import com.example.alexmelnikov.vocabra.model.Deck;
import com.example.alexmelnikov.vocabra.ui.cardbrowser.CardBrowserPresenter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by AlexMelnikov on 14.03.18.
 */

public class DecksDialogAdapter extends RecyclerView.Adapter<DecksDialogAdapter.DecksViewHolder> {

    private static final String TAG = "MyTag";
    
    Context mContext;
    ArrayList<Deck> mData;
    CardBrowserPresenter presenter;

    public DecksDialogAdapter(Context mContext, ArrayList<Deck> mData, CardBrowserPresenter presenter) {
        this.mContext = mContext;
        this.mData = mData;
        this.presenter = presenter;
    }

    class DecksViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_color) ImageView ivColor;
        @BindView(R.id.tv_deck_name) TextView tvDeckName;
        @BindView(R.id.tv_langs) TextView tvLanguages;

        public DecksViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "DecksViewHolder: ");
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public DecksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        return new DecksViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.dialog_item_deck, parent, false));
    }

    @Override
    public void onBindViewHolder(DecksViewHolder holder, int position) {
        Deck deck = mData.get(mData.size() - position - 1);

        final Drawable drawable = mContext.getResources().getDrawable(R.drawable.bg_color);
        drawable.setColorFilter(deck.getColor(), PorterDuff.Mode.SRC_ATOP);
        holder.ivColor.setBackground(drawable);
        if (deck.getName().length() < 14)
            holder.tvDeckName.setText(deck.getName());
        else
            holder.tvDeckName.setText(deck.getName().substring(0, 14) + "...");

        holder.tvLanguages.setText(deck.getFirstLanguage().getId() + "-" + deck.getSecondLanguage().getId());

        holder.itemView.setOnClickListener(view -> presenter.decksDialogRecyclerItemPressed(mData.size() - position - 1));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

}
