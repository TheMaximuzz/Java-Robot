package robots.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProfileManager {
    private static final String PROFILES_FILE = "profiles.json";

    public static void saveProfiles(List<Profile> profiles) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(PROFILES_FILE), profiles);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Profile> loadProfiles() {
        File file = new File(PROFILES_FILE);
        if (file.exists()) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                List<Profile> profiles = mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(List.class, Profile.class));
                return profiles;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }
}