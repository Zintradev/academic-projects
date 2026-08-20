#include "ConsoleView.h"
#include <iostream>
#include <limits>

#ifdef _WIN32
#include <conio.h>
#include <cstdlib>
#else
#include <termios.h>
#include <unistd.h>
#include <cstdio>
#endif

char ConsoleView::getCharNoEcho() const {
#ifdef _WIN32
    return _getch();
#else
    struct termios oldt, newt;
    char ch;
    tcgetattr(STDIN_FILENO, &oldt);
    newt = oldt;
    newt.c_lflag &= ~(ICANON | ECHO);
    tcsetattr(STDIN_FILENO, TCSANOW, &newt);
    ch = getchar();
    tcsetattr(STDIN_FILENO, TCSANOW, &oldt);
    return ch;
#endif
}

void ConsoleView::clearScreen() const {
#ifdef _WIN32
    std::system("cls");
#else
    std::cout << "\033[2J\033[1;1H";
#endif
}

void ConsoleView::showMenu() const {
    std::cout << "\n==== BLACKJACK MENU ====" << std::endl;
    std::cout << "1. Start New Game" << std::endl;
    std::cout << "2. View Game History" << std::endl;
    std::cout << "3. Exit" << std::endl;
    std::cout << "Select an option: ";
}

int ConsoleView::getMenuChoice() const {
    char ch = getCharNoEcho();
    if (ch == '1') return 1;
    if (ch == '2') return 2;
    if (ch == '3') return 3;
    return 0; // Invalid option
}

int ConsoleView::getPlayerCount() const {
    int count;
    std::cout << "Enter the number of players for this game: ";
    if (!(std::cin >> count)) {
        std::cin.clear();
        std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
        return -1; // Triggers InvalidPlayerCountException
    }
    std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
    return count;
}

std::string ConsoleView::getPlayerName(int index) const {
    std::string name;
    std::cout << "Enter the name of Player " << index + 1 << ": ";
    std::getline(std::cin, name);
    return name;
}

int ConsoleView::getPlayerAction(const std::string& playerName, int score) const {
    std::cout << "\n" << playerName << "'s turn (Current Score: " << score << "):" << std::endl;
    std::cout << "1. Hit (Take a card)" << std::endl;
    std::cout << "2. Stand" << std::endl;
    while (true) {
        char ch = getCharNoEcho();
        if (ch == '1') return 1;
        if (ch == '2') return 2;
        std::cout << "Invalid choice. Please select 1 (Hit) or 2 (Stand)." << std::endl;
    }
}

void ConsoleView::showCardDrawn(const std::string& playerName, const std::string& cardName, const std::string& cardSuit, int cardValue) const {
    std::cout << "\n" << playerName << " draws: " << cardName << " of " << cardSuit << " (Value: " << cardValue << ")" << std::endl;
}

void ConsoleView::showPlayerBusted(const std::string& playerName) const {
    std::cout << playerName << " went over 21! Standing automatically." << std::endl;
}

void ConsoleView::showPlayerStanding(const std::string& playerName) const {
    std::cout << playerName << " stands." << std::endl;
}

void ConsoleView::showDealerAction(const std::string& action, const std::string& cardDetails) const {
    if (action == "draw") {
        std::cout << "Dealer draws: " << cardDetails << std::endl;
    } else if (action == "stand") {
        std::cout << "Dealer stands." << std::endl;
    } else if (action == "turn") {
        std::cout << "\nDealer's turn:" << std::endl;
    }
}

void ConsoleView::showStatus(const std::vector<Player>& players, const Dealer& dealer) const {
    std::cout << "\n============ SCORES ============" << std::endl;

    for (const auto& player : players) {
        if (player.isActive()) {
            std::cout << "\nPlayer " << player.getName() << ": Score = " << player.getScore() << std::endl;
            std::cout << "Cards:\n";
            for (const auto& card : player.getHand()) {
                std::cout << "  - " << card.getName() << " of " << card.getSuit() << "\n";
            }
        }
    }

    std::cout << "\nDealer: Score = " << dealer.getScore() << std::endl;
    std::cout << "Cards:\n";
    for (const auto& card : dealer.getHand()) {
        std::cout << "  - " << card.getName() << " of " << card.getSuit() << "\n";
    }
    std::cout << "================================\n" << std::endl;
}

void ConsoleView::showRoundResults(const std::vector<Player>& players, const Dealer& dealer) const {
    std::cout << "\n============ ROUND RESULTS ============" << std::endl;
    int dealerScore = dealer.getScore();

    for (const auto& player : players) {
        if (!player.isActive()) continue;

        int playerScore = player.getScore();
        std::cout << "\nPlayer: " << player.getName() << " (Final Score: " << playerScore << ")" << std::endl;
        std::cout << "Dealer: (Final Score: " << dealerScore << ")" << std::endl;

        if (playerScore > 21) {
            std::cout << "Result: LOSS - Busted (went over 21)!" << std::endl;
        }
        else if (dealerScore > 21) {
            std::cout << "Result: WIN - Dealer busted!" << std::endl;
        }
        else if (playerScore == 21 && player.getHand().size() == 2 && !(dealerScore == 21 && dealer.getHand().size() == 2)) {
            std::cout << "Result: WIN - Natural Blackjack!" << std::endl;
        }
        else if (dealerScore == 21 && dealer.getHand().size() == 2 && !(playerScore == 21 && player.getHand().size() == 2)) {
            std::cout << "Result: LOSS - Dealer had a Natural Blackjack!" << std::endl;
        }
        else if (playerScore > dealerScore) {
            std::cout << "Result: WIN - Closer to 21 than the dealer!" << std::endl;
        }
        else if (playerScore < dealerScore) {
            std::cout << "Result: LOSS - Dealer was closer to 21!" << std::endl;
        }
        else {
            std::cout << "Result: TIE (Push)!" << std::endl;
        }
        std::cout << "---------------------------------------" << std::endl;
    }
}

void ConsoleView::showHistory(const std::vector<Player>& history) const {
    std::cout << "\n\n==== GAME HISTORY ====" << std::endl;
    if (history.empty()) {
        std::cout << "No games recorded yet." << std::endl;
    } else {
        for (const auto& player : history) {
            std::cout << "Player: " << player.getName() << std::endl;
            std::cout << "  Wins:   " << player.getWins() << std::endl;
            std::cout << "  Losses: " << player.getLosses() << std::endl;
            std::cout << "------------------------" << std::endl;
        }
    }
}

void ConsoleView::showErrorMessage(const std::string& message) const {
    std::cout << "Error: " << message << std::endl;
}

void ConsoleView::showExitMessage() const {
    std::cout << "Leaving the game. Thanks for playing!" << std::endl;
}

void ConsoleView::showInvalidOptionMessage() const {
    std::cout << "Invalid option. Please try again." << std::endl;
}

void ConsoleView::waitForAnyKey() const {
    std::cout << "\nPress any key to continue..." << std::endl;
    getCharNoEcho();
}
