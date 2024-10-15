package fireflasher.rplog.fabric;

import fireflasher.rplog.*;
import fireflasher.rplog.config.json.ServerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static fireflasher.rplog.Chatlogger.*;
import static fireflasher.rplog.RPLog.LOGGER;
import static fireflasher.rplog.RPLog.CONFIG;

public class FabricChatLogger {
    private static String serverIP = "";
    private static List<String> keywordList = new ArrayList<>();
    private static String timedmessage = "";
    private static boolean error;

    public static void chatFilter(String chat){

        if( Minecraft.getInstance().getConnection() != null && !Minecraft.getInstance().hasSingleplayerServer()) servercheck();
        else{
            serverName = "Local";
            keywordList = CONFIG.getDefaultKeywords();
        }

        boolean isChannel = keywordList.stream().anyMatch(chat::contains);
        if (isChannel) addMessage(chat);

    }
    public static void servercheck(){
        String address = Minecraft.getInstance().getConnection().getConnection().getRemoteAddress().toString();
        System.out.println(address);
        String ip = address.split("/")[1];
        ip = ip.split(":")[0];

        ServerConfig serverConfig = CONFIG.getServerObject(ip);

        if(serverConfig != null){
            keywordList = serverConfig.getServerDetails().getServerKeywords();
            if(!address.split("/")[0].contains(serverName) || serverName.equals("Local")) {
                serverName = getServerNameShortener(serverConfig.getServerDetails().getServerNames());
            }
        }
        else keywordList = CONFIG.getDefaultKeywords();
        serverIP = ip;
    }


    private static void addMessage(String chat){
        String Path = RPLog.getFolder() + serverName;
        if(!log.toString().contains(LocalDateTime.now().format(DATE)) || !log.getPath().equalsIgnoreCase(Path)) {
            LocalDateTime today = LocalDateTime.now();
            String date = today.format(DATE);
            String Filename = date + ".txt";
            log = new File(Path, Filename);
            if(error)log = new File(RPLog.getFolder(), date + "-error.txt");
            if (!log.exists()) {
                try {
                    File path = new File(Path);
                    path.mkdir();
                    log.createNewFile();
                } catch (IOException e) {
                    Component logger_creationwarning = RPLog.translateAbleStrings.get("rplog.logger.chatlogger.creation_warning");
                    LOGGER.warn(logger_creationwarning + log.toString());
                    error = true;
                }
            }
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(log, true));
            BufferedReader br = new BufferedReader(new FileReader(log));
            LocalDateTime date = LocalDateTime.now();

            String time = "[" + date.format(TIME) + "] ";
            String message = time + chat;

            String collect = br.lines().collect(Collectors.joining(""));
            if(collect.isEmpty()) bw.append(message);
            else if (!timedmessage.equalsIgnoreCase(chat))bw.append("\n" + message);
            bw.close();

            timedmessage = chat;

        } catch (IOException e) {
            Component logger_writewarning = RPLog.translateAbleStrings.get("rplog.logger.chatlogger.write_warning");
            LOGGER.warn(logger_writewarning + log.toString());
        }
    }




}