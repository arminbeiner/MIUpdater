import models.Customer;
import models.CustomerResult;
import models.Server;
import models.SSHConnection;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.*;

/**
 * view klasse
 * Created by asi on 27.12.2016.
 */
public class View extends JPanel {

    /** logger */
    private static Logger LOG = Logger.getLogger(View.class.getName());
    private static Level loglevel = Level.ALL;

    /** dimensionen */
    private Dimension DIMENSION = Toolkit.getDefaultToolkit().getScreenSize();
    private final int buttonWidth = 200;
    private final int buttonHeight = 40;
    private final Dimension buttonDimension = new Dimension(buttonWidth, buttonHeight);
    private final Dimension addTextfieldDimension = new Dimension(200, 40);

    /** fonts */
    private final Font buttonFont = new Font("Helvetica", Font.PLAIN, 16);
    private final Font labelFont = new Font("Helvetica", Font.BOLD, 18);
    private final Font textFont = new Font("Helvetica", Font.PLAIN, 22);

    /** JFrame für Running Box*/
    JFrame runningFrame = null;

    /**running box komponenten */
    private JTextArea runningArea = null;

    /** combo box für kunden auswahl*/
    private JComboBox getCustomerField;

    /** suchfeld */
    private JTextField searchField = new JTextField();

    /** document listener für suche */
    private DocumentListener documentListener;


    /** tabelle initialisierungs daten */
    private static String[] header = {"Name", "Hostname", "IP", "Type"};
    private static String[][] data = null;


    /** der result table in einem scroll pane
     * scroll pane in einem panel
     * zellen nicht editierbar */
    private static DefaultTableModel resultTableModel = new DefaultTableModel(data, header) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private static JTable resultTable = new JTable(resultTableModel);
    private static JPanel resultTablePanel = new JPanel();


    /** der select table in einem scroll pane
     * scroll pane in einem panel
     * zellen nicht editierbar */
    private static DefaultTableModel selectTableModel = new DefaultTableModel(data, header) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private static JTable selectTable = new JTable(selectTableModel);
    private static JPanel selectTablePanel = new JPanel();

    /** box um kunde hinzuzufügen */
    private JFrame addCustomerFrame = null;

    /** textfelder der kundenbox */
    private JTextField addCustomerNameTextField = null;
    private JTextField addCustomerBch4TextField = null;

    /** box um server hinzuzufügen */
    private JFrame addServerFrame = null;

    /** elemente der server box */
    private JTextField addServerHostnameTextField = null;
    private JTextField addServerIPTextField = null;
    private JComboBox addServerTypeField = null;

    /** model und controller */
    private Model model = new Model(this);
    private Controller controller = new Controller(this, model);

    /** remove server box komponenten */
    private JFrame removeServerFrame = null;
    private JComboBox getServerField = null;
    private JLabel ServerLabel;
    private JButton removeServerButton;

    private String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
    private String date2 = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date());


    /**
     * der view für die panels
     *
     * @param panelDimension dimension der panels
     */
    View(Dimension panelDimension) {

        try {
            initLogger(date + " viewLogfile.txt");
        } catch (IOException e) {
            showErrorBox(e.getMessage());
        }
        LOG.info(date2 + " " + System.getProperty("user.name") + " has App Started");

        documentListener = new DocumentListener() {
            /** listener für live suchfeld
             * sobald eingabe erfolgt wird eine DB abfrage ausgelöst
             * das ergebnis wird in dem result table angezeigt
             * bei * werden alle kunden angezeigt */
            @Override
            public void insertUpdate(DocumentEvent e) {
                DocumentEvent.EventType type = e.getType();
                if (type.equals(DocumentEvent.EventType.INSERT)) {
                    try {
                        clearTableModel(resultTableModel);

                        String text = e.getDocument().getText(0, e.getDocument().getLength());
                        if(text.equalsIgnoreCase("*")) {
                            loadTableModel(resultTableModel, "");
                        }
                        else {
                            loadTableModel(resultTableModel, text);
                        }
                    } catch (BadLocationException | SQLException e1) {
                        showErrorBox("Fehler bei Kundensuche\n" + e1.getMessage());
                        LOG.severe(e1.getMessage());
                    }
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                DocumentEvent.EventType type = e.getType();
                if (type.equals(DocumentEvent.EventType.REMOVE)) {
                    try {
                        clearTableModel(resultTableModel);

                        String text = e.getDocument().getText(0, e.getDocument().getLength());
                        if (text.length() > 0) {
                            if (text.equalsIgnoreCase("*")) {
                                loadTableModel(resultTableModel, "");
                            }
                            else {
                                loadTableModel(resultTableModel, text);
                            }
                        }
                    }
                    catch (BadLocationException | SQLException e1) {
                        showErrorBox("Fehler bei Kundensuche\n" + e1.getMessage());
                        LOG.severe(e1.getMessage());
                    }
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                System.out.println("CHange");
            }
        };


        add(addLeftPanel(panelDimension), BorderLayout.WEST);
        add(addRightPanel(panelDimension), BorderLayout.EAST);
    }


    /**
     * erstellt das JPanel mit den gegebenen dimensionen
     * hier sind die buttons angeordnet mit einem gridbaglayout
     * @param panelDimension dimension
     * @return jpanel mit buttons
     */
    private JPanel addRightPanel(Dimension panelDimension) {
        JPanel rightPanel = new JPanel();
        rightPanel.setPreferredSize(panelDimension);
        rightPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        java.util.List<JButton> buttonLabels = new ArrayList<>();
        buttonLabels.add(new JButton("Add Customer"));
        buttonLabels.add(new JButton("Add Server"));
        buttonLabels.add(new JButton("Remove Server"));
        buttonLabels.add(new JButton("Run"));
        buttonLabels.add(new JButton("Exit"));

        int gridy = 0;
        /** button schleife um
         * buttons zu platzieren
         * und action listener hinzuzufügen */
        for (JButton button : buttonLabels) {
            button.addActionListener(new ActionListener() {
                                         @Override
                                         public void actionPerformed(ActionEvent e) {
                                             controller.actionPerformed(e);
                                         }
                                     }
            );
            button.setPreferredSize(buttonDimension);
            button.setFont(buttonFont);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            if (gridy == 3) {
                gbc.insets = new Insets(100, 20, 20, 20);
            } else {
                gbc.insets = new Insets(20, 20, 20, 20);
            }
            gbc.gridx = 0;
            gbc.gridy = gridy;
            rightPanel.add(button, gbc);
            gridy++;
        }
        return rightPanel;
    }


    /**
     * erstellte ein JPanel mit der gegebenen dimension
     * hier sind
     * suchtextfeld, labels und tables mit gridbaglayout angeordnet
     ** @param panelDimension die panel dimension
     * @return ein JPanel
     */
    private JPanel addLeftPanel(Dimension panelDimension) {
        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(panelDimension);
        leftPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        JLabel[] labels = new JLabel[3];
        labels[0] = new JLabel("Customer Search (* für alle)");
        labels[1] = new JLabel("Results");
        labels[2] = new JLabel("Selected");

        /** schleife für
         * die labels und tables */
        int gridy = 0;
        for (int i = 0; i < labels.length; i++) {
            labels[i].setFont(labelFont);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;
            gbc.gridy = gridy;
            leftPanel.add(labels[i], gbc);
            gridy++;

            /** suchfeld mit document listener*/
            if (i == 0) {
                searchField.getDocument().addDocumentListener(documentListener);
                gbc.gridx = 0;
                gbc.gridy = gridy;
                leftPanel.add(searchField, gbc);
            }
            /** result table mit mouse listener*/
            else if (i == 1) {
                JScrollPane resultScrollPanel = new JScrollPane(resultTable);
                resultScrollPanel.setPreferredSize(new Dimension(panelDimension.width - 20, panelDimension.height / 4));
                resultTable.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        /** eintrag zum select table hinzufügen wenn nicht schon vorhanden */
                        String[] selectedRow = getSelectedTableRow(resultTable);
                        if(!rowExistsInSelectTable(selectedRow)) {
                            selectTableModel.addRow(selectedRow);
                            selectTable.setModel(selectTableModel);

                            /** eintrag aus result table löschen */
                            int row = resultTable.getSelectedRow();
                            if (row != -1) {
                                resultTableModel.removeRow(resultTable.getSelectedRow());
                            }
                        }
                    }
                });
                resultScrollPanel.setFocusable(true);
                resultTablePanel.add(resultScrollPanel);

                gbc.gridx = 0;
                gbc.gridy = gridy;
                leftPanel.add(resultTablePanel, gbc);
            }
            /** select table mit mouse listener*/
            else if (i == 2) {
                JScrollPane selectScrollPane = new JScrollPane(selectTable);
                selectScrollPane.setPreferredSize(new Dimension(panelDimension.width - 20, panelDimension.height / 4));
                selectTable.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        int row = selectTable.getSelectedRow();
                        if(row != -1) {
                            resultTableModel.addRow(getSelectedTableRow(selectTable));
                            selectTableModel.removeRow(row);
                            selectTable.setModel(selectTableModel);
                        }
                    }
                });
                selectScrollPane.setFocusable(true);
                selectTablePanel.add(selectScrollPane);

                gbc.gridx = 0;
                gbc.gridy = gridy;
                leftPanel.add(selectTablePanel, gbc);
            }
            gridy++;
        }

        return leftPanel;
    }


    /**
     * neues frame um kunden hinzuzufügen
     */
    public void addCustomerBox() {
        addCustomerFrame = new JFrame("Add Customer");
        addCustomerFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        Container contentPane = addCustomerFrame.getContentPane();
        JPanel panel = new JPanel();

        /** labels und textfields */
        JLabel nameLabel = new JLabel("Name");
        nameLabel.setFont(labelFont);

        addCustomerNameTextField = new JTextField();
        addCustomerNameTextField.setFont(textFont);
        JLabel firstNameLabel = new JLabel("BCH4");
        firstNameLabel.setFont(labelFont);
        addCustomerBch4TextField = new JTextField();
        addCustomerBch4TextField.setFont(textFont);
        JButton addCustomerButton = new JButton("Add");
        addCustomerButton.setFont(buttonFont);

        /** komponenten liste für schleife */
        java.util.List<JComponent> componentList = new ArrayList<JComponent>();
        componentList.add(nameLabel);
        componentList.add(addCustomerNameTextField);
        componentList.add(firstNameLabel);
        componentList.add(addCustomerBch4TextField);

        /** textfeld und label layout */
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 10, 0, 10);
        /** schleife um komponenten zum panel hinzuzufügen */
        int gridy = 0;
        for (JComponent component : componentList) {
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridy = gridy;
            gbc.gridx = 0;
            gbc.gridwidth = 2;
            component.setPreferredSize(addTextfieldDimension);
            panel.add(component, gbc);
            gridy++;
        }

        /** position von add button */
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        addCustomerButton.setPreferredSize(buttonDimension);
        /** action listener zu button hinzufügen */
        addCustomerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.actionPerformed(e);
            }
        });
        panel.add(addCustomerButton, gbc);

        contentPane.add(panel);

        /** panel grösse bestimmen*/
        int width = DIMENSION.width / 4;
        int height = DIMENSION.height / 4;
        addCustomerFrame.setPreferredSize(new Dimension(width, height));
        addCustomerFrame.pack();
        centerScreen(addCustomerFrame, DIMENSION);
        addCustomerFrame.setMinimumSize(new Dimension(width, height));
        addCustomerFrame.setVisible(true);

    }

    /**
     * neues frame um server hinzuzufügen
     */
    public void addServerBox() throws SQLException {

        addServerFrame = new JFrame("Add Server");
        addServerFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        Container contentPane = addServerFrame.getContentPane();
        JPanel panel = new JPanel();

        /** erstellen von label und textfeldern */
        JLabel CustomerLabel = new JLabel("Select Customer");
        CustomerLabel.setFont(labelFont);

        /**Abfüllen der neuen ComboBox mit den Werten aus
         * derSQL Abfrage aus Model.
         * Hinzufügen der Namen aller bestehender Kunden. */
        getCustomerField = new JComboBox(model.getNames().toArray());
        getCustomerField.setSelectedIndex(-1);
        JLabel HostnameLabel = new JLabel("Hostname");
        HostnameLabel.setFont(labelFont);
        addServerHostnameTextField = new JTextField();
        addServerHostnameTextField.setFont(textFont);
        JLabel IPLabel = new JLabel("IP");
        IPLabel.setFont(labelFont);
        addServerIPTextField = new JTextField();
        addServerIPTextField.setFont(textFont);
        JLabel typeLabel = new JLabel("Type");
        typeLabel.setFont(labelFont);
        addServerTypeField = new JComboBox(model.getType().toArray());

        JButton addServerButton = new JButton("Add New Server");
        addServerButton.setFont(buttonFont);

        /** komponenten liste für layout schleife */
        java.util.List<JComponent> componentList = new ArrayList<JComponent>();
        componentList.add(CustomerLabel);
        componentList.add(getCustomerField);
        componentList.add(HostnameLabel);
        componentList.add(addServerHostnameTextField);
        componentList.add(IPLabel);
        componentList.add(addServerIPTextField);
        componentList.add(typeLabel);
        componentList.add(addServerTypeField);


        /** textfelder und label layout*/
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 10, 0, 10);

        /** schleife um komponenten zum panel hinzuzufügen */
        int gridy = 0;
        for (JComponent component : componentList) {
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;
            gbc.gridy = gridy;
            component.setPreferredSize(addTextfieldDimension);
            panel.add(component, gbc);
            gridy++;
        }


        /** position vom add button */
        gbc.gridx = 0;
        gbc.gridy = gridy + 1;
        gbc.gridwidth = 1;
        addServerButton.setPreferredSize(buttonDimension);

        /** action listener zu button hinzufügen */
        addServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.actionPerformed(e);
            }
        });
        panel.add(addServerButton, gbc);

        contentPane.add(panel);

        /** panel groesse bestimmen */
        int width = DIMENSION.width / 3;
        int height = DIMENSION.height / 3;
        addServerFrame.setPreferredSize(new Dimension(width, height));
        addServerFrame.pack();
        centerScreen(addServerFrame, DIMENSION);
        addServerFrame.setMinimumSize(new Dimension(width, height));
        addServerFrame.setVisible(true);


    }

    /**
     * neues frame um server zu entfernen
     */
    public void removeServerBox() throws SQLException {
        removeServerFrame = new JFrame("Remove Server");
        removeServerFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        Container contentPane = removeServerFrame.getContentPane();
        JPanel panel = new JPanel();

        /** labels und textfielder erstellen */
        JLabel CustomerLabel = new JLabel("Select Customer");
        CustomerLabel.setFont(labelFont);

        /** Abfüllen der neuen ComboBox mit
         * den Werten aus derSQL Abfrage aus Model.
         * Hinzufügen der Namen aller bestehender Kunden. */
        getCustomerField = new JComboBox(model.getNames().toArray());
        ServerLabel = new JLabel("Select Server");
        ServerLabel.setFont(labelFont);

        /** Abfüllen der neuen ComboBox mit den Werten
         * aus derSQL Abfrage aus Model.
         * Hinzufügen der Server für den ausgewählten Kunden.
         */
        getServerField = new JComboBox(model.getServer().toArray());
        getServerField.setSelectedIndex(-1);

        removeServerButton = new JButton("Remove");
        removeServerButton.setFont(buttonFont);

        /** komponenten liste for layout schleife*/
        java.util.List<JComponent> componentList = new ArrayList<JComponent>();
        componentList.add(CustomerLabel);
        componentList.add(getCustomerField);
        componentList.add(ServerLabel);
        componentList.add(getServerField);


        /** textfeld und label layout */
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 10, 0, 10);

        /** schleife um komponenten zum panel hinzuzufügen */
        int gridy = 0;
        for (JComponent component : componentList) {
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridy = gridy;
            gbc.gridx = 0;
            gbc.gridwidth = 1;
            component.setPreferredSize(addTextfieldDimension);
            panel.add(component, gbc);
            gridy++;
        }

        /** position vom add button */
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        removeServerButton.setPreferredSize(buttonDimension);

        /** action listener zu button hinzufügen*/
        removeServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.actionPerformed(e);
            }
        });
        getCustomerField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.actionPerformed(e);
            }
        });

        /**
         * Hinzufügen eines ItemListeners,
         * der überprüft ob der Status der ComboBox getCustomerField ändert.
         * Wird benötigt, ComboBox getServerField neu zu definieren,
         * abhängig vom ausgewählten Kunden
         */
        getCustomerField.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                try {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        /** Ändert der Status werden
                         * als erstes alle Items aus der ComboBox entfernt
                         */
                        getServerField.removeAll();

                        /** sollte die getServer Abfrage zu diesem Zeitpunkt leer sein
                         * (keine Server für den entsprechend Kunden), werden
                         * ServerLabel, getServerField und removeServerButton ausgeblendet
                         */
                        if (model.getServer().isEmpty()) {
                            ServerLabel.setVisible(false);
                            getServerField.setVisible(false);
                            removeServerButton.setVisible(false);
                            noServerBox();

                        }
                        /** Sind Daten vorhanden in der getServer Abfrage,
                         * wird die ComboBox neu initialisiert
                         */
                        else {
                            ServerLabel.setVisible(true);
                            getServerField.setVisible(true);
                            removeServerButton.setVisible(true);
                            getServerField.setModel(new DefaultComboBoxModel(model.getServer().toArray()));
                            getServerField.setSelectedIndex(-1);
                        }

                    }
                } catch (SQLException ex) {
                    showErrorBox(ex.getMessage());
                }
            }

        });
        removeServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.actionPerformed(e);
            }
        });
        panel.add(removeServerButton, gbc);

        contentPane.add(panel);

        /** dimension des panels */
        int width = DIMENSION.width / 3;
        int height = DIMENSION.height / 3;
        removeServerFrame.setPreferredSize(new Dimension(width, height));
        removeServerFrame.pack();
        centerScreen(removeServerFrame, DIMENSION);
        removeServerFrame.setMinimumSize(new Dimension(width, height));
        removeServerFrame.setVisible(true);

    }

    /**
     * neues frame fuer die Running box
     */
    public void runningBox() {
        runningFrame = new JFrame("Running");
        runningFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        Container contentPane = runningFrame.getContentPane();
        JPanel panel = new JPanel();

        /** label erstellen */
        JLabel RunningLabel = new JLabel("Running: ");
        RunningLabel.setFont(labelFont);

        /** textarea definieren */
        runningArea = new JTextArea(20, 60);
        runningArea.setEditable(false);

        /** selected eintraege anzeigen */
        runningArea.setText(getSelectedEntries());

        /** textarea in scroll pane laden
         * scroll pane definieren */
        JScrollPane runningScrollPane = new JScrollPane(runningArea);
        runningScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        runningScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        /** cancel button */
        JButton cancel = new JButton("Cancel");
        cancel.setPreferredSize(buttonDimension);

        /** komponenten liste fuer layout schleife */
        java.util.List<JComponent> componentList = new ArrayList<JComponent>();
        componentList.add(RunningLabel);
        componentList.add(runningScrollPane);
        componentList.add(cancel);

        /** textfeld und label layout */
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 10, 0, 10);

        /** schleife um komponenten panel hinzuzufügen */
        int gridy = 0;
        for (JComponent component : componentList) {
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridy = gridy;
            gbc.gridx = 0;
            gbc.gridwidth = 1;
            //component.setPreferredSize(addTextfieldDimension);
            panel.add(component, gbc);
            gridy++;
        }


        /** action listener zu button hinzufügen*/
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.actionPerformed(e);
            }
        });

        contentPane.add(panel);

        /** wichtig: pack vor preferredSize */
        runningFrame.pack();

        /** panel groesse und position bestimmen */
        int width = DIMENSION.width / 3;
        int height = DIMENSION.height / 3;
        runningFrame.setPreferredSize(new Dimension(width, height));
        centerScreen(runningFrame, DIMENSION);
        runningFrame.setMinimumSize(new Dimension(width, height));
        runningFrame.setVisible(true);
    }



    /**
     * positioniert ein frame in der mitte des bildschirms
     * @param frame frame
     * @param dim dimension
     */
    public static void centerScreen(JFrame frame, Dimension dim) {
        int x = dim.width / 2 - (frame.getWidth() / 2);
        int y = dim.height / 2 - (frame.getHeight() / 2);
        frame.setLocation(x, y);
    }


    /**
     * zeigt ein option pane für exceptions
     * @param exception exception meldung
     */
    public void showErrorBox(String exception) {
        JOptionPane optionPane = new JOptionPane();
        JOptionPane.showMessageDialog(optionPane, exception, "Error", JOptionPane.ERROR_MESSAGE);
        LOG.severe(date2 + " " + System.getProperty("user.name") + "generierte Exception: " + exception);
    }


    /**
     * zeigt ein option pane fuer die erfolgsmeldung des kunden
     * @param customer kunde der angezeigt wird
     */
    public void showSuccessBox(Customer customer) {
        JOptionPane optionPane = new JOptionPane();
        String message = "Customer: " + customer.getName() +
                " mit BCH4: " + customer.getBch4() + " hinzugefügt";
        JOptionPane.showMessageDialog(optionPane, message, "Success", JOptionPane.INFORMATION_MESSAGE);
        addCustomerNameTextField.setText("");
        addCustomerBch4TextField.setText("");
    }


    /**
     * zeigt eine erfolgsmeldung fuer den server
     * @param server server der angezeigt wird
     */
    public void showSuccessBox(Server server) {
        JOptionPane optionPane = new JOptionPane();
        String message = "Server: " + server.getHostname() +
                " mit IP: " + server.getIP() +
                " zu Kunde: " + server.getName_customer() + " hinzugefügt";
        JOptionPane.showMessageDialog(optionPane, message, "Success", JOptionPane.INFORMATION_MESSAGE);
        getAddServerHostnameTextField().setText("");
        getAddServerIPTextField().setText("");
        getCustomerField.setSelectedIndex(-1);
    }

    /**
     * remove server message box
     * @param remove geloeschter server
     * @throws SQLException
     */
    public void rmSuccessBox(Server remove) throws SQLException {
        JOptionPane optionPane = new JOptionPane();
        String message = "Server: " + remove.getHostname() +
                " von Kunde: " + getValueCustomerField().toString() + " gelöscht";
        if (model.getServer().isEmpty()) {
            getServerField.removeAllItems();
            getServerField.setVisible(false);
            ServerLabel.setVisible(false);
            removeServerButton.setVisible(false);
        }
        else {
            JOptionPane.showMessageDialog(optionPane, message, "Success", JOptionPane.INFORMATION_MESSAGE);
            getServerField.setModel(new DefaultComboBoxModel(model.getServer().toArray()));
            getServerField.setSelectedIndex(-1);
        }
    }

    /**
     * message box fuer kein server gefunden
     */
    public void noServerBox() {
        JOptionPane optionPane = new JOptionPane();
        String message = "Kunde: " + getValueCustomerField().toString() + " hat keine zugewiesenen Server!";
        JOptionPane.showMessageDialog(optionPane, message, "No Server for this Customer", JOptionPane.INFORMATION_MESSAGE);
        LOG.info(date2 + " " + System.getProperty("user.name") + " -- " + message);
    }

    /**
     * getter
     *
     * @return
     */
    public JTextField getAddCustomerNameTextField() {
        return addCustomerNameTextField;
    }

    /**
     * getter
     *
     * @return
     */
    public JTextField getAddCustomerBch4TextField() {
        return addCustomerBch4TextField;
    }

    /**
     * getter
     * @return server hostname
     */
    public JTextField getAddServerHostnameTextField() {
        return addServerHostnameTextField;
    }

    /**
     * getter
     * @return server ip
     */
    public JTextField getAddServerIPTextField() {
        return addServerIPTextField;
    }

    /**
     *  getter server type
     * @return
     */
    public String getServerTypeField() {
        return addServerTypeField.getSelectedItem().toString();
    }

    /**
     * getter fuer remove server combo box
     * @return
     */
    public String getValueRemoveServer() {
        if(getServerField.getSelectedItem() == null){
            return "notnull";
        }
        else {
            return getServerField.getSelectedItem().toString();
        }
    }

    /**
     * getter fuer customer combo box selection
     * @return
     */
    public String getValueCustomerField() {
        if(getCustomerField.getSelectedItem() == null){
            return "nocustomerselected";
        }
        else {
            return getCustomerField.getSelectedItem().toString();
        }
    }

    /**
     * gibt die tabellen zeile der gegebenen tabelle zurueck
     * @param table jtable
     * @return tabellen zeile als string array
     */
    private static String[] getSelectedTableRow(JTable table) {
        String[] result = new String[table.getColumnCount()];
        for(int column = 0; column < table.getColumnCount(); column++) {
            result[column] = table.getValueAt(table.getSelectedRow(), column).toString();
        }
        return result;
    }


    /**
     * holt die zeile vom select Table
     * anhand des index
     * @param rowindex zeilen index
     * @return zeile mit gegebenen index
     */
    private static String[] getTableRowFromSelectTable(int rowindex) {
        String[] result = new String[selectTable.getColumnCount()];
        for(int column = 0; column < selectTable.getColumnCount(); column++) {
            result[column] = selectTable.getValueAt(rowindex, column).toString();
        }
        return result;
    }


    /**
     * initialisiert den logger
     * logt in den gegebenen dateinamen
     * @param filename dateiname
     * @throws IOException
     */
    private static void initLogger(String filename) throws IOException {
        boolean append = true;
        Handler handler = new FileHandler(filename, append);
        handler.setFormatter(new SimpleFormatter());
        LOG.setLevel(loglevel);
        LOG.addHandler(handler);
    }


    /**
     * loescht alle tabellen einträge
     * @param tableModel tabellen model
     */
    private void clearTableModel(DefaultTableModel tableModel) {
        if (tableModel.getRowCount() > 0) {
            for (int rows = tableModel.getRowCount() - 1; rows > -1; rows--) {
                tableModel.removeRow(rows);
            }
        }
    }

    /**
     * laedt ein tabellen model mit den gefundenen customer results
     * @param tableModel tabellen model
     * @param text such text
     * @throws SQLException
     */
    private void loadTableModel(DefaultTableModel tableModel, String text) throws SQLException {
        for (CustomerResult customerResult : model.findCustomers(model.getCustomerResults(), text)) {
            tableModel.addRow(customerResult.loadCustomerResultToArray(customerResult));
        }
    }


    /**
     * holt die eintraege vom selected table
     * und gibt sie als string zurueck
     * @return selected eintraege als string
     */
    private String getSelectedEntries() {
        StringBuilder result = new StringBuilder();
        for (int row = selectTableModel.getRowCount() - 1; row > -1; row--) {
            for(int cell = 0; cell < selectTableModel.getColumnCount(); cell++) {
                if(cell == 2) {
                    SSHConnection connection = new SSHConnection();
                    connection.setIP(selectTableModel.getValueAt(row, cell).toString());
                    connection.setPort(22);
                    connection.sshSessionConnect();
                    connection.sshChannel1Connect();
                    connection.sshChannel2Connect();
                    result.append("Connection to: " + selectTableModel.getValueAt(row, cell) + "\n" +
                            "Is connected? " + connection.getSession() + "\n" +
                            "Send command sudo /home/scsupdater/test3.sh " +"\n"
                            + "Command sent successfully? " + connection.getChannel1() + "\n" +
                            "Send Command echo hier wäre das Script >> /home/scsupdater/hallo " + "\n"
                            + "Command sent successfully? " + connection.getChannel2() + "\n"
                            + "Disconnecting ..." + "\n"
                            + "-----------------------------------------");
                    connection.sshChannelDisconnect();
                    connection.sshSessionDissconnect();
                }
            }
            result.append("\n");
        }
        return result.toString();
    }


    /**
     * prüft ob eine zeile im selected table schon exsistiert
     * @param selectedRow gegebene zeile
     * @return true wenn die zeile gefunden wurde
     */
    private boolean rowExistsInSelectTable(String[] selectedRow) {
        if(selectTableModel.getRowCount() > 0) {
            for (int row = selectTableModel.getRowCount() - 1; row > -1; row--) {
                String[] tableRow = getTableRowFromSelectTable(row);
                if (rowsAreEquals(selectedRow, tableRow)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * vergleicht von 2 zeilen die zellen
     * ergibt false sobald eine zelle nicht übereinstimmt
     * @param row1 zeile 1
     * @param row2 zeile 2
     * @return true wenn alle zellen gleich sind
     */
    private boolean rowsAreEquals(String[] row1, String[] row2) {
        for(int cell = 0; cell < row1.length; cell++) {
            if (!row1[cell].equalsIgnoreCase(row2[cell])) {
                 return false;
            }
        }
        return true;
    }
}