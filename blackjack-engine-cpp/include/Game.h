#pragma once
#include <vector>
#include "Player.h"
#include "Dealer.h"
#include "Deck.h"
#include "GameHistory.h"
#include "ConsoleView.h"

class Game
{
private:
    std::vector<Player> players;
    Dealer dealer;
    Deck deck;
    GameHistory history;
    ConsoleView view;

    void addPlayers();
    void startRound();
    void playTurns();
    void determineWinner();

public:
    Game();
    Game(const std::vector<Player>& players, const Dealer& dealer, const Deck& deck, const GameHistory& history);

    void startMenu();
    void updateScore(Player& player);
    const std::vector<Player>& getPlayers() const { return players; }
    const Dealer& getDealer() const { return dealer; }
    const GameHistory& getHistory() const { return history; }
};
