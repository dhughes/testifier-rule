package net.doughughes.testifier.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by doug on 10/18/16.
 */
public class Instructor {
    /**
     * This method attempts to identify an instructor for a class.
     * @return The instructor's name
     */
    public static String identify() {
        String instructor = "Unknown Instructor";

        String home = System.getenv("HOME");

        if(home != null){
            try {
                instructor = new String(Files.readAllBytes(Paths.get(home, ".tiy-instructor")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return instructor.trim();
    }
}
