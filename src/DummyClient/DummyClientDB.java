package DummyClient;

import java.io.*;
import java.sql.*;

/**
 * Created by penguin on 17. 6. 15.
 */
public class DummyClientDB {
    public DummyClientGUI view;
    public DummyClient controller;

    ////////////////// 데이터 베이스 //////////////////
    private String dbname;
    private Connection conn;
    private Statement statement;

    public DummyClientDB(){}
    public void StartDB(String dbname) {
        this.dbname = dbname;
        File file = new File(dbname + ".db");
        if(!file.exists()){
            try { // DB 없으면 생성
                Class.forName("SQLite.JDBCDriver").newInstance();
                conn = DriverManager.getConnection("jdbc:sqlite:/"+dbname+".db");
                statement = conn.createStatement();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


    }
}
