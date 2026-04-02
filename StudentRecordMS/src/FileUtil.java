

import java.io.BufferedReader;   
import java.io.BufferedWriter;   
import java.io.IOException;     
import java.nio.file.Files;      
import java.nio.file.Path;       
import java.nio.file.Paths;     
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;      
import java.util.List;           

public class FileUtil {


    public static final String DATA_FOLDER = "data/";


    public static void ensureFileExists(String filename) throws IOException {
        Path path = Paths.get(DATA_FOLDER + filename);

        // Check if the data folder exists — if not, create it
        if (!Files.exists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }

        // Check if the file exists — if not, create an empty file
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
    }


    public static List<String> readAllLines(String filename) throws IOException {
        ensureFileExists(filename); // Make sure the file exists first
        Path path = Paths.get(DATA_FOLDER + filename);
        List<String> lines = new ArrayList<>();

        // Open the file and read it line by line
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) { // Read until end of file
                if (!line.trim().isEmpty()) {            // Skip empty lines
                    lines.add(line);                     // Add line to our list
                }
            }
        }
        return lines; // Return the complete list of lines
    }


    public static void writeLine(String filename, String line, boolean append)
            throws IOException {
        ensureFileExists(filename);
        Path path = Paths.get(DATA_FOLDER + filename);

        // Choose whether to append or overwrite
        StandardOpenOption option = append
                ? StandardOpenOption.APPEND       
                : StandardOpenOption.TRUNCATE_EXISTING; 

        try (BufferedWriter writer = Files.newBufferedWriter(path, option)) {
            writer.write(line);   
            writer.newLine();      
        }
    }


    public static void writeAllLines(String filename, List<String> lines)
            throws IOException {
        ensureFileExists(filename);
        Path path = Paths.get(DATA_FOLDER + filename);

        
        try (BufferedWriter writer = Files.newBufferedWriter(
                path, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (String line : lines) { 
                writer.write(line);      
                writer.newLine();        
            }
        }
    }


    public static void clearFile(String filename) throws IOException {
        ensureFileExists(filename);
        Path path = Paths.get(DATA_FOLDER + filename);

        
        try (BufferedWriter writer = Files.newBufferedWriter(
                path, StandardOpenOption.TRUNCATE_EXISTING)) {
            
        }
    }
}