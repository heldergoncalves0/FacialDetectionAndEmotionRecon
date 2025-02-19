package com.FacialEmotionRecon.image;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class ImageProcessor {

    /**
     * Aplica pixelização à imagem.
     *
     * @param image Matriz de imagem original
     * @return Matriz de imagem pixelizada
     */
    public Mat applyPixelation(Mat image) {
        int pixelSize = 10; // Tamanho do pixel
        for (int y = 0; y < image.rows(); y += pixelSize) {
            for (int x = 0; x < image.cols(); x += pixelSize) {
                int endX = Math.min(x + pixelSize, image.cols());
                int endY = Math.min(y + pixelSize, image.rows());

                // Obter a média de cor do bloco
                Mat block = image.submat(y, endY, x, endX);
                org.opencv.core.Scalar color = Core.mean(block);
                Imgproc.rectangle(image, new org.opencv.core.Point(x, y), new org.opencv.core.Point(endX, endY),
                        color, -1);
            }
        }
        return image;
    }

    /**
     * Altera as cores da imagem.
     *
     * @param image Matriz de imagem original
     * @return Matriz de imagem com cores alteradas
     */
    public Mat alterColors(Mat image) {
        // Exemplo: Inverter as cores
        Mat inverted = new Mat();
        Core.bitwise_not(image, inverted);
        return inverted;
    }

    /**
     * Adiciona ruído aleatório à imagem.
     *
     * @param image Matriz de imagem original
     * @return Matriz de imagem com ruído
     */
    public Mat addNoise(Mat image) {
        Mat noise = new Mat(image.size(), image.type());
        Core.randn(noise, 0, 50); // Média 0, desvio padrão 50
        Mat noisyImage = new Mat();
        Core.add(image, noise, noisyImage);
        return noisyImage;
    }

    /**
     * Converte a imagem para ter mais brilho.
     *
     * @param image Matriz de imagem original
     * @return Matriz de imagem com mais brilho
     */
    public Mat transformImage(Mat image) {
        Mat CoreImage = new Mat(image.size(), image.type());
        Core.absdiff(image, CoreImage, CoreImage);
        Mat grayImage = new Mat();
        Core.add(image, CoreImage, grayImage);
        return grayImage;
    }
}
