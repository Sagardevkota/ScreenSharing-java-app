/*
 * @created 27/{01}/2021 - 8:10 PM
 * @project screen-sharing
 * @author SAGAR DEVKOTA
 */

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Frames {
    private static JFrame jFrame;
    private static Screens screens = new Screens();

    public static void main(String[] args) {
        System.out.println("Do you want to add or remove screen?");
        Scanner scanner = new Scanner(System.in);

        while (true) {

            String userInput = scanner.nextLine();

            if (userInput.equals("Add"))
                screens.addScreen(new SingleScreen(UUID.randomUUID().toString()));

            if (userInput.equals("Remove"))
                screens.removeScreen();

        }
    }

    static class SingleScreen extends JPanel {

        private String stringToDraw;

        SingleScreen(String stringToDraw) {
            this.stringToDraw = stringToDraw;
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            g.drawString(stringToDraw, 50, 50);
        }

        public String getStringToDraw() {
            return stringToDraw;
        }
    }

    static class Screens {

        private final List<SingleScreen> singleScreens = new ArrayList<>();
        private final List<Color> colors = Arrays.asList(Color.black, Color.BLUE, Color.MAGENTA, Color.CYAN,Color.GRAY,Color.ORANGE);

        Screens() {
            jFrame = new JFrame();
            jFrame.setSize(1000, 1000);
            jFrame.getContentPane().setLayout(new BoxLayout(jFrame.getContentPane(), BoxLayout.Y_AXIS));

        }

        public void addScreen(SingleScreen singleScreen) {

            singleScreens.add(singleScreen);
            for (SingleScreen screen : singleScreens) {
                screen.setBackground(colors.get(ThreadLocalRandom
                        .current()
                        .nextInt(0, colors.size() - 1)));
                screen.setSize(jFrame.getWidth() / singleScreens.size(), jFrame.getHeight() / singleScreens.size());
                jFrame.add(screen);
                System.out.println("Added screen");
            }

            jFrame.setVisible(true);

        }

        public void removeScreen() {
            jFrame.remove(singleScreens.size() - 1);
        }


    }
}
