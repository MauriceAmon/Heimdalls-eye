/*
 * The MIT License
 *
 * Copyright 2016 Maurice Amon.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.example.heimdallseye.Controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 *
 * @author Maurice Amon
 */
public class DatabaseConnection {
        // Zustaendig fuer die Verbindung zur Datenbank ...
    private static Connection conn = null;
    private static DatabaseConnection con = null;

    private final Integer PORT = 1527;
    private final String DB_NAME = "HeimdallsEye";
    private final String USER_NAME = "root";
    private final String USER_PASS = "";

    public DatabaseConnection() {

    }

    // Holt eigene Instanz ...
    public static DatabaseConnection getInstance() {
        if (con == null) {
            con = new DatabaseConnection();
        }
        return con;
    }

    // Stellt die Verbindung zur DB her ...
    public Connection getConnection() {

        try {
            String classpath = System.getProperty("user.dir");
            //System.out.println(classpath);
            // Instanttiert ein neues Object des Drivers ...
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            // Initiiere die Verbindung zur DATABASE ...
            //System.out.println("jdbc:derby:" + classpath + "/src/main/java/com/example/heimdallseye/test2_db");
            conn = DriverManager.getConnection("jdbc:derby:" + classpath + "/src/main/java/com/example/heimdallseye/test2_db");
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | SQLException e) {
            // Fehler/Dialog ausgeben/anzeigen ...
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.out.println("Can't connect to database ...");
        }
        return conn;
    }
}
