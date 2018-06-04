package com.crilu.gothandroid.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crilu.gothandroid.GothandroidApplication;
import com.crilu.gothandroid.R;
import com.crilu.gothandroid.model.firestore.Tournament;

import java.util.List;

public class TournamentPublishedListAdapter extends RecyclerView.Adapter<TournamentPublishedListAdapter.ViewHolder> {

    private final OnTournamentClickListener mClickHandler;

    private List<Tournament> mData;

    public interface OnTournamentClickListener {
        void onTournamentSelected(int position);
    }

    public TournamentPublishedListAdapter(OnTournamentClickListener clickHandler, List<Tournament> tournaments) {
        mClickHandler = clickHandler;
        mData = tournaments;
    }

    public void setData(List<Tournament> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View rootView = inflater.inflate(R.layout.tournament_published_item_layout, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tournament tournament = mData.get(position);
        holder.beginDate.setText(GothandroidApplication.dateFormat.format(tournament.getBeginDate()));
        holder.fullName.setText(tournament.getFullName());
        holder.location.setText(tournament.getLocation());
    }

    @Override
    public int getItemCount() {
        return mData != null? mData.size(): 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView beginDate;
        TextView fullName;
        TextView location;
        TextView director;

        public ViewHolder(View itemView) {
            super(itemView);
            beginDate = itemView.findViewById(R.id.begin_date);
            fullName = itemView.findViewById(R.id.full_name);
            location = itemView.findViewById(R.id.location);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            mClickHandler.onTournamentSelected(pos);
        }
    }
}
