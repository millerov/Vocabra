package com.example.alexmelnikov.vocabra.ui.statistics;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.alexmelnikov.vocabra.VocabraApp;
import com.example.alexmelnikov.vocabra.data.StatisticsRepository;
import com.example.alexmelnikov.vocabra.model.DailyStats;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

/**
 * Created by AlexMelnikov on 06.04.18.
 */

@InjectViewState
public class StatisticsPresenter extends MvpPresenter<StatisticsView> {

    @Inject
    StatisticsRepository mStatsRep;

    private ArrayList<DailyStats> stats;
    private HashMap<String, Integer> valuesForChart;

    public StatisticsPresenter() {
        valuesForChart = new HashMap<String, Integer>();
    }

    @Override
    public void attachView(StatisticsView view) {
        VocabraApp.getPresenterComponent().inject(this);
        super.attachView(view);
        getViewState().attachInputListeners();

        setupStatisticsFromDb();
    }

    @Override
    public void detachView(StatisticsView view) {
        super.detachView(view);
        getViewState().detachInputListeners();
    }

    private void setupStatisticsFromDb() {
        stats = mStatsRep.getStatisticsFromDB();
        for (DailyStats s : stats) {
            valuesForChart.put(s.getStringDate(), s.getCardsTrained());
        }
        getViewState().setupChartData(valuesForChart);
    }
}
