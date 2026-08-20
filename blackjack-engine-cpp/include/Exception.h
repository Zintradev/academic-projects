#pragma once
#include <stdexcept>
#include <string>

class InvalidPlayerCountException : public std::invalid_argument {
public:
    explicit InvalidPlayerCountException(const std::string& message)
        : std::invalid_argument(message) {}
};
