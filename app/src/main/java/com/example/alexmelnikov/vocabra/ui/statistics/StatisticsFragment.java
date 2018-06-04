package com.example.alexmelnikov.vocabra.ui.statistics;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.alexmelnikov.vocabra.R;
import com.example.alexmelnikov.vocabra.model.DailyStats;
import com.example.alexmelnikov.vocabra.ui.BaseFragment;
import com.example.alexmelnikov.vocabra.ui.main.MainActivity;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * StatisticsFragment.java – fragment displaying taining statistics
 * @author Alexander Melnikov
 */

public class StatisticsFragment extends BaseFragment implements StatisticsView {

    private static final String TAG = "MyTag";

    @InjectPresenter
    StatisticsPresenter mStatisticsPresenter;

    @BindView(R.id.btn_back) ImageButton btnBack;
    @BindView(R.id.chart) LineChartView chart;

    @BindView(R.id.tv_total_cards) TextView tvTotalCards;
    @BindView(R.id.tv_total_repeats) TextView tvTotalRepeats;
    @BindView(R.id.tv_days) TextView tvDays;
    @BindView(R.id.tv_average_per_day) TextView tvRepeatsPerDay;
    @BindView(R.id.tv_average_interval) TextView tvAvgInterval;
    @BindView(R.id.tv_longest_interval) TextView tvMaxInterval;

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
    public void setupChartData(ArrayList<DailyStats> stats) {

        List<PointValue> values = new ArrayList<PointValue>();

        List<AxisValue> axisValues = new ArrayList<AxisValue>();

        for (int i = 0; i < stats.size(); i++) {
            Log.d(TAG, "setupChartData: date=" + stats.get(i).getStringDate());
            String date = stats.get(i).getStringDate();
            String[] parsedDate = date.split("-");

            axisValues.add(new AxisValue(i).setLabel(parsedDate[2] + "." + parsedDate[1]));

            values.add(new PointValue(i, stats.get(i).getCardsTrained()));
        }



        Line line = new Line(values);
        line.setColor(getActivity().getResources().getColor(R.color.colorPrimary))
            .setCubic(true)
            .setFilled(true)
            .setHasLabels(true);

        ArrayList<Line> lines = new ArrayList<Line>();
        lines.add(line);

        LineChartData data = new LineChartData(lines);

        data.setAxisXBottom(new Axis(axisValues).setHasLines(true));

        chart.setLineChartData(data);
        Viewport v = new Viewport(chart.getMaximumViewport());
        v.right = v.right + 0.2f;
        v.left = v.left - 0.2f;
        v.top = v.top + 0.05f;
        chart.setMaximumViewport(v);
        chart.setViewportCalculationEnabled(false);

        v = new Viewport(chart.getMaximumViewport());
        v.left = v.right - 6.30f;
        chart.setCurrentViewport(v);
        chart.setZoomType(ZoomType.VERTICAL);
    }

    public void setupStatisticsData(String totalCards, String totalRepeats, String days,
                                    String repeatsPerDay, String avgInterval, String maxInterval) {
        tvTotalCards.setText(totalCards);
        tvTotalRepeats.setText(totalRepeats);
        tvDays.setText(days);
        tvRepeatsPerDay.setText(repeatsPerDay);
        tvAvgInterval.setText(avgInterval);
        tvMaxInterval.setText(maxInterval);
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
