package me.cristianmicheleguarriello.pusher.api;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class APIQuery {

    public String query(String apiRequest, HashMap<String, String> data) throws UnsupportedEncodingException {
        final HttpPost post = new HttpPost("https://changelog.lcisoft.it/api/" + apiRequest);

        final List<NameValuePair> urlParameters = new ArrayList<>();

        for (Map.Entry<String, String> postData : data.entrySet()) {
            urlParameters.add(new BasicNameValuePair(postData.getKey(), postData.getValue()));
        }

        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        try (final CloseableHttpClient httpClient = HttpClients.createDefault();
             final CloseableHttpResponse response = httpClient.execute(post)) {

            return EntityUtils.toString(response.getEntity());
        } catch (final IOException ex) {
            return null;
        }

    }

    public File queryFile(String apiRequest, HashMap<String, String> data, String filename) throws UnsupportedEncodingException {
        final HttpPost post = new HttpPost("https://changelog.lcisoft.it/api/" + apiRequest);

        final List<NameValuePair> urlParameters = new ArrayList<>();

        for (Map.Entry<String, String> postData : data.entrySet()) {
            urlParameters.add(new BasicNameValuePair(postData.getKey(), postData.getValue()));
        }

        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        try {
            final CloseableHttpClient httpClient = HttpClients.createDefault();
            final CloseableHttpResponse response = httpClient.execute(post);

            File file = new File(filename);

            BufferedInputStream reader = new BufferedInputStream(response.getEntity().getContent());
            BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(file));

            int inByte;

            while ((inByte = reader.read()) != -1) writer.write(inByte);

            writer.flush();
            writer.close();
            reader.close();

            return file;
        } catch (final IOException ex) {
            return null;
        }

    }

}
