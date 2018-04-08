package com.example.alexmelnikov.vocabra.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.alexmelnikov.vocabra.R;
import com.example.alexmelnikov.vocabra.model.Translation;
import com.example.alexmelnikov.vocabra.ui.translator.TranslatorPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by AlexMelnikov on 04.03.18.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private static final String TAG = "MyTag";

    Context mContext;
    ArrayList<Translation> mData;
    TranslatorPresenter fragmentPresenter;

    public HistoryAdapter(Context mContext, ArrayList<Translation> mData, TranslatorPresenter presenter) {
        this.mContext = mContext;
        this.mData = mData;
        this.fragmentPresenter = presenter;
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_front) TextView tvFrom;
        @BindView(R.id.tv_back) TextView tvTo;
        @BindView(R.id.ib_favourite) ImageButton btnFavourite;
        @BindView(R.id.card_history) CardView cardHistory;

        public HistoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HistoryViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_history, parent, false));
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        Translation translation = mData.get(mData.size() - position - 1);

        holder.tvFrom.setText(translation.getFromText());
        holder.tvTo.setText(translation.getToText());

        if (translation.getFavorite())
            holder.btnFavourite.setImageResource(R.drawable.ic_star_yellow_24dp);
        else
            holder.btnFavourite.setImageResource(R.drawable.ic_star_border_accent_24dp);

        holder.btnFavourite.setOnClickListener(view -> {
            if (translation.getFavorite()) {
                fragmentPresenter.dropFavoriteStatusRequest(mData.size() - position - 1);
            } else {
                fragmentPresenter.addNewCardFromHistoryRequest(mData.size() - position - 1);
            }
        });

        holder.cardHistory.setOnClickListener(view -> {
            if (!translation.getFavorite()) {
                fragmentPresenter.addNewCardFromHistoryRequest(mData.size() - position - 1);
            }
        });

        holder.cardHistory.setOnLongClickListener(view -> {
            fragmentPresenter.historyItemLongClicked(mData.size() - position - 1);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void replaceData(List<Translation> translations) {
        mData.clear();
        mData.addAll(translations);
        notifyDataSetChanged();
    }

    public void clear() {
        mData.clear();
        notifyDataSetChanged();
    }

    /**@param pos equals -1 in case when translation has been added to favourites with
     * imagebutton on translation card. That way we update last added to history translation */
    public void updateElement(int pos, Translation translation) {
        Log.d(TAG, "updateElement: " + pos);
        if (pos != -1) {
            for (int i = 0; i < mData.size(); i++)
                Log.d(TAG, "updateElement: " + i + ":" + mData.get(i).getFromText());
            mData.set(pos, translation);
            super.notifyItemChanged(mData.size() - 1 - pos);
        } else {
            mData.set(0, translation);
            super.notifyItemChanged(0);
        }
    }

}
