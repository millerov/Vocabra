package com.example.alexmelnikov.vocabra.ui.statistics;

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.example.alexmelnikov.vocabra.ui.BaseView;

import java.util.HashMap;

/**
 * Created by AlexMelnikov on 06.04.18.
 */
@StateStrategyType(OneExecutionStateStrategy.class)
public interface StatisticsView extends BaseView {

    void closeFragment();

    void setupChartData(HashMap<String, Integer> values);
}
