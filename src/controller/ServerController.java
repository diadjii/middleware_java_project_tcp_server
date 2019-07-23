package controller;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import model.Log;
import service.Server;

public class ServerController implements Initializable {

    public static ArrayList<Log> logs = new ArrayList<>();
    @FXML
    private Button runCloseBtn;

    @FXML
    private TableView<Log> logTable;

    @FXML
    private TableColumn<Log, String> userAction;

    @FXML
    private TableColumn<Log, String> useradressIP;

    @FXML
    private TableColumn<Log, String> nameMachine;

    @FXML
    private TableColumn<Log, String> dateEtHeure;

    @FXML
    private Text statusText;

    private boolean status = false;

    ServerSocket serverSocket;

    static ObservableList<Log> data = FXCollections.observableArrayList();

    @FXML
    void closeServer(ActionEvent event) {
	System.exit(0);
    }

    @FXML
    void runServer(ActionEvent event) {

	if (status == false) {
	    new Thread(() -> {
		try {
		    serverSocket = new ServerSocket(8888);
		    Log log = new Log("Démarrage du serveur", "...", "...", "" + new Date());
		    data.add(log);

		    while (true) {
			Socket socket = serverSocket.accept();

			Platform.runLater(() -> {

			    InetAddress inetAddress = socket.getInetAddress();
			    String adressMachine = inetAddress.getHostAddress();
			    String nomMachine = inetAddress.getHostName();
			    Log log1 = new Log("Connexion du client", adressMachine, nomMachine, "" + new Date());
			    data.add(log1);
			});
			new Thread(new Server(socket)).start();
			logTable.setItems(data);
		    }
		} catch (Exception e) {
		    System.out.println("Erreur de connexion :" + e.getMessage());
		}
	    }).start();

	    status = true;

	    statusText.setText("Le serveur a démarré");
	    statusText.setStyle("-fx-fill:green");

	    runCloseBtn.setText("Arréter");
	    runCloseBtn.setStyle("-fx-background-color:#ff401f");
	} else {
	    try {
		serverSocket.close();

		Log log = new Log("Arr�t du serveur", "...", "...", "" + new Date());
		data.add(log);

		statusText.setText("Le serveur a arr�t�");
		statusText.setStyle("-fx-fill:#ff401f");

		runCloseBtn.setText("D�marrer");
		runCloseBtn.setStyle("-fx-background-color:green");

		status = false;

	    } catch (IOException e) {
		System.out.println("Probl�me d'arr�t du serveur :" + e.getMessage());
	    }
	}
    }

    public static void setToData(Object odj) {
	// data.add(odj);
    }

    public static void setTable() {
	logs.forEach(log -> data.add(log));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
	userAction.setCellValueFactory(new PropertyValueFactory<>("Action"));
	useradressIP.setCellValueFactory(new PropertyValueFactory<>("adressMachine"));
	nameMachine.setCellValueFactory(new PropertyValueFactory<>("nomMachine"));
	dateEtHeure.setCellValueFactory(new PropertyValueFactory<>("dateEtHeure"));

	logTable.setItems(data);
    }
}
