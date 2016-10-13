package net.doughughes.testifier.util;

import net.doughughes.testifier.exception.NoSuchRepositoryException;
import net.doughughes.testifier.exception.NotAGitRepositoryException;
import org.apache.commons.io.IOUtils;
import java.io.IOException;

public class GitService {

    public static String getGitUserName(){

        try {
            Process process = Runtime.getRuntime().exec("git config user.name");
            return IOUtils.toString(process.getInputStream()).trim();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Unknown username";
    }

    public static String getGitEmail() {

        try {
            Process process = Runtime.getRuntime().exec("git config user.email");
            return IOUtils.toString(process.getInputStream()).trim();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Unknown email address";
    }

    public static String getOriginUrl(){
        String output = null;
        try {
            Process process = Runtime.getRuntime().exec("git remote get-url origin");
            String error = IOUtils.toString(process.getErrorStream()).trim();
            if(error.matches(".*?No such remote.*?")){
                throw new NoSuchRepositoryException();
            } else if(error.matches(".*?Not a git repository.*?")){
                throw new NotAGitRepositoryException();
            }
            output = IOUtils.toString(process.getInputStream()).trim();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchRepositoryException e) {
            return "No origin";
        } catch (NotAGitRepositoryException e) {
            return "Not a Git repository";
        }

        return output;
    }
}
