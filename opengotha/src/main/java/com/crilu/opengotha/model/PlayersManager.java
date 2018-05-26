package com.crilu.opengotha.model;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.crilu.opengotha.CountriesList;
import com.crilu.opengotha.Country;
import com.crilu.opengotha.Gotha;
import com.crilu.opengotha.Gotha.DownloadProgressListener;
import com.crilu.opengotha.Player;
import com.crilu.opengotha.PlayerComparator;
import com.crilu.opengotha.PlayerException;
import com.crilu.opengotha.RatedPlayer;
import com.crilu.opengotha.RatingList;
import com.crilu.opengotha.Tournament.OnTournamentChangeListener;
import com.crilu.opengotha.TournamentException;
import com.crilu.opengotha.TournamentInterface;
import com.crilu.opengotha.TournamentPrinting;

public class PlayersManager {

    private static final long REFRESH_DELAY = 2000;
    private long lastComponentsUpdateTime = 0;
    private int playersSortType = PlayerComparator.NAME_ORDER;
    private final static int PLAYER_MODE_NEW = 1;
    private final static int PLAYER_MODE_MODIF = 2;
    private int playerMode = PLAYER_MODE_NEW;
    private Player playerInModification = null;
    private static final int REG_COL = 0;
    private static final int NAME_COL = 1;
    private static final int FIRSTNAME_COL = 2;
    private static final int COUNTRY_COL = 3;
    private static final int CLUB_COL = 4;
    private static final int RANK_COL = 5;
    private static final int RATING_COL = 6;
    private static final int GRADE_COL = 7;
    /**  current Tournament */
    private TournamentInterface tournament;
    /** Rating List */
    private RatingList ratingList = new RatingList();

    private String[] registeredPlayersHeaders = new String [] {
            "R", "Last name", "First name", "Co", "Club", "Rk", "Rating", "EGF Grade"
        };
    private List<String[]> registeredPlayers = new ArrayList<>();
    private boolean[] tabCkbParticipation;
    private List<String> cbxCountry = new ArrayList<>();
    private boolean ckbRatingList;
    private boolean rdbEGF;
    private boolean rdbFFG;
    private boolean rdbAGA;
    
	private List<String> cbxRatingList = new ArrayList<>();
	private boolean txfPlayerNameChoice;
	private Vector<String> lstPlayerNameChoice;
	private boolean rdbFirstCharacters;
	
	private String txfNbPlPre, txfNbPlFin;

    private OnUpdateRatingListListener ratingListListener;
    private OnTournamentChangeListener tournamentChangeListener;
    private OnPlayerRegistrationListener playerRegistrationListener;
    private DownloadProgressListener progressListener;

	interface OnUpdateRatingListListener {
		void updateMessage(String message);
		void onError(String message);
	}
    
	interface OnPlayerRegistrationListener {
		void onError(String message);
	}
    
    PlayersManager(TournamentInterface tournament)  {
//      LogElements.incrementElement("players.manager", "");
      this.tournament = tournament;

      customInitComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * Unlike initComponents, customInitComponents is editable
     */
    private void customInitComponents() {
        tabCkbParticipation = new boolean[Gotha.MAX_NUMBER_OF_ROUNDS];

        initCountriesList();
        initRatingListControls();
        initPnlRegisteredPlayers();
    }
    
	private void initCountriesList(){
        File f = new File(Gotha.runningDirectory, "documents/iso_3166-1_list_en.xml");
        if (!f.exists()) {
            System.out.println("Country list file not found");
            return;
        }
        ArrayList<Country> alCountries = CountriesList.importCountriesFromXMLFile(f);
        for(Country c : alCountries){
            cbxCountry.add(c.getAlpha2Code());
        }
    }
    
    private void initRatingListControls(){
        // Use the preferred rating list as in Preferences
        Preferences prefs = Preferences.userRoot().node(Gotha.strPreferences + "/playersmanager");
        String defRL = prefs.get("defaultratinglist", "" );
        int rlType = RatingList.TYPE_UNDEFINED;
        try{
            rlType = Integer.parseInt(defRL);
        }catch(Exception e){
            rlType = RatingList.TYPE_UNDEFINED;
        }
        this.ckbRatingList = true;
        switch(rlType){
            case RatingList.TYPE_UNDEFINED :
                this.ckbRatingList = false; break;
            case RatingList.TYPE_EGF :
                this.rdbEGF = true; break;
            case RatingList.TYPE_FFG :
                this.rdbFFG = true; break;
            case RatingList.TYPE_AGA :
                this.rdbAGA = true; 
        }     

        this.resetRatingListControls();
    }
    
    private void resetRatingListControls() {                
        boolean bRL = this.ckbRatingList;
               
        int rlType = RatingList.TYPE_UNDEFINED;
        if (bRL){
            if (this.rdbEGF) rlType = RatingList.TYPE_EGF;
            if (this.rdbFFG) rlType = RatingList.TYPE_FFG;
            if (this.rdbAGA) rlType = RatingList.TYPE_AGA;
        }
        // Save current rating list into Preferences
        Preferences prefs = Preferences.userRoot().node(Gotha.strPreferences + "/playersmanager");
        prefs.put("defaultratinglist", "" + rlType);

        this.useRatingList(rlType);
                
        switch(rlType){
            case RatingList.TYPE_EGF :
            	if (ratingListListener != null) {
            		ratingListListener.updateMessage("Update EGF rating list from ...");
            	}
                break;
            case RatingList.TYPE_FFG :
            	if (ratingListListener != null) {
            		ratingListListener.updateMessage("Update FFG rating list from ...");
            	}
                break;
            case RatingList.TYPE_AGA :
            	if (ratingListListener != null) {
            		ratingListListener.updateMessage("Update AGA rating list from ...");
            	}
                break;
            default :
            	if (ratingListListener != null) {
            		ratingListListener.updateMessage("Update rating list");
            	}
        }        
    }
    
    private void useRatingList(int typeRatingList) {
        switch (typeRatingList) {
            case RatingList.TYPE_EGF:
            	updateRatingListLabel("Searching for EGF rating list");
                ratingList = new RatingList(RatingList.TYPE_EGF, new File(Gotha.runningDirectory, "ratinglists/egf_db.txt"));
                break;
            case RatingList.TYPE_FFG:
            	updateRatingListLabel("Searching for FFG rating list");
//                ratingList = new RatingList(RatingList.TYPE_FFG, new File(Gotha.runningDirectory, "ratinglists/ech_ffg_new.txt"));
                ratingList = new RatingList(RatingList.TYPE_FFG, new File(Gotha.runningDirectory, "ratinglists/ech_ffg_V3.txt"));
                break;
            case RatingList.TYPE_AGA:
            	updateRatingListLabel("Searching for AGA rating list");
                ratingList = new RatingList(RatingList.TYPE_AGA, new File(Gotha.runningDirectory, "ratinglists/tdlista.txt"));
                break;
            default:
                ratingList = new RatingList();
        }
        int nbPlayersInRL = ratingList.getALRatedPlayers().size();
        for (RatedPlayer rP : ratingList.getALRatedPlayers()) {
            cbxRatingList.add(this.ratingList.getRatedPlayerString(rP));        
            
        }
        if (nbPlayersInRL == 0) {
            ratingList.setRatingListType(RatingList.TYPE_UNDEFINED);
            updateRatingListLabel("No rating list has been loaded yet");
        } else {
            String strType = "";

            switch (ratingList.getRatingListType()) {
                case RatingList.TYPE_EGF:
                    strType = "EGF rating list";
                    break;
                case RatingList.TYPE_FFG:
                    strType = "FFG rating list";
                    break;
                case RatingList.TYPE_AGA:
                    strType = "AGA rating list";
                    break;
            }
            updateRatingListLabel(strType + " " + ratingList.getStrPublicationDate() + " " + nbPlayersInRL + " players");
        }

    }
    
    private void initPnlRegisteredPlayers() {
    	updatePnlRegisteredPlayers(tournament.playersList());
	}

    private void updatePnlRegisteredPlayers(ArrayList<Player> playersList) {
        int nbPreliminary = 0;
        int nbFinal = 0;
        for (Player p : playersList) {
            if (p.getRegisteringStatus().compareTo("PRE") == 0) {
                nbPreliminary++;
            }
            if (p.getRegisteringStatus().compareTo("FIN") == 0) {
                nbFinal++;
            }
        }
        txfNbPlPre = "" + nbPreliminary;
        txfNbPlFin = "" + nbFinal;
        // sort
        ArrayList<Player> displayedPlayersList = new ArrayList<Player>(playersList);

        PlayerComparator playerComparator = new PlayerComparator(playersSortType);
        Collections.sort(displayedPlayersList, playerComparator);

        int line = 0;
        for (Player p : displayedPlayersList) {
        	registeredPlayers.set(line, new String[8]);
        	registeredPlayers.get(line)[REG_COL] = (p.getRegisteringStatus().compareTo("PRE") == 0) ? "P" : "F";
        	registeredPlayers.get(line)[NAME_COL] = p.getName();
        	registeredPlayers.get(line)[FIRSTNAME_COL] = p.getFirstName();
        	registeredPlayers.get(line)[RANK_COL] = Player.convertIntToKD(p.getRank());
        	registeredPlayers.get(line)[COUNTRY_COL] = p.getCountry();
        	registeredPlayers.get(line)[CLUB_COL] = p.getClub();
        	registeredPlayers.get(line)[RATING_COL] = "" + p.getRating();
        	registeredPlayers.get(line)[GRADE_COL] = p.getStrGrade();
            line++;
        }
    }
    
    private String normalizeCase(String name) {
        StringBuilder sb = new StringBuilder();
        Pattern namePattern = Pattern.compile(
                "(?:(da|de|degli|del|der|di|el|la|le|ter|und|van|vom|von|zu|zum)" +
                "|(.+?))(?:\\b|(?=_))([- _]?)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = namePattern.matcher(name.trim().toLowerCase());
        while (matcher.find()) {
            String noblePart = matcher.group(1);
            String namePart = matcher.group(2);
            String wordBreak = matcher.group(3);
            if (noblePart != null) {
                sb.append(noblePart);
            } else {
                sb.append(Character.toUpperCase(namePart.charAt(0)));
                sb.append(namePart.substring(1)); // always returns at least ""
            }
            if (wordBreak != null) {
                sb.append(wordBreak);
            }
        }
        return sb.toString();
    }
    
    private void removeSelectedPlayer(String name, String firstName) {
        try {
            Player playerToRemove = tournament.getPlayerByKeyString(name + firstName);
            boolean b = tournament.removePlayer(playerToRemove);
            if (b) {
                resetRatingListControls();
                this.tournamentChanged();
            } else {
                String strMessage = "" + name + " " + firstName + "could not be removed";
                tournamentSendErrorMessage(strMessage);
            }
        } catch (TournamentException te) {
        	tournamentSendErrorMessage(te.getMessage());
        }
    }
    
    private void sortByName() {
        playersSortType = PlayerComparator.NAME_ORDER;
        updatePnlRegisteredPlayers(tournament.playersList());
    }
    
    private void sortByRank() {
        playersSortType = PlayerComparator.RANK_ORDER;
        updatePnlRegisteredPlayers(tournament.playersList());
    }
    
    private void print() {
        TournamentPrinting.printPlayersList(tournament, playersSortType);
    }

    private void playerNameChoiceTextValueChanged(String str) {
        if (str.length() == 0) {
            return;
        }
        int pos = str.indexOf(" ");
        String str1;
        String str2;
        if (pos < 0) {
            str1 = str;
            str2 = "";
        } else {
            str1 = str.substring(0, pos);
            if (str.length() <= pos + 1) {
                str2 = "";
            } else {
                str2 = str.substring(pos + 1, str.length());
            }
        }

        Vector<String> vS = new Vector<String>();

        for (int iRP = 0; iRP < ratingList.getALRatedPlayers().size(); iRP++) {
            RatedPlayer rP = ratingList.getALRatedPlayers().get(iRP);
            String strName = rP.getName().toLowerCase();
            String strFirstName = rP.getFirstName().toLowerCase();
            int dn1 = RatedPlayer.distance_Levenshtein(str1, strName);
            int df1 = RatedPlayer.distance_Levenshtein(str2, strFirstName);
            int dn2 = RatedPlayer.distance_Levenshtein(str2, strName);
            int df2 = RatedPlayer.distance_Levenshtein(str1, strFirstName);
            int d = Math.min(dn1 + df1, dn2 + df2);
            int threshold = 9;
            if (d <= threshold) {
                String strNumber = "" + iRP;
                while (strNumber.length() < 5) {
                    strNumber = " " + strNumber;
                }
                vS.addElement("(" + d + ")" + strNumber + " " + rP.getName() + " " + rP.getFirstName() + " " +
                        rP.getCountry() + " " + rP.getClub() + " " + rP.getStrRawRating());
            }
        }
        if (!vS.isEmpty()) {
            Collections.sort(vS);
            lstPlayerNameChoice = vS;
        }
    }

    private void register(String name, String firstName, String country, String club, String egfPin, String ffgLicence,
    		String ffgLicenceStatus, String agaId, String agaExpirationDate, String grade,
    		String strRegistration, String rankStr, String ratingStr, String smmsCorrectionStr, boolean tabCkbParticipation[]) {
        manageRankGradeAndRatingValues(rankStr, grade, ratingStr); // Before anything else, fill unfilled grade/rank/rating fields

        Player p;

        if (strRegistration == null) {
            strRegistration = "PRE";
        }

        int rating;
        int rank = Player.convertKDPToInt(rankStr);

        String strOrigin = "INI";
        try{
            rating = new Integer(ratingStr).intValue();
        }catch(Exception e){
            rating = Player.ratingFromRank(rank);
        }
        
        int smmsCorrection;
        try {
            String strCorr = smmsCorrectionStr;
            if (strCorr.substring(0, 1).equals("+")) strCorr = strCorr.substring(1);
            smmsCorrection = Integer.parseInt(strCorr);
        } catch (NumberFormatException ex) {
            smmsCorrection = 0;
        }

        try {
            p = new Player(
                    name,
                    firstName,
                    country,
                    club,
                    egfPin,
                    ffgLicence,
                    ffgLicenceStatus,
                    agaId,
                    agaExpirationDate,
                    rank,
                    rating,
                    strOrigin,
                    grade,
                    smmsCorrection,
                    strRegistration);

            boolean[] bPart = new boolean[Gotha.MAX_NUMBER_OF_ROUNDS];
            
            int nbRounds = 0;
            nbRounds = tournament.getTournamentParameterSet().getGeneralParameterSet().getNumberOfRounds();
            for (int i = 0; i < nbRounds; i++) {
                bPart[i] = tabCkbParticipation[i];
            }
            p.setParticipating(tabCkbParticipation);
        } catch (PlayerException pe) {
            playerRegistrationError(pe.getMessage());
            return;
        }
        if (this.playerMode == PLAYER_MODE_NEW) {
            try {
                tournament.addPlayer(p);
                // Keep current registration status as default registration status
                strRegistration = "FIN";
                //strRegistration = "PRE";
                Preferences prefs = Preferences.userRoot().node(Gotha.strPreferences + "/playersmanager");
                prefs.put("defaultregistration", strRegistration);

                resetRatingListControls();
                this.tournamentChanged();
            } catch (TournamentException te) {
            	tournamentSendErrorMessage(te.getMessage());
                resetRatingListControls();
                return;
            }

        } else if (this.playerMode == PLAYER_MODE_MODIF) {
            try {
                if (tournament.isPlayerImplied(p)){
                    p.setRegisteringStatus("FIN");
                }
                tournament.modifyPlayer(playerInModification, p);
                resetRatingListControls();
            } catch (TournamentException ex) {
                resetRatingListControls();
                playerRegistrationError(ex.getMessage());
                return;
            }
            this.tournamentChanged();
            
        }
    }

    private void help() {
        Gotha.displayGothaHelp("Players Manager frame");
    }

    private String changeRating(String ratingStr, String newRatingStr) {
        int oldRating;
        try{
            oldRating = Integer.parseInt(ratingStr);   
        }
        catch(NumberFormatException e){
            oldRating = 0;
        }
        
        int newRating = oldRating;
        try{
            newRating = Integer.parseInt(newRatingStr);
            if (newRating < Player.MIN_RATING) newRating = Player.MIN_RATING;
            if (newRating > Player.MAX_RATING) newRating = Player.MAX_RATING;
        }catch(Exception e){
            newRating = oldRating;    
        }
        
        if (newRating != oldRating){
            return "" + newRating;
        } else {
        	return "" + oldRating;
        }
    }

    private void rankFromGrade() {
        resetRatingListControls();
    }

    private void rankFromGoR() {
        resetRatingListControls();
    }

    private void processRatingListChangeEvent() {
        this.resetRatingListControls();
    }

    private void updateRatingList(String url) {
        int rlType = RatingList.TYPE_UNDEFINED;
        if (!Gotha.isRatingListsDownloadEnabled()){
            String strMessage = "Access to Rating lists is currently disabled.\nSee Options .. Preferences menu item";
            errorRatingList(strMessage);
            return;
        }
        if (this.rdbEGF) rlType = RatingList.TYPE_EGF;
        if (this.rdbFFG) rlType = RatingList.TYPE_FFG;
        if (this.rdbAGA) rlType = RatingList.TYPE_AGA;

        String strDefaultURL;
        File fDefaultFile;
        String strPrompt;
        
        switch(rlType){
            case RatingList.TYPE_EGF:
                strDefaultURL = "http://www.europeangodatabase.eu/EGD/EGD_2_0/downloads/allworld_lp.html";
                fDefaultFile = new File(Gotha.runningDirectory, "ratinglists/egf_db.txt");
                strPrompt = "Download EGF Rating List from :";
                break;
            case RatingList.TYPE_FFG:
//                strDefaultURL = "http://ffg.jeudego.org/echelle/echtxt/ech_ffg_new.txt";
                strDefaultURL = "http://ffg.jeudego.org/echelle/echtxt/ech_ffg_V3.txt";
//                fDefaultFile = new File(Gotha.runningDirectory, "ratinglists/ech_ffg_new.txt");
                fDefaultFile = new File(Gotha.runningDirectory, "ratinglists/ech_ffg_V3.txt");
                strPrompt = "Download FFG Rating List from :";
                break;
            case RatingList.TYPE_AGA:
                strDefaultURL = "https://usgo.org/mm/tdlista.txt";
                fDefaultFile = new File(Gotha.runningDirectory, "ratinglists/tdlista.txt");
                strPrompt = "Download AGA Rating List from :";
                break;
            default:
                System.out.println("updateRatingList : Internal error");
                return;
        }
        
        try {
            if (url == null ) url = strDefaultURL;
            updateRatingListLabel("Download in progress");
            Gotha.download(progressListener, url, fDefaultFile);
        } catch (MalformedURLException ex) {
        	errorRatingList("Malformed URL\nRating list could not be loaded");
        } catch (IOException ex) {
        	errorRatingList("Unreachable file\nRating list could not be loaded");
        }
        useRatingList(rlType);        

    }

    private void sortByGrade() {
        playersSortType = PlayerComparator.GRADE_ORDER;
        updatePnlRegisteredPlayers(tournament.playersList());        
    }

    private void sortByRating() {
        playersSortType = PlayerComparator.RATING_ORDER;
        updatePnlRegisteredPlayers(tournament.playersList());        
    }

    private void manageRankGradeAndRatingValues(String rankStr, String gradeStr, String ratingStr){
        if (rankStr.equals("") && !gradeStr.equals("")){
            int r = Player.convertKDPToInt(gradeStr);
            rankStr = Player.convertIntToKD(r);
        }
        if (gradeStr.equals("") && !rankStr.equals("")){
        	gradeStr = rankStr;
        }
        
        if (rankStr.equals("")) return;
        int rank = Player.convertKDPToInt(rankStr);
        if (ratingStr.equals("")){
            int rating = rank * 100 + 2100;
            ratingStr = "" + rating;
        }
    }

    private void tournamentChanged() {
        tournament.setLastTournamentModificationTime(tournament.getCurrentTournamentTime());
        if (tournamentChangeListener != null) {
        	tournamentChangeListener.onChange();
        }
    }

    private void tournamentSendErrorMessage(String message) {
        if (tournamentChangeListener != null) {
        	tournamentChangeListener.onErrorMessage(message);
        }
    }

    private void playerRegistrationError(String message) {
    	if (playerRegistrationListener != null) {
    		playerRegistrationListener.onError(message);
    	}
    }
    
    private void updateRatingListLabel(String message) {
    	if (ratingListListener != null) {
    		ratingListListener.updateMessage(message);
    	}
    }
    
    private void errorRatingList(String message) {
    	if (ratingListListener != null) {
    		ratingListListener.onError(message);
    	}
    }
    
	public void setRatingListListener(OnUpdateRatingListListener ratingListListener) {
		this.ratingListListener = ratingListListener;
	}

	public void setTournamentChangeListener(OnTournamentChangeListener tournamentChangeListener) {
		this.tournamentChangeListener = tournamentChangeListener;
	}

	public void setPlayerRegistrationListener(OnPlayerRegistrationListener playerRegistrationListener) {
		this.playerRegistrationListener = playerRegistrationListener;
	}
    
	public void setProgressListener(DownloadProgressListener progressListener) {
		this.progressListener = progressListener;
	}

}
