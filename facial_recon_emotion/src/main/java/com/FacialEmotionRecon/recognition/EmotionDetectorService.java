package com.FacialEmotionRecon.recognition;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class EmotionDetectorService {
    private String detectedEmotion;  // Stores the detected emotion

    public EmotionDetectorService(String imagePath) {
        try {
            // Absolute path to the Python script
            String pythonScriptPath = "/Users/heldergoncalves/Desktop/facial_emotion/facial_recon_emotion/src/main/resources/emotion_model/main.py";

            // Use Python 3 (adjust the path if needed)
            ProcessBuilder processBuilder = new ProcessBuilder("python3", pythonScriptPath, imagePath);

            // Ensure the script runs in the correct directory
            processBuilder.directory(new File("/Users/heldergoncalves/Desktop/facial_emotion/facial_recon_emotion/src/main/resources/emotion_model/"));

            // Start the process
            Process process = processBuilder.start();

            // Capture output
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            // Read stdout and store result
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = stdInput.readLine()) != null) {
                output.append(line).append("\n"); // Append to string
            }

            // Read stderr (error output)
            while ((line = stdError.readLine()) != null) {
                System.err.println("Error: " + line);
            }

            // Wait for the process to finish
            process.waitFor();

            // Store the detected emotion
            detectedEmotion = output.toString().trim(); // Remove trailing newlines
        } catch (Exception e) {
            e.printStackTrace();
            detectedEmotion = "Error: Unable to detect emotion.";
        }
    }

    // Getter method to retrieve the detected emotion
    public String getDetectedEmotion() {
        return detectedEmotion;
    }

    public static void main(String[] args) {
        String imagePath = "/Users/heldergoncalves/Desktop/facial_emotion/facial_recon_emotion/src/main/resources/train/dataset/person1/fernando_feliz.jpeg";
        EmotionDetectorService detector = new EmotionDetectorService(imagePath);

        // Now, properly printing the detected emotion
        System.out.println("Detected Emotion: " + detector.getDetectedEmotion());
    }
}
