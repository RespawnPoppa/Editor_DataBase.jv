package compiler4_highlight_26mayo23;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class menu extends JFrame {

    // frame
    int width = 400;
    int height = 550;
    int x = 100;
    int y = 100;
    String title = "Menu";
    int i = 0;

    public menu() {
        // super("Menu");
        setTitle(title);
        setSize(width, height);
        setVisible(true);
        setLocation(x, y);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // center at the top
        setVisible(true);
        setResizable(false);

        // background and text color
        getContentPane().setBackground(new Color(40, 42, 54));
        getContentPane().setForeground(new Color(248, 248, 242));

        // NEW: Create createdb bttn
        JButton dbButton = new JButton("Crear DB");
        dbButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dbCreator test2 = new dbCreator(); // create instance
                test2.setVisible(true); // show window

            }
        });

        // Create code editor bttn
        JButton editorButton = new JButton("Code Editor");
        editorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeEditor test3 = new codeEditor(); // instance
                test3.setVisible(true); // show

            }
        });

        // Create tables bttn
        JButton tablesButton = new JButton("Tables");
        tablesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //JOptionPane.showMessageDialog(menu.this, "Tables button clicked!");
                tables test4 = new tables(); // instance
                test4.setVisible(true); // show
            }
        });

        //add bttns
        dbButton.setBounds(140, 150, 100,40);
        add(dbButton);
        editorButton.setBounds(140, 200, 100,40);
        add(editorButton);
        tablesButton.setBounds(140, 250, 100,40);
        add(tablesButton);

    }
}
