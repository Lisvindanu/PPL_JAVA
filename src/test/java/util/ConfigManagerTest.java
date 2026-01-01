package util;

import org.junit.jupiter.api.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pengujian ConfigManager")
public class ConfigManagerTest {

    private static final String TEST_CONFIG_FILE = "config.properties";

    @BeforeAll
    static void setup() throws IOException {
        Properties props = new Properties();
        props.setProperty("app.name", "FilmManagerTest");
        props.setProperty("app.version", "1.0.0");
        try (FileWriter writer = new FileWriter(TEST_CONFIG_FILE)) {
            props.store(writer, "Test Config");
        }
    }

    @Nested
    @DisplayName("Pembacaan Properti")
    class PropertyReadTest {
        @Test
        @DisplayName("Membaca property yang ada")
        void testGetPropertySuccess() {
            // WHY: Memastikan konfigurasi aplikasi dapat dibaca dari file eksternal (config.properties)
            // Act
            String appName = ConfigManager.getProperty("app.name");

            // Assert
            // Properti yang didefinisikan di file konfigurasi harus dapat dibaca nilainya
            assertEquals("FilmManagerTest", appName, "Nilai property app.name harus sesuai");
        }

        @Test
        @DisplayName("Membaca property app.version")
        void testGetAppVersion() {
            // WHY: Versi aplikasi penting untuk pelacakan rilis dan kompatibilitas
            // Act
            String version = ConfigManager.getProperty("app.version");

            // Assert
            // Memastikan property lain juga dapat dibaca dengan benar
            assertEquals("1.0.0", version, "Nilai property app.version harus sesuai");
        }

        @Test
        @DisplayName("Membaca property yang tidak ada")
        void testGetPropertyNotFound() {
            // WHY: Sistem harus mengembalikan null secara aman jika kunci konfigurasi tidak ditemukan, daripada melempar exception
            // Act
            String value = ConfigManager.getProperty("non.existent");

            // Assert
            // Key yang tidak terdaftar di file properties harus mengembalikan null
            assertNull(value, "Property yang tidak ada harus return null");
        }

        @Test
        @DisplayName("Membaca property dengan key null")
        void testGetPropertyNullKey() {
            // WHY: Robustness; aplikasi tidak boleh crash jika ada kesalahan logika yang mengirimkan key null
            // Act
            String value = ConfigManager.getProperty(null);

            // Assert
            // Input null pada key harus ditangani dan mengembalikan null
            assertNull(value, "Key null harus return null");
        }

        @Test
        @DisplayName("Membaca property dengan key kosong")
        void testGetPropertyEmptyKey() {
            // WHY: Key kosong secara semantik tidak valid dan harus ditangani sebagai 'tidak ditemukan'
            // Act
            String value = ConfigManager.getProperty("");

            // Assert
            // Key kosong biasanya tidak ada di file properties dan harus return null
            assertNull(value, "Key kosong harus return null");
        }
    }
}
