package com.example.alexmelnikov.vocabra.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.example.alexmelnikov.vocabra.R;
import com.example.alexmelnikov.vocabra.data.DecksRepository;
import com.example.alexmelnikov.vocabra.model.Deck;
import com.example.alexmelnikov.vocabra.model.Language;

import java.util.ArrayList;

/**
 * Created by AlexMelnikov on 16.03.18.
 */

public class DecksSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

    private static final String TAG = "MyTag";

    private Context mContext;
    private ArrayList<Deck> mData;

    public DecksSpinnerAdapter(Context context, ArrayList<Deck> data){
        data.add(null);
        this.mContext = context;
        this.mData = data;
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return mData.get(mData.size() - i - 1).getName();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_deck, null);
        }
        ImageView ivColor = (ImageView) view.findViewById(R.id.iv_color);
        TextView textView = (TextView) view.findViewById(R.id.line_one);

        Deck current = mData.get(mData.size() - i - 1);

        if (current != null) {

            ivColor.setVisibility(View.VISIBLE);

            if (getItem(i).toString().length() < 22) {
                textView.setText(getItem(i).toString());
            } else {
                String text = getItem(i).toString().substring(0, 21) + "...      ";
                textView.setText(text);
            }
            textView.setGravity(Gravity.LEFT);
            textView.setMaxLines(1);


            final Drawable drawable = mContext.getResources().getDrawable(R.drawable.bg_color);
            drawable.setColorFilter(current.getColor(), PorterDuff.Mode.SRC_ATOP);
            ivColor.setBackground(drawable);

        } else {
            textView.setText("По умолчанию");
            ivColor.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);

        RelativeLayout li = (RelativeLayout) view;
        TextView tv = (TextView) li.findViewById(R.id.line_one);
        tv.setGravity(Gravity.LEFT);
        tv.setTextColor(Color.parseColor("#000000"));

        /*DisplayMetrics metrics = parent.getResources().getDisplayMetrics();
        tv.setWidth((int) ((metrics.density * 00f) + 0.5f));*/

        return view;
    }

}
