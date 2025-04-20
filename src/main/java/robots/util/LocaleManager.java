package robots.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.util.Locale;

public class LocaleManager {
    private static final String LOCALE_FILE = "lastLocale.json";

    public static void saveLastLocale(Locale locale) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(LOCALE_FILE), locale.getLanguage() + "_" + locale.getCountry());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Locale loadLastLocale() {
        File file = new File(LOCALE_FILE);
        if (file.exists()) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                String localeString = mapper.readValue(file, String.class);
                String[] parts = localeString.split("_");
                Locale locale = parts.length == 2 ? new Locale(parts[0], parts[1]) : new Locale(parts[0]);
                return locale;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Locale.getDefault();
    }
}