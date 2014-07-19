package AssistantClasses;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import sdes_client.ClientInterface;

/**
 *
 * @author Giannouloudis Stergios
 */
public class LogFileManager {

    public static BufferedWriter buffWriter;
    private static final String logFileName = "Client_Logfile.txt";

    public LogFileManager() {
        try {
            buffWriter = new BufferedWriter(new FileWriter(logFileName));
            logFileWrite("<<Start>>");
            buffWriter.newLine();
            logFileWriteNote();
            //ClientInterface.getjTextArea1().append("Created client's logfile.\n");
        } catch (IOException ex) {
            //ClientInterface.getjTextArea1().append("Error. Unable to create client's log file\n");
        }
    }

    public void logFileWrite(String message) {
        try {
            buffWriter.write(message);
            buffWriter.newLine();
            buffWriter.flush();
        } catch (IOException ex) {
            //ClientInterface.getjTextArea1().append("Error. Unable to write to logfile.\n");
        }
    }

    /**
     * Writes "Received:" and changes line on the log file.
     */
    public void logFileWriteReceive() {
        try {
            buffWriter.newLine();
            buffWriter.write("Received:");
            buffWriter.newLine();
            buffWriter.flush();
        } catch (IOException ex) {
            //ClientInterface.getjTextArea1().append("Error. Unable to write to logfile.\n");
        }
    }

    public void logFileWriteSent() {
        try {
            buffWriter.newLine();
            buffWriter.write("Sent:");
            buffWriter.newLine();
            buffWriter.flush();
        } catch (IOException ex) {
            //ClientInterface.getjTextArea1().append("Error. Unable to write to logfile.\n");
        }
    }

    public void logFileWriteNote() {
        try {
            buffWriter.write("Οι παρακάτω εγγραφές είναι τα μυνήματα που παρέλαβε ο client.");
            buffWriter.newLine();
            buffWriter.write("Είναι το αποτέλεσμα της κωδικοποίησης και αποκωδικοποίησης της,");
            buffWriter.newLine();
            buffWriter.write("μήκους 8 bit, δυαδικής αναπαράστασης κάθε χαρακτήρα.");
            buffWriter.newLine();
            buffWriter.write("Η αντιστοίχιση χαρακτήτα-δυαδικής αναπαράστασης δίνεται στα");
            buffWriter.newLine();
            buffWriter.write("αρχεία \"charToBit.xml\" και \"bitToChar.xml\"");
            buffWriter.newLine();
            buffWriter.newLine();
            buffWriter.flush();
        } catch (IOException ex) {
            //ClientInterface.getjTextArea1().append("Error. Unable to write to logfile.\n");
        }
    }

    public static BufferedWriter getBuffWriter() {
        return buffWriter;
    }
}
