-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: mysql
-- Generation Time: Jan 24, 2025 at 10:13 PM
-- Server version: 8.0.40
-- PHP Version: 8.2.8

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `Bibliotekssystem`
--

-- --------------------------------------------------------

--
-- Table structure for table `Books`
--

CREATE TABLE `Books` (
  `id` bigint NOT NULL,
  `title` varchar(255) NOT NULL,
  `author` varchar(255) DEFAULT NULL,
  `type` enum('Bok','Tidskrifter','Media') NOT NULL,
  `borrowed` tinyint(1) DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `Books`
--

INSERT INTO `Books` (`id`, `title`, `author`, `type`, `borrowed`) VALUES
(1, 'Sagan om Ringen', 'J.R.R. Tolkien', 'Bok', 0),
(2, 'National Geographic', 'National Geographic Society', 'Tidskrifter', 1),
(3, 'Star Wars: A New Hope', 'George Lucas', 'Media', 0),
(4, 'Harry Potter och De Vises Sten', 'J.K. Rowling', 'Bok', 0),
(5, 'Time Magazine', 'Time Inc.', 'Tidskrifter', 0);

-- --------------------------------------------------------

--
-- Table structure for table `Borrow`
--

CREATE TABLE `Borrow` (
  `id` bigint NOT NULL,
  `userId` bigint NOT NULL,
  `bookId` bigint NOT NULL,
  `borrowDate` date NOT NULL,
  `returnDate` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `Borrow`
--

INSERT INTO `Borrow` (`id`, `userId`, `bookId`, `borrowDate`, `returnDate`) VALUES
(5, 1, 2, '2025-01-24', '2025-02-03');

-- --------------------------------------------------------

--
-- Table structure for table `Reservations`
--

CREATE TABLE `Reservations` (
  `id` bigint NOT NULL,
  `userId` bigint NOT NULL,
  `bookId` bigint NOT NULL,
  `reservationDate` datetime NOT NULL,
  `availableTo` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `Reservations`
--

INSERT INTO `Reservations` (`id`, `userId`, `bookId`, `reservationDate`, `availableTo`) VALUES
(1, 2, 4, '2025-01-24 20:43:14', '2025-02-23 20:43:14'),
(2, 2, 2, '2025-01-24 21:33:28', '2025-02-23 21:33:28');

-- --------------------------------------------------------

--
-- Table structure for table `Users`
--

CREATE TABLE `Users` (
  `id` bigint NOT NULL,
  `user` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `namn` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `registrationDate` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `Users`
--

INSERT INTO `Users` (`id`, `user`, `password`, `namn`, `email`, `registrationDate`) VALUES
(1, 'admin', 'admin123', 'Admin Användare', 'admin@example.com', '2025-01-24 20:41:24'),
(2, 'user1', 'password1', 'Första Användare', 'user1@example.com', '2025-01-24 20:41:24'),
(3, 'user2', 'password2', 'Andra Användare', 'user2@example.com', '2025-01-24 20:41:24');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `Books`
--
ALTER TABLE `Books`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `Borrow`
--
ALTER TABLE `Borrow`
  ADD PRIMARY KEY (`id`),
  ADD KEY `userId` (`userId`),
  ADD KEY `bookId` (`bookId`);

--
-- Indexes for table `Reservations`
--
ALTER TABLE `Reservations`
  ADD PRIMARY KEY (`id`),
  ADD KEY `userId` (`userId`),
  ADD KEY `bookId` (`bookId`);

--
-- Indexes for table `Users`
--
ALTER TABLE `Users`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `Books`
--
ALTER TABLE `Books`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `Borrow`
--
ALTER TABLE `Borrow`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `Reservations`
--
ALTER TABLE `Reservations`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `Users`
--
ALTER TABLE `Users`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `Borrow`
--
ALTER TABLE `Borrow`
  ADD CONSTRAINT `Borrow_ibfk_1` FOREIGN KEY (`userId`) REFERENCES `Users` (`id`),
  ADD CONSTRAINT `Borrow_ibfk_2` FOREIGN KEY (`bookId`) REFERENCES `Books` (`id`);

--
-- Constraints for table `Reservations`
--
ALTER TABLE `Reservations`
  ADD CONSTRAINT `Reservations_ibfk_1` FOREIGN KEY (`userId`) REFERENCES `Users` (`id`),
  ADD CONSTRAINT `Reservations_ibfk_2` FOREIGN KEY (`bookId`) REFERENCES `Books` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
