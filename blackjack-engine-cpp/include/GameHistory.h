#pragma once
#include <vector>
#include "Player.h"

class GameHistory
{
private:
    std::vector<Player> playerHistory;

public:
    GameHistory();
    explicit GameHistory(const std::vector<Player>& history);

    void addOrUpdatePlayer(const Player& player);
    const std::vector<Player>& getPlayers() const;
    void setPlayers(const std::vector<Player>& history);
};
