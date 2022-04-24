package DoublePendulum;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;

// This class is for the JFrame that is 1100x700.
public class DoublePendulum {
    private static void createWindow() {
        JFrame window = new JFrame("Double Pendulum Simulation");
        window.setSize(1100, 700);
        window.setLocation(500, 500);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        PendulumHolder penHolder = new PendulumHolder();
        window.getContentPane().add(penHolder);
        window.setVisible(true);
    }

    public static void main(String[] args) {
        createWindow();
    }
}

// This panel holds PendulumPanel, which draws the pendulum, and PendulumControlPanel, which can be used
// to customize the pendulum.
class PendulumHolder extends JPanel {
    public PendulumHolder() {
        setLayout(new BorderLayout());

        PendulumPanel pendulumPan = new PendulumPanel();
        add(pendulumPan, BorderLayout.CENTER);

        PendulumPanel.PendulumControlPanel pendulumControl = pendulumPan.new PendulumControlPanel();
        add(pendulumControl, BorderLayout.EAST);

    }
}

// This panel draws the pendulum using two equations that can be found here:
// https://www.myphysicslab.com/pendulum/double-pendulum-en.html
//
// Most of the field variables can be customized in PendulumControlPanel. The ones that are not customized
// are for the movement of the pendulum.
class PendulumPanel extends JPanel implements ActionListener {
    private int l1 = 200; // First arm length
    private int l2 = 200; // Second arm length
    private int m1 = 40; // Mass of first point
    private int m2 = 40; // Mass of second point
    private double a1 = Math.PI/2; // First angle (Starts at 90 degrees)
    private double a2 = Math.PI/2; // Second angle (Starts at 90 degrees)
    private double a1_velocity = 0.0; // Angle 1 velocity
    private double a2_velocity = 0.0; // Angle 2 velocity
    private double a1_acceleration = 0.0; // Angle 1 acceleration
    private double a2_acceleration = 0.0; // Angle 2 acceleration
    private int x1, y1, x2, y2; // Positions of first point
    private double g = 1.0; // Gravitational constant for gravity level

    private final int horizontalShift = 450; // Shifts the pendulum left and right
    private final int verticalShift = 200; // Shifts the pendulum up and down

    private Timer drawTimer; // A timer that is used to draw each frame

    public PendulumPanel() {
        setBackground(Color.BLACK);
        drawTimer = new Timer(15, this);
        drawTimer.start();
    }

    public void actionPerformed(ActionEvent evt) {
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        performCalculations();
        g.setColor(Color.WHITE);

        // Draws a circle at the first point (x1, y1)
        g.fillOval(x1 + horizontalShift - m1/2, y1 + verticalShift - m1/2, m1, m1);
        // Draws a line from origin to first point (origin is at (450, 200) in this case)
        g.drawLine(0 + horizontalShift, 0 + verticalShift, x1 + horizontalShift, y1 + verticalShift);
        
        // Draws an oval at the second point (x2, y2)
        g.fillOval(x2 + horizontalShift - m2/2, y2 + verticalShift - m2/2, m2, m2);
        // Draws a line from first point to second point
        g.drawLine(x1 + horizontalShift, y1 + verticalShift, x2 + horizontalShift, y2 + verticalShift);
    }

    // Calculates the angles and the positions of the points for the pendulum using the equations.
    public void performCalculations()
    {
        // First angle
        double numerator1 = -g * (2*m1 + m2) * Math.sin(a1);
        double numerator2 = -m2 * g * Math.sin(a1 - 2*a2);
        double numerator3 = -2 * Math.sin(a1 - a2) * m2;
        double numerator4 = (a2_velocity*a2_velocity) * l2 + (a1_velocity*a1_velocity) * l1 * Math.cos(a1 - a2);
        double denominator = l1 * (2 * m1 + m2 - m2 * Math.cos(2*a1 - 2*a2));
        a1_acceleration = (numerator1 + numerator2 + numerator3*numerator4)/denominator;
        
        // Second Angle
        numerator1 = 2 * Math.sin(a1 - a2);
        numerator2 = (a1_velocity * a1_velocity) * l1 * (m1 + m2);
        numerator3 = g * (m1 + m2) * Math.cos(a1);
        numerator4 = (a2_velocity*a2_velocity) * l2 * m2 * Math.cos(a1 - a2);
        denominator = l2 * (2 * m1 + m2 - m2 * Math.cos(2*a1 - 2*a2));
        a2_acceleration = (numerator1 * (numerator2 + numerator3 + numerator4))/denominator;

        // Calculates the first point's position
        x1 = (int)(l1 * Math.sin(a1));
        y1 = (int)(l1 * Math.cos(a1));

        // Calculates the second point's position
        x2 = (int)(l2 * Math.sin(a2) + x1);
        y2 = (int)(l2 * Math.cos(a2) + y1);

        // Adjusts velocity before acceleration
        a1_velocity += a1_acceleration;
        a2_velocity += a2_acceleration;
        
        // Adjusts angle position
        a1 += a1_velocity;
        a2 += a2_velocity;

        // Helps to simulate air resistance
        a1_velocity *= 0.999;
        a2_velocity *= 0.999;
    }

    // This class is for the controls on the right. It is possible to change the top arm length, the
    // bottom arm length, the top point's mass, the bottom point's mass, and the gravity level.
    class PendulumControlPanel extends JPanel {
        private JSlider armLength1Slider;
        private JSlider armLength2Slider;
        private JSlider topWeightSlider;
        private JSlider bottomWeightSlider;
        private JSlider gravSlider;
        private Color bgColor;
    
        public PendulumControlPanel() {
            bgColor = new Color(3, 177, 252);
            setBackground(bgColor);
            setPreferredSize(new Dimension(200, 700));
            setLayout(new FlowLayout(FlowLayout.CENTER, 500, 20));
    
            // Top arm JLabel and JSlider
            JPanel topArm = new JPanel();
            topArm.setPreferredSize(new Dimension(200, 70));
            topArm.setBackground(bgColor);
            JLabel tal = new JLabel("Top Arm Length:");
            topArm.add(tal);
            armLength1Slider = makeSlider(50, 250, 200, 50);
            AL1SliderListener sliderListener = new AL1SliderListener();
            armLength1Slider.addChangeListener(sliderListener);
            topArm.add(armLength1Slider);
            add(topArm);

            // Bottom arm JLabel and JSlider
            JPanel bottomArm = new JPanel();
            bottomArm.setPreferredSize(new Dimension(200, 70));
            bottomArm.setBackground(bgColor);
            JLabel bal = new JLabel("Bottom Arm Length:");
            bottomArm.add(bal);
            armLength2Slider = makeSlider(50, 250, 200, 50);
            AL2SliderListener sliderListener2 = new AL2SliderListener();
            armLength2Slider.addChangeListener(sliderListener2);
            bottomArm.add(armLength2Slider);
            add(bottomArm);

            // Top weight JLabel and JSlider
            JPanel topWeight = new JPanel();
            topWeight.setPreferredSize(new Dimension(200, 70));
            topWeight.setBackground(bgColor);
            JLabel twm = new JLabel("Top Weight Mass:");
            topWeight.add(twm);
            topWeightSlider = makeSlider(10, 100, 40, 10);
            TopWeightSliderListener twsListener = new TopWeightSliderListener();
            topWeightSlider.addChangeListener(twsListener);
            topWeight.add(topWeightSlider);
            add(topWeight);

            // Bottom weight JLabel and JSlider
            JPanel bottomWeight = new JPanel();
            bottomWeight.setPreferredSize(new Dimension(200, 70));
            bottomWeight.setBackground(bgColor);
            JLabel bwm = new JLabel("Bottom Weight Mass:");
            bottomWeight.add(bwm);
            bottomWeightSlider = makeSlider(10, 100, 40, 10);
            BottomWeightSliderListener bwsListener = new BottomWeightSliderListener();
            bottomWeightSlider.addChangeListener(bwsListener);
            bottomWeight.add(bottomWeightSlider);
            add(bottomWeight);

            // Gravity JLabel and JSlider
            JPanel gravity = new JPanel();
            gravity.setPreferredSize(new Dimension(200, 70));
            gravity.setBackground(bgColor);
            JLabel grav = new JLabel("Gravity:");
            gravity.add(grav);
            gravSlider = makeSlider(1, 10, 1, 1);
            GravitySliderListener gListener = new GravitySliderListener();
            gravSlider.addChangeListener(gListener);
            gravity.add(gravSlider);
            add(gravity);

            // Listener for buttons
            ButtonListener buttonsListener = new ButtonListener();

            // Button to reset the position of the pendulum (doesn't reset slider values)
            JButton resetPendulumPos = new JButton("Reset Position");
            resetPendulumPos.addActionListener(buttonsListener);
            add(resetPendulumPos);

            // Button to reset everything
            JButton resetPendulum = new JButton("Reset Pendulum");
            resetPendulum.addActionListener(buttonsListener);
            add(resetPendulum);

            // Button to pause
            JButton pause = new JButton("Pause");
            pause.addActionListener(buttonsListener);
            add(pause);

            // Button to resume
            JButton resume = new JButton("Resume");
            resume.addActionListener(buttonsListener);
            add(resume);
        }
    
        // This method makes a JSlider based on certain parameters.
        public JSlider makeSlider(int min, int max, int val, int ticksSpacing) {
            JSlider slider = new JSlider(min, max, val);
            slider.setMajorTickSpacing(ticksSpacing);
            slider.setPaintTicks(true);
            slider.setLabelTable(slider.createStandardLabels(ticksSpacing));
            slider.setPaintLabels(true);
            slider.setOrientation(JSlider.HORIZONTAL);
            return slider;
        }
    
        // When the slider for the top arm length is changed, this will make the proper adjustments.
        class AL1SliderListener implements ChangeListener {
            public void stateChanged(ChangeEvent evt) {
                int val = armLength1Slider.getValue();
                l1 = val;
            }
        }

        // When the slider for the bottom arm length is changed, this will make the proper adjustments.
        class AL2SliderListener implements ChangeListener {
            public void stateChanged(ChangeEvent evt) {
                int val = armLength2Slider.getValue();
                l2 = val;
            }
        }

        // When the slider for the top weight is changed, this will make the proper adjustments.
        class TopWeightSliderListener implements ChangeListener {
            public void stateChanged(ChangeEvent evt) {
                int val = topWeightSlider.getValue();
                m1 = val;
            }
        }

        // When the slider for the bottom weight is changed, this will make the proper adjustments.
        class BottomWeightSliderListener implements ChangeListener {
            public void stateChanged(ChangeEvent evt) {
                int val = bottomWeightSlider.getValue();
                m2 = val;
            }
        }

        // When the slider for gravity is changed, this will make the proper adjustments.
        class GravitySliderListener  implements ChangeListener {
            public void stateChanged(ChangeEvent evt) {
                int val = gravSlider.getValue();
                g = val;
            }
        }
    
        // When either of the buttons are changed, this resets certain variables accordingly. To pause
        // and resume, the timer is either stopped or started.
        class ButtonListener implements ActionListener {
            public void actionPerformed(ActionEvent evt) {
                String button = evt.getActionCommand();
                if (button.equalsIgnoreCase("Reset Position")) {
                    a1 = Math.PI/2;
                    a2 = Math.PI/2;
                    a1_velocity = 0.0;
                    a2_velocity = 0.0;
                    a1_acceleration = 0.0;
                    a2_acceleration = 0.0;
                } else if (button.equalsIgnoreCase("Reset Pendulum")) {
                    l1 = 200;
                    l2 = 200;
                    m1 = 40;
                    m2 = 40;
                    a1 = Math.PI/2;
                    a2 = Math.PI/2;
                    a1_velocity = 0.0;
                    a2_velocity = 0.0;
                    a1_acceleration = 0.0;
                    a2_acceleration = 0.0;
                    x1 = 0;
                    y1 = 0;
                    x2 = 0;
                    y2 = 0;
                    g = 1.0;
                    armLength1Slider.setValue(200);
                    armLength2Slider.setValue(200);
                    topWeightSlider.setValue(40);
                    bottomWeightSlider.setValue(40);
                    gravSlider.setValue(1);
                } else if (button.equalsIgnoreCase("Pause")) {
                    drawTimer.stop();
                } else if (button.equalsIgnoreCase("Resume")) {
                    drawTimer.start();
                }
            }
        }
    }
}