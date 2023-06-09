package com.wrmanager.wrmanagerfx.controllers;


import com.wrmanager.wrmanagerfx.Constants;
import com.wrmanager.wrmanagerfx.Main;
import com.wrmanager.wrmanagerfx.controllers.OuiNonDialogController;
import com.wrmanager.wrmanagerfx.controllers.SideBarController;

import com.wrmanager.wrmanagerfx.entities.*;


import com.wrmanager.wrmanagerfx.models.SystemMeasure;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.effects.DepthLevel;
import javafx.animation.AnimationTimer;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.Duration;
import net.sf.jasperreports.engine.JRException;
import org.apache.poi.ss.formula.functions.Code;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.awt.Button;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

import static com.wrmanager.wrmanagerfx.Constants.*;


public class CaisseViewController implements Initializable {


    @FXML
    private MFXButton AnnulerBtn;

    @FXML
    private Button AvatarIcon;

    @FXML
    private TextField BonTf;

    @FXML
    private MFXButton HomeAppBtn;



    @FXML
    private MFXButton Btn0;

    @FXML
    private MFXButton Btn1;

    @FXML
    private MFXButton Btn2;

    @FXML
    private MFXButton Btn3;

    @FXML
    private MFXButton Btn4;

    @FXML
    private MFXButton Btn5;

    @FXML
    private MFXButton Btn6;

    @FXML
    private MFXButton Btn7;

    @FXML
    private MFXButton Btn8;

    @FXML
    private MFXButton Btn9;

    @FXML
    private MFXButton BtnEnter;

    @FXML
    private MFXButton BtnPlus;

    @FXML
    private MFXButton BtnPoint;

    @FXML
    private MFXButton Caisse1;

    @FXML
    private MFXButton Caisse2;

    @FXML
    private MFXButton Caisse3;


    private MFXButton[] caisses;

    private IntegerProperty currentCaisseIndex = new SimpleIntegerProperty(0);

    @FXML
    private TextField ClientTfd;

    @FXML
    private TextField CodeBarreTF;

    @FXML
    private TextField DesignationTF;

    @FXML
    private GridPane FavorieItemGrid;

    @FXML
    private GridPane GroupItemGrid;

    @FXML
    private Button NotificationIcon;

    @FXML
    private TextField PoidsTF;

    @FXML
    private TableView<Stock> ProduitsTable;



    @FXML
    private ImageView imageView;


    private static final String CAMERA_IP = "http://192.168.43.1:8080/video";
    public Mat frame;
    public Image image;
    public static Thread thread;
    public static Object lock = new Object();
    public VideoCapture video;
    ImageProcessing imageProcessing = new ImageProcessing();

    @FXML
    void PlayButtonOnAction(ActionEvent event) {

        thread = new Thread(() -> {
            frame=new Mat();
            while (true) {
                synchronized (lock) {
                    video = new VideoCapture(CAMERA_IP);
                    if (video.isOpened()) System.out.println("it works");
                    video.read(frame);
                    Mat processedFrame = imageProcessing.processFrame(frame);
                    image = imageProcessing.matToImage(processedFrame);
                    imageView.setImage(image);
                }
            }
        });

        thread.start();
    }

    @FXML
    void CameraButtonOnAction(ActionEvent event){
        thread.stop();
        imageView.setImage(image);
        //ImageProcessing.saveCapturedImage(frame);;
        thread.stop();
        thread.stop();
        thread.stop();
    }

    public TableView<Stock> getProduitsTable() {
        return ProduitsTable;
    }

    private TableColumn<Stock, Integer> ordreColumn = new TableColumn<>("id");
    private TableColumn<Stock, String> designationColumn = new TableColumn<>("designation");
    private TableColumn<Stock, String> dosageColumn = new TableColumn<>("dosage");
    private TableColumn <Stock, Integer> qtyColumn = new TableColumn<>("qty");
    private TableColumn<Stock, Float> ppaColumn = new TableColumn<>("ppa");



    @FXML
    private MFXButton NonExistingProductBtn;

    @FXML
    private AnchorPane RootAnchor;

    @FXML
    private MFXButton SupprimerBtn;

    @FXML
    private Label Time;

    @FXML
    private Label TotaleTF;

    @FXML
    private MFXButton ValiderBtn;

    @FXML
    private Label userLabel;

    @FXML
    private Label QtyRestTF;


    @FXML
    private MFXButton ChangerPrix;


    private String keyboardInput = "";

    ObservableList<Notification> notificationsQueue = FXCollections.observableArrayList();



    @FXML
    void Btn0OnAction(ActionEvent event) {
        keyboardInput = keyboardInput.concat("0");
    }

    @FXML
    void Btn1OnAction(ActionEvent event) {
        keyboardInput = keyboardInput.concat("1");
    }

    @FXML
    void Btn2OnAction(ActionEvent event) {
        keyboardInput = keyboardInput.concat("2");
    }

    @FXML
    void Btn3OnAction(ActionEvent event) {
        keyboardInput = keyboardInput.concat("3");
    }

    @FXML
    void Btn4OnAction(ActionEvent event) {
        keyboardInput = keyboardInput.concat("4");
    }

    @FXML
    void Btn5OnAction(ActionEvent event) {
        keyboardInput = keyboardInput.concat("5");
    }

    @FXML
    void Btn6OnAction(ActionEvent event) {
        keyboardInput = keyboardInput.concat("6");
    }

    @FXML
    void Btn7OnAction(ActionEvent event) {
        keyboardInput = keyboardInput.concat("7");
    }

    @FXML
    void Btn8OnAction(ActionEvent event) {
        keyboardInput = keyboardInput.concat("8");
    }

    @FXML
    void Btn9OnAction(ActionEvent event) {
        keyboardInput = keyboardInput.concat("9");
    }

    @FXML
    void BtnPointOnAction(ActionEvent event) {
        keyboardInput = keyboardInput.concat(".");
    }

    @FXML
    void BtnEnterOnAction(ActionEvent event) throws IOException {

    }



    @FXML
    void BtnPlusOnAction(ActionEvent event) {
        keyboardInput = "";
    }


    @FXML
    void SceneOnKeyPressed(KeyEvent event) {

        var key = event.getCode();
        switch (key) {
            case ADD -> {
                BtnPlus.fire();
                break;
            }
            case ENTER -> {
                BtnEnter.fire();
                break;
            }

            case NUMPAD0 -> {
                Btn0.fire();
                break;
            }
            case NUMPAD1 -> {
                Btn1.fire();
                break;
            }
            case NUMPAD2 -> {
                Btn2.fire();
                break;
            }
            case NUMPAD3 -> {
                Btn3.fire();
                break;
            }
            case NUMPAD4 -> {
                Btn4.fire();
                break;
            }
            case NUMPAD5 -> {
                Btn5.fire();
                break;
            }
            case NUMPAD6 -> {
                Btn6.fire();
                break;
            }
            case NUMPAD7 -> {
                Btn7.fire();
                break;
            }
            case NUMPAD8 -> {
                Btn8.fire();
                break;
            }
            case NUMPAD9 -> {
                Btn9.fire();
                break;
            }
            case DECIMAL -> {
                BtnPoint.fire();
                break;
            }


        }


    }

    @FXML
    void AnnulerBtnOnAction(ActionEvent event) {

    }

    @FXML
    void Caisse1OnAction(ActionEvent event) {

    }

    @FXML
    void Caisse2OnAction(ActionEvent event) {


    }

    @FXML
    void Caisse3OnAction(ActionEvent event) {

    }



    @FXML
    void SupprimerBtnOnAction(ActionEvent event) {

        var produit = ProduitsTable.getSelectionModel().getSelectedItem();
        int index = currentCaisseIndex.getValue();
        ObservableList<Stock> list = (ObservableList<Stock>) caisses[index].getUserData();
        list.remove(produit);


    }

    @FXML
    void ValiderBtnOnAction(ActionEvent event) throws FileNotFoundException, JRException {

    }



    private void CurrentTimeLabel() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                Time.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }
        };
        timer.start();

    }




        public void addProduitToCurrentCaisse(Produit produit) throws IOException {

        }


        private void setupTotaleTF() {

        }


    private void divideTableWidthOnColumns() {
/*
        Double size = Double.valueOf(ProduitsTable.getColumns().size());
        //calculate the size of each column
        double sizeCoulumn = 1 / (size);
        //set the size of each column
        ordreColumn.prefWidthProperty().bind(ProduitsTable.widthProperty().multiply(0.1));
        prixUniteColumn.setSortable(true);
        prixUniteColumn.prefWidthProperty().bind(ProduitsTable.widthProperty().multiply(0.3));
        designationColumn.prefWidthProperty().bind(ProduitsTable.widthProperty().multiply(0.23));
        qtyColumn.prefWidthProperty().bind(ProduitsTable.widthProperty().multiply(0.1));
        prixTotaleColumn.prefWidthProperty().bind(ProduitsTable.widthProperty().multiply(0.26));

*/
    }


    private void setupValueFactories() {
        /*
        //define the cells factory
        ordreColumn.setCellValueFactory(
                new PropertyValueFactory<ProduitCaisse, Integer>("ordre"));

        designationColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ProduitCaisse, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ProduitCaisse, String> produitCaisseStringCellDataFeatures) {
                return new SimpleStringProperty(produitCaisseStringCellDataFeatures.getValue().getProduit().getDesignation());

            }
        });
        prixUniteColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ProduitCaisse, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<ProduitCaisse, Integer> produitCaisseStringCellDataFeatures) {
                return new SimpleIntegerProperty(produitCaisseStringCellDataFeatures.getValue().getProduit().getPrixVenteUnite());

            }
        });

        qtyColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ProduitCaisse, Float>, ObservableValue>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<ProduitCaisse, Float> produitCaisseFloatCellDataFeatures) {
                DecimalFormat df = new DecimalFormat();
                df.setMaximumFractionDigits(2);
                return new SimpleStringProperty(df.format(produitCaisseFloatCellDataFeatures.getValue().getQty()));
            }
        });
        prixTotaleColumn.setCellValueFactory(new PropertyValueFactory<ProduitCaisse, Integer>("prixTotale"));

        */
    }


    private void setupTable() {
        /*
        ProduitsTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        ProduitsTable.getColumns().addAll(ordreColumn, designationColumn, prixUniteColumn, qtyColumn, prixTotaleColumn);
        setupValueFactories();
        divideTableWidthOnColumns();

        ObservableList<ProduitCaisse> list = (ObservableList<ProduitCaisse>) caisses[0].getUserData();
        ProduitsTable.setItems(list);


        currentCaisseIndex.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                ObservableList<ProduitCaisse> list = (ObservableList<ProduitCaisse>) caisses[t1.intValue()].getUserData();
                ProduitsTable.setItems(list);
            }
        });



        ProduitsTable.selectionModelProperty().addListener(new ChangeListener<TableView.TableViewSelectionModel<ProduitCaisse>>() {
            @Override
            public void changed(ObservableValue<? extends TableView.TableViewSelectionModel<ProduitCaisse>> observableValue, TableView.TableViewSelectionModel<ProduitCaisse> produitCaisseTableViewSelectionModel, TableView.TableViewSelectionModel<ProduitCaisse> t1) {

               var selectedItem = t1.getSelectedItem();
                QtyRestTF.setText(Float.valueOf(selectedItem.getProduit().getQtyTotale() - selectedItem.getQty()).toString());
            }
        });



         */
    }





    @Override
    public void initialize(URL location, ResourceBundle resources) {



        /*
        setupTable();

        CurrentTimeLabel();


        setupTotaleTF();
        */


    }


}
