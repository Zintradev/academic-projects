import sys
from src.config import Config
from src.database import CinemaDatabaseManager
from src.cli import CinemaCLI

def main():
    """Main entrypoint script initializing database logic and launching CLI."""
    try:
        db_manager = CinemaDatabaseManager(Config)
        cli = CinemaCLI(db_manager)
        cli.run()
    except KeyboardInterrupt:
        print("\n\nOperation cancelled. Exiting Cinema Database Manager...")
        sys.exit(0)
    except Exception as e:
        print(f"\n[CRITICAL ERROR] Application failed to start: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()
