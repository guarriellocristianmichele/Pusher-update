package me.cristianmicheleguarriello.pusher;

import me.cristianmicheleguarriello.pusher.database.Database;
import me.cristianmicheleguarriello.pusher.files.FileManager;
import me.cristianmicheleguarriello.pusher.tasks.Checker;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Core {

    private final static Logger LOGGER = Logger.getLogger(Core.class.getName());

    public static void main(String[] args) throws IOException {

        final FileManager config = new FileManager("config.json");
        config.createConfigFile();

        final HashMap<String, String> databaseValues = config.getConfigHashmapValues();

        if (databaseValues.get("host").equals("") || databaseValues.get("port").equals("") || databaseValues.get("database").equals("") || databaseValues.get("username").equals("") || databaseValues.get("password").equals("")) {
            LOGGER.log(Level.INFO, "PUSHER >> Please fill the fields in config.json");
            return;
        }

        if (args.length != 1) {
            LOGGER.log(Level.INFO, "PUSHER >> Use java -jar <jar> <minutes>");
            return;
        }

        if (!StringUtils.isNumeric(args[0])) {
            LOGGER.log(Level.INFO, "PUSHER >> The first argument inserted is not a number.");
            return;
        }

        final int minutes = Integer.parseInt(args[0]);

        if (minutes < 5 || minutes > 60) {
            LOGGER.log(Level.INFO, "PUSHER >> You can set from 5 to 60 minutes.");
            return;
        }

        final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        final Database database = new Database(databaseValues.get("host"), Short.parseShort(databaseValues.get("port")), databaseValues.get("database"), databaseValues.get("username"), databaseValues.get("password"));

        executorService.scheduleAtFixedRate(new Checker(database), 0, minutes, TimeUnit.MINUTES);
    }

    public static Logger getLOGGER() {
        return LOGGER;
    }

}
