package fireflasher.rplog;

import fireflasher.rplog.config.json.ServerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static fireflasher.rplog.RPLog.*;

public class Chatlogger {

    public static String serverName = "Local";
    public static List<String> keywordList = new ArrayList<>();
    public static File log;

    public static final DateTimeFormatter DATE  = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TIME  = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final String rEg = "\\.";
    public static ServerConfig serverConfig;

    private static String timedmessage = "";
    private static boolean error;

    public static void onClientConnectionStatus(boolean connectionStatus){
        //on Connection to ServerfinalDestinationFolderFilesCount
        String[] address = Chatlogger.getCurrentServerIP();
        //On Disconnect (connectionStatus == false) or SinglePlayer (address == null)
        if(!connectionStatus || address == null){
            //Set defaultkeywords and serverName for Singleplayer
            serverName = "Local";
            keywordList = CONFIG.getDefaultKeywords();
            return;
        }
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
    public static void chatFilter(String chat){
        if (keywordList.stream().anyMatch(s -> chat.contains(s))) addMessage(chat);
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
    protected void setup() {
        String path = RPLog.getFolder();
        File rpFolder = new File(path);
        if(!rpFolder.exists()) rpFolder.mkdir();
        log = new File(path + serverName, LocalDateTime.now().format(DATE) + ".txt");

        for(ServerConfig serverConfig: CONFIG.getList()){
            List<String> serverNameList =serverConfig.getServerDetails().getServerNames();
            String server_name = getShortestNameOfList(serverNameList);
            String Path = RPLog.getFolder() + server_name;
            organizeFolders(serverNameList,server_name);


            log = new File(Path ,LocalDateTime.now().format(DATE) + ".txt");
            File[] files = new File(Path).listFiles();
            if(files == null){}
            else {
                for (File textfile : files) {
                    if (textfile.toString().endsWith(".txt") && textfile.compareTo(log) != 0 ) {
                        try {
                            String filename  = textfile.toString().replaceFirst("\\.txt", ".zip");

                            FileOutputStream fos = new FileOutputStream(filename);
                            ZipOutputStream zipOut = new ZipOutputStream(fos);

                            File fileToZip = new File(textfile.toString());
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

    public static String getShortestNameOfList(List<String> domainList){
        String name = "";
        //if list only 1 in size, skip
        if(domainList.size() >= 1){
            //Iterate over domainList
            for(String domain:domainList){
                //always get main domain, no subdomain
                int domainArrayLength = domain.split(rEg).length;
                String serverDomain = domainArrayLength == 1 ? domain : domain.split(rEg)[domainArrayLength-2];
                //replace current if new domain is shorter
                if(name.equals("") || serverDomain.length() < name.length())name = serverDomain;
            }
        }
        return name;
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
