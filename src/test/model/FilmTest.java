package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pengujian Model Film")
public class FilmTest {

    @Nested
    @DisplayName("Inisialisasi Constructor")
    class ConstructorTest {
        @Test
        @DisplayName("Constructor dengan field lengkap")
        void testFullConstructor() {
            // WHY: Memastikan semua atribut film terinisialisasi dengan benar melalui constructor
            // Arrange
            String id = "550";
            String title = "Fight Club";
            String director = "David Fincher";
            String genre = "Drama";
            int year = 1999;
            String synopsis = "A film about...";
            boolean visible = true;

            // Act
            Film film = new Film(id, title, director, genre, year, synopsis, visible);

            // Assert
            assertAll(
                () -> assertEquals(id, film.getId(), "ID harus sesuai"),
                () -> assertEquals(title, film.getTitle(), "Judul harus sesuai"),
                () -> assertEquals(director, film.getDirector(), "Sutradara harus sesuai"),
                () -> assertEquals(genre, film.getGenre(), "Genre harus sesuai"),
                () -> assertEquals(year, film.getYear(), "Tahun harus sesuai"),
                () -> assertEquals(synopsis, film.getSynopsis(), "Sinopsis harus sesuai"),
                () -> assertEquals(visible, film.isVisible(), "Status visibility harus sesuai")
            );
        }
    }

    @Nested
    @DisplayName("Konversi File (Serialization)")
    class SerializationTest {
        @Test
        @DisplayName("toFileLine - Film normal")
        void testToFileLine() {
            // WHY: Format penyimpanan file harus konsisten untuk menjamin integritas data film
            // Arrange
            Film film = new Film("1", "Title", "Director", "Genre", 2024, "Synopsis", true);

            // Act
            String hasil = film.toFileLine();

            // Assert
            assertEquals("1|Title|Director|Genre|2024|Synopsis|true", hasil, "Format toFileLine tidak sesuai");
        }

        @Test
        @DisplayName("toFileLine - Escaping pipe dalam synopsis")
        void testToFileLineWithPipe() {
            // WHY: Karakter pipe (|) adalah delimiter file; harus di-escape agar tidak merusak struktur parsing
            // Arrange
            String synopsisWithPipe = "Synopsis | with pipe";
            Film film = new Film("1", "T", "D", "G", 2024, synopsisWithPipe, true);

            // Act
            String hasil = film.toFileLine();

            // Assert
            assertTrue(hasil.contains("Synopsis ~ with pipe"), "Karakter pipe harus di-escape menjadi tilde");
        }

        @Test
        @DisplayName("fromFileLine - Line valid")
        void testFromFileLineValid() {
            // WHY: Memastikan baris data dari file teks dapat dimuat kembali menjadi objek Film
            // Arrange
            String line = "550|Fight Club|David Fincher|Drama|1999|Synopsis|true";

            // Act
            Film film = Film.fromFileLine(line);

            // Assert
            assertNotNull(film, "Film tidak boleh null");
            assertEquals("Fight Club", film.getTitle(), "Judul hasil parsing harus sesuai");
            assertTrue(film.isVisible(), "Status visibility harus true");
        }

        @Test
        @DisplayName("fromFileLine - Default visibility jika field ke-7 tidak ada")
        void testFromFileLineDefaultVisibility() {
            // WHY: Menjaga kompatibilitas ke belakang (backward compatibility) dengan format data versi lama
            // Arrange
            String line = "1|T|D|G|2024|Synopsis";

            // Act
            Film film = Film.fromFileLine(line);

            // Assert
            assertNotNull(film, "Film tidak boleh null");
            assertTrue(film.isVisible(), "Visibility default harus true");
        }

        @Test
        @DisplayName("fromFileLine - Line invalid")
        void testFromFileLineInvalid() {
            // WHY: Sistem harus menangani data yang korup secara elegan tanpa melempar exception
            // Arrange
            String line = "1|Title";

            // Act
            Film film = Film.fromFileLine(line);

            // Assert
            assertNull(film, "Parsing line tidak lengkap harus return null");
        }
    }

    @Nested
    @DisplayName("Manajemen Visibility")
    class VisibilityTest {
        @Test
        @DisplayName("Ubah visibility menjadi hidden")
        void testSetVisibleFalse() {
            // WHY: Admin harus bisa menyembunyikan film tanpa menghapusnya (soft delete)
            // Arrange
            Film film = new Film("1", "T", "D", "G", 2024, "S", true);

            // Act
            film.setVisible(false);

            // Assert
            assertFalse(film.isVisible(), "Film harusnya hidden");
        }

        @Test
        @DisplayName("Ubah visibility menjadi visible")
        void testSetVisibleTrue() {
            // WHY: Admin harus bisa menampilkan kembali film yang sebelumnya disembunyikan
            // Arrange
            Film film = new Film("1", "T", "D", "G", 2024, "S", false);

            // Act
            film.setVisible(true);

            // Assert
            assertTrue(film.isVisible(), "Film harusnya visible");
        }
    }

    @Nested
    @DisplayName("Representasi Tabel")
    class TableRowTest {
        @Test
        @DisplayName("toTableRow - Film Visible")
        void testToTableRowVisible() {
            // WHY: Memastikan status visibility ditampilkan dalam format teks yang user-friendly di tabel
            // Arrange
            Film film = new Film("1", "T", "D", "G", 2024, "S", true);

            // Act
            Object[] row = film.toTableRow();

            // Assert
            assertEquals("Visible", row[6], "Status di tabel harus 'Visible'");
        }
    }

    @Nested
    @DisplayName("Getter dan Setter")
    class GetterSetterTest {
        @Test
        @DisplayName("Set dan Get ID")
        void testSetGetId() {
            // WHY: ID film mungkin perlu diperbarui jika ada sinkronisasi ulang dengan data TMDB
            // Arrange
            Film film = new Film("1", "T", "D", "G", 2024, "S", true);
            String newId = "2";

            // Act
            film.setId(newId);

            // Assert
            assertEquals(newId, film.getId(), "ID harus diperbarui");
        }

        @Test
        @DisplayName("Set dan Get Title")
        void testSetGetTitle() {
            // WHY: Judul film harus dapat dikoreksi jika terdapat kesalahan pengetikan
            // Arrange
            Film film = new Film("1", "T", "D", "G", 2024, "S", true);
            String newTitle = "New Title";

            // Act
            film.setTitle(newTitle);

            // Assert
            assertEquals(newTitle, film.getTitle(), "Judul harus diperbarui");
        }

        @Test
        @DisplayName("Set dan Get Director")
        void testSetGetDirector() {
            // WHY: Informasi sutradara adalah metadata penting untuk filter dan pencarian film
            // Arrange
            Film film = new Film("1", "T", "D", "G", 2024, "S", true);
            String newDirector = "New Director";

            // Act
            film.setDirector(newDirector);

            // Assert
            assertEquals(newDirector, film.getDirector(), "Sutradara harus diperbarui");
        }

        @Test
        @DisplayName("Set dan Get Genre")
        void testSetGetGenre() {
            // WHY: Kategorisasi film melalui genre membantu user dalam menjelajahi koleksi
            // Arrange
            Film film = new Film("1", "T", "D", "G", 2024, "S", true);
            String newGenre = "Comedy";

            // Act
            film.setGenre(newGenre);

            // Assert
            assertEquals(newGenre, film.getGenre(), "Genre harus diperbarui");
        }

        @Test
        @DisplayName("Set dan Get Year")
        void testSetGetYear() {
            // WHY: Tahun rilis membantu user membedakan remake atau film dengan judul serupa
            // Arrange
            Film film = new Film("1", "T", "D", "G", 2024, "S", true);
            int newYear = 2025;

            // Act
            film.setYear(newYear);

            // Assert
            assertEquals(newYear, film.getYear(), "Tahun harus diperbarui");
        }
    }
}
