package com.FacialEmotionRecon;

import org.opencv.core.*;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgcodecs.Imgcodecs;

public class WebcamTest {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Try opening the webcam without GStreamer
        VideoCapture capture = new VideoCapture(0); // 0 is typically the default webcam
        if (!capture.isOpened()) {
            System.out.println("Error: Could not open camera.");
            return;
        }

        Mat frame = new Mat();
        while (true) {
            capture.read(frame);
            if (frame.empty()) {
                System.out.println("Error: Could not capture frame.");
                break;
            }
            // Save the frame as an image
            Imgcodecs.imwrite("frame.jpg", frame);  // You can check the frame file written
            // Optionally: Display the frame in a window
            HighGui.imshow("Frame", frame);
            HighGui.waitKey(1); // Wait for 1 ms to render the window
        }
    }
}
