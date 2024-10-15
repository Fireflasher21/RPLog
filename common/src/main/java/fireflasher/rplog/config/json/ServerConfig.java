package fireflasher.rplog.config.json;


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

        public List<String> getServerKeywords() {
            return serverKeywords;
        }
    }
}
