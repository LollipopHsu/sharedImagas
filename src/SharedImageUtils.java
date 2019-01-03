package com.ecp.mainsite.util;

import com.ecp.core.common.util.LogUtils;

import javax.imageio.*;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;

/**
 * @Auther: Lollipop
 * @Date: 2018/11/19 15:17
 * @Description: 生成朋友圈分享图相关接口
 */
public class SharedImageUtils {
    private SharedImageUtils() {
        throw new IllegalStateException("Utility class");
    }

    /* 要放置的二维码寬度 */
    public static final int QRCODE_WIDTH = 230;
    /* 要放置的二维码長度 */
    public static final int QRCODE_LENGTH = 230;
    /* 要放置的二维码Y位置 往下为大值，往上为小值 */
    public static final int QRCODE_Y = 1070;
    /*要放置的二维码X位置 往下为大值，往上为小值 */
    public static final int QRCODE_X = 740;
    /* 要放置的头像半径 */
    public static final int PROFILE_RADIUS = 80;
    /* 要放置的头像y坐标 */
    public static final int PROFILE_Y = 1056;
    /* 要放置的头像X坐标 */
    public static final int PROFILE_X = 90;
    /* 昵称的Y位置 */
    public static final int FONT_Y = 1110;
    /*昵称的X位置*/
    public static final int FONT_X = 190;
    /* 推广文案的Y位置 */
    public static final int COPYWRITER_Y = 1200;
    /* 推广文案的X位置 */
    public static final int COPYWRITER_X = 150;
    /* 商店图案Y位置 */
    public static final int SHOP_PIC_Y = 70;
    /*商店图案位置*/
    public static final int SHOP_PIC_X = 93;
    /* 商店图案寬度 */
    public static final int SHOP_PIC_WIDTH = 900;
    /* 商店图案長度 */
    public static final int SHOP_PIC_LENGTH = 950;





    /**
     * 裁剪图片
     *
     * @param img          the img
     * @param originWidth  the origin width
     * @param originHeight the origin height
     * @return the buffered image
     * @throws Exception the exception
     */
    public static BufferedImage cutPicture(BufferedImage img, int originWidth, int originHeight) throws IOException {
        int width = img.getWidth();  // 原图的宽度
        int height = img.getHeight();  //原图的高度

        int newImageX = 0; // 要截图的坐标
        int newImageY = 0; // 要截图的坐标
        if (width > originWidth) {
            newImageX = (width - originWidth) / 2;
        }
        if (height > originHeight) {
            newImageY = height - originHeight;
        }
        return cutJPG(img, newImageX, newImageY, originWidth, originHeight);
    }

    /**
     * 图片拉伸
     *
     * @param originalImage the original image
     * @param originWidth   the origin width
     * @param originHeight  the origin height
     * @return the buffered image
     * @throws Exception the exception
     */
    public static BufferedImage zoomPicture(String originalImage, int originWidth, int originHeight) {

        BufferedImage img = null;
        BufferedImage bufferedImage = null;
        try {
            // 原来的图片
            img = ImageIO.read(new File(originalImage));
            int width = img.getWidth();  // 原图的宽度
            int height = img.getHeight();  //原图的高度

            int scaledWidth = width;
            int scaledHeight = height;
            // 如果不是正方形
            if (width == height) {
                // 按照originHeight进行缩放
                scaledWidth = originHeight;
                scaledHeight = originHeight;
            } else {
                if (width > height) {
                    // 按照originHeight进行缩放
                    scaledWidth = (scaledWidth * originHeight) / scaledHeight;
                    scaledHeight = originHeight;
                } else {
                    // 宽高比例
                    int originPercent = (originHeight * 100) / originWidth;
                    int newPercent = (height * 100) / width;
                    if (newPercent >= originPercent) {
                        // 按照originWidth进行缩放
                        scaledWidth = originWidth;
                        scaledHeight = (originHeight * scaledWidth) / scaledWidth;
                    } else {
                        // 按照originHeight进行缩放
                        scaledWidth = (scaledWidth * originHeight) / scaledHeight;
                        scaledHeight = originHeight;
                    }
                }
            }
            Image schedImage = img.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
            // 新的图片
            bufferedImage = new BufferedImage(scaledWidth, scaledHeight, img.getType());
            Graphics2D g = bufferedImage.createGraphics();
            // 绘制
            g.drawImage(schedImage, 0, 0, null);
            g.dispose();
        } catch (IOException e) {
            LogUtils.error(e);
        }

        return bufferedImage;
    }

    /**
     * 进行裁剪操作
     *
     * @param originalImage the original image
     * @param x             the x
     * @param y             the y
     * @param width         the width
     * @param height        the height
     * @return the buffered image
     * @throws IOException the io exception
     */
    public static BufferedImage cutJPG(BufferedImage originalImage, int x, int y, int width, int height) throws IOException {
        Iterator<ImageReader> iterator = ImageIO.getImageReadersByFormatName("jpg");
        ImageReader reader = iterator.next();
        // 转换成字节流
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
       // writeToJPEG(1080,originalImage,0.5f,outputStream);
        ImageIO.write(originalImage, "jpg", outputStream);
        InputStream is = new ByteArrayInputStream(outputStream.toByteArray());
        ImageInputStream iis = ImageIO.createImageInputStream(is);
        reader.setInput(iis, true);
        ImageReadParam param = reader.getDefaultReadParam();
        Rectangle rect = new Rectangle(x, y, width, height);
        param.setSourceRegion(rect);
        return reader.read(0, param);
    }

    /**
     * 合并头像和昵称
     *
     * @param baseLayer
     * @param topLayer
     * @param nickName
     * @param locationX
     * @param locationY
     * @param locationWidth
     * @param locationLength
     * @return
     * @throws IOException
     */
    public static BufferedImage mergePicture(BufferedImage baseLayer,
                                             BufferedImage topLayer,
                                             String nickName,
                                             int locationX,
                                             int locationY,
                                             int locationWidth,
                                             int locationLength) throws IOException {

        int width = baseLayer.getWidth(null); //底图的宽度
        int height = baseLayer.getHeight(null); //底图的高度
        // 按照底图的宽高生成新的图片
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.createGraphics();
        g.drawImage(baseLayer, 0, 0, width, height, null);

        //int smallWidth = topLayer.getWidth(null);   // 上层图片的宽度
        // 设置上层图片放置的位置的坐标及大小
        g.drawImage(topLayer, locationX, locationY, locationWidth, locationLength, null);
        if (nickName != null) {
            // 普通字体(ps:字体是微软雅黑，linux不具备有，需要安装，)
            Font font = new Font("微软雅黑", Font.PLAIN, 35);
            g.setFont(font);
            g.setColor(new Color(129, 129, 129));
            FontMetrics fm = g.getFontMetrics(font);
            // 字体放置的位置
            //int textWidth = fm.stringWidth(nickName);
            g.drawString(nickName, locationX + 100, 1110);
        }

        g.dispose();

        return image;
    }


    /**
     * 按指定的字节数截取字符串（一个中文字符占3个字节，一个英文字符或数字占1个字节）
     *
     * @param sourceString 源字符串
     * @param cutBytes     要截取的字节数
     * @return
     */
    public static String cutString(String sourceString, int cutBytes) {
        if (sourceString == null || "".equals(sourceString.trim())) {
            return "";
        }
        int lastIndex = 0;
        boolean stopFlag = false;
        int totalBytes = 0;
        for (int i = 0; i < sourceString.length(); i++) {
            String s = Integer.toBinaryString(sourceString.charAt(i));
            if (s.length() > 8) {
                totalBytes += 3;
            } else {
                totalBytes += 1;
            }
            if (!stopFlag) {
                if (totalBytes == cutBytes) {
                    lastIndex = i;
                    stopFlag = true;
                } else if (totalBytes > cutBytes) {
                    lastIndex = i - 1;
                    stopFlag = true;
                }
            }
        }
        if (!stopFlag) {
            return sourceString;
        } else {
            return sourceString.substring(0, lastIndex + 1);
        }
    }

    /**
     * 合并二维码附带使用说明
     *
     * @param baseImage
     * @param qrcodeBufferImage
     * @param text
     * @param locationX
     * @param locationY
     * @param locationWidth
     * @param locationLength
     * @return
     * @throws IOException
     */
    public static BufferedImage mergeQrcode(BufferedImage baseImage,
    		BufferedImage qrcodeBufferImage,
                                            String text,
                                            int locationX,
                                            int locationY,
                                            int locationWidth,
                                            int locationLength) throws IOException {


       // BufferedImage qrcodeBufferImage = ImageIO.read(qrcodeFile);

        int width = baseImage.getWidth(null); //底图的宽度
        int height = baseImage.getHeight(null); //底图的高度

        // 按照底图的宽高生成新的图片
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.createGraphics();
        g.drawImage(baseImage, 0, 0, width, height, null);

        // 设置上层图片放置的位置的坐标及大小，坐标居中
        g.drawImage(qrcodeBufferImage, locationX, locationY, locationWidth, locationLength, null);
        if (text != null) {
            // 普通字体
            Font font = new Font("微软雅黑", Font.PLAIN, 25);
            g.setFont(font);
            g.setColor(new Color(129, 129, 129));
            FontMetrics fm = g.getFontMetrics(font);
            // 字体放置的位置
            //int textWidth = fm.stringWidth(nickName);
            g.drawString(text, 748, 1330);
        }
        g.dispose();
        return image;
    }

    /**
     * 图片上添加文字
     *
     * @param src        the src
     * @param copywriter the copywriter
     * @return the buffered image
     * @throws Exception the exception
     */
    public static BufferedImage drawTextInImage(BufferedImage src, String copywriter) {
        int width = src.getWidth(null);
        int height = src.getHeight(null);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.drawImage(src, 0, 0, width, height, null);

        // 长度和位置
        Font font = new Font("微软雅黑", Font.PLAIN, 35);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics(font);
        int textWidth = fm.stringWidth(copywriter);
        g.setColor(new Color(47, 47, 47));
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 先按字节来换行，英文单词空格问题暂时未考虑
        if (copywriter.getBytes().length > 63) {
            String firstLine = cutString(copywriter, 63);
            String secondLine = copywriter.substring(firstLine.length(), copywriter.length());
            g.drawString(firstLine, (width - fm.stringWidth(firstLine)) / 2, COPYWRITER_Y);
            g.drawString(secondLine, (width - fm.stringWidth(secondLine)) / 2, COPYWRITER_Y + 35);
        } else {
            g.drawString(copywriter, COPYWRITER_X, COPYWRITER_Y);
        }
        g.dispose();

        return image;
    }

    public static BufferedImage drawTextInImage(BufferedImage src, String copywriter, int x, int y) {
        int width = src.getWidth(null);
        int height = src.getHeight(null);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.drawImage(src, 0, 0, width, height, null);

        // 长度和位置
        Font font = new Font("微软雅黑", Font.PLAIN, 35);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics(font);
        int textWidth = fm.stringWidth(copywriter);
        g.setColor(new Color(62, 62, 62));
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawString(copywriter, x, y);
        g.dispose();

        return image;
    }

    /**
     * 方形转为圆形
     *
     * @param img    the img
     * @param radius the radius 半径
     * @return the buffered image
     * @throws Exception the exception
     */
    public static BufferedImage convertRoundedImage(BufferedImage img, int radius) {
        BufferedImage result = new BufferedImage(radius, radius, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = result.createGraphics();
        //在适当的位置画图
        g.drawImage(img, (radius - img.getWidth(null)) / 2, (radius - img.getHeight(null)) / 2, null);

        //圆角
        RoundRectangle2D round = new RoundRectangle2D.Double(0, 0, radius, radius, radius * 2, radius * 2);
        Area clear = new Area(new Rectangle(0, 0, radius, radius));
        clear.subtract(new Area(round));
        g.setComposite(AlphaComposite.Clear);

        //抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.fill(clear);
        g.dispose();

        return result;
    }

    /**
     * 图像等比例缩放
     *
     * @param img     the img
     * @param maxSize the max size
     * @param type    the type
     * @return the scaled image
     */
    private static BufferedImage getScaledImage(BufferedImage img, int maxSize, int type) {
        int w0 = img.getWidth();
        int h0 = img.getHeight();
        int w = w0;
        int h = h0;
        // 头像如果是长方形：
        // 1:高度与宽度的最大值为maxSize进行等比缩放,
        // 2:高度与宽度的最小值为maxSize进行等比缩放
        if (type == 1) {
            w = w0 > h0 ? maxSize : (maxSize * w0 / h0);
            h = w0 > h0 ? (maxSize * h0 / w0) : maxSize;
        } else if (type == 2) {
            w = w0 > h0 ? (maxSize * w0 / h0) : maxSize;
            h = w0 > h0 ? maxSize : (maxSize * h0 / w0);
        }
        Image schedImage = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        BufferedImage bufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bufferedImage.createGraphics();
        g.drawImage(schedImage, 0, 0, null);
        return bufferedImage;
    }

    /**
     * 对头像处理
     *
     * @param in   of  the image
     * @param radius the radius
     * @return the buffered image
     * @throws Exception the exception
     */
    public static BufferedImage createRoundedImage(InputStream in, int radius) {
        BufferedImage img = null;
        BufferedImage fixedImg = null;
        BufferedImage bufferedImage = null;
        try {
            img = ImageIO.read(in);
            // 1. 按原比例缩减
            fixedImg = getScaledImage(img, radius, 2);
            // 2. 居中裁剪
            fixedImg = cutPicture(fixedImg, radius, radius);
            // 3. 把正方形生成圆形
            bufferedImage = convertRoundedImage(fixedImg, radius);
        } catch (IOException e) {
            LogUtils.error(e);
        }
        return bufferedImage;
    }

}