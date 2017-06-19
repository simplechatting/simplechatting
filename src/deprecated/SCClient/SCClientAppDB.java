package deprecated.SCClient;

import Settings.*;

import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * Created by penguin on 17. 6. 15.
 */
public class SCClientAppDB {
    public SCClientAppGUI view;
    public SCClientApp controller;

    ////////////////// 데이터 베이스 //////////////////
    private String dbname;

    public SCClientAppDB() {
    }

    public void startDB(String dbname) {
        this.dbname = dbname;
        File file = new File(dbname + ".db");
        if (!file.exists()) {
            try { // DB 없으면 생성
                Class.forName("org.sqlite.JDBC").newInstance();
                Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbname + ".db");
                Statement stmt = conn.createStatement();
                stmt.close();
                conn.close();
            } catch (IllegalAccessException | SQLException | ClassNotFoundException | InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    public void createRoom(String roomName) {
        Connection conn = null;
        Statement stmt = null;

        try {
            Class.forName("SQLite.JDBCDriver");
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbname);
            System.out.println("데이터 베이스 연결");

            stmt = conn.createStatement();
            String query = "CREATE TABLE IF NOT EXISTS,   " + roomName +
                    " ( LINE INT PRIMARY KEY    NOT NULL, " +
                    "   USERID         CHAR(50) NOT NULL, " +
                    "   MESSAGE          TEXT   NOT NULL);";
            stmt.executeUpdate(query);
            stmt.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        System.out.println("테이블 생성 성공");
    }

    public List<Settings.SCMessage> retrieveMessages(String roomName){
        Connection conn = null;
        Statement stmt = null;
        List<Settings.SCMessage> messages = new LinkedList<>();

        try {
            Class.forName("org.sqlite.JDBC").newInstance();
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbname);
            System.out.println("데이터 베이스 연결");

            stmt = conn.createStatement();
            String query = "SELECt * FROM " + roomName + ";";
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                messages.add(new Settings.SCMessage(rs.getInt(0), rs.getString(1), rs.getString(1)));
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public void insertMessages(String roomName, List<Settings.SCMessage> messages){

        Connection conn = null;
        Statement stmt = null;

        try {
            Class.forName("SQLite.JDBCDriver");
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbname);
            System.out.println("데이터 베이스 연결");

            stmt = conn.createStatement();
            String sql = "";
            for(Settings.SCMessage m : messages) {
                sql +=  "INSERT INTO " + roomName +
                        "(LINE, USERID, MESSAGE) "+
                        "VALUES ( " + m.line +"," +
                        "'"         + m.name +"',"+
                        "'"         + m.msg  +"');";
            }
            stmt.executeUpdate(sql);
            stmt.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteMessages(String roomName){
        Connection conn = null;
        Statement stmt = null;

        try {
            Class.forName("SQLite.JDBCDriver");
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbname);
            System.out.println("데이터 베이스 연결");
            stmt = conn.createStatement();
            String query = String.format("DROP TABLE IF EXISTS '%s.%s'", dbname, roomName);
            stmt.executeUpdate(query);
            stmt.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}