package io.github.feelfreelinux.wordling.utils;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import io.github.feelfreelinux.wordling.Values;
import io.github.feelfreelinux.wordling.objects.Wordpack;

public class GithubGistUploader extends AsyncTask<Wordpack, Void, String> {
        private String sendPost(Pair<String, String> data) {
            StringBuilder content = new StringBuilder();
            try {
                // Send POST request
                URL url = new URL(data.first);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");

                OutputStream os = conn.getOutputStream();
                os.write(data.second.getBytes());
                os.flush();

                if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED &&
                        conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + conn.getResponseCode());
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));

                String line;

                while ((line = br.readLine()) != null)
                    content.append(line + "\n");
                conn.disconnect();
                return content.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }

        @Override
        protected String doInBackground(Wordpack... data) {
            try {
                // Create JSON post object
                JSONObject postData = new JSONObject();
                JSONObject files = new JSONObject();
                files.put("wordpack.json", new JSONObject().put("content", data[0].toJSONString(false)));
                postData.put("description", data[0].getDescription());
                postData.put("public", false);
                postData.put("files", files);

                JSONObject githubResult = new JSONObject(sendPost(new Pair<>("https://api.github.com/gists", postData.toString()))).getJSONObject("files");

                if (githubResult.has("wordpack.json")) {
                    String rawUrl = githubResult.getJSONObject("wordpack.json").getString("raw_url");

                    JSONObject gData = new JSONObject();
                    gData.put("longUrl", rawUrl);

                    String shortenerReply = sendPost(new Pair<>("https://www.googleapis.com/urlshortener/v1/url?key=" + Values.GKey,
                            gData.toString()));

                    JSONObject shortenedData = new JSONObject(shortenerReply);
                    return shortenedData.getString("id");
                }
                return "";
            } catch(Exception e) {
                e.printStackTrace();
                Log.v("Wordling", "Error uploading wordpack to web" + e.getMessage());
            }
            return "";
        }
}