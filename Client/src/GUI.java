import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Created by Eversdijk on 15-5-2017.
 */
public class GUI extends JFrame {

    private JPanel left, grid;
    private ArrayList<ArrayList<JButton>> buttonList = new ArrayList<>();
    private Point target = null;
    private JLabel errorMessage = new JLabel();
    private ArrayList<Point> sources;

    public GUI(OnDataSubmissionListener submissionListener, WindowListener windowListener) {
        super("Pathfinder");
        Dimension screenSize = (Toolkit.getDefaultToolkit().getScreenSize());
        double screenWidth = screenSize.getWidth();
        double screenHeight = screenSize.getHeight();
        setSize((int) screenWidth / 2, (int) screenHeight / 2);
        setMinimumSize(new Dimension((int) screenWidth / 2, (int) screenHeight / 2));
        setExtendedState(Frame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(windowListener);

        JPanel content = new JPanel(new BorderLayout());
        add(content);

        left = new JPanel(new GridLayout(5, 0));


        JPanel gridPanel = new JPanel(new GridLayout(0, 1));
        JLabel gridLabel = new JLabel("size of the grid");

        JPanel gridInput = new JPanel(new GridLayout(3, 2));
        JLabel XGridLabel = new JLabel("X = ");
        TextField XGridTF = new TextField();
        JLabel YGridLabel = new JLabel("Y = ");
        TextField YGridTF = new TextField();
        JButton gridButton = new JButton("Set grid size");
        gridButton.addActionListener(e -> {
            content.remove(grid);
            if (isValid(XGridTF.getText()) && isValid(YGridTF.getText())) {
                grid = makeGrid(Integer.parseInt(XGridTF.getText()), Integer.parseInt(YGridTF.getText()));
                content.add(grid, BorderLayout.CENTER);
                setVisible(true);
            }
        });

        gridInput.add(XGridLabel);
        gridInput.add(XGridTF);
        gridInput.add(YGridLabel);
        gridInput.add(YGridTF);
        gridInput.add(errorMessage);
        gridInput.add(gridButton);

        gridPanel.add(gridLabel);
        gridPanel.add(gridInput);
        gridPanel.setBorder(BorderFactory.createRaisedBevelBorder());

        JPanel selectionPanel = new JPanel(new GridLayout(3, 2));

        JButton selectAll = new JButton("Select all");
        selectAll.addActionListener(e -> {
            for (ArrayList<JButton> buttons : buttonList) {
                for (JButton b : buttons) {
                    target = null;
                    b.setBackground(Color.green);
                }
            }
        });
        JButton deselectAll = new JButton("Deselect all");
        deselectAll.addActionListener(e -> {
            for (ArrayList<JButton> buttons : buttonList) {
                for (JButton b : buttons) {
                    b.setBackground(null);
                    target = null;
                }
            }
        });
        JButton inverseAll = new JButton("Inverse all");
        inverseAll.addActionListener(e -> {
            for (ArrayList<JButton> buttons : buttonList) {
                for (JButton b : buttons) {

                    if (b.getBackground() == Color.green)
                        b.setBackground(null);
                    else if (b.getBackground() != Color.blue && b.getBackground() != Color.red)
                        b.setBackground(Color.green);
                }
            }
        });

        selectionPanel.add(new JPanel());
        selectionPanel.add(selectAll);
        selectionPanel.add(new JPanel());
        selectionPanel.add(deselectAll);
        selectionPanel.add(new JPanel());
        selectionPanel.add(inverseAll);
        selectionPanel.setBorder(BorderFactory.createRaisedBevelBorder());

        JPanel instructionsPanel = new JPanel(new GridLayout(0, 1));

        JLabel instructionsLabel = new JLabel("Instructions");
        JLabel instructionsctrlLabel = new JLabel("Hold shift & drag or click for an accessible point");
        JLabel instructionsshiftLabel = new JLabel("Hold control & drag or click for to delete an accessible point");
        JLabel instructionsclickLabel = new JLabel("Click left mouse button for a source");
        JLabel instructionstargetLabel = new JLabel("Click right mouse button for a target");
        JLabel instructionstargetLabel2 = new JLabel("There can only be 1 target");

        instructionsPanel.add(instructionsLabel);
        instructionsPanel.add(instructionsctrlLabel);
        instructionsPanel.add(instructionsshiftLabel);
        instructionsPanel.add(instructionsclickLabel);
        instructionsPanel.add(instructionstargetLabel);
        instructionsPanel.add(instructionstargetLabel2);
        instructionsPanel.setBorder(BorderFactory.createRaisedBevelBorder());

        /**
         * SUBMIT knop
         */

        JButton submit = new JButton("Submit");
        submit.addActionListener(e -> {
            if (target != null) {
                ArrayList<Point> accessiblePoints = new ArrayList<>();
                sources = new ArrayList<>();
                for (int y = 0; y < buttonList.size(); y++) {
                    for (int x = 0; x < buttonList.get(y).size(); x++) {
                        JButton button = buttonList.get(y).get(x);
                        if (button.getBackground() == Color.green || button.getBackground() == Color.yellow) {
                            accessiblePoints.add(new Point(x, y));
                        } else if (button.getBackground() == Color.blue) {
                            accessiblePoints.add(new Point(x, y));
                            sources.add(new Point(x, y));
                        } else if (button.getBackground() == Color.red) {
                            accessiblePoints.add(new Point(x, y));
                        }
                    }
                }
                submissionListener.sendData(accessiblePoints, sources, target);
            } else {
                errorMessage.setText("there is no target.");
            }
        });

        left.add(instructionsPanel);
        left.add(new JPanel());
        left.add(gridPanel);
        left.add(selectionPanel);
        left.add(submit);


        grid = makeGrid(15, 15);

        content.add(left, BorderLayout.WEST);
        content.add(grid, BorderLayout.CENTER);
        setVisible(true);
    }

    private JPanel makeGrid(int x, int y) {
        JPanel newGrid = new JPanel(new GridLayout(y, x));
        buttonList.clear();
        for (int i = 0; i < y; i++) {
            ArrayList<JButton> buttons = new ArrayList<>();
            for (int j = 0; j < x; j++) {
                JButton button = new JButton("" + (j) + "," + (i));
                final int yy = i;
                final int xx = j;
                button.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if (e.isControlDown()) {
                            button.setBackground(null);
                        } else if (e.isShiftDown()) {
                            button.setBackground(Color.green);
                        } else if (e.getButton() == MouseEvent.BUTTON3) {
                            if (target != null) {
                                buttonList.get(target.y).get(target.x).setBackground(Color.green);
                            }
                            button.setBackground(Color.red);
                            target = new Point(xx, yy);
                        } else {
                            button.setBackground(Color.blue);
                        }
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {

                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (e.isControlDown()) {
                            button.setBackground(null);
                        } else if (e.isShiftDown()) {
                            button.setBackground(Color.green);
                        }
                    }

                    @Override
                    public void mouseClicked(MouseEvent e) {

                    }
                });
                buttons.add(button);
                newGrid.add(button);
            }
            buttonList.add(buttons);
        }
        return newGrid;
    }

    public interface OnDataSubmissionListener {
        void sendData(List<Point> accessiblePoints, List<Point> sources, Point target);
    }

    public void processData(float calcTime, Map<Point, List<Point>> paths) {
        System.out.println("Calculation time: " + calcTime + "s");
        resetPaths();
        for (Point mapP : sources) {
            for (Point p : paths.get(mapP)) {
                JButton b = buttonList.get(p.y).get(p.x);
                if (b.getBackground() == Color.green)
                    b.setBackground(Color.yellow);
            }
        }
        System.out.println(paths);
    }

    private boolean isValid(String input) {
        boolean valid = true;
        try {
            if (Long.parseLong(input) < 1) {
                errorMessage.setText("Minimal size of 1 required.");
                valid = false;
            } else if (Long.parseLong(input) > Integer.MAX_VALUE) {
                errorMessage.setText("Maximum width of " + Integer.MAX_VALUE + " allowed.");
                valid = false;
            }
        } catch (NumberFormatException e) {
            errorMessage.setText("No valid input");
            valid = false;
        }
        setVisible(true);
        return valid;
    }

    private void resetPaths() {
        for (int i = 0; i < buttonList.size(); i++) {
            ArrayList<JButton> buttons = buttonList.get(i);
            for (int j = 0; j < buttons.size(); j++) {
                if (buttons.get(j).getBackground() == Color.yellow)
                    buttons.get(j).setBackground(Color.green);
            }
        }
    }
}
