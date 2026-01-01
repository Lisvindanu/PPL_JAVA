package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pengujian Model Playlist")
public class PlaylistTest {

    @Nested
    @DisplayName("Inisialisasi Constructor")
    class ConstructorTest {
        @Test
        @DisplayName("Constructor dengan field lengkap")
        void testFullConstructor() {
            // WHY: Memastikan objek playlist terinisialisasi dengan benar untuk menyimpan koleksi film user
            // Arrange
            String name = "Favorites";
            String owner = "user@test.com";
            String visibility = "Private";
            List<String> ids = Arrays.asList("550", "680");

            // Act
            Playlist playlist = new Playlist(name, owner, visibility, ids);

            // Assert
            assertAll(
                () -> assertEquals(name, playlist.getName(), "Nama harus sesuai"),
                () -> assertEquals(owner, playlist.getOwnerEmail(), "Email pemilik harus sesuai"),
                () -> assertEquals(visibility, playlist.getVisibility(), "Visibility harus sesuai"),
                () -> assertEquals(2, playlist.getFilmIds().size(), "Jumlah film harus sesuai")
            );
        }
    }

    @Nested
    @DisplayName("Konversi File (Serialization)")
    class SerializationTest {
        @Test
        @DisplayName("toFileLine - Playlist dengan banyak film")
        void testToFileLineManyFilms() {
            // WHY: Format penyimpanan playlist harus mendukung daftar film yang dipisahkan koma
            // Arrange
            List<String> ids = Arrays.asList("1", "2");
            Playlist playlist = new Playlist("Favorites", "u@t.com", "Private", ids);

            // Act
            String hasil = playlist.toFileLine();

            // Assert
            assertEquals("Favorites|u@t.com|Private|1,2", hasil, "Format toFileLine tidak sesuai");
        }

        @Test
        @DisplayName("fromFileLine - Line dengan film")
        void testFromFileLineWithFilms() {
            // WHY: Memastikan daftar ID film dapat dimuat kembali secara akurat dari file teks
            // Arrange
            String line = "Favorites|user@test.com|Private|550,680";

            // Act
            Playlist playlist = Playlist.fromFileLine(line);

            // Assert
            assertNotNull(playlist, "Playlist tidak boleh null");
            assertEquals(2, playlist.getFilmIds().size(), "Jumlah film hasil parsing harus sesuai");
        }

        @Test
        @DisplayName("fromFileLine - Line invalid")
        void testFromFileLineInvalid() {
            // WHY: Mencegah error saat memproses data playlist yang tidak lengkap atau korup
            // Arrange
            String line = "OnlyName|Email";

            // Act
            Playlist playlist = Playlist.fromFileLine(line);

            // Assert
            assertNull(playlist, "Parsing line tidak lengkap harus return null");
        }
    }

    @Nested
    @DisplayName("Getter dan Setter")
    class GetterSetterTest {
        @Test
        @DisplayName("Set dan Get Name")
        void testSetGetName() {
            // WHY: User harus bisa mengganti nama playlist mereka (misal dari 'Watchlist' ke 'Best Movies')
            // Arrange
            Playlist playlist = new Playlist("Old", "u@t.com", "Public", new ArrayList<>());
            String newName = "New";

            // Act
            playlist.setName(newName);

            // Assert
            assertEquals(newName, playlist.getName(), "Nama harus diperbarui");
        }

        @Test
        @DisplayName("Set dan Get Owner Email")
        void testSetGetOwner() {
            // WHY: Kepemilikan playlist harus dapat dikelola untuk manajemen data user
            // Arrange
            Playlist playlist = new Playlist("Name", "old@t.com", "Public", new ArrayList<>());
            String newEmail = "new@t.com";

            // Act
            playlist.setOwnerEmail(newEmail);

            // Assert
            assertEquals(newEmail, playlist.getOwnerEmail(), "Email pemilik harus diperbarui");
        }

        @Test
        @DisplayName("Set dan Get Visibility")
        void testSetGetVisibility() {
            // WHY: User harus memiliki kontrol privasi atas playlist mereka (Public vs Private)
            // Arrange
            Playlist playlist = new Playlist("Name", "u@t.com", "Public", new ArrayList<>());
            String newVisibility = "Private";

            // Act
            playlist.setVisibility(newVisibility);

            // Assert
            assertEquals(newVisibility, playlist.getVisibility(), "Visibility harus diperbarui");
        }

        @Test
        @DisplayName("Set dan Get Film IDs")
        void testSetGetFilmIds() {
            // WHY: Daftar film dalam playlist adalah inti dari fitur ini; harus dapat diperbarui secara dinamis
            // Arrange
            Playlist playlist = new Playlist("Name", "u@t.com", "Public", new ArrayList<>());
            List<String> newIds = Arrays.asList("10", "20");

            // Act
            playlist.setFilmIds(newIds);

            // Assert
            assertAll(
                () -> assertEquals(2, playlist.getFilmIds().size(), "Jumlah ID harus 2"),
                () -> assertEquals("10", playlist.getFilmIds().get(0), "ID pertama harus sesuai")
            );
        }
    }

    @Nested
    @DisplayName("Representasi Tabel")
    class TableRowTest {
        @Test
        @DisplayName("toTableRow - Verifikasi format array")
        void testToTableRow() {
            // WHY: Memastikan ringkasan playlist (termasuk jumlah film) ditampilkan dengan benar di UI user
            // Arrange
            Playlist playlist = new Playlist("MyList", "u@t.com", "Public", Arrays.asList("1", "2"));

            // Act
            Object[] row = playlist.toTableRow();

            // Assert
            assertAll(
                () -> assertEquals(5, row.length, "Array harus memiliki 5 elemen"),
                () -> assertEquals("MyList", row[0], "Kolom 1: Nama"),
                () -> assertEquals(2, row[3], "Kolom 4: Jumlah Film")
            );
        }
    }
}
