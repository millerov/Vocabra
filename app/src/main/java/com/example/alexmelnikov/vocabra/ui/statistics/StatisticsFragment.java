package com.example.alexmelnikov.vocabra.ui.statistics;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.alexmelnikov.vocabra.R;
import com.example.alexmelnikov.vocabra.ui.BaseFragment;
import com.example.alexmelnikov.vocabra.ui.main.MainActivity;
import com.example.alexmelnikov.vocabra.utils.MapHelper;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.jakewharton.rxbinding2.view.RxView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;

/**
 * Created by AlexMelnikov on 06.04.18.
 */

public class StatisticsFragment extends BaseFragment implements StatisticsView {

    private static final String TAG = "MyTag";

    @InjectPresenter
    StatisticsPresenter mStatisticsPresenter;

    @BindView(R.id.btn_back) ImageButton btnBack;
    @BindView(R.id.chart) LineChart chart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        ButterKnife.bind(this, view);

        ((MainActivity) getActivity()).hideBottomNavigationBar();

        return view;
    }


    @Override
    public void attachInputListeners() {
        Disposable backButton = RxView.clicks(btnBack)
                .subscribe(o -> closeFragment());

        mDisposable.addAll(backButton);
    }

    @Override
    public void detachInputListeners() {
        mDisposable.clear();
    }

    public void setupChartData(HashMap<String, Integer> values) {

        List<Entry> entries = new ArrayList<Entry>();

        for (Map.Entry<String, Integer> cord : values.entrySet()) {
            entries.add(new Entry(cord.getValue(), cord.getValue()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "TimesTrained");
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        LineData data = new LineData(dataSet);


        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Log.d(TAG, "getFormattedValue: " + value);
                return MapHelper.getKeyFromValue(values, value) + "";
            }
        });

        chart.setData(data);
        chart.invalidate();

    }


    @Override
    public boolean onBackPressed() {
        ((MainActivity) getActivity()).showBottomNavigationBar();
        return super.onBackPressed();
    }

    //Used on left upper corner back ImageButton click
    @Override
    public void closeFragment() {
        ((MainActivity) getActivity()).showBottomNavigationBar();
        getFragmentManager().popBackStack();
    }
}
