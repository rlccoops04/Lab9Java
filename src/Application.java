import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import static java.lang.System.in;

public class Application  extends JPanel implements ActionListener{
    private WorkWithSQL sql;
    private final int width_window = 600;
    private final int delta_size_dialog = 20;
    private static JFrame mainFrame = null;
    private static Connection conn = null;
    private static ResultSet rsLocations = null;
    private static ResultSet rsBooks = null;
    private static ResultSet rsFind = null;
    private static Statement stmt = null;
    private static String SQL = null;
    private JPanel panelControl, panelFind, panelShow, panelShowBooks;
    private JButton buttonShow;
    private JButton buttonCreateLocation;
    private JButton buttonCreateBook;
    private JButton buttonEdit;
    private JButton buttonDelete;
    private JButton buttonDeleteBook;
    private JButton buttonDefault;
    private JButton buttonFind;
    private JTextField textFieldFind;
    private JTextField tfRemoveLocation;
    private DefaultTableModel tableShowModel;
    private DefaultTableModel tableShowBook;
    private JButton editLocation;
    private JTable tableShow;
    private JTable tableShowBooks;
    private Object[][] data;
    private JLabel labelFindCol;
    private JButton editBook;
    private JTextField tfRemoveBook;
    private JLabel labelFindColBooks;
    public Application() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        //Создание панели "Управление".
        panelControl = new JPanel();
        panelControl.setPreferredSize(new Dimension(width_window, 120));
        panelControl.setBorder(BorderFactory.createTitledBorder("\"Управление\""));
        add(Box.createRigidArea(new Dimension(0, 10))); // Отступ 10 пикселей
        panelControl.setLayout(new FlowLayout());
        buttonShow = new JButton("Поиск");
        buttonShow.addActionListener(this);
        buttonCreateLocation = new JButton("Создать место");
        buttonCreateLocation.addActionListener(this);
        buttonCreateBook = new JButton("Создать книгу");
        buttonCreateBook.addActionListener(this);
        editLocation = new JButton("Изменить место");
        editLocation.addActionListener(this);
        editBook = new JButton("Изменить книгу");
        editBook.addActionListener(this);
        buttonDelete = new JButton("Удалить место");
        buttonDelete.addActionListener(this);
        buttonDeleteBook = new JButton("Удалить книгу");
        buttonDeleteBook.addActionListener(this);
        buttonDefault = new JButton("Сброс");
        buttonDefault.addActionListener(this);
        panelControl.add(buttonShow);
        panelControl.add(buttonCreateLocation);
        panelControl.add(buttonCreateBook);
//        panelControl.add(buttonEdit);
        panelControl.add(buttonDefault);
        add(panelControl);
        //Создание панели "Поиск".
        panelFind = new JPanel();
        panelFind.setPreferredSize(new Dimension(width_window, 50));
        panelFind.setBorder(BorderFactory.createTitledBorder("\"Поиск\""));
        panelFind.setLayout(new GridLayout());
        tfRemoveLocation = new JTextField();
        tfRemoveBook = new JTextField();
        panelFind.add(tfRemoveLocation);
        panelFind.add(editLocation);
        panelFind.add(buttonDelete);
        panelFind.add(tfRemoveBook);
        panelFind.add(editBook);
        panelFind.add(buttonDeleteBook);
//        textFieldFind = new JTextField();
//        buttonFind = new JButton("Поиск");
//        buttonFind.addActionListener(this);
//        panelFind.add(textFieldFind);
//        panelFind.add(buttonFind);
        add(Box.createRigidArea(new Dimension(0, 10))); // Отступ сверху вниз на 10 пикселей
        add(panelFind);

        //Создание панели "Список контактов".
        panelShow = new JPanel();
        panelShow.setPreferredSize(new Dimension(width_window, 130));
        panelShow.setLayout(new BoxLayout(panelShow, BoxLayout.Y_AXIS));
        panelShow.setBorder(BorderFactory.createTitledBorder("\"Список книг\""));
        add(Box.createRigidArea(new Dimension(0, 10))); // Отступ сверху вниз на 10 пикселей

        panelShowBooks = new JPanel();
        panelShowBooks.setPreferredSize(new Dimension(width_window, 130));
        panelShowBooks.setLayout(new BoxLayout(panelShowBooks, BoxLayout.Y_AXIS));
        panelShowBooks.setBorder(BorderFactory.createTitledBorder("\"Список мест\""));
        add(Box.createRigidArea(new Dimension(0, 10))); // Отступ сверху вниз на 10 пикселей
//        dataBooks = new Object[][]{};

        tableShowBook = new DefaultTableModel(new Object[]{"Id", "Автор","Назв. изд.","Издательство","Год публикации","Страниц","Год написания","Вес","Id места"}, 0);
        tableShowModel = new DefaultTableModel(new Object[]{"Id","Этаж", "Шкаф", "Полка"},0){
            // Disabling User Edits in a JTable with DefaultTableModel
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            };
        };

        tableShow = new JTable();
        tableShow.setModel(tableShowModel);
        tableShowBooks = new JTable();
        tableShowBooks.setModel(tableShowBook);

        panelShow.add(new JScrollPane(tableShow));
        labelFindCol = new JLabel("Найдено записей: 0");
        panelShow.add(labelFindCol);
        add(panelShow);

        panelShowBooks.add(new JScrollPane(tableShowBooks));
        labelFindColBooks = new JLabel("Найдено записей: 0");
        panelShowBooks.add(labelFindColBooks);
        add(panelShowBooks);
        try {
            var connection = DBConnector.connectToDb();
            System.out.println("База данных подключена");
            sql = new WorkWithSQL(connection);
        } catch(SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("Ошибка при подключении к бд");
        }
        findByString("");
        findByStringBooks("");
    }
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Адресная книга");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame = frame;
        JComponent componentPanelAddressBook = new Application();
        frame.setContentPane(componentPanelAddressBook);
        frame.pack();
        frame.setVisible(true);
    }
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        int dataToSize = 10;
        String[] dataTo = new String[dataToSize];

        for (int i = 0; i < dataToSize; i++) {
            dataTo[i] = "";
        }

        if ("Создать место".equals(command)) {
            JDialog dialogContact = new JDialog(mainFrame,
                    "Новое место...", JDialog.DEFAULT_MODALITY_TYPE);

            PanelLocation panelContact = new PanelLocation(command, dataTo);
            dialogContact.setBounds(
                    delta_size_dialog, delta_size_dialog,
                    panelContact.getContactPanelWidth()+ 3*delta_size_dialog,
                    panelContact.getContactPanelHeight() + delta_size_dialog);
            dialogContact.add(panelContact);
            dialogContact.setVisible(true);
        }
        if("Поиск".equals(command)) {
            JDialog dialogContact = new JDialog(mainFrame,
                    "...", JDialog.DEFAULT_MODALITY_TYPE);

            PanelPublicshing panelContact = new PanelPublicshing(command, dataTo);
            dialogContact.setBounds(
                    delta_size_dialog, delta_size_dialog,
                    panelContact.getContactPanelWidth()+ 3*delta_size_dialog,
                    panelContact.getContactPanelHeight() + delta_size_dialog);
            dialogContact.add(panelContact);
            dialogContact.setVisible(true);
        }
        if("Изменить место".equals(command)) {
            JDialog dialogContact = new JDialog(mainFrame,
                    "Новое место...", JDialog.DEFAULT_MODALITY_TYPE);
            int s = Integer.parseInt(tfRemoveLocation.getText()) - 1;
            dataTo[0] = tableShow.getValueAt(s,0).toString();
            dataTo[1] = tableShow.getValueAt(s,1).toString();
            dataTo[2] = tableShow.getValueAt(s,2).toString();
            dataTo[3] = tableShow.getValueAt(s,3).toString();

            PanelLocation panelContact = new PanelLocation(command, dataTo);
            dialogContact.setBounds(
                    delta_size_dialog, delta_size_dialog,
                    panelContact.getContactPanelWidth()+ 3*delta_size_dialog,
                    panelContact.getContactPanelHeight() + delta_size_dialog);
            dialogContact.add(panelContact);
            dialogContact.setVisible(true);
        }
        if("Изменить книгу".equals(command)) {
            JDialog dialogContact = new JDialog(mainFrame,
                    "Изменить место", JDialog.DEFAULT_MODALITY_TYPE);
            int s = Integer.parseInt(tfRemoveBook.getText()) - 1;
            dataTo[0] = tableShowBooks.getValueAt(s,0).toString();
            dataTo[1] = tableShowBooks.getValueAt(s,1).toString();
            dataTo[2] = tableShowBooks.getValueAt(s,2).toString();
            dataTo[3] = tableShowBooks.getValueAt(s,3).toString();
            dataTo[4] = tableShowBooks.getValueAt(s,4).toString();
            dataTo[5] = tableShowBooks.getValueAt(s,5).toString();
            dataTo[6] = tableShowBooks.getValueAt(s,6).toString();
            dataTo[7] = tableShowBooks.getValueAt(s,7).toString();
            dataTo[8] = tableShowBooks.getValueAt(s,8).toString();

            PanelBook panelContact = new PanelBook(command, dataTo);
            dialogContact.setBounds(
                    delta_size_dialog, delta_size_dialog,
                    panelContact.getContactPanelWidth()+ 3*delta_size_dialog,
                    panelContact.getContactPanelHeight() + delta_size_dialog);
            dialogContact.add(panelContact);
            dialogContact.setVisible(true);

        }
        if("Создать книгу".equals(command)) {
            JDialog dialogContact = new JDialog(mainFrame,
                    "Новая книга...", JDialog.DEFAULT_MODALITY_TYPE);

            PanelBook panelContact = new PanelBook(command, dataTo);
            dialogContact.setBounds(
                    delta_size_dialog, delta_size_dialog,
                    panelContact.getContactPanelWidth()+ 3*delta_size_dialog,
                    panelContact.getContactPanelHeight() + delta_size_dialog);
            dialogContact.add(panelContact);
            dialogContact.setVisible(true);
        }
        if("Сброс".equals(command)) {
            sql.DeleteAll();
            String filename = "log.txt";
            File f = new File(filename);
            StringBuilder sb = new StringBuilder();
            if(f.exists()) {
                try{
                    BufferedReader br = new BufferedReader(new FileReader(f.getAbsoluteFile()));
                    try{
                        String s;
                        while((s = br.readLine())!=null){//построчное чтение
                            sb.append(s);
                            sb.append("\n");
                            String[] t = s.split(" ");
                            if(t.length == 4) {
                                int id,floor, closet, shelf;
                                id = Integer.parseInt(t[0]);
                                floor = Integer.parseInt(t[1]);
                                closet = Integer.parseInt(t[2]);
                                shelf = Integer.parseInt(t[3]);
                                sql.InsertDefaultData(id,floor, closet, shelf);
                            } else if(t.length == 8) {
                                String author, publication, publicationHouse;
                                int yearPublic, pages,yearWrite, weight, locId;
                                author = t[0];
                                publication = t[1];
                                publicationHouse = t[2];
                                yearPublic = Integer.parseInt(t[3]);
                                pages = Integer.parseInt(t[4]);
                                yearWrite = Integer.parseInt(t[5]);
                                weight = Integer.parseInt(t[6]);
                                locId = Integer.parseInt(t[7]);
                                System.out.println(locId);
                                sql.CreateNewBook(author, publication, publicationHouse,yearPublic, pages,yearWrite,weight,locId);
                            }
                        }
                    }finally{findByStringBooks("");findByString("");br.close();}
                }catch(IOException exception){throw new RuntimeException();}
            }
        }
        if ("Поиск".equals(command)){
            System.out.println(tableShowBooks.getSelectedRow());
        }
        if("Удалить книгу".equals(command)) {
            int s = Integer.parseInt(tfRemoveBook.getText()) - 1;
            int id = Integer.parseInt(tableShowBooks.getValueAt(s,0).toString());
            sql.DeleteBook(id);
            findByStringBooks("");
        }
        if("Удалить место".equals(command)) {
            int s = Integer.parseInt(tfRemoveLocation.getText()) - 1;
            int id = Integer.parseInt(tableShow.getValueAt(s,0).toString());
            sql.DeleteLocation(id);
            findByString("");
        }
    }
    private void findByString(String textFind) {
        try {
            while(tableShowModel.getRowCount() > 0) {
                tableShowModel.removeRow(0);
            }
            rsLocations = sql.ShowAllLocations();
            System.out.println("Ищем места");
            while(rsLocations.next()) {
                String id = rsLocations.getString("Id");
                String floor = rsLocations.getString("Floor");
                String closet = rsLocations.getString("Closet");
                String shelf = rsLocations.getString("Shelf");
                tableShowModel.addRow(new Object[]{id, floor, closet, shelf});
            }
            labelFindCol.setText("Найдено записей: " + tableShowModel.getRowCount());
        } catch (SQLException err) {
            System.out.println(err.getMessage());
        }
    }
    private void findByStringBooks(String textFind) {
        try {
            while(tableShowBook.getRowCount() > 0) {
                tableShowBook.removeRow(0);
            }
            rsBooks = sql.ShowAllBooks();
            System.out.println("Ищем книги");
            while(rsBooks.next()) {
                System.out.println("кажись есть че");
                String id = rsBooks.getString("Id");
                String author = rsBooks.getString("Author");
                String publication = rsBooks.getString("Publication");
                String publicshingHouse = rsBooks.getString("PublicshingHouse");
                String yearPublic = rsBooks.getString("YearPublic");
                String pages = rsBooks.getString("Pages");
                String yearWrite = rsBooks.getString("YearWrite");
                String weight = rsBooks.getString("Weight");
                String locId = rsBooks.getString("LocationId");
                tableShowBook.addRow(new Object[]{id, author, publication, publicshingHouse, yearPublic, pages, yearWrite, weight,locId});
            }
            labelFindColBooks.setText("Найдено записей: " + tableShowBook.getRowCount());
        } catch (SQLException err) {
            System.out.println(err.getMessage());
        }
    }

    class PanelLocation extends JPanel implements ActionListener {
        private final int width_window = 300;//Кратно трём
        private final int height_window = 143;
        private final int height_button_panel = 40;
        private final int height_gap = 3;
        private String mode;
        private String dataTo[];

        private JPanel panelUp, panelLabel, panelText, panelButton;
        private JLabel labelFloor;
        private JLabel labelCloset;
        private JLabel labelShelf;
        private JTextField txtFieldFloor;
        private JTextField txtFieldCloset;
        private JTextField txtFieldShelf;
        private JButton buttonApplay;
        private JButton buttonCancel;
        public PanelLocation(String mode, String[] dataTo) {
            super();
            this.mode = mode;
            this.dataTo = dataTo;
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            setPreferredSize(new Dimension(width_window, height_window));
            panelUp = new JPanel();//Панель для размещения панелей
            panelLabel = new JPanel();
            panelText = new JPanel();
            panelButton = new JPanel();
            labelFloor = new JLabel("Этаж");
            labelCloset = new JLabel("Шкаф");
            labelShelf = new JLabel("Полка");
            txtFieldFloor = new JTextField(dataTo[1]);
            txtFieldCloset = new JTextField(dataTo[2]);
            txtFieldShelf = new JTextField(dataTo[3]);
            buttonApplay = new JButton("Принять");
            buttonApplay.addActionListener(this);
            buttonCancel = new JButton("Отменить");
            buttonCancel.addActionListener(this);
            panelUp.setPreferredSize(new Dimension(width_window, height_window
                    - height_button_panel - height_gap));
            panelUp.setBorder(BorderFactory.createBevelBorder(1));
            add(panelUp);
            panelUp.setLayout(new BoxLayout(panelUp, BoxLayout.LINE_AXIS));
            panelLabel.setPreferredSize(new Dimension(width_window/3, height_window
                    - height_button_panel - height_gap));
            panelLabel.setBorder(BorderFactory.createBevelBorder(1));
            panelLabel.setLayout(new GridLayout(4,1));
            panelLabel.add(labelFloor);
            panelLabel.add(labelCloset);
            panelLabel.add(labelShelf);
            panelText.setPreferredSize(new Dimension(2*width_window/3, height_window
                    - height_button_panel - height_gap));
            panelText.setBorder(BorderFactory.createBevelBorder(1));
            panelText.setLayout(new GridLayout(4,1));
            panelText.add(txtFieldFloor);
            panelText.add(txtFieldCloset);
            panelText.add(txtFieldShelf);
            panelUp.add(panelLabel);
            panelUp.add(panelText);
            add(Box.createRigidArea(new Dimension(0, height_gap))); // Отступ 10 пикселей
            panelButton.setPreferredSize(new Dimension(width_window, height_button_panel));
            panelButton.setBorder(BorderFactory.createBevelBorder(1));
            add(panelButton);
            panelButton.setLayout(new FlowLayout());
            panelButton.add(buttonApplay);
            panelButton.add(buttonCancel);

            if ("Просмотреть".equals(mode)) {
                buttonApplay.setEnabled(false);
                txtFieldFloor.setEditable(false);
                txtFieldCloset.setEditable(false);
                txtFieldCloset.setEditable(false);
            }
        }
        public int getContactPanelWidth(){
            return width_window;
        }
        public int getContactPanelHeight(){
            return height_window;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            JDialog dialog = (JDialog) SwingUtilities.getWindowAncestor(this);
            if ("Отменить".equals(command)) {
                dialog.dispose();
            }
            if ("Принять".equals(command)) {
                System.out.println(txtFieldFloor.getText().length());
                System.out.println(txtFieldCloset.getText().length());
                System.out.println(txtFieldShelf.getText().length());
                String SQL_Update_TBL_PhoneNumbers = null;
                String SQL_Update_TBL_Contacts = null;
                if ("Создать место".equals(mode)) {
                    sql.CreateNewLocation(Integer.parseInt(txtFieldFloor.getText()), Integer.parseInt(txtFieldCloset.getText()),Integer.parseInt(txtFieldShelf.getText()));
                    findByString("");
                }
                if ("Изменить место".equals(mode)) {
                    sql.UpdateLocation(Integer.parseInt(dataTo[0]),Integer.parseInt(txtFieldFloor.getText()),
                            Integer.parseInt(txtFieldCloset.getText()),
                            Integer.parseInt(txtFieldShelf.getText()));
                    findByString("");
                }
                dialog.dispose();
            }

        }
    }
    class PanelPublicshing extends JPanel implements ActionListener {
        private final int width_window = 300;//Кратно трём
        private final int height_window = 400;
        private final int height_button_panel = 40;
        private final int height_gap = 3;
        private String mode;
        private String dataTo[];

        private JPanel panelUp, panelLabel, panelText, panelButton;
        private JLabel labelFloor,labelAuthor;
        private JTextField tfFloor,tfAuthor;
        private JButton buttonSearchFloor,buttonSearchAuthor;
        private JButton buttonCancel;
        private DefaultTableModel tabModel;
        private JTable tabShow;
        public PanelPublicshing(String mode, String[] dataTo) {
            super();
            this.mode = mode;
            this.dataTo = dataTo;
            tabModel = new DefaultTableModel(new Object[]{"Найдено"}, 0);
            tabShow = new JTable();
            tabShow.setModel(tabModel);

            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            setPreferredSize(new Dimension(width_window, height_window));
            panelUp = new JPanel();//Панель для размещения панелей
            panelLabel = new JPanel();
            panelText = new JPanel();
            panelButton = new JPanel();
            labelFloor = new JLabel("Этаж");
            labelAuthor = new JLabel("Автор");
            tfFloor = new JTextField();
            tfAuthor = new JTextField();
            buttonSearchFloor = new JButton("Найти издательства");
            buttonSearchFloor.addActionListener(this);
            buttonSearchAuthor = new JButton("Найти издания автора");
            buttonSearchAuthor.addActionListener(this);

            buttonCancel = new JButton("Отменить");
            buttonCancel.addActionListener(this);
            panelUp.setPreferredSize(new Dimension(width_window, height_window
                    - height_button_panel - height_gap));
            panelUp.setBorder(BorderFactory.createBevelBorder(1));
            add(panelUp);
            panelUp.setLayout(new BoxLayout(panelUp, BoxLayout.LINE_AXIS));
            panelLabel.setPreferredSize(new Dimension(width_window / 3, height_window
                    - height_button_panel - height_gap));
            panelLabel.setBorder(BorderFactory.createBevelBorder(1));
            panelLabel.setLayout(new GridLayout(4, 1));
            panelLabel.add(labelFloor);
            panelLabel.add(labelAuthor);

            panelText.setPreferredSize(new Dimension(2 * width_window / 3, height_window
                    - height_button_panel - height_gap));
            panelText.setBorder(BorderFactory.createBevelBorder(1));
            panelText.setLayout(new GridLayout(4, 1));
            panelText.add(tfFloor);
            panelText.add(tfAuthor);
            panelText.add(tabShow);
            panelUp.add(panelLabel);
            panelUp.add(panelText);
            add(Box.createRigidArea(new Dimension(0, height_gap))); // Отступ 10 пикселей
            panelButton.setPreferredSize(new Dimension(width_window, height_button_panel));
            panelButton.setBorder(BorderFactory.createBevelBorder(1));
            add(panelButton);
            panelButton.setLayout(new FlowLayout());
            panelButton.add(buttonSearchFloor);
            panelButton.add(buttonSearchAuthor);

            panelButton.add(buttonCancel);

        }

        public int getContactPanelWidth() {
            return width_window;
        }

        public int getContactPanelHeight() {
            return height_window;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            JDialog dialog = (JDialog) SwingUtilities.getWindowAncestor(this);
            if ("Отменить".equals(command)) {
                dialog.dispose();
            }
            if("Найти издательства".equals(command)) {
                try {
                    while(tabShow.getRowCount() > 0) {
                        tabModel.removeRow(0);
                    }
                    int floor = Integer.parseInt(tfFloor.getText());
                    rsFind = sql.ShowAllPublicationInCurrentFloorByLexicOrder(floor);
                    while(rsFind.next()) {
                        String publicshingHouse = rsFind.getString("PublicshingHouse");
                        tabModel.addRow(new Object[]{publicshingHouse});
                    }
                } catch (SQLException err) {
                    System.out.println(err.getMessage());
                }
            }
            if("Найти издания автора".equals(command)) {
                try {
                    while(tabShow.getRowCount() > 0) {
                        tabModel.removeRow(0);
                    }
                    String author = tfAuthor.getText();
                    rsFind = sql.ShowAllPublicationByAuthor(author);
                    while(rsFind.next()) {
                        String publication = rsFind.getString("Publication");
                        tabModel.addRow(new Object[]{publication});
                    }
                } catch (SQLException err) {
                    System.out.println(err.getMessage());
                }

            }
        }
    }
    class PanelBook extends JPanel implements ActionListener {
        private final int width_window = 300;//Кратно трём
        private final int height_window = 143;
        private final int height_button_panel = 40;
        private final int height_gap = 3;
        private String mode;
        private String dataTo[];

        private JPanel panelUp, panelLabel, panelText, panelButton;
        private JLabel labelAuthor, labelPublish, labelPublish_House, labelYearPublic, labelPages, labelYearWrite, labelWeight, labelLocId;
        private JTextField tfAuthor, tfPublish, tfPublish_House, tfYearPublic, tfPages, tfYearWrite, tfWeight, tfLocId;
        private JButton buttonApplay;
        private JButton buttonCancel;
        public PanelBook(String mode, String[] dataTo) {
            super();
            this.mode = mode;
            this.dataTo = dataTo;
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            setPreferredSize(new Dimension(width_window, height_window));
            panelUp = new JPanel();//Панель для размещения панелей
            panelLabel = new JPanel();
            panelText = new JPanel();
            panelButton = new JPanel();
            labelAuthor= new JLabel("Автор");
            labelPublish = new JLabel("Название издания");
            labelPublish_House = new JLabel("Издательство");
            labelYearPublic = new JLabel("Год публикации");
            labelPages = new JLabel("Количество страниц");
            labelYearWrite = new JLabel(("Год написания"));
            labelWeight = new JLabel("Вес");
            labelLocId = new JLabel("Id места");
            tfAuthor = new JTextField((dataTo[1]));
            tfPublish = new JTextField(dataTo[2]);
            tfPublish_House = new JTextField(dataTo[3]);
            tfYearPublic = new JTextField(dataTo[4]);
            tfPages = new JTextField(dataTo[5]);
            tfYearWrite = new JTextField(dataTo[6]);
            tfWeight = new JTextField(dataTo[7]);
            tfLocId = new JTextField(dataTo[8]);
            buttonApplay = new JButton("Принять");
            buttonApplay.addActionListener(this);
            buttonCancel = new JButton("Отменить");
            buttonCancel.addActionListener(this);
            panelUp.setPreferredSize(new Dimension(width_window, height_window
                    - height_button_panel - height_gap));
            panelUp.setBorder(BorderFactory.createBevelBorder(1));
            add(panelUp);
            panelUp.setLayout(new BoxLayout(panelUp, BoxLayout.LINE_AXIS));
            panelLabel.setPreferredSize(new Dimension(width_window/3, height_window
                    - height_button_panel - height_gap));
            panelLabel.setBorder(BorderFactory.createBevelBorder(1));
            panelLabel.setLayout(new GridLayout(4,1));
            panelLabel.add(labelAuthor);
            panelLabel.add(labelPublish);
            panelLabel.add(labelPublish_House);
            panelLabel.add(labelYearPublic);
            panelLabel.add(labelPages);
            panelLabel.add(labelYearWrite);
            panelLabel.add(labelWeight);
            panelLabel.add(labelLocId);

            panelText.setPreferredSize(new Dimension(2*width_window/3, height_window
                    - height_button_panel - height_gap));
            panelText.setBorder(BorderFactory.createBevelBorder(1));
            panelText.setLayout(new GridLayout(4,1));
            panelText.add(tfAuthor);
            panelText.add(tfPublish);
            panelText.add(tfPublish_House);
            panelText.add(tfYearPublic);
            panelText.add(tfPages);
            panelText.add(tfYearWrite);
            panelText.add(tfWeight);
            panelText.add(tfLocId);
            panelUp.add(panelLabel);
            panelUp.add(panelText);
            add(Box.createRigidArea(new Dimension(0, height_gap))); // Отступ 10 пикселей
            panelButton.setPreferredSize(new Dimension(width_window, height_button_panel));
            panelButton.setBorder(BorderFactory.createBevelBorder(1));
            add(panelButton);
            panelButton.setLayout(new FlowLayout());
            panelButton.add(buttonApplay);
            panelButton.add(buttonCancel);

            if ("Просмотреть".equals(mode)) {
                buttonApplay.setEnabled(false);
                tfAuthor.setEditable(false);
                tfPublish.setEditable(false);
                tfPublish_House.setEditable(false);
                tfYearPublic.setEditable(false);
                tfPages.setEditable(false);
                tfYearWrite.setEditable(false);
                tfWeight.setEditable(false);
                tfLocId.setEditable(false);
            }
        }
        public int getContactPanelWidth(){
            return width_window;
        }
        public int getContactPanelHeight(){
            return height_window;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            JDialog dialog = (JDialog) SwingUtilities.getWindowAncestor(this);
            if ("Отменить".equals(command)) {
                dialog.dispose();
            }
            if ("Принять".equals(command)) {
                if ("Создать книгу".equals(mode)) {
                    sql.CreateNewBook(tfAuthor.getText(),
                            tfPublish.getText(),
                            tfPublish_House.getText(),
                            Integer.parseInt(tfYearPublic.getText()),
                            Integer.parseInt(tfPages.getText()),
                            Integer.parseInt(tfYearWrite.getText()),
                            Integer.parseInt(tfWeight.getText()),
                            Integer.parseInt((tfLocId.getText())));
                    findByStringBooks("");
                }
                if ("Изменить книгу".equals(mode)) {
                    sql.UpdateBook(Integer.parseInt(dataTo[0]),
                            tfAuthor.getText(),
                            tfPublish.getText(),
                            tfPublish_House.getText(),
                            Integer.parseInt(tfYearPublic.getText()),
                            Integer.parseInt(tfPages.getText()),
                            Integer.parseInt(tfYearWrite.getText()),
                            Integer.parseInt(tfWeight.getText()),
                            Integer.parseInt((tfLocId.getText())));
                    findByStringBooks("");

                }
                dialog.dispose();
            }

        }
    }
}
