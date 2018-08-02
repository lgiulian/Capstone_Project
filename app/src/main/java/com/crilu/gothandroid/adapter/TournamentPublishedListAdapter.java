package com.crilu.gothandroid.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crilu.gothandroid.GothandroidApplication;
import com.crilu.gothandroid.R;
import com.crilu.gothandroid.model.firestore.Subscription;
import com.crilu.gothandroid.model.firestore.Tournament;
import com.crilu.gothandroid.utils.TournamentUtils;

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
        holder.beginDate.setText(GothandroidApplication.dateFormatPretty.format(tournament.getBeginDate()));
        holder.fullName.setText(tournament.getFullName());
        holder.location.setText(tournament.getLocation());
        if (TournamentUtils.isMeOwner(tournament)) {
            holder.rootView.setBackgroundResource(R.drawable.list_color_owner_selector);
        } else {
            holder.rootView.setBackgroundResource(R.drawable.list_color_selector);
        }
        String subscriptionType = TournamentUtils.getSubscriptionType(holder.getContext(), tournament);
        if (Subscription.INTENT_OBSERVER.equals(subscriptionType)) {
            holder.badge.setBackgroundResource(R.color.badge_observer);
        } else if (Subscription.INTENT_PARTICIPANT.equals(subscriptionType)) {
            holder.badge.setBackgroundResource(R.color.badge_participant);
        } else {
            holder.badge.setBackgroundResource(R.color.badge_opened);
        }
    }

    @Override
    public int getItemCount() {
        return mData != null? mData.size(): 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
        Context context;
        TextView beginDate;
        TextView fullName;
        TextView location;
        TextView director;
        View badge;
        View rootView;

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            beginDate = itemView.findViewById(R.id.begin_date);
            fullName = itemView.findViewById(R.id.full_name);
            location = itemView.findViewById(R.id.location);
            badge = itemView.findViewById(R.id.badge_view);
            rootView = itemView.findViewById(R.id.root_view);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        public Context getContext() {
            return context;
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
            boolean published = TournamentUtils.isPublished(selectedTournamentForContextMenu);
            boolean iAmOwner = TournamentUtils.isMeOwner(selectedTournamentForContextMenu);
            if (iAmOwner) {
                menu.add(0, R.id.open, 0, v.getContext().getString(R.string.open));
                menu.add(0, R.id.edit, 0, v.getContext().getString(R.string.edit));
            }
            if (iAmOwner && !published) {
                menu.add(0, R.id.publish_tournament, 0, v.getContext().getString(R.string.publish_tournament));
            }
            if (iAmOwner && published) {
                menu.add(0, R.id.save_and_upload, 0, v.getContext().getString(R.string.save_and_upload_tournament));
                menu.add(0, R.id.publish_results, 0, v.getContext().getString(R.string.publish_results));
                menu.add(0, R.id.send_message_to_all, 0, v.getContext().getString(R.string.send_message_to_all));
            }
            if (published) {
                menu.add(0, R.id.register, 0, v.getContext().getString(R.string.register));
                menu.add(0, R.id.subscribe, 0, v.getContext().getString(R.string.subscribe));
            }
        }
    }

}
