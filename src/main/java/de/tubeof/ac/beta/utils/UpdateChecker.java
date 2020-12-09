package de.tubeof.ac.beta.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.tubeof.ac.beta.data.Messages;
import de.tubeof.ac.beta.enums.MessageType;
import de.tubeof.ac.beta.main.Main;
import org.bukkit.plugin.Plugin;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

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
            this.resourceRestApiUrl = new URL("https://jenkins.tubeof.de/job/" + resourceName + "/lastSuccessfulBuild/api/json?pretty=true");
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

        int currentVersion = Integer.parseInt(currentBuild.replace("v", "").replace(".", "").replaceAll("[^0-9]", ""));
        int latestVersion = Integer.parseInt(getLatestBuild().replace("v", "").replace(".", "").replaceAll("[^0-9]", ""));

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
            URLConnection urlConnection = getResourceRestApiUrl().openConnection();
            urlConnection.connect();

            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader((InputStream) urlConnection.getContent()));
            JsonObject rootobj = root.getAsJsonObject();
            latestBuild = rootobj.get("number").getAsString();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
