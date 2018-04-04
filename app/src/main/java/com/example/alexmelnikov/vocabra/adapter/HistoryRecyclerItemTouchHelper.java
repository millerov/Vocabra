package com.example.alexmelnikov.vocabra.adapter;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.example.alexmelnikov.vocabra.ui.translator.TranslatorPresenter;

/**
 * Created by AlexMelnikov on 04.04.18.
 */

public class HistoryRecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private HistoryItemTouchHelperListener listener;

    public HistoryRecyclerItemTouchHelper(int dragDirs, int swipeDirs, HistoryItemTouchHelperListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        listener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
    }


    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            final View cardHistory = ((HistoryAdapter.HistoryViewHolder) viewHolder).cardHistory;

            getDefaultUIUtil().onSelected(cardHistory);
        }
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {
        final View cardHistory = ((HistoryAdapter.HistoryViewHolder) viewHolder).cardHistory;
        getDefaultUIUtil().onDrawOver(c, recyclerView, cardHistory, dX, dY,
                actionState, isCurrentlyActive);
    }


    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final View cardHistory = ((HistoryAdapter.HistoryViewHolder) viewHolder).cardHistory;
        getDefaultUIUtil().clearView(cardHistory);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        final View cardHistory = ((HistoryAdapter.HistoryViewHolder) viewHolder).cardHistory;

        getDefaultUIUtil().onDraw(c, recyclerView, cardHistory, dX, dY,
                actionState, isCurrentlyActive);
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    public interface HistoryItemTouchHelperListener {
        void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }

}
