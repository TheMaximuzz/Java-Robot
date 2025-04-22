package robots.profile;

import org.junit.jupiter.api.*;
import robots.gui.GameVisualizer;
import robots.gui.GameWindow;
import robots.gui.LogWindow;
import robots.gui.MainApplicationFrame;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.jupiter.api.Assertions.*;
import robots.log.LogWindowSource;


public class ProfilesTest {
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
        // сохраняем оригинальный profiles.json, если он существует
        profilesFile = new File(PROFILES_FILE);
        backupProfilesFile = new File("profiles_backup.json");
        if (profilesFile.exists()) {
            Files.copy(profilesFile.toPath(), backupProfilesFile.toPath());
            profilesFile.delete();
        }

        // инициализируем окна
        SwingUtilities.invokeAndWait(() -> {
            testLocale = new Locale("ru", "RU");
            mainFrame = new MainApplicationFrame(null);
            mainFrame.setBounds(100, 100, 800, 600);
            mainFrame.setExtendedState(Frame.MAXIMIZED_BOTH);

            logSource = new LogWindowSource(100);
            logWindow = new LogWindow(logSource);
            logWindow.setBounds(200, 200, 300, 150);
            logWindow.setVisible(true);

            gameWindow = new GameWindow(testLocale);
            gameWindow.setBounds(300, 300, 400, 200);
            gameWindow.setVisible(true);


            GameVisualizer visualizer = (GameVisualizer) ((JPanel) gameWindow.getContentPane().getComponent(0)).getComponent(0);
            visualizer.setRobotPosition(5, 5);
            List<Profile.MobPosition> mobs = new ArrayList<>();
            mobs.add(new Profile.MobPosition(10, 10));
            mobs.add(new Profile.MobPosition(15, 15));
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


    // 1: Тест на сохранение и загрузку профиля
    @Test
    void testProfileSaveAndLoad() throws Exception {
        // создаём и сохраняем профиль
        Profile profile = new Profile(1, mainFrame, logWindow, gameWindow, testLocale);
        List<Profile> profiles = new ArrayList<>();
        profiles.add(profile);
        ProfileManager.saveProfiles(profiles);

        // загрузка профиля
        List<Profile> loadedProfiles = ProfileManager.loadProfiles();
        assertEquals(1, loadedProfiles.size(), "Должен быть загружен ровно один профиль");
        Profile loadedProfile = loadedProfiles.get(0);

        // совпадают ли данные
        assertEquals(1, loadedProfile.getProfileNumber());
        assertEquals(mainFrame.getX(), loadedProfile.getMainX());
        assertEquals(mainFrame.getY(), loadedProfile.getMainY());
        assertTrue(loadedProfile.isMainMaximized());
        assertEquals(logWindow.getX(), loadedProfile.getLogNormalX());
        assertTrue(loadedProfile.isLogVisible());
        assertEquals(gameWindow.getWidth(), loadedProfile.getGameNormalWidth());
        assertEquals(5, loadedProfile.getPlayerX());
        assertEquals(5, loadedProfile.getPlayerY());
        assertEquals(2, loadedProfile.getMobPositions().size());
        assertEquals(10, loadedProfile.getMobPositions().get(0).getMobX());
        assertEquals("ru", loadedProfile.getLocale().getLanguage());
        assertEquals("RU", loadedProfile.getLocale().getCountry());
    }


    // 2: Тест на восстановление состояния игры
    @Test
    void testGameStateRestoration() throws Exception {
        // создание, сохранение и загрузка профиля
        Profile profile = new Profile(1, mainFrame, logWindow, gameWindow, testLocale);
        List<Profile> profiles = new ArrayList<>();
        profiles.add(profile);
        ProfileManager.saveProfiles(profiles);
        List<Profile> loadedProfiles = ProfileManager.loadProfiles();
        Profile loadedProfile = loadedProfiles.get(0);

        // создаём новое игровое окно и восстанавливаем состояние
        AtomicReference<GameWindow> newGameWindow = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> {
            newGameWindow.set(new GameWindow(testLocale));
            newGameWindow.get().setPlayerPosition(loadedProfile.getPlayerX(), loadedProfile.getPlayerY());
            newGameWindow.get().setMobPositions(loadedProfile.getMobPositions());
        });

        // восстановилось ли корректно
        assertEquals(loadedProfile.getPlayerX(), newGameWindow.get().getPlayerX());
        assertEquals(loadedProfile.getPlayerY(), newGameWindow.get().getPlayerY());
        List<Profile.MobPosition> restoredMobs = newGameWindow.get().getMobPositions();
        assertEquals(loadedProfile.getMobPositions().size(), restoredMobs.size());
        for (int i = 0; i < restoredMobs.size(); i++) {
            assertEquals(loadedProfile.getMobPositions().get(i).getMobX(), restoredMobs.get(i).getMobX());
            assertEquals(loadedProfile.getMobPositions().get(i).getMobY(), restoredMobs.get(i).getMobY());
        }
    }


    // 3: Сохранения и загрузки нескольких профилей
    @Test
    void testMultipleProfilesSaveAndLoad() throws Exception {
        Profile profile1 = new Profile(1, mainFrame, logWindow, gameWindow, testLocale);

        // второй профиль уже с другими данными
        SwingUtilities.invokeAndWait(() -> {
            mainFrame.setBounds(150, 150, 900, 700);
            mainFrame.setExtendedState(Frame.NORMAL);
            logWindow.setVisible(false);
            gameWindow.setBounds(350, 350, 450, 250);
            GameVisualizer visualizer = (GameVisualizer) ((JPanel) gameWindow.getContentPane().getComponent(0)).getComponent(0);
            visualizer.setRobotPosition(7, 7);
            List<Profile.MobPosition> mobs = new ArrayList<>();
            mobs.add(new Profile.MobPosition(12, 12));
            mobs.add(new Profile.MobPosition(18, 18));
            visualizer.setEnemyPositions(mobs);
        });
        Profile profile2 = new Profile(2, mainFrame, logWindow, gameWindow, testLocale);

        List<Profile> profiles = new ArrayList<>();
        profiles.add(profile1);
        profiles.add(profile2);
        ProfileManager.saveProfiles(profiles);

        List<Profile> loadedProfiles = ProfileManager.loadProfiles();
        assertEquals(2, loadedProfiles.size());

        Profile loadedProfile1 = loadedProfiles.stream()
                .filter(p -> p.getProfileNumber() == 1)
                .findFirst()
                .orElse(null);
        assertNotNull(loadedProfile1);
        assertEquals(1, loadedProfile1.getProfileNumber());
        assertTrue(loadedProfile1.isMainMaximized());
        assertTrue(loadedProfile1.isLogVisible());
        assertEquals(5, loadedProfile1.getPlayerX());

        Profile loadedProfile2 = loadedProfiles.stream()
                .filter(p -> p.getProfileNumber() == 2)
                .findFirst()
                .orElse(null);
        assertNotNull(loadedProfile2);
        assertEquals(2, loadedProfile2.getProfileNumber());
        assertFalse(loadedProfile2.isMainMaximized());
        assertFalse(loadedProfile2.isLogVisible());
        assertEquals(7, loadedProfile2.getPlayerX());
        assertEquals(450, loadedProfile2.getGameNormalWidth());
    }


    // 4: Изменение состояний после создания профиля
    @Test
    void testStateChangeAfterProfileCreation() throws Exception {
        Profile profile1 = new Profile(1, mainFrame, logWindow, gameWindow, testLocale);

        // меняем первый профиль
        SwingUtilities.invokeAndWait(() -> {
            mainFrame.setBounds(200, 200, 1000, 800);
            logWindow.setVisible(false);
            GameVisualizer visualizer = (GameVisualizer) ((JPanel) gameWindow.getContentPane().getComponent(0)).getComponent(0);
            visualizer.setRobotPosition(8, 8);
        });

        // создаём второй профиль, чтобы убедиться, что меняя данные одного профиля, у второго они не изменятся
        Profile profile2 = new Profile(2, mainFrame, logWindow, gameWindow, testLocale);
        List<Profile> profiles = new ArrayList<>();
        profiles.add(profile1);
        profiles.add(profile2);
        ProfileManager.saveProfiles(profiles);

        List<Profile> loadedProfiles = ProfileManager.loadProfiles();
        Profile loadedProfile1 = loadedProfiles.stream()
                .filter(p -> p.getProfileNumber() == 1)
                .findFirst()
                .orElse(null);
        Profile loadedProfile2 = loadedProfiles.stream()
                .filter(p -> p.getProfileNumber() == 2)
                .findFirst()
                .orElse(null);


        assertEquals(800, loadedProfile1.getMainWidth());
        assertTrue(loadedProfile1.isLogVisible());
        assertEquals(5, loadedProfile1.getPlayerX());

        assertEquals(1000, loadedProfile2.getMainWidth());
        assertFalse(loadedProfile2.isLogVisible());
        assertEquals(8, loadedProfile2.getPlayerX());
    }

    // 5: Сохранение и загрузка максимизированного состояния окон
    @Test
    void testMaximizedStateSaveAndLoad() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            mainFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
            logWindow.setMaximized(true);
            gameWindow.setMaximized(true);
        });

        Profile profile = new Profile(1, mainFrame, logWindow, gameWindow, testLocale);
        List<Profile> profiles = new ArrayList<>();
        profiles.add(profile);
        ProfileManager.saveProfiles(profiles);

        List<Profile> loadedProfiles = ProfileManager.loadProfiles();
        assertEquals(1, loadedProfiles.size());
        Profile loadedProfile = loadedProfiles.get(0);

        assertTrue(loadedProfile.isMainMaximized());
        assertTrue(loadedProfile.isLogMaximized());
        assertTrue(loadedProfile.isGameMaximized());
    }

    // 6: Сохранение и загрузка свёрнутого состояния окон
    @Test
    void testIconifiedStateSaveAndLoad() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            mainFrame.setExtendedState(Frame.ICONIFIED);
            try {
                logWindow.setIcon(true);
                gameWindow.setIcon(true);
            } catch (Exception e) {
                fail("Не получилось установить свёрнутое состояние окон: " + e.getMessage());
            }
        });

        Profile profile = new Profile(1, mainFrame, logWindow, gameWindow, testLocale);
        List<Profile> profiles = new ArrayList<>();
        profiles.add(profile);
        ProfileManager.saveProfiles(profiles);

        List<Profile> loadedProfiles = ProfileManager.loadProfiles();
        assertEquals(1, loadedProfiles.size());
        Profile loadedProfile = loadedProfiles.get(0);

        assertTrue(loadedProfile.isMainIconified());
        assertTrue(loadedProfile.isLogIconified());
        assertTrue(loadedProfile.isGameIconified());
    }
}