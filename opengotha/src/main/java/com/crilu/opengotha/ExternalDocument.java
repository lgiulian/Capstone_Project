package com.crilu.opengotha;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
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
		// TODO Auto-generated method stub
		return null;
	}

	public static String importTournamentFromXMLFile(File f, TournamentInterface t, boolean b, boolean c, boolean d,
			boolean e, boolean f2) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void generateXMLFile(TournamentInterface tournament, File xmlFile) {
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

		// generate file
		Writer output = null;
		try {
			output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(xmlFile), DEFAULT_CHARSET));
		} catch (IOException ex) {
			Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
			String strMessage = "Unreachable file";

			return;
		}

		StreamResult result = new StreamResult(output);
		try {
			transformer.transform(source, result);
		} catch (TransformerException ex) {
			Logger.getLogger(ExternalDocument.class.getName()).log(Level.SEVERE, null, ex);
		}
		try {
			output.close();
		} catch (IOException ex) {
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
			for ( int ir = 0; ir < Gotha.MAX_NUMBER_OF_ROUNDS; ir++){
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
			for (Club club : cg.getHmClubs().values()){
				String strClub = club.getName();
				Element emClub = document.createElement("Club");
				emClub.setAttribute("name", strClub);
				emClubsGroup.appendChild(emClub);
			}
			emClubsGroups.appendChild(emClubsGroup);
		}

		if(emClubsGroups.hasChildNodes()){
			return emClubsGroups;
		}
		else {
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

}
