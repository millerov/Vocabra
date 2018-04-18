package com.example.alexmelnikov.vocabra.ui.statistics;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.alexmelnikov.vocabra.VocabraApp;
import com.example.alexmelnikov.vocabra.data.CardsRepository;
import com.example.alexmelnikov.vocabra.data.StatisticsRepository;
import com.example.alexmelnikov.vocabra.data.UserDataRepository;
import com.example.alexmelnikov.vocabra.model.Card;
import com.example.alexmelnikov.vocabra.model.DailyStats;
import com.example.alexmelnikov.vocabra.utils.CardUtils;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.text.DecimalFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.inject.Inject;

/**
 * Created by AlexMelnikov on 06.04.18.
 */

@InjectViewState
public class StatisticsPresenter extends MvpPresenter<StatisticsView> {

    private static final String TAG = "MyTag";

    @Inject
    StatisticsRepository mStatsRep;
    @Inject
    UserDataRepository mUserData;
    @Inject
    CardsRepository mCardsRep;

    private ArrayList<DailyStats> stats;

    public StatisticsPresenter() {
        VocabraApp.getPresenterComponent().inject(this);

        mStatsRep.fillStatisticsUpToDate(new DateTime((Date)mUserData.getValue(mUserData.FIRST_APP_LAUNCH_DATE, new Date())));
    }

    @Override
    public void attachView(StatisticsView view) {
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
        ArrayList<Card> cards = mCardsRep.getCardsFromDB();

        int totalCards = cards.size();
        int totalRepeats = 0;
        int sumIntervals = 0;
        int maxInterval = 0;

        for (DailyStats ds : stats)
            totalRepeats += ds.getCardsTrained();

        for (Card c : cards) {
            int inc = c.getLastIncrement();
            sumIntervals += inc;
            if (inc > maxInterval)
                maxInterval = inc;
        }

        int allDays = stats.size();
        int trainedDays = 0;
        for (DailyStats ds : stats)
            if (ds.getCardsTrained() != 0)
                trainedDays++;

        double avgInterval = 0;
        double avgRepeatsPerDay = 0;

        if (cards.size() != 0) {
            avgInterval = (double) sumIntervals / cards.size();
            avgRepeatsPerDay = (double) totalRepeats / allDays;
        }

        double trainedDaysPercent = ((double) trainedDays / allDays) * 100;

        getViewState().setupStatisticsData(totalCards + "", totalRepeats + "",
                new DecimalFormat("##.##").format(trainedDaysPercent) + "% (" + trainedDays + " из " +allDays + ")",
                new DecimalFormat("##.##").format(avgRepeatsPerDay),
                new DecimalFormat("##.##").format(avgInterval),
                maxInterval + "");

        getViewState().setupChartData(stats);

    }
}
