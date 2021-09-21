package ru.netology.graphics;

import ru.netology.graphics.image.Converter;
import ru.netology.graphics.image.TextGraphicsConverter;

import java.io.File;
import java.io.PrintWriter;

public class Main {
    public static void main(String[] args) throws Exception {
        TextGraphicsConverter converter = new Converter();
        PrintWriter fileWriter = new PrintWriter(new File("converted-image.txt"));
        converter.setMaxWidth(200);
        converter.setMaxHeight(300);
        fileWriter.write(converter.convert("https://i.ibb.co/pv8Mzhc/GNs-L5p-K-jb-A.jpg"));

        fileWriter.close();
    }
}
