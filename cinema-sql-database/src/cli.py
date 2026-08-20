import sys
from src.database import DatabaseConnectionError

class CinemaCLI:
    """Console interface controller for the Cinema Database Manager."""

    def __init__(self, db_manager):
        self.db = db_manager

    def show_menu(self):
        """Displays the operations menu to the console."""
        print("\n" + "=" * 50)
        print("          CINEMA DATABASE MANAGER CLI")
        print("=" * 50)
        print("1. List All Movies")
        print("2. View Movie Sales & Revenue Report")
        print("3. Add New Movie to Catalog")
        print("4. Register New Customer")
        print("5. View Screening Schedule")
        print("6. Book a Ticket (Secure Transaction)")
        print("7. Exit")
        print("=" * 50)

    def run(self):
        """Runs the CLI execution loop."""
        print("Initializing database connections...")
        try:
            # Attempt a test connection to verify state on startup
            conn = self.db.get_connection()
            conn.close()
            print("Successfully connected to the database.")
        except DatabaseConnectionError as e:
            print(f"\n[WARNING] Database connection test failed: {e}")
            print("You can still navigate the menu, but queries might fail until database is ready.\n")
        except Exception as e:
            print(f"\n[WARNING] Unexpected initialization error: {e}\n")

        while True:
            self.show_menu()
            choice = input("Enter choice (1-7): ").strip()
            
            try:
                if choice == "1":
                    self.list_movies()
                elif choice == "2":
                    self.show_sales_report()
                elif choice == "3":
                    self.add_new_movie()
                elif choice == "4":
                    self.register_new_customer()
                elif choice == "5":
                    self.show_screenings()
                elif choice == "6":
                    self.book_ticket()
                elif choice == "7":
                    print("\nThank you for using Cinema Database Manager. Goodbye!")
                    break
                else:
                    print("\n[ERROR] Invalid choice. Please enter a number between 1 and 7.")
            except DatabaseConnectionError as e:
                print(f"\n[DATABASE ERROR] Connection failed: {e}")
            except Exception as e:
                print(f"\n[APPLICATION ERROR] An error occurred: {e}")

    def list_movies(self):
        """Displays the catalog of movies."""
        print("\n--- Movie Catalog ---")
        movies = self.db.get_movies()
        if not movies:
            print("No movies found in the catalog.")
            return

        # Print table header
        header = f"{'ID':<6} | {'Title':<25} | {'Duration':<10} | {'Director':<25} | {'Genre':<15}"
        print(header)
        print("-" * len(header))

        for movie in movies:
            duration_str = f"{movie['duration']} min" if movie['duration'] else "N/A"
            director = movie['director'] if movie['director'] else "Unknown"
            genre = movie['genre'] if movie['genre'] else "N/A"
            print(
                f"{movie['movie_id']:<6} | "
                f"{movie['title'][:24]:<25} | "
                f"{duration_str:<10} | "
                f"{director[:24]:<25} | "
                f"{genre[:14]:<15}"
            )

    def show_sales_report(self):
        """Displays tickets sold and revenue earned grouped by movie."""
        print("\n--- Sales & Revenue Report ---")
        report = self.db.get_sales_by_movie()
        if not report:
            print("No sales records available.")
            return

        header = f"{'Movie Title':<30} | {'Tickets Sold':<12} | {'Total Revenue':<15}"
        print(header)
        print("-" * len(header))

        for item in report:
            revenue_formatted = f"${float(item['total_revenue']):.2f}"
            print(
                f"{item['title'][:29]:<30} | "
                f"{item['tickets_sold']:<12} | "
                f"{revenue_formatted:<15}"
            )

    def add_new_movie(self):
        """Guides user to insert a new movie with inputs validated to avoid format errors."""
        print("\n--- Add New Movie ---")
        title = input("Enter Movie Title: ").strip()
        if not title:
            print("[ERROR] Title cannot be empty.")
            return

        try:
            duration_input = input("Enter Duration (minutes): ").strip()
            duration = int(duration_input) if duration_input else None
            if duration is not None and duration <= 0:
                print("[ERROR] Duration must be a positive integer.")
                return
        except ValueError:
            print("[ERROR] Invalid number format for duration.")
            return

        director = input("Enter Director Name: ").strip() or None
        genre = input("Enter Genre: ").strip() or None

        movie_id = self.db.add_movie(title, duration, director, genre)
        print(f"\n[SUCCESS] Movie '{title}' added successfully with ID: {movie_id}")

    def register_new_customer(self):
        """Guides customer registration."""
        print("\n--- Register New Customer ---")
        first_name = input("Enter First Name: ").strip()
        last_name = input("Enter Last Name: ").strip()
        if not first_name or not last_name:
            print("[ERROR] First name and last name are required.")
            return

        email = input("Enter Email Address: ").strip() or None
        phone = input("Enter Phone Number: ").strip() or None

        customer_id = self.db.register_customer(first_name, last_name, email, phone)
        print(f"\n[SUCCESS] Customer registered successfully with ID: {customer_id}")

    def show_screenings(self):
        """Displays available screenings schedule."""
        print("\n--- Screening Schedule ---")
        screenings = self.db.get_screenings()
        if not screenings:
            print("No screenings scheduled.")
            return

        header = f"{'ID':<6} | {'Movie Title':<25} | {'Screen':<8} | {'Type':<6} | {'Date':<12} | {'Time':<10}"
        print(header)
        print("-" * len(header))

        for s in screenings:
            date_str = str(s['screening_date'])
            # Format time to HH:MM if it is a timedelta or time object
            time_str = str(s['screening_time'])
            print(
                f"{s['screening_id']:<6} | "
                f"{s['movie_title'][:24]:<25} | "
                f"Screen {s['screen_id']:<1} | "
                f"{s['screen_type']:<6} | "
                f"{date_str:<12} | "
                f"{time_str:<10}"
            )

    def book_ticket(self):
        """Coordinates ticket booking using SQL transactions to ensure consistency."""
        print("\n--- Book a Ticket ---")
        try:
            customer_id = int(input("Enter Customer ID: ").strip())
            screening_id = int(input("Enter Screening ID: ").strip())
        except ValueError:
            print("[ERROR] Customer ID and Screening ID must be numeric.")
            return

        seat = input("Enter Seat Coordinate (e.g., A12): ").strip().upper()
        if not seat:
            print("[ERROR] Seat coordinate cannot be empty.")
            return

        try:
            price = float(input("Enter Ticket Price ($): ").strip())
            if price < 0:
                print("[ERROR] Price cannot be negative.")
                return
        except ValueError:
            print("[ERROR] Invalid price decimal format.")
            return

        # Perform reservation transaction
        ticket_id = self.db.book_ticket(customer_id, screening_id, seat, price)
        print(f"\n[SUCCESS] Ticket booked successfully!")
        print(f"Ticket ID: {ticket_id} | Customer: {customer_id} | Seat: {seat} | Price: ${price:.2f}")
