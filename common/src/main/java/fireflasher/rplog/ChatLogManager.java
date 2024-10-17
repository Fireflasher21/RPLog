package fireflasher.rplog;

import fireflasher.rplog.config.json.ServerConfig;
import fireflasher.rplog.logging.LoggerRunner;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static fireflasher.rplog.RPLog.*;

public class ChatLogManager {

    public static String serverName = "Local";
    public static List<String> keywordList = new ArrayList<>();

    public static final DateTimeFormatter DATE  = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final LinkedBlockingDeque<String> messageQueue = new LinkedBlockingDeque<>();
    public static final String rEg = "\\.";

    private static ServerConfig serverConfig;
    private static Thread loggerRunnerThread = new Thread();
    private static LoggerRunner loggerRunner = new LoggerRunner();

    public static void onClientConnectionStatus(boolean connectionStatus){
        //On Disconnect (connectionStatus = false)
        if(!connectionStatus || fireflasher.rplog.ChatLogManager.getCurrentServerIP() == null){
            //Set defaultkeywords and serverName for Singleplayer
            serverName = "Local";
            keywordList = CONFIG.getDefaultKeywords();
            LOGGER.info(serverName);
            return;
        }
        //on Connection to ServerfinalDestinationFolderFilesCount
        String[] address = fireflasher.rplog.ChatLogManager.getCurrentServerIP();
        //get serverConfig by IP
        serverConfig = CONFIG.getServerObject(address[1]);

        //when config exists
        if(serverConfig != null){
            //set keywordList to current
            keywordList = serverConfig.getServerDetails().getServerKeywords();
            //if current connection domain doesnt contain the shortest domain
            if(!address[0].contains(serverName)) {
                //find the shortest domain and set
                serverName = getMainDomain(serverConfig.getServerDetails().getServerNames().getFirst());
            }
        }
        //when no config was found, set ad
        else{
            //get main domain of address and set as serverName
            serverName = getMainDomain(address[0]);
            //set keywords to default of config
            keywordList = CONFIG.getDefaultKeywords();
        }

        LOGGER.info(serverName + " " + address);
        //Call loggerRunner to unset possible errorHandling
        loggerRunner.onServerInteraction();
    }
    public static void chatFilter(String chat){
        if (keywordList.stream().anyMatch(chat::contains)) messageQueue.addLast(chat);
    }

    protected void setup() {
        //start the logger
        loggerRunnerThread = new Thread(loggerRunner);
        loggerRunnerThread.start();

        File rpFolder = new File(RPLog.getFolder());
        if(!rpFolder.exists()) rpFolder.mkdir();

        for(ServerConfig serverConfig: CONFIG.getList()){
            List<String> serverNameList =serverConfig.getServerDetails().getServerNames();
            String server_name = getMainDomain(serverNameList.getFirst());
            String Path = RPLog.getFolder() + server_name;
            organizeFolders(serverNameList,server_name);


            File log = new File(Path ,LocalDateTime.now().format(DATE) + ".txt");
            File[] files = new File(Path).listFiles();
            if(files == null){}
            else {
                for (File fileToZip : files) {
                    if (fileToZip.toString().endsWith(".txt") && fileToZip.compareTo(log) != 0 ) {

                            String filename  = fileToZip.toString().replaceFirst(rEg+"txt", rEg+"zip");

                        try {

                            FileOutputStream fos = new FileOutputStream(filename);
                            ZipOutputStream zipOut = new ZipOutputStream(fos);

                            FileInputStream fis = new FileInputStream(fileToZip);

                            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
                            zipOut.putNextEntry(zipEntry);

                            byte[] bytes = new byte[1024];
                            int length;
                            while ((length = fis.read(bytes)) >= 0) {
                                zipOut.write(bytes, 0, length);
                            }
                            zipOut.close();
                            fis.close();
                            fos.close();

                            if(new File(filename).exists()) fileToZip.delete();
                        }
                        catch (IOException e){
                            Component logger_zipwarning  = RPLog.translateAbleStrings.get("rplog.logger.chatlogger.zip_warning");
                            LOGGER.warn(logger_zipwarning);
                        }
                    }
                }
            }
        }
    }

    private boolean organizeFolders(List<String> serverNameList, String shortestServerName){
        //if another folder exists, we want to move the files to the folder with the shortest domain
        File finalDestinationFolder = new File(RPLog.getFolder() + shortestServerName);
        //iterate over each server
        for(String serverName: serverNameList){
            //if serverName contains shortest domain, skip
            if(serverName.contains(shortestServerName))continue;

            String path = RPLog.getFolder() + serverName;
            File serverFolder = new File(path);

            //if there is no folder for this name, skip
            if(!serverFolder.exists())continue;

            //get all files in folder
            File[] filesInServerFolder = serverFolder.listFiles();
            //if no files in folder, delete and skip
            if(filesInServerFolder == null || filesInServerFolder.length == 0){
                serverFolder.delete();
                continue;
            }

            //check if finalDestinationFolder exists
            if(finalDestinationFolder.exists()) {

                File[] finalDestinationFolderFiles = finalDestinationFolder.listFiles();
                //if existing but empty, delete and rename current to finalDestination
                boolean isEmpty = finalDestinationFolderFiles == null || finalDestinationFolderFiles.length == 0;
                if(isEmpty){
                    finalDestinationFolder.delete();
                    serverFolder.renameTo(finalDestinationFolder);
                    continue;
                }
                int finalDestinationFolderFilesCount = finalDestinationFolderFiles.length;
                //else not empty
                //if newFolder has more files, move and rename
                if (filesInServerFolder.length > finalDestinationFolderFilesCount ){
                    moveFiles(finalDestinationFolder, serverFolder);
                    serverFolder.renameTo(finalDestinationFolder);
                }
                //else move newFolderFiles to finalDestinationFolder
                else moveFiles(serverFolder, finalDestinationFolder);
            }
            //if finalDestinationFolder does not exist, rename current
            else serverFolder.renameTo(finalDestinationFolder);
        }
        return true;
    }

    private boolean moveFiles(File sourceFolder, File newFolder) {
        List<Path> folderstodelete = new ArrayList<>();
        try (Stream<Path> pathStream = Files.walk(sourceFolder.toPath())) {
            pathStream.forEach(path1 -> {
                String filename = path1.getFileName().toString();
                if(filename.equals(sourceFolder.getName()));
                else{
                    try {
                        Path target = Path.of(newFolder + path1.toString().replace(sourceFolder.toString(), ""));
                        if (Files.isRegularFile(path1))
                            Files.move(path1, target);
                        if (Files.isDirectory(path1)) {
                            Files.createDirectory(target);
                            folderstodelete.add(path1);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = folderstodelete.size() - 1; i > -1; i--  ) {
            folderstodelete.get(i).toFile().delete();
        }
        sourceFolder.delete();
        return true;
    }

    public static String getMainDomain(String domainName){
        int domainArrayLength = domainName.split(rEg).length;
        return domainArrayLength == 1 ? domainName : domainName.split(rEg)[domainArrayLength-2];
    }


    public static String[] getCurrentServerIP(){
        Minecraft instance = Minecraft.getInstance();
        //if is in Singleplayer, return null
        if(instance.getConnection() == null || instance.hasSingleplayerServer())return null;
        //Get address with pattern: domain/ip:port
        String address = instance.getConnection().getConnection().getRemoteAddress().toString();
        //Split for [domain][IP:Port]
        String[] domain_ip = address.split("/");
        //edge case, if domain ends with a \\. remove this
        if(domain_ip[0].endsWith(rEg))domain_ip[0] = domain_ip[0].substring(0, domain_ip[0].length()-2);


        //pattern for static domain_ip: static.xxx.xxx.xxx.xxx.client.your-server.de
        Pattern staticIPPattern = Pattern.compile("static[.]([0-9]{1,3}[.]){4}");
        //check if domain_ip[0](domain) has pattern
        boolean isStaticIP = staticIPPattern.matcher(domain_ip[0]).find();

        //if domain is static ip, set domain name in saved name
        if(isStaticIP) domain_ip[0] = instance.getCurrentServer().name ;
        //Split for [domain][IP]
        domain_ip[1] = domain_ip[1].split(":")[0];
        return domain_ip;
    }

}
