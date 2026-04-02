import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class ResizeIcon {
    public static void main(String[] args) throws Exception {
        String srcPath = args[0];
        String resDir = args[1];

        BufferedImage src = ImageIO.read(new File(srcPath));

        String[][] targets = {
            {"mipmap-mdpi",    "48"},
            {"mipmap-hdpi",    "72"},
            {"mipmap-xhdpi",   "96"},
            {"mipmap-xxhdpi",  "144"},
            {"mipmap-xxxhdpi", "192"}
        };

        for (String[] t : targets) {
            String dir   = resDir + File.separator + t[0];
            int    size  = Integer.parseInt(t[1]);
            new File(dir).mkdirs();

            BufferedImage scaled = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = scaled.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
            g.drawImage(src, 0, 0, size, size, null);
            g.dispose();

            ImageIO.write(scaled, "PNG", new File(dir + File.separator + "ic_launcher.png"));
            ImageIO.write(scaled, "PNG", new File(dir + File.separator + "ic_launcher_round.png"));
            System.out.println("Written " + size + "x" + size + " -> " + dir);
        }
    }
}
