package com.FacialEmotionRecon.recognition;

import com.FacialEmotionRecon.utils.Utils;
import javafx.scene.image.Image;
import org.opencv.core.*;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FaceRecognizerService {

    private static LBPHFaceRecognizer recognizer = null;
    private final CascadeClassifier faceDetector;

    public FaceRecognizerService() {

        System.setProperty("java.library.path", "/usr/local/share/java/opencv4");
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        recognizer = LBPHFaceRecognizer.create();

        // Carregar o classificador de rostos
        URL faceClassifierPath = FaceRecognizerService.class.getClassLoader().getResource("faces_classifier/haarcascade_frontalface_default.xml");
        assert faceClassifierPath != null;
        faceDetector = new CascadeClassifier(faceClassifierPath.getPath());

        FaceRecognizerService.trainModel(faceDetector);

        // Carregar o modelo treinado
        URL lbphModelPath = FaceRecognizerService.class.getClassLoader().getResource("gui/lbph_model.xml");
        assert lbphModelPath != null;
        recognizer.read(lbphModelPath.getPath()); // Atualize o caminho para o seu modelo
    }

    public static void trainModel(CascadeClassifier faceDetector) {
        Integer label = 0;
        URL dataPath = FaceRecognizerService.class.getClassLoader().getResource("train/dataset");

        List<Mat> images = new ArrayList<>();
        List<Integer> labels = new ArrayList<>();

        assert dataPath != null;
        File datasetDir = new File(dataPath.getPath());

        File[] files = datasetDir.listFiles();

        assert files != null;
        for (File file : files) {
            if (file.isDirectory()) {
                File[] imageFiles = file.listFiles();
                assert imageFiles != null;

                // Ler imagens e adicionar ao dataset
                for (File imageFile : imageFiles) {
                    // Ler imagem e converter para grayscale
                    Mat image = Imgcodecs.imread(imageFile.getAbsolutePath());
                    System.out.println(imageFile.getAbsolutePath());
                    Mat grayImage = new Mat();
                    Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);

                    // Detetar rostos na imagem
                    MatOfRect faces = new MatOfRect();
                    faceDetector.detectMultiScale(grayImage, faces);
                    for (Rect face : faces.toArray()) {
                        Mat faceROI = new Mat(grayImage, face);

                        Mat resizedFace = new Mat();
                        Imgproc.resize(faceROI, resizedFace, new Size(face.width, face.height));
                        Imgproc.equalizeHist(resizedFace, resizedFace);
                        images.add(resizedFace);
                        labels.add(label);
                        System.out.println("Adicionado rótulo: " + label + " para a imagem: " + imageFile.getName());
                    }
                }
                label++;
            }
        }

        // Converter lista de rótulos para Mat
        Mat labelsMat = new Mat(labels.size(), 1, CvType.CV_32SC1);
        for (int i = 0; i < labels.size(); i++) {
            labelsMat.put(i, 0, labels.get(i));
        }

        System.out.println(("Training model..."));
        recognizer.train(images, labelsMat);

        // Salvar o modelo treinado
        URL modelPath = FaceRecognizerService.class.getClassLoader().getResource("gui");
        assert modelPath != null;
        String modelPathStr = modelPath.getPath() + "/lbph_model.xml";
        recognizer.save(modelPathStr);
    }

    /**
     * Reconhece rostos na imagem e retorna um mapa de identificação com grau de confiança.
     *
     * @param image Matriz de imagem contendo o rosto
     * @return Mapa com ID do membro e grau de confiança
     */
    public Map<Integer, Double> recognizeFaces(Mat image) {
        Map<Integer, Double> recognitionResults = new HashMap<>();

        // Detetar rostos na imagem
        //Mat grayImage = new Mat();
        if (image.channels() > 1) {
            Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2GRAY); // Convert to grayscale
        } else {
            // If the image is already grayscale, just use it
            image = image.clone();
        }
        //Imgproc.resize(image, image, new Size(100, 100));
        Rect[] facesArray = detectFaces(image);

        for (Rect face : facesArray) {
            Mat faceROI = new Mat(image, face);
            int[] label = new int[1];
            double[] confidence = new double[1];
            Imgproc.resize(faceROI, faceROI, new Size(face.width, face.height));
            Imgproc.equalizeHist(faceROI, faceROI);
            recognizer.predict(faceROI, label, confidence);
            Imgproc.rectangle(image, face.tl(), face.br(), new Scalar(0, 255, 0), 6);
            recognitionResults.put(label[0], confidence[0]);
        }
        return recognitionResults;
    }

    /**
     * Deteta rostos numa imagem em grayscale.
     *
     * @param image Imagem em grayscale
     * @return Array de retângulos representando as faces detetadas
     */
    Rect[] detectFaces(Mat image) {
        MatOfRect faces = new MatOfRect();
        faceDetector.detectMultiScale(image, faces);
        return faces.toArray();
    }
}
