#pragma once
#include "Card.h"
#include <vector>

class Deck
{
private:
    std::vector<Card> cards;

public:
    Deck();
    explicit Deck(const std::vector<Card>& cards);

    void generateDeck();
    void shuffle();
    Card drawCard();
    bool isEmpty() const { return cards.empty(); }
    const std::vector<Card>& getCards() const { return cards; }
};
