#pragma once
#include <string>
#include <vector>
#include "Card.h"

class Player
{
protected:
    std::string name;
    int score;
    std::vector<Card> hand;
    int wins;
    int losses;
    bool stood;
    bool active;

public:
    Player();
    Player(const std::string& name, int score, const std::vector<Card>& hand, int wins, int losses, bool stood, bool active);
    Player(const Player& other);

    void addCard(const Card& card);
    void clearHand();

    // Getters and Setters
    bool isStood() const { return stood; }
    void setStood(bool stoodStatus) { stood = stoodStatus; }

    bool isActive() const { return active; }
    void setActive(bool activeStatus) { active = activeStatus; }

    void setName(const std::string& newName) { name = newName; }
    std::string getName() const { return name; }

    void setScore(int newScore) { score = newScore; }
    int getScore() const { return score; }

    void setWins(int newWins) { wins = newWins; }
    int getWins() const { return wins; }

    void setLosses(int newLosses) { losses = newLosses; }
    int getLosses() const { return losses; }

    const std::vector<Card>& getHand() const { return hand; }
};
