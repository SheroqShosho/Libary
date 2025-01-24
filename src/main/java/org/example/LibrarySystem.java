package org.example;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class LibrarySystem {
    private  static Scanner scanner = new Scanner(System.in);

    public static User login() {
        System.out.println("Ange användarnamn: ");
        String username = scanner.nextLine();

        System.out.println("Ange lösenord: ");
        String password = scanner.nextLine();

        String sql = "SELECT * FROM Users WHERE user = ? AND password = ?";
        try (Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new User(rs.getLong("id"), rs.getString("user"), rs.getString("password"),
                        rs.getString("namn"), rs.getString("email"));
            } else {
                System.out.println("Fel användarnamn eller lösenord.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void updateProfile(User user) {
        System.out.println("Uppdatera din profil:");
        System.out.print("Nytt användarnamn: ");
        String newUsername = scanner.nextLine();
        System.out.print("Nytt namn: ");
        String newName = scanner.nextLine();
        System.out.print("Ny e-post: ");
        String newEmail = scanner.nextLine();
        System.out.print("Nytt lösenord: ");
        String newPassword = scanner.nextLine();

        String query = "UPDATE Users SET user = ?, namn = ?, email = ?, password = ? WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newUsername);
            stmt.setString(2, newName);
            stmt.setString(3, newEmail);
            stmt.setString(4, newPassword); // OBS! Lösenordet borde helst vara krypterat.
            stmt.setLong(5, user.getId());

            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Profilen har uppdaterats!");

                // Uppdatera användarens lokala data
                user.setUsername(newUsername);
                user.setName(newName);
                user.setEmail(newEmail);
                user.setPassword(newPassword); // Uppdatera även här om lösenordet ändras
            } else {
                System.out.println("Misslyckades med att uppdatera profilen.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Ett fel uppstod vid uppdatering av profilen.");
        }
    }

    public static void listBooks() {
        String query = "SELECT title, author, type, borrowed FROM Books ORDER BY title";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("Tillgängliga böcker:");
            while (rs.next()) {
                String title = rs.getString("title");
                String author = rs.getString("author");
                String type = rs.getString("type");
                boolean borrowed = rs.getBoolean("borrowed");

                String status = borrowed ? "Utlånad" : "Tillgänglig";
                System.out.println("- " + title + " av " + author + " (" + type + ") [" + status + "]");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Ett fel uppstod vid hämtning av böcker.");
        }
    }

    public static void borrowBook(User user) {
        System.out.print("Ange boktitel att låna: ");
        String title = scanner.nextLine();

        String query = "SELECT * FROM Books WHERE title LIKE ? AND borrowed = FALSE";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + title + "%");
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                long bookId = rs.getLong("id");
                String bookTitle = rs.getString("title");
                String bookType = rs.getString("type");

                // Sätt lånetiden baserat på boktypen
                int loanDays = bookType.equals("Bok") ? 30 : 10;
                LocalDate borrowDate = LocalDate.now();
                LocalDate returnDate = borrowDate.plusDays(loanDays);

                String insertQuery  = "INSERT INTO Borrow (userId, bookId, borrowDate, returnDate) VALUES (?, ?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery )) {
                    insertStmt.setLong(1, user.getId());
                    insertStmt.setLong(2, bookId);
                    insertStmt.setDate(3, Date.valueOf(borrowDate));
                    insertStmt.setDate(4, Date.valueOf(returnDate));
                    insertStmt.executeUpdate();


                    String updateQuery = "UPDATE Books SET borrowed = TRUE WHERE id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setLong(1, bookId);
                        updateStmt.executeUpdate();
                    }


                    System.out.println("Boken '" + bookTitle + "' har lånats ut till dig. Återlämnas senast: " + returnDate);
                }
            } else {
                System.out.println("Boken är inte tillgänglig eller redan utlånad.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Ett fel uppstod vid försök att låna boken.");
        }
    }

    public static void viewBorrowedBooks(User user) {
        String query = "SELECT Books.title, Borrow.borrowDate, Borrow.returnDate " +
                        "FROM Borrow JOIN Books ON Borrow.bookId = Books.id WHERE Borrow.userId = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, user.getId());
            ResultSet rs = stmt.executeQuery();

            boolean hasBooks = false;
            System.out.println("Dina lånade böcker: ");
            while (rs.next()) {
                System.out.println("Titel: " + rs.getString("title") +
                                    ", Lånedatum: " + rs.getDate("borrowDate") +
                                    ", Tillbakadatum: " + rs.getDate("returnDate"));
            }

            if (!hasBooks) {
                System.out.println("Du har inga lånade böcker.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Ett fel uppstod när lånade böcker hämtades. ");
        }
    }

    public static void returnBook(User user) {
        System.out.print("Ange boktitel att lämna tillbaka: ");
        String title = scanner.nextLine();

        String query = "SELECT * FROM Books WHERE title LIKE ? AND borrowed = TRUE";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + title + "%");
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                long bookId = rs.getLong("id");

                String deleteQuery = "DELETE FROM Borrow WHERE bookId = ? AND userId = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                    deleteStmt.setLong(1, bookId);
                    deleteStmt.setLong(2, user.getId());
                    deleteStmt.executeUpdate();

                    // Uppdatera bokens status till tillgänglig
                    String updateQuery = "UPDATE Books SET borrowed = FALSE WHERE id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setLong(1, bookId);
                        updateStmt.executeUpdate();
                    }

                    System.out.println("Boken har lämnats tillbaka!");
                }
            } else {
                System.out.println("Ingen bok hittades med den titeln som är utlånad.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Ett fel uppstod vid försök att lämna tillbaka boken.");
        }
    }


    public static void reserveBook(Scanner scanner, User user) {
        System.out.print("Ange titeln på boken du vill reservera: ");
        String bookTitle = scanner.nextLine();

        try (Connection conn = Database.getConnection()) {
            // SQL-fråga för att hämta bookId baserat på titeln
            String findBookQuery = "SELECT id FROM Books WHERE title LIKE ?";
            try (PreparedStatement findStmt = conn.prepareStatement(findBookQuery)) {
                findStmt.setString(1, "%" + bookTitle + "%");
                ResultSet rs = findStmt.executeQuery();

                if (rs.next()) {
                    // Hämta bok-ID
                    long bookId = rs.getLong("id");

                    // SQL-fråga för att reservera boken
                    String reserveQuery = "INSERT INTO Reservations (userId, bookId, reservationDate) VALUES (?, ?, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY))";
                    try (PreparedStatement reserveStmt = conn.prepareStatement(reserveQuery)) {
                        reserveStmt.setLong(1, user.getId());
                        reserveStmt.setLong(2, bookId);
                        reserveStmt.executeUpdate();
                        System.out.println("Boken har reserverats.");
                    }
                } else {
                    System.out.println("Boken med titeln '" + bookTitle + "' hittades inte.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Ett fel uppstod: " + e.getMessage());
        }
    }



    /*
    public static void reserveBook(Scanner scanner, User user) {
        System.out.print("Ange titeln på boken du vill reservera: ");
        String bookTitle = scanner.nextLine();

        try (Connection conn = Database.getConnection()) {
            String reserveQuery = "INSERT INTO reservations (user_id, book_id, reservation_date) VALUES (?, ?, NOW())";

            try (PreparedStatement stmt = conn.prepareStatement(reserveQuery)) {
                stmt.setLong(1, user.getId());
                stmt.setLong(2, bookId);
                stmt.executeUpdate();
                System.out.println("Boken har reserverats.");
            }
        } catch (SQLException e) {
            System.out.println("Ett fel uppstod: " + e.getMessage());
        }
    }


     */


    /*
    public static void reserveBook(User user) {
        System.out.print("Ange titeln på boken du vill reservera: ");
        String bookTitle = scanner.nextLine();

        // SQL för att hämta boken och kontrollera dess status
        String checkBookQuery = "SELECT id, title, borrowed, reserved FROM Books WHERE title LIKE ?";
        String checkReservationQuery = "SELECT * FROM Reservations WHERE bookId = ? AND availableTo >= CURDATE()";
        String reserveBookQuery = "INSERT INTO Reservations (userId, bookId, reservationDate, availableTo) VALUES (?, ?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 30 DAY))";

        try (Connection conn = Database.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkBookQuery)) {

            // Kontrollera om boken finns och är utlånad
            checkStmt.setString(1, "%" + bookTitle.trim() + "%");
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                long bookId = rs.getLong("id");
                String title = rs.getString("title");
                boolean isBorrowed = rs.getBoolean("borrowed");


                // Kolla om boken är reserverad
                try (PreparedStatement checkReservationStmt = conn.prepareStatement(checkReservationQuery)) {
                    checkReservationStmt.setLong(1, bookId);
                    ResultSet reservationRs = checkReservationStmt.executeQuery();

                    if (reservationRs.next()) {
                        System.out.println("Boken '" + title + "' är redan reserverad.");
                    } else if (isBorrowed) {
                        // Boken är utlånad, skapa en reservation
                        try (PreparedStatement reserveStmt = conn.prepareStatement(reserveBookQuery)) {
                            reserveStmt.setLong(1, user.getId());
                            reserveStmt.setLong(2, bookId);
                            reserveStmt.executeUpdate();

                            // Beräkna det tillgängliga datumet
                            LocalDate reservationAvailableDate = LocalDate.now().plusDays(30);
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            String formattedDate = reservationAvailableDate.format(formatter);

                            System.out.println("Boken '" + title + "' har reserverats. Reservationen gäller i 30 dagar efter att boken blir tillgänglig.");
                        }
                    } else {
                        System.out.println("Boken '" + title + "' är tillgänglig och behöver inte reserveras.");
                    }
                }
            } else {
                System.out.println("Boken hittades inte.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Ett fel uppstod vid reservation av boken.");
        }
    }

     */

}
