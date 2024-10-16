package fireflasher.rplog.config.json;


import org.lwjgl.system.CallbackI;

import java.util.ArrayList;
import java.util.List;

public class ServerConfig {

    private String serverIp;
    private ServerDetails serverDetails;

    public ServerConfig(){
        this.serverDetails = new ServerDetails();
    }

    public ServerConfig(String serverIp, List<String> serverName, List<String> serverKeywords){
        this.serverIp = serverIp;
        this.serverDetails = new ServerDetails(serverName,serverKeywords);
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
        return  "{" + "\"Server\": " + serverIp + "," + "\n" +
                serverDetails + "}";
    }
    

    public class ServerDetails{


        private List<String> serverNames = new ArrayList<>();

        private List<String> serverKeywords = new ArrayList<>();

        public ServerDetails(){}
        public ServerDetails(List<String> serverName, List<String> serverKeywords){
            this.serverNames = serverName;
            this.serverKeywords = serverKeywords;
        }

        public List<String> getServerNames() {
            return serverNames;
        }
        public void setServerNames(List<String> serverNames) {this.serverNames = serverNames;}

        public List<String> getServerKeywords() {
            return serverKeywords;
        }
        
        @Override
        public String toString(){
            return "{" + "\"serverDetails\":" + "{" + "\n" +
                    "\"serverNames\":" + serverNamesToString() + "," + "\n" +
                    "\"serverKeywords\":" + serverKeywordsToString() + "\n" +
                    "}";
        }
        
        private String serverNamesToString(){
            StringBuilder serverNames = new StringBuilder().append("[");
            for (String serverName: this.serverNames) {
                serverNames.append(serverName).append(",");
            }
            serverNames.deleteCharAt(serverNames.length()-1);
            serverNames.append("]");
            return serverNames.toString();
        }
        private String serverKeywordsToString(){
            StringBuilder serverKeywords = new StringBuilder().append("[");
            for (String keyword: this.serverKeywords) {
                serverKeywords.append(keyword).append(",");
            }
            serverKeywords.deleteCharAt(serverKeywords.length()-1);
            serverKeywords.append("]");
            return serverKeywords.toString();
        }

    }
}
