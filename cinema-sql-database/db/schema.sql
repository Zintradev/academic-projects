-- Cinema Database Schema
-- Standard schema in English with proper constraints, naming conventions, and AUTO_INCREMENT fields.

CREATE DATABASE IF NOT EXISTS cinema_db;
USE cinema_db;

-- 1. Customers Table
CREATE TABLE IF NOT EXISTS customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Employees Table
CREATE TABLE IF NOT EXISTS employees (
    employee_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    salary DECIMAL(10,2),
    role VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Screens Table
CREATE TABLE IF NOT EXISTS screens (
    screen_id INT AUTO_INCREMENT PRIMARY KEY,
    capacity INT NOT NULL,
    type ENUM('2D', '3D', 'IMAX') NOT NULL DEFAULT '2D'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Movies Table
CREATE TABLE IF NOT EXISTS movies (
    movie_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    duration INT, -- Duration in minutes
    director VARCHAR(100),
    genre VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Screenings Table (Shows when a movie is shown on which screen)
CREATE TABLE IF NOT EXISTS screenings (
    screening_id INT AUTO_INCREMENT PRIMARY KEY,
    screening_time TIME NOT NULL,
    screening_date DATE NOT NULL,
    movie_id INT NOT NULL,
    screen_id INT NOT NULL,
    FOREIGN KEY (movie_id) REFERENCES movies(movie_id) ON DELETE CASCADE,
    FOREIGN KEY (screen_id) REFERENCES screens(screen_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. Tickets Table
CREATE TABLE IF NOT EXISTS tickets (
    ticket_id INT AUTO_INCREMENT PRIMARY KEY,
    price DECIMAL(8,2) NOT NULL,
    seat VARCHAR(10) NOT NULL,
    screening_id INT NOT NULL,
    FOREIGN KEY (screening_id) REFERENCES screenings(screening_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. Customer Tickets Table (Many-to-Many join table for Customers and Tickets purchase records)
CREATE TABLE IF NOT EXISTS customer_tickets (
    customer_id INT NOT NULL,
    ticket_id INT NOT NULL,
    PRIMARY KEY (customer_id, ticket_id),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (ticket_id) REFERENCES tickets(ticket_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. Employee Screens Table (Many-to-Many join table for Employees assigned to Screens)
CREATE TABLE IF NOT EXISTS employee_screens (
    employee_id INT NOT NULL,
    screen_id INT NOT NULL,
    PRIMARY KEY (employee_id, screen_id),
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE,
    FOREIGN KEY (screen_id) REFERENCES screens(screen_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
