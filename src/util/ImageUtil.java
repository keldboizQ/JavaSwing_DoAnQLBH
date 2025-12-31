package util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class ImageUtil {

    public static String saveProductImage(File srcFile, String productCode) {
        try {
            BufferedImage img = ImageIO.read(srcFile);
            if (img == null) return null;

            // Convert sang RGB
            BufferedImage rgbImg = new BufferedImage(
                    img.getWidth(),
                    img.getHeight(),
                    BufferedImage.TYPE_INT_RGB
            );
            Graphics2D g = rgbImg.createGraphics();
            g.drawImage(img, 0, 0, null);
            g.dispose();

            // Resize
            int width = 300;
            int height = 300;
            Image scaled = rgbImg.getScaledInstance(width, height, Image.SCALE_SMOOTH);

            BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = resized.createGraphics();
            g2.drawImage(scaled, 0, 0, null);
            g2.dispose();

            // Lưu file
            String path = "images/products/" + productCode + ".jpg";
            ImageIO.write(resized, "jpg", new File(path));

            return path;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
