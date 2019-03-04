package com.example.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class QRUtils {
    private static final int BLACK = 0xFF000000;
    private static final int WHITE = 0xFFFFFFFF;

    /**
     * 把生成的二维码存入到图片中
     *
     * @param matrix zxing包下的二维码类
     * @return
     */
    public static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
            }
        }
        return image;
    }

    /**
     * 生成二维码并写入文件
     *
     * @param content 扫描二维码的内容
     * @param format  图片格式 jpg
     * @param qrCodeSize  图片大小
     * @param file    文件
     * @throws Exception
     */
    public static void writeToFile(String content, String format, int qrCodeSize, File file)
            throws Exception {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        @SuppressWarnings("rawtypes")
        Map hints = new HashMap();
        //设置UTF-8， 防止中文乱码
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        //设置二维码四周白色区域的大小
        hints.put(EncodeHintType.MARGIN, 1);
        //设置二维码的容错性
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        //画二维码
        BitMatrix bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, qrCodeSize, qrCodeSize, hints);
        BufferedImage image = toBufferedImage(bitMatrix);
        if (!ImageIO.write(image, format, file)) {
            throw new IOException("Could not write an image of format " + format + " to " + file);
        }
    }

    /**
     * 给二维码图片加上文字
     *
     * @param pressText 文字
     * @param qrFile    二维码文件
     * @param fontStyle
     * @param color
     * @param qrCodeSize
     * @param fontSize
     */
    public static void pressText(String pressText, File qrFile, int fontStyle, Color color, int qrCodeSize, int fontSize) throws Exception {
        pressText = new String(pressText.getBytes(), "utf-8");
        Image src = ImageIO.read(qrFile);
        int imageW = src.getWidth(null);
        int imageH = src.getHeight(null);
        BufferedImage image = new BufferedImage(imageW, imageH, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.createGraphics();
        g.drawImage(src, 0, 0, imageW, imageH, null);
        //设置画笔的颜色
        g.setColor(color);
        //设置字体
        Font font = new Font("宋体", fontStyle, fontSize);
        FontMetrics metrics = g.getFontMetrics(font);
        //文字在图片中的坐标 这里设置在中间
        int startX = (qrCodeSize - metrics.stringWidth(pressText)) / 2;
        int startY = qrCodeSize / 2;
        g.setFont(font);
        g.drawString(pressText, startX, startY);
        g.dispose();
        FileOutputStream out = new FileOutputStream(qrFile);
        ImageIO.write(image, "JPEG", out);
        out.close();
        System.out.println("image press success");
    }

    /**
     * 测试代码
     *
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        File file = new File("d:\\qrcode.jpg");
        int qrCodeSize = 400;
        writeToFile("http://hwocr.implus100.com/appPicUpload.jsp", "jpeg", qrCodeSize,
                file);
        pressText("OCR", file, Font.BOLD, Color.RED, qrCodeSize, 40);

    }
}
