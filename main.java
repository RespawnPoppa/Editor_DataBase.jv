package compiler4_highlight_26mayo23;

import java.sql.*;

public class main {

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        menu test1 = new menu(); // create menu instance
        test1.setVisible(true); // show menu window

    }

    void consultData(){}
    // send query and database to compile and returnsinfo
    public static void sendDataAndReturn(String query, String dbName) {
        String host = "localhost";
        String port = "3306";
        String database = dbName;
        String username = "root";
        String password = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
            Statement stmt = conn.createStatement();

            String[] queryArr = query.split(";");
            for (String q : queryArr) {
                if (q.trim().length() > 0) {
                    stmt.execute(q); //prev executeUpdate
                }
            }

            ResultSet rs = stmt.getResultSet();
            if (rs != null) {
                ResultSetMetaData metaData = rs.getMetaData();

                // Print column names
                int columnCount = metaData.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(metaData.getColumnName(i) + "\t");
                }
                System.out.println();

                // Print data
                while (rs.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        System.out.print(rs.getString(i) + "\t");
                    }
                    System.out.println();
                }

                rs.close();
            }

            stmt.close();
            conn.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    static void sendData(String query){
       String host = "localhost";
       String port = "3306";
       String database = "sqlcompiler";
       String username = "root";
       String password = null;

       try {
           Class.forName("com.mysql.cj.jdbc.Driver");
           Connection con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);

           String[] queryArr = query.split(";");
           for (String q : queryArr) {
               if (q.trim().length() > 0) {
                   Statement stmt = con.createStatement();
                   stmt.executeUpdate(q);
               }
           }

           con.close();
       } catch (Exception e) {
           System.out.println(e);
       }
   }

    void testConnection(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/sqlcompiler", "root", "a!bb2(2)()()[],.-[]!");

            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select * from user");

//          while(rs.next())
//            {System.out.println(rs.getInt(1)+" "+rs.getString(2)+" " +rs.getInt(3)+ " " +rs.getString(4));}
            while(rs.next()) {
                int id = rs.getInt("id_user");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String password = rs.getString("password");
                int platformId = rs.getInt("platform_id");

                System.out.println(id + " " + name + " " + email + " " + password + " " + platformId);
            }
            con.close();
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(e);
        }

    }
    }




