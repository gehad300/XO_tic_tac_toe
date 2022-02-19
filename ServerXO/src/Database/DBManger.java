/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Database;

import Game.Player;
import Network.Message;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 //preparee statement to prepare statement  and stored it in an object to retreive it .
 //also it's an 
 The PreparedStatement interface is a subinterface of Statement. It is used to execute parameterized query.

Let's see the example of parameterized query:
//dynamic values it will execute when i enter a value 
String sql="insert into emp values(?,?,?)";  
 * @author Gehad
 */
public class DBManger {

    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    //receieve whatever comes from select statement

    public DBManger() {
        System.out.println("Database Connection" + connect());
        //test to see  if  connection is done or not to see it on console
    }

    boolean connect() {
        try {

            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            //initialize mysql driver
            connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/tiktok_game", "root", "123456789");
           
            
          
            System.out.println("====> DataBase Connected <====");
            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;

        }
        //TO DO SEARCH FOR PRINTSTAKE EXCEPTION
        //PRINT EXCEPTION WITH DETAILS LIKE WHERE IS THE EXCEPTION AND IN WHICH LINE
    }

    public int login(String name, String Password) {
        try {
            statement = connect.createStatement();
            String queryst = new String("select * from players where name = '" + name + "' and password = '" + Password + "' ;");
            resultSet = statement.executeQuery(queryst);
            if (resultSet.next()) {
                return Integer.parseInt(resultSet.getString(1));  //NOTE
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return -1;//سالب واحد دليل علي الفشل قاله محمود نهرو
    }

    public boolean signUp(String name, String password, String email) {
        try {
            // String queryst;
            //check  username
            statement = connect.createStatement();
            String queryst = "select * from players where name = '" + name + "' ;";
            resultSet = statement.executeQuery(queryst);
            if (resultSet.next()) {
                return false;
            } else {
                queryst = "insert into players (name,password,email) values( '" + name + "', '" + password + "','" + email + "') ;";
                //            resultSet = statement.executeUpdate(queryst);
                statement.executeUpdate(queryst);

                return true;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

    }

    public boolean setGame(int x, int o, String senario) {
        //scenario here is the game it self 

        try {
             
            statement = connect.createStatement();
            String queryst = "select * from game where x_user = " + x + " and o_user = " + o + " and  senario = '" + senario + "'";

            resultSet = statement.executeQuery(queryst);
            if (resultSet.next()) {
                return true;
            } else {
                String queryst2 = "select * from game where x_user = " + x + " and o_user = " + o + " ";
                resultSet = statement.executeQuery(queryst2);
                if (resultSet.next()) {
                    String query = ("update game set senario ='" + senario + "'  where x_user = " + x + " and o_user = " + o + " ;");
                    statement.executeUpdate(query);
                    return true;

                } else {
                    queryst = "insert into game (x_user,o_user,senario) values(" + x + ",'" + o + "','" + senario + "')";
                    statement.executeUpdate(queryst);
                    return true;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     *
     * @param player
     * @throws SQLException
     */
    public void update(Player player) throws SQLException {
        try {
            // take player
            String query = ("update players set points = ? where id = ?;");
            PreparedStatement preparedStmt = connect.prepareStatement(query);
            preparedStmt.setInt(1, player.getPoints());
            preparedStmt.setInt(2, player.getIdnum());
            preparedStmt.executeUpdate();
        } catch (Exception ex) {

        }
    }

    public ArrayList<Player> loadPlayer() {
        ArrayList<Player> players = new ArrayList<Player>();
        try {
            statement = connect.createStatement();
            String queryst = new String("select id,name,points from players ORDER BY points DESC ;");
            resultSet = statement.executeQuery(queryst);
            System.out.println("Loading Table");
            while (resultSet.next()) {
                players.add(new Player(resultSet.getInt(1), resultSet.getString(2), resultSet.getInt(3)));
            }
            //add function in array list to add p.ayer in arraylist

        } catch (SQLException ex) {
            Logger.getLogger(DBManger.class.getName()).log(Level.SEVERE, null, ex);
        }
        return players;
        //players is the array list
    }
    //////////////////////////////////////

    public String[] getGameBoard(Message msg) {
        String senarioGame = "";
        try {
            statement = connect.createStatement();
            String queryst = new String("select * from game where ( x_user = '" + Integer.parseInt(msg.getData()[1]) + "' and o_user = '" + Integer.parseInt(msg.getData()[2]) + "' ) or ( x_user = '" + Integer.parseInt(msg.getData()[2]) + "' and o_user = '" + Integer.parseInt(msg.getData()[1]) + "' ) ;");
            resultSet = statement.executeQuery(queryst);
            if (resultSet.next()) {
                return new String[]{resultSet.getString("senario"), resultSet.getString("x_user")};
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return new String[]{"", ""};
    }

    public int deleteOldGame(int p1id, int p2id) {
        try {
            statement = connect.createStatement();
            String queryst = new String("delete from game where ( x_user = '" + p1id + "' and o_user = '" + p2id + "' ) or ( x_user = '" + p2id + "' and o_user = '" + p1id + "' ) ;");
            statement.executeUpdate(queryst);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return 0;
    }

    public Connection getConnect() {
        return connect;
    }

    public void setConnect(Connection connect) {
        this.connect = connect;
    }

}
