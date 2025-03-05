package logger;

import com.google.gson.GsonBuilder;
import com.intellij.openapi.application.PathManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.google.gson.Gson;

public class InteractionLogger {

    private static final File logFile;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // Static initializer block to set up the log file.
    static {
        System.out.println("InteractionLogger: Setting up log file at " + PathManager.getLogPath());
        logFile = new File(PathManager.getLogPath(), "interactions.json");
        try {
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Private constructor to prevent instantiation.
    private InteractionLogger() {
    }

    public static void log(Map<String, String> data) {
        assert data != null;
        if (!data.containsKey("timestamp")) {
            String formattedDate = dateFormat.format(new Date()); // Get current time in human-readable form
            data.put("timestamp", formattedDate);
        }
        String jsonData = gson.toJson(data); // Convert data to JSON format
        synchronized (InteractionLogger.class) { // Synchronize on the class object for thread safety
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
                writer.write(jsonData);
                writer.newLine(); // Write each log entry on a new line
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
