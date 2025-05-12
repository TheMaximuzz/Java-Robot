package robots.profile;

import org.junit.jupiter.api.*;
import robots.gui.GameVisualizer;
import robots.gui.GameWindow;
import robots.gui.LogWindow;
import robots.gui.MainApplicationFrame;
import robots.log.LogWindowSource;

import javax.swing.*;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class ProfileStateTest {
    private static final String PROFILES_FILE = "profiles.json";
    private File profilesFile;
    private File backupProfilesFile;
    private MainApplicationFrame mainFrame;
    private LogWindow logWindow;
    private GameWindow gameWindow;
    private Locale testLocale;
    private LogWindowSource logSource;

    @BeforeEach
    void setUp() throws Exception {
        profilesFile = new File(PROFILES_FILE);
        backupProfilesFile = new File("profiles_backup.json");
        if (profilesFile.exists()) {
            Files.copy(profilesFile.toPath(), backupProfilesFile.toPath());
            profilesFile.delete();
        }

        SwingUtilities.invokeAndWait(() -> {
            testLocale = new Locale("en", "US");
            mainFrame = new MainApplicationFrame(null);
            mainFrame.setBounds(100, 100, 800, 600);

            logSource = new LogWindowSource(100);
            logWindow = new LogWindow(logSource);
            logWindow.setBounds(200, 200, 300, 200);
            logWindow.setVisible(true);

            gameWindow = new GameWindow(testLocale);
            gameWindow.setBounds(300, 300, 400, 300);
            gameWindow.setVisible(true);

            GameVisualizer visualizer = (GameVisualizer) ((JPanel) gameWindow.getContentPane().getComponent(0)).getComponent(0);
            visualizer.setRobotPosition(10, 10);
            List<Profile.MobPosition> mobs = new ArrayList<>();
            mobs.add(new Profile.MobPosition(20, 20));
            visualizer.setEnemyPositions(mobs);
        });
    }

    @AfterEach
    void tearDown() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            mainFrame.dispose();
            logWindow.dispose();
            gameWindow.dispose();
        });
        if (profilesFile.exists()) {
            profilesFile.delete();
        }
        if (backupProfilesFile.exists()) {
            Files.copy(backupProfilesFile.toPath(), profilesFile.toPath());
            backupProfilesFile.delete();
        }
    }

    // 1: Проверка сохранения состояний максимизации
    @Test
    void testMaximizedState() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            try {
                logWindow.setMaximized(true);
                gameWindow.setMaximized(true);
            } catch (Exception e) {
                fail("Не получилось максимизировать окна: " + e.getMessage());
            }
        });

        Profile profile = new Profile(1, mainFrame, logWindow, gameWindow, testLocale);
        List<Profile> profiles = new ArrayList<>();
        profiles.add(profile);
        ProfileManager.saveProfiles(profiles);

        List<Profile> loadedProfiles = ProfileManager.loadProfiles();
        assertEquals(1, loadedProfiles.size());
        Profile loadedProfile = loadedProfiles.get(0);

        assertTrue(loadedProfile.isLogMaximized());
        assertTrue(loadedProfile.isGameMaximized());
        assertFalse(loadedProfile.isLogIconified());
        assertFalse(loadedProfile.isGameIconified());
    }

    // 2: Проверка сохранения состояний сворачивания
    @Test
    void testIconifiedState() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            try {
                logWindow.setIcon(true);
                gameWindow.setIcon(true);
            } catch (Exception e) {
                fail("Не получилось свернуть окна: " + e.getMessage());
            }
        });

        Profile profile = new Profile(1, mainFrame, logWindow, gameWindow, testLocale);
        List<Profile> profiles = new ArrayList<>();
        profiles.add(profile);
        ProfileManager.saveProfiles(profiles);

        List<Profile> loadedProfiles = ProfileManager.loadProfiles();
        assertEquals(1, loadedProfiles.size());
        Profile loadedProfile = loadedProfiles.get(0);

        assertTrue(loadedProfile.isLogIconified());
        assertTrue(loadedProfile.isGameIconified());
        assertFalse(loadedProfile.isLogMaximized());
        assertFalse(loadedProfile.isGameMaximized());
    }

    // 3: Проверка отложенной максимизации
    @Test
    void testPendingMaximization() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            try {
                logWindow.setMaximized(true);
                gameWindow.setMaximized(true);
            } catch (Exception e) {
                fail("Не получилось максимизировать окна: " + e.getMessage());
            }
            try {
                logWindow.setIcon(true);
                gameWindow.setIcon(true);
            } catch (Exception e) {
                fail("Не получилось свернуть окна: " + e.getMessage());
            }
        });

        Profile profile = new Profile(1, mainFrame, logWindow, gameWindow, testLocale);
        List<Profile> profiles = new ArrayList<>();
        profiles.add(profile);
        ProfileManager.saveProfiles(profiles);

        List<Profile> loadedProfiles = ProfileManager.loadProfiles();
        assertEquals(1, loadedProfiles.size());
        Profile loadedProfile = loadedProfiles.get(0);

        assertTrue(loadedProfile.isLogIconified());
        assertTrue(loadedProfile.isGameIconified());
        assertTrue(loadedProfile.wasLogMaximizedBeforeIconify());
        assertTrue(loadedProfile.wasGameMaximizedBeforeIconify());
        assertTrue(loadedProfile.isLogPendingMaximize());
        assertTrue(loadedProfile.isGamePendingMaximize());
        assertTrue(loadedProfile.shouldLogMaximizeOnDeiconify());
        assertTrue(loadedProfile.shouldGameMaximizeOnDeiconify());
    }

    // 4: Изменение координат + отложенная максимизация
    @Test
    void testHardestSerialization() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            logWindow.setBounds(111, 222, 444, 333);
            gameWindow.setBounds(333, 333, 555, 444);
            try {
                logWindow.setMaximized(true);
                gameWindow.setMaximized(true);
            } catch (Exception e) {
                fail("Не получилось максимизировать окна: " + e.getMessage());
            }
            try {
                logWindow.setIcon(true);
                gameWindow.setIcon(true);
            } catch (Exception e) {
                fail("Не получилось свернуть окна: " + e.getMessage());
            }
        });

        Profile profile = new Profile(1, mainFrame, logWindow, gameWindow, testLocale);
        List<Profile> profiles = new ArrayList<>();
        profiles.add(profile);
        ProfileManager.saveProfiles(profiles);

        List<Profile> loadedProfiles = ProfileManager.loadProfiles();
        assertEquals(1, loadedProfiles.size());
        Profile loadedProfile = loadedProfiles.get(0);

        assertTrue(loadedProfile.isLogIconified());
        assertTrue(loadedProfile.isGameIconified());
        assertEquals(111, loadedProfile.getLogNormalX());
        assertEquals(222, loadedProfile.getLogNormalY());
        assertEquals(444, loadedProfile.getLogNormalWidth());
        assertEquals(333, loadedProfile.getLogNormalHeight());
        assertEquals(333, loadedProfile.getGameNormalX());
        assertEquals(333, loadedProfile.getGameNormalY());
        assertEquals(555, loadedProfile.getGameNormalWidth());
        assertEquals(444, loadedProfile.getGameNormalHeight());
    }
}