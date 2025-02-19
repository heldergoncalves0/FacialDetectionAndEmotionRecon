package com.FacialEmotionRecon.gui;

import com.FacialEmotionRecon.recognition.EmotionDetectorService;
import com.FacialEmotionRecon.recognition.FaceRecognizerService;
import com.FacialEmotionRecon.recognition.WebcamFaceRecognition;
import com.FacialEmotionRecon.utils.Utils;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.opencv.core.Mat;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.Map;

public class MainController {

    @FXML
    private Button uploadButton;

    @FXML
    private Button startWebcamButton;

    @FXML
    private ImageView originalImageView;

    @FXML
    private ImageView processedImageView;

    @FXML
    private TextArea faceRecognitionResults;

    @FXML
    private TextArea emotionDetectionResults;


    private FaceRecognizerService faceRecognizerService;
    private WebcamFaceRecognition webcamFaceRecognition;

    @FXML
    private void initialize() {
        faceRecognizerService = new FaceRecognizerService();
        webcamFaceRecognition = new WebcamFaceRecognition(faceRecognizerService, processedImageView);

        // Iniciar a captura da webcam
        startWebcamButton.setOnAction(event -> webcamFaceRecognition.startWebcam());

        // Configurar ação do botão de “upload”
        uploadButton.setOnAction(event -> handleUploadImage());
    }

    private void handleUploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Imagem");
        // Configurar filtros de extensão, se necessário
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg", "*.bmp")
        );
        File selectedFile = fileChooser.showOpenDialog(uploadButton.getScene().getWindow());

        if (selectedFile != null) {
            try {
                System.out.println("selectedFile: " + selectedFile.getAbsolutePath());
                // Carregar a imagem original
                Mat originalMat = Utils.loadImage(selectedFile.getAbsolutePath());
                if (originalMat.empty()) {
                    showError("Erro ao carregar a imagem. Verifique o formato e o conteúdo do arquivo.");
                    return;
                }

                // Exibir a imagem original na interface
                Image originalImage = Utils.mat2Image(originalMat);
                originalImageView.setImage(originalImage);

                // Limpar resultados anteriores
                faceRecognitionResults.clear();
                emotionDetectionResults.clear();

                // Realizar reconhecimento facial e deteção de emoções em uma thread separada
                performRecognitionAndDetection(originalMat, selectedFile.getAbsolutePath());

            } catch (Exception ex) {
                showError("Erro ao processar a imagem: " + ex.getMessage());
            }
        }
    }

    private void performRecognitionAndDetection(Mat processedMat, String imagePath) {

        //Deteção de emoções
        EmotionDetectorService emotionDetectorService = new EmotionDetectorService(imagePath);
        String emotionResults = emotionDetectorService.getDetectedEmotion().split("\n")[2];
            System.out.println("Resultados de deteção de emoções: " + emotionResults);

        Map<Integer, Double> recognitionResults = faceRecognizerService.recognizeFaces(processedMat);
        System.out.println("Resultados de reconhecimento facial: " + recognitionResults);

        // Atualizar GUI com os resultados de reconhecimento facial
        StringBuilder sb = new StringBuilder();
        recognitionResults.forEach((id, confidence) -> {
            if(id == 0)
                sb.append("ID: ").append("Carlos").append(", Confiança: ").append(String.format("%.2f", confidence)).append("\n");
            else if(id == 1)
   		        sb.append("ID: ").append("Helder").append(", Confiança: ").append(String.format("%.2f", confidence)).append("\n");
            else if(id == 2)
                sb.append("ID: ").append("Fernando").append(", Confiança: ").append(String.format("%.2f", confidence)).append("\n");

        });
        faceRecognitionResults.setText(sb.toString());
        emotionDetectionResults.setText(emotionResults);
        

        Image processedImage = Utils.mat2Image(processedMat);
        processedImageView.setImage(processedImage);
    }

    /**
     * Exibe uma mensagem de erro na “interface” gráfica.
     *
     * @param message Mensagem de erro a ser exibida
     */
    private void showError(String message) {
        Platform.runLater(() -> {
            // Exibir uma caixa de diálogo de erro
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Ocorreu um erro");
            alert.setContentText(message);
            alert.showAndWait();

            // Limpar os resultados
            faceRecognitionResults.clear();
            emotionDetectionResults.clear();
        });
    }
}
