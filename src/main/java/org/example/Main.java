package org.example;

import java.util.Scanner;

import static org.example.LibrarySystem.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Välkommen till bibliotekssystemet!");
        User user = login();
        Scanner scanner = new Scanner(System.in);

        if (user != null) {
            System.out.println("Välkommen, " + user.getName());

            // Boolean variabel för att kontrollera om programmet ska fortsätta köra
            boolean running = true;

            // Startar en loop som fortsätter tills användaren väljer att avsluta
            while (running) {
                // Skriver ut menyn med olika alternativ
                System.out.println("1. Uppdatera profil");
                System.out.println("2. Lista böcker");
                System.out.println("3. Låna bok");
                System.out.println("4. Visa lånade böcker");
                System.out.println("5. Reservera bok");
                System.out.println("6. Återlämna bok");
                System.out.println("7. Sök efter böcker");
                System.out.println("8. Avsluta");

                // Ber användaren om att välja ett alternativ
                System.out.print("Välj ett alternativ: ");
                // Läser in användarens val (en siffra) och konverterar det till ett heltal
                int choice = Integer.parseInt(scanner.nextLine());

                // Switch-sats som hanterar olika alternativ beroende på användarens val
                switch (choice) {
                    case 1:
                        updateProfile(user);
                        break;
                    case 2:
                        listBooks();
                        break;
                    case 3:
                        borrowBook(user);
                        break;
                    case 4:
                        viewBorrowedBooks(user);
                        break;
                    case 5:
                        reserveBook(scanner, user);
                        break;
                    case 6:
                        viewBorrowedBooks(user);
                        returnBook(user);
                        break;
                    case 7:
                        searchBooks();
                        break;
                    case 8:
                        System.out.println("Avslutar programmet...");
                        return;
                    default:
                        System.out.println("Ogiltigt val. Försök igen.");
                }
            }
        }

        Database.closeConnection();
    }
    }
