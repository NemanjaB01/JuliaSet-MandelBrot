package at.tugraz.oop2.shared;

import at.tugraz.oop2.shared.exception.InvalidDepthException;
import lombok.Getter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class SimpleImage implements Serializable {
    @Getter
    private short[] data;
    @Getter
    private int depth;
    @Getter
    private int width;
    @Getter
    private int height;

    public SimpleImage(int width, int height) {
        this(3, width, height);
    }

    public SimpleImage(List<SimpleImage> interleaved) throws Exception {
        this(3, 0, 0);
        if (interleaved.size() > 0) {
            this.width = interleaved.get(0).getWidth();
            this.height = interleaved.stream().mapToInt(SimpleImage::getHeight).sum();
            this.depth = interleaved.get(0).getDepth();
            this.data = new short[width * height * depth];
            for (int i = 0; i < interleaved.size(); i++) {
                SimpleImage subImage = interleaved.get(i);

                for (int j = 0; j < subImage.height; j++) {
                    for (int k = 0; k < subImage.width; k++) {
                        int y = i + interleaved.size() * j;
                        if (y < height) {
                            setPixel(k, y, subImage.getPixel(k, j));
                        }
                    }
                }
            }

        }
    }

    public SimpleImage(int depth, int width, int height) {
        this.depth = depth;
        this.width = width;
        this.height = height;
        this.data = new short[width * height * depth];
    }

    public byte[] getByteData() {
        byte[] arr = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            arr[i] = (byte) data[i];
        }
        return arr;
    }

    public void setPixel(int x, int y, short[] data) throws InvalidDepthException {
        if (data.length != depth) {
            throw new InvalidDepthException();
        }
        for (int i = 0; i < depth; i++) {
            this.data[width * y * depth + x * depth + i] = data[i];
        }
    }

    public short[] getPixel(int x, int y) {
        short[] pixel = new short[depth];
        for (int i = 0; i < depth; i++) {
            pixel[i] = this.data[width * y * depth + x * depth + i];
        }
        return pixel;
    }
}
