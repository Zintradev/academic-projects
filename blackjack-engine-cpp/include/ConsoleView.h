#pragma once
#include <string>
#include <vector>
#include "Player.h"
#include "Dealer.h"

class ConsoleView
{
private:
    char getCharNoEcho() const;

public:
    void clearScreen() const;
    void showMenu() const;
    int getMenuChoice() const;
    int getPlayerCount() const;
    std::string getPlayerName(int index) const;
    int getPlayerAction(const std::string& playerName, int score) const;

    void showCardDrawn(const std::string& playerName, const std::string& cardName, const std::string& cardSuit, int cardValue) const;
    void showPlayerBusted(const std::string& playerName) const;
    void showPlayerStanding(const std::string& playerName) const;
    void showDealerAction(const std::string& action, const std::string& cardDetails = "") const;
    void showStatus(const std::vector<Player>& players, const Dealer& dealer) const;
    void showRoundResults(const std::vector<Player>& players, const Dealer& dealer) const;
    void showHistory(const std::vector<Player>& history) const;

    void showErrorMessage(const std::string& message) const;
    void showExitMessage() const;
    void showInvalidOptionMessage() const;
    void waitForAnyKey() const;
};
