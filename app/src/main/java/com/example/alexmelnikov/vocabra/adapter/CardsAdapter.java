package com.example.alexmelnikov.vocabra.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alexmelnikov.vocabra.R;
import com.example.alexmelnikov.vocabra.model.Card;
import com.example.alexmelnikov.vocabra.ui.cardbrowser.CardBrowserPresenter;

import java.util.ArrayList;
import java.util.Arrays;

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

    boolean selectMode;
    boolean[] selectedItems;


    public CardsAdapter(Context mContext, ArrayList<Card> mData, CardBrowserPresenter presenter) {
        this.mContext = mContext;
        this.mData = mData;
        this.presenter = presenter;
        this.selectMode = false;
    }

    class CardsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_color)
        ImageView ivColor;
        @BindView(R.id.tv_front)
        TextView tvFront;
        @BindView(R.id.tv_back)
        TextView tvBack;
        @BindView(R.id.cb_select)
        CheckBox cbSelect;

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

        if (selectMode) {
            holder.cbSelect.setVisibility(View.VISIBLE);

            if (selectedItems[position])
                holder.cbSelect.setChecked(true);
            else
                holder.cbSelect.setChecked(false);

            holder.cbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                        selectedItems[position] = true;
                    else
                        selectedItems[position] = false;
                }
            });
        } else {
            holder.cbSelect.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(view -> {
            if (!selectMode)
                presenter.cardsRecyclerItemPressed(mData.size() - position - 1);
        });
        holder.itemView.setOnLongClickListener(view -> {
            if (!selectMode)
                presenter.cardsRecyclerItemLongPressed(mData.size() - position - 1);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public long getItemId(int position) {
        return mData.get(mData.size() - position - 1).hashCode();
    }

    public void replaceData(ArrayList<Card> cards) {
        mData.clear();
        mData.addAll(cards);
        selectedItems = new boolean[mData.size()];
        Arrays.fill(selectedItems, false);
        notifyDataSetChanged();
    }

    public void enableSelectMode() {
        selectMode = true;
        notifyDataSetChanged();
    }

    public void disableSelectMode() {
        selectMode = false;
        notifyDataSetChanged();
    }

    public void selectAllItems() {
        Arrays.fill(selectedItems, true);
        notifyDataSetChanged();
    }

    public void unselectAllItems() {
        Arrays.fill(selectedItems, false);
        notifyDataSetChanged();
    }

    public boolean[] getSelectedItemsIndexes() {
        return selectedItems;
    }
}
