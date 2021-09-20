package ru.netology.graphics.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.net.URL;

public class Converter implements TextGraphicsConverter {
    private int width;
    private int height;
    private Schema schema = new Schema();
    private final Double DOUBLE_EMPTY_VALUE = 0.0;
    private Double maxRatio = DOUBLE_EMPTY_VALUE;
    private final double NUMBER_TO_CORRECT_COMPARING = 1.0000001;

    @Override
    public String convert(String url) throws IOException, BadImageSizeException {
        BufferedImage img = ImageIO.read(new URL(url));

        int imgWidth = img.getWidth();
        int imgHeight = img.getHeight();
        checkRatio(imgWidth, imgHeight);

        int newWidth = getNewWidth(imgWidth);
        int newHeight = getNewHeight(imgHeight, newWidth, imgWidth);

        /**
         * Получаем ссылку на новую картинку, которая представляет собой суженную старую.
         */
        Image scaledImage = img.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_SMOOTH);
        /**
         *  Создадим новую пустую картинку нужных размеров, заранее указав последним
         *  параметром чёрно-белую цветовую палитру:
         */
        BufferedImage bwImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY);

        Graphics2D graphics = bwImg.createGraphics();

        graphics.drawImage(scaledImage, 0, 0, null);

        WritableRaster bwRaster = bwImg.getRaster();

        int[] pixelStorage = new int[3];
        StringBuilder imageString = new StringBuilder();
        for (int w = 0; w < newWidth; w++) {
            for (int h = 0; h < newHeight; h++) {
                int color = bwRaster.getPixel(w, h, pixelStorage)[0];
                char c = schema.convert(color);
                /**
                 * Для того чтобы изображение не было слишком узким, каждый пиксель дублируется
                 */
                imageString.append(c).append(c);
            }
            imageString.append("\n");
        }
        return imageString.toString();
    }

    @Override
    public void setMaxWidth(int width) {
        this.width = width;
    }

    @Override
    public void setMaxHeight(int height) {
        this.height = height;
    }

    @Override
    public void setMaxRatio(double maxRatio) {
        this.maxRatio = maxRatio;
    }

    @Override
    public void setTextColorSchema(TextColorSchema schema) {
        this.schema = (Schema) schema;
    }

    private void checkRatio(int width, int height) throws BadImageSizeException {
        double initRatio = maxRatio.doubleValue();
        double newRatio = width / height;
        if (initRatio != DOUBLE_EMPTY_VALUE && newRatio > initRatio) {
            throw new BadImageSizeException(newRatio, initRatio);
        }
    }

    private int getNewWidth(int width) {
        /**
         если задана ширина, то проверяем больше ли ширина полученной картинки заданной ширины и возвращаем заданную,
         в противном случае возвращаем ширину картинки
         */
        float widthFloat = width;
        float initWidthFloat = this.width;
        float ratio = widthFloat / initWidthFloat;
        return this.width != 0 && ratio > NUMBER_TO_CORRECT_COMPARING ? this.width : width;
    }

    private int getNewHeight(int height, int newWidth, int oldWidth) {
        /**
         возвращаем высоту полученной картинки, измененную в соотношении новой ширины к старой
         */
        float newWidthFloat = newWidth;
        float oldWidthFloat = oldWidth;
        float ratio = newWidthFloat / oldWidthFloat;
        return Math.round(height * ratio);
    }
}
