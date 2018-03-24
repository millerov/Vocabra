package com.example.alexmelnikov.vocabra.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.alexmelnikov.vocabra.R;
import com.example.alexmelnikov.vocabra.model.CardSortMethod;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by AlexMelnikov on 24.03.18.
 */

public class SortMethodsDialogAdapter extends RecyclerView.Adapter<SortMethodsDialogAdapter.SortMethodsViewHolder> {

    private static final String TAG = "MyTag";

    Context mContext;
    ArrayList<CardSortMethod> mData;
    CardSortMethod selectedMethod;
    int selectedIndex;

    public SortMethodsDialogAdapter(Context mContext, ArrayList<CardSortMethod> mData,
                                    CardSortMethod selectedMethod, int selectedIndex) {
        this.mContext = mContext;
        this.mData = mData;
        this.selectedMethod = selectedMethod;
        this.selectedIndex = selectedIndex;
    }

    class SortMethodsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rb_status)
        RadioButton rbStatus;
        @BindView(R.id.method_name)
        TextView tvMethodName;
        @BindView(R.id.iv_vector)
        ImageView ivVector;

        public SortMethodsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public SortMethodsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SortMethodsViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.dialog_item_sort_method, parent, false));
    }

    @Override
    public void onBindViewHolder(SortMethodsViewHolder holder, int position) {
        CardSortMethod method = mData.get(position);
        holder.tvMethodName.setText(method.getName());
        if (position == selectedIndex) {
            Log.d(TAG, "onBindViewHolder: " + method.getName());
            Log.d(TAG, "onBindViewHolder: " + selectedMethod.getName());
            holder.rbStatus.toggle();
            if (selectedMethod.isAscending()) {
                holder.ivVector.setBackground(mContext.getResources().getDrawable(R.drawable.ic_arrow_upward_black_24dp));
            } else {
                holder.ivVector.setBackground(mContext.getResources().getDrawable(R.drawable.ic_arrow_downward_black_24dp));
            }
        } else {
            holder.ivVector.setBackground(mContext.getResources().getDrawable(R.drawable.ic_arrow_downward_black_24dp));
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

}
