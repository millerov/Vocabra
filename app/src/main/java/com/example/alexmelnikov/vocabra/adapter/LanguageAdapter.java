package com.example.alexmelnikov.vocabra.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.example.alexmelnikov.vocabra.R;
import com.example.alexmelnikov.vocabra.model.Language;

import java.util.ArrayList;

/**
 * Created by AlexMelnikov on 02.03.18.
 */

public class LanguageAdapter extends BaseAdapter implements SpinnerAdapter {

    private Context mContext;
    private ArrayList<Language> mData;

    public LanguageAdapter(Context context, ArrayList<Language> data){
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i).getLang();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_language, null);
        }
        TextView textView = (TextView) view.findViewById(R.id.line_one);
        textView.setText(getItem(i).toString());
        textView.setGravity(Gravity.CENTER);
        textView.setMaxLines(1);
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);

        LinearLayout li = (LinearLayout) view;
        TextView tv = (TextView) li.findViewById(R.id.line_one);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(Color.parseColor("#000000"));

        return view;
    }
}
