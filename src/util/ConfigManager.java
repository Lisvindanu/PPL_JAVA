package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class untuk memuat konfigurasi dari file properties.
 * Menggunakan design pattern Singleton untuk memastikan konfigurasi hanya dimuat sekali.
 */
public class ConfigManager {
    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE = "config.properties";

    static {
        loadConfig();
    }

    private static void loadConfig() {
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
        } catch (IOException e) {
            System.err.println("Error loading config file: " + e.getMessage());
            System.err.println("Make sure " + CONFIG_FILE + " exists in the project root.");
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}
