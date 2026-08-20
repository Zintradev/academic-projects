import os

# Try to load environment variables from a .env file if python-dotenv is installed
try:
    from dotenv import load_dotenv
    load_dotenv()
except ImportError:
    # Fallback silently if python-dotenv is not installed in the current environment
    pass

class Config:
    """Encapsulates system configuration parameters."""
    DB_HOST = os.environ.get("DB_HOST", "localhost")
    DB_USER = os.environ.get("DB_USER", "root")
    DB_PASSWORD = os.environ.get("DB_PASSWORD", "")
    DB_NAME = os.environ.get("DB_NAME", "cinema_db")
    DB_PORT = int(os.environ.get("DB_PORT", 3306))
    DB_POOL_NAME = os.environ.get("DB_POOL_NAME", "cinema_pool")
    DB_POOL_SIZE = int(os.environ.get("DB_POOL_SIZE", 5))
