package ru.netology;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("Введите ваше имя:");
            Scanner scanner = new Scanner(System.in);
            String userName = scanner.nextLine();
            writer.println(userName);

            new Thread(() -> readMessages(reader)).start();

            String message;
            while (true) {
                message = scanner.nextLine();
                writer.println(message);
                if (message.equals("/exit")) {
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void readMessages(BufferedReader reader) {
        try {
            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
