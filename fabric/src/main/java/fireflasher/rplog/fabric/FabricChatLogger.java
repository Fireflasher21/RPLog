package fireflasher.rplog.fabric;

import fireflasher.rplog.*;
import net.minecraft.network.chat.Component;

import java.io.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static fireflasher.rplog.Chatlogger.*;
import static fireflasher.rplog.RPLog.LOGGER;
import static fireflasher.rplog.RPLog.CONFIG;

public class FabricChatLogger {
    private static String timedmessage = "";
    private static boolean error;

    public static void chatFilter(String chat){
        if (keywordList.stream().anyMatch(chat::contains)) addMessage(chat);
    }

    protected static void onServerConnectionStatus(boolean connectionStatus) {
        //On Disconnect (connectionStatus = false)
        if(!connectionStatus){
            //Set defaultkeywords and serverName for Singleplayer
            serverName = "Local";
            keywordList = CONFIG.getDefaultKeywords();
            return;
        }
        //on Connection to ServerfinalDestinationFolderFilesCount
        String[] address = Chatlogger.getCurrentServerIP();
        //get serverConfig by IP
        serverConfig = CONFIG.getServerObject(address[1]);

        //when config exists
        if(serverConfig != null){
            //set keywordList to current
            keywordList = serverConfig.getServerDetails().getServerKeywords();
            //if current connection domain doesnt contain the shortest domain
            if(!address[0].contains(serverName)) {
                //find the shortest domain and set
                serverName = getShortestNameOfList(serverConfig.getServerDetails().getServerNames());
            }
        }
        //when no config was found, set ad
        else{
            //get main domain of address and set as serverName
            serverName = getShortestNameOfList(List.of(address[0]));
            //set keywords to default of config
            keywordList = CONFIG.getDefaultKeywords();
        }
    
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