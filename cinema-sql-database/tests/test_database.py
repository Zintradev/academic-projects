import unittest
from unittest.mock import MagicMock, patch
from src.database import CinemaDatabaseManager, DatabaseConnectionError

class TestCinemaDatabaseManager(unittest.TestCase):
    """Unit test suite for the CinemaDatabaseManager, utilizing mock structures to test DB actions."""

    @patch('mysql.connector.pooling.MySQLConnectionPool')
    def setUp(self, mock_pool_class):
        # Create a mock pool instance
        self.mock_pool = MagicMock()
        mock_pool_class.return_value = self.mock_pool
        
        # Instantiate manager (which triggers pool creation)
        self.db = CinemaDatabaseManager()
        # Explicitly assign pool for unit testing isolation
        self.db.pool = self.mock_pool

    def test_get_connection_success(self):
        """Should return the connection if pool returns a connected instance."""
        mock_conn = MagicMock()
        mock_conn.is_connected.return_value = True
        self.mock_pool.get_connection.return_value = mock_conn
        
        conn = self.db.get_connection()
        self.assertEqual(conn, mock_conn)
        mock_conn.is_connected.assert_called_once()

    def test_get_connection_failure(self):
        """Should raise DatabaseConnectionError if pool gets an error or is uninitialized."""
        self.db.pool = None
        with patch.object(self.db, '_initialize_pool') as mock_init:
            with self.assertRaises(DatabaseConnectionError):
                self.db.get_connection()

    def test_get_movies(self):
        """Should fetch movies from database, closing resources afterwards."""
        mock_conn = MagicMock()
        mock_cursor = MagicMock()
        mock_conn.cursor.return_value = mock_cursor
        self.mock_pool.get_connection.return_value = mock_conn
        
        expected_movies = [
            {"movie_id": 1, "title": "Inception", "duration": 148, "director": "Christopher Nolan", "genre": "Sci-Fi"}
        ]
        mock_cursor.fetchall.return_value = expected_movies
        
        movies = self.db.get_movies()
        self.assertEqual(movies, expected_movies)
        mock_cursor.execute.assert_called_once_with(
            "SELECT movie_id, title, duration, director, genre FROM movies ORDER BY title"
        )
        mock_cursor.close.assert_called_once()
        mock_conn.close.assert_called_once()

    def test_add_movie_parameterized(self):
        """Should securely insert movie details using a parameterized query format."""
        mock_conn = MagicMock()
        mock_cursor = MagicMock()
        mock_conn.cursor.return_value = mock_cursor
        self.mock_pool.get_connection.return_value = mock_conn
        mock_cursor.lastrowid = 99
        
        movie_id = self.db.add_movie("Dune", 155, "Denis Villeneuve", "Sci-Fi")
        self.assertEqual(movie_id, 99)
        mock_cursor.execute.assert_called_once()
        
        # Check query parameters
        args, _ = mock_cursor.execute.call_args
        self.assertIn("INSERT INTO movies", args[0])
        # Assert parameters are passed as tuple elements (SQL Injection proofing)
        self.assertEqual(args[1], ("Dune", 155, "Denis Villeneuve", "Sci-Fi"))
        mock_conn.commit.assert_called_once()
        mock_cursor.close.assert_called_once()
        mock_conn.close.assert_called_once()

    def test_book_ticket_transaction_success(self):
        """Should commit transaction if both ticket insertion and customer relations mapping succeed."""
        mock_conn = MagicMock()
        mock_cursor = MagicMock()
        mock_conn.cursor.return_value = mock_cursor
        self.mock_pool.get_connection.return_value = mock_conn
        mock_cursor.lastrowid = 101
        
        ticket_id = self.db.book_ticket(customer_id=1, screening_id=2, seat="A12", price=7.50)
        
        self.assertEqual(ticket_id, 101)
        mock_conn.start_transaction.assert_called_once()
        self.assertEqual(mock_cursor.execute.call_count, 2)
        mock_conn.commit.assert_called_once()
        mock_conn.rollback.assert_not_called()

    def test_book_ticket_transaction_rollback_on_failure(self):
        """Should trigger rollback and abort commit if mapping operation fails."""
        mock_conn = MagicMock()
        mock_cursor = MagicMock()
        mock_conn.cursor.return_value = mock_cursor
        self.mock_pool.get_connection.return_value = mock_conn
        
        # Force a database failure on the mapping insert step (the 2nd database query call)
        mock_cursor.execute.side_effect = [None, RuntimeError("Simulated Database Crash")]
        
        with self.assertRaises(RuntimeError) as context:
            self.db.book_ticket(customer_id=1, screening_id=2, seat="A12", price=7.50)
            
        self.assertIn("Transaction Rolled Back", str(context.exception))
        mock_conn.start_transaction.assert_called_once()
        mock_conn.rollback.assert_called_once()
        mock_conn.commit.assert_not_called()

if __name__ == "__main__":
    unittest.main()
