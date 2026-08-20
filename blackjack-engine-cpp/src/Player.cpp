#include "Player.h"

Player::Player()
    : name(""), score(0), hand({}), wins(0), losses(0), stood(false), active(false) {}

Player::Player(const std::string& name, int score, const std::vector<Card>& hand, int wins, int losses, bool stood, bool active)
    : name(name), score(score), hand(hand), wins(wins), losses(losses), stood(stood), active(active) {}

Player::Player(const Player& other)
    : name(other.name), score(other.score), hand(other.hand), wins(other.wins), losses(other.losses), stood(other.stood), active(other.active) {}

void Player::addCard(const Card& card) {
    hand.push_back(card);
}

void Player::clearHand() {
    hand.clear();
}
