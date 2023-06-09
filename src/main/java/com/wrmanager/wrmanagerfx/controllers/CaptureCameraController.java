package com.wrmanager.wrmanagerfx.controllers;

import com.wrmanager.wrmanagerfx.Constants;
import com.wrmanager.wrmanagerfx.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;


import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;


public class CaptureCameraController implements Initializable {

    private static final String CAMERA_IP = Main.CAMERA_IP;
    public Mat frame;
    public Image image;
    public static Thread thread;
    public static Object lock = new Object();
    public VideoCapture video;
    ImageProcessing imageProcessing = new ImageProcessing();
    private Dialog<ButtonType> dialog;

    @FXML
    private ImageView imageView;
    private String scriptPath;


    @FXML
    void CameraButtonOnAction(ActionEvent event) throws InterruptedException {
        thread.stop();
        thread.stop();
        imageView.setImage(image);
        ImageProcessing.saveCapturedImage(frame);
        dialog.setResult(ButtonType.YES);
        dialog.close();
        thread.stop();

    }


    @FXML
    void CloseButtonOnAction(ActionEvent event) {
        thread.stop();
        thread.stop();
        dialog.setResult(ButtonType.CLOSE);
        dialog.close();
        thread.stop();
        thread.stop();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        thread = new Thread(() -> {
            frame=new Mat();
            while (true) {
                synchronized (lock) {
                    video = new VideoCapture(CAMERA_IP);
                    video.read(frame);
                    Mat processedFrame = imageProcessing.processFrame(frame);
                    image = imageProcessing.matToImage(processedFrame);
                    imageView.setImage(image);
                }
            }
        });

        thread.start();


    }


    public void setDialog(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }
}
