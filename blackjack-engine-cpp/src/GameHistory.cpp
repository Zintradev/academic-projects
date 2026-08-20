#include "GameHistory.h"
#include <algorithm>

GameHistory::GameHistory() : playerHistory({}) {}

GameHistory::GameHistory(const std::vector<Player>& history) : playerHistory(history) {}

void GameHistory::addOrUpdatePlayer(const Player& player) {
    auto it = std::find_if(playerHistory.begin(), playerHistory.end(),
        [&player](const Player& p) { return p.getName() == player.getName(); });

    if (it == playerHistory.end()) {
        playerHistory.push_back(player);
    }
    else {
        it->setWins(player.getWins());
        it->setLosses(player.getLosses());
    }
}

const std::vector<Player>& GameHistory::getPlayers() const {
    return playerHistory;
}

void GameHistory::setPlayers(const std::vector<Player>& history) {
    playerHistory = history;
}
