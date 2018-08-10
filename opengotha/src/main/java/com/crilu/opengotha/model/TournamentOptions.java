package com.crilu.opengotha.model;

import com.crilu.opengotha.Club;
import com.crilu.opengotha.ClubsGroup;
import com.crilu.opengotha.ClubsList;
import com.crilu.opengotha.DPParameterSet;
import com.crilu.opengotha.GeneralParameterSet;
import com.crilu.opengotha.Gotha;
import com.crilu.opengotha.HandicapParameterSet;
import com.crilu.opengotha.PairingParameterSet;
import com.crilu.opengotha.PlacementCriterion;
import com.crilu.opengotha.PlacementParameterSet;
import com.crilu.opengotha.Player;
import com.crilu.opengotha.RatingList;
import com.crilu.opengotha.TeamPlacementParameterSet;
import com.crilu.opengotha.TeamTournamentParameterSet;
import com.crilu.opengotha.TournamentInterface;
import com.crilu.opengotha.TournamentParameterSet;
import com.crilu.opengotha.components.DefaultComboBoxModel;
import com.crilu.opengotha.components.DefaultTableModel;
import com.crilu.opengotha.components.JComboBox;
import com.crilu.opengotha.components.JList;
import com.crilu.opengotha.components.JPanel;
import com.crilu.opengotha.components.ListModel;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TournamentOptions {
    private static final long REFRESH_DELAY = 2000;
    private long lastComponentsUpdateTime = 0;

    private static final int CRITERION_NAME = 0;
    private static final int CRITERION_SHORT_NAME = 1;
    private static final int CRITERION_DESCRIPTION = 2;

    private TournamentInterface tournament;
    public boolean rdbMcMahon;
    public boolean ckbRoundDown;
    public boolean ckbAvoidPairingSamePair;
    public boolean ckbBalanceWB;
    public boolean rdbNoRandom;
    public boolean ckbAvoidMixingCategories;
    public boolean ckbMinimizeScoreDifference;
    public boolean ckbMinimizeScoreDifferenceEnabled;
    public boolean rdbDUDDUGMid;
    public boolean rdbDUDDLGMid;
    public boolean ckbCompensate;
    public boolean ckbAvoid2DUDD;
    public boolean ckbAddSortOnRating;
    public boolean rdbFormerSplitAndRandom;
    public boolean rdbLatterSplitAndFold;
    public boolean ckbSeBarThresholdActive;

    public boolean rdbAbsentMMS0;
    public boolean rdbAbsentMMS1;
    public boolean rdbAbsentMMS2;
    public boolean rdbAbsentNBW0;
    public boolean rdbAbsentNBW1;
    public boolean rdbAbsentNBW2;
    public boolean rdbAcceptRandom;
    public boolean rdbByeMMS0;
    public boolean rdbByeMMS1;
    public boolean rdbByeMMS2;
    public boolean rdbByeNBW0;
    public boolean rdbByeNBW1;
    public boolean rdbByeNBW2;
    public boolean rdbDUDDLGBot;
    public boolean rdbDUDDLGTop;
    public boolean rdbDUDDUGBot;
    public boolean rdbDUDDUGTop;
    public boolean rdbFormerSplitAndFold;
    public boolean rdbFormerSplitAndSlip;
    public boolean rdbHdBaseMMS;
    public boolean rdbHdBaseRank;
    public boolean rdbHdCorrection0;
    public boolean rdbHdCorrection1;
    public boolean rdbHdCorrection2;
    public boolean rdbHdCorrection3;
    public boolean rdbHdCorrectionPlus1;
    public boolean rdbLatterSplitAndRandom;
    public boolean rdbLatterSplitAndSlip;
    public boolean rdbSwiss;
    public boolean rdbSwissCat;
    public boolean ckbResetParameters;

    public boolean lblNewSystemEnabled = true;
    public boolean rdbMcMahonEnabled = true;
    public boolean rdbSwissEnabled = true;
    public boolean rdbSwissCatEnabled = true;
    public boolean lblRecommendedEnabled = true;

    public List<OnTournamentOptionsListener> mCallbacks = new ArrayList<>();
    public String txfSeRankThreshold;
    public boolean ckbSeNbWinsThresholdActive;
    public String txfSeClub;
    public String txfSeCountry;
    public String txfSeClubsGroup;
    public boolean ckbSeMinimizeHandicap;
    public boolean ckbDeterministic;
    public String txfLastRoundForSeedSystem1;
    public String txfMMFloor;
    public String txfMMBar;
    public String txfHdCeiling;
    public String txfNoHdRankThreshold;
    public String txfNumberOfRounds;
    public String txfBeginDate;
    public String txfLocation;
    public String txfNumberOfCategories;
    public String txfName;
    public String[] tabTxfLowerLimitOfCat;
    public String txaWarning;
    public String txfShortName;
    public boolean txfShortNameEditable;
    public String txfEndDate;
    public String txfMMZero;
    public boolean cbxTeamCrit1Enabled;
    public boolean pnlMcMahonVisible;
    public boolean tpnParametersVisible;
    public boolean ckbDeterministicEnabled;
    public boolean ckbSeBarThresholdActiveVisible;
    public boolean ckbSeMinimizeHandicapVisible;
    public boolean pnlHandicapVisible;
    public String txfDirector;
    public boolean pnlCategoriesVisible;
    public JList lstClubsInSelectedGroup;
    public JComboBox cbxCrit1;
    public JComboBox cbxCrit2;
    public JComboBox cbxCrit3;
    public JComboBox cbxCrit4;
    public JList lstClubsGroups;
    public JComboBox cbxTeamCrit1;
    public JComboBox cbxTeamCrit2;
    public JComboBox cbxTeamCrit3;
    public JComboBox cbxTeamCrit4;
    public JComboBox cbxTeamCrit5;
    public JComboBox cbxTeamCrit6;
    public boolean cbxCrit1Enabled;
    public boolean cbxCrit2Enabled;
    public DefaultTableModel tblTeamGlossaryModel;
    public boolean pnlTournamentDetailsVisible;
    public boolean lblSystemNameVisible;
    public String lblSystemName;
    public String[] tabLblCat;
    public String lblHandicap;
    public JList lstClubs;
    public JPanel pnlCategories;
    public String[] tabLblNbPlayersOfCat;
    public DefaultTableModel tblGlossary;

    public interface OnTournamentOptionsListener {
        void onDisplayChangeSystem();
        void onTitleUpdate(String title);
        void onMessage(String message);
    }

    public TournamentOptions(TournamentInterface tournament) {
        this.tournament = tournament;

        initComponents();
        customInitComponents();

    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rdbMcMahon = true;
        ckbRoundDown = true;

        ckbAvoidPairingSamePair = true;
        ckbBalanceWB = true;
        rdbNoRandom = true;

        ckbAvoidMixingCategories = true;
        ckbMinimizeScoreDifference = true;
        ckbMinimizeScoreDifferenceEnabled = false;

        rdbDUDDUGMid = true;
        rdbDUDDLGMid = true;
        ckbCompensate = true;
        ckbAvoid2DUDD = true;
        ckbAddSortOnRating = true;
        rdbFormerSplitAndRandom = true;
        rdbLatterSplitAndFold = true;
        ckbSeBarThresholdActive = true;

        pnlCategories = new JPanel();
        pnlCategories.add("Number of categories");
        pnlCategories.add("Lower limits :");
        pnlCategories.add("1");

        cbxCrit1 = new JComboBox();
        cbxCrit2 = new JComboBox();
        cbxCrit3 = new JComboBox();
        cbxCrit4 = new JComboBox();
        cbxCrit1.setModel(new DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbxCrit2.setModel(new DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbxCrit3.setModel(new DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbxCrit4.setModel(new DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cbxTeamCrit1 = new JComboBox();
        cbxTeamCrit2 = new JComboBox();
        cbxTeamCrit3 = new JComboBox();
        cbxTeamCrit4 = new JComboBox();
        cbxTeamCrit5 = new JComboBox();
        cbxTeamCrit6 = new JComboBox();
        cbxTeamCrit1.setModel(new DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbxTeamCrit2.setModel(new DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbxTeamCrit3.setModel(new DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbxTeamCrit4.setModel(new DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbxTeamCrit5.setModel(new DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbxTeamCrit6.setModel(new DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        tblTeamGlossaryModel = new DefaultTableModel(
                new String [][] {
                        {null, null, null},
                        {null, null, null},
                        {null, null, null},
                        {null, null, null}
                });
        tblGlossary = new DefaultTableModel(
                new String [][] {
                        {null, null, null},
                        {null, null, null},
                        {null, null, null},
                        {null, null, null}
                });
        lstClubsInSelectedGroup = new JList();
        lstClubsGroups = new JList();
        lstClubsGroups.setListData(new String[]{ "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" });
        lstClubs = new JList();
        lstClubs.setListData(new String[]{ "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" });


    }// </editor-fold>//GEN-END:initComponents

    public boolean addTournamentOptionsListener(OnTournamentOptionsListener listener) {
        return mCallbacks.add(listener);
    }

    public boolean removeTournamentOptionsListener(OnTournamentOptionsListener listener) {
        return mCallbacks.remove(listener);
    }

    private void btnDlgChangeSystemOKActionPerformed() {//GEN-FIRST:event_btnDlgChangeSystemOKActionPerformed
        int system = TournamentParameterSet.TYPE_MCMAHON;
        if (this.rdbMcMahon) system = TournamentParameterSet.TYPE_MCMAHON;
        if (this.rdbSwiss) system = TournamentParameterSet.TYPE_SWISS;
        if (this.rdbSwissCat) system = TournamentParameterSet.TYPE_SWISSCAT;

        TournamentParameterSet tps;
        tps = tournament.getTournamentParameterSet();
        GeneralParameterSet gps = tps.getGeneralParameterSet();
        HandicapParameterSet hps = tps.getHandicapParameterSet();
        PlacementParameterSet pps = tps.getPlacementParameterSet();
        PairingParameterSet paiPS = tps.getPairingParameterSet();
        DPParameterSet dpps = tps.getDPParameterSet();
        switch (system){
            case TournamentParameterSet.TYPE_MCMAHON :
                gps.initForMM();
                hps.initForMM();
                pps.initForMM();
                paiPS.initForMM();
                dpps.initForMM();
                break;
            case TournamentParameterSet.TYPE_SWISS:
                gps.initForSwiss();
                hps.initForSwiss();
                pps.initForSwiss();
                paiPS.initForSwiss();
                dpps.initForSwiss();
                break;
            case TournamentParameterSet.TYPE_SWISSCAT :
                gps.initForSwissCat();
                hps.initForSwissCat();
                pps.initForSwissCat();
                paiPS.initForSwissCat();
                dpps.initForSwissCat();
                break;
            default :
                gps.initForMM();
                hps.initForMM();
                pps.initForMM();
                paiPS.initForMM();
        }

        tournament.setTournamentParameterSet(tps);
        this.tournamentChanged();
    }//GEN-LAST:event_btnDlgChangeSystemOKActionPerformed

    private void btnChangeSystemActionPerformed() {//GEN-FIRST:event_btnChangeSystemActionPerformed

        this.ckbResetParameters = false;

        this.lblNewSystemEnabled = true;
        this.rdbMcMahonEnabled = true;
        this.rdbSwissEnabled = true;
        this.rdbSwissCatEnabled = true;
        this.lblRecommendedEnabled = true;

        int tournamentType = TournamentParameterSet.TYPE_UNDEFINED;
        tournamentType = tournament.tournamentType();
        if (tournamentType == TournamentParameterSet.TYPE_MCMAHON) rdbMcMahon = true;
        if (tournamentType == TournamentParameterSet.TYPE_SWISS) rdbSwiss = true;
        if (tournamentType == TournamentParameterSet.TYPE_SWISSCAT) rdbSwissCat = true;

        displayChangeSystem();

    }//GEN-LAST:event_btnChangeSystemActionPerformed

    public void rdbAcceptRandomActionPerformed() {//GEN-FIRST:event_rdbAcceptRandomActionPerformed
        this.randomControlsChange();
    }//GEN-LAST:event_rdbAcceptRandomActionPerformed

    public void rdbNoRandomActionPerformed() {//GEN-FIRST:event_rdbNoRandomActionPerformed
        this.randomControlsChange();
    }//GEN-LAST:event_rdbNoRandomActionPerformed

    public void ckbSecCritFocusLost() {//GEN-FIRST:event_ckbSecCritFocusLost
        secCritControlsFocusLost();
    }//GEN-LAST:event_ckbSecCritFocusLost

    public void txfSecCritFocusLost() {//GEN-FIRST:event_txfSecCritFocusLost
        secCritControlsFocusLost();
    }//GEN-LAST:event_txfSecCritFocusLost

    private void secCritControlsFocusLost(){
        TournamentParameterSet tps;
        PairingParameterSet paiPS;
        tps = tournament.getTournamentParameterSet();
        paiPS = tps.getPairingParameterSet();

        boolean bSomethingHasChanged = false;


        int oldRankThreshold = paiPS.getPaiSeRankThreshold();
        int newRankThreshold = Player.convertKDPToInt(this.txfSeRankThreshold);
        if (newRankThreshold != oldRankThreshold){
            paiPS.setPaiSeRankThreshold(newRankThreshold);
            bSomethingHasChanged = true;
        }

        boolean oldBarThresholdActive = paiPS.isPaiSeBarThresholdActive();
        boolean newBarThresholdActive = this.ckbSeBarThresholdActive;
        if (newBarThresholdActive != oldBarThresholdActive){
            paiPS.setPaiSeBarThresholdActive(newBarThresholdActive);
            bSomethingHasChanged = true;
        }
        boolean oldNbWinsThresholdActive = paiPS.isPaiSeNbWinsThresholdActive();
        boolean newNbWinsThresholdActive = this.ckbSeNbWinsThresholdActive;
        if (newNbWinsThresholdActive != oldNbWinsThresholdActive){
            paiPS.setPaiSeNbWinsThresholdActive(newNbWinsThresholdActive);
            bSomethingHasChanged = true;
        }

        int oldPreferMMSDiffRatherThanSameCountry = paiPS.getPaiSePreferMMSDiffRatherThanSameCountry();
        int newPreferMMSDiffRatherThanSameCountry = new Integer(this.txfSeCountry).intValue();
        if (newPreferMMSDiffRatherThanSameCountry != oldPreferMMSDiffRatherThanSameCountry){
            paiPS.setPaiSePreferMMSDiffRatherThanSameCountry(newPreferMMSDiffRatherThanSameCountry);
            bSomethingHasChanged = true;
        }
        int oldPreferMMSDiffRatherThanSameClubsGroup = paiPS.getPaiSePreferMMSDiffRatherThanSameClubsGroup();
        int newPreferMMSDiffRatherThanSameClubsGroup = new Integer(this.txfSeClubsGroup).intValue();
        if (newPreferMMSDiffRatherThanSameClubsGroup != oldPreferMMSDiffRatherThanSameClubsGroup){
            paiPS.setPaiSePreferMMSDiffRatherThanSameClubsGroup(newPreferMMSDiffRatherThanSameClubsGroup);
            bSomethingHasChanged = true;
        }
        int oldPreferMMSDiffRatherThanSameClub = paiPS.getPaiSePreferMMSDiffRatherThanSameClub();
        int newPreferMMSDiffRatherThanSameClub = new Integer(this.txfSeClub).intValue();
        if (newPreferMMSDiffRatherThanSameClub != oldPreferMMSDiffRatherThanSameClub){
            paiPS.setPaiSePreferMMSDiffRatherThanSameClub(newPreferMMSDiffRatherThanSameClub);
            bSomethingHasChanged = true;
        }
        long oldMinimizeHandicap = paiPS.getPaiSeMinimizeHandicap();
        long newMinimizeHandicap = this.ckbSeMinimizeHandicap ? paiPS.getPaiSeDefSecCrit() : 0;
        if (newMinimizeHandicap != oldMinimizeHandicap){
            paiPS.setPaiSeMinimizeHandicap(newMinimizeHandicap);
            bSomethingHasChanged = true;
        }

        if (bSomethingHasChanged){
            tournament.setTournamentParameterSet(tps);
            this.tournamentChanged();
        }
    }

    public void ckbBalanceWBFocusLost() {//GEN-FIRST:event_ckbBalanceWBFocusLost
        TournamentParameterSet tps;
        PairingParameterSet paiPS;
        tps = tournament.getTournamentParameterSet();
        paiPS = tps.getPairingParameterSet();

        boolean bSomethingHasChanged = false;
        long oldBalanceWB = paiPS.getPaiBaBalanceWB();
        long newBalanceWB = this.ckbBalanceWB ? PairingParameterSet.PAIBA_MAX_BALANCEWB : 0;
        if (newBalanceWB != oldBalanceWB){
            paiPS.setPaiBaBalanceWB(newBalanceWB);
            bSomethingHasChanged = true;
        }
        if (bSomethingHasChanged){
            tournament.setTournamentParameterSet(tps);
            this.tournamentChanged();
        }
    }//GEN-LAST:event_ckbBalanceWBFocusLost

    private void ckbDeterministicFocusLost() {//GEN-FIRST:event_ckbDeterministicFocusLost
        randomControlsChange();
    }//GEN-LAST:event_ckbDeterministicFocusLost

    private void rdbRandomFocusLost() {//GEN-FIRST:event_rdbRandomFocusLost
        randomControlsChange();
    }//GEN-LAST:event_rdbRandomFocusLost



    private void randomControlsChange(){
        TournamentParameterSet tps;
        PairingParameterSet paiPS;
        tps = tournament.getTournamentParameterSet();
        paiPS = tps.getPairingParameterSet();

        boolean bSomethingHasChanged = false;
        long oldRandom = paiPS.getPaiBaRandom();
        long newRandom = this.rdbAcceptRandom ? PairingParameterSet.PAIBA_MAX_RANDOM : 0;
        if (newRandom != oldRandom){
            paiPS.setPaiBaRandom(newRandom);
            bSomethingHasChanged = true;
        }
        boolean oldDeterminitic = paiPS.isPaiBaDeterministic();
        boolean newDeterminitic = this.ckbDeterministic;
        if (newDeterminitic != oldDeterminitic){
            paiPS.setPaiBaDeterministic(newDeterminitic);
            bSomethingHasChanged = true;
        }
        if (bSomethingHasChanged){
            tournament.setTournamentParameterSet(tps);
            this.tournamentChanged();
        }
    }

    public void rdbDUDDFocusLost() {//GEN-FIRST:event_rdbDUDDFocusLost
        TournamentParameterSet tps;
        PairingParameterSet paiPS;
        tps = tournament.getTournamentParameterSet();
        paiPS = tps.getPairingParameterSet();

        boolean bSomethingHasChanged = false;
        int oldDUDDUpperMode = paiPS.getPaiMaDUDDUpperMode();
        int newDUDDUpperMode = PairingParameterSet.PAIMA_DUDD_TOP;
        if (this.rdbDUDDUGMid) newDUDDUpperMode = PairingParameterSet.PAIMA_DUDD_MID;
        if (this.rdbDUDDUGBot) newDUDDUpperMode = PairingParameterSet.PAIMA_DUDD_BOT;
        if (newDUDDUpperMode != oldDUDDUpperMode){
            paiPS.setPaiMaDUDDUpperMode(newDUDDUpperMode);
            bSomethingHasChanged = true;
        }
        int oldDUDDLowerMode = paiPS.getPaiMaDUDDLowerMode();
        int newDUDDLowerMode = PairingParameterSet.PAIMA_DUDD_TOP;
        if (this.rdbDUDDLGMid) newDUDDLowerMode = PairingParameterSet.PAIMA_DUDD_MID;
        if (this.rdbDUDDLGBot) newDUDDLowerMode = PairingParameterSet.PAIMA_DUDD_BOT;
        if (newDUDDLowerMode != oldDUDDLowerMode){
            paiPS.setPaiMaDUDDLowerMode(newDUDDLowerMode);
            bSomethingHasChanged = true;
        }

        if (bSomethingHasChanged){
            tournament.setTournamentParameterSet(tps);
            this.tournamentChanged();
        }
    }//GEN-LAST:event_rdbDUDDFocusLost

    public void rdbSeedSystemFocusLost() {//GEN-FIRST:event_rdbSeedSystemFocusLost
        TournamentParameterSet tps;
        PairingParameterSet paiPS;
        tps = tournament.getTournamentParameterSet();
        paiPS = tps.getPairingParameterSet();

        boolean bSomethingHasChanged = false;
        int oldSeedSystem1 = paiPS.getPaiMaSeedSystem1();
        int newSeedSystem1 = PairingParameterSet.PAIMA_SEED_SPLITANDRANDOM;
        if (this.rdbFormerSplitAndFold) newSeedSystem1 = PairingParameterSet.PAIMA_SEED_SPLITANDFOLD;
        if (this.rdbFormerSplitAndSlip) newSeedSystem1 = PairingParameterSet.PAIMA_SEED_SPLITANDSLIP;
        if (newSeedSystem1 != oldSeedSystem1){
            paiPS.setPaiMaSeedSystem1(newSeedSystem1);
            bSomethingHasChanged = true;
        }

        int oldSeedSystem2 = paiPS.getPaiMaSeedSystem2();
        int newSeedSystem2 = PairingParameterSet.PAIMA_SEED_SPLITANDRANDOM;
        if (this.rdbLatterSplitAndFold) newSeedSystem2 = PairingParameterSet.PAIMA_SEED_SPLITANDFOLD;
        if (this.rdbLatterSplitAndSlip) newSeedSystem2 = PairingParameterSet.PAIMA_SEED_SPLITANDSLIP;
        if (newSeedSystem2 != oldSeedSystem2){
            paiPS.setPaiMaSeedSystem2(newSeedSystem2);
            bSomethingHasChanged = true;
        }

        if (bSomethingHasChanged){
            tournament.setTournamentParameterSet(tps);
            this.tournamentChanged();
        }
    }//GEN-LAST:event_rdbSeedSystemFocusLost

    public void ckbAddSortOnRatingFocusLost() {//GEN-FIRST:event_ckbAddSortOnRatingFocusLost
        TournamentParameterSet tps;
        GeneralParameterSet gps = null;
        PairingParameterSet paiPS;
        tps = tournament.getTournamentParameterSet();
        paiPS = tps.getPairingParameterSet();
        int oldPaiMaAdditionalPlacementCritSystem1 = paiPS.getPaiMaAdditionalPlacementCritSystem1();
        int newPaiMaAdditionalPlacementCritSystem1 = this.ckbAddSortOnRating ?
                PlacementParameterSet.PLA_CRIT_RATING : 0;

        if (newPaiMaAdditionalPlacementCritSystem1 != oldPaiMaAdditionalPlacementCritSystem1){
            paiPS.setPaiMaAdditionalPlacementCritSystem1(newPaiMaAdditionalPlacementCritSystem1);
            tournament.setTournamentParameterSet(tps);
            this.tournamentChanged();
        }
    }//GEN-LAST:event_ckbAddSortOnRatingFocusLost

    public void txfLastRoundForSeedSystem1FocusLost() {//GEN-FIRST:event_txfLastRoundForSeedSystem1FocusLost
        TournamentParameterSet tps;
        GeneralParameterSet gps;
        PairingParameterSet paiPS;
        tps = tournament.getTournamentParameterSet();
        gps = tps.getGeneralParameterSet();
        paiPS = tps.getPairingParameterSet();
        int oldLastRoundForSeedSystem1 = paiPS.getPaiMaLastRoundForSeedSystem1();
        int newLastRoundForSeedSystem1 = new Integer(txfLastRoundForSeedSystem1).intValue() - 1;
        if (newLastRoundForSeedSystem1 < 1 || newLastRoundForSeedSystem1 >= gps.getNumberOfRounds()){
            this.txfLastRoundForSeedSystem1 = "" + (oldLastRoundForSeedSystem1 + 1);
            return;
        }
        if (newLastRoundForSeedSystem1 != oldLastRoundForSeedSystem1){
            paiPS.setPaiMaLastRoundForSeedSystem1(newLastRoundForSeedSystem1);
            tournament.setTournamentParameterSet(tps);
            this.tournamentChanged();
        }
    }//GEN-LAST:event_txfLastRoundForSeedSystem1FocusLost

    public void txfMMFloorFocusLost() {//GEN-FIRST:event_txfMMFloorFocusLost
        TournamentParameterSet tps;
        GeneralParameterSet gps;
        tps = tournament.getTournamentParameterSet();
        gps = tps.getGeneralParameterSet();
        int oldGenMMFloor = gps.getGenMMFloor();
        int newGenMMFloor = Player.convertKDPToInt(this.txfMMFloor);
        txfMMFloor = "" + Player.convertIntToKD(newGenMMFloor);
        if (newGenMMFloor > GeneralParameterSet.GEN_MM_FLOOR_MAX
                || newGenMMFloor < GeneralParameterSet.GEN_MM_FLOOR_MIN
                || newGenMMFloor > gps.getGenMMBar()){
            // Error. Keep old value
            txfMMFloor = "" + Player.convertIntToKD(oldGenMMFloor);
            return;
        }
        if (newGenMMFloor != oldGenMMFloor){
            gps.setGenMMFloor(newGenMMFloor);
            tournament.setTournamentParameterSet(tps);
            this.tournamentChanged();
        }
    }//GEN-LAST:event_txfMMFloorFocusLost

    public void txfMMBarFocusLost() {//GEN-FIRST:event_txfMMBarFocusLost
        TournamentParameterSet tps;
        GeneralParameterSet gps;
        tps = tournament.getTournamentParameterSet();
        gps = tps.getGeneralParameterSet();
        int oldGenMMBar = gps.getGenMMBar();
        int newGenMMBar = Player.convertKDPToInt(this.txfMMBar);
        txfMMBar = "" + Player.convertIntToKD(newGenMMBar);
        if ((newGenMMBar > GeneralParameterSet.GEN_MM_BAR_MAX)
                || (newGenMMBar < GeneralParameterSet.GEN_MM_BAR_MIN)
                || (newGenMMBar < gps.getGenMMFloor())){
            // Error. Keep old value
            txfMMBar = "" + Player.convertIntToKD(oldGenMMBar);
            return;
        }
        if (newGenMMBar != oldGenMMBar){
            gps.setGenMMBar(newGenMMBar);
            tournament.setTournamentParameterSet(tps);
            this.tournamentChanged();
        }
    }//GEN-LAST:event_txfMMBarFocusLost

    public void rdbAbsentOrByeFocusLost() {//GEN-FIRST:event_rdbAbsentOrByeFocusLost
        TournamentParameterSet tps;
        GeneralParameterSet gps;
        tps = tournament.getTournamentParameterSet();
        gps = tps.getGeneralParameterSet();

        boolean bSomethingHasChanged = false;
        int newGenMMS2ValueAbsent = 0;
        if (this.rdbAbsentMMS1) newGenMMS2ValueAbsent = 1;
        if (this.rdbAbsentMMS2) newGenMMS2ValueAbsent = 2;
        if (newGenMMS2ValueAbsent != gps.getGenMMS2ValueAbsent()){
            gps.setGenMMS2ValueAbsent(newGenMMS2ValueAbsent);
            bSomethingHasChanged = true;
        }
        int newGenNBW2ValueAbsent = 0;
        if (this.rdbAbsentNBW1) newGenNBW2ValueAbsent = 1;
        if (this.rdbAbsentNBW2) newGenNBW2ValueAbsent = 2;
        if (newGenNBW2ValueAbsent != gps.getGenNBW2ValueAbsent()){
            gps.setGenNBW2ValueAbsent(newGenNBW2ValueAbsent);
            bSomethingHasChanged = true;
        }
        int newGenMMS2ValueBye = 0;
        if (this.rdbByeMMS1) newGenMMS2ValueBye = 1;
        if (this.rdbByeMMS2) newGenMMS2ValueBye = 2;
        if (newGenMMS2ValueBye != gps.getGenMMS2ValueBye()){
            gps.setGenMMS2ValueBye(newGenMMS2ValueBye);
            bSomethingHasChanged = true;
        }
        int newGenNBW2ValueBye = 0;
        if (this.rdbByeNBW1) newGenNBW2ValueBye = 1;
        if (this.rdbByeNBW2) newGenNBW2ValueBye = 2;
        if (newGenNBW2ValueBye != gps.getGenNBW2ValueBye()){
            gps.setGenNBW2ValueBye(newGenNBW2ValueBye);
            bSomethingHasChanged = true;
        }

        if (bSomethingHasChanged){
            tournament.setTournamentParameterSet(tps);
            this.tournamentChanged();
        }
    }//GEN-LAST:event_rdbAbsentOrByeFocusLost

    public void txfHdCeilingFocusLost() {//GEN-FIRST:event_txfHdCeilingFocusLost
        TournamentParameterSet tps;
        tps = tournament.getTournamentParameterSet();
        HandicapParameterSet hps = tps.getHandicapParameterSet();
        int oldHdCeiling = hps.getHdCeiling();
        int newHdCeiling = 0;
        try{
            newHdCeiling = (new Integer(txfHdCeiling)).intValue();
        }
        catch(NumberFormatException e){
            this.updatePnlHan();
        }
        if (newHdCeiling < 0 || newHdCeiling > 9) {
            this.updatePnlHan();
            return;
        }
        if (newHdCeiling == oldHdCeiling) return;
        hps.setHdCeiling(newHdCeiling);
        tps.setHandicapParameterSet(hps);
        tournament.setTournamentParameterSet(tps);
        this.tournamentChanged();
    }//GEN-LAST:event_txfHdCeilingFocusLost

    private void updHdCorrection(){
        TournamentParameterSet tps;
        tps = tournament.getTournamentParameterSet();
        HandicapParameterSet hps = tps.getHandicapParameterSet();
        int oldHdCorrection = hps.getHdCorrection();
        int newHdCorrection = 0;
        if (rdbHdCorrection0) newHdCorrection = 0;
        if (rdbHdCorrection1) newHdCorrection = 1;
        if (rdbHdCorrection2) newHdCorrection = 2;
        if (rdbHdCorrection3) newHdCorrection = 3;
        if (rdbHdCorrectionPlus1) newHdCorrection = -1;
        if (newHdCorrection == oldHdCorrection) return;
        hps.setHdCorrection(newHdCorrection);
        tps.setHandicapParameterSet(hps);
        tournament.setTournamentParameterSet(tps);
        this.tournamentChanged();
    }

    public void txfNoHdRankThresholdFocusLost() {//GEN-FIRST:event_txfNoHdRankThresholdFocusLost
        TournamentParameterSet tps;
        tps = tournament.getTournamentParameterSet();
        HandicapParameterSet hps = tps.getHandicapParameterSet();
        int oldNoHdRankThreshold = hps.getHdNoHdRankThreshold();
        int newNoHdRankThreshold = Player.convertKDPToInt(txfNoHdRankThreshold);
        if (newNoHdRankThreshold < Gotha.MIN_RANK) {
            this.updatePnlHan();
            return;
        }
        if (newNoHdRankThreshold == oldNoHdRankThreshold) return;
        hps.setHdNoHdRankThreshold(newNoHdRankThreshold);
        tps.setHandicapParameterSet(hps);
        tournament.setTournamentParameterSet(tps);
        this.tournamentChanged();
    }//GEN-LAST:event_txfNoHdRankThresholdFocusLost

    private void btnAdjustCategoryLimitsActionPerformed() {//GEN-FIRST:event_btnAdjustCategoryLimitsActionPerformed
        tournament.adjustCategoryLimits();
        this.tournamentChanged();
    }//GEN-LAST:event_btnAdjustCategoryLimitsActionPerformed

    public void txfNumberOfRoundsFocusLost() {//GEN-FIRST:event_txfNumberOfRoundsFocusLost
        TournamentParameterSet tps;
        tps = tournament.getTournamentParameterSet();
        GeneralParameterSet gps = tps.getGeneralParameterSet();

        int oldNbRounds = gps.getNumberOfRounds();
        int newNbRounds = oldNbRounds;
        try {
            newNbRounds = Integer.parseInt(this.txfNumberOfRounds);
        } catch (NumberFormatException ex) {
        }
        if (newNbRounds <= 0) newNbRounds = oldNbRounds;
        if (newNbRounds > Gotha.MAX_NUMBER_OF_ROUNDS) newNbRounds = Gotha.MAX_NUMBER_OF_ROUNDS;

        // Refuse to decrease number of rounds if round not empty
        for (int r = oldNbRounds - 1; r >= newNbRounds; r-- ){
            if ((tournament.gamesList(r).size() > 0) || (tournament.getByePlayer(r) != null)) {
                newNbRounds = r + 1;
                break;
            }
        }
        if (newNbRounds == oldNbRounds){
            this.txfNumberOfRounds = "" + oldNbRounds;
            return;
        }

        gps.setNumberOfRounds(newNbRounds);
        tps.setGeneralParameterSet(gps);
        tournament.setTournamentParameterSet(tps);
        this.tournamentChanged();

    }//GEN-LAST:event_txfNumberOfRoundsFocusLost

    public void txfBeginDateFocusLost() {//GEN-FIRST:event_txfBeginDateFocusLost
        TournamentParameterSet tps;
        tps = tournament.getTournamentParameterSet();
        GeneralParameterSet gps = tps.getGeneralParameterSet();
        Date oldBeginDate = gps.getBeginDate();
        Date newBeginDate;
        try {
            newBeginDate = new SimpleDateFormat("yyyy-MM-dd").parse(this.txfBeginDate);
        } catch (ParseException ex) {
            Logger.getLogger(TournamentOptions.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        if (newBeginDate.equals(oldBeginDate)) return;
        gps.setBeginDate(newBeginDate);

        tps.setGeneralParameterSet(gps);
        tournament.setTournamentParameterSet(tps);
        this.tournamentChanged();

    }//GEN-LAST:event_txfBeginDateFocusLost

    public void txfLocationFocusLost() {//GEN-FIRST:event_txfLocationFocusLost
        TournamentParameterSet tps;
        tps = tournament.getTournamentParameterSet();
        GeneralParameterSet gps = tps.getGeneralParameterSet();
        String oldLocation = gps.getLocation();
        String newLocation = txfLocation;
        if (newLocation.compareTo(oldLocation) == 0) return;
        gps.setLocation(newLocation);
        tps.setGeneralParameterSet(gps);
        tournament.setTournamentParameterSet(tps);
        this.tournamentChanged();
    }//GEN-LAST:event_txfLocationFocusLost

    public void txfNameFocusLost() {//GEN-FIRST:event_txfNameFocusLost
        TournamentParameterSet tps;
        tps = tournament.getTournamentParameterSet();
        GeneralParameterSet gps = tps.getGeneralParameterSet();
        String oldName = gps.getName();
        String newName = txfName;
        if (newName.compareTo(oldName) == 0) return;
        gps.setName(newName);
        tps.setGeneralParameterSet(gps);
        tournament.setTournamentParameterSet(tps);
        this.tournamentChanged();
    }//GEN-LAST:event_txfNameFocusLost

    private void txfNumberOfCategoriesFocusLost() {//GEN-FIRST:event_txfNumberOfCategoriesFocusLost
        TournamentParameterSet tps;
        tps = tournament.getTournamentParameterSet();
        GeneralParameterSet gps = tps.getGeneralParameterSet();

        int oldNbCategories = gps.getNumberOfCategories();
        int newNbCategories = -1;
        try {
            newNbCategories = Integer.parseInt(this.txfNumberOfCategories);
        } catch (NumberFormatException ex) {
            Logger.getLogger(TournamentOptions.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (newNbCategories <= 0 || newNbCategories > Gotha.MAX_NUMBER_OF_CATEGORIES){
            this.txfNumberOfCategories = "" + oldNbCategories;
        }
        else{
            if (newNbCategories == oldNbCategories) return;
            int[] oldLowerCategoryLimits = gps.getLowerCategoryLimits();
            int[] newLowerCategoryLimits = new int[newNbCategories - 1];
            for (int c = 0; c < newLowerCategoryLimits.length; c++ ){
                if (c < oldLowerCategoryLimits.length) newLowerCategoryLimits[c] = oldLowerCategoryLimits[c];
                else newLowerCategoryLimits[c] = -30;
            }
            gps.setNumberOfCategories(newNbCategories);
            gps.setLowerCategoryLimits(newLowerCategoryLimits);
            tps.setGeneralParameterSet(gps);
            tournament.setTournamentParameterSet(tps);
            this.tournamentChanged();
        }
    }//GEN-LAST:event_txfNumberOfCategoriesFocusLost

    private void tabTxfLowerLimitOfCatFocusLost(String evtSource) {
        TournamentParameterSet tps;
        tps = tournament.getTournamentParameterSet();
        GeneralParameterSet gps = tps.getGeneralParameterSet();

        for (int c = 0; c < this.tabTxfLowerLimitOfCat.length; c++){
            if (evtSource == tabTxfLowerLimitOfCat[c]){
                int newLowerLimit = Player.convertKDPToInt(tabTxfLowerLimitOfCat[c]);
                int oldLowerLimit = gps.getLowerCategoryLimits()[c];
                if (newLowerLimit < -30) {
                    this.updatePnlGen();
                    return;
                }
                if (newLowerLimit == oldLowerLimit) return;
                int[] newTabTxfLowerLimitOfCat = gps.getLowerCategoryLimits();
                newTabTxfLowerLimitOfCat[c] = newLowerLimit;
                for (int cc = 0; cc < c; cc++)
                    if (newTabTxfLowerLimitOfCat[cc] < newLowerLimit) newTabTxfLowerLimitOfCat[cc] = newLowerLimit;
                for (int cc = c; cc < newTabTxfLowerLimitOfCat.length; cc++)
                    if (newTabTxfLowerLimitOfCat[cc] > newLowerLimit) newTabTxfLowerLimitOfCat[cc] = newLowerLimit;
                gps.setLowerCategoryLimits(newTabTxfLowerLimitOfCat);
                tps.setGeneralParameterSet(gps);
                tournament.setTournamentParameterSet(tps);
                this.tournamentChanged();
            }
        }
    }

    private void updateCriteriaFromComboBoxes(){
        TournamentParameterSet tps;
        PlacementParameterSet pps;
        tps = tournament.getTournamentParameterSet();
        pps = tps.getPlacementParameterSet();
        int[] plaCrit = pps.getPlaCriteria();
        plaCrit[0] = PlacementParameterSet.criterionUID((String)cbxCrit1.getModel().getSelectedItem());
        plaCrit[1] = PlacementParameterSet.criterionUID((String)cbxCrit2.getModel().getSelectedItem());
        plaCrit[2] = PlacementParameterSet.criterionUID((String)cbxCrit3.getModel().getSelectedItem());
        plaCrit[3] = PlacementParameterSet.criterionUID((String)cbxCrit4.getModel().getSelectedItem());

        // Immediately filter double DC/SDC criteria, which is stritly forbidden
        int nbDirCrit = 0;
        for (int c = 0; c < plaCrit.length; c++){
            if (plaCrit[c] != PlacementParameterSet.PLA_CRIT_DC && plaCrit[c] != PlacementParameterSet.PLA_CRIT_SDC) continue;
            nbDirCrit ++;
            if (nbDirCrit > 1) plaCrit[c] = PlacementParameterSet.PLA_CRIT_NUL;
        }

        pps.setPlaCriteria(plaCrit);
        tps.setPlacementParameterSet(pps);

        String strCriteria = pps.checkCriteriaCoherence();
        this.txaWarning = strCriteria;
        tournament.setTournamentParameterSet(tps);
        this.tournamentChanged();
    }
    private void updateTeamCriteriaFromComboBoxes(){
        TeamTournamentParameterSet ttps;
        TeamPlacementParameterSet tpps;
        ttps = tournament.getTeamTournamentParameterSet();
        tpps = ttps.getTeamPlacementParameterSet();
        int[] plaCrit = tpps.getPlaCriteria();
        plaCrit[0] = TeamPlacementParameterSet.criterionUID((String)cbxTeamCrit1.getModel().getSelectedItem());
        plaCrit[1] = TeamPlacementParameterSet.criterionUID((String)cbxTeamCrit2.getModel().getSelectedItem());
        plaCrit[2] = TeamPlacementParameterSet.criterionUID((String)cbxTeamCrit3.getModel().getSelectedItem());
        plaCrit[3] = TeamPlacementParameterSet.criterionUID((String)cbxTeamCrit4.getModel().getSelectedItem());
        plaCrit[4] = TeamPlacementParameterSet.criterionUID((String)cbxTeamCrit5.getModel().getSelectedItem());
        plaCrit[5] = TeamPlacementParameterSet.criterionUID((String)cbxTeamCrit6.getModel().getSelectedItem());

        tpps.setPlaCriteria(plaCrit);
        ttps.setTeamPlacementParameterSet(tpps);



        tournament.setTeamTournamentParameterSet(ttps);
        this.tournamentChanged();
    }

    public void txfShortNameFocusLost() {//GEN-FIRST:event_txfShortNameFocusLost
        TournamentParameterSet tps;
        tps = tournament.getTournamentParameterSet();
        GeneralParameterSet gps = tps.getGeneralParameterSet();
        String oldShortName = gps.getShortName();
        String newShortName = txfShortName;
        newShortName = Gotha.eliminateForbiddenCharacters(newShortName);
        txfShortName = newShortName;
        if (newShortName.compareTo(oldShortName) == 0) return;
        gps.setShortName(newShortName);
        tps.setGeneralParameterSet(gps);
        tournament.setTournamentParameterSet(tps);
        tournament.setHasBeenSavedOnce(false);
        this.tournamentChanged();

    }//GEN-LAST:event_txfShortNameFocusLost

    public void rdbHdBaseMMSActionPerformed() {//GEN-FIRST:event_rdbHdBaseMMSActionPerformed
        this.updHdBase();
    }//GEN-LAST:event_rdbHdBaseMMSActionPerformed

    public void rdbHdBaseRankActionPerformed() {//GEN-FIRST:event_rdbHdBaseRankActionPerformed
        this.updHdBase();
    }//GEN-LAST:event_rdbHdBaseRankActionPerformed

    private void btnHelpPlacementActionPerformed() {//GEN-FIRST:event_btnHelpPlacementActionPerformed
        Gotha.displayGothaHelp("Placement settings");
    }//GEN-LAST:event_btnHelpPlacementActionPerformed

    private void btnHelpGeneralActionPerformed() {//GEN-FIRST:event_btnHelpGeneralActionPerformed
        Gotha.displayGothaHelp("General settings");
    }//GEN-LAST:event_btnHelpGeneralActionPerformed

    private void btnHelpHandicapActionPerformed() {//GEN-FIRST:event_btnHelpHandicapActionPerformed
        Gotha.displayGothaHelp("Handicap settings");
    }//GEN-LAST:event_btnHelpHandicapActionPerformed

    private void btnHelpPairingActionPerformed() {//GEN-FIRST:event_btnHelpPairingActionPerformed
        Gotha.displayGothaHelp("Pairing settings");
    }//GEN-LAST:event_btnHelpPairingActionPerformed

    private void ckbResetParametersActionPerformed() {//GEN-FIRST:event_ckbResetParametersActionPerformed
        boolean bResetSelected = this.ckbResetParameters;

        this.lblNewSystemEnabled = !bResetSelected;
        this.rdbMcMahonEnabled = !bResetSelected;
        this.rdbSwissEnabled = !bResetSelected;
        this.rdbSwissCatEnabled = !bResetSelected;
        this.lblRecommendedEnabled = !bResetSelected;

        if (bResetSelected){
            int tournamentType = TournamentParameterSet.TYPE_UNDEFINED;
            tournamentType = tournament.tournamentType();
            if (tournamentType == TournamentParameterSet.TYPE_MCMAHON) rdbMcMahon = true;
            if (tournamentType == TournamentParameterSet.TYPE_SWISS) rdbSwiss = true;
            if (tournamentType == TournamentParameterSet.TYPE_SWISSCAT) rdbSwissCat = true;
        }

    }//GEN-LAST:event_ckbResetParametersActionPerformed

    private void btnHelpTeamPlacementActionPerformed() {//GEN-FIRST:event_btnHelpTeamPlacementActionPerformed
        Gotha.displayGothaHelp("Team Placement settings");
    }//GEN-LAST:event_btnHelpTeamPlacementActionPerformed

    public void cbxCritFocusLost() {//GEN-FIRST:event_cbxCritFocusLost
        updateCriteriaFromComboBoxes();
    }//GEN-LAST:event_cbxCritFocusLost

    private void cbxTeamCritFocusLost() {//GEN-FIRST:event_cbxTeamCritFocusLost
        updateTeamCriteriaFromComboBoxes();
    }//GEN-LAST:event_cbxTeamCritFocusLost

    public void txfEndDateFocusLost() {//GEN-FIRST:event_txfEndDateFocusLost
        TournamentParameterSet tps;
        tps = tournament.getTournamentParameterSet();
        GeneralParameterSet gps = tps.getGeneralParameterSet();
        Date oldEndDate = gps.getEndDate();
        Date newEndDate;
        try {
            newEndDate = new SimpleDateFormat("yyyy-MM-dd").parse(this.txfEndDate);
        } catch (ParseException ex) {
            Logger.getLogger(TournamentOptions.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        if (newEndDate.equals(oldEndDate)) return;
        gps.setEndDate(newEndDate);

        tps.setGeneralParameterSet(gps);
        tournament.setTournamentParameterSet(tps);
        this.tournamentChanged();

    }//GEN-LAST:event_txfEndDateFocusLost

    public void txfDirectorFocusLost() {//GEN-FIRST:event_txfDirectorFocusLost
        TournamentParameterSet tps;
        tps = tournament.getTournamentParameterSet();
        GeneralParameterSet gps = tps.getGeneralParameterSet();
        String oldDirector = gps.getDirector();
        String newDirector = txfDirector;
        if (newDirector.compareTo(oldDirector) == 0) return;
        gps.setDirector(newDirector);
        tps.setGeneralParameterSet(gps);
        tournament.setTournamentParameterSet(tps);
        this.tournamentChanged();
    }//GEN-LAST:event_txfDirectorFocusLost

    public void rdbHdCorrectionActionPerformed() {//GEN-FIRST:event_rdbHdCorrectionActionPerformed
        updHdCorrection();
    }//GEN-LAST:event_rdbHdCorrectionActionPerformed

    public void ckbRoundDownFocusLost() {//GEN-FIRST:event_ckbRoundDownFocusLost
        TournamentParameterSet tps;
        GeneralParameterSet gps;
        tps = tournament.getTournamentParameterSet();
        gps = tps.getGeneralParameterSet();

        boolean bSomethingHasChanged = false;
        boolean newRoundDown = this.ckbRoundDown;
        if (newRoundDown != gps.isGenRoundDownNBWMMS()){
            gps.setGenRoundDownNBWMMS(newRoundDown);
            bSomethingHasChanged = true;
        }

        if (bSomethingHasChanged){
            tournament.setTournamentParameterSet(tps);
            this.tournamentChanged();
        }

    }//GEN-LAST:event_ckbRoundDownFocusLost

    public void ckbSeBarThresholdActiveFocusLost() {//GEN-FIRST:event_ckbSeBarThresholdActiveFocusLost
        secCritControlsFocusLost();
    }//GEN-LAST:event_ckbSeBarThresholdActiveFocusLost

    public void ckbCompensateFocusLost() {//GEN-FIRST:event_ckbCompensateFocusLost
        TournamentParameterSet tps;
        PairingParameterSet paiPS;
        tps = tournament.getTournamentParameterSet();
        paiPS = tps.getPairingParameterSet();

        boolean bSomethingHasChanged = false;
        boolean oldCompensateDUDD = paiPS.isPaiMaCompensateDUDD();
        boolean newCompensateDUDD = this.ckbCompensate;
        if (newCompensateDUDD != oldCompensateDUDD){
            paiPS.setPaiMaCompensateDUDD(newCompensateDUDD);
            bSomethingHasChanged = true;
        }
        if (bSomethingHasChanged){
            tournament.setTournamentParameterSet(tps);
            this.tournamentChanged();
        }

    }//GEN-LAST:event_ckbCompensateFocusLost

    public void txfMMZeroFocusLost() {//GEN-FIRST:event_txfMMZeroFocusLost
        TournamentParameterSet tps;
        GeneralParameterSet gps;
        tps = tournament.getTournamentParameterSet();
        gps = tps.getGeneralParameterSet();
        int oldGenMMZero = gps.getGenMMZero();
        int newGenMMZero = Player.convertKDPToInt(this.txfMMZero);
        txfMMZero = "" + Player.convertIntToKD(newGenMMZero);
        if (newGenMMZero > GeneralParameterSet.GEN_MM_ZERO_MAX
                || newGenMMZero < GeneralParameterSet.GEN_MM_ZERO_MIN){
            // Error. Keep old value
            txfMMZero = "" + Player.convertIntToKD(oldGenMMZero);
            return;
        }
        if (newGenMMZero != oldGenMMZero){
            gps.setGenMMZero(newGenMMZero);
            tournament.setTournamentParameterSet(tps);
            this.tournamentChanged();
        }
    }//GEN-LAST:event_txfMMZeroFocusLost

    private void btnEditClubsGroupsActionPerformed() {//GEN-FIRST:event_btnEditClubsGroupsActionPerformed

    }//GEN-LAST:event_btnEditClubsGroupsActionPerformed

    private void lstClubsGroupsValueChanged() {//GEN-FIRST:event_lstClubsGroupsValueChanged
        this.lstClubsInSelectedGroup.removeAll();
        updateClubsInSelectedGroup();
    }//GEN-LAST:event_lstClubsGroupsValueChanged

    private void btnAddGroupActionPerformed(String groupName) {//GEN-FIRST:event_btnAddGroupActionPerformed
        if (groupName.length() < 1) return;
        ClubsGroup clubsGroup = tournament.getClubsGroupByName(groupName);
        if (clubsGroup != null){
            sendMessage(groupName + " group already exits");
            return;
        }
        tournament.addClubsGroup(new ClubsGroup(groupName));
        this.tournamentChanged();

    }//GEN-LAST:event_btnAddGroupActionPerformed

    private void btnRemoveGroupActionPerformed() {//GEN-FIRST:event_btnRemoveGroupActionPerformed
        Object sel = lstClubsGroups.getSelectedValue();
        if (sel == null) return;
        String strGroup = (String)sel;
        tournament.removeClubsGroup(new ClubsGroup(strGroup));
        lstClubsGroups.clearSelection();
        this.tournamentChanged();

    }//GEN-LAST:event_btnRemoveGroupActionPerformed

    private void btnAddClubActionPerformed(String clubName) {//GEN-FIRST:event_btnAddClubActionPerformed
        String groupName = "";
        Object sel = lstClubsGroups.getSelectedValue();
        if (sel == null){
            sendMessage("Please select a Clubs group");
            return;
        }
        groupName = (String)sel;

        if (clubName == null) {
            return;
        }
        if (clubName.length() < 1){
            return;
        }
        tournament.addClubToClubsGroup(groupName, clubName);
        this.tournamentChanged();
    }//GEN-LAST:event_btnAddClubActionPerformed

    private void btnRemoveClubActionPerformed() {//GEN-FIRST:event_btnRemoveClubActionPerformed
        Object selGroup = this.lstClubsGroups.getSelectedValue();
        if (selGroup == null) return;
        String groupName = (String)selGroup;
        Object selClub = this.lstClubsInSelectedGroup.getSelectedValue();
        if (selClub == null) return;
        String clubName = (String)selClub;
        ClubsGroup cg = tournament.getClubsGroupByName(groupName);
        tournament.removeClubFromClubsGroup(groupName, clubName);
        this.tournamentChanged();
    }//GEN-LAST:event_btnRemoveClubActionPerformed

    private void updHdBase(){
        TournamentParameterSet tps;
        tps = tournament.getTournamentParameterSet();
        HandicapParameterSet hps = tps.getHandicapParameterSet();
        boolean oldHdBase = hps.isHdBasedOnMMS();
        boolean newHdBase = true;
        if (rdbHdBaseRank) newHdBase = false;
        if (newHdBase == oldHdBase) return;
        hps.setHdBasedOnMMS(newHdBase);
        tps.setHandicapParameterSet(hps);
        tournament.setTournamentParameterSet(tps);
        this.tournamentChanged();
    }

    private void customInitComponents() {
        initPnlGen();
        initPnlHan();
        initPnlPla();
        initPnlPai();
        initPnlTPl();
    }

    private void initPnlGen() {
        if (Gotha.runningMode == Gotha.RUNNING_MODE_CLI) this.txfShortNameEditable = false;
        updatePnlGen();
    }
    private void initPnlHan() {
        updatePnlHan();
    }
    private void initPnlPla() {
        // Fill criterion combo boxes
        String[] strCritLN = PlacementParameterSet.criteriaLongNames();
        cbxCrit1.setModel(new DefaultComboBoxModel(strCritLN));
        cbxCrit2.setModel(new DefaultComboBoxModel(strCritLN));
        cbxCrit3.setModel(new DefaultComboBoxModel(strCritLN));
        cbxCrit4.setModel(new DefaultComboBoxModel(strCritLN));

        // For any type of tournament crit 1 should not be modified
        cbxCrit1Enabled = false;
        // And for SwissCat Tournaments, crit 2 should not be modified
        if (tournament.tournamentType() == TournamentParameterSet.TYPE_SWISSCAT)
            cbxCrit2Enabled = false;

        // Fill Criterion Glossary JTable

        DefaultTableModel model = tblGlossary;
        while (model.getRowCount() > 0) model.removeRow(0);

        for (int iPCrit = 0; iPCrit < PlacementParameterSet.allPlacementCriteria.length; iPCrit++){
            PlacementCriterion pCrit = PlacementParameterSet.allPlacementCriteria[iPCrit];
            Vector<String> row = new Vector<String>();
            row.add("" + pCrit.longName);
            row.add("" + pCrit.shortName);
            row.add("" + pCrit.description);

            model.addRow(row);
        }

        updatePnlPla();
    }

    private void initPnlTPl() {
        // Fill criterion combo boxes
        String[] strCritLN = TeamPlacementParameterSet.criteriaLongNames();

        cbxTeamCrit1.setModel(new DefaultComboBoxModel(strCritLN));
        cbxTeamCrit2.setModel(new DefaultComboBoxModel(strCritLN));
        cbxTeamCrit3.setModel(new DefaultComboBoxModel(strCritLN));
        cbxTeamCrit4.setModel(new DefaultComboBoxModel(strCritLN));
        cbxTeamCrit5.setModel(new DefaultComboBoxModel(strCritLN));
        cbxTeamCrit6.setModel(new DefaultComboBoxModel(strCritLN));

        // For any type of tournament crit 1 should not be modified
        cbxTeamCrit1Enabled = false;

        // Fill Criterion Glossary JTable

        DefaultTableModel model = tblTeamGlossaryModel;
        while (model.getRowCount() > 0) model.removeRow(0);

        for (int iPCrit = 0; iPCrit < TeamPlacementParameterSet.allPlacementCriteria.length; iPCrit++){
            PlacementCriterion pCrit = TeamPlacementParameterSet.allPlacementCriteria[iPCrit];
            Vector<String> row = new Vector<String>();
            row.add("" + pCrit.longName);
            row.add("" + pCrit.shortName);
            row.add("" + pCrit.description);


            model.addRow(row);
        }

        updatePnlTPl();
    }

    private void initPnlPai() {
        updatePnlPai();
    }

    private void updatePnlGen() {
        GeneralParameterSet gps = tournament.getTournamentParameterSet().getGeneralParameterSet();
        int tournamentType = tournament.tournamentType();
        if (tournamentType == TournamentParameterSet.TYPE_UNDEFINED){
            this.pnlTournamentDetailsVisible = true;
            this.lblSystemNameVisible = false;
            this.pnlCategoriesVisible = false;
            this.pnlMcMahonVisible = false;
        }
        if (tournamentType == TournamentParameterSet.TYPE_MCMAHON){
            this.pnlTournamentDetailsVisible = true;
            this.lblSystemNameVisible = true;
            this.lblSystemName = "McMahon system";
            this.pnlCategoriesVisible = false;
            this.pnlMcMahonVisible = true;
        }
        if (tournamentType == TournamentParameterSet.TYPE_SWISS){
            this.pnlTournamentDetailsVisible = true;
            this.lblSystemNameVisible = true;
            this.lblSystemName = "Swiss system";
            this.pnlCategoriesVisible = false;
            this.pnlMcMahonVisible = false;
        }
        if (tournamentType == TournamentParameterSet.TYPE_SWISSCAT){
            this.pnlTournamentDetailsVisible = true;
            this.lblSystemNameVisible = true;
            this.lblSystemName = "SwissCat system";
            this.pnlCategoriesVisible = true;
            this.pnlMcMahonVisible = false;
        }

        // Identification Panel
        this.txfShortName = gps.getShortName();
        this.txfName = gps.getName();
        this.txfLocation = gps.getLocation();
        this.txfDirector = gps.getDirector();
        this.txfBeginDate = (new SimpleDateFormat("yyyy-MM-dd")).format(gps.getBeginDate());
        this.txfEndDate = (new SimpleDateFormat("yyyy-MM-dd")).format(gps.getEndDate());
        this.txfNumberOfRounds = "" + gps.getNumberOfRounds();

        // Categories Panel
        if (this.pnlCategoriesVisible){
            int nbCat = gps.getNumberOfCategories();
            this.txfNumberOfCategories = "" + nbCat;
            if (tabLblCat != null){
                for (int c = 0; c < tabLblCat.length; c++){
                    pnlCategories.remove(tabLblCat[c]);
                }
            }
            if (tabTxfLowerLimitOfCat != null){
                for (int c = 0; c < tabTxfLowerLimitOfCat.length; c++){
                    pnlCategories.remove(tabTxfLowerLimitOfCat[c]);
                }
            }
            if (tabLblNbPlayersOfCat != null){
                for (int c = 0; c < tabLblNbPlayersOfCat.length; c++){
                    pnlCategories.remove(tabLblNbPlayersOfCat[c]);
                }
            }

            // Dimensions for pnlCategories and its components
            int tpgcPnlWidth = 220;
            int tpgcPnlHeadHeight = 120;
            int tpgcPnlBottomHeight = 30;

            int tpgcLblLeft = 30;
            int tpgcLblTop = tpgcPnlHeadHeight;
            int tpgcLblHeight = 20;
            int tpgcLblWidth = 80;

            int tpgcTxfLeft = tpgcLblLeft + tpgcLblWidth + 10;
            int tpgcTxfWidth = 30;

            int tpgcNbPLeft = tpgcTxfLeft + tpgcTxfWidth + 10;
            int tpgcNbPWidth = 30;

            tabLblCat = new String[nbCat];
            tabTxfLowerLimitOfCat = new String[nbCat];
            tabLblNbPlayersOfCat = new String[nbCat];
            for (int c = 0; c < nbCat; c++){
                // Category c Labels
                tabLblCat[c] = "Category" + (c + 1);
//                tabLblCat[c].setBounds(30, 120 + 20 * c, 80, 20);
                pnlCategories.add(tabLblCat[c]);
                // Lower limits Text fields
                int niv = (c < nbCat -1) ? gps.getLowerCategoryLimits()[c] : -30;
                String strNiv = Player.convertIntToKD(niv);
                tabTxfLowerLimitOfCat[c] = strNiv;
//                tabTxfLowerLimitOfCat[c].setBounds(120, 120 + 20 * c, 30, 20);
                pnlCategories.add(tabTxfLowerLimitOfCat[c]);
                // number of players in the category Text Field
                int nbPl = tournament.numberOfPlayersStrongerOrEqualTo(niv);
                if (c > 0) nbPl -= tournament.numberOfPlayersStrongerOrEqualTo(gps.getLowerCategoryLimits()[c-1]);
                tabLblNbPlayersOfCat[c] = "" + nbPl;
//                tabLblNbPlayersOfCat[c].setBounds(160, 120 + 20 * c, 30, 20);
                pnlCategories.add(tabLblNbPlayersOfCat[c]);
            }
        }
        // McMahon Panel
        if (this.pnlMcMahonVisible){
            this.txfMMBar = Player.convertIntToKD(gps.getGenMMBar());
            this.txfMMFloor = Player.convertIntToKD(gps.getGenMMFloor());
            this.txfMMZero = Player.convertIntToKD(gps.getGenMMZero());
        }

        // Special results panel
        switch(gps.getGenMMS2ValueAbsent()){
            case 0 : this.rdbAbsentMMS0 = true; break;
            case 1 : this.rdbAbsentMMS1 = true; break;
            case 2 : this.rdbAbsentMMS2 = true; break;
        }
        switch(gps.getGenNBW2ValueAbsent()){
            case 0 : this.rdbAbsentNBW0 = true; break;
            case 1 : this.rdbAbsentNBW1 = true; break;
            case 2 : this.rdbAbsentNBW2 = true; break;
        }
        switch(gps.getGenMMS2ValueBye()){
            case 0 : this.rdbByeMMS0 = true; break;
            case 1 : this.rdbByeMMS1 = true; break;
            case 2 : this.rdbByeMMS2 = true; break;
        }
        switch(gps.getGenNBW2ValueBye()){
            case 0 : this.rdbByeNBW0 = true; break;
            case 1 : this.rdbByeNBW1 = true; break;
            case 2 : this.rdbByeNBW2 = true; break;
        }

        this.ckbRoundDown = gps.isGenRoundDownNBWMMS();
    }

    private void updatePnlHan() {
        int tournamentType = tournament.tournamentType();
        if (tournamentType == TournamentParameterSet.TYPE_UNDEFINED){
            this.lblHandicap = "No Handicap";
            this.pnlHandicapVisible = true;
        }
        if (tournamentType == TournamentParameterSet.TYPE_MCMAHON){
            this.lblHandicap = "";
            this.pnlHandicapVisible = true;
        }
        if (tournamentType == TournamentParameterSet.TYPE_SWISS){
            this.lblHandicap = "No Handicap";
            this.pnlHandicapVisible = false;
        }
        if (tournamentType == TournamentParameterSet.TYPE_SWISSCAT){
            this.lblHandicap = "";
            this.pnlHandicapVisible = true;
        }
        if (pnlHandicapVisible){
            HandicapParameterSet hps = tournament.getTournamentParameterSet().getHandicapParameterSet();
            this.txfNoHdRankThreshold = Player.convertIntToKD(hps.getHdNoHdRankThreshold());
            if(hps.isHdBasedOnMMS()) this.rdbHdBaseMMS = true;
            else this.rdbHdBaseRank = true;
            switch(hps.getHdCorrection()){
                case 0 : this.rdbHdCorrection0 = true; break;
                case 1 : this.rdbHdCorrection1 = true; break;
                case 2 : this.rdbHdCorrection2 = true; break;
                case 3 : this.rdbHdCorrection3 = true; break;
                case -1: this.rdbHdCorrectionPlus1 = true; break;
            }
            this.txfHdCeiling = "" + hps.getHdCeiling();
        }
    }

    private void updatePnlPla() {
        PlacementParameterSet pps = tournament.getTournamentParameterSet().getPlacementParameterSet();
        GeneralParameterSet gps = tournament.getTournamentParameterSet().getGeneralParameterSet();
        // update contents of combo boxes
        int[] displayedCriteria = pps.getPlaCriteria();
        this.cbxCrit1.getModel().setSelectedItem(PlacementParameterSet.criterionLongName(displayedCriteria[0]));
        this.cbxCrit2.getModel().setSelectedItem(PlacementParameterSet.criterionLongName(displayedCriteria[1]));
        this.cbxCrit3.getModel().setSelectedItem(PlacementParameterSet.criterionLongName(displayedCriteria[2]));
        this.cbxCrit4.getModel().setSelectedItem(PlacementParameterSet.criterionLongName(displayedCriteria[3]));

        // update of McMahon bar and floor JTextField
        int bar = gps.getGenMMBar();
        int floor = gps.getGenMMFloor();
        int zero = gps.getGenMMZero();
        this.txfMMBar = "" + Player.convertIntToKD(bar);
        this.txfMMFloor = "" + Player.convertIntToKD(floor);
        this.txfMMZero = "" + Player.convertIntToKD(zero);
    }

    private void updatePnlTPl() {
        TeamPlacementParameterSet tpps = tournament.getTeamTournamentParameterSet().getTeamPlacementParameterSet();
        // update contents of combo boxes
        int[] displayedCriteria = tpps.getPlaCriteria();
        this.cbxTeamCrit1.getModel().setSelectedItem(TeamPlacementParameterSet.criterionLongName(displayedCriteria[0]));
        this.cbxTeamCrit2.getModel().setSelectedItem(TeamPlacementParameterSet.criterionLongName(displayedCriteria[1]));
        this.cbxTeamCrit3.getModel().setSelectedItem(TeamPlacementParameterSet.criterionLongName(displayedCriteria[2]));
        this.cbxTeamCrit4.getModel().setSelectedItem(TeamPlacementParameterSet.criterionLongName(displayedCriteria[3]));
        this.cbxTeamCrit5.getModel().setSelectedItem(TeamPlacementParameterSet.criterionLongName(displayedCriteria[4]));
        this.cbxTeamCrit6.getModel().setSelectedItem(TeamPlacementParameterSet.criterionLongName(displayedCriteria[5]));
    }

    private void updatePnlPai() {
        if (tournament.tournamentType() == TournamentParameterSet.TYPE_SWISSCAT)
            this.ckbSeMinimizeHandicapVisible = true;
        else
            this.ckbSeMinimizeHandicapVisible = false;

        if (tournament.tournamentType() == TournamentParameterSet.TYPE_MCMAHON)
            this.ckbSeBarThresholdActiveVisible = true;
        else
            this.ckbSeBarThresholdActiveVisible = false;

        PairingParameterSet paiPS = tournament.getTournamentParameterSet().getPairingParameterSet();

        this.ckbAvoidMixingCategories = paiPS.getPaiMaAvoidMixingCategories() != 0;
        this.ckbMinimizeScoreDifference = paiPS.getPaiMaMinimizeScoreDifference() != 0;
        this.txfLastRoundForSeedSystem1 = "" + (paiPS.getPaiMaLastRoundForSeedSystem1() + 1);
        this.ckbAddSortOnRating = 
                paiPS.getPaiMaAdditionalPlacementCritSystem1() == PlacementParameterSet.PLA_CRIT_RATING;

        if (paiPS.getPaiMaSeedSystem1() == PairingParameterSet.PAIMA_SEED_SPLITANDRANDOM)
            this.rdbFormerSplitAndRandom = true;
        if (paiPS.getPaiMaSeedSystem1() == PairingParameterSet.PAIMA_SEED_SPLITANDFOLD)
            this.rdbFormerSplitAndFold = true;
        if (paiPS.getPaiMaSeedSystem1() == PairingParameterSet.PAIMA_SEED_SPLITANDSLIP)
            this.rdbFormerSplitAndSlip = true;
        if (paiPS.getPaiMaSeedSystem2() == PairingParameterSet.PAIMA_SEED_SPLITANDRANDOM)
            this.rdbLatterSplitAndRandom = true;
        if (paiPS.getPaiMaSeedSystem2() == PairingParameterSet.PAIMA_SEED_SPLITANDFOLD)
            this.rdbLatterSplitAndFold = true;
        if (paiPS.getPaiMaSeedSystem2() == PairingParameterSet.PAIMA_SEED_SPLITANDSLIP)
            this.rdbLatterSplitAndSlip = true;

        this.ckbCompensate = paiPS.isPaiMaCompensateDUDD();
        if (paiPS.getPaiMaDUDDUpperMode() == PairingParameterSet.PAIMA_DUDD_TOP)
            this.rdbDUDDUGTop = true;
        if (paiPS.getPaiMaDUDDUpperMode() == PairingParameterSet.PAIMA_DUDD_MID)
            this.rdbDUDDUGMid = true;
        if (paiPS.getPaiMaDUDDUpperMode() == PairingParameterSet.PAIMA_DUDD_BOT)
            this.rdbDUDDUGBot = true;
        if (paiPS.getPaiMaDUDDLowerMode() == PairingParameterSet.PAIMA_DUDD_TOP)
            this.rdbDUDDLGTop = true;
        if (paiPS.getPaiMaDUDDLowerMode() == PairingParameterSet.PAIMA_DUDD_MID)
            this.rdbDUDDLGMid = true;
        if (paiPS.getPaiMaDUDDLowerMode() == PairingParameterSet.PAIMA_DUDD_BOT)
            this.rdbDUDDLGBot = true;

        this.ckbAvoidPairingSamePair = paiPS.getPaiBaAvoidDuplGame() != 0;

        if (paiPS.getPaiBaRandom() == 0){
            this.rdbNoRandom = true;
            this.ckbDeterministic = false;
            this.ckbDeterministicEnabled = false;
        }
        else{
            this.rdbAcceptRandom = true;
            this.ckbDeterministic = paiPS.isPaiBaDeterministic();
            this.ckbDeterministicEnabled = true;
        }

        this.ckbBalanceWB = paiPS.getPaiBaBalanceWB() != 0;

        this.txfSeRankThreshold = Player.convertIntToKD(paiPS.getPaiSeRankThreshold());
        this.ckbSeBarThresholdActive = paiPS.isPaiSeBarThresholdActive();
        this.ckbSeNbWinsThresholdActive = paiPS.isPaiSeNbWinsThresholdActive();
        this.txfSeCountry = "" + paiPS.getPaiSePreferMMSDiffRatherThanSameCountry();
        this.txfSeClubsGroup = "" + paiPS.getPaiSePreferMMSDiffRatherThanSameClubsGroup();
        this.txfSeClub = "" + paiPS.getPaiSePreferMMSDiffRatherThanSameClub();
        this.ckbSeMinimizeHandicap = paiPS.getPaiSeMinimizeHandicap() != 0;
    }

    private void updateDlgClubsGroups() {
        // Clubs groups
        // If a group is selected, keep the selection
        String strSelGroup = "";
        Object sel = lstClubsGroups.getSelectedValue();
        if (sel != null)  strSelGroup = (String)sel;

        ArrayList<ClubsGroup> alClubsGroups = tournament.clubsGroupsList();
        if (alClubsGroups == null) return;
        ArrayList<String> alStrCG = new ArrayList<String>();
        for(int i = 0; i < alClubsGroups.size(); i++){
            alStrCG.add(alClubsGroups.get(i).getName());
        }
        Collections.sort(alStrCG);
        String[] strCG = new String[alStrCG.size()];

        for(int i = 0; i < strCG.length; i++){
            strCG[i] = alStrCG.get(i);
        }
        this.lstClubsGroups.removeAll();
        this.lstClubsGroups.setListData(strCG);

        // Restore selection
        this.lstClubsGroups.clearSelection();
        if (strSelGroup.length() > 0){
            ListModel lm = lstClubsGroups.getModel();
            for (int i = 0; i < lm.getSize(); i++){
                String cg = (String)lm.getElementAt(i);
                if (cg.equals(strSelGroup)){
                    lstClubsGroups.setSelectedIndex(i);
                }
            }
        }
        this.updateClubsInSelectedGroup();

        // Known clubs 
        HashMap<String,String> hmClubs = new HashMap<String, String>();
        // Clubs from Rating lists
        ClubsList cl =  new ClubsList();
        RatingList rlEGF = new RatingList(RatingList.TYPE_EGF, new File(Gotha.runningDirectory, "ratinglists/egf_db.txt"));
        cl.importClubsFromRatingList(rlEGF);
//        RatingList rlFFG = new RatingList(RatingList.TYPE_FFG, new File(Gotha.runningDirectory, "ratinglists/ech_ffg_new.txt"));
        RatingList rlFFG = new RatingList(RatingList.TYPE_FFG, new File(Gotha.runningDirectory, "ratinglists/ech_ffg_V3.txt"));
        cl.importClubsFromRatingList(rlFFG);
        RatingList rlAGA = new RatingList(RatingList.TYPE_AGA, new File(Gotha.runningDirectory, "ratinglists/tdlista.txt"));
        cl.importClubsFromRatingList(rlAGA);
        for(Club club : cl.getHmClubs().values()){
            hmClubs.put(club.getName(), club.getName());
        }

        // Clubs from players in the tournament
        ArrayList<Player> alP = tournament.playersList();
        for (Player p : alP){
            hmClubs.put(p.getClub(), p.getClub());
        }
        ArrayList<String> alClubs = new ArrayList<String>(hmClubs.values());

        Collections.sort(alClubs);
        String[] strClubs = new String[alClubs.size()];
        this.lstClubs.removeAll();

        for(int i = 0; i < strClubs.length; i++){
            strClubs[i] = alClubs.get(i);
        }

        this.lstClubs.setListData(strClubs);
    }

    private void updateClubsInSelectedGroup() {
        lstClubsInSelectedGroup.setListData(new String[0]); // clear 
        Object sel = lstClubsGroups.getSelectedValue();
        if (sel == null) return;
        String strSelectedGroup = (String)sel;
        ClubsGroup clubsGroup = tournament.getClubsGroupByName(strSelectedGroup);
        ArrayList<Club> alClubs = new ArrayList<Club>(clubsGroup.getHmClubs().values());
        ArrayList<String> alStrClubs = new ArrayList<String>();
        for(Club c : alClubs){
            alStrClubs.add(c.getName());
        }
        Collections.sort(alStrClubs);
        String[] strClubs = new String[alClubs.size()];
        for(int i = 0; i < strClubs.length; i++){
            strClubs[i] = alStrClubs.get(i);
        }

        this.lstClubsInSelectedGroup.removeAll();
        this.lstClubsInSelectedGroup.setListData(strClubs);
    }


    private void tournamentChanged(){
        tournament.setLastTournamentModificationTime(tournament.getCurrentTournamentTime());
        updateAllViews();
    }

    private void updateAllViews(){
        this.tpnParametersVisible = true;
        if (!tournament.isOpen()) {
            sendMessage("No tournament opened");
        }
        this.lastComponentsUpdateTime = tournament.getCurrentTournamentTime();
        updateTitle("Tournament settings. " + tournament.getFullName());
        updatePnlGen();
        updatePnlHan();
        updatePnlPla();
        updatePnlPai();
        updatePnlTPl();
    }

    public TournamentInterface getTournament() {
        return tournament;
    }

    private void displayChangeSystem() {
        for (OnTournamentOptionsListener callback : mCallbacks) {
            callback.onDisplayChangeSystem();
        }
    }

    private void updateTitle(String title) {
        for (OnTournamentOptionsListener callback : mCallbacks) {
            callback.onTitleUpdate(title);
        }
    }

    private void sendMessage(String message) {
        for (OnTournamentOptionsListener callback : mCallbacks) {
            callback.onMessage(message);
        }
    }
}