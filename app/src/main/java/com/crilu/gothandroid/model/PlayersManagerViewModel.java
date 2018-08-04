package com.crilu.gothandroid.model;

import android.arch.lifecycle.ViewModel;

import com.crilu.gothandroid.GothandroidApplication;
import com.crilu.opengotha.Player;
import com.crilu.opengotha.model.PlayersManager;

import java.util.List;

public class PlayersManagerViewModel extends ViewModel {
    private final PlayersManager mPlayersManager;

    public PlayersManagerViewModel() {
        mPlayersManager = GothandroidApplication.getPlayersManagerInstance();
    }

    public PlayersManager getPlayersManager() {
        return mPlayersManager;
    }

    public List<Player> getPlayersList() {
        return GothandroidApplication.getGothaModelInstance().getTournament().playersList();
    }
}
