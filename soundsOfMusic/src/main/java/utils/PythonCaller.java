package utils;

import java.io.File;
import java.io.IOException;

public class PythonCaller {
    public static int executePythonScipt(String scriptPath, String args) throws Exception {
        String pythonExecutable = getPythonExecutable();
        System.out.println("python is: "+ pythonExecutable);
        if (pythonExecutable == "none") {
            throw new Exception("No python executable");
        }

        ProcessBuilder processBuilder = new ProcessBuilder(pythonExecutable, scriptPath, args);
        processBuilder.redirectErrorStream(true);
        processBuilder.redirectOutput(new File("out.txt"));

        Process process = processBuilder.start();

        return process.waitFor();
    }

    public static String getPythonExecutable() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("python", "--version");
        Process process = processBuilder.start();

        if (process.waitFor() == 0) {
            return "python";
        }

        processBuilder = new ProcessBuilder("python3",  "--version");
        process = processBuilder.start();

        if (process.waitFor() == 0) {
            return "python3";
        }

        return "none";
    }
}