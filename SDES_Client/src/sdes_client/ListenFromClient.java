package sdes_client;

import AssistantClasses.CharBitPairing;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * This class is responsible for listening from another client when the
 * other has started the communication.
 * @author Giannouloudis Stergios
 */
public class ListenFromClient extends Thread {

    private Socket s;
    private DataInputStream listenIn = null;
    String msg = "";

    /**
     * Starts the thread.
     */
    public ListenFromClient() {
        this.start();
    }

    @Override
    public void run() {
        try {

            s = Start.cData.getServerS().accept();   //accept connection initialy from kdc to get speakers info (and then from the speaker himself)

            if (!s.isConnected()) {
                ClientInterface.getjTextArea1().append("Error. Unable to accept connection from KDC"
                                                                    + "and get \"speakers\" data.\n");
            }
            listenIn = new DataInputStream(s.getInputStream());
        } catch (IOException ex) {
            ClientInterface.getjTextArea1().append("Error. Unable to bind IO Streams to listening socket "
                                                    + "and receive information from KDC.\n");
        }

        try {
            String commonKey = "";
            String requestersAddrStr = "";
            int requestersPort = 0;

            //processing incoming messages from KDC!
            String kdcEncMsg = listenIn.readUTF();
            String kdcNotification = CharBitPairing.decryptBinaryMessage(kdcEncMsg, Start.cData.getKey());
            if (kdcNotification.equals("incomming request")) {
                commonKey = CharBitPairing.decryptBinaryMessage(listenIn.readUTF(), Start.cData.getKey());
                ClientInterface.logManager.logFileWrite("Common key: " + commonKey);
                requestersPort = Integer.parseInt(CharBitPairing.decryptBinaryMessage(listenIn.readUTF(), Start.cData.getKey()));
                requestersAddrStr = CharBitPairing.decryptBinaryMessage(listenIn.readUTF(), Start.cData.getKey());
            }

            s = Start.cData.getServerS().accept();   //accept new connection from speaker this time!
            if (!s.isConnected()) {
                ClientInterface.getjTextArea1().append("Error. Unable to connect to speaker and get his messages.\n");
            }
            listenIn = new DataInputStream(s.getInputStream());
            
            //alter interface
            //deactivate talk-button
            ClientInterface.getjButton2().setEnabled(false);
            ClientInterface.getjTextField2().setEnabled(false);
            ClientInterface.getjTextField2().setEditable(false);

            //deactivate activate send-button
            ClientInterface.getjTextField3().setEditable(false);
            ClientInterface.getjTextField3().setEnabled(false);
            ClientInterface.getjButton3().setEnabled(false);

            ClientInterface.getjTextArea1().append("Talking mode: OFF\n");

            //listen to incoming messages from peer!
            while (true){
                String encryptedMsg = listenIn.readUTF();
                ClientInterface.logManager.logFileWriteReceive();
                ClientInterface.logManager.logFileWrite("Encrypted message: " + encryptedMsg + "\t");
                msg = CharBitPairing.decryptBinaryMessage(encryptedMsg, commonKey);
                ClientInterface.logManager.logFileWrite("Decrypted message: " + msg + "\n");
                ClientInterface.getjTextArea2().append("Recieved: " + msg + "\n");
            }
        } catch (IOException ex) {
            ClientInterface.getjTextArea1().append("Error. Unable to bind IO srteams to listening socket"
                                                    + "and get peer's message.\n");
        }
    }

    /**
     * Returns the speakers message.
     * @return The speakers message
     */
    public String getMsg() {
        return msg;
    }
}
