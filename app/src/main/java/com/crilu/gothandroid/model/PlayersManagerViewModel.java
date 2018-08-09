package com.crilu.gothandroid.model;

import android.arch.lifecycle.ViewModel;

import com.crilu.gothandroid.GothandroidApplication;
import com.crilu.opengotha.Player;
import com.crilu.opengotha.model.PlayersManager;

import java.util.ArrayList;
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
        List<Player> players = new ArrayList<>();
        if (GothandroidApplication.getGothaModelInstance() != null && GothandroidApplication.getGothaModelInstance().getTournament() != null) {
            players = GothandroidApplication.getGothaModelInstance().getTournament().playersList();
        }
        return players;
    }
}
