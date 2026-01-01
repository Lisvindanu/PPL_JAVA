package util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pengujian ValidationUtil")
public class ValidationUtilTest {

    @Nested
    @DisplayName("Validasi Email")
    class EmailValidation {
        @Test
        @DisplayName("Email valid standar")
        void testValidEmail() {
            // WHY: Memastikan format email standar (user@domain) dikenali sebagai valid
            // Arrange
            String email = "user@example.com";

            // Act
            boolean hasil = ValidationUtil.isValidEmail(email);

            // Assert
            assertTrue(hasil, "Email valid harus return true");
        }

        @Test
        @DisplayName("Email invalid tanpa simbol @")
        void testInvalidEmailTanpaAt() {
            // WHY: Simbol '@' adalah pemisah wajib antara local-part dan domain dalam standar email
            // Arrange
            String email = "userexample.com";

            // Act
            boolean hasil = ValidationUtil.isValidEmail(email);

            // Assert
            assertFalse(hasil, "Email tanpa @ harus return false");
        }

        @Test
        @DisplayName("Email invalid null")
        void testInvalidEmailNull() {
            // WHY: Input null tidak boleh menyebabkan crash dan harus dianggap sebagai input tidak valid
            // Arrange
            String email = null;

            // Act
            boolean hasil = ValidationUtil.isValidEmail(email);

            // Assert
            assertFalse(hasil, "Email null harus return false");
        }
    }

    @Nested
    @DisplayName("Validasi Tahun")
    class YearValidation {
        @Test
        @DisplayName("Tahun valid 2024")
        void testValidYear() {
            // WHY: Tahun 2024 berada dalam rentang bisnis yang diizinkan (1900-2100)
            // Arrange
            int year = 2024;

            // Act
            boolean hasil = ValidationUtil.isValidYear(year);

            // Assert
            assertTrue(hasil, "Tahun 2024 harus valid");
        }

        @Test
        @DisplayName("Tahun invalid di bawah batas (1899)")
        void testInvalidYearDibawahBatas() {
            // WHY: Tahun di bawah 1900 dianggap data historis yang tidak relevan untuk sistem ini
            // Arrange
            int year = 1899;

            // Act
            boolean hasil = ValidationUtil.isValidYear(year);

            // Assert
            assertFalse(hasil, "Tahun 1899 harus invalid");
        }

        @Test
        @DisplayName("Tahun invalid dari string non-angka")
        void testInvalidYearStringNonAngka() {
            // WHY: Sistem harus mampu menangani input string non-numerik tanpa error (robustness)
            // Arrange
            String year = "abc";

            // Act
            boolean hasil = ValidationUtil.isValidYear(year);

            // Assert
            assertFalse(hasil, "String non-angka harus invalid");
        }
    }

    @Nested
    @DisplayName("Pengecekan String Kosong")
    class EmptyStringCheck {
        @Test
        @DisplayName("String kosong")
        void testStringKosong() {
            // WHY: Field wajib tidak boleh dibiarkan kosong untuk menjaga integritas data
            // Arrange
            String text = "";

            // Act
            boolean hasil = ValidationUtil.isEmpty(text);

            // Assert
            assertTrue(hasil, "String kosong harus return true");
        }

        @Test
        @DisplayName("String valid")
        void testStringValid() {
            // WHY: Memastikan string yang berisi teks tidak dianggap kosong
            // Arrange
            String text = "hello";

            // Act
            boolean hasil = ValidationUtil.isEmpty(text);

            // Assert
            assertFalse(hasil, "String berisi teks harus return false");
        }
    }

    @Nested
    @DisplayName("Validasi Username")
    class UsernameValidation {
        @Test
        @DisplayName("Username valid alphanumeric")
        void testValidUsername() {
            // WHY: Username alphanumeric memudahkan identifikasi unik user tanpa karakter kompleks
            // Arrange
            String username = "user123";

            // Act
            boolean hasil = ValidationUtil.isValidUsername(username);

            // Assert
            assertTrue(hasil, "Username alphanumeric harus valid");
        }

        @Test
        @DisplayName("Username invalid kurang dari 3 karakter")
        void testInvalidUsernameTerlaluPendek() {
            // WHY: Username minimal 3 karakter untuk mencegah tabrakan id yang terlalu simpel
            // Arrange
            String username = "ab";

            // Act
            boolean hasil = ValidationUtil.isValidUsername(username);

            // Assert
            assertFalse(hasil, "Username < 3 karakter harus invalid");
        }

        @Test
        @DisplayName("Username invalid null")
        void testInvalidUsernameNull() {
            // WHY: Mencegah NullPointerException saat memproses data registrasi
            // Arrange
            String username = null;

            // Act
            boolean hasil = ValidationUtil.isValidUsername(username);

            // Assert
            assertFalse(hasil, "Username null harus invalid");
        }
    }

    @Nested
    @DisplayName("Validasi Kekuatan Password")
    class PasswordStrengthValidation {
        @Test
        @DisplayName("Password kuat (lengkap)")
        void testPasswordKuat() {
            // WHY: Password harus mengandung kombinasi karakter untuk keamanan akun maksimal
            // Arrange
            String pass = "MyPass123";

            // Act
            boolean hasil = ValidationUtil.isValidPasswordStrength(pass);

            // Assert
            assertTrue(hasil, "Password dengan kombinasi lengkap harus valid");
        }

        @Test
        @DisplayName("Password lemah (terlalu pendek)")
        void testPasswordTerlaluPendek() {
            // WHY: Password pendek sangat rentan terhadap serangan brute-force
            // Arrange
            String pass = "pass";

            // Act
            boolean hasil = ValidationUtil.isValidPasswordStrength(pass);

            // Assert
            assertFalse(hasil, "Password < 8 karakter harus invalid");
        }

        @Test
        @DisplayName("Password lemah (tanpa huruf besar/angka)")
        void testPasswordTanpaVariasi() {
            // WHY: Tanpa variasi karakter, password mudah ditebak (entropy rendah)
            // Arrange
            String pass = "password";

            // Act
            boolean hasil = ValidationUtil.isValidPasswordStrength(pass);

            // Assert
            assertFalse(hasil, "Password tanpa variasi harus invalid");
        }

        @ParameterizedTest(name = "Password {0} harusnya berkategori {1}")
        @CsvSource({
            "abc, Lemah",
            "password, Sedang",
            "Password1, Kuat",
            "MyP@ssw0rd123, Sangat Kuat"
        })
        @DisplayName("Deskripsi kekuatan password")
        void testGetPasswordStrengthDescription(String password, String expected) {
            // WHY: Memberikan feedback visual yang akurat kepada user mengenai tingkat keamanan password mereka
            // Act
            String hasil = ValidationUtil.getPasswordStrengthDescription(password);

            // Assert
            assertEquals(expected, hasil, "Deskripsi kekuatan password harus sesuai");
        }
    }

    @Nested
    @DisplayName("Validasi TMDB ID")
    class TMDBIdValidation {
        @Test
        @DisplayName("TMDB ID valid")
        void testValidTMDBId() {
            // WHY: ID dari API TMDB selalu berupa angka positif
            // Arrange
            String id = "550";

            // Act
            boolean hasil = ValidationUtil.isValidTMDBId(id);

            // Assert
            assertTrue(hasil, "TMDB ID angka harus valid");
        }

        @Test
        @DisplayName("TMDB ID invalid (huruf)")
        void testInvalidTMDBIdHuruf() {
            // WHY: ID yang mengandung huruf akan menyebabkan kegagalan saat fetch data API
            // Arrange
            String id = "abc123";

            // Act
            boolean hasil = ValidationUtil.isValidTMDBId(id);

            // Assert
            assertFalse(hasil, "TMDB ID dengan huruf harus invalid");
        }
    }

    @Nested
    @DisplayName("Validasi Panjang String")
    class StringLengthValidation {
        @Test
        @DisplayName("Panjang string valid")
        void testValidStringLength() {
            // WHY: Memastikan data input sesuai dengan kapasitas penyimpanan database/file
            // Arrange
            String text = "hello";
            int min = 3;
            int max = 10;

            // Act
            boolean hasil = ValidationUtil.isValidStringLength(text, min, max);

            // Assert
            assertTrue(hasil, "Panjang string 5 (rentang 3-10) harus valid");
        }

        @Test
        @DisplayName("Panjang string terlalu pendek")
        void testInvalidStringLengthTerlaluPendek() {
            // WHY: Mencegah input yang terlalu singkat yang tidak informatif (misal judul film)
            // Arrange
            String text = "ab";
            int min = 3;
            int max = 10;

            // Act
            boolean hasil = ValidationUtil.isValidStringLength(text, min, max);

            // Assert
            assertFalse(hasil, "String < min harus invalid");
        }

        @Test
        @DisplayName("Panjang string null")
        void testInvalidStringLengthNull() {
            // WHY: Validasi panjang tidak bisa dilakukan pada objek null
            // Arrange
            String text = null;

            // Act
            boolean hasil = ValidationUtil.isValidStringLength(text, 3, 10);

            // Assert
            assertFalse(hasil, "String null harus invalid");
        }
    }

    @Nested
    @DisplayName("Dialog Helpers")
    class DialogHelperTest {
        @Test
        @DisplayName("showError - tidak melempar exception")
        void testShowError() {
            // WHY: Memastikan method UI helper aman dipanggil bahkan dengan parameter null
            // Assert
            assertDoesNotThrow(() -> ValidationUtil.showError(null, "Test Error"));
        }
    }
}
