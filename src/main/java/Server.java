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
import java.util.*;
import java.util.List;


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
            var serverThread = new ServerThread(socket, UUID.randomUUID().toString());
            serverThread.start();
        }

    }

    static class ServerThread extends Thread {

        private final Socket socket;
        private final PrintWriter printWriter;
        private final BufferedReader bufferedReader;
        private TestPane testPane = new TestPane();
        private String clientName;


        ServerThread(Socket socket, String clientName) throws IOException {
            this.socket = socket;
            this.clientName = clientName;
            printWriter = new PrintWriter(socket.getOutputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }

        @Override
        public void run() {
            try {
                System.out.println(clientName + " Joined");
                multipleScreen.addTestPane(testPane, clientName);
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
            multipleScreen.getTestPaneList().get(clientIndex).setBufferedImage(img);
            multipleScreen.getTestPaneList().get(clientIndex).repaint();
            System.out.println("Rendering Screen for " + clientName);

        }


        public void closeSocket() throws IOException {
            socket.close();
        }
    }


    static class MultipleScreen {
        private final JFrame jFrame;
        private JPanel cards;
        private final List<TestPane> testPaneList = new ArrayList<>();
        private final Map<String, Integer> clientMap = new HashMap();

        MultipleScreen() {
            jFrame = new JFrame();
            cards = new JPanel();
        }

        public void addTestPane(TestPane testPane, String clientName) {
            clientMap.put(clientName, testPaneList.size());
            this.testPaneList.add(testPane);
            for (TestPane testPane1 : testPaneList) {
                cards.add(testPane1);
                testPane.setSize(jFrame.getWidth() / testPaneList.size(),
                        jFrame.getHeight() / testPaneList.size());

            }
            cards.setLayout(new BoxLayout(cards, BoxLayout.PAGE_AXIS));
            jFrame.add(cards);
            jFrame.repaint();

        }

        public int getClientIndex(String clientName) {
            return clientMap.get(clientName);
        }

        public List<TestPane> getTestPaneList() {
            return testPaneList;
        }

        public void init() {
            jFrame.add(cards);
            jFrame.setVisible(true);
            jFrame.setSize(1000, 1000);

        }
    }


}


class TestPane extends JPanel {

    private BufferedImage bufferedImage;

    public Dimension getPreferredSize() {
        return new Dimension(200, 200);
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (bufferedImage != null)
            g.drawImage(bufferedImage, 0, 0, this);
        else
            g.drawString("Nothing to display", 100, 100);
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }


}

