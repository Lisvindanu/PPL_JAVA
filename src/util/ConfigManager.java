package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Utility class untuk mengelola konfigurasi aplikasi.
 * Membaca properti dari file config.properties.
 *
 * @author Junie
 * @version 1.0
 */
public class ConfigManager {
    private static final String CONFIG_FILE = "config.properties";
    private static Properties properties = new Properties();

    static {
        loadProperties();
    }

    private static void loadProperties() {
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            properties.load(fis);
        } catch (IOException e) {
            // Silently fail or use default values
        }
    }

    /**
     * Mendapatkan nilai properti berdasarkan key.
     *
     * @param key kunci properti
     * @return nilai properti, atau null jika tidak ditemukan
     */
    public static String getProperty(String key) {
        if (key == null) return null;
        return properties.getProperty(key);
    }
}
