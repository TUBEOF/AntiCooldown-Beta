package de.tubeof.ac.beta.main;

import com.sun.org.apache.xpath.internal.operations.Bool;
import de.tubeof.ac.beta.commands.AntiCooldown;
import de.tubeof.ac.beta.data.Data;
import de.tubeof.ac.beta.data.Messages;
import de.tubeof.ac.beta.enums.MessageType;
import de.tubeof.ac.beta.enums.SettingsType;
import de.tubeof.ac.beta.files.Config;
import de.tubeof.ac.beta.listener.*;
import de.tubeof.ac.beta.utils.Metrics;
import de.tubeof.ac.beta.utils.UpdateChecker;
import de.tubeof.tubetils.api.cache.CacheContainer;
import de.tubeof.tubetils.main.TubeTils;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends JavaPlugin {

    private final static Messages messages = new Messages();
    private final static Data data = new Data();

    private final ConsoleCommandSender ccs = Bukkit.getConsoleSender();
    private final PluginManager pluginManager = Bukkit.getPluginManager();

    private static Object cacheContainer;
    private static Main main;

    private float downloadProgress = 0;

    @Override
    public void onEnable() {
        ccs.sendMessage(messages.getTextMessage(MessageType.STARTUP_PREFIX) + "§aThe Plugin will be activated ...");
        ccs.sendMessage(messages.getTextMessage(MessageType.STARTUP_PREFIX) + "§c§l!!!WARNING!!! §cTHIS IS A BETA BUILD! §c§l!!!WARNING!!!");

        checkPluginFileName();

        main = this;

        ccs.sendMessage(messages.getTextMessage(MessageType.STARTUP_PREFIX) + "==================================================");
        ccs.sendMessage(messages.getTextMessage(MessageType.STARTUP_PREFIX) + "JOIN MY DISCORD NOW: §ehttps://discord.gg/73ZDfbx");
        ccs.sendMessage(messages.getTextMessage(MessageType.STARTUP_PREFIX) + "==================================================");

        checkTubeTils();
        TubeTils.Properties.setDebuggingStatus(false);

        manageConfigs();
        registerListener();
        registerCommands();
        checkUpdate();

        setOnlinePlayersCooldown();
        bStats();

        ccs.sendMessage(messages.getTextMessage(MessageType.STARTUP_PREFIX) + "§aThe plugin was successfully activated!");
    }

    @Override
    public void onDisable() {
        ccs.sendMessage(messages.getTextMessage(MessageType.STARTUP_PREFIX) + "§aThe Plugin will be deactivated ...");

        setDefaultCooldown();

        ccs.sendMessage(messages.getTextMessage(MessageType.STARTUP_PREFIX) + "§aThe plugin was successfully deactivated!");
    }

    private void checkPluginFileName() {
        File file = new File("plugins/AntiCooldown-Beta-jar-with-dependencies.jar");
        if(!file.exists()) return;
        else {
            ccs.sendMessage(messages.getTextMessage(MessageType.STARTUP_PREFIX) + "§cYou MUST rename the plugin-file from §eAntiCooldown-Beta-jar-with-dependencies.jar §cto §eAntiCooldown-Beta.jar§c!");
            pluginManager.disablePlugin(this);
        }
    }

    private void checkTubeTils() {
        Plugin tubetils = pluginManager.getPlugin("TubeTils");
        if(tubetils == null) {
            ccs.sendMessage("TubeTils are not installed! Downloading ...");
            getTubeTils();
        } else {
            ccs.sendMessage("TubeTils v" + tubetils.getDescription().getVersion() + " is installed!");
            return;
        }
    }

    private void getTubeTils() {
        try {
            URL url = new URL("https://hub.tubeof.de/repo/de/tubeof/TubeTils/SNAPSHOT-16/TubeTils-SNAPSHOT-16.jar");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "TubeApiBridgeConnector");
            connection.setRequestProperty("Header-Token", "SD998FS0FG07");
            int filesize = connection.getContentLength();

            Timer timer = new Timer();
            Thread thread = new Thread(() -> {
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        ccs.sendMessage("Progress: " + (int)downloadProgress + "%");
                    }
                };
                timer.schedule(timerTask, 0, 250);
            });
            thread.start();

            float totalDataRead = 0;
            BufferedInputStream in = new java.io.BufferedInputStream(connection.getInputStream());
            FileOutputStream fos = new java.io.FileOutputStream("plugins/TubeTils.jar");
            BufferedOutputStream bout = new BufferedOutputStream(fos,1024);
            byte[] data = new byte[1024];
            int i = 0;

            while((i=in.read(data,0,1024))>=0) {
                totalDataRead=totalDataRead+i;
                bout.write(data,0,i);
                downloadProgress = (totalDataRead*100) / filesize;
            }
            timer.cancel();
            thread.stop();
            ccs.sendMessage("Progress: " + (int)downloadProgress + "%");

            bout.close();
            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        File file = new File("plugins/TubeTils.jar");
        try {
            Plugin plugin = pluginManager.loadPlugin(file);
            pluginManager.enablePlugin(plugin);
        } catch (Exception e) {
            e.printStackTrace();

            ccs.sendMessage("Error while enabling TubeTils! Stopping AntiCooldown ...");
            pluginManager.disablePlugin(this);
        }
    }

    private void setupCache() {
        ccs.sendMessage(messages.getTextMessage(MessageType.STARTUP_PREFIX) + "§aCreating Cache Container ...");

        cacheContainer = new CacheContainer("AntiCooldown-Beta::VERSION-" + getDescription().getVersion() + "::SNAPSHOT-" + getDescription().getAPIVersion());
        getCacheContainer().registerCacheType(Integer.class);
        getCacheContainer().registerCacheType(Boolean.class);
        getCacheContainer().registerCacheType(String.class);

        ccs.sendMessage(messages.getTextMessage(MessageType.STARTUP_PREFIX) + "§aCache Container was successfully created!");
    }

    private void manageConfigs() {
        ccs.sendMessage(messages.getTextMessage(MessageType.STARTUP_PREFIX) + "§aLoading Config Files ...");

        Config.cfgConfig();
        Config.setChache();
        if(data.getConfigVersion() != 6) Config.configUpdateMessage();

        ccs.sendMessage(messages.getTextMessage(MessageType.STARTUP_PREFIX) + "§aConfig Files was successfully loaded!");
    }

    private void registerListener() {
        ccs.sendMessage(messages.getTextMessage(MessageType.STARTUP_PREFIX) + "§aListeners will be registered ...");

        pluginManager.registerEvents(new Join(), this);
        pluginManager.registerEvents(new Quit(), this);
        pluginManager.registerEvents(new SwitchWorld(), this);
        pluginManager.registerEvents(new SweepAttack(), this);
        pluginManager.registerEvents(new ItemRestriction(), this);

        ccs.sendMessage(messages.getTextMessage(MessageType.STARTUP_PREFIX) + "§aListeners have been successfully registered!");
    }

    @SuppressWarnings("ConstantConditions")
    private void registerCommands() {
        ccs.sendMessage(messages.getTextMessage(MessageType.STARTUP_PREFIX) + "§aCommands will be registered ...");

        getCommand("anticooldown").setExecutor(new AntiCooldown());

        ccs.sendMessage(messages.getTextMessage(MessageType.STARTUP_PREFIX) + "§aCommands have been successfully registered!");
    }

    private void checkUpdate() {
        if(!data.getBooleanSettings(SettingsType.USE_UPDATE_CHECKER)) {
            ccs.sendMessage(messages.getTextMessage(MessageType.STARTUP_PREFIX) + "§cCheck for updates disabled. The check will be skipped!");
            return;
        }

        ccs.sendMessage(messages.getTextMessage(MessageType.STARTUP_PREFIX) + "§aChecking for updates ...");

        UpdateChecker updateChecker = new UpdateChecker("AntiCooldown-Beta", this);

        if(updateChecker.getUpdateCheckResult() == UpdateChecker.UpdateCheckResult.UP_TO_DATE) {
            data.setUpdateAvailable(false);
            ccs.sendMessage(messages.getTextMessage(MessageType.STARTUP_PREFIX) + "§aNo update available!");
            return;
        }
        if(updateChecker.getUpdateCheckResult() == UpdateChecker.UpdateCheckResult.OUT_DATED) {
            data.setUpdateAvailable(true);
            if(data.getBooleanSettings(SettingsType.UPDATE_NOTIFY_CONSOLE)) ccs.sendMessage(messages.getTextMessage(MessageType.STARTUP_PREFIX) + "§cAn update was found! You are using §eBuild " + updateChecker.getCurrentBuild() + "§c. Latest Build is §e" + updateChecker.getLatestBuild() + "§c!");
            if(data.getBooleanSettings(SettingsType.UPDATE_AUTO_UPDATE)) {

                try {
                    URL url = new URL("https://hub.tubeof.de/jenkins/job/AntiCooldown-Beta/lastSuccessfulBuild/artifact/target/AntiCooldown-Beta-SNAPSHOT-" + updateChecker.getLatestBuild() + ".jar");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("User-Agent", "TubeApiBridgeConnector");
                    connection.setRequestProperty("Header-Token", "SD998FS0FG07");
                    int filesize = connection.getContentLength();

                    Timer timer = new Timer();
                    Thread thread = new Thread(() -> {
                        TimerTask timerTask = new TimerTask() {
                            @Override
                            public void run() {
                                ccs.sendMessage("Progress: " + (int)downloadProgress + "%");
                            }
                        };
                        timer.schedule(timerTask, 0, 250);
                    });
                    thread.start();

                    float totalDataRead = 0;
                    java.io.BufferedInputStream in = new java.io.BufferedInputStream(connection.getInputStream());
                    java.io.FileOutputStream fos = new java.io.FileOutputStream("plugins/AntiCooldown-Beta.jar");
                    BufferedOutputStream bout = new BufferedOutputStream(fos,1024);
                    byte[] data = new byte[1024];
                    int i = 0;

                    while((i=in.read(data,0,1024))>=0) {
                        totalDataRead=totalDataRead+i;
                        bout.write(data,0,i);
                        downloadProgress = (totalDataRead*100) / filesize;
                    }
                    timer.cancel();
                    thread.stop();
                    ccs.sendMessage("Progress: " + (int)downloadProgress + "%");

                    bout.close();
                    in.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return;
        }
        if(updateChecker.getUpdateCheckResult() == UpdateChecker.UpdateCheckResult.UNRELEASED) {
            ccs.sendMessage(messages.getTextMessage(MessageType.STARTUP_PREFIX) + "§aThis version will be released in the future!");
            return;
        }
        if(updateChecker.getUpdateCheckResult() == UpdateChecker.UpdateCheckResult.NO_RESULT) {
            ccs.sendMessage(messages.getTextMessage(MessageType.STARTUP_PREFIX) + "§cAn error occurred in the dpdate checker. Possibly the API is currently offline.");
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void setDefaultCooldown() {
        for(Player all : Bukkit.getOnlinePlayers()) {
            if(all.getAttribute(Attribute.GENERIC_ATTACK_SPEED).getBaseValue() != 4) all.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(4);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void setOnlinePlayersCooldown() {
        for(Player all : Bukkit.getOnlinePlayers()) {
            String world = all.getLocation().getWorld().getName();
            if(data.isWorldDisabled(world)) return;

            all.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(data.getIntegerSettings(SettingsType.ATTACK_SPEED_VALUE));
        }
    }

    @SuppressWarnings("unused")
    private void bStats() {
        ccs.sendMessage(messages.getTextMessage(MessageType.STARTUP_PREFIX) + "§aLoad and activate bStats ...");

        Metrics metrics = new Metrics(this, 9604);

        ccs.sendMessage(messages.getTextMessage(MessageType.STARTUP_PREFIX) + "§abStats was successfully loaded and activated!");
    }

    public static Messages getMessages() {
        return messages;
    }

    public static Data getData() {
        return data;
    }

    public static CacheContainer getCacheContainer() {
        return (CacheContainer) cacheContainer;
    }

    public static Main getMain() {
        return main;
    }
}
