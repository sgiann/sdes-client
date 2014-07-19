package sdes_client;

import AssistantClasses.CharBitPairing;
import AssistantClasses.Encrypt;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Responsible for connecting this client to KDC
 * @author Giannouloudis Stergios
 */
public class ConnectToKDC {

    private InetAddress serverAddress;
    private static DataInputStream input = null;
    private static DataOutputStream output = null;
    private static Encrypt encrypt;
    private String kdcResponse = "";

    public ConnectToKDC(String serverIP) {

        //create an InetAddress to give to the socket
        try {
            serverAddress = InetAddress.getByName(serverIP);
            try {
                Start.cData.setSocket(new Socket(serverAddress, Start.cData.getServerPort()));
                try {
                    input = new DataInputStream(Start.cData.getSocket().getInputStream());
                    output = new DataOutputStream(Start.cData.getSocket().getOutputStream());

                    try {
                        //talk to KDC in order to connect to it.
                        output.writeUTF("initialize");
                        //send clients name unencrypted!!!
                        output.writeUTF(Start.cData.getName());
                        //encrypt key
                        encrypt = new Encrypt(Start.cData.getKey());
                        String encryptedKey = encrypt.encryptKey(Start.cData.getKey());
                        //send clients info
                        output.writeUTF(encryptedKey);//encrypted key
                        output.writeUTF(encryptTextMessage(String.valueOf(Start.cData.getClientsSocketPort())));
                        output.writeUTF(encryptTextMessage(String.valueOf(Start.cData.getClientListenPort())));
                        output.writeUTF(encryptTextMessage(Start.cData.getClientsLocalHostName()));
                        //read acceptance message from kdc
                        kdcResponse = CharBitPairing.decryptBinaryMessage(input.readUTF(), Start.cData.getKey());
                    } catch (IOException ex) {
                        ClientInterface.getjTextArea1().append("Error. Unable to send initialization message to KDC.\n");
                    }
                } catch (IOException ex) {
                    ClientInterface.getjTextArea1().append("Error. Could not bind I/O Streams to KDC");
                }
            } catch (IOException ex) {
                Start.cData.setSocket(null);
                ClientInterface.getjTextArea1().append("Error. Unable to connect to KDC\n");
            }
        } catch (UnknownHostException ex) {
            ClientInterface.getjTextArea1().append("Error. Unable to resolve Hostname and connecto to KDC, " + "please check the IP and try again.\n");
        }


    }

    /**
     * Gets the message (characters), converts each character one by one to an 8-bit string
     * and calls "Encrypt" to encrypt the character. Forms the final binary message.
     * @param message The message to be encrypted
     * @return The encrypted message as a binary-string
     */
    private static String encryptTextMessage(String message) {
        StringBuilder encryptedMsg = new StringBuilder("");
        char msgAsChar[] = message.toCharArray();
        for (char c : msgAsChar) {
            String bitSequence = "";
            //convert every char to bit
            bitSequence = CharBitPairing.getCharToBitProperties().getProperty(String.valueOf(c));
            //encryptTextMessage the bit sequence representing every character
            String encryptedChar = encrypt.encryptText(bitSequence);
            //build the whole sequence
            encryptedMsg.append(encryptedChar);
        }
        return encryptedMsg.toString();
    }

    public static DataInputStream getInput() {
        return input;
    }

    public static DataOutputStream getOutput() {
        return output;
    }

    public String getKdcResponse() {
        return kdcResponse;
    }
}
