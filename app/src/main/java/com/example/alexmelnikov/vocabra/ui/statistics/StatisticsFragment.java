package com.example.alexmelnikov.vocabra.ui.statistics;

import android.graphics.Color;
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
import com.jakewharton.rxbinding2.view.RxView;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by AlexMelnikov on 06.04.18.
 */

public class StatisticsFragment extends BaseFragment implements StatisticsView {

    private static final String TAG = "MyTag";

    @InjectPresenter
    StatisticsPresenter mStatisticsPresenter;

    @BindView(R.id.btn_back) ImageButton btnBack;
    @BindView(R.id.chart) LineChartView chart;

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

    @Override
    public void setupChartData(HashMap<String, Integer> stats) {

        List<PointValue> values = new ArrayList<PointValue>();


        for (Map.Entry<String, Integer> cord : stats.entrySet()) {
            Log.d(TAG, "setupChartData: value=" + cord.getValue());
            values.add(new PointValue(cord.getValue(), cord.getValue()));
        }

        Line line = new Line(values);
        line.setColor(getActivity().getResources().getColor(R.color.colorPrimary));
        line.setCubic(true);
        line.setFilled(true);

        ArrayList<Line> lines = new ArrayList<Line>();
        lines.add(line);

        LineChartData data = new LineChartData(lines);

        Axis axisX = new Axis();
        Axis axisY = new Axis().setHasLines(true);
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);

        chart.setLineChartData(data);


        //MapHelper.getKeyFromValue(values, value)


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
