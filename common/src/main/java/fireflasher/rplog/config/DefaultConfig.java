package fireflasher.rplog.config;

import com.google.gson.Gson;
import fireflasher.rplog.RPLog;
import fireflasher.rplog.config.json.JsonConfig;
import fireflasher.rplog.config.json.ServerConfig;
import net.minecraft.network.chat.TranslatableComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DefaultConfig {

    private File ConfigFile;
    private static final String ConfigFileName = "rplog.json";
    private static final Gson GSON = new Gson();
    private static final String ConfigDir = RPLog.getConfigFolder();
    private List<String> defaultKeywords = new ArrayList<>();
    public static final List<String> defaultList =
            Arrays.asList(
                    "[Flüstern]",
                    "[Leise]",
                    "[Reden]",
                    "[PReden]",
                    "[Rufen]",
                    "[PRufen]",
                    "[Schreien]",
                    "[Magie]"
            );

    public List<ServerConfig> serverList = new ArrayList<>();
    private static final Logger LOGGER = LogManager.getLogger("RPLog DefaultConfig");

    public void setup() {
        this.ConfigFile = new File(ConfigDir + ConfigFileName);
        if (ConfigFile.exists()) LOGGER.info(new TranslatableComponent("rplog.logger.defaultconfig.config_created").getContents());
        else setConfigFile();
        loadConfig();
    }

    private void setConfigFile(){
        if (this.ConfigFile == null || !this.ConfigFile.exists()) {
            this.ConfigFile = new File(ConfigDir, ConfigFileName);
            try {
                boolean wasCreated = ConfigFile.createNewFile();
                if(!wasCreated)LOGGER.error(new TranslatableComponent("rplog.logger.defaultconfig.config_create_error")+"\n"+ConfigFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        JsonConfig channellistconfig = new JsonConfig(defaultList);
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(ConfigFile, false));
            String json = GSON.toJson(channellistconfig);
            pw.write(json);
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void loadConfig(){
        if (this.ConfigFile == null || !this.ConfigFile.exists())setConfigFile();

        try {
            BufferedReader br = new BufferedReader(new FileReader(ConfigFile));
            String collect = br.lines().collect(Collectors.joining(""));
            if(collect.isEmpty()){
                setConfigFile();
                collect = br.lines().collect(Collectors.joining(""));
            }
            JsonConfig jsonConfig = GSON.fromJson(collect, JsonConfig.class);


            defaultKeywords = jsonConfig.getDefaultKeywords();
            serverList = jsonConfig.getServerList();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        if (this.ConfigFile == null) {
            return;
        }
        JsonConfig jsonConfig = new JsonConfig(defaultKeywords, serverList);
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(ConfigFile, false));
            String json = GSON.toJson(jsonConfig);
            pw.write(json);
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<ServerConfig> getList() {
        if (serverList.isEmpty()) {
            loadConfig();
        }
        return this.serverList;
    }

    public List<String> getDefaultKeywords() {
        return defaultKeywords;
    }

    public void addServerToList(String serverIp, String serverName) {
        boolean save = false;
        int i = 0;

        for (ServerConfig serverListe : serverList) {
            if (!serverListe.getServerIp().equals(serverIp)) {
                i++;
                continue;
            }
            //Falls Server als Ip bereits in der Liste, überprüf die Liste der Servernamen
            ServerConfig.ServerDetails temp_serverdetails = serverListe.getServerDetails();
            if (!temp_serverdetails.getServerNames().contains(serverName)) {
                this.serverList.remove(serverListe);
                Pattern serverAddress = Pattern.compile("[A-z]{1,}");
                if(serverAddress.matcher(serverName).find()) temp_serverdetails.getServerNames().add(0,serverName);
                else temp_serverdetails.getServerNames().add(serverName);
                serverListe.setServerDetails(temp_serverdetails);
                this.serverList.add(serverListe);
                save = true;
            }
        }
        if(i == serverList.size()) {
            ServerConfig server = new ServerConfig(serverIp, List.of(serverName), defaultList);
            serverList.add(server);
            saveConfig();
            return;
        }
        if(save){
            saveConfig();
        }
    }

    public void removeServerFromList(ServerConfig serverConfig){
        getList().remove(serverConfig);
        saveConfig();
    }

    public ServerConfig getServerObject(String serverIp) {
        for (ServerConfig server : serverList) {
            if (!server.getServerIp().equals(serverIp)) {
                continue;
            }
            return server;
        }
        return null;
    }

}

