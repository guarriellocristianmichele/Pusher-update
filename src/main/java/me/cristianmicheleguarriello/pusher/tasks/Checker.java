package me.cristianmicheleguarriello.pusher.tasks;

import me.cristianmicheleguarriello.pusher.Core;
import me.cristianmicheleguarriello.pusher.database.Database;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Checker implements Runnable {

    private final Database database;

    public Checker(Database database) {
        this.database = database;
    }

    @Override
    public void run() {
        final String licenseKey = database.getString("product_key", "license");

        if (licenseKey == null) return;

        String response = null;

        try {
            response = checkLicense(licenseKey);
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (response == null) return;

        final JSONParser parser = new JSONParser();
        JSONObject obj = null;

        try {
            obj = (JSONObject) parser.parse(response);
        } catch (final ParseException e) {
            e.printStackTrace();
        }

        if (obj == null) return;

        switch (Integer.parseInt(obj.get("isValid").toString())) {

            case 1: {
                Core.getLOGGER().log(Level.INFO, "PUSHER >> License is valid.");
                break;
            }

            case 0: {
                Core.getLOGGER().log(Level.INFO, "PUSHER >> License not valid or not existing.");
                break;
            }

            case -1: {
                Core.getLOGGER().log(Level.INFO, "PUSHER >> License value sent is null.");
                break;
            }

        }

    }

    private String checkLicense(final String license) throws UnsupportedEncodingException {

        final HttpPost post = new HttpPost("https://changelog.lcisoft.it/api/check_license");

        final List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("license", license));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        try (final CloseableHttpClient httpClient = HttpClients.createDefault();
             final CloseableHttpResponse response = httpClient.execute(post)) {

            return EntityUtils.toString(response.getEntity());
        } catch (final IOException ex) {
            return null;
        }

    }

}
