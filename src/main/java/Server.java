/*
 * @created 27/{01}/2021 - 8:09 PM
 * @project screen-sharing
 * @author Sagar Devkota
 */

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.*;


public class Server {
    private static MultipleScreen multipleScreen;

    public static void main(String[] args) throws IOException {

        //create server socket
        var serverSocket = new ServerSocket(5999);
        multipleScreen = new MultipleScreen();
        multipleScreen.init();


        while (true) {
            var socket = serverSocket.accept();
            //create thread for each new client
            var serverThread = new ServerThread(socket);
            serverThread.start();

        }

    }

    static class ServerThread extends Thread {

        private final Socket socket;
        private final PrintWriter printWriter;
        private final BufferedReader bufferedReader;
        private SingleScreen singleScreen;
        private String clientName;


        ServerThread(Socket socket) throws IOException {
            this.socket = socket;
            printWriter = new PrintWriter(socket.getOutputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientName = UUID.randomUUID().toString();
            singleScreen = new SingleScreen(clientName);
        }

        @Override
        public void run() {
            try {
                System.out.println(clientName + " Joined");
                multipleScreen.addTestPane(singleScreen, clientName);
                while (true)
                    readMessage();

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void sendMessage(String msg) {
            printWriter.println(msg);
            printWriter.flush();
        }

        public void readMessage() throws IOException, InterruptedException {
            Thread.sleep(1000);
            var img = ImageIO.read(ImageIO.createImageInputStream(socket.getInputStream()));
            int clientIndex = multipleScreen.getClientIndex(clientName);
            multipleScreen.getSingleScreenList().get(clientIndex).setBufferedImage(img);
            multipleScreen.getSingleScreenList().get(clientIndex).repaint();
            System.out.println("Rendering Screen for " + clientName);
        }


        public void closeSocket() throws IOException {
            socket.close();
        }
    }


    static class MultipleScreen {
        private final JFrame jFrame;
        private final List<SingleScreen> singleScreenList = new ArrayList<>();
        private final Map<String, Integer> clientMap = new HashMap();

        MultipleScreen() {
            jFrame = new JFrame();
            jFrame.getContentPane().setLayout(new BoxLayout(jFrame.getContentPane(), BoxLayout.Y_AXIS));

        }

        public void addTestPane(SingleScreen singleScreen, String clientName) {
            clientMap.put(clientName, singleScreenList.size());
            this.singleScreenList.add(singleScreen);
            for (SingleScreen screen : singleScreenList) {
                screen.setSize(jFrame.getWidth() / singleScreenList.size(), jFrame.getHeight() / singleScreenList.size());
                jFrame.add(screen);
            }

            jFrame.setVisible(true);

        }

        public int getClientIndex(String clientName) {
            return clientMap.get(clientName);
        }

        public List<SingleScreen> getSingleScreenList() {
            return singleScreenList;
        }

        public void init() {
            jFrame.setVisible(true);
            jFrame.setSize(1000, 1000);

        }
    }


}


class SingleScreen extends JPanel {

    private final String clientName;

    SingleScreen(String clientName) {
        this.clientName = clientName;
    }

    private BufferedImage bufferedImage;


    @Override
    protected void paintComponent(Graphics g) {
        if (bufferedImage != null)
            g.drawImage(bufferedImage, 0, 0, this);
        else
            g.drawString("Nothing to display for client " + clientName, 100, 100);
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }


}


