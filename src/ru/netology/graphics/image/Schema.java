package ru.netology.graphics.image;

public class Schema implements TextColorSchema {
    private char[] initSchema = new char[]{
            '▇', '●', '◉', '◍', '◎', '○', '☉', '◌', '-'
    };
    /**
     * получение среднего промежутка для каждого символа из массива с добавлением единицы,
     * чтобы забрать оставшиеся элементы
     */
    private int charCoeffToGet = (255 / initSchema.length) + 1;

    @Override
    public char convert(int color) {
        return initSchema[color / charCoeffToGet];
    }

    public void setInitSchema(char[] initSchema) {
        this.initSchema = initSchema;
    }
}
