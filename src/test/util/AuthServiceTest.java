package util;

import model.User;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pengujian AuthService")
public class AuthServiceTest {

    private static final String TEST_USER_EMAIL = "test@user.com";
    private static final String TEST_USER_PASS = "password123";
    private static List<String> originalUsers;

    @BeforeAll
    static void setup() throws IOException {
        // Backup original users
        originalUsers = FileManager.readLines(FileManager.USERS_FILE);
        
        // Clear file and add a test user
        List<String> testUsers = new ArrayList<>();
        User testUser = new User(TEST_USER_EMAIL, TEST_USER_PASS, "TestUser", "USER");
        User adminUser = new User("admin@test.com", "admin", "Admin", "ADMIN");
        testUsers.add(testUser.toFileLine());
        testUsers.add(adminUser.toFileLine());
        FileManager.writeLines(FileManager.USERS_FILE, testUsers);
    }

    @AfterAll
    static void teardown() {
        // Restore original users
        FileManager.writeLines(FileManager.USERS_FILE, originalUsers);
    }

    @BeforeEach
    void logoutBeforeTest() {
        AuthService.logout();
    }

    @Nested
    @DisplayName("Proses Login")
    class LoginTest {
        @Test
        @DisplayName("Login dengan kredensial valid")
        void testLoginSuccess() {
            // WHY: Alur login adalah pintu masuk utama sistem; harus menjamin user valid bisa masuk
            // Act
            User user = AuthService.login(TEST_USER_EMAIL, TEST_USER_PASS);

            // Assert
            assertNotNull(user, "User tidak boleh null");
            assertEquals(TEST_USER_EMAIL, user.getEmail(), "Email user harus sesuai");
            assertTrue(AuthService.isLoggedIn(), "Status harusLoggedIn");
        }

        @Test
        @DisplayName("Login dengan password salah")
        void testLoginWrongPassword() {
            // WHY: Keamanan akun bergantung pada penolakan login dengan kredensial yang salah
            // Act
            User user = AuthService.login(TEST_USER_EMAIL, "wrongpass");

            // Assert
            assertNull(user, "User harus null");
            assertFalse(AuthService.isLoggedIn(), "Status harus tidakLoggedIn");
        }
    }

    @Nested
    @DisplayName("Proses Logout")
    class LogoutTest {
        @Test
        @DisplayName("Logout membersihkan session user")
        void testLogout() {
            // WHY: Logout harus memastikan session user benar-benar dihapus untuk mencegah akses tidak sah setelahnya
            // Arrange
            AuthService.login(TEST_USER_EMAIL, TEST_USER_PASS);
            assertTrue(AuthService.isLoggedIn(), "Harus login dulu untuk test logout");

            // Act
            AuthService.logout();

            // Assert
            assertFalse(AuthService.isLoggedIn(), "Status setelah logout harus false");
            assertNull(AuthService.getCurrentUser(), "Current user setelah logout harus null");
        }
    }

    @Nested
    @DisplayName("Manajemen Registrasi")
    class RegisterTest {
        @Test
        @DisplayName("Registrasi user baru")
        void testRegisterSuccess() {
            // WHY: Memungkinkan pertumbuhan basis user sistem melalui pendaftaran akun baru secara mandiri
            // Arrange
            String email = "new@user.com";
            String pass = "pass";
            String name = "NewUser";

            // Act
            boolean success = AuthService.register(email, pass, name);

            // Assert
            assertTrue(success, "Registrasi user baru harus berhasil");
            assertNotNull(AuthService.login(email, pass), "User baru harus bisa login");
        }
    }

    @Nested
    @DisplayName("Pengecekan Role dan Session")
    class AuthorizationTest {
        @Test
        @DisplayName("isAdmin mengembalikan true untuk Admin")
        void testIsAdminTrue() {
            // WHY: Memastikan sistem mengenali identitas admin untuk memberikan hak akses penuh
            // Arrange
            AuthService.login("admin@test.com", "admin");

            // Act
            boolean hasil = AuthService.isAdmin();

            // Assert
            assertTrue(hasil, "Admin user harus return true pada isAdmin()");
        }

        @Test
        @DisplayName("isAdmin mengembalikan false untuk User biasa")
        void testIsAdminFalse() {
            // WHY: Memastikan proteksi data tetap terjaga bagi user non-admin
            // Arrange
            AuthService.login(TEST_USER_EMAIL, TEST_USER_PASS);

            // Act
            boolean hasil = AuthService.isAdmin();

            // Assert
            assertFalse(hasil, "Regular user harus return false pada isAdmin()");
        }

        @Test
        @DisplayName("isAdmin mengembalikan false jika belum login")
        void testIsAdminNotLoggedIn() {
            // WHY: Secara default, session kosong tidak boleh memiliki hak administratif apapun
            // Act
            boolean hasil = AuthService.isAdmin();

            // Assert
            assertFalse(hasil, "Tanpa login harus return false pada isAdmin()");
        }

        @Test
        @DisplayName("getCurrentUser mengembalikan user yang tepat")
        void testGetCurrentUser() {
            // WHY: Aplikasi membutuhkan referensi ke objek user yang sedang login untuk fitur personalisasi
            // Arrange
            AuthService.login(TEST_USER_EMAIL, TEST_USER_PASS);

            // Act
            User current = AuthService.getCurrentUser();

            // Assert
            assertNotNull(current, "Current user tidak boleh null setelah login");
            assertEquals(TEST_USER_EMAIL, current.getEmail(), "Email current user harus sesuai");
        }
    }

    @Nested
    @DisplayName("Operasi CRUD User")
    class UserCRUDTest {
        @Test
        @DisplayName("Mendapatkan semua user")
        void testGetAllUsers() {
            // WHY: Admin membutuhkan visibilitas terhadap seluruh user yang terdaftar untuk manajemen akun
            // Act
            List<User> users = AuthService.getAllUsers();

            // Assert
            assertTrue(users.size() >= 2, "Harus ada minimal 2 user dari setup");
        }

        @Test
        @DisplayName("Update data user")
        void testUpdateUser() {
            // WHY: User harus bisa memperbarui data profil mereka dan perubahannya harus tersimpan secara persisten
            // Arrange
            AuthService.login(TEST_USER_EMAIL, TEST_USER_PASS);
            User current = AuthService.getCurrentUser();
            String newUsername = "UpdatedName";
            current.setUsername(newUsername);

            // Act
            AuthService.updateUser(current);
            
            // Verifikasi
            AuthService.logout();
            User updated = AuthService.login(TEST_USER_EMAIL, TEST_USER_PASS);

            // Assert
            assertEquals(newUsername, updated.getUsername(), "Username harus terupdate secara persisten");
        }
    }
}
