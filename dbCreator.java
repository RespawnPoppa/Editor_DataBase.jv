package compiler4_highlight_26mayo23;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class dbCreator extends JFrame {

    // frame
    int width = 400;
    int height = 550;
    int x = 100;
    int y = 100;
    String title = "Menu";
    int i = 0;

    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static String DATABASE = "";
    private static final String USERNAME = "root";
    private static final String PASSWORD = null;

    public dbCreator() {
        setTitle(title);
        setSize(width, height);
        setVisible(true);
        setLocation(x, y);
        setLayout(null);
        setLocationRelativeTo(null); //cneter at the top
        setVisible(true);
        setResizable(false);

        // Set background and text color
        getContentPane().setBackground(new Color(40, 42, 54));
        getContentPane().setForeground(new Color(248, 248, 242));

        // GUI
        JTextField dbField;
        JButton dbBttn;


        dbField = new JTextField();
        dbField.setBounds(120, 180, 150, 30);
        add(dbField);

        dbBttn = new JButton("Crear DB");
        dbBttn.setBounds(120, 230, 150, 30);
        add(dbBttn);

        // dbBttn action listener
        dbBttn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String dbName = dbField.getText().trim();
                if (!dbName.isEmpty()) {
                    createDB(dbName);
                } else {
                    JOptionPane.showMessageDialog(null, "Introduzca nombre de base de datos.");
                }
            }
        });
    }
    // create db method
    private void createDB(String dbName) {
        try {
            // create db connection
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://" + HOST + ":" + PORT + "/", USERNAME, PASSWORD);
            Statement stmt = conn.createStatement();
            String query = "CREATE DATABASE IF NOT EXISTS " + dbName +";";
            stmt.executeUpdate(query);
            JOptionPane.showMessageDialog(null, "Base de datos '" + dbName + "' creada exitosamente!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al crear base de datos: " + e.getMessage());
        }
    }

    }
