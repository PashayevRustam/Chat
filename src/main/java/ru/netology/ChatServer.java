package ru.netology;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatServer {
    private static final int PORT = 8080;
    private static final List<PrintWriter> clients = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Сервер запущен на порту " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                clients.add(writer);

                new Thread(() -> handleClient(clientSocket, writer)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket, PrintWriter writer) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            String clientName = reader.readLine();
            System.out.println(clientName + " присоединился к чату");

            broadcast(clientName + " присоединился к чату");

            String clientMessage;
            while ((clientMessage = reader.readLine()) != null) {
                if (clientMessage.equals("/exit")) {
                    break;
                }
                String formattedMessage = getCurrentTime() + " " + clientName + ": " + clientMessage;
                System.out.println(formattedMessage);
                broadcast(formattedMessage);
            }

            clients.remove(writer);
            broadcast(clientName + " покинул чат");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void broadcast(String message) {
        for (PrintWriter client : clients) {
            try {
                client.println(message);
                client.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(new Date());
    }
}
