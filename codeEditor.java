package compiler4_highlight_26mayo23;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Arrays;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.Color;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;


public class codeEditor extends JFrame {
    // frame
    int width = 900;
    int height = 900;
    int x = 100;
    int y = 100;
    String title = "Lexer";
    int i = 0;

    //NEW: db
    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static String DATABASE = "";
    private static final String USERNAME = "root";
    private static final String PASSWORD = null;

    static String dbName = ""; //class lvl

    // NEW: GUI
    JButton consult;
    JTextPane  textPane = new JTextPane();
    JComboBox<String> dbList;
    StyledDocument doc = textPane.getStyledDocument(); //new3

    public codeEditor() {

        //frame
        setTitle(title);
        setSize(width, height);
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // center at the top
        setVisible(true);


        // NEW: JPanel with FlowLayout
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // NEW: COMBOBOX Initialize combo box
        dbList = new JComboBox<>();
        dbList.setPreferredSize(new Dimension(140, 30));
        dbList.addPopupMenuListener(new PopupMenuListener() {
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                updatedbList(); //TODO
            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });
        topPanel.add(dbList);

        // NEW: Add bttn
        consult = new JButton("Seleccionar");
        topPanel.add(consult);
        add(topPanel, BorderLayout.NORTH);

        // NEW: consult action listener
        consult.addActionListener(e -> {
            dbName = (String) dbList.getSelectedItem(); // declaring here makes it unusuable
            System.out.println("SELECCIÓN: " + dbName);
            // TODO
        });

        // NEW: JScrollPane with textpane
        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        // key bindings for code autocomplete
        InputMap inputMap = textPane.getInputMap(JTextComponent.WHEN_FOCUSED); //prev textArea.getInputMap();
        ActionMap actionMap = textPane.getActionMap();
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, KeyEvent.CTRL_DOWN_MASK), "showAutoComplete");
        actionMap.put("showAutoComplete", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAutoComplete(textPane);
            }
        });

        // compile with select
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.CTRL_DOWN_MASK), "sendData");
        actionMap.put("sendData", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the text from the text area
                StyledDocument doc = textPane.getStyledDocument();
                String text = "";
                try {
                    text = doc.getText(0, doc.getLength());
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
                //print query
                System.out.println("Query executed:");
                System.out.println(text);
                // Pass text and db to sendDataAndReturn method in main
                main.sendDataAndReturn(text, dbName);
            }
        });

        // Add text pane to frame
        add(textPane, BorderLayout.CENTER);

        // Set default colors for background and txt
        setBackground(new Color(40, 42, 54));
        textPane.setBackground(new Color(40, 42, 54));
        textPane.setForeground(new Color(248, 248, 242));
        textPane.setFont(new Font("Verdana", Font.PLAIN, 14));
        textPane.setCaretColor(new Color(248, 248, 242));

        // default style color
        Style style = textPane.addStyle("DefaultStyle", textPane.getStyle("default"));
        StyleConstants.setForeground(style, new Color(248, 248, 242));
        doc.setLogicalStyle(0, style);

        // DocumentListener in JTextPane
        doc.addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(this::updateTextColor);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(this::updateTextColor);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // Plain text components (JtextPane) do not trigger these events
            }

            private void updateTextColor() {
                try {
                    // Get txt from  doc
                    String text = doc.getText(0, doc.getLength());

                    // numbers style
                    Style numberStyle = textPane.addStyle("NumberStyle", style);
                    StyleConstants.setForeground(numberStyle, new Color(189, 147, 249));

                    // keyword style
                    Style keywordStyle = textPane.addStyle("keywordStyle", style);
                    StyleConstants.setForeground(keywordStyle, new Color(255, 121, 198));

                    // Clear existing color attributes
                    doc.setCharacterAttributes(0, doc.getLength(), style, true);

                    // Find numbers and update their color
                    Pattern numberPattern = Pattern.compile("\\b\\d+\\b");
                    Matcher numberMatcher = numberPattern.matcher(text);

                    while (numberMatcher.find()) {
                        int start = numberMatcher.start();
                        int end = numberMatcher.end();
                        doc.setCharacterAttributes(start, end - start, numberStyle, false);
                    }

                    // Keywords to color
                    List<String> keywords = Arrays.asList(
                            "ACCESSIBLE", "ACCOUNT", "ACTION", "ACTIVE", "ADD", "ADMIN", "AFTER", "AGAINST",
                            "AGGREGATE", "ALGORITHM", "ALL", "ALTER", "ALWAYS", "ANALYSE", "ANALYZE", "AND",
                            "ANY", "ARRAY", "AS", "ASC", "ASCII", "ASENSITIVE", "AT", "ATTRIBUTE", "AUTHENTICATION",
                            "AUTOEXTEND_SIZE", "AUTO_INCREMENT", "AVG", "AVG_ROW_LENGTH", "BACKUP", "BEFORE", "BEGIN",
                            "BETWEEN", "BIGINT", "BINARY", "BINLOG", "BIT", "BLOB", "BLOCK", "BOOL", "BOOLEAN", "BOTH",
                            "BTREE", "BUCKETS", "BULK", "BYTE", "CACHE", "CALL", "CASCADE", "CASE", "CATALOG_NAME",
                            "CHAIN", "CHALLENGE_RESPONSE", "CHANGE", "CHANGED", "CHANNEL", "CHAR", "CHARACTER",
                            "CHARSET", "CHECK", "CHECKSUM", "CIPHER", "CLASS_ORIGIN", "CLIENT", "CLONE", "CLOSE",
                            "COALESCE", "CODE", "COLLATE", "COLLATION", "COLUMN", "COLUMNS", "COLUMN_FORMAT",
                            "COLUMN_NAME", "COMMENT", "COMMIT", "COMMITTED", "COMPACT", "COMPLETION", "COMPONENT",
                            "COMPRESSED", "COMPRESSION", "CONCURRENT", "CONDITION", "CONNECTION", "CONSISTENT",
                            "CONSTRAINT", "CONSTRAINT_CATALOG", "CONSTRAINT_NAME", "CONSTRAINT_SCHEMA", "CONTAINS",
                            "CONTEXT", "CONTINUE", "CONVERT", "CPU", "CREATE", "CROSS", "CUBE", "CUME_DIST", "CURRENT",
                            "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "CURSOR", "CURSOR_NAME",
                            "DATA", "DATABASE", "DATABASES", "DATAFILE", "DATE", "DATETIME", "DAY", "DAY_HOUR",
                            "DAY_MICROSECOND", "DAY_MINUTE", "DAY_SECOND", "DEALLOCATE", "DEC", "DECIMAL", "DECLARE",
                            "DEFAULT", "DEFAULT_AUTH", "DEFINER", "DEFINITION", "DELAYED", "DELAY_KEY_WRITE", "DELETE",
                            "DENSE_RANK", "DESC", "DESCRIBE", "DESCRIPTION", "DES_KEY_FILE", "DETERMINISTIC", "DIAGNOSTICS",
                            "DIRECTORY", "DISABLE", "DISCARD", "DISK", "DISTINCT", "DISTINCTROW", "DIV", "DO", "DOUBLE",
                            "DROP", "DUAL", "DUMPFILE", "DUPLICATE", "DYNAMIC", "EACH", "ELSE", "ELSEIF", "EMPTY",
                            "ENABLE", "ENCLOSED", "ENCRYPTION", "END", "ENDS", "ENFORCED", "ENGINE", "ENGINES",
                            "ENGINE_ATTRIBUTE", "ENUM", "ERROR", "ERRORS", "ESCAPE", "ESCAPED", "EVENT", "EVENTS",
                            "EVERY", "EXCEPT", "EXCHANGE", "EXCLUDE", "EXECUTE", "EXISTS", "EXIT", "EXPANSION", "EXPIRE",
                            "EXPLAIN", "EXPORT", "EXTENDED", "EXTENT_SIZE", "FALSE", "FAST", "FAULTS", "FETCH", "FIELDS",
                            "FILE", "FILE_BLOCK_SIZE", "FILTER", "FINISH", "FIRST", "FIRST_VALUE", "FIXED", "FLOAT",
                            "FLOAT4", "FLOAT8", "FLUSH", "FOLLOWING", "FOLLOWS", "FOR", "FORCE", "FOREIGN", "FORMAT",
                            "FOUND", "FROM", "FULL", "FULLTEXT", "FUNCTION", "GEOMCOLLECTION", "GEOMETRY",
                            "GEOMETRYCOLLECTION", "GET", "GET_FORMAT", "GET_MASTER_PUBLIC_KEY", "GET_SOURCE_PUBLIC_KEY",
                            "GLOBAL", "GRANT", "GRANTS", "GROUP", "GROUPING", "GROUPS", "GROUP_REPLICATION", "GTID_ONLY",
                            "HANDLER", "HASH", "HAVING", "HELP", "HIGH_PRIORITY", "HISTOGRAM", "HISTORY", "HOST", "HOSTS",
                            "HOUR", "HOUR_MICROSECOND", "HOUR_MINUTE", "HOUR_SECOND", "IDENTIFIED", "IF", "IGNORE",
                            "IGNORE_SERVER_IDS", "IMPORT", "IN", "INACTIVE", "INDEX", "INDEXES", "INFILE", "INITIAL",
                            "INITIAL_SIZE", "INITIATE", "INNER", "INOUT", "INSENSITIVE", "INSERT", "INSERT_METHOD",
                            "INSTALL", "INSTANCE", "INT", "INT1", "INT2", "INT3", "INT4", "INT8", "INTEGER", "INTERSECT",
                            "INTERVAL", "INTO", "INVISIBLE", "INVOKER", "IO", "IO_AFTER_GTIDS", "IO_BEFORE_GTIDS",
                            "IO_THREAD", "IPC", "IS", "ISOLATION", "ISSUER", "ITERATE", "JOIN", "JSON", "JSON_TABLE",
                            "JSON_VALUE", "KEY", "KEYRING", "KEYS", "KEY_BLOCK_SIZE", "KILL", "LAG", "LANGUAGE", "LAST",
                            "LAST_VALUE", "LATERAL", "LEAD", "LEADING", "LEAVE", "LEAVES", "LEFT", "LESS", "LEVEL",
                            "LIKE", "LIMIT", "LINEAR", "LINES", "LINESTRING", "LIST", "LOAD", "LOCAL", "LOCALTIME",
                            "LOCALTIMESTAMP", "LOCK", "LOCKED", "LOCKS", "LOGFILE", "LOGS", "LONG", "LONGBLOB",
                            "LONGTEXT", "LOOP", "LOW_PRIORITY", "MASTER", "MASTER_AUTO_POSITION", "MASTER_BIND",
                            "MASTER_COMPRESSION_ALGORITHMS", "MASTER_CONNECT_RETRY", "MASTER_DELAY",
                            "MASTER_HEARTBEAT_PERIOD", "MASTER_HOST", "MASTER_LOG_FILE", "MASTER_LOG_POS",
                            "MASTER_PASSWORD", "MASTER_PORT", "MASTER_PUBLIC_KEY_PATH", "MASTER_RETRY_COUNT",
                            "MASTER_SERVER_ID", "MASTER_SSL", "MASTER_SSL_CA", "MASTER_SSL_CAPATH", "MASTER_SSL_CERT",
                            "MASTER_SSL_CIPHER", "MASTER_SSL_CRL", "MASTER_SSL_CRLPATH", "MASTER_SSL_KEY",
                            "MASTER_SSL_VERIFY_SERVER_CERT", "MASTER_TLS_CIPHERSUITES", "MASTER_TLS_VERSION",
                            "MASTER_USER", "MASTER_ZSTD_COMPRESSION_LEVEL", "MATCH", "MAXVALUE",
                            "MAX_CONNECTIONS_PER_HOUR", "MAX_QUERIES_PER_HOUR", "MAX_ROWS", "MAX_SIZE",
                            "MAX_UPDATES_PER_HOUR", "MAX_USER_CONNECTIONS", "MEDIUM", "MEDIUMBLOB", "MEDIUMINT",
                            "MEDIUMTEXT", "MEMBER", "MEMORY", "MERGE", "MESSAGE_TEXT", "MICROSECOND", "MIDDLEINT",
                            "MIGRATE", "MINUTE", "MINUTE_MICROSECOND", "MINUTE_SECOND", "MIN_ROWS", "MOD", "MODE",
                            "MODIFIES", "MODIFY", "MONTH", "MULTILINESTRING", "MULTIPOINT", "MULTIPOLYGON", "MUTEX",
                            "MYSQL_ERRNO", "NAME", "NAMES", "NATIONAL", "NATURAL", "NCHAR", "NDB", "NDBCLUSTER",
                            "NESTED", "NETWORK_NAMESPACE", "NEVER", "NEW", "NEXT", "NO", "NODEGROUP", "NONE", "NOT",
                            "NOWAIT", "NO_WAIT", "NO_WRITE_TO_BINLOG", "NTH_VALUE", "NTILE", "NULL", "NULLS", "NUMBER",
                            "NUMERIC", "NVARCHAR", "OF", "OFF", "OFFSET", "OJ", "OLD", "ON", "ONE", "ONLY", "OPEN",
                            "OPTIMIZE", "OPTIMIZER_COSTS", "OPTION", "OPTIONAL", "OPTIONALLY", "OPTIONS", "OR", "ORDER",
                            "ORDINALITY", "ORGANIZATION", "OTHERS", "OUT", "OUTER", "OUTFILE", "OVER", "OWNER",
                            "PACK_KEYS", "PAGE", "PARSER", "PARTIAL", "PARTITION", "PARTITIONING", "PARTITIONS",
                            "PASSWORD", "PASSWORD_LOCK_TIME", "PATH", "PERCENT_RANK", "PERSIST", "PERSIST_ONLY", "PHASE",
                            "PLUGIN", "PLUGINS", "PLUGIN_DIR", "POINT", "POLYGON", "PORT", "PRECEDES", "PRECEDING",
                            "PRECISION", "PREPARE", "PRESERVE", "PREV", "PRIMARY", "PRIVILEGES", "PRIVILEGE_CHECKS_USER",
                            "PROCEDURE", "PROCESS", "PROCESSLIST", "PROFILE", "PROFILES", "PROXY", "PURGE",
                            "QUARTER", "QUERY", "QUICK", "RANDOM", "RANGE", "RANK", "READ", "READS", "READ_ONLY",
                            "READ_WRITE", "REAL", "REBUILD", "RECOVER", "RECURSIVE", "REDOFILE", "REDO_BUFFER_SIZE",
                            "REDUNDANT", "REFERENCE", "REFERENCES", "REGEXP", "REGISTRATION", "RELAY", "RELAYLOG",
                            "RELAY_LOG_FILE", "RELAY_LOG_POS", "RELAY_THREAD", "RELEASE", "RELOAD", "REMOTE", "REMOVE",
                            "RENAME", "REORGANIZE", "REPAIR", "REPEAT", "REPEATABLE", "REPLACE", "REPLICA", "REPLICAS",
                            "REPLICATE_DO_DB", "REPLICATE_DO_TABLE", "REPLICATE_IGNORE_DB", "REPLICATE_IGNORE_TABLE",
                            "REPLICATE_REWRITE_DB", "REPLICATE_WILD_DO_TABLE", "REPLICATE_WILD_IGNORE_TABLE",
                            "REPLICATION", "REQUIRE", "REQUIRE_ROW_FORMAT", "RESET", "RESIGNAL", "RESOURCE",
                            "RESPECT", "RESTART", "RESTORE", "RESTRICT", "RESUME", "RETAIN", "RETURN",
                            "RETURNED_SQLSTATE", "RETURNING", "RETURNS", "REUSE", "REVERSE", "REVOKE", "RIGHT",
                            "RLIKE", "ROLE", "ROLLBACK", "ROLLUP", "ROTATE", "ROUTINE", "ROW", "ROWS", "ROW_COUNT",
                            "ROW_FORMAT", "ROW_NUMBER", "RTREE", "SAVEPOINT", "SCHEDULE", "SCHEMA", "SCHEMAS",
                            "SCHEMA_NAME", "SECOND", "SECONDARY", "SECONDARY_ENGINE", "SECONDARY_ENGINE_ATTRIBUTE",
                            "SECONDARY_LOAD", "SECONDARY_UNLOAD", "SECOND_MICROSECOND", "SECURITY", "SELECT",
                            "SENSITIVE", "SEPARATOR", "SERIAL", "SERIALIZABLE", "SERVER", "SESSION", "SET", "SHARE",
                            "SHOW", "SHUTDOWN", "SIGNAL", "SIGNED", "SIMPLE", "SKIP", "SLAVE", "SLOW", "SMALLINT",
                            "SNAPSHOT", "SOCKET", "SOME", "SONAME", "SOUNDS", "SOURCE", "SOURCE_AUTO_POSITION",
                            "SOURCE_BIND", "SOURCE_COMPRESSION_ALGORITHMS", "SOURCE_CONNECT_RETRY", "SOURCE_DELAY",
                            "SOURCE_HEARTBEAT_PERIOD", "SOURCE_HOST", "SOURCE_LOG_FILE", "SOURCE_LOG_POS",
                            "SOURCE_PASSWORD", "SOURCE_PORT", "SOURCE_PUBLIC_KEY_PATH", "SOURCE_RETRY_COUNT",
                            "SOURCE_SSL", "SOURCE_SSL_CA", "SOURCE_SSL_CAPATH", "SOURCE_SSL_CERT", "SOURCE_SSL_CIPHER",
                            "SOURCE_SSL_CRL", "SOURCE_SSL_CRLPATH", "SOURCE_SSL_KEY", "SOURCE_SSL_VERIFY_SERVER_CERT",
                            "SOURCE_TLS_CIPHERSUITES", "SOURCE_TLS_VERSION", "SOURCE_USER",
                            "SOURCE_ZSTD_COMPRESSION_LEVEL", "SPATIAL", "SPECIFIC", "SQL", "SQLEXCEPTION", "SQLSTATE",
                            "SQLWARNING", "SQL_AFTER_GTIDS", "SQL_AFTER_MTS_GAPS", "SQL_BEFORE_GTIDS", "SQL_BIG_RESULT",
                            "SQL_BUFFER_RESULT", "SQL_CACHE", "SQL_CALC_FOUND_ROWS", "SQL_NO_CACHE", "SQL_SMALL_RESULT",
                            "SQL_THREAD", "SQL_TSI_DAY", "SQL_TSI_HOUR", "SQL_TSI_MINUTE", "SQL_TSI_MONTH",
                            "SQL_TSI_QUARTER", "SQL_TSI_SECOND", "SQL_TSI_WEEK", "SQL_TSI_YEAR", "SRID", "SSL",
                            "STACKED", "START", "STARTING", "STARTS", "STATS_AUTO_RECALC", "STATS_PERSISTENT",
                            "STATS_SAMPLE_PAGES", "STATUS", "STOP", "STORAGE", "STORED", "STRAIGHT_JOIN", "STREAM",
                            "STRING", "SUBCLASS_ORIGIN", "SUBJECT", "SUBPARTITION", "SUBPARTITIONS", "SUPER", "SUSPEND",
                            "SWAPS", "SWITCHES", "SYSTEM", "TABLE", "TABLES", "TABLESPACE", "TABLE_CHECKSUM",
                            "TABLE_NAME", "TEMPORARY", "TEMPTABLE", "TERMINATED", "TEXT", "THAN", "THEN",
                            "THREAD_PRIORITY", "TIES", "TIME", "TIMESTAMP", "TIMESTAMPADD", "TIMESTAMPDIFF", "TINYBLOB",
                            "TINYINT", "TINYTEXT", "TLS", "TO", "TRAILING", "TRANSACTION", "TRIGGER", "TRIGGERS", "TRUE",
                            "TRUNCATE", "TYPE", "TYPES", "UNBOUNDED", "UNCOMMITTED", "UNDEFINED", "UNDO", "UNDOFILE",
                            "UNDO_BUFFER_SIZE", "UNICODE", "UNINSTALL", "UNION", "UNIQUE", "UNKNOWN", "UNLOCK",
                            "UNREGISTER", "UNSIGNED", "UNTIL", "UPDATE", "UPGRADE", "URL", "USAGE", "USE", "USER",
                            "USER_RESOURCES", "USE_FRM", "USING", "UTC_DATE", "UTC_TIME", "UTC_TIMESTAMP",
                            "VALIDATION", "VALUE", "VALUES", "VARBINARY", "VARCHAR", "VARCHARACTER", "VARIABLES",
                            "VARYING", "VCPU", "VIEW", "VIRTUAL", "VISIBLE", "WAIT", "WARNINGS",
                            "WEEK", "WEIGHT_STRING", "WHEN", "WHERE", "WHILE", "WINDOW", "WITH", "WITHOUT",
                            "WORK", "WRAPPER", "WRITE", "X509", "XA", "XID", "XML", "XOR", "YEAR", "YEAR_MONTH",
                            "ZEROFILL", "ZONE"
                    );

                    // Find keywords and update their color
                    for (String keyword : keywords) {
                        Pattern wordPattern = Pattern.compile("\\b" + keyword + "\\b", Pattern.CASE_INSENSITIVE);
                        Matcher wordMatcher = wordPattern.matcher(text);

                        while (wordMatcher.find()) {
                            int start = wordMatcher.start();
                            int end = wordMatcher.end();
                            doc.setCharacterAttributes(start, end - start, keywordStyle, false);
                        }
                    }
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }
        });
        //JOptionPane.showMessageDialog(null, new JScrollPane(textPane));

    }



    public static void main(String[] args) {
        codeEditor lexer = new codeEditor();
        lexer.setVisible(true);
    }

    //input singular-nonsingular
    static String[] beforeFROM = {"SELECT * ", "SELECT column_name ", "SELECT DISTINCT * ",
            "SELECT DISTINCT column_name " , "SELECT column_name AS title "};

    static String[] beforeTABLE = {"SELECT * FROM ", "SELECT column_name FROM ", "SELECT DISTINCT * FROM ",
            "SELECT DISTINCT column_name FROM ", "select column_name AS title FROM "};

    //colname
    static String[] afterWHERE1 = {
            "select column_name AS title FROM table_name WHERE ",
            "select column_name FROM table_name WHERE ",
            "select * FROM table_name WHERE ",
            "select column_name FROM table_name WHERE ",
    };

    static String[] afterWHERE2 = {
            "select column_name AS title FROM table_name WHERE CONDITION ",
            "select column_name AS title FROM table_name WHERE CONDITION AND CONDITION ",
            "select column_name AS title FROM table_name WHERE CONDITION OR CONDITION ",

            "select column_name FROM table_name WHERE CONDITION ",
            "select column_name FROM table_name WHERE CONDITION AND CONDITION ",
            "select column_name FROM table_name WHERE CONDITION OR CONDITION ",

            "select * FROM table_name WHERE CONDITION ",
            "select * FROM table_name WHERE CONDITION AND CONDITION ",
            "select * FROM table_name WHERE CONDITION OR CONDITION ",
    };

    static String[] afterWHERE3 = {"" +
            "select column_name AS title FROM table_name WHERE CONDITION AND ",
            "select column_name AS title FROM table_name WHERE CONDITION OR ",
            "select column_name FROM table_name WHERE CONDITION AND ",
            "select column_name FROM table_name WHERE CONDITION OR ",
            "select * FROM table_name WHERE CONDITION AND ",
            "select * FROM table_name WHERE CONDITION OR ",
            "select column_name FROM table_name WHERE CONDITION AND ",
            "select column_name FROM table_name WHERE CONDITION OR "};

    //BETWEEN, LIKE, IS NULL, IS NOT NULL
    static String[] afterWHERECOL = {"select column_name AS title FROM table_name WHERE column_name ",
            "select column_name FROM table_name WHERE column_name ",};

    static String[] afterBETWEEN = {
            "select column_name AS title FROM table_name WHERE column_name BETWEEN ",
            "select column_name FROM table_name WHERE column_name BETWEEN ",
    };

    static String[] afterBETWEEN1 = {
            "select column_name AS title FROM table_name WHERE column_name BETWEEN value1 ",
            "select column_name FROM table_name WHERE column_name BETWEEN value1 ",
    };

    static String[] afterBETWEEN2 = {
            "select column_name AS title FROM table_name WHERE column_name BETWEEN value1 AND ",
            "select column_name FROM table_name WHERE column_name BETWEEN value1 AND ",
    };

    static String[] afterLIKE = {
            "select column_name AS title FROM table_name WHERE column_name LIKE ",
            "select column_name FROM table_name WHERE column_name LIKE ",
    };

    static String[] groupBY1 = {
            "SELECT * FROM table_name ",
            "select column_name FROM table_name ",
            "select DISTINCT * FROM table_name ",
            "select DISTINCT column_name FROM table_name ",
            "select column_name AS title FROM table_name ",

            "SELECT * FROM table_name WHERE CONDITION ",
            "SELECT column_name FROM table_name WHERE column_name BETWEEN value1 AND value2 ",

            "SELECT column_name FROM table_name WHERE column_name LIKE text ",

            "SELECT column_name FROM table_name WHERE column_name IS NULL ",
            "SELECT column_name FROM table_name WHERE column_name IS NOT NULL ",
            "SELECT * FROM table_name WHERE CONDITION AND CONDITION ",
            "SELECT * FROM table_name WHERE CONDITION OR CONDITION ",
    };

    static String[] groupBY2 = {
            "SELECT * FROM table_name GROUP BY column_name ",
            "SELECT * FROM table_name WHERE CONDITION GROUP BY column_name ",
            "SELECT column_name FROM table_name WHERE column_name BETWEEN value1 AND value2 GROUP BY column_name ",
            "SELECT column_name FROM table_name WHERE column_name LIKE text GROUP BY column_name ",
            "SELECT column_name FROM table_name WHERE column_name IS NULL GROUP BY column_name ",
            "SELECT column_name FROM table_name WHERE column_name IS NOT NULL GROUP BY column_name ",
            "SELECT * FROM table_name WHERE CONDITION AND CONDITION GROUP BY column_name ",
            "SELECT * FROM table_name WHERE CONDITION OR CONDITION GROUP BY column_name ",

            //"SELECT * FROM table_name ",
            "SELECT * FROM table_name WHERE CONDITION ",
            "SELECT column_name FROM table_name WHERE column_name BETWEEN value1 AND value2 ",
            "SELECT column_name FROM table_name WHERE column_name IS NULL ",
            "SELECT column_name FROM table_name WHERE column_name IS NOT NULL ",
            "SELECT * FROM table_name WHERE CONDITION AND CONDITION ",
            "SELECT * FROM table_name WHERE CONDITION OR CONDITION ",
    };

    static String[] groupBY3 = {
            "SELECT * FROM table_name GROUP BY column_name ORDER BY column_name ",
            "SELECT * FROM table_name WHERE CONDITION GROUP BY column_name ORDER BY column_name ",
            "SELECT column_name FROM table_name WHERE column_name BETWEEN value1 AND value2 GROUP BY column_name ORDER BY column_name ",
            "SELECT column_name FROM table_name WHERE column_name LIKE text GROUP BY column_name ORDER BY column_name ",
            "SELECT column_name FROM table_name WHERE column_name IS NULL GROUP BY column_name ORDER BY column_name ",
            "SELECT column_name FROM table_name WHERE column_name IS NOT NULL GROUP BY column_name ORDER BY column_name ",
            "SELECT * FROM table_name WHERE CONDITION AND CONDITION GROUP BY column_name ORDER BY column_name ",
            "SELECT * FROM table_name WHERE CONDITION OR CONDITION GROUP BY column_name ORDER BY column_name ",

            "select * FROM table_name ORDER BY column_name ",

            "select * FROM table_name WHERE CONDITION ",
            "select * FROM table_name WHERE CONDITION AND CONDITION ",
            "select * FROM table_name WHERE CONDITION OR CONDITION ",

            "select column_name FROM table_name WHERE column_name BETWEEN value1 AND value2 ORDER BY column_name ",
            "select column_name FROM table_name WHERE column_name LIKE text ORDER BY column_name ",
            "select column_name FROM table_name WHERE column_name IS NULL ORDER BY column_name ",
            "select column_name FROM table_name WHERE column_name IS NOT NULL ORDER BY column_name ",

            "select * FROM table_name WHERE CONDITION ORDER BY column_name ",
            "select * FROM table_name WHERE CONDITION AND CONDITION ORDER BY column_name ",
            "select * FROM table_name WHERE CONDITION OR CONDITION ORDER BY column_name ",
    };

    //suggestions
    static String[] afterSelect = {"*", "column_name", "DISTINCT",
            "COUNT(*) FROM table_name",

            "COUNT(column_name) FROM table_name",
            "SUM(column_name) FROM table_name",
            "AVG(column_name) FROM table_name",
            "MIN(column_name) FROM table_name",
            "MAX(column_name) FROM table_name",

            "COUNT(DISTINCT column_name) FROM table_name",
            "SUM(DISTINCT column_name) FROM table_name",
            "AVG(DISTINCT column_name) FROM table_name",
            "MIN(DISTINCT column_name) FROM table_name",
            "MAX(DISTINCT column_name) FROM table_name",
    };

    private static void showAutoComplete(JTextPane textPane) {
        StyledDocument document = textPane.getStyledDocument();
        String text = null;
        try {
            text = document.getText(0, document.getLength());
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }

        int cursorPosition = textPane.getCaretPosition();
        int wordStart = findWordStart(text, cursorPosition);
        int wordEnd = findWordEnd(text, cursorPosition);
        String word = text.substring(wordStart, wordEnd);
        System.out.println(text);

        if (word.equalsIgnoreCase("SELECT ")) {
            String[] suggestions = afterSelect;
            JPopupMenu popupMenu = new JPopupMenu();
            for (String suggestion : suggestions) {
                popupMenu.add(createAutoCompleteMenuItem(suggestion, textPane, wordStart, wordEnd));
            }
            Point popupLocation = textPane.getCaret().getMagicCaretPosition();
            popupMenu.show(textPane, popupLocation.x, popupLocation.y);

            // SINGULAR
        } else if (word.equalsIgnoreCase("SELECT DISTINCT ")) {
            String[] suggestions = {"*", "column_name"};
            JPopupMenu popupMenu = new JPopupMenu();
            for (String suggestion : suggestions) {
                popupMenu.add(createAutoCompleteMenuItem(suggestion, textPane, wordStart, wordEnd));
            }
            Point popupLocation = textPane.getCaret().getMagicCaretPosition();
            popupMenu.show(textPane, popupLocation.x, popupLocation.y);

        } else if (word.equalsIgnoreCase("SELECT column_name ")) {
            String[] suggestions = {"FROM", "AS"};
            JPopupMenu popupMenu = new JPopupMenu();
            for (String suggestion : suggestions) {
                popupMenu.add(createAutoCompleteMenuItem(suggestion, textPane, wordStart, wordEnd));
            }
            Point popupLocation = textPane.getCaret().getMagicCaretPosition();
            popupMenu.show(textPane, popupLocation.x, popupLocation.y);

        } else if (word.equalsIgnoreCase("SELECT column_name AS ")) {
            String[] suggestions = {"title"};
            JPopupMenu popupMenu = new JPopupMenu();
            for (String suggestion : suggestions) {
                popupMenu.add(createAutoCompleteMenuItem(suggestion, textPane, wordStart, wordEnd));
            }
            Point popupLocation = textPane.getCaret().getMagicCaretPosition();
            popupMenu.show(textPane, popupLocation.x, popupLocation.y);

        } else if (word.equalsIgnoreCase("SELECT column_name AS title WHERE ")) {
            String[] suggestions = {"CONDITION", "column_name"};
            JPopupMenu popupMenu = new JPopupMenu();
            for (String suggestion : suggestions) {
                popupMenu.add(createAutoCompleteMenuItem(suggestion, textPane, wordStart, wordEnd));
            }
            Point popupLocation = textPane.getCaret().getMagicCaretPosition();
            popupMenu.show(textPane, popupLocation.x, popupLocation.y);

        } else if (word.equalsIgnoreCase("select column_name AS title FROM table_name WHERE column_name BETWEEN ")) {
            String[] suggestions = {"value1"};
            JPopupMenu popupMenu = new JPopupMenu();
            for (String suggestion : suggestions) {
                popupMenu.add(createAutoCompleteMenuItem(suggestion, textPane, wordStart, wordEnd));
            }
            Point popupLocation = textPane.getCaret().getMagicCaretPosition();
            popupMenu.show(textPane, popupLocation.x, popupLocation.y);

        } else if (word.equalsIgnoreCase("select column_name AS title FROM table_name WHERE column_name BETWEEN value1 ")) {
            String[] suggestions = {"AND"};
            JPopupMenu popupMenu = new JPopupMenu();
            for (String suggestion : suggestions) {
                popupMenu.add(createAutoCompleteMenuItem(suggestion, textPane, wordStart, wordEnd));
            }
            Point popupLocation = textPane.getCaret().getMagicCaretPosition();
            popupMenu.show(textPane, popupLocation.x, popupLocation.y);

        } else if (word.equalsIgnoreCase("select column_name AS title FROM table_name WHERE column_name BETWEEN value1 AND ")) {
            String[] suggestions = {"value2"};
            JPopupMenu popupMenu = new JPopupMenu();
            for (String suggestion : suggestions) {
                popupMenu.add(createAutoCompleteMenuItem(suggestion, textPane, wordStart, wordEnd));
            }
            Point popupLocation = textPane.getCaret().getMagicCaretPosition();
            popupMenu.show(textPane, popupLocation.x, popupLocation.y);

        } else if (word.isEmpty()) {
            String[] suggestions = {"SELECT", "INSERT", "UPDATE", "DELETE"};
            JPopupMenu popupMenu = new JPopupMenu();
            for (String suggestion : suggestions) {
                popupMenu.add(createAutoCompleteMenuItem(suggestion, textPane, wordStart, wordEnd));
            }
            Point popupLocation = textPane.getCaret().getMagicCaretPosition();
            if (popupLocation != null) {
                popupMenu.show(textPane, popupLocation.x, popupLocation.y);
            }


        } else if (word.equalsIgnoreCase("INSERT ")) {
            String[] suggestions = {"INTO table_name (column_name, column_name, column_name) values (value1, value2, value3)",
            };
            JPopupMenu popupMenu = new JPopupMenu();
            for (String suggestion : suggestions) {
                popupMenu.add(createAutoCompleteMenuItem(suggestion, textPane, wordStart, wordEnd));
            }
            Point popupLocation = textPane.getCaret().getMagicCaretPosition();
            popupMenu.show(textPane, popupLocation.x, popupLocation.y);

        } else if (word.equalsIgnoreCase("UPDATE ")) {
            String[] suggestions = {"table_name SET column1 = value1, column2 = value2, column3 = value3 WHERE CONDITION",
            };
            JPopupMenu popupMenu = new JPopupMenu();
            for (String suggestion : suggestions) {
                popupMenu.add(createAutoCompleteMenuItem(suggestion, textPane, wordStart, wordEnd));
            }
            Point popupLocation = textPane.getCaret().getMagicCaretPosition();
            popupMenu.show(textPane, popupLocation.x, popupLocation.y);

        } else if (word.equalsIgnoreCase("DELETE ")) {
            String[] suggestions = {"FROM table_name WHERE CONDITION",
            };
            JPopupMenu popupMenu = new JPopupMenu();
            for (String suggestion : suggestions) {
                popupMenu.add(createAutoCompleteMenuItem(suggestion, textPane, wordStart, wordEnd));
            }
            Point popupLocation = textPane.getCaret().getMagicCaretPosition();
            popupMenu.show(textPane, popupLocation.x, popupLocation.y);

            //LISTS
        } else if (Arrays.stream(afterLIKE).anyMatch(word::equalsIgnoreCase)) {
            String[] suggestions = {"text"}; //HERE
            JPopupMenu popupMenu = new JPopupMenu();
            for (String suggestion : suggestions) {
                popupMenu.add(createAutoCompleteMenuItem(suggestion, textPane, wordStart, wordEnd));
            }
            Point popupLocation = textPane.getCaret().getMagicCaretPosition();
            popupMenu.show(textPane, popupLocation.x, popupLocation.y);

        } else if (Arrays.stream(afterWHERE1).anyMatch(word::equalsIgnoreCase)) {
            String[] suggestions = {"CONDITION", "column_name"}; //HERE
            JPopupMenu popupMenu = new JPopupMenu();
            for (String suggestion : suggestions) {
                popupMenu.add(createAutoCompleteMenuItem(suggestion, textPane, wordStart, wordEnd));
            }
            Point popupLocation = textPane.getCaret().getMagicCaretPosition();
            popupMenu.show(textPane, popupLocation.x, popupLocation.y);

        } else if (Arrays.stream(afterWHERE2).anyMatch(word::equalsIgnoreCase)) {
            String[] suggestions = {"AND", "OR", "LIMIT", "GROUP BY column_name", "ORDER BY column_name"}; //HERE
            JPopupMenu popupMenu = new JPopupMenu();
            for (String suggestion : suggestions) {
                popupMenu.add(createAutoCompleteMenuItem(suggestion, textPane, wordStart, wordEnd));
            }
            Point popupLocation = textPane.getCaret().getMagicCaretPosition();
            popupMenu.show(textPane, popupLocation.x, popupLocation.y);

        } else if (Arrays.stream(afterWHERE3).anyMatch(word::equalsIgnoreCase)) {
            String[] suggestions = {"CONDITION"}; //HERE
            JPopupMenu popupMenu = new JPopupMenu();
            for (String suggestion : suggestions) {
                popupMenu.add(createAutoCompleteMenuItem(suggestion, textPane, wordStart, wordEnd));
            }
            Point popupLocation = textPane.getCaret().getMagicCaretPosition();
            popupMenu.show(textPane, popupLocation.x, popupLocation.y);

        } else if (Arrays.stream(afterWHERECOL).anyMatch(word::equalsIgnoreCase)) {
            String[] suggestions = {"BETWEEN", "LIKE", "IS NULL", "IS NOT NULL"}; //HERE
            JPopupMenu popupMenu = new JPopupMenu();
            for (String suggestion : suggestions) {
                popupMenu.add(createAutoCompleteMenuItem(suggestion, textPane, wordStart, wordEnd));
            }
            Point popupLocation = textPane.getCaret().getMagicCaretPosition();
            popupMenu.show(textPane, popupLocation.x, popupLocation.y);

        } else if (Arrays.stream(afterBETWEEN).anyMatch(word::equalsIgnoreCase)) {
            String[] suggestions = {"value1"}; //HERE
            JPopupMenu popupMenu = new JPopupMenu();
            for (String suggestion : suggestions) {
                popupMenu.add(createAutoCompleteMenuItem(suggestion, textPane, wordStart, wordEnd));
            }
            Point popupLocation = textPane.getCaret().getMagicCaretPosition();
            popupMenu.show(textPane, popupLocation.x, popupLocation.y);

        } else if (Arrays.stream(afterBETWEEN1).anyMatch(word::equalsIgnoreCase)) {
            String[] suggestions = {"AND"}; //HERE
            JPopupMenu popupMenu = new JPopupMenu();
            for (String suggestion : suggestions) {
                popupMenu.add(createAutoCompleteMenuItem(suggestion, textPane, wordStart, wordEnd));
            }
            Point popupLocation = textPane.getCaret().getMagicCaretPosition();
            popupMenu.show(textPane, popupLocation.x, popupLocation.y);

        } else if (Arrays.stream(afterBETWEEN2).anyMatch(word::equalsIgnoreCase)) {
            String[] suggestions = {"value2"}; //HERE
            JPopupMenu popupMenu = new JPopupMenu();
            for (String suggestion : suggestions) {
                popupMenu.add(createAutoCompleteMenuItem(suggestion, textPane, wordStart, wordEnd));
            }
            Point popupLocation = textPane.getCaret().getMagicCaretPosition();
            popupMenu.show(textPane, popupLocation.x, popupLocation.y);

        } else if (Arrays.stream(beforeFROM).anyMatch(word::equalsIgnoreCase)) {
            String[] suggestions = {"FROM"};
            JPopupMenu popupMenu = new JPopupMenu();
            for (String suggestion : suggestions) {
                popupMenu.add(createAutoCompleteMenuItem(suggestion, textPane, wordStart, wordEnd));
            }
            Point popupLocation = textPane.getCaret().getMagicCaretPosition();
            popupMenu.show(textPane, popupLocation.x, popupLocation.y);

        } else if (Arrays.stream(beforeTABLE).anyMatch(word::equalsIgnoreCase)) {
            String[] suggestions = {"table_name"};
            JPopupMenu popupMenu = new JPopupMenu();
            for (String suggestion : suggestions) {
                popupMenu.add(createAutoCompleteMenuItem(suggestion, textPane, wordStart, wordEnd));
            }
            Point popupLocation = textPane.getCaret().getMagicCaretPosition();
            popupMenu.show(textPane, popupLocation.x, popupLocation.y);

            //delete?
        } else if (Arrays.stream(groupBY1).anyMatch(word::equalsIgnoreCase)) {
            String[] suggestions = {"WHERE", "LIMIT", "GROUP BY column_name", "ORDER BY column_name"};
            JPopupMenu popupMenu = new JPopupMenu();
            for (String suggestion : suggestions) {
                popupMenu.add(createAutoCompleteMenuItem(suggestion, textPane, wordStart, wordEnd));
            }
            Point popupLocation = textPane.getCaret().getMagicCaretPosition();
            popupMenu.show(textPane, popupLocation.x, popupLocation.y);
        }

        else if (Arrays.stream(groupBY2).anyMatch(word::equalsIgnoreCase)) {
            String[] suggestions = {"ORDER BY column_name"};
            JPopupMenu popupMenu = new JPopupMenu();
            for (String suggestion : suggestions) {
                popupMenu.add(createAutoCompleteMenuItem(suggestion, textPane, wordStart, wordEnd));
            }
            Point popupLocation = textPane.getCaret().getMagicCaretPosition();
            popupMenu.show(textPane, popupLocation.x, popupLocation.y);
        }

        else if (Arrays.stream(groupBY3).anyMatch(word::equalsIgnoreCase)) {
            String[] suggestions = {"ASC", "DESC"};
            JPopupMenu popupMenu = new JPopupMenu();
            for (String suggestion : suggestions) {
                popupMenu.add(createAutoCompleteMenuItem(suggestion, textPane, wordStart, wordEnd));
            }
            Point popupLocation = textPane.getCaret().getMagicCaretPosition();
            popupMenu.show(textPane, popupLocation.x, popupLocation.y);
        }
    }

    private static int findWordStart(String text, int position) {
        for (int i = position - 1; i >= 0; i--) {
            char c = text.charAt(i);
            if (!Character.isLetterOrDigit(c) && c != '_' && c != ' ' && c != '*') {
                return i + 1;
            }
        }
        return 0;
    }

    private static int findWordEnd(String text, int position) {
        for (int i = position; i < text.length(); i++) {
            char c = text.charAt(i);
            if (!Character.isLetterOrDigit(c) && c != '_' && c != ' ' && c != '*') {
                return i;
            }
        }
        return text.length();
    }

    private static JMenuItem createAutoCompleteMenuItem(String suggestion, JTextPane textPane, int wordStart, int wordEnd) {
        JMenuItem menuItem = new JMenuItem(suggestion);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StyledDocument document = textPane.getStyledDocument();
                SimpleAttributeSet attributes = new SimpleAttributeSet();
                // Set attributes for insert text font and ccolor
                StyleConstants.setForeground(attributes, new Color(248, 248, 242));

                try {
                    // inserting at
                    int insertPosition = wordEnd;
                    document.insertString(insertPosition, suggestion, attributes);
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }
        });
        return menuItem;
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
}
