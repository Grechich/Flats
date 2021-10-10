package com.gmail.grechich;

import java.sql.*;
import java.util.Scanner;

public class Main {
    static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/Flats?serverTimezone=Europe/Kiev";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "password";
    static Connection conn;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            try {
                conn = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
//                initDB();  // if you want to create new table Flats
                while (true) {
                    System.out.println("1: add flat");
                    System.out.println("2: view flats");
                    System.out.println("3: delete flat");
                    System.out.println("4: change flat");
                    System.out.println("5: select flats with price");
                    System.out.print("-> ");
                    String s = sc.nextLine();
                    switch (s) {
                        case "1":
                            addFlat(sc);
                            break;
                        case "2":
                            viewFlats();
                            break;
                        case "3":
                            deleteFlat(sc);
                            break;
                        case "4":
                            changeFlat(sc);
                            break;
                        case "5":
                            selectFlatsWithPrice(sc);
                            break;
                        default:
                            return;
                    }
                }
            } finally {
                sc.close();
                if (conn != null) conn.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return;
        }
    }

    private static void initDB() throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("DROP TABLE IF EXISTS Flats");
            st.execute("CREATE TABLE Flats (" +
                    "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "district VARCHAR(100), " +
                    "address VARCHAR(100), " +
                    "square INT, " +
                    "rooms INT," +
                    "price DOUBLE)");
        }
    }

    private static void addFlat(Scanner sc) throws SQLException {
        System.out.print("Enter district: ");
        String district = sc.nextLine();
        System.out.print("Enter address: ");
        String address = sc.nextLine();
        System.out.print("Enter square: ");
        String sSquare = sc.nextLine();
        int square = Integer.parseInt(sSquare);
        System.out.print("Enter rooms: ");
        String sRooms = sc.nextLine();
        int rooms = Integer.parseInt(sRooms);
        System.out.print("Enter price: ");
        String sPrice = sc.nextLine();
        double price = Double.parseDouble(sPrice);
        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO Flats (district, address, square, rooms, price) VALUES(?, ?, ?, ?, ?)");
        try {
            ps.setString(1, district);
            ps.setString(2, address);
            ps.setInt(3, square);
            ps.setInt(4, rooms);
            ps.setDouble(5, price);
            ps.executeUpdate();
        } finally {
            ps.close();
        }
    }

    private static void viewFlats() throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Flats");
        try {
            ResultSet rs = ps.executeQuery();
            try {
                ResultSetMetaData md = rs.getMetaData();
                for (int i = 1; i <= md.getColumnCount(); i++)
                    System.out.print(md.getColumnName(i) + "\t");
                System.out.println();
                while (rs.next()) {
                    for (int i = 1; i <= md.getColumnCount(); i++) {
                        System.out.print(rs.getString(i) + "\t\t");
                    }
                    System.out.println();
                }
            } finally {
                rs.close();
            }
        } finally {
            ps.close();
        }
    }

    private static void deleteFlat(Scanner sc) throws SQLException {
        System.out.print("Enter flat ID: ");
        String id = sc.nextLine();
        PreparedStatement ps = conn.prepareStatement("DELETE FROM Flats WHERE ID = ?");
        try {
            ps.setString(1, id);
            ps.executeUpdate();
        } finally {
            ps.close();
        }
    }

    private static void changeFlat(Scanner sc) throws SQLException {
        System.out.print("Enter flat id: ");
        String id = sc.nextLine();
        System.out.print("Enter new price: ");
        String sPrice = sc.nextLine();
        double price = Double.parseDouble(sPrice);
        PreparedStatement ps = conn.prepareStatement("UPDATE Flats SET price = ? WHERE ID = ?");
        try {
            ps.setDouble(1, price);
            ps.setString(2, id);
            ps.executeUpdate();
        } finally {
            ps.close();
        }
    }

    private static void selectFlatsWithPrice(Scanner sc) throws SQLException {
        System.out.println("Enter the lowest price:");
        String sLowestPrice = sc.nextLine();
        double lowestPrice = Double.parseDouble(sLowestPrice);
        System.out.println("Enter the highest price:");
        String sHighestPrice = sc.nextLine();
        double highestPrice = Double.parseDouble(sHighestPrice);
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Flats WHERE price>? AND price<?");
        try {
            ps.setDouble(1, lowestPrice);
            ps.setDouble(2, highestPrice);
            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData md = rs.getMetaData();
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    System.out.print(md.getColumnName(i) + "\t");
                }
                System.out.println();
                while (rs.next()) {
                    for (int i = 1; i <= md.getColumnCount(); i++) {
                        System.out.print(rs.getString(i) + "\t\t");
                    }
                    System.out.println();
                }
            }
        } finally {
            ps.close();
        }
    }
}
