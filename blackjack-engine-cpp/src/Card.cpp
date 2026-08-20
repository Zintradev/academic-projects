#include "Card.h"

Card::Card() : suit(""), value(0), name("") {}

Card::Card(const std::string& suit, int value, const std::string& name)
    : suit(suit), value(value), name(name) {}

Card::Card(const Card& other)
    : suit(other.suit), value(other.value), name(other.name) {}

int Card::getValue() const {
    return value;
}

std::string Card::getSuit() const {
    return suit;
}

std::string Card::getName() const {
    return name;
}
