import javax.swing.*;
import java.awt.*;

/** main class
 * Created by asi on 27.12.2016.
 */
public class Main {


    public static void main(String[] args) {
        JFrame frame = new JFrame("MI Updater");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // theme selection
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // dimension for sizes of frame and panels
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension panelDimension = new Dimension(dim.width / 4, dim.height / 2);
        Dimension frameDimension = new Dimension((dim.width / 2) + 50, dim.height / 2);
        frame.setMinimumSize(frameDimension);
        frame.add(new View(panelDimension));
        frame.pack();
        centerScreen(frame, dim);
        frame.setVisible(true);
    }


    /**
     * sets a frame to the middle screen location
     * @param frame frame
     * @param dim dimension
     */
    private static void centerScreen(JFrame frame, Dimension dim) {
        int x = dim.width / 2 - (frame.getWidth() / 2);
        int y = dim.height / 2 - (frame.getHeight() / 2);
        frame.setLocation(x, y);
    }

}
