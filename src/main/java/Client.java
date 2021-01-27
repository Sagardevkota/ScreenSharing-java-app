/*
 * @created 27/{01}/2021 - 8:10 PM
 * @project screen-sharing
 * @author SAGAR DEVKOTA
 */
import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException, AWTException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Do you want to connect to server?");
        String clientName = scanner.nextLine();
        var client = new MyClient("localhost", 5999, clientName);
        System.out.println("Connection Established");

        while (true) {
            client.sendScreen();
        }


    }

    static class MyClient {
        private final Socket socket;
        private final List<String> messages;
        private final BufferedReader bufferedReader;
        private final PrintWriter printWriter;
        private final String clientName;

        MyClient(String host, int port, String clientName) throws IOException {
            socket = new Socket(host, port);
            var inputStreamReader = new InputStreamReader(socket.getInputStream());
            bufferedReader = new BufferedReader(inputStreamReader);
            messages = new ArrayList<>();
            printWriter = new PrintWriter(socket.getOutputStream());
            this.clientName = clientName;

        }


        public void close() throws IOException {
            socket.close();
        }

        public void sendScreen() throws AWTException, IOException {
            var robot = new Robot();
            var capture = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            var image = robot.createScreenCapture(capture);
            ImageIO.write(image, "png", socket.getOutputStream());

        }
    }

}

