package frostdev.frostdev.DBCOMMIT;

import frostdev.frostdev.HMDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerEconCommit {
    private HMDB main;
    private TableSetup tableSetup;
    private String table = "userdata";
    private ResultSet result;


    public void EconCommit (String UUID, String econ, Connection connection){

        String exist = "SELECT * FROM userdata WHERE UUID='" + UUID + "';";
        String sql;
        try {
            PreparedStatement update = connection.prepareStatement(exist);
            result = update.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if(!result.next()) {
                try {
                    sql = "INSERT INTO userdata(econbal) VALUES ('"+ econ + "' );";
                    PreparedStatement stm = connection.prepareStatement(sql);
                    stm.executeUpdate();
                } catch (SQLException e){
                    try {
                        tableSetup.OnTableSetup("userdata", "username", connection);
                    }catch (SQLException e1){
                        e1.printStackTrace();
                    }
                }
            }else {
                sql = "UPDATE userdata SET econbal ='" + econ + "'" + "WHERE UUID = '"+ UUID + "';";
                PreparedStatement stm = connection.prepareStatement(sql);
                stm.executeUpdate();
            }
        } catch (SQLException e){
            e.printStackTrace();
        }



    }
}
