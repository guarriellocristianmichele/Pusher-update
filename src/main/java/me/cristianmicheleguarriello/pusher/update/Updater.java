package me.cristianmicheleguarriello.pusher.update;

import me.cristianmicheleguarriello.pusher.Core;
import me.cristianmicheleguarriello.pusher.api.APIQuery;
import me.cristianmicheleguarriello.pusher.database.Database;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.logging.Level;

public class Updater {

    private final String license;
    private final Database database;

    public Updater(String license, Database database) throws UnsupportedEncodingException {
        this.license = license;
        this.database = database;
        this.checker();
    }

    public void checker() throws UnsupportedEncodingException {
        final String softwareVersion = database.getString("about", "version");

        HashMap<String, String> data = new HashMap<>();
        data.put("license", license);

        APIQuery api = new APIQuery();
        String response = api.query("check_latest_version", data);

        final JSONParser parser = new JSONParser();
        JSONObject obj;

        try {
            obj = (JSONObject) parser.parse(response);
        } catch (final ParseException e) {
            Core.getLOGGER().log(Level.INFO, "PUSHER >> Cannot parse, exception caught: " + e.getMessage());
            return;
        }

        if (obj == null) return;

        String latestVersion = obj.get("version").toString();

        if (softwareVersion.trim().equals(latestVersion.trim())) {
            Core.getLOGGER().log(Level.INFO, "PUSHER >> Software already updated.");
            return;
        }

        if (this.update()) {
            Core.getLOGGER().log(Level.INFO, "PUSHER >> Software updated successfully.");
            return;
        }

        Core.getLOGGER().log(Level.INFO, "PUSHER >> Software not updated.");
    }

    private boolean update() throws UnsupportedEncodingException {

        File updatesCategory = new File("updates");

        if (!updatesCategory.exists()) {
            updatesCategory.mkdir();
        }

        HashMap<String, String> data = new HashMap<>();
        data.put("license", license);

        APIQuery api = new APIQuery();
        File update = api.queryFile("download_update", data, updatesCategory + "/update.zip");
        return true;
    }

}
