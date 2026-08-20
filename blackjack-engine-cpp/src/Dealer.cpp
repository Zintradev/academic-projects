#include "Dealer.h"

Dealer::Dealer() : Player("Dealer", 0, {}, 0, 0, false, true) {}

Dealer::Dealer(int score, const std::vector<Card>& hand)
    : Player("Dealer", score, hand, 0, 0, false, true) {}
