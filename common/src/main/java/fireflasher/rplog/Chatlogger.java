package fireflasher.rplog;

import fireflasher.rplog.config.json.ServerConfig;
import net.minecraft.network.chat.TranslatableComponent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static fireflasher.rplog.RPLog.CONFIG;
import static fireflasher.rplog.RPLog.LOGGER;

public class Chatlogger {

    public static String serverName = "Local";
    public static File log;

    public static final DateTimeFormatter DATE  = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TIME  = DateTimeFormatter.ofPattern("HH:mm:ss");
    public void setup() {
        String path = RPLog.getFolder();
        File rpFolder = new File(path);
        if(!rpFolder.exists()) rpFolder.mkdir();
        log = new File(path + serverName, LocalDateTime.now().format(DATE) + ".txt");

        for(ServerConfig serverConfig: CONFIG.getList()){
            organizeFolders(serverConfig);

            String server_name = getServerNameShortener(serverConfig.getServerDetails().getServerNames());
            String Path = RPLog.getFolder() + server_name;

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
                            TranslatableComponent logger_zipwarning  = new TranslatableComponent("rplog.logger.chatlogger.zip_warning");
                            LOGGER.warn(logger_zipwarning);
                        }
                    }
                }
            }
        }
    }

    private boolean organizeFolders(ServerConfig serverConfig){
        List<String> serverNameList = serverConfig.getServerDetails().getServerNames();
        Pattern serverAddress = Pattern.compile("[A-z]{1,}");
        for(String serverName: serverNameList){
            if(serverAddress.matcher(serverName).find()) continue;

            String path = RPLog.getFolder() + serverName;
            File ipFolder = new File(path);

            if(!ipFolder.exists())continue;

            File[] ipFolderFiles = ipFolder.listFiles();
            if(ipFolderFiles == null){
                ipFolder.delete();
                continue;
            }

            File newFolder = new File(RPLog.getFolder() + Chatlogger.getServerNameShortener(serverConfig.getServerDetails().getServerNames()));

            if(newFolder.exists()) {
                if(newFolder.listFiles().length == 0) {
                    newFolder.delete();
                    ipFolder.renameTo(newFolder);
                }else {
                    File[] newFolderFiles = newFolder.listFiles();
                    if (newFolderFiles.length < ipFolderFiles.length){
                        moveFiles(newFolder, ipFolder);
                        ipFolder.renameTo(newFolder);
                    }
                    else moveFiles(ipFolder, newFolder);
                }
            }
            else ipFolder.renameTo(newFolder);


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
                        if (Files.isRegularFile(path1))
                            Files.move(path1, Path.of(newFolder + path1.toString().replace(sourceFolder.toString(),"")));
                        if (Files.isDirectory(path1)) {
                            Files.createDirectory(Path.of(newFolder + path1.toString().replace(sourceFolder.toString(),"")));
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

    public static String getServerNameShortener(List<String> namelist){
        int[] lenght = new int[2];
        lenght[0] = namelist.get(0).length();
        Pattern serverAddress = Pattern.compile("[A-z]{1,}");
        if(namelist.size() != 1){
            for(String name:namelist){
                if(!serverAddress.matcher(name).find()) continue;

                if(lenght[0] > name.length()){
                    lenght[0] = name.length();
                    lenght[1] = namelist.indexOf(name);
                }
            }
        }
        String name = namelist.get(lenght[1]);
        if(serverAddress.matcher(name).find()){
            Pattern pattern = Pattern.compile("\\.");
            Matcher match = pattern.matcher(name);
            int count = 0;
            while (match.find()) {
                count++;
            }
            if (count > 1) name = name.split("\\.", 2)[1];
            name = name.split("\\.")[0];
        }
        return name;
    }

}
