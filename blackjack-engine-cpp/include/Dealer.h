#pragma once
#include "Player.h"

class Dealer : public Player
{
public:
    Dealer();
    Dealer(int score, const std::vector<Card>& hand);
};
