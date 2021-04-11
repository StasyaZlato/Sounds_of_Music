package utils;

import java.io.File;
import java.io.IOException;

public class PythonCaller {
    public static int executePythonScipt(String scriptPath, String args) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("python", scriptPath, args);
        processBuilder.redirectErrorStream(true);
        processBuilder.redirectOutput(new File("out.txt"));

        Process process = processBuilder.start();

        return process.waitFor();
    }
}