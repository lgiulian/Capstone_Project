package com.crilu.gothandroid;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.crilu.gothandroid.data.TournamentDao;
import com.crilu.gothandroid.databinding.ActivitySendMessageBinding;
import com.crilu.gothandroid.model.firestore.Tournament;
import com.crilu.gothandroid.utils.TournamentUtils;
import com.crilu.opengotha.Player;

import java.util.List;

public class SendMessageActivity extends AppCompatActivity {

    public static final String TOURNAMENT_KEY = "TOURNAMENT_KEY";

    private ActivitySendMessageBinding mBinding;
    private Tournament mTournament;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_send_message);

        setSupportActionBar(mBinding.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        String tournamentIdentity = null;
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(TOURNAMENT_KEY)) {
            tournamentIdentity = extras.getString(TOURNAMENT_KEY);
        }

        mTournament = TournamentDao.getTournamentByIdentity(this, tournamentIdentity);
        if (mTournament != null) {
            String tournamentInfoFormat = getString(R.string.format_tournament_info);
            String tournamentInfo = String.format(tournamentInfoFormat,
                    mTournament.getFullName(),
                    GothandroidApplication.dateFormatPretty.format(mTournament.getBeginDate()),
                    mTournament.getLocation());
            String tournamentParticipantsFormat = getString(R.string.format_tournament_participants);
            String fileContents = mTournament.getContent();
            TournamentUtils.openTournament(this, fileContents, mTournament.getIdentity());
            List<Player> players = GothandroidApplication.getGothaModelInstance().getTournament().playersList();
            int numPlayers = 0;
            if (players != null) {
                numPlayers = players.size();
            }
            String tournamentParticipants = String.format(tournamentParticipantsFormat, numPlayers);

            mBinding.tournamentTv.setText(tournamentInfo);
            mBinding.participantsTv.setText(tournamentParticipants);
            mBinding.sendMessageBtn.setEnabled(true);
        } else {
            mBinding.sendMessageBtn.setEnabled(false);
        }
    }

    public void sendMessage(View view) {
        if (mTournament != null) {
            TournamentDao.sendMessageToAll(this, mTournament, mBinding.message.getText().toString(), mBinding.coordinatorLayout);
        }
    }
}
