import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class GenForeground {
    public static void main(String[] args) throws Exception {
        BufferedImage src = ImageIO.read(new File("icon.jpeg"));

        int[]    sizes = {108, 162, 216, 324, 432};
        String[] dirs  = {"mipmap-mdpi", "mipmap-hdpi", "mipmap-xhdpi", "mipmap-xxhdpi", "mipmap-xxxhdpi"};

        String resDir = "app/src/main/res/";
        for (int i = 0; i < sizes.length; i++) {
            int size = sizes[i];
            String dir = resDir + dirs[i];
            BufferedImage scaled = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = scaled.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
            g.drawImage(src, 0, 0, size, size, null);
            g.dispose();
            ImageIO.write(scaled, "PNG", new File(dir + "/ic_launcher_foreground.png"));
            System.out.println("Written " + size + "x" + size + " -> " + dir);
        }
    }
}
