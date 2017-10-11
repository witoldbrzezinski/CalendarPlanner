package com.brzezinski.witold.calendarplanner.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.brzezinski.witold.calendarplanner.R;
import com.brzezinski.witold.calendarplanner.database.DatabaseManager;
import com.brzezinski.witold.calendarplanner.model.Event;

import java.util.List;

/**
 * Created by Witold Brzeziński on 10.10.2017.
 */

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventViewHolder> {

    private List<Event> mEventList;
    private Context mContext;
    DateUtils dateUtils = new DateUtils();
    DatabaseManager databaseManager = new DatabaseManager();

    public EventListAdapter(List<Event> eventList) {
        mEventList = eventList;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item,parent,false);
        EventViewHolder viewHolder = new EventViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        mContext = holder.itemView.getContext();
        final Event event = mEventList.get(position);
        holder.nameTextView.setText(event.getName());
        holder.startDateTextView.setText(dateUtils.getDateStringForItem(event.getStartDate())+" "
                +event.getStartHour()+" - "+dateUtils.getDateStringForItem(event.getEndDate())+" "+event.getEndHour());
        holder.placeTextView.setText(event.getPlace());
        holder.descriptionTextView.setText(event.getDescription());
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(R.string.deleteEvent);
                builder.setCancelable(true);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        databaseManager.deleteEvent(event);
                        notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton(R.string.cancel,null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mEventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.eventNameListTextView)
        TextView nameTextView;
        @BindView(R.id.eventStartDateTextView) TextView startDateTextView;
        @BindView(R.id.eventDescriptionTextView) TextView descriptionTextView;
        @BindView(R.id.eventPlaceTextView) TextView placeTextView;

        public EventViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View view) {
        }
    }
}
