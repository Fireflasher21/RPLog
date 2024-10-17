package fireflasher.rplog.config.json;


import fireflasher.rplog.ChatLogManager;
import org.lwjgl.system.CallbackI;

import java.util.*;

public class ServerConfig {

    private String serverIp;
    private ServerDetails serverDetails;

    public ServerConfig(){
        this.serverDetails = new ServerDetails();
    }

    public ServerConfig(String serverIp, List<String> serverName, List<String> serverKeywords){
        this.serverIp = serverIp;
        this.serverDetails = new ServerDetails(serverName, serverKeywords);
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {this.serverIp = serverIp;}

    public ServerDetails getServerDetails() {
        return serverDetails;
    }

    public void setServerDetails(ServerDetails serverDetails) {
        this.serverDetails = serverDetails;
    }
    
    @Override
    public String toString(){
        return String.format("{\"Server\": \"%s\",\n%s}",serverIp,serverDetails);
    }
    

    public static class ServerDetails{


        private List<String> serverNames;

        private List<String> serverKeywords;

        public ServerDetails() {
            this.serverNames = new ArrayList<>();
            this.serverKeywords = new ArrayList<>();
        }

        public ServerDetails(List<String> serverNames, List<String> serverKeywords) {
            this.serverNames = new ArrayList<>(serverNames);
            this.serverKeywords = serverKeywords;
            this.serverNames.sort(Comparator.comparingInt(value -> ChatLogManager.getMainDomain(value).length()));
        }

        public List<String> getServerNames() {
            return Collections.unmodifiableList(serverNames);
        }
        public void addServerName(String serverName) {
            if(serverNames.contains(serverName))return;
            // Insert while maintaining sorted order
            int index = Collections.binarySearch(serverNames, serverName, Comparator.comparingInt(value -> ChatLogManager.getMainDomain(value).length()));
            if (index < 0) {
                index = -(index + 1); // Convert to insertion point
            }
            serverNames.add(index, serverName);
        }

        //ServerKeywordMethods
        public List<String> getServerKeywords() {
            return serverKeywords;
        }
        public void removeServerKeywords(String keyword) {
            serverKeywords.remove(keyword);
        }
        public void setServerKeywords(List<String> keywordList) {
            this.serverKeywords = new ArrayList<>(keywordList);
        }
        
        @Override
        public String toString() {
            return String.format("{\"serverDetails\": {\n\"serverNames\": %s,\n\"serverKeywords\": %s\n}}",
                    serverNamesToString(), serverKeywordsToString());
        }
        
        private String serverNamesToString(){
            return serverNames.isEmpty() ? "[]" : String.format("[%s]", String.join(", ", serverNames));

        }
        private String serverKeywordsToString(){
            return serverKeywords.isEmpty() ? "[]" : String.format("[%s]", String.join(", ", serverKeywords));

        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ServerDetails)) return false;
            ServerDetails that = (ServerDetails) o;
            return Objects.equals(serverNames, that.serverNames) &&
                    Objects.equals(serverKeywords, that.serverKeywords);
        }

        @Override
        public int hashCode() {
            return Objects.hash(serverNames, serverKeywords);
        }

    }
}
