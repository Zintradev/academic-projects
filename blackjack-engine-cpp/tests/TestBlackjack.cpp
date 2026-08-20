#include <iostream>
#include <cassert>
#include "Card.h"
#include "Deck.h"
#include "Player.h"
#include "Game.h"

void testCardCreation() {
    Card c("Hearts", 10, "K");
    assert(c.getSuit() == "Hearts");
    assert(c.getValue() == 10);
    assert(c.getName() == "K");
    std::cout << "Card creation tests passed." << std::endl;
}

void testDeckGenerationAndDraw() {
    Deck deck;
    deck.generateDeck();
    assert(deck.getCards().size() == 52);

    Card drawn = deck.drawCard();
    assert(deck.getCards().size() == 51);
    assert(drawn.getSuit() != "");
    std::cout << "Deck generation and draw tests passed." << std::endl;
}

void testPlayerScoreCalculation() {
    Game game;
    Player p;
    p.setName("TestPlayer");

    // Hand [K, 5] -> score 15
    p.addCard(Card("Clubs", 10, "K"));
    p.addCard(Card("Diamonds", 5, "5"));
    game.updateScore(p);
    assert(p.getScore() == 15);

    // Hand [K, 5, A] -> score 16 (Ace becomes 1 instead of 11, otherwise 26)
    p.addCard(Card("Hearts", 11, "A"));
    game.updateScore(p);
    assert(p.getScore() == 16);

    p.clearHand();
    // Hand [A, A] -> score 12 (One Ace is 11, one Ace is 1, total 12)
    p.addCard(Card("Hearts", 11, "A"));
    p.addCard(Card("Spades", 11, "A"));
    game.updateScore(p);
    assert(p.getScore() == 12);

    // Hand [A, A, A] -> score 13
    p.addCard(Card("Clubs", 11, "A"));
    game.updateScore(p);
    assert(p.getScore() == 13);

    std::cout << "Player score calculation tests passed." << std::endl;
}

int main() {
    std::cout << "Running Simple Blackjack tests..." << std::endl;
    testCardCreation();
    testDeckGenerationAndDraw();
    testPlayerScoreCalculation();
    std::cout << "All tests passed successfully!" << std::endl;
    return 0;
}
