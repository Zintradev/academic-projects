#pragma once
#include <string>

class Card
{
private:
    std::string suit;
    int value;
    std::string name;

public:
    Card();
    Card(const std::string& suit, int value, const std::string& name);
    Card(const Card& other);

    int getValue() const;
    std::string getSuit() const;
    std::string getName() const;
};
