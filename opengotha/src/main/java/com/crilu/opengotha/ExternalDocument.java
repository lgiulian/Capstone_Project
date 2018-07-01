package com.crilu.opengotha;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class ExternalDocument {

    public final static String DEFAULT_CHARSET = "UTF-8";
    public final static int DT_UNDEFINED = 0;
    public final static int DT_H9 = 1;
    public final static int DT_TOU = 2;
    public final static int DT_XML = 3;

    public static File generatePlayersListHTMLFile(TournamentInterface tournament) {
        // TODO Auto-generated method stub
        return null;
    }

    public static File generateTeamsListHTMLFile(TournamentInterface tournament) {
        // TODO Auto-generated method stub
        return null;
    }

    public static File generateGamesListHTMLFile(TournamentInterface tournament, int roundNumber) {
        // TODO Auto-generated method stub
        return null;
    }

    public static File generateStandingsHTMLFile(TournamentInterface tournament, int roundNumber) {
        // TODO Auto-generated method stub
        return null;
    }

    public static File generateMatchesListHTMLFile(TournamentInterface tournament, int roundNumber) {
        // TODO Auto-generated method stub
        return null;
    }

    public static File generateTeamsStandingsHTMLFile(TournamentInterface tournament, int roundNumber) {
        // TODO Auto-generated method stub
        return null;
    }

    public static Document getDocumentFromXMLFile(File sourceFile) {
        DocumentBuilder docBuilder;
        Document doc = null;
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setIgnoringElementContentWhitespace(true);
        try {
            docBuilder = docBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            System.out.println("Wrong parser configuration: " + e.getMessage());
            return null;
        }
        try {
            doc = docBuilder.parse(sourceFile);
        } catch (SAXException e) {
            System.out.println("Wrong XML file structure: " + e.getMessage());
            return null;
        } catch (IOException e) {
            System.out.println("Could not read source file: " + e.getMessage());
        }
        return doc;
    }

    public static String importTournamentFromXMLFile(File sourceFile, TournamentInterface tournament,
                                                     boolean bPlayers, boolean bGames, boolean bTPS, boolean bTeams, boolean bClubsGroups) {
        // What dataVersion ?
        long dataVersion = ExternalDocument.importDataVersionFromXMLFile(sourceFile);

        int nbImportedPlayers = 0;
        int nbNotImportedPlayers = 0;
        if (bPlayers) {
            ArrayList<Player> alPlayers = ExternalDocument.importPlayersFromXMLFile(sourceFile);
            if (alPlayers == null || alPlayers.isEmpty()) {
                System.out.println("No player has been imported");
            }
            if (alPlayers != null) {
                for (Player p : alPlayers) {
                    try {
                        tournament.addPlayer(p);
                        nbImportedPlayers++;
                    } catch (TournamentException ex) {
                        Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                nbNotImportedPlayers = alPlayers.size() - nbImportedPlayers;
            }
        }

        int nbImportedGames = 0;
        int nbNotImportedGames = 0;
        int nbReplacedGames = 0;
        int nbImportedByePlayers = 0;
        int nbReplacedByePlayers = 0;

        if (bGames) {
            // import games
            int nbGamesBeforeImport = 0;
            nbGamesBeforeImport = tournament.gamesList().size();
            ArrayList<Game> alGames = ExternalDocument.importGamesFromXMLFile(sourceFile, tournament);
            if (alGames == null) {
                System.out.println("No game could be imported");
            }

            if (alGames != null) {
                for (Game g : alGames) {
                    try {
                        tournament.addGame(g);
                        nbImportedGames++;
                    } catch (TournamentException ex) {
                        Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                nbNotImportedGames = alGames.size() - nbImportedGames;
            }

            int nbGamesAfterImport = 0;
            nbGamesAfterImport = tournament.gamesList().size();
            nbReplacedGames = nbImportedGames - (nbGamesAfterImport - nbGamesBeforeImport);

            // import bye players
            Player[] importedByePlayers = ExternalDocument.importByePlayersFromXMLFile(sourceFile, tournament);
            Player[] byePlayers = null;
            byePlayers = tournament.getByePlayers();
            for (int r = 0; r < byePlayers.length; r++) {
                if (importedByePlayers[r] == null) {
                    continue;
                }
                nbImportedByePlayers++;
                if (byePlayers[r] != null) {
                    nbReplacedByePlayers++;
                }
                tournament.assignByePlayer(importedByePlayers[r], r);
            }

        }

        if (bTPS) {
            // import tournament parameters
            TournamentParameterSet tps = ExternalDocument.importTournamentParameterSetFromXMLFile(sourceFile);
            if (tps == null) {
                System.out.println("No parameter could be imported");
            }
            if (tps != null) {
                tournament.setTournamentParameterSet(tps);
            }
        }

        int nbImportedTeams = 0;
        int nbNotImportedTeams = 0;
        int nbReplacedTeams = 0;

        if (bTeams) {
            // import teams

            int nbTeamsBeforeImport = 0;
            nbTeamsBeforeImport = tournament.teamsList().size();
            ArrayList<Team> alTeams = ExternalDocument.importTeamsFromXMLFile(sourceFile, tournament);
            if (alTeams != null) {
                for (Team t : alTeams) {
                    tournament.addTeam(t);
                    nbImportedTeams++;
                }
                nbNotImportedTeams = alTeams.size() - nbImportedTeams;
            }

            int nbTeamsAfterImport = 0;
            nbTeamsAfterImport = tournament.teamsList().size();
            nbReplacedTeams = nbImportedTeams - (nbTeamsAfterImport - nbTeamsBeforeImport);

            // import team tournament parameters
            TeamTournamentParameterSet ttps = ExternalDocument.importTeamTournamentParameterSetFromXMLFile(sourceFile);
            if (ttps == null) {
                System.out.println("No team parameter could be imported");
            }
            if (ttps != null) {
                tournament.setTeamTournamentParameterSet(ttps);
            }
        }

        // Import Clubs groups
        int nbImportedClubsGroups = 0;
        int nbNotImportedClubsGroups = 0;
        int nbReplacedClubsGroups = 0;
        if (bClubsGroups) {
            int nbClubsGroupsBeforeImport = 0;
            nbClubsGroupsBeforeImport = tournament.clubsGroupsList().size();
            ArrayList<ClubsGroup> alClubsGroups = ExternalDocument.importClubsGroupsFromXMLFile(sourceFile);
            if (alClubsGroups != null) {
                for (ClubsGroup cg : alClubsGroups) {
                    if (tournament.addClubsGroup(cg)) nbImportedClubsGroups++;
                    nbNotImportedClubsGroups = alClubsGroups.size() - nbImportedClubsGroups;
                }
            }
            int nbClubsGroupsAfterImport = 0;
            nbClubsGroupsAfterImport = tournament.clubsGroupsList().size();

            nbReplacedClubsGroups = nbClubsGroupsBeforeImport + nbImportedClubsGroups - nbClubsGroupsAfterImport;
        }


        tournament.updateNumberOfRoundsIfNecesary();

        // Report about imported objects
        String strReport = "" + nbImportedPlayers + " players have been imported";
        if (nbNotImportedPlayers > 0) {
            strReport += "\n" + nbNotImportedPlayers + " players could not be imported.";
        }

        strReport += "\n\n" + nbImportedGames + " games have been imported.";
        if (nbNotImportedGames > 0) {
            strReport = "\n" + nbNotImportedGames + " games could not be imported.";
        }
        if (nbReplacedGames > 0) {
            strReport += "\n" + nbReplacedGames + " games have been replaced.";
        }

        strReport += "\n\n" + nbImportedByePlayers + " bye players have been imported.";
        if (nbReplacedByePlayers > 0) {
            strReport += "\n" + nbReplacedByePlayers + " bye players have been replaced.";
        }

        strReport += "\n\n" + nbImportedTeams + " Teams have been imported.";
        if (nbNotImportedTeams > 0) {
            strReport = "\n" + nbNotImportedTeams + " Teams could not be imported.";
        }
        if (nbReplacedTeams > 0) {
            strReport += "\n" + nbReplacedTeams + " Teams have been replaced.";
        }

        strReport += "\n\n" + nbImportedClubsGroups + " Clubs Groups have been imported.";
        if (nbNotImportedClubsGroups > 0) {
            strReport += "\n" + nbNotImportedClubsGroups + " Clubs Groups could not be imported.";
        }
        if (nbReplacedClubsGroups > 0) {
            strReport += "\n" + nbReplacedClubsGroups + " Clubs Groups have been replaced.";
        }

        return strReport;
    }

    private static long importDataVersionFromXMLFile(File sourceFile) {
        Document doc = getDocumentFromXMLFile(sourceFile);
        long dataVersion;
        if (doc == null) {
            return 0L;
        }
        NodeList nl = doc.getElementsByTagName("Tournament");
        Node n = nl.item(0);
        NamedNodeMap nnm = n.getAttributes();
        String strDataVersion = extractNodeValue(nnm, "dataVersion", "200");
        dataVersion = Long.parseLong(strDataVersion);

        return dataVersion;

    }

    private static ArrayList<Player> importPlayersFromXMLFile(File sourceFile) {
        long currentDataVersion = Gotha.GOTHA_DATA_VERSION;
        long importedDataVersion = importDataVersionFromXMLFile(sourceFile);

        Document doc = getDocumentFromXMLFile(sourceFile);
        if (doc == null) {
            return null;
        }
        ArrayList<Player> alPlayers = new ArrayList<Player>();

        NodeList nl = doc.getElementsByTagName("Player");
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            NamedNodeMap nnm = n.getAttributes();

            String name = extractNodeValue(nnm, "name", "");
            String firstName = extractNodeValue(nnm, "firstName", "");
            String country = extractNodeValue(nnm, "country", "");
            String club = extractNodeValue(nnm, "club", "");
            String egfPin = extractNodeValue(nnm, "egfPin", "");
            String ffgLicence = extractNodeValue(nnm, "ffgLicence", "");
            String ffgLicenceStatus = extractNodeValue(nnm, "ffgLicenceStatus", "");
            String agaId = extractNodeValue(nnm, "agaId", "");
            String agaExpirationDate = extractNodeValue(nnm, "agaExpirationDate", "");
            String strRank = extractNodeValue(nnm, "rank", "30K");
            int rank = Player.convertKDPToInt(strRank);
            String strRating = extractNodeValue(nnm, "rating", "-900");
            int rating = new Integer(strRating).intValue();
            if (importedDataVersion < 201L) {
                rating += 2050;
            }
            if (rating > Player.MAX_RATING) {
                rating = Player.MAX_RATING;
            }
            if (rating < Player.MIN_RATING) {
                rating = Player.MIN_RATING;
            }

            String ratingOrigin = extractNodeValue(nnm, "ratingOrigin", "");
            String strGrade = extractNodeValue(nnm, "grade", "");
            String strSmmsCorrection = extractNodeValue(nnm, "smmsCorrection", "0");
            int smmsCorrection = new Integer(strSmmsCorrection).intValue();
            String strDefaultParticipating = "";
            for (int r = 0; r < Gotha.MAX_NUMBER_OF_ROUNDS; r++) {
                strDefaultParticipating += "1";
            }
            String strParticipating = extractNodeValue(nnm, "participating", strDefaultParticipating);
            boolean[] participating = new boolean[Gotha.MAX_NUMBER_OF_ROUNDS];
            for (int r = 0; r < participating.length; r++) {
                try {
                    char cPart = strParticipating.charAt(r);
                    if (cPart == '0') {
                        participating[r] = false;
                    } else {
                        participating[r] = true;
                    }
                } catch (IndexOutOfBoundsException e) {
                    participating[r] = true;
                }
            }
            String registeringStatus = extractNodeValue(nnm, "registeringStatus", "FIN");
            Player p = null;
            try {
                p = new Player(name, firstName, country, club, egfPin, ffgLicence, ffgLicenceStatus,
                        agaId, agaExpirationDate, rank, rating, ratingOrigin, strGrade, smmsCorrection, registeringStatus);
            } catch (PlayerException ex) {
                Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
            }
            p.setParticipating(participating);

            alPlayers.add(p);
        }
        return alPlayers;
    }

    private static TournamentParameterSet importTournamentParameterSetFromXMLFile(File sourceFile) {
        Document doc = getDocumentFromXMLFile(sourceFile);
        if (doc == null) {
            return null;
        }

        // Is there a TournamentParameterSet node in file ?
        NodeList nlTPS = doc.getElementsByTagName("TournamentParameterSet");
        if (nlTPS == null || nlTPS.getLength() == 0) {
            return null;
        }

        TournamentParameterSet tps = new TournamentParameterSet();

        //GPS
        GeneralParameterSet gps = new GeneralParameterSet();
        NodeList nlGPS = doc.getElementsByTagName("GeneralParameterSet");
        Node nGPS = nlGPS.item(0);
        NamedNodeMap nnmGPS = nGPS.getAttributes();

        String shortName = extractNodeValue(nnmGPS, "shortName", "defaultshortname");
        gps.setShortName(shortName);
        String name = extractNodeValue(nnmGPS, "name", "default Name");
        gps.setName(name);
        String location = extractNodeValue(nnmGPS, "location", "Paris");
        gps.setLocation(location);
        String director = extractNodeValue(nnmGPS, "director", "");
        gps.setDirector(director);
        String strBeginDate = extractNodeValue(nnmGPS, "beginDate", "2000-01-01");
        try {
            gps.setBeginDate(new SimpleDateFormat("yyyy-MM-dd").parse(strBeginDate));
        } catch (ParseException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
        String strEndDate = extractNodeValue(nnmGPS, "endDate", "2000-01-01");
        try {
            gps.setEndDate(new SimpleDateFormat("yyyy-MM-dd").parse(strEndDate));
        } catch (ParseException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
        int time = extractNodeIntValue(nnmGPS, "time", GeneralParameterSet.GEN_GP_BASICTIME_DEF);    // For old dataVersion
        gps.setBasicTime(extractNodeIntValue(nnmGPS, "basicTime", time));

        // Complementary time for old dataVersion
        String strByoYomi = extractNodeValue(nnmGPS, "byoYomi", "true");
        boolean bByoYomi = Boolean.valueOf(strByoYomi).booleanValue();
        if (bByoYomi) {
            gps.setComplementaryTimeSystem(GeneralParameterSet.GEN_GP_CTS_CANBYOYOMI);
            gps.setNbMovesCanTime(GeneralParameterSet.GEN_GP_CTS_NBMOVESCANTIME_DEF);
            gps.setCanByoYomiTime(GeneralParameterSet.GEN_GP_CTS_CANBYOYOMITIME_DEF);
        } else {
            gps.setComplementaryTimeSystem(GeneralParameterSet.GEN_GP_CTS_SUDDENDEATH);
        }

        String strCTS = extractNodeValue(nnmGPS, "complementaryTimeSystem", "CANBYOYOMI");
        int cts = GeneralParameterSet.GEN_GP_CTS_CANBYOYOMI;
        if (strCTS.equals("SUDDENDEATH")) {
            cts = GeneralParameterSet.GEN_GP_CTS_SUDDENDEATH;
        }
        if (strCTS.equals("STDBYOYOMI")) {
            cts = GeneralParameterSet.GEN_GP_CTS_STDBYOYOMI;
        }
        if (strCTS.equals("CANBYOYOMI")) {
            cts = GeneralParameterSet.GEN_GP_CTS_CANBYOYOMI;
        }
        if (strCTS.equals("FISCHER")) {
            cts = GeneralParameterSet.GEN_GP_CTS_FISCHER;
        }
        gps.setComplementaryTimeSystem(cts);

        gps.setStdByoYomiTime(extractNodeIntValue(nnmGPS, "stdByoYomiTime", GeneralParameterSet.GEN_GP_CTS_STDBYOYOMITIME_DEF));
        gps.setNbMovesCanTime(extractNodeIntValue(nnmGPS, "nbMovesCanTime", GeneralParameterSet.GEN_GP_CTS_NBMOVESCANTIME_DEF));
        gps.setCanByoYomiTime(extractNodeIntValue(nnmGPS, "canByoYomiTime", GeneralParameterSet.GEN_GP_CTS_CANBYOYOMITIME_DEF));
        gps.setFischerTime(extractNodeIntValue(nnmGPS, "fischerTime", GeneralParameterSet.GEN_GP_CTS_FISCHERTIME_DEF));

        String strSize = extractNodeValue(nnmGPS, "size", "19");
        gps.setStrSize(strSize);
        String strKomi = extractNodeValue(nnmGPS, "komi", "7.5");
        gps.setStrKomi(strKomi);
        String strNumberOfRounds = extractNodeValue(nnmGPS, "numberOfRounds", "5");
        gps.setNumberOfRounds(new Integer(strNumberOfRounds).intValue());
        String strNumberOfCategories = extractNodeValue(nnmGPS, "numberOfCategories", "1");
        int nbCategories = new Integer(strNumberOfCategories).intValue();
        gps.setNumberOfCategories(nbCategories);

        NodeList nl = doc.getElementsByTagName("Category");
        int[] lowerLimits = new int[nbCategories - 1];
        for (int c = 0; c < nl.getLength(); c++) {
            Node n = nl.item(c);
            NamedNodeMap nnm = n.getAttributes();
            String strNumber = extractNodeValue(nnm, "number", "1");
            String strLowerLimit = extractNodeValue(nnm, "lowerLimit", "30K");
            int numCat = new Integer(strNumber).intValue() - 1;
            lowerLimits[numCat] = Player.convertKDPToInt(strLowerLimit);
        }
        gps.setLowerCategoryLimits(lowerLimits);

        String strGenMMFloor = extractNodeValue(nnmGPS, "genMMFloor", "20K");
        gps.setGenMMFloor(Player.convertKDPToInt(strGenMMFloor));
        String strGenMMBar = extractNodeValue(nnmGPS, "genMMBar", "4D");
        gps.setGenMMBar(Player.convertKDPToInt(strGenMMBar));
        String strGenMMZero = extractNodeValue(nnmGPS, "genMMZero", "30K");
        gps.setGenMMZero(Player.convertKDPToInt(strGenMMZero));

        String strGenNBW2ValueAbsent = extractNodeValue(nnmGPS, "genNBW2ValueAbsent", "0");
        gps.setGenNBW2ValueAbsent(new Integer(strGenNBW2ValueAbsent).intValue());

        String strGenNBW2ValueBye = extractNodeValue(nnmGPS, "genNBW2ValueBye", "0");
        gps.setGenNBW2ValueBye(new Integer(strGenNBW2ValueBye).intValue());

        String strGenMMS2ValueAbsent = extractNodeValue(nnmGPS, "genMMS2ValueAbsent", "0");
        gps.setGenMMS2ValueAbsent(new Integer(strGenMMS2ValueAbsent).intValue());

        String strGenMMS2ValueBye = extractNodeValue(nnmGPS, "genMMS2ValueBye", "0");
        gps.setGenMMS2ValueBye(new Integer(strGenMMS2ValueBye).intValue());

        String strGenRoundDownNBWMMS = extractNodeValue(nnmGPS, "genRoundDownNBWMMS", "true");
        gps.setGenRoundDownNBWMMS(Boolean.valueOf(strGenRoundDownNBWMMS).booleanValue());

        tps.setGeneralParameterSet(gps);

        // HPS
        HandicapParameterSet hps = new HandicapParameterSet();
        NodeList nlHPS = doc.getElementsByTagName("HandicapParameterSet");
        Node nHPS = nlHPS.item(0);
        NamedNodeMap nnmHPS = nHPS.getAttributes();

        String strHdBasedOnMMS = extractNodeValue(nnmHPS, "hdBasedOnMMS", "true");
        hps.setHdBasedOnMMS(Boolean.valueOf(strHdBasedOnMMS).booleanValue());
        String strHdNoHdRankThreshold = extractNodeValue(nnmHPS, "hdNoHdRankThreshold", "1D");
        hps.setHdNoHdRankThreshold(Player.convertKDPToInt(strHdNoHdRankThreshold));
        String strHdCorrection = extractNodeValue(nnmHPS, "hdCorrection", "1");
        hps.setHdCorrection(new Integer(strHdCorrection).intValue());
        String strHdCeiling = extractNodeValue(nnmHPS, "hdCeiling", "9");
        hps.setHdCeiling(new Integer(strHdCeiling).intValue());
        tps.setHandicapParameterSet(hps);

        // PPS
        PlacementParameterSet pps = new PlacementParameterSet();
        NodeList nlPPS = doc.getElementsByTagName("PlacementParameterSet");
        ArrayList<Node> alCritNodes = extractNodes(nlPPS.item(0), "PlacementCriterion");

        int[] plaC = new int[TeamPlacementParameterSet.TPL_MAX_NUMBER_OF_CRITERIA];
        for (int nC = 0; nC < plaC.length; nC++) {
            plaC[nC] = PlacementParameterSet.PLA_CRIT_NUL;
        }
        for (Node n : alCritNodes) {
            NamedNodeMap nnm = n.getAttributes();
            String strNumber = extractNodeValue(nnm, "number", "1");
            int number = new Integer(strNumber).intValue();
            String strName = extractNodeValue(nnm, "name", "NULL");
            for (int nPC = 0; nPC < PlacementParameterSet.allPlacementCriteria.length; nPC++) {
                PlacementCriterion pC = PlacementParameterSet.allPlacementCriteria[nPC];
                if (strName.equals(pC.longName)) {
                    plaC[number - 1] = pC.uid;
                    break;
                }
            }

        }

        pps.setPlaCriteria(plaC);

        tps.setPlacementParameterSet(pps);

        //paiPS
        PairingParameterSet paiPS = new PairingParameterSet();
        NodeList nlPaiPS = doc.getElementsByTagName("PairingParameterSet");
        Node nPaiPS = nlPaiPS.item(0);
        NamedNodeMap nnmPaiPS = nPaiPS.getAttributes();

        paiPS.setPaiStandardNX1Factor(new Double(extractNodeValue(nnmPaiPS, "paiStandardNX1Factor", "0.5")).doubleValue());
        paiPS.setPaiBaAvoidDuplGame(new Long(extractNodeValue(nnmPaiPS, "paiBaAvoidDuplGame", "500000000000000")).longValue());
        paiPS.setPaiBaRandom(new Long(extractNodeValue(nnmPaiPS, "paiBaRandom", "0")).longValue());
        paiPS.setPaiBaDeterministic(Boolean.valueOf(extractNodeValue(nnmPaiPS, "paiBaDeterministic", "true")).booleanValue());
        paiPS.setPaiBaBalanceWB(new Long(extractNodeValue(nnmPaiPS, "paiBaBalanceWB", "1000")).longValue());
        paiPS.setPaiMaAvoidMixingCategories(new Long(extractNodeValue(nnmPaiPS, "paiMaAvoidMixingCategories", "20000000000000")).longValue());
        paiPS.setPaiMaMinimizeScoreDifference(new Long(extractNodeValue(nnmPaiPS, "paiMaMinimizeScoreDifference", "100000000000")).longValue());
        paiPS.setPaiMaDUDDWeight(new Long(extractNodeValue(nnmPaiPS, "paiMaDUDDWeight", "100000000")).longValue());
        paiPS.setPaiMaCompensateDUDD(Boolean.valueOf(extractNodeValue(nnmPaiPS, "paiMaCompensateDUDD", "true")).booleanValue());

        String strDUDDU = extractNodeValue(nnmPaiPS, "paiMaDUDDUpperMode", "MID");
        int duddu = PairingParameterSet.PAIMA_DUDD_MID;
        if (strDUDDU.equals("TOP")) {
            duddu = PairingParameterSet.PAIMA_DUDD_TOP;
        }
        if (strDUDDU.equals("MID")) {
            duddu = PairingParameterSet.PAIMA_DUDD_MID;
        }
        if (strDUDDU.equals("BOT")) {
            duddu = PairingParameterSet.PAIMA_DUDD_BOT;
        }
        paiPS.setPaiMaDUDDUpperMode(duddu);

        String strDUDDL = extractNodeValue(nnmPaiPS, "paiMaDUDDLowerMode", "MID");
        int duddl = PairingParameterSet.PAIMA_DUDD_MID;
        if (strDUDDL.equals("TOP")) {
            duddl = PairingParameterSet.PAIMA_DUDD_TOP;
        }
        if (strDUDDL.equals("MID")) {
            duddl = PairingParameterSet.PAIMA_DUDD_MID;
        }
        if (strDUDDL.equals("BOT")) {
            duddl = PairingParameterSet.PAIMA_DUDD_BOT;
        }
        paiPS.setPaiMaDUDDLowerMode(duddl);
        paiPS.setPaiMaMaximizeSeeding(new Long(extractNodeValue(nnmPaiPS, "paiMaMaximizeSeeding", "5000000")).longValue());
        paiPS.setPaiMaLastRoundForSeedSystem1(new Integer(extractNodeValue(nnmPaiPS, "paiMaLastRoundForSeedSystem1", "2")).intValue() - 1);

        String strS1 = extractNodeValue(nnmPaiPS, "paiMaSeedSystem1", "SPLITANDRANDOM");
        int s1 = PairingParameterSet.PAIMA_SEED_SPLITANDRANDOM;
        if (strS1.equals("SPLITANDRANDOM")) {
            s1 = PairingParameterSet.PAIMA_SEED_SPLITANDRANDOM;
        }
        if (strS1.equals("SPLITANDFOLD")) {
            s1 = PairingParameterSet.PAIMA_SEED_SPLITANDFOLD;
        }
        if (strS1.equals("SPLITANDSLIP")) {
            s1 = PairingParameterSet.PAIMA_SEED_SPLITANDSLIP;
        }
        paiPS.setPaiMaSeedSystem1(s1);

        String strS2 = extractNodeValue(nnmPaiPS, "paiMaSeedSystem2", "SPLITANDFOLD");
        int s2 = PairingParameterSet.PAIMA_SEED_SPLITANDRANDOM;
        if (strS2.equals("SPLITANDRANDOM")) {
            s2 = PairingParameterSet.PAIMA_SEED_SPLITANDRANDOM;
        }
        if (strS2.equals("SPLITANDFOLD")) {
            s2 = PairingParameterSet.PAIMA_SEED_SPLITANDFOLD;
        }
        if (strS2.equals("SPLITANDSLIP")) {
            s2 = PairingParameterSet.PAIMA_SEED_SPLITANDSLIP;
        }
        paiPS.setPaiMaSeedSystem2(s2);

        String strAddCrit1 = extractNodeValue(nnmPaiPS, "paiMaAdditionalPlacementCritSystem1", "RATING");
        int aCrit1 = PlacementParameterSet.PLA_CRIT_RATING;
        for (int nPC = 0; nPC < PlacementParameterSet.allPlacementCriteria.length; nPC++) {
            PlacementCriterion pC = PlacementParameterSet.allPlacementCriteria[nPC];
            if (strAddCrit1.equals(pC.longName)) {
                aCrit1 = pC.uid;
                break;
            }
        }
        paiPS.setPaiMaAdditionalPlacementCritSystem1(aCrit1);

        String strAddCrit2 = extractNodeValue(nnmPaiPS, "paiMaAdditionalPlacementCritSystem2", "NULL");
        int aCrit2 = PlacementParameterSet.PLA_CRIT_NUL;
        for (int nPC = 0; nPC < PlacementParameterSet.allPlacementCriteria.length; nPC++) {
            PlacementCriterion pC = PlacementParameterSet.allPlacementCriteria[nPC];
            if (strAddCrit2.equals(pC.longName)) {
                aCrit2 = pC.uid;
                break;
            }
        }
        paiPS.setPaiMaAdditionalPlacementCritSystem2(aCrit2);

        paiPS.setPaiSeRankThreshold(Player.convertKDPToInt(extractNodeValue(nnmPaiPS, "paiSeRankThreshold", "4D")));
        paiPS.setPaiSeNbWinsThresholdActive(Boolean.valueOf(extractNodeValue(nnmPaiPS, "paiSeNbWinsThresholdActive", "true")).booleanValue());
        paiPS.setPaiSeBarThresholdActive(Boolean.valueOf(extractNodeValue(nnmPaiPS, "paiSeBarThresholdActive", "true")).booleanValue());
        paiPS.setPaiSeDefSecCrit(new Long(extractNodeValue(nnmPaiPS, "paiSeDefSecCrit", "100000000000")).longValue());
        paiPS.setPaiSeMinimizeHandicap(new Long(extractNodeValue(nnmPaiPS, "paiSeMinimizeHandicap", "0")).longValue());
        paiPS.setPaiSeAvoidSameGeo(new Long(extractNodeValue(nnmPaiPS, "paiSeAvoidSameGeo", "100000000000")).longValue());
        paiPS.setPaiSePreferMMSDiffRatherThanSameCountry(new Integer(extractNodeValue(nnmPaiPS, "paiSePreferMMSDiffRatherThanSameCountry", "1")).intValue());
        paiPS.setPaiSePreferMMSDiffRatherThanSameClubsGroup(new Integer(extractNodeValue(nnmPaiPS, "paiSePreferMMSDiffRatherThanSameClubsGroup", "2")).intValue());
        paiPS.setPaiSePreferMMSDiffRatherThanSameClub(new Integer(extractNodeValue(nnmPaiPS, "paiSePreferMMSDiffRatherThanSameClub", "3")).intValue());

        tps.setPairingParameterSet(paiPS);

        // DPPS
        DPParameterSet dpps = new DPParameterSet();
        NodeList nlDPPS = doc.getElementsByTagName("DPParameterSet");
        Node nDPPS = nlDPPS.item(0);
        if (nDPPS != null) {
            NamedNodeMap nnmDPPS = nDPPS.getAttributes();

            String strPlayerSortType = extractNodeValue(nnmDPPS, "playerSortType", "name");
            int playerSortType = PlayerComparator.NAME_ORDER;
            if (strPlayerSortType.equals("rank")) playerSortType = PlayerComparator.RANK_ORDER;
            if (strPlayerSortType.equals("grade")) playerSortType = PlayerComparator.GRADE_ORDER;
            dpps.setPlayerSortType(playerSortType);

            String strGameFormat = extractNodeValue(nnmDPPS, "gameFormat", "full");
            int gameFormat = DPParameterSet.DP_GAME_FORMAT_FULL;
            if (strGameFormat.equals("short")) gameFormat = DPParameterSet.DP_GAME_FORMAT_SHORT;
            dpps.setGameFormat(gameFormat);

            String strShowPlayerGrade = extractNodeValue(nnmDPPS, "showPlayerGrade", "true");
            dpps.setShowPlayerGrade(Boolean.valueOf(strShowPlayerGrade).booleanValue());
            String strShowPlayerCountry = extractNodeValue(nnmDPPS, "showPlayerCountry", "false");
            dpps.setShowPlayerCountry(Boolean.valueOf(strShowPlayerCountry).booleanValue());
            String strShowPlayerClub = extractNodeValue(nnmDPPS, "showPlayerClub", "true");
            dpps.setShowPlayerClub(Boolean.valueOf(strShowPlayerClub).booleanValue());

            String strShowByePlayer = extractNodeValue(nnmDPPS, "showByePlayer", "true");
            dpps.setShowByePlayer(Boolean.valueOf(strShowByePlayer).booleanValue());
            String strShowNotPairedPlayers = extractNodeValue(nnmDPPS, "showNotPairedPlayers", "true");
            dpps.setShowNotPairedPlayers(Boolean.valueOf(strShowNotPairedPlayers).booleanValue());
            String strShowNotParticipatingPlayers = extractNodeValue(nnmDPPS, "showNotParticipatingPlayers", "true");
            dpps.setShowNotParticipatingPlayers(Boolean.valueOf(strShowNotParticipatingPlayers).booleanValue());
            String strShowNotFinallyRegisteredPlayers = extractNodeValue(nnmDPPS, "showNotFinallyRegisteredPlayers", "true");
            dpps.setShowNotFinallyRegisteredPlayers(Boolean.valueOf(strShowNotFinallyRegisteredPlayers).booleanValue());

            String strDisplayNumCol = extractNodeValue(nnmDPPS, "displayNumCol", "true");
            dpps.setDisplayNumCol(Boolean.valueOf(strDisplayNumCol).booleanValue());
            String strDisplayPlCol = extractNodeValue(nnmDPPS, "displayPlCol", "true");
            dpps.setDisplayPlCol(Boolean.valueOf(strDisplayPlCol).booleanValue());
            String strDisplayCoCol = extractNodeValue(nnmDPPS, "displayCoCol", "true");
            dpps.setDisplayCoCol(Boolean.valueOf(strDisplayCoCol).booleanValue());
            String strDisplayClCol = extractNodeValue(nnmDPPS, "displayClCol", "false");
            dpps.setDisplayClCol(Boolean.valueOf(strDisplayClCol).booleanValue());

            String strDisplayIndGamesInMatches = extractNodeValue(nnmDPPS, "displayIndGamesInMatches", "true");
            dpps.setDisplayIndGamesInMatches(Boolean.valueOf(strDisplayIndGamesInMatches).booleanValue());
        }
        tps.setDPParameterSet(dpps);

        // PubPS
        PublishParameterSet pubPS = new PublishParameterSet();
        NodeList nlPubPS = doc.getElementsByTagName("PublishParameterSet");
        Node nPubPS = nlPubPS.item(0);
        if (nPubPS != null) {
            NamedNodeMap nnmPubPS = nPubPS.getAttributes();

            String strPrint = extractNodeValue(nnmPubPS, "print", "true");
            pubPS.setPrint(Boolean.valueOf(strPrint).booleanValue());
            String strExportToLocalFile = extractNodeValue(nnmPubPS, "exportToLocalFile", "true");
            pubPS.setExportToLocalFile(Boolean.valueOf(strExportToLocalFile).booleanValue());
            String strExportHFToOGSite = extractNodeValue(nnmPubPS, "exportHFToOGSite", "false");
            pubPS.setExportHFToOGSite(Boolean.valueOf(strExportHFToOGSite).booleanValue());
            String strExportTFToOGSite = extractNodeValue(nnmPubPS, "exportTFToOGSite", "true");
            pubPS.setExportTFToOGSite(Boolean.valueOf(strExportTFToOGSite).booleanValue());
            String strExportToUDSite = extractNodeValue(nnmPubPS, "exportToUDSite", "false");
            pubPS.setExportToUDSite(Boolean.valueOf(strExportToUDSite).booleanValue());
            String strHtmlAutoScroll = extractNodeValue(nnmPubPS, "htmlAutoScroll", "false");
            pubPS.setHtmlAutoScroll(Boolean.valueOf(strHtmlAutoScroll).booleanValue());
        }
        tps.setPublishParameterSet(pubPS);

        return tps;
    }

    private static ArrayList<Team> importTeamsFromXMLFile(File sourceFile, TournamentInterface tournament) {
        long importedDataVersion = importDataVersionFromXMLFile(sourceFile);
        Document doc = getDocumentFromXMLFile(sourceFile);
        if (doc == null) {
            return null;
        }

        NodeList nlTeamList = doc.getElementsByTagName("Team");

        if (nlTeamList == null || nlTeamList.getLength() == 0) {
            return null;
        }

        ArrayList<Team> alTeams = new ArrayList<Team>();
        for (int i = 0; i < nlTeamList.getLength(); i++) {
            Node nTeam = nlTeamList.item(i);
            NamedNodeMap nnmTeam = nTeam.getAttributes();

            String strTeamNumber = extractNodeValue(nnmTeam, "teamNumber", "1");
            String strTeamName = extractNodeValue(nnmTeam, "teamName", "Unnamed team");
            int teamNumber = new Integer(strTeamNumber).intValue() - 1;
            String teamName = strTeamName;
            Team t = new Team(teamNumber, teamName);

            NodeList nlElements = nTeam.getChildNodes();
            for (int iel = 0; iel < nlElements.getLength(); iel++) {
                Node nBoard = nlElements.item(iel);
                if (nBoard.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                if (!nBoard.getNodeName().equals("Board")) {
                    continue;
                }
                NamedNodeMap nnmBoard = nBoard.getAttributes();
                String strRoundNumber = extractNodeValue(nnmBoard, "roundNumber", "0");
                int roundNumber = new Integer(strRoundNumber).intValue() - 1;
                String strBoardNumber = extractNodeValue(nnmBoard, "boardNumber", "1");
                int boardNumber = new Integer(strBoardNumber).intValue() - 1;
                String strPlayer = extractNodeValue(nnmBoard, "player", "unnamed player");
                Player p;
                if (importedDataVersion <= 200)
                    p = tournament.getPlayerByObsoleteCanonicalName(strPlayer);
                else p = tournament.getPlayerByKeyString(strPlayer);
                if (p == null) {
                    strPlayer = Gotha.forceToASCII(strPlayer).toUpperCase();
                    p = tournament.getPlayerByObsoleteCanonicalName(strPlayer);
                }
                if (p == null) continue;
                if (roundNumber < 0){ // Before V3.28.04, roundNumber was not documented. So, set TeamMember for all rounds
                    for (int r = 0; r < Gotha.MAX_NUMBER_OF_ROUNDS; r++){
                        t.setTeamMember(p, r, boardNumber);
                    }

                }
                else{
                    t.setTeamMember(p, roundNumber, boardNumber);
                }
            }
            alTeams.add(t);
        }
        return alTeams;
    }

    private static TeamTournamentParameterSet importTeamTournamentParameterSetFromXMLFile(File sourceFile) {
        Document doc = getDocumentFromXMLFile(sourceFile);
        if (doc == null) {
            return null;
        }

        // Is there a TeamTournamentParameterSet node in file ?
        NodeList nlTTPS = doc.getElementsByTagName("TeamTournamentParameterSet");
        if (nlTTPS == null || nlTTPS.getLength() == 0) {
            return null;
        }

        TeamTournamentParameterSet ttps = new TeamTournamentParameterSet();

        // TGPS
        TeamGeneralParameterSet tgps = new TeamGeneralParameterSet();
        NodeList nlTGPS = doc.getElementsByTagName("TeamGeneralParameterSet");
        Node nTGPS = nlTGPS.item(0);
        NamedNodeMap nnmTGPS = nTGPS.getAttributes();

        String strTeamSize = extractNodeValue(nnmTGPS, "teamSize", "4");
        int teamSize = Integer.parseInt(strTeamSize);
        tgps.setTeamSize(teamSize);

        ttps.setTeamGeneralParameterSet(tgps);

        // TPPS
        TeamPlacementParameterSet tpps = new TeamPlacementParameterSet();
        NodeList nlTPPS = doc.getElementsByTagName("TeamPlacementParameterSet");
        ArrayList<Node> alCritNodes = extractNodes(nlTPPS.item(0), "PlacementCriterion");

        int[] plaC = new int[TeamPlacementParameterSet.TPL_MAX_NUMBER_OF_CRITERIA];
        for (int nC = 0; nC < plaC.length; nC++) {
            plaC[nC] = PlacementParameterSet.PLA_CRIT_NUL;
        }
        for (Node n : alCritNodes) {
            NamedNodeMap nnm = n.getAttributes();
            String strNumber = extractNodeValue(nnm, "number", "1");
            int number = new Integer(strNumber).intValue();
            String strName = extractNodeValue(nnm, "name", "NULL");
            for (int nPC = 0; nPC < TeamPlacementParameterSet.allPlacementCriteria.length; nPC++) {
                PlacementCriterion pC = TeamPlacementParameterSet.allPlacementCriteria[nPC];
                if (strName.equals(pC.longName)) {
                    plaC[number - 1] = pC.uid;
                    break;
                }
            }
        }
        tpps.setPlaCriteria(plaC);
        ttps.setTeamPlacementParameterSet(tpps);

        return ttps;
    }

    /**
     * recursively searches a Node tree for nodes with a given name
     * @param nodeBase
     * @param nodeName
     * @return an ArrayList of all found Nodes
     */
    public static ArrayList<Node> extractNodes(Node nodeBase, String nodeName) {
        ArrayList<Node> alNodes = new ArrayList<Node>();
        NodeList nlElements = nodeBase.getChildNodes();
        for (int iel = 0; iel < nlElements.getLength(); iel++) {
            Node n = nlElements.item(iel);
            if (n.getNodeName().equals(nodeName)) {
                alNodes.add(n);
            }
            ArrayList<Node> alN = extractNodes(n, nodeName);
            alNodes.addAll(alN);
        }
        return alNodes;
    }

    private static ArrayList<Game> importGamesFromXMLFile(File sourceFile, TournamentInterface tournament) {
        long importedDataVersion = importDataVersionFromXMLFile(sourceFile);
        Document doc = getDocumentFromXMLFile(sourceFile);
        if (doc == null) {
            return null;
        }

        ArrayList<Game> alGames = new ArrayList<Game>();
        NodeList nl = doc.getElementsByTagName("Game");
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            NamedNodeMap nnm = n.getAttributes();

            String strRoundNumber = extractNodeValue(nnm, "roundNumber", "1");
            int roundNumber = new Integer(strRoundNumber).intValue() - 1;
            String strTableNumber = extractNodeValue(nnm, "tableNumber", "1");
            int tableNumber = new Integer(strTableNumber).intValue() - 1;
            String strWhitePlayer = extractNodeValue(nnm, "whitePlayer", "");
            String strBlackPlayer = extractNodeValue(nnm, "blackPlayer", "");
            Player wP;
            Player bP;
            if (importedDataVersion <= 200)
                wP = tournament.getPlayerByObsoleteCanonicalName(strWhitePlayer);
            else wP = tournament.getPlayerByKeyString(strWhitePlayer);
            if (wP == null) {
                strWhitePlayer = Gotha.forceToASCII(strWhitePlayer).toUpperCase();
                wP = tournament.getPlayerByObsoleteCanonicalName(strWhitePlayer);
            }
            if (wP == null) continue;

            if (importedDataVersion <= 200)
                bP = tournament.getPlayerByObsoleteCanonicalName(strBlackPlayer);
            else bP = tournament.getPlayerByKeyString(strBlackPlayer);
            if (bP == null) {
                strBlackPlayer = Gotha.forceToASCII(strBlackPlayer).toUpperCase();
                bP = tournament.getPlayerByObsoleteCanonicalName(strBlackPlayer);
            }
            if (bP == null) continue;

            String strKnownColor = extractNodeValue(nnm, "strKnownColor", "true");
            boolean knownColor = true;
            if (strKnownColor.equals("false")) {
                knownColor = false;
            }
            String strHandicap = extractNodeValue(nnm, "handicap", "0");
            int handicap = new Integer(strHandicap).intValue();
            String strResult = extractNodeValue(nnm, "result", "RESULT_UNKNOWN");
            int result = Game.RESULT_UNKNOWN;
            if (strResult.equals("RESULT_WHITEWINS")) {
                result = Game.RESULT_WHITEWINS;
            }
            if (strResult.equals("RESULT_BLACKWINS")) {
                result = Game.RESULT_BLACKWINS;
            }
            if (strResult.equals("RESULT_EQUAL")) {
                result = Game.RESULT_EQUAL;
            }
            if (strResult.equals("RESULT_BOTHLOSE")) {
                result = Game.RESULT_BOTHLOSE;
            }
            if (strResult.equals("RESULT_BOTHWIN")) {
                result = Game.RESULT_BOTHWIN;
            }
            if (strResult.equals("RESULT_WHITEWINS_BYDEF")) {
                result = Game.RESULT_WHITEWINS_BYDEF;
            }
            if (strResult.equals("RESULT_BLACKWINS_BYDEF")) {
                result = Game.RESULT_BLACKWINS_BYDEF;
            }
            if (strResult.equals("RESULT_EQUAL_BYDEF")) {
                result = Game.RESULT_EQUAL_BYDEF;
            }
            if (strResult.equals("RESULT_BOTHLOSE_BYDEF")) {
                result = Game.RESULT_BOTHLOSE_BYDEF;
            }
            if (strResult.equals("RESULT_BOTHWIN_BYDEF")) {
                result = Game.RESULT_BOTHWIN_BYDEF;
            }

            Game g = new Game(roundNumber, tableNumber, wP, bP, true, handicap, result);
            g.setKnownColor(knownColor);
            alGames.add(g);
        }
        return alGames;
    }

    public static Player[] importByePlayersFromXMLFile(File sourceFile, TournamentInterface tournament) {
        long importedDataVersion = importDataVersionFromXMLFile(sourceFile);
        Document doc = getDocumentFromXMLFile(sourceFile);

        Player[] byePlayers = new Player[Gotha.MAX_NUMBER_OF_ROUNDS];
        for (int r = 0; r < byePlayers.length; r++) {
            byePlayers[r] = null;
        }

        NodeList nl = doc.getElementsByTagName("ByePlayer");
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            NamedNodeMap nnm = n.getAttributes();
            String strRoundNumber = extractNodeValue(nnm, "roundNumber", "1");
            int roundNumber = new Integer(strRoundNumber).intValue() - 1;
            String strPlayer = extractNodeValue(nnm, "player", "");
            Player p;
            if (importedDataVersion <= 200)
                p = tournament.getPlayerByObsoleteCanonicalName(strPlayer);
            else p = tournament.getPlayerByKeyString(strPlayer);
            if (p == null) {
                strPlayer = Gotha.forceToASCII(strPlayer).toUpperCase();
                p = tournament.getPlayerByObsoleteCanonicalName(strPlayer);
            }
            if (p == null) continue;
            byePlayers[roundNumber] = p;
        }

        return byePlayers;
    }

    public static void generateXMLFile(TournamentInterface tournament, File xmlFile) {
        Writer output;
        try {
            output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(xmlFile), DEFAULT_CHARSET));
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
            String strMessage = "Unreachable file";

            return;
        }
        generateXML(tournament, output);
        try {
            output.close();
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String generateXMLString(TournamentInterface tournament) {
        Writer output;
        output = new BufferedWriter(new StringWriter());
        generateXML(tournament, output);
        String xmlContent = output.toString();
        try {
            output.close();
        } catch (IOException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
        return xmlContent;
    }

    public static void generateXML(TournamentInterface tournament, Writer output) {
        DocumentBuilderFactory documentBuilderFactory =
                DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
        Document document = documentBuilder.newDocument();

        Element rootElement = document.createElement("Tournament");
        rootElement.setAttribute("dataVersion", "" + Gotha.GOTHA_DATA_VERSION);
        document.appendChild(rootElement);

        // Include players
        ArrayList<Player> alPlayers = null;
        alPlayers = tournament.playersList();
        Element emPlayers = generateXMLPlayersElement(document, alPlayers);
        rootElement.appendChild(emPlayers);

        // Include games
        ArrayList<Game> alGames = null;
        alGames = tournament.gamesList();
        Element emGames = generateXMLGamesElement(document, alGames);
        rootElement.appendChild(emGames);

        // Include bye players if any
        Player[] byePlayers = null;
        byePlayers = tournament.getByePlayers();
        Element emByePlayers = generateXMLByePlayersElement(document, byePlayers);
        if (emByePlayers != null) {
            rootElement.appendChild(emByePlayers);
        }

        // Include teams if any
        ArrayList<Team> alTeams = null;
        alTeams = tournament.teamsList();
        if (!alTeams.isEmpty()) {
            Element emTeams = generateXMLTeamsElement(document, alTeams);
            rootElement.appendChild(emTeams);
        }

        // Include tournament parameters
        TournamentParameterSet tps = null;
        tps = tournament.getTournamentParameterSet();
        Element emTournamentParameterSet = generateXMLTournamentParameterSetElement(document, tps);
        rootElement.appendChild(emTournamentParameterSet);

        // Include team tournament parameters
        TeamTournamentParameterSet ttps = null;
        ttps = tournament.getTeamTournamentParameterSet();
        Element emTeamTournamentParameterSet = generateXMLTeamTournamentParameterSetElement(document, ttps);
        rootElement.appendChild(emTeamTournamentParameterSet);


        // Include Clubs groups
        ArrayList<ClubsGroup> alClubsGroup = new ArrayList<ClubsGroup>();
        alClubsGroup = tournament.clubsGroupsList();

        if (!alClubsGroup.isEmpty()) {
            Element emClubsGroups = generateXMLClubsGroupsElement(document, alClubsGroup);
            rootElement.appendChild(emClubsGroups);
        }

        // Transform document into a DOM source
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, ExternalDocument.DEFAULT_CHARSET);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        DOMSource source = new DOMSource(document);

        StreamResult result = new StreamResult(output);
        try {
            transformer.transform(source, result);
        } catch (TransformerException ex) {
            Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Generates an xml players Liste Element and includes all players from alPlayers
     * returns the Element
     */
    private static Element generateXMLPlayersElement(Document document, ArrayList<Player> alPlayers) {
        Element emPlayers = document.createElement("Players");
        for (Player p : alPlayers) {
            String strName = p.getName();
            String strFirstName = p.getFirstName();
            String strCountry = p.getCountry();
            String strClub = p.getClub();
            String strEgfPin = p.getEgfPin();
            String strFfgLicence = p.getFfgLicence();
            String strFfgLicenceStatus = p.getFfgLicenceStatus();
            String strAgaId = p.getAgaId();
            String strAgaExpirationDate = p.getAgaExpirationDate();
            String strRank = Player.convertIntToKD(p.getRank());
            String strRating = Integer.valueOf(p.getRating()).toString();
            String strRatingOrigin = p.getRatingOrigin();
            String strGrade = p.getStrGrade();
            String strSMMSCorrection = Integer.valueOf(p.getSmmsCorrection()).toString();
            boolean[] part = p.getParticipating();
            String strParticipating = "";
            for (int r = 0; r < Gotha.MAX_NUMBER_OF_ROUNDS; r++) {
                if (part[r]) {
                    strParticipating += "1";
                } else {
                    strParticipating += "0";
                }
            }
            String strRegisteringStatus = p.getRegisteringStatus();

            Element emPlayer = document.createElement("Player");
            emPlayer.setAttribute("name", strName);
            emPlayer.setAttribute("firstName", strFirstName);
            emPlayer.setAttribute("country", strCountry);
            emPlayer.setAttribute("club", strClub);
            emPlayer.setAttribute("egfPin", strEgfPin);
            emPlayer.setAttribute("ffgLicence", strFfgLicence);
            emPlayer.setAttribute("ffgLicenceStatus", strFfgLicenceStatus);
            emPlayer.setAttribute("agaId", strAgaId);
            emPlayer.setAttribute("agaExpirationDate", strAgaExpirationDate);
            emPlayer.setAttribute("rank", strRank);
            emPlayer.setAttribute("rating", strRating);
            emPlayer.setAttribute("ratingOrigin", strRatingOrigin);
            emPlayer.setAttribute("grade", strGrade);
            emPlayer.setAttribute("smmsCorrection", strSMMSCorrection);
            emPlayer.setAttribute("participating", strParticipating);
            emPlayer.setAttribute("registeringStatus", strRegisteringStatus);

            emPlayers.appendChild(emPlayer);
        }

        return emPlayers;
    }

    /**
     * Generates an xml games list Element and includes all games from alGames()
     * returns the Element
     */
    private static Element generateXMLGamesElement(Document document, ArrayList<Game> alGames) {
        Element emGames = document.createElement("Games");
        for (Game g : alGames) {
            String strRoundNumber = Integer.valueOf(g.getRoundNumber() + 1).toString();
            String strTableNumber = Integer.valueOf(g.getTableNumber() + 1).toString();
            String strWhitePlayer = g.getWhitePlayer().getKeyString();
            String strBlackPlayer = g.getBlackPlayer().getKeyString();
            String strKnownColor = g.isKnownColor() ? "true" : "false";
            String strHandicap = Integer.valueOf(g.getHandicap()).toString();
            String strResult;
            switch (g.getResult()) {
                case Game.RESULT_WHITEWINS:
                    strResult = "RESULT_WHITEWINS";
                    break;
                case Game.RESULT_BLACKWINS:
                    strResult = "RESULT_BLACKWINS";
                    break;
                case Game.RESULT_EQUAL:
                    strResult = "RESULT_EQUAL";
                    break;
                case Game.RESULT_BOTHWIN:
                    strResult = "RESULT_BOTHWIN";
                    break;
                case Game.RESULT_BOTHLOSE:
                    strResult = "RESULT_BOTHLOSE";
                    break;
                case Game.RESULT_WHITEWINS_BYDEF:
                    strResult = "RESULT_WHITEWINS_BYDEF";
                    break;
                case Game.RESULT_BLACKWINS_BYDEF:
                    strResult = "RESULT_BLACKWINS_BYDEF";
                    break;
                case Game.RESULT_EQUAL_BYDEF:
                    strResult = "RESULT_EQUAL_BYDEF";
                    break;
                case Game.RESULT_BOTHWIN_BYDEF:
                    strResult = "RESULT_BOTHWIN_BYDEF";
                    break;
                case Game.RESULT_BOTHLOSE_BYDEF:
                    strResult = "RESULT_BOTHLOSE_BYDEF";
                    break;
                default:
                    strResult = "RESULT_UNKNOWN";
            }

            Element emGame = document.createElement("Game");
            emGame.setAttribute("roundNumber", strRoundNumber);
            emGame.setAttribute("tableNumber", strTableNumber);
            emGame.setAttribute("whitePlayer", strWhitePlayer);
            emGame.setAttribute("blackPlayer", strBlackPlayer);
            emGame.setAttribute("knownColor", strKnownColor);
            emGame.setAttribute("handicap", strHandicap);
            emGame.setAttribute("result", strResult);

            emGames.appendChild(emGame);
        }
        return emGames;
    }

    /**
     * Generates an xml teams list Element and includes all teams from alTeams()
     * returns the Element
     */
    private static Element generateXMLTeamsElement(Document document, ArrayList<Team> alTeams) {
        Element emTeams = document.createElement("Teams");
        for (Team t : alTeams) {
            String strTeamNumber = Integer.valueOf(t.getTeamNumber() + 1).toString();
            String strTeamName = t.getTeamName();
            Element emTeam = document.createElement("Team");
            emTeam.setAttribute("teamNumber", strTeamNumber);
            emTeam.setAttribute("teamName", strTeamName);
            for (int ir = 0; ir < Gotha.MAX_NUMBER_OF_ROUNDS; ir++) {
                String strRoundNumber = Integer.valueOf(ir + 1).toString();
                for (int ibn = 0; ibn < Gotha.MAX_NUMBER_OF_MEMBERS_BY_TEAM; ibn++) {
                    Player p = t.getTeamMember(ir, ibn);
                    if (p == null) {
                        continue;
                    }
                    String strBoardNumber = Integer.valueOf(ibn + 1).toString();
                    String strPlayer = p.getKeyString();
                    Element emBoard = document.createElement("Board");
                    emBoard.setAttribute("roundNumber", strRoundNumber);
                    emBoard.setAttribute("boardNumber", strBoardNumber);
                    emBoard.setAttribute("player", strPlayer);
                    emTeam.appendChild(emBoard);
                }
            }
            emTeams.appendChild(emTeam);
        }
        if (emTeams.hasChildNodes()) {
            return emTeams;
        } else {
            return null;
        }
    }

    /**
     * Generates an xml clubsgroups Element
     * returns the Element
     */
    private static Element generateXMLClubsGroupsElement(Document document, ArrayList<ClubsGroup> alClubsGroups) {
        Element emClubsGroups = document.createElement("ClubsGroups");
        for (ClubsGroup cg : alClubsGroups) {
            String strCGName = cg.getName();
            Element emClubsGroup = document.createElement("ClubsGroup");
            emClubsGroup.setAttribute("name", strCGName);
            for (Club club : cg.getHmClubs().values()) {
                String strClub = club.getName();
                Element emClub = document.createElement("Club");
                emClub.setAttribute("name", strClub);
                emClubsGroup.appendChild(emClub);
            }
            emClubsGroups.appendChild(emClubsGroup);
        }

        if (emClubsGroups.hasChildNodes()) {
            return emClubsGroups;
        } else {
            return null;
        }

    }


    /**
     * Generates an xml ByePlayers Element and includes all  bye players
     * returns the Element or null if no bye players
     */
    private static Element generateXMLByePlayersElement(Document document, Player[] byePlayers) {
        Element emByePlayers = document.createElement("ByePlayers");
        for (int r = 0; r < byePlayers.length; r++) {
            String strRoundNumber = Integer.valueOf(r + 1).toString();
            Player p = byePlayers[r];
            if (byePlayers[r] == null) {
                continue;
            }
            String strPlayer = byePlayers[r].getKeyString();

            Element emByePlayer = document.createElement("ByePlayer");
            emByePlayer.setAttribute("roundNumber", strRoundNumber);
            emByePlayer.setAttribute("player", strPlayer);

            emByePlayers.appendChild(emByePlayer);
        }

        if (emByePlayers.hasChildNodes()) {
            return emByePlayers;
        } else {
            return null;
        }
    }

    /**
     * Generates an xml players Liste Element and includes all players from alPlayers
     * returns the Element
     */
    private static Element generateXMLTournamentParameterSetElement(Document document, TournamentParameterSet tps) {
        Element emTournamentParameterSet = document.createElement("TournamentParameterSet");

        GeneralParameterSet gps = tps.getGeneralParameterSet();
        Element emGeneralParameterSet = document.createElement("GeneralParameterSet");
        emGeneralParameterSet.setAttribute("shortName", gps.getShortName());
        emGeneralParameterSet.setAttribute("name", gps.getName());
        emGeneralParameterSet.setAttribute("location", gps.getLocation());
        emGeneralParameterSet.setAttribute("director", gps.getDirector());
        emGeneralParameterSet.setAttribute("beginDate", new SimpleDateFormat("yyyy-MM-dd").format(gps.getBeginDate()));
        emGeneralParameterSet.setAttribute("endDate", new SimpleDateFormat("yyyy-MM-dd").format(gps.getEndDate()));
        emGeneralParameterSet.setAttribute("size", gps.getStrSize());
        emGeneralParameterSet.setAttribute("komi", gps.getStrKomi());
        emGeneralParameterSet.setAttribute("basicTime", "" + gps.getBasicTime());
        String strComplementaryTimeSystem;
        switch (gps.getComplementaryTimeSystem()) {
            case GeneralParameterSet.GEN_GP_CTS_SUDDENDEATH:
                strComplementaryTimeSystem = "SUDDENDEATH";
                break;
            case GeneralParameterSet.GEN_GP_CTS_STDBYOYOMI:
                strComplementaryTimeSystem = "STDBYOYOMI";
                break;
            case GeneralParameterSet.GEN_GP_CTS_CANBYOYOMI:
                strComplementaryTimeSystem = "CANBYOYOMI";
                break;
            case GeneralParameterSet.GEN_GP_CTS_FISCHER:
                strComplementaryTimeSystem = "FISCHER";
                break;
            default:
                strComplementaryTimeSystem = "SUDDENDEATH";
        }
        emGeneralParameterSet.setAttribute("complementaryTimeSystem", strComplementaryTimeSystem);

        emGeneralParameterSet.setAttribute("stdByoYomiTime", "" + gps.getStdByoYomiTime());
        emGeneralParameterSet.setAttribute("nbMovesCanTime", "" + gps.getNbMovesCanTime());
        emGeneralParameterSet.setAttribute("canByoYomiTime", "" + gps.getCanByoYomiTime());
        emGeneralParameterSet.setAttribute("fischerTime", "" + gps.getFischerTime());

        emGeneralParameterSet.setAttribute("numberOfRounds", "" + gps.getNumberOfRounds());
        emGeneralParameterSet.setAttribute("numberOfCategories", "" + gps.getNumberOfCategories());
        emGeneralParameterSet.setAttribute("genMMFloor", Player.convertIntToKD(gps.getGenMMFloor()));
        emGeneralParameterSet.setAttribute("genMMBar", Player.convertIntToKD(gps.getGenMMBar()));
        emGeneralParameterSet.setAttribute("genMMZero", Player.convertIntToKD(gps.getGenMMZero()));

        emGeneralParameterSet.setAttribute("genNBW2ValueAbsent", "" + gps.getGenNBW2ValueAbsent());
        emGeneralParameterSet.setAttribute("genNBW2ValueBye", "" + gps.getGenNBW2ValueBye());
        emGeneralParameterSet.setAttribute("genMMS2ValueAbsent", "" + gps.getGenMMS2ValueAbsent());
        emGeneralParameterSet.setAttribute("genMMS2ValueBye", "" + gps.getGenMMS2ValueBye());
        emGeneralParameterSet.setAttribute("genRoundDownNBWMMS", "" + gps.isGenRoundDownNBWMMS());


        if (gps.getNumberOfCategories() > 1) {
            Element emCategories = document.createElement("Categories");
            int[] lCL = gps.getLowerCategoryLimits();
            for (int c = 0; c < lCL.length; c++) {
                Element emCategory = document.createElement("Category");
                emCategory.setAttribute("number", "" + (c + 1));
                emCategory.setAttribute("lowerLimit", Player.convertIntToKD(lCL[c]));
                emCategories.appendChild(emCategory);
            }
            if (emCategories.hasChildNodes()) {
                emGeneralParameterSet.appendChild(emCategories);
            }
        }

        emTournamentParameterSet.appendChild(emGeneralParameterSet);

        // HandicapParameterSet
        HandicapParameterSet hps = tps.getHandicapParameterSet();
        Element emHandicapParameterSet = document.createElement("HandicapParameterSet");
        emHandicapParameterSet.setAttribute("hdBasedOnMMS", Boolean.valueOf(hps.isHdBasedOnMMS()).toString());
        emHandicapParameterSet.setAttribute("hdNoHdRankThreshold", Player.convertIntToKD(hps.getHdNoHdRankThreshold()));
        emHandicapParameterSet.setAttribute("hdCorrection", "" + hps.getHdCorrection());
        emHandicapParameterSet.setAttribute("hdCeiling", "" + hps.getHdCeiling());

        emTournamentParameterSet.appendChild(emHandicapParameterSet);

        // PlacementParameterSet
        PlacementParameterSet pps = tps.getPlacementParameterSet();
        Element emPlacementParameterSet = document.createElement("PlacementParameterSet");

        Element emPlacementCriteria = document.createElement("PlacementCriteria");
        int[] plaC = pps.getPlaCriteria();
        for (int c = 0; c < plaC.length; c++) {
            Element emPlacementCriterion = document.createElement("PlacementCriterion");
            emPlacementCriterion.setAttribute("number", "" + (c + 1));
            emPlacementCriterion.setAttribute("name", PlacementParameterSet.criterionLongName(plaC[c]));
            emPlacementCriteria.appendChild(emPlacementCriterion);
        }
        emPlacementParameterSet.appendChild(emPlacementCriteria);

        emTournamentParameterSet.appendChild(emPlacementParameterSet);

        // PairingParameterSet
        PairingParameterSet paiPS = tps.getPairingParameterSet();
        Element emPairingParameterSet = document.createElement("PairingParameterSet");
        emPairingParameterSet.setAttribute("paiStandardNX1Factor", "" + paiPS.getPaiStandardNX1Factor());
        emPairingParameterSet.setAttribute("paiBaAvoidDuplGame", "" + paiPS.getPaiBaAvoidDuplGame());
        emPairingParameterSet.setAttribute("paiBaRandom", "" + paiPS.getPaiBaRandom());
        emPairingParameterSet.setAttribute("paiBaDeterministic", "" + paiPS.isPaiBaDeterministic());
        emPairingParameterSet.setAttribute("paiBaBalanceWB", "" + paiPS.getPaiBaBalanceWB());

        emPairingParameterSet.setAttribute("paiMaAvoidMixingCategories", "" + paiPS.getPaiMaAvoidMixingCategories());
        emPairingParameterSet.setAttribute("paiMaMinimizeScoreDifference", "" + paiPS.getPaiMaMinimizeScoreDifference());
        emPairingParameterSet.setAttribute("paiMaDUDDWeight", "" + paiPS.getPaiMaDUDDWeight());
        emPairingParameterSet.setAttribute("paiMaCompensateDUDD", "" + paiPS.isPaiMaCompensateDUDD());
        String strPaiMaDUDDUpperMode;
        switch (paiPS.getPaiMaDUDDUpperMode()) {
            case PairingParameterSet.PAIMA_DUDD_TOP:
                strPaiMaDUDDUpperMode = "TOP";
                break;
            case PairingParameterSet.PAIMA_DUDD_MID:
                strPaiMaDUDDUpperMode = "MID";
                break;
            case PairingParameterSet.PAIMA_DUDD_BOT:
                strPaiMaDUDDUpperMode = "BOT";
                break;
            default:
                strPaiMaDUDDUpperMode = "MID";
        }
        emPairingParameterSet.setAttribute("paiMaDUDDUpperMode", strPaiMaDUDDUpperMode);
        String strPaiMaDUDDLowerMode;
        switch (paiPS.getPaiMaDUDDLowerMode()) {
            case PairingParameterSet.PAIMA_DUDD_TOP:
                strPaiMaDUDDLowerMode = "TOP";
                break;
            case PairingParameterSet.PAIMA_DUDD_MID:
                strPaiMaDUDDLowerMode = "MID";
                break;
            case PairingParameterSet.PAIMA_DUDD_BOT:
                strPaiMaDUDDLowerMode = "BOT";
                break;
            default:
                strPaiMaDUDDLowerMode = "MID";
        }
        emPairingParameterSet.setAttribute("paiMaDUDDLowerMode", strPaiMaDUDDLowerMode);
        emPairingParameterSet.setAttribute("paiMaMaximizeSeeding", "" + paiPS.getPaiMaMaximizeSeeding());
        emPairingParameterSet.setAttribute("paiMaLastRoundForSeedSystem1", "" + (paiPS.getPaiMaLastRoundForSeedSystem1() + 1));
        String strPaiMaSeedSystem1;
        switch (paiPS.getPaiMaSeedSystem1()) {
            case PairingParameterSet.PAIMA_SEED_SPLITANDRANDOM:
                strPaiMaSeedSystem1 = "SPLITANDRANDOM";
                break;
            case PairingParameterSet.PAIMA_SEED_SPLITANDFOLD:
                strPaiMaSeedSystem1 = "SPLITANDFOLD";
                break;
            case PairingParameterSet.PAIMA_SEED_SPLITANDSLIP:
                strPaiMaSeedSystem1 = "SPLITANDSLIP";
                break;
            default:
                strPaiMaSeedSystem1 = "SPLITANDFOLD";
        }
        emPairingParameterSet.setAttribute("paiMaSeedSystem1", strPaiMaSeedSystem1);
        String strPaiMaSeedSystem2;
        switch (paiPS.getPaiMaSeedSystem1()) {
            case PairingParameterSet.PAIMA_SEED_SPLITANDRANDOM:
                strPaiMaSeedSystem2 = "SPLITANDRANDOM";
                break;
            case PairingParameterSet.PAIMA_SEED_SPLITANDFOLD:
                strPaiMaSeedSystem2 = "SPLITANDFOLD";
                break;
            case PairingParameterSet.PAIMA_SEED_SPLITANDSLIP:
                strPaiMaSeedSystem2 = "SPLITANDSLIP";
                break;
            default:
                strPaiMaSeedSystem2 = "SPLITANDFOLD";
        }
        emPairingParameterSet.setAttribute("paiMaSeedSystem1", strPaiMaSeedSystem2);
        emPairingParameterSet.setAttribute("paiMaAdditionalPlacementCritSystem1",
                PlacementParameterSet.criterionLongName(paiPS.getPaiMaAdditionalPlacementCritSystem1()));
        emPairingParameterSet.setAttribute("paiMaAdditionalPlacementCritSystem2",
                PlacementParameterSet.criterionLongName(paiPS.getPaiMaAdditionalPlacementCritSystem2()));

        emPairingParameterSet.setAttribute("paiSeRankThreshold", "" + Player.convertIntToKD(paiPS.getPaiSeRankThreshold()));
        emPairingParameterSet.setAttribute("paiSeNbWinsThresholdActive", "" + paiPS.isPaiSeNbWinsThresholdActive());
        emPairingParameterSet.setAttribute("paiSeBarThresholdActive", "" + paiPS.isPaiSeBarThresholdActive());
        emPairingParameterSet.setAttribute("paiSeDefSecCrit", "" + paiPS.getPaiSeDefSecCrit());
        emPairingParameterSet.setAttribute("paiSeMinimizeHandicap", "" + paiPS.getPaiSeMinimizeHandicap());
        emPairingParameterSet.setAttribute("paiSeAvoidSameGeo", "" + paiPS.getPaiSeAvoidSameGeo());
        emPairingParameterSet.setAttribute("paiSePreferMMSDiffRatherThanSameCountry", "" + paiPS.getPaiSePreferMMSDiffRatherThanSameCountry());
        emPairingParameterSet.setAttribute("paiSePreferMMSDiffRatherThanSameClubsGroup", "" + paiPS.getPaiSePreferMMSDiffRatherThanSameClubsGroup());
        emPairingParameterSet.setAttribute("paiSePreferMMSDiffRatherThanSameClub", "" + paiPS.getPaiSePreferMMSDiffRatherThanSameClub());

        emTournamentParameterSet.appendChild(emPairingParameterSet);

        // DPParameterSet
        DPParameterSet dpps = tps.getDPParameterSet();
        Element emDPParameterSet = document.createElement("DPParameterSet");
        String strPlayerSortType;
        switch (dpps.getPlayerSortType()) {
            case PlayerComparator.NAME_ORDER:
                strPlayerSortType = "name";
                break;
            case PlayerComparator.RANK_ORDER:
                strPlayerSortType = "rank";
                break;
            case PlayerComparator.GRADE_ORDER:
                strPlayerSortType = "grade";
                break;
            default:
                strPlayerSortType = "name";
        }
        emDPParameterSet.setAttribute("playerSortType", strPlayerSortType);

        String strGameFormat;
        switch (dpps.getGameFormat()) {
            case DPParameterSet.DP_GAME_FORMAT_FULL:
                strGameFormat = "full";
                break;
            case DPParameterSet.DP_GAME_FORMAT_SHORT:
                strGameFormat = "short";
                break;
            default:
                strGameFormat = "full";
        }
        emDPParameterSet.setAttribute("gameFormat", strGameFormat);

        emDPParameterSet.setAttribute("showPlayerGrade", Boolean.valueOf(dpps.isShowPlayerGrade()).toString());
        emDPParameterSet.setAttribute("showPlayerCountry", Boolean.valueOf(dpps.isShowPlayerCountry()).toString());
        emDPParameterSet.setAttribute("showPlayerClub", Boolean.valueOf(dpps.isShowPlayerClub()).toString());

        emDPParameterSet.setAttribute("showByePlayer", Boolean.valueOf(dpps.isShowByePlayer()).toString());
        emDPParameterSet.setAttribute("showNotPairedPlayers", Boolean.valueOf(dpps.isShowNotPairedPlayers()).toString());
        emDPParameterSet.setAttribute("showNotParticipatingPlayers", Boolean.valueOf(dpps.isShowNotParticipatingPlayers()).toString());
        emDPParameterSet.setAttribute("showNotFinallyRegisteredPlayers", Boolean.valueOf(dpps.isShowNotFinallyRegisteredPlayers()).toString());

        emDPParameterSet.setAttribute("displayNumCol", Boolean.valueOf(dpps.isDisplayNumCol()).toString());
        emDPParameterSet.setAttribute("displayPlCol", Boolean.valueOf(dpps.isDisplayPlCol()).toString());
        emDPParameterSet.setAttribute("displayCoCol", Boolean.valueOf(dpps.isDisplayCoCol()).toString());
        emDPParameterSet.setAttribute("displayClCol", Boolean.valueOf(dpps.isDisplayClCol()).toString());
        emDPParameterSet.setAttribute("displayIndGamesInMatches", Boolean.valueOf(dpps.isDisplayIndGamesInMatches()).toString());

        emTournamentParameterSet.appendChild(emDPParameterSet);

        // PublishParameterSet
        PublishParameterSet pubPS = tps.getPublishParameterSet();
        Element emPublishParameterSet = document.createElement("PublishParameterSet");

        emPublishParameterSet.setAttribute("print", Boolean.valueOf(pubPS.isPrint()).toString());
        emPublishParameterSet.setAttribute("exportToLocalFile", Boolean.valueOf(pubPS.isExportToLocalFile()).toString());
        emPublishParameterSet.setAttribute("exportHFToOGSite", Boolean.valueOf(pubPS.isExportHFToOGSite()).toString());
        emPublishParameterSet.setAttribute("exportTFToOGSite", Boolean.valueOf(pubPS.isExportTFToOGSite()).toString());
        emPublishParameterSet.setAttribute("exportToUDSite", Boolean.valueOf(pubPS.isExportToUDSite()).toString());
        emPublishParameterSet.setAttribute("htmlAutoScroll", Boolean.valueOf(pubPS.isHtmlAutoScroll()).toString());

        emTournamentParameterSet.appendChild(emPublishParameterSet);

        return emTournamentParameterSet;
    }

    private static Element generateXMLTeamTournamentParameterSetElement(Document document, TeamTournamentParameterSet ttps) {
        Element emTeamTournamentParameterSet = document.createElement("TeamTournamentParameterSet");

        TeamGeneralParameterSet tgps = ttps.getTeamGeneralParameterSet();
        Element emTeamGeneralParameterSet = document.createElement("TeamGeneralParameterSet");
        emTeamGeneralParameterSet.setAttribute("teamSize", "" + tgps.getTeamSize());

        emTeamTournamentParameterSet.appendChild(emTeamGeneralParameterSet);

        // TeamPlacementParameterSet
        TeamPlacementParameterSet tpps = ttps.getTeamPlacementParameterSet();
        Element emTeamPlacementParameterSet = document.createElement("TeamPlacementParameterSet");

        Element emPlacementCriteria = document.createElement("PlacementCriteria");
        int[] plaC = tpps.getPlaCriteria();
        for (int c = 0; c < plaC.length; c++) {
            Element emPlacementCriterion = document.createElement("PlacementCriterion");
            emPlacementCriterion.setAttribute("number", "" + (c + 1));
            emPlacementCriterion.setAttribute("name", TeamPlacementParameterSet.criterionLongName(plaC[c]));
            emPlacementCriteria.appendChild(emPlacementCriterion);
        }
        emTeamPlacementParameterSet.appendChild(emPlacementCriteria);

        emTeamTournamentParameterSet.appendChild(emTeamPlacementParameterSet);

        return emTeamTournamentParameterSet;
    }

    private static ArrayList<ClubsGroup> importClubsGroupsFromXMLFile(File sourceFile) {
        Document doc = getDocumentFromXMLFile(sourceFile);
        if (doc == null) {
            return null;
        }

        NodeList nlClubsGroupList = doc.getElementsByTagName("ClubsGroup");
        // Is there a ClubsGroups node in file ?
        if (nlClubsGroupList == null || nlClubsGroupList.getLength() == 0) {
            return null;
        }

        ArrayList<ClubsGroup> alClubsGroups = new ArrayList<ClubsGroup>();
        for(int i = 0; i < nlClubsGroupList.getLength(); i++){
            Node nClubsGroup = nlClubsGroupList.item(i);
            NamedNodeMap nnmClubsGroup = nClubsGroup.getAttributes();
            String strCGName = extractNodeValue(nnmClubsGroup, "name", "Unnamed Clubs Group");
            ClubsGroup cg = new ClubsGroup(strCGName);

            NodeList nlElements = nClubsGroup.getChildNodes();
            for(int iel = 0; iel < nlElements.getLength(); iel++){
                Node nClub = nlElements.item(iel);
                if (nClub.getNodeType() != Node.ELEMENT_NODE) continue;
                if (!nClub.getNodeName().equals("Club")) continue;
                NamedNodeMap nnmClub = nClub.getAttributes();
                String strClubName = extractNodeValue(nnmClub, "name", "Unnamed club");
                Club club = new Club(strClubName);
                cg.put(club);
            }
            alClubsGroups.add(cg);
        }
        return alClubsGroups;
    }

    public static String extractNodeValue(NamedNodeMap nnm, String attributeName, String defaultValue) {
        String value = defaultValue;
        Node node = nnm.getNamedItem(attributeName);
        if (node != null) {
            value = node.getNodeValue();
        }
        return value;
    }

    public static int extractNodeIntValue(NamedNodeMap nnm, String attributeName, int defaultValue) {
        String strValue = extractNodeValue(nnm, attributeName, "");
        int value = defaultValue;
        try {
            value = Integer.parseInt(strValue);
        } catch (Exception e) {
        }
        return value;
    }

}
