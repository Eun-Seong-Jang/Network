import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class CalcServer {
	public static String calc(String exp) {
	    StringTokenizer st = new StringTokenizer(exp, " ");
	    String operator = st.nextToken();

	    // Check if the operator is correct
	    if (!(operator.equals("ADD") || operator.equals("SUB") || operator.equals("MUL") || operator.equals("DIV"))) {
	        return "[ERROR01]";
	    }
	    
	    if (st.countTokens() > 2) {
	        return "[ERROR02] Too many arguments"; // More than 2 operands provided
	    }
	    
	    int op1, op2;

	    try {
	        op1 = Integer.parseInt(st.nextToken());
	        op2 = Integer.parseInt(st.nextToken());
	    } catch (NumberFormatException | NoSuchElementException e) {
	        return "[ERROR04]";
	    }

	    String res = "";
	    switch (operator) {
	        case "ADD":
	            res = Integer.toString(op1 + op2);
	            break;
	        case "SUB":
	            res = Integer.toString(op1 - op2);
	            break;
	        case "MUL":
	            res = Integer.toString(op1 * op2);
	            break;
	        case "DIV":
	            if (op2 != 0) {
	                res = Integer.toString(op1 / op2);
	            } else {
	                res = "[ERROR03] Division by zero";
	            }
	            break;
	        default:
	            res = "[ERROR05]";
	    }
	    return res;
	}


    public static void main(String[] args) {
        ServerSocket listener = null;
        Socket socket = null;

        try {
            listener = new ServerSocket(7777); // Change port number to avoid conflict
            System.out.println("Server is running...");
            ExecutorService pool = Executors.newFixedThreadPool(20);

            while (true) {
                socket = listener.accept();
                pool.execute(new ClientHandler(socket));
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (listener != null)
                    listener.close();
            } catch (IOException e) {
                System.out.println("Error closing the server socket: " + e.getMessage());
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            System.out.println("Connected: " + socket);
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                while (true) {
                    String inputMessage = in.readLine();
                    if (inputMessage.equalsIgnoreCase("bye")) {
                        System.out.println("Client disconnected");
                        break;
                    }
                    System.out.println("Received: " + inputMessage);
                    String res = calc(inputMessage);
                    out.write(res + "\n");
                    out.flush();
                }
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            } finally {
                try {
                    if (socket != null)
                        socket.close();
                } catch (IOException e) {
                    System.out.println("Error closing the client socket: " + e.getMessage());
                }
                System.out.println("Closed: " + socket);
            }
        }
    }
}