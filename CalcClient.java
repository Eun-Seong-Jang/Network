import java.io.*;
import java.net.*;
import java.util.*;

public class CalcClient {
    public static void main(String[] args) throws Exception {
        BufferedReader in = null;
        BufferedWriter out = null;
        Socket socket = null;
        Scanner scanner = new Scanner(System.in);
        try {
            socket = new Socket("localhost", 7777); // Changed port number to match the CapitalizeClient port
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            while (true) {
                System.out.print("[Calculator] (separated by spaces (eg.ADD 24 42)) >>"); // Prompt
                String outputMessage = scanner.nextLine(); // Read the equation from the keyboard
                if (outputMessage.equalsIgnoreCase("bye")) {
                    out.write(outputMessage + "\n"); // Send the string "bye"
                    out.flush();
                    break; // If the user inputs "bye," send it to the server and disconnect the connection
                }
                out.write(outputMessage + "\n"); // Send the string of the equation read from the keyboard
                out.flush();
                String inputMessage = in.readLine(); // Receive the calculation result from the server
                System.out.println("[Answer]: PRINT " + inputMessage);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                scanner.close();
                if (socket != null)
                    socket.close(); // Close the client socket
            } catch (IOException e) {
                System.out.println("There was an error communicating with the server.");
            }
        }
    }
}