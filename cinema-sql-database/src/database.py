import mysql.connector
from mysql.connector import Error, pooling
from src.config import Config

class DatabaseConnectionError(Exception):
    """Custom exception raised when database connection fails."""
    pass

class CinemaDatabaseManager:
    """Manages MySQL database connections, pooling, and parameterized queries."""

    def __init__(self, config=Config):
        self.config = config
        self.pool = None
        self._initialize_pool()

    def _initialize_pool(self):
        """Initializes the MySQL connection pool."""
        try:
            self.pool = pooling.MySQLConnectionPool(
                pool_name=self.config.DB_POOL_NAME,
                pool_size=self.config.DB_POOL_SIZE,
                pool_reset_session=True,
                host=self.config.DB_HOST,
                database=self.config.DB_NAME,
                user=self.config.DB_USER,
                password=self.config.DB_PASSWORD,
                port=self.config.DB_PORT
            )
        except Error as e:
            # Pool initialization can fail if MySQL is offline or configuration is wrong.
            # We don't raise it immediately on init so the app can start and show clean errors.
            self.pool = None

    def get_connection(self):
        """Retrieves a database connection from the pool.

        Raises:
            DatabaseConnectionError: If connection cannot be established.
        """
        if not self.pool:
            # Try to reinitialize pool in case MySQL server has started
            self._initialize_pool()
            if not self.pool:
                raise DatabaseConnectionError(
                    "Could not initialize connection pool. Make sure MySQL is running."
                )
        
        try:
            connection = self.pool.get_connection()
            if connection.is_connected():
                return connection
            else:
                raise DatabaseConnectionError("Retrieved connection is not active.")
        except Error as e:
            raise DatabaseConnectionError(f"Error fetching connection from pool: {e}")

    def get_movies(self):
        """Fetches all movies from the database, ordered by title.

        Returns:
            list: A list of dicts containing movie records.
        """
        connection = self.get_connection()
        cursor = None
        try:
            cursor = connection.cursor(dictionary=True)
            cursor.execute("SELECT movie_id, title, duration, director, genre FROM movies ORDER BY title")
            return cursor.fetchall()
        except Error as e:
            raise RuntimeError(f"Database query failed: {e}")
        finally:
            if cursor:
                cursor.close()
            connection.close()

    def get_sales_by_movie(self):
        """Generates a sales report showing total tickets sold and revenue per movie.

        Returns:
            list: A list of dicts with sales reporting metrics.
        """
        connection = self.get_connection()
        cursor = None
        try:
            cursor = connection.cursor(dictionary=True)
            query = """
                SELECT 
                    m.title, 
                    COUNT(t.ticket_id) AS tickets_sold, 
                    COALESCE(SUM(t.price), 0.00) AS total_revenue
                FROM movies m
                LEFT JOIN screenings s ON m.movie_id = s.movie_id
                LEFT JOIN tickets t ON s.screening_id = t.screening_id
                GROUP BY m.movie_id, m.title
                ORDER BY total_revenue DESC
            """
            cursor.execute(query)
            return cursor.fetchall()
        except Error as e:
            raise RuntimeError(f"Database query failed: {e}")
        finally:
            if cursor:
                cursor.close()
            connection.close()

    def add_movie(self, title, duration, director, genre):
        """Adds a new movie to the catalog (Parameterized Query).

        Args:
            title (str): Title of the movie.
            duration (int): Duration in minutes.
            director (str): Name of the director.
            genre (str): Genre classification.

        Returns:
            int: The primary key (movie_id) of the inserted movie.
        """
        connection = self.get_connection()
        cursor = None
        try:
            cursor = connection.cursor()
            query = """
                INSERT INTO movies (title, duration, director, genre)
                VALUES (%s, %s, %s, %s)
            """
            cursor.execute(query, (title, duration, director, genre))
            connection.commit()
            return cursor.lastrowid
        except Error as e:
            raise RuntimeError(f"Failed to add movie: {e}")
        finally:
            if cursor:
                cursor.close()
            connection.close()

    def get_movie_by_id(self, movie_id):
        """Fetches a single movie by its unique identifier (Parameterized Query).

        Args:
            movie_id (int): Database key for the movie.

        Returns:
            dict or None: Movie details, or None if not found.
        """
        connection = self.get_connection()
        cursor = None
        try:
            cursor = connection.cursor(dictionary=True)
            query = "SELECT movie_id, title, duration, director, genre FROM movies WHERE movie_id = %s"
            cursor.execute(query, (movie_id,))
            return cursor.fetchone()
        except Error as e:
            raise RuntimeError(f"Failed to retrieve movie by ID: {e}")
        finally:
            if cursor:
                cursor.close()
            connection.close()

    def register_customer(self, first_name, last_name, email, phone):
        """Registers a new customer (Parameterized Query).

        Args:
            first_name (str): Customer's first name.
            last_name (str): Customer's last name.
            email (str): Contact email.
            phone (str): Contact phone number.

        Returns:
            int: The primary key (customer_id) of the registered customer.
        """
        connection = self.get_connection()
        cursor = None
        try:
            cursor = connection.cursor()
            query = """
                INSERT INTO customers (first_name, last_name, email, phone)
                VALUES (%s, %s, %s, %s)
            """
            cursor.execute(query, (first_name, last_name, email, phone))
            connection.commit()
            return cursor.lastrowid
        except Error as e:
            raise RuntimeError(f"Failed to register customer: {e}")
        finally:
            if cursor:
                cursor.close()
            connection.close()

    def get_screenings(self):
        """Fetches all screening slots available, joining movie and screen details.

        Returns:
            list: Screening schedules.
        """
        connection = self.get_connection()
        cursor = None
        try:
            cursor = connection.cursor(dictionary=True)
            query = """
                SELECT 
                    s.screening_id, 
                    m.title AS movie_title, 
                    sc.screen_id, 
                    sc.type AS screen_type, 
                    s.screening_date, 
                    s.screening_time
                FROM screenings s
                JOIN movies m ON s.movie_id = m.movie_id
                JOIN screens sc ON s.screen_id = sc.screen_id
                ORDER BY s.screening_date, s.screening_time
            """
            cursor.execute(query)
            return cursor.fetchall()
        except Error as e:
            raise RuntimeError(f"Failed to retrieve screenings: {e}")
        finally:
            if cursor:
                cursor.close()
            connection.close()

    def book_ticket(self, customer_id, screening_id, seat, price):
        """Books a ticket for a screening under a customer (Atomic Transaction).

        Demonstrates transactional consistency (commits both records or rolls back completely).

        Args:
            customer_id (int): ID of purchasing customer.
            screening_id (int): Screening session identifier.
            seat (str): Designated seating coordinate (e.g. 'A1').
            price (float): Seat pricing amount.

        Returns:
            int: The primary key of the booked ticket.
        """
        connection = self.get_connection()
        cursor = None
        try:
            # Begin Transaction
            connection.start_transaction()
            cursor = connection.cursor()

            # 1. Create a ticket record
            ticket_query = """
                INSERT INTO tickets (price, seat, screening_id) 
                VALUES (%s, %s, %s)
            """
            cursor.execute(ticket_query, (price, seat, screening_id))
            ticket_id = cursor.lastrowid

            # 2. Relate customer to the booked ticket in the join table
            association_query = """
                INSERT INTO customer_tickets (customer_id, ticket_id) 
                VALUES (%s, %s)
            """
            cursor.execute(association_query, (customer_id, ticket_id))

            # Commit Transaction
            connection.commit()
            return ticket_id
        except Exception as e:
            # Abort transaction and rollback changes on database error
            connection.rollback()
            raise RuntimeError(f"Ticket booking failed (Transaction Rolled Back): {e}")
        finally:
            if cursor:
                cursor.close()
            connection.close()
