package extensions.junit;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ApiExtension implements BeforeEachCallback, AfterEachCallback {

    private static final Logger log = LoggerFactory.getLogger(ApiExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) {
        log.info("Starting test: {}", context.getDisplayName());
    }

    @Override
    public void afterEach(ExtensionContext context) {
        boolean passed = context.getExecutionException().isEmpty();
        log.info("Test {}: {}", context.getDisplayName(), passed ? "PASSED" : "FAILED");

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
        String testName = context.getDisplayName().replaceAll("[^a-zA-Z0-9]", "_");

        try {
            java.nio.file.Path dir = Paths.get("target/test-logs");
            Files.createDirectories(dir);
            String path = dir + "/" + testName + "_" + timestamp + ".log";
            StringBuilder content = new StringBuilder();
            content.append("Test:      ").append(context.getDisplayName()).append("\n");
            content.append("Status:    ").append(passed ? "PASSED" : "FAILED").append("\n");
            content.append("Timestamp: ").append(timestamp).append("\n");
            if (context.getExecutionException().isPresent()) {
                content.append("Failure:   ").append(context.getExecutionException().get().getMessage()).append("\n");
            }
            Files.write(Paths.get(path), content.toString().getBytes());
            log.info("Log saved: {}", path);
        } catch (Exception e) {
            log.warn("Failed to save test log: {}", e.getMessage());
        }
    }
}
