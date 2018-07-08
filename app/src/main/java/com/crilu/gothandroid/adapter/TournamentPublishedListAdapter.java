package com.crilu.gothandroid.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ContextMenu;
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

    private int mSelectedItemForContextMenu;

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

    public int getSelectedItemForContextMenu() {
        return mSelectedItemForContextMenu;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View rootView = inflater.inflate(R.layout.item_tournament_published_list, parent, false);
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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
        TextView beginDate;
        TextView fullName;
        TextView location;
        TextView director;

        public ViewHolder(View itemView) {
            super(itemView);
            beginDate = itemView.findViewById(R.id.begin_date);
            fullName = itemView.findViewById(R.id.full_name);
            location = itemView.findViewById(R.id.location);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            mClickHandler.onTournamentSelected(pos);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            mSelectedItemForContextMenu = getAdapterPosition();
            menu.setHeaderTitle(R.string.select_the_action);
            Tournament selectedTournamentForContextMenu = mData.get(mSelectedItemForContextMenu);
            String uID = GothandroidApplication.getCurrentUser();
            boolean published = !TextUtils.isEmpty(selectedTournamentForContextMenu.getIdentity());
            boolean iAmOwner = uID != null && uID.equals(selectedTournamentForContextMenu.getCreator());
            if (iAmOwner) {
                menu.add(0, R.id.open, 0, v.getContext().getString(R.string.open));
                menu.add(0, R.id.edit, 0, v.getContext().getString(R.string.edit));
                menu.add(0, R.id.delete, 0, v.getContext().getString(R.string.delete));
                menu.add(0, R.id.publish_tournament, 0, v.getContext().getString(R.string.publish_tournament));
            }
            if (iAmOwner && published) {
                menu.add(0, R.id.publish_results, 0, v.getContext().getString(R.string.publish_results));
            }
            if (published) {
                menu.add(0, R.id.register, 0, v.getContext().getString(R.string.register));
                menu.add(0, R.id.subscribe, 0, v.getContext().getString(R.string.subscribe));
            }
        }
    }
}
