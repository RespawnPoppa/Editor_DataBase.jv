package compiler4_highlight_26mayo23;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.*;

public class tables extends JFrame {

    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static String DATABASE = "mysqleditor";
    private static final String USERNAME = "root";
    private static final String PASSWORD = null;

    //combobox
    private JComboBox<String> tableList;
    private JComboBox<String> dbList; //NEW: combobox

    public tables() {
        setTitle("Tables");
        setSize(800, 800);
        setLocationRelativeTo(null);

        // Jpanel with flowLayout
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        //NEW: init combobox dbList
        dbList = new JComboBox<String>();
        dbList.setPreferredSize(new Dimension(140, 30));
        dbList.addPopupMenuListener(new PopupMenuListener() {
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                updatedbList();
            }
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
            public void popupMenuCanceled(PopupMenuEvent e) {}
        });
        dbList.setBounds(0, 30, 85, 25);
        topPanel.add(dbList);

        //NEW: dbList Logic
        dbList.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) { // ONLY update when item is selected
                    updateTableList(); // Update tableList with tables only from selected db
                }
            }
        });

        //init combobox tableList
        tableList = new JComboBox<String>();
        tableList.setPreferredSize(new Dimension(100, 30));
        tableList.addPopupMenuListener(new PopupMenuListener() {
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                updateTableList();
            }
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
            public void popupMenuCanceled(PopupMenuEvent e) {}
        });
        tableList.setBounds(300, 30, 85, 25);
        topPanel.add(tableList);

        //bttn consult loc
        JButton consult = new JButton("Consult");
        consult.setBounds(400, 30, 85, 25);
        topPanel.add(consult);
        add(topPanel, BorderLayout.NORTH);

        //create scrollPane
        JScrollPane scrollPane = new JScrollPane(); //NEW: REMOVED TABLE

        //bttn consult logic
        consult.addActionListener(e -> {
            String dbName = (String) dbList.getSelectedItem(); // NEW: db name
            String tableName = (String) tableList.getSelectedItem();
            System.out.println("Consult db: " +dbName +" table: " +tableName); //NEW: added db name. PREV: ("Consult: " + tableName)
            String[] columnNames = getColumnNamesFromDatabase(tableName);
            Object[][] newData = getDataFromDatabase(tableName); // get data from database

            // create table model with new data
            DefaultTableModel model = new DefaultTableModel(newData, columnNames);

            // create JTable with table model
            JTable newTable = new JTable(model);

            // set table as content in JScrollPan
            scrollPane.setViewportView(newTable);
        });

        //NEW: moved
        updateTableList();

        //conf scrollPane
        scrollPane.setPreferredSize(new Dimension(800, 400));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    //fill tbl
    private Object[][] getDataFromDatabase(String tableName) {
        Object[][] data = null;
        String dbName = (String) dbList.getSelectedItem(); // NEW: db name
        DATABASE = dbName; // NEW: db name

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE,
                    USERNAME, PASSWORD);
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            data = new Object[getRowCount(resultSet)][columnCount];

            int i = 0;
            while (resultSet.next()) {
                for (int j = 0; j < columnCount; j++) {
                    data[i][j] = resultSet.getObject(j + 1);
                }
                i++;
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    //get rows
    private int getRowCount(ResultSet resultSet) {
        int count = 0;

        try {
            resultSet.last();
            count = resultSet.getRow();
            resultSet.beforeFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public static void main(String[] args) {
        new tables();
    }

    //updte tableList combobox with tbles
    private void updateTableList() {
    String dbName = (String) dbList.getSelectedItem(); // NEW: db name
    DATABASE = dbName; // NEW: db name

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE,
                    USERNAME, PASSWORD);
            DatabaseMetaData dbMetaData = connection.getMetaData();
            ResultSet tables = dbMetaData.getTables(DATABASE, null, null, new String[]{"TABLE"});
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
            while (tables.next()) {
                model.addElement(tables.getString("TABLE_NAME"));
            }
            tableList.setModel(model);
            tables.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //NEW: updt dbList combobox w dbs
    private void updatedbList() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://" + HOST + ":" + PORT,
                    USERNAME, PASSWORD);
            ResultSet resultSet = connection.getMetaData().getCatalogs();
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
            while (resultSet.next()) {
                model.addElement(resultSet.getString("TABLE_CAT"));
            }
            dbList.setModel(model);
            resultSet.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //get colnames for tbl
    private String[] getColumnNamesFromDatabase(String tableName) {
        String[] columnNames = null;
        String dbName = (String) dbList.getSelectedItem(); // NEW: db name
        DATABASE = dbName; // NEW: db name

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE,
                    USERNAME, PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName + " LIMIT 0");
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            columnNames = new String[columnCount];

            for (int i = 0; i < columnCount; i++) {
                columnNames[i] = metaData.getColumnName(i + 1);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return columnNames;
    }


}
