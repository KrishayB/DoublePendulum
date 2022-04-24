package DoublePendulum;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;

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

class PendulumHolder extends JPanel {
    public PendulumHolder() {
        setLayout(new BorderLayout());

        PendulumPanel pendulumPan = new PendulumPanel();
        add(pendulumPan, BorderLayout.CENTER);

        PendulumPanel.PendulumControlPanel pendulumControl = pendulumPan.new PendulumControlPanel();
        add(pendulumControl, BorderLayout.EAST);

    }
}

class PendulumPanel extends JPanel implements ActionListener {
    private int r1 = 200; // First arm length
    private int r2 = 200; // Second arm length
    private int m1 = 40; // Mass of first point
    private int m2 = 40; // Mass of second point
    private double a1 = Math.PI/2; // First angle
    private double a2 = Math.PI/2; // Second angle
    private double a1_v = 0.0; // Angle 1 velocity
    private double a2_v = 0.0; // Angle 2 velocity
    private double a1_a = 0.0; // Angle 1 acceleration
    private double a2_a = 0.0; // Angle 2 acceleration
    private int x1, y1, x2, y2; // Positions of first point
    private double g = 1.0; // Gravitational constant for amount of gravity

    private final int horizontalShift = 450; // Shifts the pendulum left and right
    private final int verticalShift = 200; // Shifts the pendulum up and down

    private Timer drawTimer;

    public PendulumPanel() {
        setBackground(Color.BLACK);
        drawTimer = new Timer(15, this);
        drawTimer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        performCalculations();
        g.setColor(Color.WHITE);

        g.drawLine(0 + horizontalShift, 0 + verticalShift, x1 + horizontalShift, y1 + verticalShift); // Draws a line from origin to first point
        g.fillOval(x1 + horizontalShift - m1/2, y1 + verticalShift - m1/2, m1, m1); // Draws an oval at the first point

        g.drawLine(x1 + horizontalShift, y1 + verticalShift, x2 + horizontalShift, y2 + verticalShift); // Draws a line from first point to second point
        g.fillOval(x2 + horizontalShift - m2/2, y2 + verticalShift - m2/2, m2, m2); // Draws an oval at the second point
    }

    public void performCalculations()
    {
        double num1 = -g * (2*m1 + m2) * Math.sin(a1);
        double num2 = -m2 * g * Math.sin(a1 - 2*a2);
        double num3 = -2 * Math.sin(a1 - a2) * m2;
        double num4 = (a2_v*a2_v) * r2 + (a1_v*a1_v) * r1 * Math.cos(a1 - a2);
        double den = r1 * (2 * m1 + m2 - m2 * Math.cos(2*a1 - 2*a2));
        a1_a = (num1 + num2 + num3*num4)/den;

        
        num1 = 2 * Math.sin(a1 - a2);
        num2 = (a1_v * a1_v) * r1 * (m1 + m2);
        num3 = g * (m1 + m2) * Math.cos(a1);
        num4 = (a2_v*a2_v) * r2 * m2 * Math.cos(a1 - a2);
        den = r2 * (2 * m1 + m2 - m2 * Math.cos(2*a1 - 2*a2));
        a2_a = (num1 * (num2 + num3 + num4))/den;

        x1 = (int)(r1 * Math.sin(a1));
        y1 = (int)(r1 * Math.cos(a1));

        x2 = (int)(r2 * Math.sin(a2) + x1);
        y2 = (int)(r2 * Math.cos(a2) + y1);

        a1_v += a1_a; // Adjusts velocity before force
        a2_v += a2_a;
        a1 += a1_v; // Adjusts angle position
        a2 += a2_v;

        // Can simulate air resistance
        a1_v *= 0.999;
        a2_v *= 0.999;
    }

    public void actionPerformed(ActionEvent evt) {
        repaint();
    }

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
    
            // Top Arm JLabel and JSlider
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

            // Bottom Arm JLabel and JSlider
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

            // Top Weight JLabel and JSlider
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

            // Bottom Weight JLabel and JSlider
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

            JButton resetPendulumPos = new JButton("Reset Position");
            resetPendulumPos.addActionListener(buttonsListener);
            add(resetPendulumPos);

            JButton resetPendulum = new JButton("Reset Pendulum");
            resetPendulum.addActionListener(buttonsListener);
            add(resetPendulum);

            JButton pause = new JButton("Pause");
            pause.addActionListener(buttonsListener);
            add(pause);

            JButton resume = new JButton("Resume");
            resume.addActionListener(buttonsListener);
            add(resume);
        }
    
        public JSlider makeSlider(int min, int max, int val, int ticksSpacing) {
            JSlider slider = new JSlider(min, max, val);
            slider.setMajorTickSpacing(ticksSpacing);
            slider.setPaintTicks(true);
            slider.setLabelTable(slider.createStandardLabels(ticksSpacing));
            slider.setPaintLabels(true);
            slider.setOrientation(JSlider.HORIZONTAL);
            return slider;
        }
    
        class AL1SliderListener implements ChangeListener {
            public void stateChanged(ChangeEvent evt) {
                int val = armLength1Slider.getValue();
                r1 = val;
            }
        }

        class AL2SliderListener implements ChangeListener {
            public void stateChanged(ChangeEvent evt) {
                int val = armLength2Slider.getValue();
                r2 = val;
            }
        }

        class TopWeightSliderListener implements ChangeListener {
            public void stateChanged(ChangeEvent evt) {
                int val = topWeightSlider.getValue();
                m1 = val;
            }
        }

        class BottomWeightSliderListener implements ChangeListener {
            public void stateChanged(ChangeEvent evt) {
                int val = bottomWeightSlider.getValue();
                m2 = val;
            }
        }

        class GravitySliderListener  implements ChangeListener {
            public void stateChanged(ChangeEvent evt) {
                int val = gravSlider.getValue();
                g = val;
            }
        }
    
        class ButtonListener implements ActionListener {
            public void actionPerformed(ActionEvent evt) {
                String button = evt.getActionCommand();
                if (button.equalsIgnoreCase("Reset Position")) {
                    a1 = Math.PI/2;
                    a2 = Math.PI/2;
                    a1_v = 0.0;
                    a2_v = 0.0;
                    a1_a = 0.0;
                    a2_a = 0.0;
                } else if (button.equalsIgnoreCase("Reset Pendulum")) {
                    r1 = 200;
                    r2 = 200;
                    m1 = 40;
                    m2 = 40;
                    a1 = Math.PI/2;
                    a2 = Math.PI/2;
                    a1_v = 0.0;
                    a2_v = 0.0;
                    a1_a = 0.0;
                    a2_a = 0.0;
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