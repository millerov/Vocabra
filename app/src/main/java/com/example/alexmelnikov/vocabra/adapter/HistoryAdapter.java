package com.example.alexmelnikov.vocabra.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.alexmelnikov.vocabra.R;
import com.example.alexmelnikov.vocabra.model.Translation;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by AlexMelnikov on 04.03.18.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    Context mContext;
    ArrayList<Translation> mData;

    public HistoryAdapter(Context mContext, ArrayList<Translation> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_From)
        TextView tvFrom;
        @BindView(R.id.tv_To)
        TextView tvTo;
        @BindView(R.id.ib_favourite)
        ImageButton btnFavorite;

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
        Log.d("adapter", "position: " + position);
        Translation translation = mData.get(mData.size()-position-1);
        if (translation.getFromText().length() > 29 || translation.getToText().length() > 29) {
            holder.tvFrom.setText(translation.getFromText().substring(0,30).trim() + "...");
            holder.tvTo.setText(translation.getToText().substring(0,30).trim() + "...");
        } else {
            holder.tvFrom.setText(translation.getFromText());
            holder.tvTo.setText(translation.getToText());
        }
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
}
