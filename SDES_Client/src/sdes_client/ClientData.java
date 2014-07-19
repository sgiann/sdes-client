package sdes_client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Represents a Client.
 * Holds clients data.
 * @author Giannouloudis Stergios
 */
public class ClientData {
    
    private static final String logFileName = "client_config.xml"; //the configuration file that we need to parse.
    BufferedReader buffReader;

    private String name;
    private String key;
    private String serverIpString;
    private int serverPort;

    private Socket socket;
    private int clientsSocketPort;
    private ServerSocket serverS;
    private int clientListenPort;
    private String clientsLocalHostName;
    private String clientsLocalhostAddressStr;
    
    

    /**
     * Initializes fields and sockets.
     */
    public ClientData() {

        this.name = "";
        this.key = "";
        this.serverIpString = "";
        this.serverPort = 0;
        this.clientListenPort = 0;
        this.clientsSocketPort = 0;
        this.clientsLocalHostName = "";
        this.clientsLocalhostAddressStr = "";

        this.socket = new Socket();

        try {
            this.socket.bind(null);
        } catch (IOException ex) {
            ClientInterface.getjTextArea1().append("Error. Unable to load data and create client.");
        }
        
        try {
            this.clientsLocalHostName = InetAddress.getLocalHost().getHostName();
            this.clientsLocalhostAddressStr = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            ClientInterface.getjTextArea1().append("Error. Unable to load data and create client.");
        }
        this.clientsSocketPort = socket.getLocalPort();

        parseClientConfigFile();

        //Initialize ServerSocket and bind to specific port.
        try {
            serverS = new ServerSocket(clientListenPort);
        } catch (IOException ex) {
            ClientInterface.getjTextArea1().append("Error. Unable to load data and create client.");
        }
    }

    /**
     * Parses the LogFile and initiaizes the clients data.
     */
    private void parseClientConfigFile(){

        boolean isClientFile = false; //states if the config-file is a clients file
        String line = "";

        try{
            buffReader = new BufferedReader(new FileReader (new File(logFileName) ));

            while( buffReader.ready()) {
                line = buffReader.readLine().trim();
                //check to see if <client> tag exists
                if (line.startsWith("<client>")){
                    isClientFile = true;
                }
                
                if (isClientFile){
                    if (line.startsWith("<name>")){
                        this.name = line.substring(6, line.length()-7);
                    } else if (line.startsWith("<key>")){
                        this.key = line.substring(5, line.length()-6);
                    } else if (line.startsWith("<serverip>")){
                        this.serverIpString = line.substring(10, line.length()-11);
                    } else if (line.startsWith("<serverport>")){
                        this.serverPort = Integer.valueOf(line.substring(12, line.length()-13));
                    } else if (line.startsWith("<clientListenPort>")){
                        clientListenPort = Integer.valueOf(line.substring(18, line.length()-19));
                    }
                    else
                        continue;
                } else
                    initializeNA();
            }

        } catch (FileNotFoundException fnfEx){
            ClientInterface.getjTextArea1().append("Could not FIND client's Configuration File.");
            initializeNA();
        } catch (IOException ioEx){
            ClientInterface.getjTextArea1().append("Could not OPEN client's Configuration File.");
            initializeNA();
        }
    }

    /**
     * Initializes Clients the data fields to N/A.<br/>
     * Used when Clients data are not available for some reason.
     */
    private void initializeNA(){
        name = "N/A";
        key = "N/A";
        serverIpString = "N/A";
        serverPort = 0;
        clientListenPort = 0;
    }

    /*-----------GETTERS AND SETTERS-------------------*/
    public int getClientListenPort() {
        return clientListenPort;
    }

    public void setClientListenPort(int clientListenPort) {
        this.clientListenPort = clientListenPort;
    }

    public String getClientsLocalHostName() {
        return clientsLocalHostName;
    }

    public void setClientsLocalHostName(String clientsLocalHostName) {
        this.clientsLocalHostName = clientsLocalHostName;
    }

    public String getClientsLocalhostAddressStr() {
        return clientsLocalhostAddressStr;
    }

    public void setClientsLocalhostAddressStr(String clientsLocalhostAddressStr) {
        this.clientsLocalhostAddressStr = clientsLocalhostAddressStr;
    }

    public int getClientsSocketPort() {
        return clientsSocketPort;
    }

    public void setClientsSocketPort(int clientsSocketPort) {
        this.clientsSocketPort = clientsSocketPort;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServerIpString() {
        return serverIpString;
    }

    public void setServerIpString(String serverIpString) {
        this.serverIpString = serverIpString;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public ServerSocket getServerS() {
        return serverS;
    }

    public void setServerS(ServerSocket serverS) {
        this.serverS = serverS;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
    
    @Override
    public String toString() {
        return "Name = " + name + ", Key = " + key +  "\n" 
                + "localHostName = " + clientsLocalHostName + "\n"
                + "localSocketPort = " + clientsSocketPort + "\n"
                + "clientListenPort = " + clientListenPort;
    }

}
