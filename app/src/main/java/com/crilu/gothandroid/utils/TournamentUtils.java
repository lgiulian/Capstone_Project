package com.crilu.gothandroid.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.crilu.gothandroid.GothandroidApplication;
import com.crilu.gothandroid.R;
import com.crilu.gothandroid.data.TournamentDao;
import com.crilu.gothandroid.model.firestore.Tournament;
import com.crilu.opengotha.ExternalDocument;
import com.crilu.opengotha.Gotha;
import com.crilu.opengotha.RatedPlayer;
import com.crilu.opengotha.RatingList;
import com.crilu.opengotha.TournamentInterface;
import com.crilu.opengotha.model.PlayersManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import timber.log.Timber;

public class TournamentUtils {

    public static void registerPlayerForTournament(final Context context, final String egfPin, final String tournamentIdentity) {
        // First ensure the players are loaded and then make the registration
        FutureTask<RatingList> futureTask = new FutureTask<>(new Callable<RatingList>() {
            @Override
            public RatingList call() throws Exception {
                RatingList ratingList = GothandroidApplication.getRatingList();
                if (ratingList == null) {
                    ratingList = new RatingList(RatingList.TYPE_EGF, context.getResources().openRawResource(R.raw.egf_db));
                }
                GothandroidApplication.setRatingList(ratingList);
                Tournament tournament = TournamentDao.getTournamentByIdentity(context, tournamentIdentity);
                if (tournament != null) {
                    openTournament(context, tournament.getContent(), tournament.getIdentity());
                    PlayersManager playersManager = GothandroidApplication.getPlayersManagerInstance();
                    RatedPlayer selectedPlayer = ratingList.getRatedPlayer(egfPin);
                    boolean[] participation = new boolean[Gotha.MAX_NUMBER_OF_ROUNDS];
                    Arrays.fill(participation, Boolean.TRUE);
                    playersManager.register(selectedPlayer.getName(),
                            selectedPlayer.getFirstName(),
                            selectedPlayer.getCountry(),
                            selectedPlayer.getClub(),
                            selectedPlayer.getEgfPin(),
                            selectedPlayer.getFfgLicence(),
                            selectedPlayer.getFfgLicenceStatus(),
                            selectedPlayer.getAgaId(),
                            selectedPlayer.getAgaExpirationDate(),
                            selectedPlayer.getStrGrade(),
                            "FIN",
                            selectedPlayer.getStrRawRating(),
                            selectedPlayer.getStrRawRating(),
                            "0",
                            participation);
                    updateTournamentModelAndSaveInDB(context, tournamentIdentity);
                } else {
                    Timber.d("No tournament found for identity %s", tournamentIdentity);
                }
                return ratingList;
            }
        });
        AppExecutors.getInstance().diskIO().execute(futureTask);
    }

    private static void updateTournamentModelAndSaveInDB(Context context, String tournamentIdentity) {
        // update tournament model with the new content and save it in DB
        final TournamentInterface currentOpenedTournament = GothandroidApplication.getGothaModelInstance().getTournament();
        Tournament tournamentModel = TournamentDao.getTournamentByIdentity(context, tournamentIdentity);
        File file = TournamentUtils.getXmlFile(context, currentOpenedTournament);
        try {
            String tournamentContent = FileUtils.getFileContents(file);
            tournamentModel.setContent(tournamentContent);
            TournamentDao.saveTournament(context, tournamentModel);
        } catch (IOException e) {
            Timber.d("Failed to read tournament file");
            e.printStackTrace();
        }
    }

    public static void openTournament(Context context, String fileContents, String tournamentIdentity) {
        String filename = "temp_tournament_file";
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        File file = new File(context.getFilesDir(), filename);
        try {
            Timber.d("Opening tournament %s", tournamentIdentity);
            GothandroidApplication.getGothaModelInstance().openTournamentFromFile(file, tournamentIdentity);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    public static File getXmlFile(Context context, TournamentInterface currentOpenedTournament) {
        String filename = currentOpenedTournament.getFullName() + ".xml";
        File file = new File(context.getFilesDir(), filename);
        ExternalDocument.generateXMLFile(currentOpenedTournament, file);
        return file;
    }

}
