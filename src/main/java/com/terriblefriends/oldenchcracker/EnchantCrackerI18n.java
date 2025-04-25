package com.terriblefriends.oldenchcracker;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Scanner;

public class EnchantCrackerI18n {
    private static final ResourceBundle I18N = ResourceBundle.getBundle("i18n.lang", new UTF8ResourceBundleControl());

    public static String translate(String key) {
        if (I18N.containsKey(key)) {
            return I18N.getString(key).trim();
        }
        else {
            System.err.println("Failed to find entry for translation key " + key + "!");
            return key;
        }
    }

    private static class UTF8ResourceBundleControl extends ResourceBundle.Control {
        @Override
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader,
                                        boolean reload) throws IOException {
            String bundleName = this.toBundleName(baseName, locale);
            String resourceName = this.toResourceName(bundleName, "txt");
            PropertyResourceBundle bundle = null;
            InputStream stream = null;
            if (reload) {
                URLConnection connection;
                URL url = loader.getResource(resourceName);
                if (url != null && (connection = url.openConnection()) != null) {
                    connection.setUseCaches(false);
                    stream = connection.getInputStream();
                }
            } else {
                stream = loader.getResourceAsStream(resourceName);
            }
            if (stream != null) {
                String utf8Text;
                try (Scanner scanner = new Scanner(stream, "UTF-8").useDelimiter("\\A");) {
                    utf8Text = scanner.hasNext() ? scanner.next() : "";
                }
                StringBuilder escapedText = new StringBuilder(utf8Text.length());
                for (int i = 0; i < utf8Text.length(); ++i) {
                    char c = utf8Text.charAt(i);
                    if (c < 0x80) {
                        escapedText.append(c);
                        continue;
                    }
                    escapedText.append(String.format("\\u%04x", c));
                }
                bundle = new PropertyResourceBundle(new StringReader(escapedText.toString()));
            }
            return bundle;
        }
    }
}
