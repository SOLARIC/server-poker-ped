package poker.server.model.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import poker.server.model.game.Game;
import poker.server.model.game.card.Card;
import poker.server.model.game.card.Value;

/**
 * This class is used to compare a set of hands. Their functions are used at the
 * showdown round, to sort the player by rank, and create a ranking list.
 * 
 * @author <b> Rafik Ferroukh </b> <br>
 *         <b> Lucas Kerdoncuff </b> <br>
 *         <b> Xan Lucu </b> <br>
 *         <b> Youga Mbaye </b> <br>
 *         <b> Balla Seck </b> <br>
 * <br>
 *         University Bordeaux 1, Software Engineering, Master 2 <br>
 * 
 * @see Game
 */
public class CompareHands {

	/**
	 * Main function of this class. Return the ranking of given players.
	 * 
	 * @param playersWithHands
	 *            map associating players with their hand value
	 * @see Hand
	 * 
	 * @return ranking map associating players with an integer value (rank).
	 */
	public static Map<Player, Integer> getRanking(
			Map<Player, Integer> playersWithHands) {

		Map<Player, Integer> ranking = new HashMap<Player, Integer>();
		ranking = initRanks(playersWithHands, 0);

		List<Player> playersToCompare = new ArrayList<Player>();

		Player player;
		int handValue, worstRank;

		Iterator<Entry<Player, Integer>> it;

		for (int hv = 9; hv >= 0; hv--) {

			it = playersWithHands.entrySet().iterator();
			while (it.hasNext()) {

				Entry<Player, Integer> pairs = it.next();
				player = pairs.getKey();
				handValue = pairs.getValue();

				if (handValue == hv) {
					playersToCompare.add(player);
				}
			}

			if (playersToCompare.size() > 0) {
				worstRank = getWorstRank(ranking);
				setMinRankTo(ranking, worstRank + 1, playersToCompare);
				compareAllHands(ranking, playersToCompare, hv);
				playersToCompare.clear();
			}
		}

		return ranking;
	}

	/**
	 * Modify an existing ranking map, comparing given players. All players must
	 * have the same kind of hand.
	 * 
	 * @param ranking
	 *            existing ranking map to modify
	 * 
	 * @param playersToCompare
	 *            A list of players to compare.
	 * 
	 * @param handValue
	 *            An integer value to specify the kind of hand to deal with.
	 * @see <@links Hand>
	 */
	public static void compareAllHands(Map<Player, Integer> ranking,
			List<Player> playersToCompare, int handValue) {

		Player ref, current;
		Hand refHand, currentHand;
		Hand sortedRefHand, sortedCurrentHand;
		int result, refRank, currentRank;

		if (playersToCompare.size() >= 2) {
			for (int i = 0; i < playersToCompare.size(); i++) {

				ref = playersToCompare.get(i);

				refHand = ref.getBestHand();
				sortedRefHand = sortHand(refHand);

				for (int j = i + 1; j < playersToCompare.size(); j++) {

					current = playersToCompare.get(j);
					currentHand = current.getBestHand();
					sortedCurrentHand = sortHand(currentHand);

					result = compareHands(sortedRefHand, sortedCurrentHand,
							handValue);

					switch (result) {
					case 1:
						refRank = ranking.get(ref);
						currentRank = refRank + 1;
						ranking.put(current, currentRank);
						break;
					case -1:
						currentRank = ranking.get(ref);
						ranking = updateRanksFor(ranking, currentRank);
						ranking.put(current, currentRank);
						break;
					default:
						break;
					}
				}
			}
		}
	}

	public static int compareHands(Hand hand1, Hand hand2, Integer handValue) {

		int result = 0;

		switch (handValue) {
		case 0:
			result = compareHightestCards(hand1, hand2);
			break;
		case 1:
			result = compareOnePair(hand1, hand2);
			break;
		case 2:
			result = compareTwoPair(hand1, hand2);
			break;
		case 3:
			result = compareTrips(hand1, hand2);
			break;
		case 4:
			result = compareStraight(hand1, hand2);
			break;
		case 5:
			result = compareFlush(hand1, hand2);
			break;
		case 6:
			result = compareFullHouse(hand1, hand2);
			break;
		case 7:
			result = compareQuads(hand1, hand2);
			break;
		case 8:
			result = compareStraightFlush(hand1, hand2);
			break;
		default:
			break;
		}

		return result;
	}

	// ////////////////////////////////////////////
	// TOOLS
	// //////////////////////////////////////////////
	// ////////////////////////////////////////////////////

	/**
	 * 
	 * @param hand
	 *            the hand to sort
	 * @return the hand sorted
	 */
	public static Hand sortHand(Hand hand) {

		Hand sortedHand = new Hand();

		List<Card> aces = new ArrayList<Card>();
		List<Card> rest = new ArrayList<Card>();

		hand.sort(hand.getCards());

		for (Card card : hand.getCards()) {
			if (card.getValue() == Value.ACE) {
				aces.add(card);
			} else {
				rest.add(card);
			}
		}

		sortedHand.addCards(rest);
		sortedHand.addCards(aces);

		return sortedHand;
	}

	/**
	 * Function used in all comparaisons from this class. Compare two integer
	 * values.
	 * 
	 * @param rank1
	 *            first value
	 * 
	 * @param rank2
	 *            second value.
	 * 
	 * @return result this integer value could be : 1 = rank1 > rank2 -1 = rank1
	 *         < rank2 0 = rank1 = rank2
	 * 
	 */
	public static int compareRanks(int rank1, int rank2) {

		int result = 0;

		if (rank1 != rank2) {

			if (rank1 == Value.ACE) {
				result = 1;
			} else if (rank2 == Value.ACE) {
				result = -1;
			} else {

				if (rank1 > rank2) {
					result = 1;
				} else if (rank1 < rank2) {
					result = -1;
				}
			}
		}

		return result;
	}

	public static int nbSameCards(Hand hand, Card card) {
		int nb = 0;
		Card cardTmp = null;

		for (int i = 0; i < hand.getCards().size(); i++) {
			cardTmp = hand.getCards().get(i);

			if (cardTmp.getValue() == card.getValue()) {
				nb++;
			}
		}

		return nb;
	}

	public static Boolean sameHand(Hand hand1, Hand hand2) {
		boolean sameHand = true;

		Card card1 = null;
		Card card2 = null;

		for (int i = 0; i < hand1.getSize(); i++) {
			card1 = hand1.getCards().get(i);
			card2 = hand2.getCards().get(i);

			if (card1.getValue() != card2.getValue()) {
				sameHand = false;
				break;
			}
		}

		return sameHand;
	}

	public static boolean haveSameHand(List<Player> players) {

		boolean haveSameHand = true;

		if (players.size() >= 2) {

			Player ref = players.get(0);
			Player player;

			for (int i = 1; i < players.size(); i++) {
				player = players.get(i);

				if (!sameHand(ref.getCurrentHand(), player.getCurrentHand())) {
					haveSameHand = false;
					break;
				}
			}
		}

		return haveSameHand;
	}

	/**
	 * Update all ranks superior or equal to a given integer value.
	 * 
	 * @param ranking
	 *            A ranking map to modify.
	 * 
	 * @param rank
	 *            A reference integer value. all the ranks, superior or equal to
	 *            this value will be updated.
	 * 
	 * @return updatedRanking A new ranking map.
	 */
	public static Map<Player, Integer> updateRanksFor(
			Map<Player, Integer> ranking, int rank) {

		Map<Player, Integer> updatedRanking = ranking;

		Iterator<Entry<Player, Integer>> it;
		it = updatedRanking.entrySet().iterator();

		int value;

		while (it.hasNext()) {
			Entry<Player, Integer> pairs = it.next();
			value = pairs.getValue();

			if (value >= rank) {
				pairs.setValue(value + 1);
			}
		}

		return ranking;
	}

	public static int getWorstRank(Map<Player, Integer> ranking) {

		int worstRank = 0;
		int value;

		Iterator<Entry<Player, Integer>> it;
		it = ranking.entrySet().iterator();

		while (it.hasNext()) {
			Entry<Player, Integer> pairs = it.next();
			value = pairs.getValue();

			if (value > worstRank) {
				worstRank = value;
			}
		}

		return worstRank;
	}

	/**
	 * Create a new ranking map, according to a given player map. The ranking of
	 * each players will be initialized with the same value given as a second
	 * parameter.
	 * 
	 * @param players
	 *            A map associationg players with an integer value. This value
	 *            is not important.
	 * 
	 * @param rank
	 *            A default integer value. all players will have this default
	 *            rank.
	 * 
	 * @return ranking A new ranking map.
	 */
	public static Map<Player, Integer> initRanks(Map<Player, Integer> players,
			int rank) {

		Map<Player, Integer> ranking = new HashMap<Player, Integer>();

		Iterator<Entry<Player, Integer>> it;
		it = players.entrySet().iterator();
		Player player;

		while (it.hasNext()) {
			Entry<Player, Integer> pairs = it.next();
			player = pairs.getKey();

			ranking.put(player, rank);
		}

		return ranking;
	}

	public static void setMinRankTo(Map<Player, Integer> ranking, int rank,
			List<Player> players) {

		Iterator<Entry<Player, Integer>> it;
		it = ranking.entrySet().iterator();
		Player player;

		while (it.hasNext()) {
			Entry<Player, Integer> pairs = it.next();
			player = pairs.getKey();

			if (players.contains(player)) {
				ranking.put(player, rank);
			}
		}
	}

	public static void displayMap(Map<Player, Integer> map) {

		Iterator<Entry<Player, Integer>> it;
		it = map.entrySet().iterator();
		Player player;
		int value;

		System.out.println("//////////////////////////");

		while (it.hasNext()) {

			Entry<Player, Integer> pairs = it.next();
			player = pairs.getKey();
			value = pairs.getValue();
			System.out.println(map.get(player + " : " + value));
		}
	}

	// ////////////////////////////////////////////////////
	// / COMPARE HANDS BY TYPE
	// /////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////

	public static int compareHightestCards(Hand hand1, Hand hand2) {
		int result = 0;

		Card card1 = null;
		Card card2 = null;

		for (int i = 4; i >= 0; i--) {
			card1 = hand1.getCards().get(i);
			card2 = hand2.getCards().get(i);

			if (card1.getValue() != card2.getValue()) {
				result = compareRanks(card1.getValue(), card2.getValue());
				break;
			}
		}

		return result;
	}

	/**
	 * Get the value of the pair contained in the given hand.
	 * 
	 * @param hand
	 *            The hand containing the pair to check.
	 * 
	 * @return rank An integer value, corresponding to the card value of the
	 *         pair.
	 * @see <@links Card>
	 */
	public static int evaluatePair(Hand hand) {

		int rank = -1;
		int nbCards = 0;

		// cards from different pairs
		Card card2 = hand.getCards().get(1);
		Card card4 = hand.getCards().get(3);

		nbCards = nbSameCards(hand, card4);

		if (nbCards == 2) {
			rank = card4.getValue();
		} else {

			nbCards = nbSameCards(hand, card2);

			if (nbCards == 2) {
				rank = card2.getValue();
			}
		}

		return rank;
	}

	public static int compareOnePair(Hand hand1, Hand hand2) {

		int result = 0;
		int rankPair1 = evaluatePair(hand1);
		int rankPair2 = evaluatePair(hand2);

		if (rankPair1 == rankPair2) {
			result = compareHightestCards(hand1, hand2);
		} else {
			result = compareRanks(rankPair1, rankPair2);
		}

		return result;
	}

	/**
	 * Get the value of the two pairs contained in the given hand.
	 * 
	 * @param hand
	 *            The hand containing the pair to check.
	 * 
	 * @return ranks A list of integer values : The first value is the card
	 *         value of the best pair. The second value is the card value of the
	 *         second pair. The third value is the card value of last isolated
	 *         card.
	 * @see <@links Card>
	 */
	public static List<Integer> evaluateTwoPairs(Hand hand) {

		List<Integer> ranks = new ArrayList<Integer>();
		ranks.add(-1); // rank of the first pair
		ranks.add(-1); // rank of the second pair
		ranks.add(-1); // rank of the last card

		// cards from different pairs
		Card card2 = hand.getCards().get(1);
		Card card4 = hand.getCards().get(3);

		ranks.add(0, card4.getValue());
		ranks.add(1, card2.getValue());

		// unique card candidates
		Card card1 = hand.getCards().get(0);
		Card card3 = hand.getCards().get(2);
		Card card5 = hand.getCards().get(4);

		int nbCard1 = nbSameCards(hand, card1);
		int nbCard3 = nbSameCards(hand, card3);

		if (nbCard1 == 1) {
			ranks.add(2, card1.getValue());
		} else if (nbCard3 == 1) {
			ranks.add(2, card3.getValue());
		} else {
			ranks.add(2, card5.getValue());
		}

		return ranks;
	}

	public static int compareTwoPair(Hand hand1, Hand hand2) {

		int result = 0;

		List<Integer> ranksTwoPair1 = evaluateTwoPairs(hand1);
		List<Integer> ranksTwoPair2 = evaluateTwoPairs(hand2);

		// compare best pair
		if (ranksTwoPair1.get(0) != ranksTwoPair2.get(0)) {
			result = compareRanks(ranksTwoPair1.get(0), ranksTwoPair2.get(0));
		} else {
			// compare second pair
			if (ranksTwoPair1.get(1) != ranksTwoPair2.get(1)) {
				result = compareRanks(ranksTwoPair1.get(1),
						ranksTwoPair2.get(1));
			} else {
				// compare the last card
				result = compareRanks(ranksTwoPair1.get(2),
						ranksTwoPair2.get(2));
			}
		}

		return result;
	}

	public static int compareTrips(Hand hand1, Hand hand2) {
		int result = 0;

		int rankTrip1 = hand1.getCards().get(2).getValue();
		int rankTrip2 = hand2.getCards().get(2).getValue();

		if (rankTrip1 == rankTrip2) {
			result = compareHightestCards(hand1, hand2);
		} else {
			result = compareRanks(rankTrip1, rankTrip2);
		}

		return result;
	}

	public static int compareQuads(Hand hand1, Hand hand2) {
		int result = 0;

		int rankQuad1 = hand1.getCards().get(1).getValue();
		int rankQuad2 = hand2.getCards().get(1).getValue();

		if (rankQuad1 == rankQuad2) {
			result = compareHightestCards(hand1, hand2);
		} else {
			result = compareRanks(rankQuad1, rankQuad2);
		}

		return result;
	}

	public static int compareFullHouse(Hand hand1, Hand hand2) {
		int result = 0;

		int rankTrip1 = hand1.getCards().get(2).getValue();
		int rankTrip2 = hand2.getCards().get(2).getValue();

		int rankPair1 = evaluatePair(hand1);
		int rankPair2 = evaluatePair(hand2);

		if (rankTrip1 != rankTrip2) {
			result = compareRanks(rankTrip1, rankTrip2);
		} else if (rankPair1 != rankPair2) {
			result = compareRanks(rankPair1, rankPair2);
		}

		return result;
	}

	public static int compareStraight(Hand hand1, Hand hand2) {
		int result = 0;

		result = compareHightestCards(hand1, hand2);

		return result;
	}

	public static int compareFlush(Hand hand1, Hand hand2) {
		int result = 0;

		result = compareHightestCards(hand1, hand2);

		return result;
	}

	public static int compareStraightFlush(Hand hand1, Hand hand2) {
		int result = 0;

		result = compareHightestCards(hand1, hand2);

		return result;
	}
}