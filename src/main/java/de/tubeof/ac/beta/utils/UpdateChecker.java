package de.tubeof.ac.beta.utils;

import de.tubeof.ac.beta.data.Messages;
import de.tubeof.ac.beta.enums.MessageType;
import de.tubeof.ac.beta.main.Main;
import org.bukkit.plugin.Plugin;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {

    private final Messages messages = Main.getMessages();

    private String resourceName;
    private URL resourceRestApiUrl;
    private String currentBuild;
    private String latestBuild;
    private UpdateCheckResult updateCheckResult;

    public UpdateChecker(String resourceName, Plugin plugin) {
        try {
            this.resourceName = resourceName;
            this.resourceRestApiUrl = new URL("https://jenkins.tubeof.de/job/" + resourceName + "/lastSuccessfulBuild/api/json");
        } catch (Exception exception) {
            return;
        }

        fetchLatestBuild();

        currentBuild = messages.getTextMessage(MessageType.BUILD);
        latestBuild = getLatestBuild();

        if (latestBuild == null) {
            updateCheckResult = UpdateCheckResult.NO_RESULT;
            return;
        }

        int currentVersion = Integer.parseInt(currentBuild);
        int latestVersion = Integer.parseInt(getLatestBuild());

        if (currentVersion != latestVersion) updateCheckResult = UpdateCheckResult.OUT_DATED;
        else if (currentVersion == latestVersion) updateCheckResult = UpdateCheckResult.UP_TO_DATE;
        else updateCheckResult = UpdateCheckResult.UNRELEASED;
    }

    public enum UpdateCheckResult {
        NO_RESULT, OUT_DATED, UP_TO_DATE, UNRELEASED,
    }

    public String getResourceName() {
        return resourceName;
    }

    public URL getResourceRestApiUrl() {
        return resourceRestApiUrl;
    }

    public String getCurrentBuild() {
        return currentBuild;
    }

    public String getLatestBuild() {
        return latestBuild;
    }

    public UpdateCheckResult getUpdateCheckResult() {
        return updateCheckResult;
    }

    public void fetchLatestBuild() {
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) getResourceRestApiUrl().openConnection();
            urlConnection.setRequestProperty("User-Agent", "TubeApiBridgeConnector");
            urlConnection.connect();

            int responseCode = urlConnection.getResponseCode();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = bufferedReader.readLine()) != null) {
                response.append(inputLine);
            }
            bufferedReader.close();

            System.out.println(response.toString());

            JSONObject jsonObject = new JSONObject(response.toString());
            System.out.println(jsonObject.getString("id"));

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
