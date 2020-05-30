package net.cubespace.geSuit.configs;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 24/09/2017.
 */
public class MainConfigTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    File testFile;
    File testFile2;

    @Before
    public void setup() {
        try {
            testFile = folder.newFile("config.yml");
            testFile2 = folder.newFile("bans.yml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void yamlTest() throws Exception {
        MainConfig testConfig = new MainTestConfig(testFile);
        testConfig.init();
        testConfig.save();
        testConfig.MOTD_Enabled = !testConfig.MOTD_Enabled;
        MainConfig newConfig = new MainTestConfig(testFile);
        newConfig.init();
        newConfig.load(testFile);
        assertEquals(newConfig.Seen_Enabled, testConfig.Seen_Enabled);
        assertNotEquals(newConfig.MOTD_Enabled, testConfig.MOTD_Enabled);
        BansConfig bansConfig = new BansConfig(testFile2);
        bansConfig.init();
        bansConfig.save();
        assertEquals(true, bansConfig.GeoIP.ShowOnLogin);
        bansConfig.GeoIP.ShowOnLogin = false;
        bansConfig.save();
        BansConfig newConfig2 = new BansConfig(testFile2);
        newConfig2.load();
        assertEquals(false, newConfig2.GeoIP.ShowOnLogin);

    }

    @Test
    public void load() {
    }

    @After
    public void tearDown() {
        testFile.delete();
    }

    private class MainTestConfig extends MainConfig {

        private MainTestConfig(File file) {
            super(file);
        }
    }

}