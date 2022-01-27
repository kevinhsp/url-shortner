package theBigShortners.urlService;

import java.awt.*;
import java.net.URI;
import java.util.Random;

public class Util {

    private static final String acceptableChars = "aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ0123456789";

    public static String keyGen(int length) {
        StringBuilder key = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            key.append(acceptableChars.charAt(random.nextInt(acceptableChars.length())));
        }
        return key.toString();
    }

    public static void browse(String url) {
        System.setProperty("java.awt.headless", "false");
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Browse website failed, desktop not supported.");
        }
    }

}
