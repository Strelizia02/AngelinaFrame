package top.angelinaBot.model;

import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 文本生成图片类
 */
public class TextLine {
    //文本内容
    private final List<List<Object>> text = new ArrayList<>();
    //单行内容
    private List<Object> line = new ArrayList<>();
    //列数
    private int width = 0;
    //行数
    private int height = 0;
    //画图指针
    private int pointers;
    //最长允许多少列
    private final int maxWidth;

    /**
     * 默认最长允许20个字符
     */
    public TextLine() {
        this.maxWidth = 20;
    }

    /**
     * @param maxWidth 最长允许maxWidth个字符
     */
    public TextLine(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    /**
     * 为内容增加一个图片，图片可以超出最长字符限制
     *
     * @param image 图片
     */
    public void addImage(BufferedImage image) {
        pointers += 3;
        addSpace();
        line.add(image);
        addSpace();
    }

    /**
     * 为内容增加一行字符串，字符串若超出最长限制，截断换行并进行两个空格的缩进
     *
     * @param s 字符串
     */
    public void addString(String s) {
        if (pointers > 20) {
            nextLine();
            addString(s);
        } else if (s.length() + pointers > 20) {
            int splitPointer = 20 - pointers;
            line.add(s.substring(0, splitPointer));
            nextLine();
            addSpace(2);
            addString(s.substring(splitPointer));
            pointers = 20;
        } else {
            pointers += s.length();
            line.add(s);
        }
    }

    /**
     * 为内容添加空格，若添加空格后超出最长限制，则仅添加一个
     *
     * @param spaceNum 空格数
     */
    public void addSpace(int spaceNum) {
        if (pointers + spaceNum > 20) {
            spaceNum = 1;
        }
        line.add(spaceNum);
        pointers += spaceNum;
    }

    /**
     * 默认只增加一个空格
     */
    public void addSpace() {
        addSpace(1);
    }

    /**
     * 换行
     */
    public void nextLine() {
        if (pointers > width) {
            width = pointers;
        }
        pointers = 0;
        height++;
        text.add(line);
        line = new ArrayList<>();
    }

    /**
     * 添加单独一行的居中字符串，居中字符串必须单独一行
     *
     * @param s 字符串
     */
    public void addCenterStringLine(String s) {
        StringBuilder sb = new StringBuilder(s);
        if (pointers != 0) {
            nextLine();
        }
        pointers = maxWidth;
        line.add(sb);
        nextLine();
    }

    /**
     * 将TextLine生成一个图片
     *
     * @param size 单个字符大小
     * @return 生成图片
     */
    public BufferedImage drawImage(int size) {
        if (!line.isEmpty()) {
            if (pointers > width) {
                width = pointers;
            }
            height++;
            text.add(line);
        }
        BufferedImage image = new BufferedImage((width + 2) * size, (height + 2) * size, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();

        graphics.setColor(new Color(208, 145, 122));
        graphics.fillRect(0, 0, (width + 2) * size, (height + 2) * size);
        graphics.setColor(new Color(160, 130, 115));
        graphics.fillRect(size / 2, size / 2, (width + 1) * size, (height + 1) * size);
        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font("宋体", Font.BOLD, size));

        int x = size / 2;
        int y = size / 2;
        for (List<Object> line : text) {
            for (Object obj : line) {
                if (obj instanceof String) {
                    String str = (String) obj;
                    graphics.drawString(str, x, y + size);
                    x += str.length() * size;
                }
                if (obj instanceof StringBuilder) {
                    String str = ((StringBuilder) obj).toString();
                    graphics.drawString(str, (width - str.length()) / 2 * size, y + size);
                    x = size / 2;
                }
                if (obj instanceof BufferedImage) {
                    BufferedImage bf = (BufferedImage) obj;
                    graphics.drawImage(bf, x, y, size, size, null);
                    x += size;
                }
                if (obj instanceof Integer) {
                    x += (int) obj * size;
                }
            }
            x = size / 2;
            y += size;
        }

        try {
            InputStream is = new ClassPathResource("/pic/logo.jpg").getInputStream();
            graphics.drawImage(ImageIO.read(is), image.getWidth() - size / 2, 0, size / 2, size / 2, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        graphics.dispose();
        return image;
    }

    /**
     * 默认字符大小50
     */
    public BufferedImage drawImage() {
        return drawImage(50);
    }
}
