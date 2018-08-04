package com.crilu.gothandroid;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;

import com.crilu.gothandroid.adapter.MessageListAdapter;
import com.crilu.gothandroid.databinding.ActivityMessageBinding;
import com.crilu.gothandroid.model.MessagesViewModel;
import com.crilu.gothandroid.model.firestore.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    private List<Message> mMessages = new ArrayList<>();
    private MessageListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMessageBinding mBinding = DataBindingUtil.setContentView(this, R.layout.activity_message);

        setSupportActionBar(mBinding.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setupViewModel();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mBinding.messagesListView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mBinding.messagesListView.getContext(),
                layoutManager.getOrientation());
        mBinding.messagesListView.addItemDecoration(dividerItemDecoration);
        mAdapter = new MessageListAdapter(mMessages);
        mBinding.messagesListView.setAdapter(mAdapter);
    }

    private void setupViewModel() {
        MessagesViewModel mModel = ViewModelProviders.of(this).get(MessagesViewModel.class);
        final Observer<List<Message>> listObserver = new Observer<List<Message>>() {
            @Override
            public void onChanged(@Nullable final List<Message> newList) {
                // Update the UI
                mMessages = newList;
                mAdapter.setData(newList);
                mAdapter.notifyDataSetChanged();
            }
        };

        mModel.getMessages().observe(this, listObserver);
    }
}
