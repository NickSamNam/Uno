import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Created by Eversdijk on 15-5-2017.
 */
public class GUI extends JFrame {

    public static void main(String[] args) {
        new GUI();
    }

    JPanel sourcesListPanel, left, grid;
    JScrollPane sourcesScrollPane;
    ArrayList<Point> sources = new ArrayList<>();
    ArrayList<ArrayList<JButton>> buttonList = new ArrayList<>();
    TextField XSourcesTF, YSourcesTF;

    public GUI() {
        super("Pathfinder");
        Dimension screenSize = (Toolkit.getDefaultToolkit().getScreenSize());
        double screenWidth = screenSize.getWidth();
        double screenHeight = screenSize.getHeight();
        setSize((int) screenWidth / 2, (int) screenHeight / 2);
        setMinimumSize(new Dimension((int) screenWidth / 2, (int) screenHeight / 2));
        setExtendedState(Frame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);


        JPanel content = new JPanel(new BorderLayout());
        add(content);

        left = new JPanel(new GridLayout(5, 0));

        JPanel targetPanel = new JPanel(new GridLayout(2, 0));

        JLabel targetLabel = new JLabel("Coördinate for the target");

        JPanel targetInput = new JPanel(new GridLayout(3, 2));
        JLabel XTargetLabel = new JLabel("X = ");
        TextField XTargetTF = new TextField();
        JLabel YTargetLabel = new JLabel("Y = ");
        TextField YTargetTF = new TextField();
        JLabel targetCoordinatLabel = new JLabel();
        JButton targetButton = new JButton("Set target");
        targetButton.addActionListener(e -> {
            targetCoordinatLabel.setText("X = " + Integer.parseInt(XTargetTF.getText()) + ", Y = " + Integer.parseInt(YTargetTF.getText()));
            setVisible(true);});


        targetInput.add(XTargetLabel);
        targetInput.add(XTargetTF);
        targetInput.add(YTargetLabel);
        targetInput.add(YTargetTF);
        targetInput.add(targetCoordinatLabel);
        targetInput.add(targetButton);

        targetPanel.add(targetLabel);
        targetPanel.add(targetInput);
        targetPanel.setBorder(BorderFactory.createRaisedBevelBorder());

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
            grid = makeGrid(Integer.parseInt(XGridTF.getText()), Integer.parseInt(YGridTF.getText()));
            content.add(grid, BorderLayout.CENTER);
            setVisible(true);});

        gridInput.add(XGridLabel);
        gridInput.add(XGridTF);
        gridInput.add(YGridLabel);
        gridInput.add(YGridTF);
        gridInput.add(new JLabel());
        gridInput.add(gridButton);

        gridPanel.add(gridLabel);
        gridPanel.add(gridInput);
        gridPanel.setBorder(BorderFactory.createRaisedBevelBorder());



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
            ArrayList<Point> accessiblePoints = new ArrayList<>();
            ArrayList<Point> sources = new ArrayList<>();
            Point target;
            for(int y = 0; y < buttonList.size(); y++){
                for(int x = 0; x < buttonList.get(y).size(); x++){
                    JButton button = buttonList.get(y).get(x);
                    if(button.getBackground() == Color.green){
                        accessiblePoints.add(new Point(x,y));
                    } else if(button.getBackground() == Color.blue){
                        accessiblePoints.add(new Point(x,y));
                        sources.add(new Point(x,y));
                    }
                    else if(button.getBackground() == Color.red){
                        accessiblePoints.add(new Point(x,y));
                        target = new Point(x,y);
                    }
                }
            }

        });

        left.add(instructionsPanel);
        left.add(new JPanel());
        left.add(gridPanel);
        left.add(new JPanel());
        left.add(submit);


        grid = makeGrid(15, 15);

        content.add(left, BorderLayout.WEST);
        content.add(grid, BorderLayout.CENTER);
        setVisible(true);
    }

    public JPanel makeGrid(int x, int y) {
        JPanel newGrid = new JPanel(new GridLayout(y, x));
        buttonList.clear();
        for (int i = 0; i < y; i++) {
            ArrayList<JButton> buttons = new ArrayList<>();
            for (int j = 0; j < x; j++) {
                JButton button = new JButton("" + (j+1) + "," + (i+1));
                button.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if(e.isControlDown()){
                            button.setBackground(null);
                        }else if(e.isShiftDown()){
                            button.setBackground(Color.green);
                        }else if(e.getButton() == MouseEvent.BUTTON3){
                            button.setBackground(Color.red);
                        }else{ button.setBackground(Color.blue);
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
                        if(e.isControlDown()){
                            button.setBackground(null);
                        } else if(e.isShiftDown()){
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
}
