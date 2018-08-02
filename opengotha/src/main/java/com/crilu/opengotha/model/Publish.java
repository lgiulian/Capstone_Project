package com.crilu.opengotha.model;

import com.crilu.opengotha.DPParameterSet;
import com.crilu.opengotha.ExternalDocument;
import com.crilu.opengotha.GeneralParameterSet;
import com.crilu.opengotha.Gotha;
import com.crilu.opengotha.Player;
import com.crilu.opengotha.PlayerComparator;
import com.crilu.opengotha.PublishParameterSet;
import com.crilu.opengotha.TournamentInterface;
import com.crilu.opengotha.TournamentParameterSet;
import com.crilu.opengotha.TournamentPrinting;
import com.crilu.opengotha.TournamentPublishing;
import com.crilu.opengotha.components.JPanel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Publish {
    private static final long REFRESH_DELAY = 2000;
    private long lastComponentsUpdateTime = 0;
    private TournamentInterface tournament;
    int processedRoundNumber = 0;

    public boolean ckbDisplayClCol;
    public boolean ckbDisplayCoCol;
    public boolean ckbDisplayIndGames;
    public boolean ckbDisplayNumCol;
    public boolean ckbDisplayPlCol;
    public boolean ckbExportHFToOGSite;
    public boolean ckbExportTFToOGSite;
    public boolean ckbExportToLocalFile;
    public boolean ckbExportToUDSite;
    public boolean ckbHTMLAutoscroll;
    public boolean ckbKeepIDs;
    public boolean ckbPrint;
    public boolean ckbShowByePlayer;
    public boolean ckbShowNotFinallyRegisteredPlayers;
    public boolean ckbShowNotPairedPlayers;
    public boolean ckbShowNotParticipatingPlayers;
    public boolean ckbShowPlayerClub;
    public boolean ckbShowPlayerCountry;
    public boolean ckbShowPlayerGrade;
    public boolean jCheckBox1;
    public JPanel jPanel1;
    public JPanel pnlActions;
    public JPanel pnlContents;
    public JPanel pnlGL;
    public JPanel pnlML;
    public JPanel pnlNPP;
    public JPanel pnlPL;
    public JPanel pnlPar;
    public JPanel pnlPub;
    public JPanel pnlQR;
    public JPanel pnlSt;
    public JPanel pnlTeams;
    public boolean rdbGameFormatFull;
    public boolean rdbGameFormatShort;
    public boolean rdbSortByGrade;
    public boolean rdbSortByName;
    public boolean rdbSortByRank;
    private int spnRoundNumber;

    public List<OnPublishListener> mCallbacks = new ArrayList<>();

    public interface OnPublishListener {
        void onTitleUpdate(String title);
        void onMessage(String message);
    }

    public Publish(TournamentInterface tournament) {
        this.tournament = tournament;
        processedRoundNumber = tournament.presumablyCurrentRoundNumber();
        initComponents();
        customInitComponents();
    }

    public boolean addOnPublishListener(OnPublishListener listener) {
        return mCallbacks.add(listener);
    }

    public boolean removeOnPublishListener(OnPublishListener listener) {
        return mCallbacks.remove(listener);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlContents = new JPanel();
        pnlPL = new JPanel();
        pnlGL = new JPanel();
        pnlSt = new JPanel();
        pnlML = new JPanel();
        pnlPar = new JPanel();
        pnlActions = new JPanel();
        pnlQR = new JPanel();
        jPanel1 = new JPanel();
        pnlPub = new JPanel();
        pnlTeams = new JPanel();

        ckbShowPlayerGrade = true;
        ckbShowPlayerClub = true;
        ckbShowByePlayer = true;
        ckbShowNotPairedPlayers = true;
        ckbShowNotFinallyRegisteredPlayers = true;
        ckbDisplayNumCol = true;
        ckbDisplayPlCol = true;
        ckbDisplayIndGames = true;
        ckbPrint = true;
        ckbExportToLocalFile = true;
        ckbExportHFToOGSite = true;
        ckbExportTFToOGSite = true;
    }// </editor-fold>//GEN-END:initComponents
    
    private void customInitComponents() {
        updateAllViews();
    }

    private void btnExportRLFFGActionPerformed(File f) {//GEN-FIRST:event_btnExportRLFFGActionPerformed
        if (tournament == null) {
            return;
        }

        TournamentParameterSet tps = tournament.getTournamentParameterSet();
        GeneralParameterSet gps = tps.getGeneralParameterSet();
        if (gps.getStrSize().length() == 0 || gps.getBasicTime() == 0) {
            sendMessage("Goban size and Thinking time should be documented."
                    + "\nSee Options .. Games menu item");
            return;
        }

        if (f == null) {
            return;
        }
        // Keep tournamentDirectory
        Gotha.exportDirectory = f.getParentFile();

        ExternalDocument.generateTouFile(tournament, f);
    }//GEN-LAST:event_btnExportRLFFGActionPerformed

    private void btnPrintTPActionPerformed() {//GEN-FIRST:event_btnPrintTPActionPerformed
        TournamentPrinting.printTournamentParameters(tournament);
    }//GEN-LAST:event_btnPrintTPActionPerformed

    private void btnExportRLAGAActionPerformed(File f, boolean generateDummyAgaIds) {//GEN-FIRST:event_btnExportRLAGAActionPerformed
        if (tournament == null) {
            sendMessage("No currently open tournament");
            return;
        }
        // If some players have no aga id, should OpenGotha generate dummy Ids ?
        ArrayList<Player> alP = null;
        alP = tournament.playersList();
        int nbPWithoutId = 0;
        for (Player p : alP) {
            if (p.getAgaId().equals("")) {
                nbPWithoutId++;
            }
        }
        if (nbPWithoutId > 0) {
            if (!generateDummyAgaIds) {
                return;
            }
        }
        if (f == null) {
            return;
        }
        // Keep tournamentDirectory
        Gotha.exportDirectory = f.getParentFile();

        ExternalDocument.generateAGAResultsFile(tournament, f);
    }//GEN-LAST:event_btnExportRLAGAActionPerformed

    public void btnExportRLEGFActionPerformed(File f) {//GEN-FIRST:event_btnExportRLEGFActionPerformed
        if (tournament == null) {
            sendMessage("No currently open tournament");
            return;
        }

        if (f == null) {
            return;
        }
        // Keep tournamentDirectory
        Gotha.exportDirectory = f.getParentFile();

        ExternalDocument.generateH9File(tournament, f, false);
    }//GEN-LAST:event_btnExportRLEGFActionPerformed

    public void exportRLEGF(File f) {//GEN-FIRST:event_btnExportRLEGFActionPerformed
        if (tournament == null) {
            sendMessage("No currently open tournament");
            return;
        }

        if (f == null) {
            return;
        }
        // Keep tournamentDirectory
        Gotha.exportDirectory = f.getParentFile();

        ExternalDocument.generateH9ShortFile(tournament, f, false);
    }//GEN-LAST:event_btnExportRLEGFActionPerformed

    private void spnRoundNumberStateChanged() {//GEN-FIRST:event_spnRoundNumberStateChanged
        int demandedRN = spnRoundNumber - 1;
        this.demandedDisplayedRoundNumberHasChanged(demandedRN);
    }//GEN-LAST:event_spnRoundNumberStateChanged

    private void btnExportPlayersCSVActionPerformed(File f) {//GEN-FIRST:event_btnExportPlayersCSVActionPerformed
        if (tournament == null) {
            sendMessage("No currently open tournament");
            return;
        }

        if (f == null) {
            return;
        }
        // Keep tournamentDirectory
        Gotha.exportDirectory = f.getParentFile();

        ExternalDocument.generatePlayersCSVFile(tournament, f);
    }//GEN-LAST:event_btnExportPlayersCSVActionPerformed

    private void btnTestFTPActionPerformed() {//GEN-FIRST:event_btnTestFTPActionPerformed
        File f = new File(Gotha.exportHTMLDirectory, "testfile.html");

        String strReport = TournamentPublishing.sendByFTPToOGSite(tournament, f);
        sendMessage(strReport);
    }//GEN-LAST:event_btnTestFTPActionPerformed

    private void allSortRDBActionPerformed() {//GEN-FIRST:event_allSortRDBActionPerformed
        TournamentParameterSet tps;
        tps = tournament.getTournamentParameterSet();
        DPParameterSet dpps = tps.getDPParameterSet();

        boolean somethingHasChanged = false;
        int newPlayerSortType = PlayerComparator.NAME_ORDER;
        if (this.rdbSortByRank) {
            newPlayerSortType = PlayerComparator.RANK_ORDER;
        }
        if (this.rdbSortByGrade) {
            newPlayerSortType = PlayerComparator.GRADE_ORDER;
        }
        if (newPlayerSortType != dpps.getPlayerSortType()) {
            dpps.setPlayerSortType(newPlayerSortType);
            somethingHasChanged = true;
        }

        if (somethingHasChanged) {
            tournament.setTournamentParameterSet(tps);
            this.tournamentChanged();
        }
    }//GEN-LAST:event_allSortRDBActionPerformed

    private void allGameFormatRDBActionPerformed() {//GEN-FIRST:event_allGameFormatRDBActionPerformed
        TournamentParameterSet tps;
        tps = tournament.getTournamentParameterSet();
        DPParameterSet dpps = tps.getDPParameterSet();

        boolean somethingHasChanged = false;
        int newGameFormat = DPParameterSet.DP_GAME_FORMAT_FULL;
        if (this.rdbGameFormatShort) {
            newGameFormat = DPParameterSet.DP_GAME_FORMAT_SHORT;
        }
        if (newGameFormat != dpps.getGameFormat()) {
            dpps.setGameFormat(newGameFormat);
            somethingHasChanged = true;
        }

        if (somethingHasChanged) {
            tournament.setTournamentParameterSet(tps);
            this.tournamentChanged();
        }
    }//GEN-LAST:event_allGameFormatRDBActionPerformed

    private void allContentsCKBActionPerformed() {//GEN-FIRST:event_allContentsCKBActionPerformed
        TournamentParameterSet tps;
        tps = tournament.getTournamentParameterSet();
        DPParameterSet dpps = tps.getDPParameterSet();
        boolean oldValue;
        boolean newValue;

        boolean somethingHasChanged = false;

        oldValue = dpps.isShowPlayerGrade();
        newValue = this.ckbShowPlayerGrade;
        if (newValue != oldValue) {
            dpps.setShowPlayerGrade(newValue);
            somethingHasChanged = true;
        }
        oldValue = dpps.isShowPlayerCountry();
        newValue = this.ckbShowPlayerCountry;
        if (newValue != oldValue) {
            dpps.setShowPlayerCountry(newValue);
            somethingHasChanged = true;
        }
        oldValue = dpps.isShowPlayerClub();
        newValue = this.ckbShowPlayerClub;
        if (newValue != oldValue) {
            dpps.setShowPlayerClub(newValue);
            somethingHasChanged = true;
        }
        oldValue = dpps.isShowByePlayer();
        newValue = this.ckbShowByePlayer;
        if (newValue != oldValue) {
            dpps.setShowByePlayer(newValue);
            somethingHasChanged = true;
        }
        oldValue = dpps.isShowNotPairedPlayers();
        newValue = this.ckbShowNotPairedPlayers;
        if (newValue != oldValue) {
            dpps.setShowNotPairedPlayers(newValue);
            somethingHasChanged = true;
        }
        oldValue = dpps.isShowNotParticipatingPlayers();
        newValue = this.ckbShowNotParticipatingPlayers;
        if (newValue != oldValue) {
            dpps.setShowNotParticipatingPlayers(newValue);
            somethingHasChanged = true;
        }
        oldValue = dpps.isShowNotFinallyRegisteredPlayers();
        newValue = this.ckbShowNotFinallyRegisteredPlayers;
        if (newValue != oldValue) {
            dpps.setShowNotFinallyRegisteredPlayers(newValue);
            somethingHasChanged = true;
        }

        oldValue = dpps.isDisplayNumCol();
        newValue = this.ckbDisplayNumCol;
        if (newValue != oldValue) {
            dpps.setDisplayNumCol(newValue);
            somethingHasChanged = true;
        }
        oldValue = dpps.isDisplayPlCol();
        newValue = this.ckbDisplayPlCol;
        if (newValue != oldValue) {
            dpps.setDisplayPlCol(newValue);
            somethingHasChanged = true;
        }
        oldValue = dpps.isDisplayCoCol();
        newValue = this.ckbDisplayCoCol;
        if (newValue != oldValue) {
            dpps.setDisplayCoCol(newValue);
            somethingHasChanged = true;
        }
        oldValue = dpps.isDisplayClCol();
        newValue = this.ckbDisplayClCol;
        if (newValue != oldValue) {
            dpps.setDisplayClCol(newValue);
            somethingHasChanged = true;
        }
        oldValue = dpps.isDisplayIndGamesInMatches();
        newValue = this.ckbDisplayIndGames;
        if (newValue != oldValue) {
            dpps.setDisplayIndGamesInMatches(newValue);
            somethingHasChanged = true;
        }

        if (somethingHasChanged) {
            tournament.setTournamentParameterSet(tps);
            this.tournamentChanged();
        }
    }//GEN-LAST:event_allContentsCKBActionPerformed

    private void allParametersCKBActionPerformed() {//GEN-FIRST:event_allParametersCKBActionPerformed
        TournamentParameterSet tps = tournament.getTournamentParameterSet();
        PublishParameterSet pubPS = tps.getPublishParameterSet();
        boolean oldValue;
        boolean newValue;

        boolean somethingHasChanged = false;

        oldValue = pubPS.isPrint();
        newValue = this.ckbPrint;
        if (newValue != oldValue) {
            pubPS.setPrint(newValue);
            somethingHasChanged = true;
        }
        oldValue = pubPS.isExportToLocalFile();
        newValue = this.ckbExportToLocalFile;
        if (newValue != oldValue) {
            pubPS.setExportToLocalFile(newValue);
            somethingHasChanged = true;
        }
        oldValue = pubPS.isExportHFToOGSite();
        newValue = this.ckbExportHFToOGSite;
        if (newValue != oldValue) {
            pubPS.setExportHFToOGSite(newValue);
            somethingHasChanged = true;
        }
        oldValue = pubPS.isExportTFToOGSite();
        newValue = this.ckbExportTFToOGSite;
        if (newValue != oldValue) {
            pubPS.setExportTFToOGSite(newValue);
            somethingHasChanged = true;
        }
        oldValue = pubPS.isExportToUDSite();
        newValue = this.ckbExportToUDSite;
        if (newValue != oldValue) {
            pubPS.setExportToUDSite(newValue);
            somethingHasChanged = true;
        }
        oldValue = pubPS.isHtmlAutoScroll();
        newValue = this.ckbHTMLAutoscroll;
        if (newValue != oldValue) {
            pubPS.setHtmlAutoScroll(newValue);
            somethingHasChanged = true;
        }

        if (somethingHasChanged) {
            tournament.setTournamentParameterSet(tps);
            this.tournamentChanged();
        }
    }//GEN-LAST:event_allParametersCKBActionPerformed

    private void btnPublishPLActionPerformed(File f) {//GEN-FIRST:event_btnPublishPLActionPerformed
        TournamentPublishing.publish(f, tournament, processedRoundNumber,
                TournamentPublishing.TYPE_PLAYERSLIST, TournamentPublishing.SUBTYPE_DEFAULT);
    }//GEN-LAST:event_btnPublishPLActionPerformed

    private void btnPublishGLActionPerformed(File f) {//GEN-FIRST:event_btnPublishGLActionPerformed
        TournamentPublishing.publish(f, tournament, processedRoundNumber,
                TournamentPublishing.TYPE_GAMESLIST, TournamentPublishing.SUBTYPE_DEFAULT);
    }//GEN-LAST:event_btnPublishGLActionPerformed

    public void btnPublishStActionPerformed(File f) {//GEN-FIRST:event_btnPublishStActionPerformed
        TournamentPublishing.publish(f, tournament, processedRoundNumber,
                TournamentPublishing.TYPE_STANDINGS, TournamentPublishing.SUBTYPE_DEFAULT);
    }//GEN-LAST:event_btnPublishStActionPerformed

    private void btnPrintNPPActionPerformed() {//GEN-FIRST:event_btnPrintNPPActionPerformed
        TournamentPrinting.printNotPlayingPlayersList(tournament, processedRoundNumber);
    }//GEN-LAST:event_btnPrintNPPActionPerformed

    private void btnPublishMLActionPerformed(File f) {//GEN-FIRST:event_btnPublishMLActionPerformed
        TournamentPublishing.publish(f, tournament, processedRoundNumber,
                TournamentPublishing.TYPE_MATCHESLIST, TournamentPublishing.SUBTYPE_DEFAULT);
    }//GEN-LAST:event_btnPublishMLActionPerformed

    private void btnPublishTLActionPerformed(File f) {//GEN-FIRST:event_btnPublishTLActionPerformed
        TournamentPublishing.publish(f, tournament, processedRoundNumber,
                TournamentPublishing.TYPE_TEAMSLIST, TournamentPublishing.SUBTYPE_DEFAULT);
    }//GEN-LAST:event_btnPublishTLActionPerformed

    private void btnPublishTSActionPerformed(File f) {//GEN-FIRST:event_btnPublishTSActionPerformed
        TournamentPublishing.publish(f, tournament, processedRoundNumber,
                TournamentPublishing.TYPE_TEAMSSTANDINGS, TournamentPublishing.SUBTYPE_DEFAULT);
    }//GEN-LAST:event_btnPublishTSActionPerformed

    private void btnPrintRSActionPerformed() {//GEN-FIRST:event_btnPrintRSActionPerformed
        TournamentPrinting.printResultSheets(tournament, processedRoundNumber);
    }//GEN-LAST:event_btnPrintRSActionPerformed

    private void demandedDisplayedRoundNumberHasChanged(int demandedRN) {
        int numberOfRounds = 0;
        numberOfRounds = tournament.getTournamentParameterSet().getGeneralParameterSet().getNumberOfRounds();
        if (demandedRN < 0 || demandedRN >= numberOfRounds) {
            spnRoundNumber = processedRoundNumber + 1;
            return;
        }
        if (demandedRN == processedRoundNumber) {
            return;
        }

        processedRoundNumber = demandedRN;
        updateAllViews();
    }

    private void tournamentChanged() {
        tournament.setLastTournamentModificationTime(tournament.getCurrentTournamentTime());
        updateAllViews();
    }

    private void updateAllViews() {
        this.lastComponentsUpdateTime = tournament.getCurrentTournamentTime();
        setTitle("Publish. " + tournament.getFullName());

        int nbRounds = Gotha.MAX_NUMBER_OF_ROUNDS;
        nbRounds = tournament.getTournamentParameterSet().getGeneralParameterSet().getNumberOfRounds();
        if (this.processedRoundNumber >= nbRounds) {
            sendMessage("The number of rounds has been modified."
                            + "\n" + "Current round will be consequently changed");
            this.processedRoundNumber = nbRounds - 1;
        }

        updateComponents();

        this.lastComponentsUpdateTime = tournament.getCurrentTournamentTime();

    }

    private void updateComponents() {
        this.updatePnlContents();
        updatePnlPar();
        updatePnlPub();
    }

    private void updatePnlPub() {
        this.spnRoundNumber = this.processedRoundNumber + 1;
    }

    private void updatePnlContents() {
        DPParameterSet dpps = tournament.getTournamentParameterSet().getDPParameterSet();
        if (dpps.getPlayerSortType() == PlayerComparator.NAME_ORDER) {
            this.rdbSortByName = true;
        } else {
            this.rdbSortByGrade = true;
        }
        if (dpps.getGameFormat() == DPParameterSet.DP_GAME_FORMAT_FULL) {
            this.rdbGameFormatFull = true;
        } else {
            this.rdbGameFormatShort = true;
        }

        this.ckbShowPlayerGrade = dpps.isShowPlayerGrade();
        this.ckbShowPlayerCountry = dpps.isShowPlayerCountry();
        this.ckbShowPlayerClub = dpps.isShowPlayerClub();

        this.ckbDisplayNumCol = dpps.isDisplayNumCol();
        this.ckbDisplayPlCol = dpps.isDisplayPlCol();
        this.ckbDisplayCoCol = dpps.isDisplayCoCol();
        this.ckbDisplayClCol = dpps.isDisplayClCol();
        this.ckbShowByePlayer = dpps.isShowByePlayer();
        this.ckbShowNotPairedPlayers = dpps.isShowNotPairedPlayers();
        this.ckbShowNotParticipatingPlayers = dpps.isShowNotParticipatingPlayers();
        this.ckbShowNotFinallyRegisteredPlayers = dpps.isShowNotFinallyRegisteredPlayers();
        this.ckbDisplayIndGames = dpps.isDisplayIndGamesInMatches();
    }

    private void updatePnlPar() {
        TournamentParameterSet tps = tournament.getTournamentParameterSet();
        GeneralParameterSet gps = tps.getGeneralParameterSet();
        PublishParameterSet pubPS = tps.getPublishParameterSet();

        this.ckbPrint = pubPS.isPrint();
        this.ckbExportToLocalFile = pubPS.isExportToLocalFile();
        this.ckbExportHFToOGSite = pubPS.isExportHFToOGSite();
        this.ckbExportTFToOGSite = pubPS.isExportTFToOGSite();
        this.ckbExportToUDSite = pubPS.isExportToUDSite();
        this.ckbHTMLAutoscroll = pubPS.isHtmlAutoScroll();
    }

    public TournamentInterface getTournament() {
        return tournament;
    }

    private void setTitle(String title) {
        for (OnPublishListener callback : mCallbacks) {
            callback.onTitleUpdate(title);
        }
    }

    private void sendMessage(String message) {
        for (OnPublishListener callback : mCallbacks) {
            callback.onMessage(message);
        }
    }

}