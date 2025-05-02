import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {
    private final Properties props = new Properties();

    public ConfigLoader(String configPath) throws IOException {
        try (FileInputStream fis = new FileInputStream(configPath)) {
            props.load(fis);
        }
    }

    public boolean isSsoEnabled() {
        return Boolean.parseBoolean(props.getProperty("sso", "false"));
    }

    public String getServerName() {
        return props.getProperty("serverName", "localhost");
    }

    public String getDatabaseName() {
        return props.getProperty("databaseName", "TestDB");
    }
}
