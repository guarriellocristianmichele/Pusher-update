package me.cristianmicheleguarriello.pusher.files;

import me.cristianmicheleguarriello.pusher.Core;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;

public class FileManager {

    private final File file;

    public FileManager(final String file) {
        this.file = new File(file);
    }

    public File createFile() {

        if (!this.file.exists()) {

            Core.getLOGGER().log(Level.INFO, "PUSHER >> " + this.file.getName() + " not found");

            try {
                this.file.createNewFile();
            } catch (final IOException e) {
                e.printStackTrace();
            }

            Core.getLOGGER().log(Level.INFO, "PUSHER >> " + this.file.getName() + " has been created, please set it");
        }

        return this.file;
    }

    public void createConfigFile() {
        if (this.file.exists()) return;
        this.createFile();
        this.writeDefaultValues();
    }

    public HashMap<String, String> getConfigHashmapValues() throws IOException {

        if (FileUtils.readFileToString(this.file, "UTF-8").trim().isEmpty()) {
            this.writeDefaultValues();
        }

        final HashMap<String, String> database = new HashMap<>();
        final JSONParser jsonParser = new JSONParser();

        try (final FileReader reader = new FileReader(this.file)) {

            final Object obj = jsonParser.parse(reader);
            final JSONObject databaseObject = (JSONObject) obj;

            database.put("host", (String) databaseObject.get("host"));
            database.put("port", (String) databaseObject.get("port"));
            database.put("database", (String) databaseObject.get("database"));
            database.put("username", (String) databaseObject.get("username"));
            database.put("password", (String) databaseObject.get("password"));

        } catch (final IOException | ParseException e) {
            e.printStackTrace();
        }

        return database;
    }

    private void writeDefaultValues() {
        final JSONObject defaultValues = new JSONObject();
        defaultValues.put("host", "");
        defaultValues.put("port", "");
        defaultValues.put("database", "");
        defaultValues.put("username", "");
        defaultValues.put("password", "");

        try (final FileWriter jsonFile = new FileWriter(this.file)) {
            jsonFile.write(defaultValues.toJSONString());
            jsonFile.flush();

        } catch (final IOException e) {
            Core.getLOGGER().log(Level.INFO, "PUSHER >> Error: ", e);
            return;
        }

        Core.getLOGGER().log(Level.INFO, "PUSHER >> Added default values to " + this.file.getName());
    }

}
