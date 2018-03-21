package com.example.alexmelnikov.vocabra.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alexmelnikov.vocabra.R;
import com.example.alexmelnikov.vocabra.model.Card;
import com.example.alexmelnikov.vocabra.ui.cardbrowser.CardBrowserPresenter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by AlexMelnikov on 10.03.18.
 */

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.CardsViewHolder> {

    private static final String TAG = "MyTag";

    Context mContext;
    ArrayList<Card> mData;
    CardBrowserPresenter presenter;


    public CardsAdapter(Context mContext, ArrayList<Card> mData, CardBrowserPresenter presenter) {
        this.mContext = mContext;
        this.mData = mData;
        this.presenter = presenter;
    }

    class CardsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_color)
        ImageView ivColor;
        @BindView(R.id.tv_front)
        TextView tvFront;
        @BindView(R.id.tv_back)
        TextView tvBack;

        public CardsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    @Override
    public CardsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CardsViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_card_list, parent, false));
    }

    @Override
    public void onBindViewHolder(CardsViewHolder holder, int position) {
        Card card = mData.get(mData.size() - position - 1);

        holder.tvFront.setText(card.getFront());
        holder.tvBack.setText(card.getBack());

        final Drawable drawable = mContext.getResources().getDrawable(R.drawable.bg_color);
        drawable.setColorFilter(card.getDeck().getColor(), PorterDuff.Mode.SRC_ATOP);
        holder.ivColor.setBackground(drawable);

        holder.itemView.setOnClickListener(view -> presenter.cardsRecyclerItemPressed(mData.size() - position - 1));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void replaceData(ArrayList<Card> cards) {
        mData.clear();
        mData.addAll(cards);
        notifyDataSetChanged();
    }
}
