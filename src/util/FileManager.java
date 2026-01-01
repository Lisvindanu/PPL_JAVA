package util;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Utility class untuk mengelola operasi file I/O.
 * Menangani pembacaan dan penulisan data ke file teks.
 * Menyediakan method untuk operasi file seperti read, write, append, dan pengecekan eksistensi file.
 *
 * @author lisvindanu
 * @version 2.0
 */
public class FileManager {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(FileManager.class);

    private static final String DATA_DIR = ConfigManager.getProperty("data.directory", "src/data/");
    public static final String USERS_FILE = DATA_DIR + "users.txt";
    public static final String FILMS_FILE = DATA_DIR + "films.txt";
    public static final String PLAYLISTS_FILE = DATA_DIR + "playlists.txt";

    static {
        initializeDataDirectory();
    }

    /**
     * Menginisialisasi direktori data dan file-file yang diperlukan.
     * Membuat direktori dan file jika belum ada.
     * Dipanggil secara otomatis saat class dimuat.
     */
    private static void initializeDataDirectory() {
        try {
            Path dataPath = Paths.get(DATA_DIR);
            if (!Files.exists(dataPath)) {
                Files.createDirectories(dataPath);
            }

            // Create files if they don't exist
            createFileIfNotExists(USERS_FILE);
            createFileIfNotExists(FILMS_FILE);
            createFileIfNotExists(PLAYLISTS_FILE);
        } catch (IOException e) {
            logger.error("CRITICAL: Failed to initialize data directory structure at " + DATA_DIR + ". This may prevent the application from saving data.", e);
        }
    }

    /**
     * Membuat file baru jika file tersebut belum ada.
     *
     * @param filePath path file yang akan dibuat
     * @throws IOException jika terjadi error saat membuat file
     */
    private static void createFileIfNotExists(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
    }

    /**
     * Membaca semua baris dari file.
     *
     * @param filePath path file yang akan dibaca
     * @return list berisi semua baris dalam file
     */
    public static List<String> readLines(String filePath) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            logger.error("Error reading data from file: " + filePath + ". Please ensure the file exists and is readable.", e);
        }
        return lines;
    }

    /**
     * Menulis list of lines ke file (overwrite).
     * File yang sudah ada akan ditimpa dengan data baru.
     *
     * @param filePath path file tujuan
     * @param lines list baris yang akan ditulis
     */
    public static void writeLines(String filePath, List<String> lines) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            logger.error("Error writing data to file: " + filePath + ". Data point may be lost. Check disk space and permissions.", e);
        }
    }

    /**
     * Menambahkan satu baris ke akhir file (append mode).
     *
     * @param filePath path file tujuan
     * @param line baris yang akan ditambahkan
     */
    public static void appendLine(String filePath, String line) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            logger.error("Error appending data to file: " + filePath + ". Record update failed.", e);
        }
    }

    /**
     * Mengecek apakah file ada di path tertentu.
     *
     * @param filePath path file yang akan dicek
     * @return true jika file ada, false jika tidak
     */
    public static boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }
}
