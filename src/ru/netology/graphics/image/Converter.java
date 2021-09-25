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
    private final double NUMBER_TO_CORRECT_COMPARING = 0.0000001;

    @Override
    public String convert(String url) throws IOException, BadImageSizeException {
        BufferedImage img = ImageIO.read(new URL(url));

        int imgWidth = img.getWidth();
        int imgHeight = img.getHeight();
        checkRatio(imgWidth, imgHeight);

        boolean isWidthBigger = isBigger(imgWidth, imgHeight);

        int newWidth;
        int newHeight;
        if (isWidthBigger) {
            newWidth = getMainValue(imgWidth, this.width);
            newHeight = getAdictedValue(this.height, newWidth, this.width);
        } else {
            newHeight = getMainValue(imgHeight, this.height);
            newWidth = getAdictedValue(this.width, newHeight, this.height);
        }

        /**
         * Получаем ссылку на новую картинку, которая представляет собой суженную
         * старую.
         */
        Image scaledImage = img.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_SMOOTH);
        /**
         * Создадим новую пустую картинку нужных размеров, заранее указав последним
         * параметром чёрно-белую цветовую палитру:
         */
        BufferedImage bwImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY);

        Graphics2D graphics = bwImg.createGraphics();

        graphics.drawImage(scaledImage, 0, 0, null);

        WritableRaster bwRaster = bwImg.getRaster();

        int[] pixelStorage = new int[3];
        StringBuilder imageString = new StringBuilder();
        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {
                int color = bwRaster.getPixel(x, y, pixelStorage)[0];
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
        float widthFloat = width;
        float heightFloat = height;
        double initRatio = maxRatio.doubleValue();
        double newRatio = widthFloat / heightFloat;
        if (initRatio != DOUBLE_EMPTY_VALUE && newRatio - initRatio > NUMBER_TO_CORRECT_COMPARING) {
            throw new BadImageSizeException(newRatio, initRatio);
        }
    }

    private boolean isBigger(int a, int b) {
        /**
         * определяем какая из сторон больше, в случае равного значения за "ведущую"
         * сторону принимаем ширину
         */
        float aFloat = a;
        float bFloat = b;
        return Math.abs(aFloat - bFloat) < NUMBER_TO_CORRECT_COMPARING || a - b > 0;
    }

    private int getMainValue(int value, int initValue) {
         /**
         * если задано значение, то проверяем больше ли данное значение полученного от картинки
         * и возвращаем заданное, в противном случае возвращаем знчение, полученное от
         * картинки
         */
        float valueFloat = value;
        float initValueFloat = initValue;
        float ratio = valueFloat / initValueFloat;
        return initValue != 0 && ratio > (NUMBER_TO_CORRECT_COMPARING + 1) ? initValue : value;
    }

    private int getAdictedValue(int addictedValue, int newMainValue, int oldMainValue) {
        /**
         * возвращаем значение, полученное от картинки, измененное в соотношении нового "ведущего"
         * знасения к старому
         */
        float newMainValueFloat = newMainValue;
        float oldMainValueFloat = oldMainValue;
        float ratio = newMainValueFloat / oldMainValueFloat;
        return Math.round(addictedValue * ratio);
    }
}
