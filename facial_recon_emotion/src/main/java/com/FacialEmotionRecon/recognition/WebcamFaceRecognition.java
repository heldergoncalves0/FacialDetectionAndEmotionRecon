package com.FacialEmotionRecon.recognition;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.util.Map;

public class WebcamFaceRecognition {
    public FaceRecognizerService faceRecognizer; // Assuming you have a face recognizer (LBPH or similar)
    private VideoCapture videoCapture;// For webcam capture

    @FXML
    private ImageView processedImageView;

    @FXML
    private TextArea faceRecognitionResults;

    public WebcamFaceRecognition(FaceRecognizerService faceRecognizer, ImageView processedImageView) {
        this.faceRecognizer = faceRecognizer;
        this.processedImageView = processedImageView;
        this.videoCapture = new VideoCapture(0); // 0 for default webcam
    }

    public void startWebcam() {
        if (!videoCapture.isOpened()) {
            System.out.println("Error: Could not open webcam.");
            return;
        }

        videoCapture.set(3, 1280);
        videoCapture.set(4, 720);

        // Run the webcam processing in a new thread
        new Thread(() -> {
            Mat frame = new Mat(); // To store the current frame
            while (true) {
                videoCapture.read(frame);
                if (frame.empty()) {
                    System.out.println("Error: Could not capture frame.");
                    break;
                }
                // Convert the frame to grayscale for face detection
                Mat grayFrame = new Mat();
                Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);

                // Detect faces in the grayscale image
                Rect[] facesArray = faceRecognizer.detectFaces(grayFrame);

                // For each detected face, draw a rectangle around it and run face recognition
                for (Rect face : facesArray) {
                    // Draw a rectangle around the face on the original frame
                    Imgproc.rectangle(frame, face.tl(), face.br(), new Scalar(0, 255, 0), 2); // Green rectangle, thickness 2

                    // Optional: Face recognition (Assuming you have a function for this)
                    Mat faceROI = new Mat(grayFrame, face);
                    Map<Integer, Double> recognitionResults = faceRecognizer.recognizeFaces(faceROI);
                    StringBuilder sb = new StringBuilder();
                    recognitionResults.forEach((id, confidence) -> {
                        if(id == 0)
                            sb.append("ID: ").append("Carlos").append(", Confiança: ").append(String.format("%.2f", confidence)).append("\n");
                        else if(id == 2)
                            sb.append("ID: ").append("Fernando").append(", Confiança: ").append(String.format("%.2f", confidence)).append("\n");
                        else if(id == 1)
                            sb.append("ID: ").append("Helder").append(", Confiança: ").append(String.format("%.2f", confidence)).append("\n");
                    });

                    if (faceRecognitionResults != null) {
                        StringBuilder sb2 = new StringBuilder();
                        recognitionResults.forEach((id, confidence) -> {
                            sb2.append("ID: ").append(id).append(", Confiança: ").append(String.format("%.2f", confidence)).append("\n");
                        });
                        faceRecognitionResults.setText(sb.toString());
                    }

                    // Optionally, you could also display the recognition results within the frame
                    Imgproc.putText(frame, sb.toString(), face.tl(), Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 255, 0), 2);
                }
                // Display the frame with the rectangles
                HighGui.imshow("Webcam - Face Recognition", frame);

                // Break the loop if the user presses 'ESC' or closes the window
                if (HighGui.waitKey(1) == 27) {  // 27 is the ASCII value for ESC
                    break;
                }
            }
            videoCapture.release();
            HighGui.destroyAllWindows();
        }).start();
    }

    public void stopWebcam() {
        if (videoCapture.isOpened()) {
            videoCapture.release(); // Stop the video capture
        }
    }
}
