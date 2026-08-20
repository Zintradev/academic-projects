#include "Game.h"
#include "Exception.h"
#include <algorithm>

Game::Game() : players({}), dealer(Dealer()), deck(Deck()), history(GameHistory()), view(ConsoleView()) {}

Game::Game(const std::vector<Player>& players, const Dealer& dealer, const Deck& deck, const GameHistory& history)
    : players(players), dealer(dealer), deck(deck), history(history), view(ConsoleView()) {}

void Game::addPlayers() {
    int numPlayers = 0;
    do {
        try {
            numPlayers = view.getPlayerCount();
            if (numPlayers <= 0) {
                throw InvalidPlayerCountException("The number of players must be greater than 0.");
            }
            break;
        }
        catch (const InvalidPlayerCountException& ex) {
            view.showErrorMessage(ex.what());
        }
    } while (true);

    // Deactivate all current players
    for (auto& player : players) {
        player.setActive(false);
    }

    for (int i = 0; i < numPlayers; ++i) {
        std::string name = view.getPlayerName(i);

        // Check if player is already in our list
        auto it = std::find_if(players.begin(), players.end(),
            [&name](const Player& p) { return p.getName() == name; });

        if (it != players.end()) {
            it->setActive(true);
            it->clearHand();
            it->setScore(0);
            it->setStood(false);
            continue;
        }

        // Check history
        bool foundInHistory = false;
        for (const auto& histPlayer : history.getPlayers()) {
            if (histPlayer.getName() == name) {
                Player playerCopy = histPlayer;
                playerCopy.setActive(true);
                playerCopy.clearHand();
                playerCopy.setScore(0);
                playerCopy.setStood(false);
                players.push_back(playerCopy);
                foundInHistory = true;
                break;
            }
        }

        if (!foundInHistory) {
            Player newPlayer;
            newPlayer.setName(name);
            newPlayer.setActive(true);
            newPlayer.clearHand();
            newPlayer.setScore(0);
            newPlayer.setStood(false);
            players.push_back(newPlayer);
            history.addOrUpdatePlayer(newPlayer);
        }
    }
}

void Game::startRound() {
    addPlayers();

    deck.generateDeck();
    deck.shuffle();

    // Reset hands and scores
    for (auto& player : players) {
        if (player.isActive()) {
            player.clearHand();
            player.setScore(0);
            player.setStood(false);
        }
    }
    dealer.clearHand();
    dealer.setScore(0);
    dealer.setStood(false);

    view.clearScreen();

    // Deal 2 cards to each active player
    for (auto& player : players) {
        if (player.isActive()) {
            Card c1 = deck.drawCard();
            Card c2 = deck.drawCard();
            player.addCard(c1);
            player.addCard(c2);
            updateScore(player);
            view.showCardDrawn(player.getName(), c1.getName(), c1.getSuit(), c1.getValue());
            view.showCardDrawn(player.getName(), c2.getName(), c2.getSuit(), c2.getValue());

            if (player.getScore() == 21) {
                player.setStood(true);
            }
        }
    }

    // Deal 2 cards to dealer
    Card c1d = deck.drawCard();
    Card c2d = deck.drawCard();
    dealer.addCard(c1d);
    dealer.addCard(c2d);
    updateScore(dealer);
    view.showCardDrawn("Dealer", c1d.getName(), c1d.getSuit(), c1d.getValue());
    view.showCardDrawn("Dealer", c2d.getName(), c2d.getSuit(), c2d.getValue());

    if (dealer.getScore() == 21) {
        dealer.setStood(true);
    }
}

void Game::playTurns() {
    bool roundOver = false;

    // If dealer has blackjack, round is over immediately
    if (dealer.getScore() == 21) {
        roundOver = true;
    }

    while (!roundOver) {
        // Player turns
        for (auto& player : players) {
            if (!player.isActive()) continue;

            if (!player.isStood()) {
                bool turnFinished = false;
                while (!turnFinished && player.getScore() <= 21) {
                    int action = view.getPlayerAction(player.getName(), player.getScore());
                    if (action == 1) { // Hit
                        Card card = deck.drawCard();
                        player.addCard(card);
                        view.showCardDrawn(player.getName(), card.getName(), card.getSuit(), card.getValue());
                        updateScore(player);

                        if (player.getScore() > 21) {
                            view.showPlayerBusted(player.getName());
                            player.setStood(true);
                            turnFinished = true;
                        }
                    }
                    else if (action == 2) { // Stand
                        player.setStood(true);
                        view.showPlayerStanding(player.getName());
                        turnFinished = true;
                    }
                }
            }
        }

        // Dealer turn
        bool dealerFinished = false;
        view.showDealerAction("turn");
        while (!dealerFinished) {
            if (dealer.getScore() <= 16) {
                Card card = deck.drawCard();
                dealer.addCard(card);
                std::string cardDetails = card.getName() + " of " + card.getSuit() + " (Value: " + std::to_string(card.getValue()) + ")";
                view.showDealerAction("draw", cardDetails);
                updateScore(dealer);
                view.showStatus(players, dealer);
                break; // Break here to show state to users sequentially
            }
            else {
                view.showDealerAction("stand");
                dealer.setStood(true);
                dealerFinished = true;
            }
        }

        // Check if all players are stood
        bool allPlayersStood = true;
        for (const auto& player : players) {
            if (player.isActive() && !player.isStood()) {
                allPlayersStood = false;
                break;
            }
        }

        if (allPlayersStood || dealer.getScore() > 21) {
            roundOver = true;
        }
    }

    view.waitForAnyKey();
    view.clearScreen();
    determineWinner();
}

void Game::determineWinner() {
    view.showRoundResults(players, dealer);
    int dealerScore = dealer.getScore();

    for (auto& player : players) {
        if (!player.isActive()) continue;

        int playerScore = player.getScore();

        if (playerScore > 21) {
            player.setLosses(player.getLosses() + 1);
        }
        else if (dealerScore > 21) {
            player.setWins(player.getWins() + 1);
        }
        else if (playerScore == 21 && player.getHand().size() == 2 && !(dealerScore == 21 && dealer.getHand().size() == 2)) {
            player.setWins(player.getWins() + 1);
        }
        else if (dealerScore == 21 && dealer.getHand().size() == 2 && !(playerScore == 21 && player.getHand().size() == 2)) {
            player.setLosses(player.getLosses() + 1);
        }
        else if (playerScore > dealerScore) {
            player.setWins(player.getWins() + 1);
        }
        else if (playerScore < dealerScore) {
            player.setLosses(player.getLosses() + 1);
        }
        else {
            // Push (Tie), no stats changed
        }

        // Save progress to GameHistory
        history.addOrUpdatePlayer(player);
    }

    view.waitForAnyKey();
}

void Game::updateScore(Player& player) {
    int score = 0;
    int acesCount = 0;

    for (const Card& card : player.getHand()) {
        score += card.getValue();
        if (card.getName() == "A") {
            acesCount++;
        }
    }

    // Adjust Aces from 11 to 1 if score exceeds 21
    while (score > 21 && acesCount > 0) {
        score -= 10;
        acesCount--;
    }

    player.setScore(score);
}

void Game::startMenu() {
    int choice = 0;
    do {
        view.showMenu();
        choice = view.getMenuChoice();

        switch (choice) {
        case 1:
            view.clearScreen();
            startRound();
            playTurns();
            break;
        case 2:
            view.clearScreen();
            view.showHistory(history.getPlayers());
            view.waitForAnyKey();
            view.clearScreen();
            break;
        case 3:
            view.showExitMessage();
            break;
        default:
            view.showInvalidOptionMessage();
            break;
        }
    } while (choice != 3);
}
