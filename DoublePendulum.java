package DoublePendulum;

// import java.lang.Math;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class DoublePendulum {
    private static void createWindow() {
        JFrame window = new JFrame("Double Pendulum");
        window.setSize(500, 600);
        window.setLocation(500, 500);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        PendulumPanel penPan = new PendulumPanel();
        window.getContentPane().add(penPan);
        window.setResizable(false);
        window.setVisible(true);
    }

    public static void main(String[] args) {
        createWindow();
    }
}

class PendulumPanel extends JPanel implements ActionListener {

    /*
        final double armLength1 = 200, armLength2 = 200;
        final double pointMass1 = 30, pointMass2 = 30;
        double firstAngle = (Math.PI)/2, secondAngle = (Math.PI)/2;
        double firstAngleVelocity = 0, secondAngleVelocity = 0;
        final double g = 1;
    */

    private Timer drawTimer;
    private int x, y;
    private boolean left, up;

    public PendulumPanel() {
        setBackground(Color.BLACK);
        drawTimer = new Timer(10, this);
        drawTimer.start();
    }

    public void paintComponent(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillOval(x, y, 20, 20);

        // Moves the ball right and left
        if (!left && x < getWidth()-20) {
            x++;
        } else {
            left = true; 
            x--; 
        } if (left && x > 0) {
            x--;
        } else {
            left = false; 
            x++; 
        }
        
        // Moves the ball up and down
        if (!up && y < getHeight()-20) {
            y++;
        } else {
            up = true; 
            y--; 
        } if (up && y > 0) {
            y--;
        } else {
            up = false; 
            y++; 
        }
    }

    public void actionPerformed(ActionEvent evt) {
        repaint();
    }
}