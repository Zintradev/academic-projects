#include "Deck.h"
#include <algorithm>
#include <random>

Deck::Deck() : cards({}) {}

Deck::Deck(const std::vector<Card>& cards) : cards(cards) {}

void Deck::generateDeck() {
    cards.clear();
    std::string suits[] = { "Hearts", "Diamonds", "Clubs", "Spades" };
    std::string names[] = { "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A" };
    int values[] = { 2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10, 11 };

    for (const auto& suit : suits) {
        for (size_t i = 0; i < sizeof(names) / sizeof(names[0]); ++i) {
            cards.push_back(Card(suit, values[i], names[i]));
        }
    }
}

void Deck::shuffle() {
    std::random_device rd;
    std::mt19937 g(rd());
    std::shuffle(cards.begin(), cards.end(), g);
}

Card Deck::drawCard() {
    if (!cards.empty()) {
        Card drawnCard = cards.back();
        cards.pop_back();
        return drawnCard;
    }
    return Card();
}
