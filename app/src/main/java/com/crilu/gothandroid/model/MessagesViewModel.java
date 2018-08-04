package com.crilu.gothandroid.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.crilu.gothandroid.data.GothaContract;
import com.crilu.gothandroid.data.MessageDao;
import com.crilu.gothandroid.model.firestore.Message;

import java.util.List;

import timber.log.Timber;

public class MessagesViewModel extends AndroidViewModel {

    private MutableLiveData<List<Message>> mMessages;
    private final ContentObserver mContentObserver;

    public MessagesViewModel(@NonNull Application application) {
        super(application);
        Timber.d("In viewModel constructor. Calling setValue on viewModel");
        getMessages().setValue(MessageDao.getAllMessages(application));

        mContentObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                Timber.d("ContentObserver.onChange() called. Calling setValue on viewModel");
                getMessages().setValue(MessageDao.getAllMessages(getApplication()));
            }
        };
        ContentResolver contentResolver = application.getContentResolver();
        /* Register a content observer to be notified of changes to data at a given URI */
        contentResolver.registerContentObserver(
                GothaContract.MessageEntry.CONTENT_URI,
                true,
                mContentObserver);
    }

    public MutableLiveData<List<Message>> getMessages() {
        if (mMessages == null) {
            mMessages = new MutableLiveData<>();
        }
        return mMessages;
    }

    @Override
    protected void finalize() throws Throwable {
        if (mContentObserver != null) {
            getApplication().getContentResolver().unregisterContentObserver(mContentObserver);
        }
        super.finalize();
    }
}
