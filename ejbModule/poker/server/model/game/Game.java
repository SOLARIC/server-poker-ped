package poker.server.model.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import poker.server.model.game.card.Card;
import poker.server.model.game.card.Cards;
import poker.server.model.game.parameters.Parameters;
import poker.server.model.game.parameters.SitAndGo;
import poker.server.model.player.Player;

@Entity
public class Game implements Serializable {

	private static final long serialVersionUID = 2687924657560495636L;

	@Id
	@GeneratedValue
	private int id;

	private transient Parameters gameType;

	private transient Cards deck;

	private transient List<Card> flippedCards;

	private ArrayList<Player> players;
	private int currentPlayer = 0;

	private int dealer = 0;
	private int bigBlindPlayer = 0;
	private int smallBlindPlayer = 0;

	private int smallBlind;
	private int bigBlind;

	private int pot = 0;
	private int bets = 0;
	private int bet = 0;

	private int currentRound = 1;

	// to be used...
	public static final int FLOP = 1;
	public static final int TOURNANT = 2;
	public static final int RIVER = 3;

	private boolean Started;

	// CONSTRUCTOR
	protected Game() {
		gameType = new SitAndGo();
		buildGame();
	}

	protected Game(Parameters gameT) {
		gameType = gameT;
		buildGame();
	}

	private void buildGame() {
		deck = new Cards();
		flippedCards = new ArrayList<Card>();
		players = new ArrayList<Player>();
		smallBlind = gameType.getSmallBlind();
		bigBlind = gameType.getBigBlind();
		setStarted(false);
		Event.buildEvents();
	}

	// GETTERS / SETTERS
	public int getId() {
		return id;
	}

	public Parameters getGameType() {
		return gameType;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public int getCurrentPlayer() {
		return currentPlayer;
	}

	public int getDealer() {
		return dealer;
	}

	public int getBigBlindPlayer() {
		return bigBlindPlayer;
	}

	public int getSmallBlindPlayer() {
		return smallBlindPlayer;
	}

	public int getPot() {
		return pot;
	}

	public int getBets() {
		return bets;
	}

	public int getBet() {
		return bet;
	}

	public int getCurrentRound() {
		return currentRound;
	}

	public List<Card> getFlipedCards() {
		return flippedCards;
	}

	public void nextPlayer() {

		if (currentPlayer == (this.players.size() - 1))
			currentPlayer = 0;
		else
			currentPlayer++;
	}

	public void setDealer() {

		if (this.dealer == (this.players.size() - 1))
			this.dealer = 0;
		else
			this.dealer++;

		Event.addEvent("THE DEALER IS : " + players.get(dealer).getName());
	}

	public void setBigBlind() {

		if (this.bigBlindPlayer == (this.players.size() - 1))
			this.bigBlindPlayer = 0;
		else
			this.bigBlindPlayer++;

		Event.addEvent("THE BIG BLIND IS : "
				+ players.get(bigBlindPlayer).getName());
	}

	public void setSmallBlind() {

		if (smallBlindPlayer == (this.players.size() - 1))
			smallBlindPlayer = 0;
		else
			smallBlindPlayer++;

		Event.addEvent("THE SMALL BLIND IS : "
				+ players.get(smallBlindPlayer).getName());
	}

	// ROUND MANAGEMENT
	public Card flipCard() {
		Card card = deck.getNextCard();
		flippedCards.add(card);
		return card;
	}

	public void flop() {

		String eventFlop = "FLOP : ";
		Card card;
		deck.burnCard();

		for (int i = 0; i < 3; i++) {
			card = flipCard();
			eventFlop += card.getValue() + " " + card.getSuit() + " , ";
		}
		updatePot();
		resetBet();
		Event.addEvent(eventFlop);
	}

	public void tournant() {

		deck.burnCard();
		Card card = flipCard();
		updatePot();
		resetBet();
		Event.addEvent("TOURNANT : " + card.getValue() + " " + card.getSuit());
	}

	public void river() {

		deck.burnCard();
		Card card = flipCard();
		updatePot();
		resetBet();
		Event.addEvent("RIVER : " + card.getValue() + " " + card.getSuit());
	}

	// BLIND / BET / POT MANAGEMENT
	public void updateBlind() {

		int blindMultFactor = gameType.getBuyInIncreasing();
		smallBlind = smallBlind * blindMultFactor;
		bigBlind = bigBlind * blindMultFactor;

		Event.addEvent("SMALL BLIND = " + smallBlind + " , BIG BLIND = "
				+ bigBlind);
	}

	public void resetBet() {

		this.bet = 0;
		for (Player player : this.players) {
			player.currentBet = 0;
		}
		Event.addEvent("RESET BET");
	}

	public void updateBet(int quantity) {
<<<<<<< HEAD
		this.bet += quantity;
	}

	public void updateBets(int quantity) {
		this.bets += quantity;
	}

	public void updatePot() {
		this.pot += this.bets;
		this.bets = 0;
=======
		bet += quantity;
		Event.addEvent("BET = " + bet);
	}

	public void updateBets(int quantity) {
		bets += quantity;
		Event.addEvent("BETS = " + bets);
	}

	public void updatePot() {
		pot += bets;
		bets = 0;
		Event.addEvent("UPDATE POT, POT = " + pot);
>>>>>>> 9cdedf15b763497777789a4e67c6adf147161bd5
	}

	// OTHER
	public void dealCards() {

		Card card;
		for (int i = 0; i < 2; i++) {
			for (Player player : players) {
				card = deck.getNextCard();
				player.currentHand.addCard(card);
			}
		}
		Event.addEvent("DEAL CARDS FOR PLAYERS");
	}

	public void start() {
		System.out.println("start() : TODO");
		Event.addEvent("START GAME");
	}

	public Cards getDeck() {
		return deck;
	}

	public void add(Player player) {
		players.add(player);
	}

	public int getSmallBlind() {
		return smallBlind;
	}

	public int getBigBlind() {
		return bigBlind;
	}

	public boolean isStarted() {
		return Started;
	}

	public void setStarted(boolean started) {
		Started = started;
	}
}
