package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pengujian Model User")
public class UserTest {

    @Nested
    @DisplayName("Inisialisasi Constructor")
    class ConstructorTest {
        @Test
        @DisplayName("Constructor dengan field lengkap")
        void testFullConstructor() {
            // WHY: Memastikan semua atribut user terinisialisasi dengan benar melalui constructor lengkap
            // Arrange
            String email = "user@test.com";
            String pass = "pass123";
            String username = "john";
            String role = "USER";
            String gender = "Male";
            boolean premium = false;

            // Act
            User user = new User(email, pass, username, role, gender, premium);

            // Assert
            assertAll(
                () -> assertEquals(email, user.getEmail(), "Email harus sesuai"),
                () -> assertEquals(pass, user.getPassword(), "Password harus sesuai"),
                () -> assertEquals(username, user.getUsername(), "Username harus sesuai"),
                () -> assertEquals(role, user.getRole(), "Role harus sesuai"),
                () -> assertEquals(gender, user.getGender(), "Gender harus sesuai"),
                () -> assertEquals(premium, user.isPremium(), "Status premium harus sesuai")
            );
        }
    }

    @Nested
    @DisplayName("Konversi File (Serialization)")
    class SerializationTest {
        @Test
        @DisplayName("toFileLine - User biasa")
        void testToFileLineUser() {
            // WHY: Format string pipe-delimited harus konsisten untuk penyimpanan data yang persisten
            // Arrange
            User user = new User("user@test.com", "pass123", "john", "USER", "Male", false);

            // Act
            String hasil = user.toFileLine();

            // Assert
            assertEquals("user@test.com|pass123|john|USER|Male|false", hasil, "Format toFileLine tidak sesuai");
        }

        @Test
        @DisplayName("fromFileLine - Line valid")
        void testFromFileLineValid() {
            // WHY: Memastikan data string dari file dapat dikonversi kembali menjadi objek User yang valid
            // Arrange
            String line = "user@test.com|pass123|john|USER|Male|false";

            // Act
            User user = User.fromFileLine(line);

            // Assert
            assertNotNull(user, "User tidak boleh null");
            assertEquals("user@test.com", user.getEmail(), "Email hasil parsing harus sesuai");
        }

        @Test
        @DisplayName("fromFileLine - Line invalid (terlalu sedikit field)")
        void testFromFileLineInvalid() {
            // WHY: Menghindari kegagalan sistem (crash) saat membaca baris data yang korup atau tidak lengkap
            // Arrange
            String line = "user@test.com|pass123";

            // Act
            User user = User.fromFileLine(line);

            // Assert
            assertNull(user, "Parsing line tidak lengkap harus return null");
        }
    }

    @Nested
    @DisplayName("Pengecekan Role Admin")
    class AdminCheckTest {
        @Test
        @DisplayName("Role ADMIN mengembalikan true")
        void testIsAdminTrue() {
            // WHY: Hak akses administratif sangat krusial untuk fitur manajemen sistem
            // Arrange
            User user = new User("a@b.com", "p", "u", "ADMIN");

            // Act
            boolean hasil = user.isAdmin();

            // Assert
            assertTrue(hasil, "Role ADMIN harus return true");
        }

        @Test
        @DisplayName("Role USER mengembalikan false")
        void testIsAdminFalse() {
            // WHY: Memastikan user biasa tidak memiliki akses ke fitur administratif
            // Arrange
            User user = new User("a@b.com", "p", "u", "USER");

            // Act
            boolean hasil = user.isAdmin();

            // Assert
            assertFalse(hasil, "Role USER harus return false");
        }
    }

    @Nested
    @DisplayName("Getter dan Setter")
    class GetterSetterTest {
        @Test
        @DisplayName("Set dan Get Username")
        void testSetGetUsername() {
            // WHY: User harus dapat mengubah nama tampilannya di profil
            // Arrange
            User user = new User("a@b.com", "p", "u", "r");
            String newUsername = "newname";

            // Act
            user.setUsername(newUsername);

            // Assert
            assertEquals(newUsername, user.getUsername(), "Username harus diperbarui");
        }

        @Test
        @DisplayName("Set dan Get Email")
        void testSetGetEmail() {
            // WHY: Email adalah identifier unik yang harus bisa diperbarui jika diperlukan
            // Arrange
            User user = new User("a@b.com", "p", "u", "r");
            String newEmail = "new@test.com";

            // Act
            user.setEmail(newEmail);

            // Assert
            assertEquals(newEmail, user.getEmail(), "Email harus diperbarui");
        }

        @Test
        @DisplayName("Set dan Get Password")
        void testSetGetPassword() {
            // WHY: Fitur ganti password adalah standar keamanan dasar bagi user
            // Arrange
            User user = new User("a@b.com", "p", "u", "r");
            String newPass = "secret";

            // Act
            user.setPassword(newPass);

            // Assert
            assertEquals(newPass, user.getPassword(), "Password harus diperbarui");
        }

        @Test
        @DisplayName("Set dan Get Role")
        void testSetGetRole() {
            // WHY: Perubahan role user (misal promosi ke admin) harus tercermin pada objek
            // Arrange
            User user = new User("a@b.com", "p", "u", "r");
            String newRole = "ADMIN";

            // Act
            user.setRole(newRole);

            // Assert
            assertEquals(newRole, user.getRole(), "Role harus diperbarui");
        }

        @Test
        @DisplayName("Set dan Get Gender")
        void testSetGetGender() {
            // WHY: Data profil seperti gender membantu personalisasi pengalaman user
            // Arrange
            User user = new User("a@b.com", "p", "u", "r");
            String newGender = "Female";

            // Act
            user.setGender(newGender);

            // Assert
            assertEquals(newGender, user.getGender(), "Gender harus diperbarui");
        }

        @Test
        @DisplayName("Set dan Get Premium Status")
        void testSetGetPremium() {
            // WHY: Status premium menentukan akses user ke konten eksklusif
            // Arrange
            User user = new User("a@b.com", "p", "u", "r");

            // Act
            user.setPremium(true);

            // Assert
            assertTrue(user.isPremium(), "Status premium harus true");
        }
    }

    @Nested
    @DisplayName("Representasi Tabel")
    class TableRowTest {
        @Test
        @DisplayName("Konversi ke Table Row")
        void testToTableRow() {
            // WHY: Memastikan data user diformat dengan benar untuk ditampilkan pada UI JTable (Admin View)
            // Arrange
            User user = new User("user@test.com", "pass123", "john", "USER", "Male", true);

            // Act
            Object[] row = user.toTableRow();

            // Assert
            assertAll(
                () -> assertEquals(4, row.length, "Array harus memiliki 4 elemen"),
                () -> assertEquals("user@test.com", row[0], "Kolom 1: Email"),
                () -> assertEquals("john", row[1], "Kolom 2: Username"),
                () -> assertEquals("USER", row[2], "Kolom 3: Role"),
                () -> assertEquals("Premium", row[3], "Kolom 4: Status Akun")
            );
        }
    }
}
