package com.FacialEmotionRecon.utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class Utils {

    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    /**
     * Carrega uma imagem a partir de um caminho de arquivo.
     *
     * @param path Caminho do arquivo da imagem
     * @return Matriz da imagem carregada
     */
    public static Mat loadImage(String path) {
        return Imgcodecs.imread(path);
    }

    /**
     * Salva uma imagem em um caminho de arquivo.
     *
     * @param path Caminho do arquivo para salvar a imagem
     * @param image Matriz da imagem a ser salva
     */
    public static void saveImage(String path, Mat image) {
        Imgcodecs.imwrite(path, image);
    }

    /**
     * Converte uma Mat do OpenCV para uma Image do JavaFX.
     *
     * @param mat Matriz de imagem do OpenCV
     * @return Image correspondente do JavaFX
     */
    public static Image mat2Image(Mat mat) {
        try {
            BufferedImage bufferedImage = matToBufferedImage(mat);
            return SwingFXUtils.toFXImage(bufferedImage, null);
        } catch (Exception e) {
            logger.error("Error converting Mat to Image: {}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * Converte uma Mat do OpenCV para BufferedImage.
     *
     * @param mat Matriz de imagem do OpenCV
     * @return BufferedImage correspondente
     */
    private static BufferedImage matToBufferedImage(Mat mat) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (mat.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = mat.channels() * mat.cols() * mat.rows();
        byte[] buffer = new byte[bufferSize];
        mat.get(0, 0, buffer); // Preencher o buffer com os pixels da imagem
        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
        final byte[] targetPixels = ((java.awt.image.DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);
        return image;
    }
}
