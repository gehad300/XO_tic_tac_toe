/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverxo;

import Database.DBManger;
import Game.GameController;
import Game.Player;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author EgyJuba
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    Button btn;
    int Count = 2;
    Image onOff;
    String state;
  

    @FXML
    private Button closeBtn, minBtn;

    @FXML
    private TableColumn<Player, Integer> tblId;
    @FXML
    private TableColumn<Player, String> tblNames;
    @FXML
    private TableColumn<Player, Integer> tblScore;
    @FXML
    private TableView<Player> tableScores;
    @FXML
    private TableColumn<Player, Text> stateplay;

    Image imgPlace;
    DBManger db = new DBManger();
    GameController gc;

    static ObservableList<Player> usersList = FXCollections.observableArrayList();
    @FXML
    private Label label;

    public FXMLDocumentController() {
        this.tableScores = new TableView<>();
        this.tblId = new TableColumn();
        this.tblNames = new TableColumn();
        this.tblScore = new TableColumn();
        this.stateplay = new TableColumn();
    }

    public Image Count() throws IOException {

        if ((Count % 2) == 0) {
            onOff = new Image(getClass().getResourceAsStream("on.png"));
            state = "on";
            // start server
            gc = new GameController();
            gc.start();
            loadUserList();
            // Start Table
            tableScores.setItems(usersList);
        } else {
            onOff = new Image(getClass().getResourceAsStream("off.png"));
            state = "off";
           
            // stop server
            gc.close();

            // Stop Table
            tableScores.setItems(null);
        }
        return onOff;
    }

    @FXML
    private void closeButtonAction() throws IOException {

        // Close Window Button
        Stage closeStage = (Stage) closeBtn.getScene().getWindow();
        // stop server
        gc.close();
        closeStage.close();
    }

    @FXML
    private void minButtonAction() {

        // Close Window Button
        Stage minStage = (Stage) minBtn.getScene().getWindow();
        minStage.setIconified(true);
    }

    @FXML
    public void ServerAction(ActionEvent e) throws IOException {
        btn = (Button) e.getSource();
        btn.setGraphic(new ImageView(Count()));
        Count++;
    }

    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        btn.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("off.png"))));
        // Funn Server Pics

        // Initializing the columns
        tblId.setCellValueFactory(new PropertyValueFactory<>("idnum"));
        tblNames.setCellValueFactory(new PropertyValueFactory<>("names"));
        tblScore.setCellValueFactory(new PropertyValueFactory<>("points"));
        stateplay.setCellValueFactory(new PropertyValueFactory<>("online"));
        // add Event Listener To table List
        // Add it to Client List Table

        tableScores.setOnMouseClicked((MouseEvent click) -> {
            if (click.getClickCount() == 2) {
                tableScores.getSelectionModel().getSelectedItem();
                System.out.println("You Clicked Player : "
                        + ((Player) tableScores.getSelectionModel().getSelectedItem()).getNames());
            }
        });

    }

    public static void loadUserList() {
        ArrayList<Player> playerList = GameController.players;
        int listSize = playerList.size();
        usersList.clear();
        for (int i = 0; i < listSize; i++) {
            Player p = playerList.get(i);
            usersList.add(p);
            System.out.println("Id : " + p.getIdnum() + "  Name : " + p.getNames() + "  Points : " + p.getPoints());
        }
        System.out.println("UserList Size: " + usersList.size());
    }

    public static void updatePlayerList() {
        Platform.runLater(() -> {
            TableView<Player> playerTable = (TableView<Player>) ServerXO.getGlobalStage().getScene()
                    .lookup("#tableScores");
            if (playerTable != null) {
                System.out.println("FILL THE TABLE");
                loadUserList();
                playerTable.setItems(usersList);
                // tableScores.setItems(usersList);
            }
        });
    }

    public static void sortPlayerList() {
        Collections.sort(GameController.players, new Comparator<Player>() {
            @Override
            public int compare(Player p1, Player p2) {
                return p2.getPoints() - p1.getPoints();
            }
        });
    }
}
