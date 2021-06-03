package com.yourgamespace.anticooldown.beta.utils;

import com.yourgamespace.anticooldown.beta.main.Main;
import org.bukkit.plugin.Plugin;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {

    private String resourceName;
    private URL resourceRestApiUrl;
    private Integer currentBuild;
    private Integer latestBuild;
    private UpdateCheckResult updateCheckResult;

    public UpdateChecker(String resourceName, Plugin plugin) {
        try {
            this.resourceName = resourceName;
            this.resourceRestApiUrl = new URL("https://hub.tubeof.de/jenkins/job/" + resourceName + "/lastSuccessfulBuild/api/json");
        } catch (Exception exception) {
            return;
        }

        currentBuild = Integer.valueOf(Main.getMain().getDescription().getVersion());
        latestBuild = fetchLatestBuild();

        if (latestBuild == null) {
            updateCheckResult = UpdateCheckResult.NO_RESULT;
            return;
        }

        int currentVersion = currentBuild;
        int latestVersion = latestBuild;

        if (currentBuild != getLatestBuild()) updateCheckResult = UpdateCheckResult.OUT_DATED;
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

    public Integer getCurrentBuild() {
        return currentBuild;
    }

    public Integer getLatestBuild() {
        return latestBuild;
    }

    public UpdateCheckResult getUpdateCheckResult() {
        return updateCheckResult;
    }

    public Integer fetchLatestBuild() {
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) getResourceRestApiUrl().openConnection();
            urlConnection.setRequestProperty("User-Agent", "TubeApiBridgeConnector");
            urlConnection.setRequestProperty("Header-Token", "SD998FS0FG07");
            urlConnection.connect();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = bufferedReader.readLine()) != null) {
                response.append(inputLine);
            }
            bufferedReader.close();

            JSONObject jsonObject = new JSONObject(response.toString());
            return jsonObject.getInt("id");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }
}
