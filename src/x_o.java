import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class x_o {
    public static void main (String[] args) throws IOException {
        JFrame frame = new JFrame("X vs O");
        frame.setContentPane(new X_o_Game().getPanel1());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setMinimumSize(new Dimension(600,400));
        frame.setVisible(true);
    }
}
