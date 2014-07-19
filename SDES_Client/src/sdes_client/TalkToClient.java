package sdes_client;

import AssistantClasses.CharBitPairing;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * This class is responsible for talking to another client.
 * @author Giannouloudis Stergios
 */
public class TalkToClient {

    private String commonKey = "";
    private String peerHostName = "";
    private int peersListenPort = 0;
    private String kdcResp = "";
    
    Socket talkSocket;
    DataOutputStream talkOut;
    
    public TalkToClient() {
    }

    public TalkToClient(DataInputStream fromKdc, DataOutputStream toKdc, String key, String clientsName) {
        try {
            //ask KDC to start a connection to a "listener" client
            toKdc.writeUTF(CharBitPairing.encryptTextMessage("start connection", key));

            //tell kdc who to connect to
            toKdc.writeUTF(CharBitPairing.encryptTextMessage(clientsName, key));
            
            //read whether peer was found or not
            kdcResp = CharBitPairing.decryptBinaryMessage(fromKdc.readUTF(), key);
            if(kdcResp.equals("Requested peer was found.")){
                //read common key
                commonKey = CharBitPairing.decryptBinaryMessage(fromKdc.readUTF(), Start.cData.getKey());
                //write common key to log file
                ClientInterface.logManager.logFileWrite("CommonKey: " + commonKey);

                //get peers info from KDC
                peerHostName = CharBitPairing.decryptBinaryMessage(fromKdc.readUTF(), Start.cData.getKey());
                peersListenPort = Integer.parseInt( CharBitPairing.decryptBinaryMessage(fromKdc.readUTF(), Start.cData.getKey()) );

                //-------talk to peer---------------
                try {
                    InetAddress i = InetAddress.getByName(peerHostName);
                    talkSocket = new Socket(i, peersListenPort);
                    
                    talkOut = new DataOutputStream(talkSocket.getOutputStream());
                } catch (UnknownHostException ex){
                    ClientInterface.getjTextArea1().append("Error. Unable to resolve listeners address.\n" + ex.getMessage());                    
                } catch (IOException ex) {
                    ClientInterface.getjTextArea1().append("Error. Unable to connect to listener.\n");
                }
            }
        } catch (IOException ex) {
            ClientInterface.getjTextArea1().append("Error. Unable to send or receive messages from KDC.\n");
        }
    }

    /**
     * Sends messages from the "speaker".
     * Used by class:ClientsInterface.
     * @param outMessage The outgoing message.
     */
    public void chat(String outMessage) {
        try {
            ClientInterface.logManager.logFileWriteSent();
            ClientInterface.logManager.logFileWrite("Unencrypted msg: " + outMessage);
            String encMsg = CharBitPairing.encryptTextMessage(outMessage, commonKey);
            talkOut.writeUTF(encMsg);
            ClientInterface.logManager.logFileWrite("Encrypted msg: " + encMsg);
        } catch (IOException ex) {
            ClientInterface.getjTextArea1().append("Error. Unable to send message to listener.\n");
        }
    }

    public String getKdcResp() {
        return kdcResp;
    }

}
