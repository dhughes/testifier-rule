package net.doughughes.testifier.service;

import com.google.gson.Gson;
import net.doughughes.testifier.entity.Config;
import net.doughughes.testifier.exception.TiyConfigNotFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by doug on 10/18/16.
 */
public class ConfigService {
    private static Config CONFIG = null;
    private static String HOME = System.getenv("HOME");

    /**
     * This method gets a student's instructor for a student as configured in ~/.tiy-config.
     * @return The instructor's name
     */
    public static String getInstructor() throws TiyConfigNotFoundException {
        return getConfig().getInstructor();
    }

    /**
     * Returns the student's ID as configured in ~/.tiy-config
     * @return The studentId
     * @throws TiyConfigNotFoundException
     */
    public static Long getStudentId() throws TiyConfigNotFoundException {
        return getConfig().getStudentId();
    }

    private static Config getConfig() throws TiyConfigNotFoundException {
        if(CONFIG == null){
            try {
                CONFIG = new Gson().fromJson(
                        new String(Files.readAllBytes(Paths.get(HOME, ".tiy-config"))),
                        Config.class);
            } catch (IOException e) {
                throw new TiyConfigNotFoundException(e);
            }
        }

        return CONFIG;
    }
}
