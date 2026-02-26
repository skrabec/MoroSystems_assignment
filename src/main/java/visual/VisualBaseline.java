package visual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class VisualBaseline {
    private static final Logger log = LoggerFactory.getLogger(VisualBaseline.class);
    private static final Path BASELINE_DIR = Paths.get("src/test/resources/visual-baseline");
    public static final Path DIFF_DIR = Paths.get("target/visual-diffs");

    /**
     * Load a baseline image by name.
     * Returns null if no baseline exists yet (first run).
     */
    public static BufferedImage load(String name) throws IOException {
        Path path = BASELINE_DIR.resolve(name + ".png");
        if (!Files.exists(path)) {
            return null;
        }
        return ImageIO.read(path.toFile());
    }

    /**
     * Save image as the new baseline. Used on first run or when updating baselines.
     */
    public static void save(BufferedImage image, String name) throws IOException {
        Files.createDirectories(BASELINE_DIR);
        Path path = BASELINE_DIR.resolve(name + ".png");
        ImageIO.write(image, "PNG", path.toFile());
        log.info("Baseline saved: {}", path);
    }

    /**
     * Save a diff image to target/visual-diffs for inspection.
     */
    public static void saveDiff(BufferedImage diffImage, String name) throws IOException {
        Files.createDirectories(DIFF_DIR);
        Path path = DIFF_DIR.resolve(name + "_diff.png");
        ImageIO.write(diffImage, "PNG", path.toFile());
        log.info("Diff image saved: {}", path);
    }

    /**
     * Convert Playwright screenshot bytes to BufferedImage.
     */
    public static BufferedImage fromBytes(byte[] bytes) throws IOException {
        return ImageIO.read(new ByteArrayInputStream(bytes));
    }
}
