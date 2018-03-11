package com.example.alexmelnikov.vocabra.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alexmelnikov.vocabra.R;
import com.example.alexmelnikov.vocabra.model.Card;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by AlexMelnikov on 10.03.18.
 */

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.CardsViewHolder> {

    Context mContext;
    ArrayList<Card> mData;


    public CardsAdapter(Context mContext, ArrayList<Card> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    class CardsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_color)
        ImageView ivColor;
        @BindView(R.id.tv_From)
        TextView tvFrom;
        @BindView(R.id.tv_To)
        TextView tvTo;

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
