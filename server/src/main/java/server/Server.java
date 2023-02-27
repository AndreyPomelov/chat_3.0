package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static constants.Commands.*;
import static constants.Constants.PORT;

/**
 * Сервер
 */
public class Server {

    /**
     * Серверный сокет.
     * Служит для обработки первичного запроса от клиента и инициализации клиентского сокета.
     */
    private ServerSocket server;

    /**
     * Входящий поток данных для приёма сообщений от клиента.
     */
    private DataInputStream in;

    /**
     * Исходящий поток данных для отправки сообщений клиенту.
     */
    private DataOutputStream out;

    /**
     * Конструктор.
     * При создании объекта сервер автоматически запускается
     * в режим ожидания соединения с клиентской частью.
     */
    public Server() {
        try {
            runServer();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                server.close();
            } catch (Exception e) {
                // Игнорируем ошибку закрытия серверного сокета
            }
        }
    }

    /**
     * Метод запускает сервер в режим ожидания подключения клиента,
     * после подключения клиента инициализирует потоки данных
     * и запускает режим обмена сообщениями.
     *
     * @throws IOException ошибка ввода-вывода
     */
    private void runServer() throws IOException {
        server = new ServerSocket(PORT);
        System.out.println("Сервер успешно запущен.");
        Socket socket = server.accept();
        System.out.println("Клиент успешно соединился.");
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        work();
    }

    /**
     * Обмен сообщениями между сервером и клиентом.
     *
     * @throws IOException ошибка ввода-вывода
     */
    private void work() throws IOException {
        System.out.println("Запущен режим обмена сообщениями.");

        // Цикл обмена сообщениями
        while (true) {
            // Принимаем сообщение от клиента
            String message = in.readUTF();
            System.out.println("Client: " + message);

            // Если пришла команда на отключение, отправляем клиенту обратно
            // команду на отключение и прерываем цикл обмена сообщениями
            if (EXIT.equals(message)) {
                System.out.println("Клиент отключился.");
                out.writeUTF(EXIT);
                break;
            }

            // Отправляем клиенту обратно его же сообщение
            out.writeUTF("Server: " + message);
        }
    }
}