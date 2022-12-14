package su.darktime;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class TestVM {

    public static final String DEF_DRIVER = "org.apache.derby.jdbc.ClientDriver";
    public static final  String DEF_URL = "jdbc:derby:db/vmderby;create=true";
    public static final  String DEF_TEMPL_PATH = "sql/createTemplDb.sql ";
    public static final  String DEF_CDB_PATH = "sql/createDb.sql";

    public static final  String[][] DEF_T_PATHS = new String[][]{
            {"sql/t1.sql"},{"sql/t2.sql"},{"sql/t3.sql"},{"sql/t4.sql"},
            {"sql/add.sql","sql/check.sql","sql/del1.sql","sql/del2.sql"}
                };
    public static final int DEF_ORDER_CNTER = 300;

    private Connection conn;

    private JFrame testFrame;

    private JTextField driverField =null;
    private JTextField urlField =null;

    private  int currentTest = 3;
    private List<JPanel> tests = new LinkedList<>();
    public void createComponents() {

        JPanel formControlsPanel = new JPanel();
        setYLayout(formControlsPanel,null);

        formControlsPanel.add(addDbConnectControls());
        formControlsPanel.add(addDbCreateControls());

        tests.add(createT1());
        tests.add(createT2());
        tests.add(createT3());
        tests.add(createT4());
        tests.add(createT5());

        testFrame =new JFrame();
        testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        testFrame.setTitle("Тестовая программа MVV");
        testFrame.setContentPane(formControlsPanel);

        JButton btPrev = new JButton("Пред. тест");
        btPrev.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                changeTest(-1);
            }
        });
        JButton btNext =new JButton("Сл. тест");
        btNext.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeTest(1);
            }
        });


        addCompsRows(new Component[]{btPrev,btNext},formControlsPanel);
        formControlsPanel.add(tests.get(currentTest));

        testFrame.pack();

        int wPos = (int) (screenSize.getWidth()/2 - testFrame.getWidth()/2);
        int hPos = (int) (screenSize.getHeight()/2 - testFrame.getHeight()/2);
        testFrame.setLocation(wPos,hPos);
        testFrame.setResizable(false);
        testFrame.setVisible(true);
    }

    private void changeTest(int shift) {
        testFrame.remove(tests.get(currentTest));
        currentTest+=shift;
        if (currentTest<0)
            currentTest=tests.size()-1;
        if (currentTest>= tests.size())
            currentTest = 0;
        testFrame.add(tests.get(currentTest),3);
        testFrame.pack();
        testFrame.repaint();
    }

    private JPanel createT1()
    {
        JLabel subproductLabel = new JLabel("Субпродукт");
        JComboBox subproductText = new JComboBox<String>();

        String[][] rs = OrderGenerator.produtSubProduct;
        for (String[] r : rs) {
            subproductText.addItem(r[1]);
        }
        subproductText.setEditable(true);

        JLabel scriptFileLabel = new JLabel("Запрос");
        JTextField scriptFile = new JTextField(25);
        scriptFile.setText(DEF_T_PATHS[0][0]);

        JTextField outText = new JTextField(25);
        outText.setEditable(false);


        JButton btnExec = new JButton("Выполнить");

        final TitledBorder tileBorder = BorderFactory.createTitledBorder("Тест 1");
        final JPanel execSQL =new JPanel();

        btnExec.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    List<String> ll = ScriptProcessor.getSqlArray(scriptFile.getText());
                    String header=ll.remove(0);
                    String headerShort=ll.remove(0);
                    tileBorder.setTitle(headerShort);
                    String sql=ll.get(0);
                    {
                        Map<String,String> mp = new HashMap<>();
                        String s = subproductText.getSelectedItem().toString();
                        mp.put("SUBPRODUCT",DbUtils.wrapQT(s));
                        sql=DbUtils.replacePlaceHolders(sql,mp);
                        System.out.print(":" + sql);
                        Object[][] res=DbUtils.execSQL(getConnection(),sql,new LinkedList<>(),true);
                        System.out.println(" OK");
                        if (res.length>0 && res[0].length>0)
                            outText.setText(res[0][0].toString());
                    }
                    execSQL.repaint();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        setYLayout(execSQL,tileBorder);

        addCompsRows(new Component[]{subproductLabel, subproductText},execSQL);
        addCompsRows(new Component[]{scriptFileLabel, scriptFile},execSQL);
        addCompsRows(new Component[]{btnExec, outText},execSQL);

        return  execSQL;
    }

    private void setYLayout(JPanel execSQL, TitledBorder titleBorder) {
        if (titleBorder!=null)
            execSQL.setBorder(titleBorder);
        BoxLayout boxLayout = new BoxLayout(execSQL, BoxLayout.Y_AXIS);
        execSQL.setLayout(boxLayout);
    }

    private JPanel createT2()
    {
//        Locale locale = new Locale("en");
//        UtilDateModel model = new UtilDateModel();
//        JDatePanelImpl datePanel = new JDatePanelImpl(model,locale);
//        JDatePickerImpl datePicker = new JDatePickerImpl(datePanel,locale.);

        LocalDateTime stateDate = LocalDateTime.now();
        stateDate=stateDate.minusMonths(1);
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        JLabel fromLabel = new JLabel("С ");
        JTextField fromText = new JTextField();
        fromText.setText(stateDate.format(formatterDate));
        stateDate=stateDate.plusMonths(1);
        JLabel toLabel = new JLabel("По");
        JTextField toText = new JTextField();
        toText.setText(stateDate.format(formatterDate));

        JLabel cntLabel = new JLabel("Вывести кол-во");
        JTextField cntText = new JTextField();
        cntText.setText("5");


        JLabel scriptFileLabel = new JLabel("Запрос");
        JTextField scriptFile = new JTextField(25);
        scriptFile.setText(DEF_T_PATHS[1][0]);

        JTextField outText = new JTextField(25);
        outText.setEditable(false);


        JButton btnExec = new JButton("Выполнить");

        final TitledBorder tileBorder = BorderFactory.createTitledBorder("Тест 2");
        final JPanel execSQL =new JPanel();
        btnExec.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    List<String> ll = ScriptProcessor.getSqlArray(scriptFile.getText());
                    String header=ll.remove(0);
                    String headerShort=ll.remove(0);

                    String sql=ll.get(0);
                    {
                        Map<String,String> mp = new HashMap<>();
                        String from = fromText.getText();
                        String to = toText.getText();
                        String cnt= cntText.getText();

                        try {
                            mp.put("BEGIN_DATE",DbUtils.wrapQT(from));
                            mp.put("END_DATE",DbUtils.wrapQT(to));
                            mp.put("N_CNT",cnt);
                            Integer.parseInt(cnt);
                            headerShort = headerShort.replace("N",cnt);
                            tileBorder.setTitle(headerShort);

                            sql=DbUtils.replacePlaceHolders(sql,mp);
                            System.out.print(":" + sql);
                            Object[][] res=DbUtils.execSQL(getConnection(),sql,new LinkedList<>(),true);
                            System.out.println(" OK");
                            StringBuilder sb = new StringBuilder();

                            if (res.length>0)
                            {
                                for (Object[] re : res)
                                    sb.append(re[0]).append(" | ");
                            }
                            else
                                sb.append("Нет продуктов за интервал");
                            outText.setText(sb.toString());
                        } catch (NumberFormatException ex) {
                            outText.setText("Кол-во должно быть целым");
                        }
                    }
                    execSQL.repaint();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        setYLayout(execSQL,tileBorder);
        addCompsRows(new Component[]{fromLabel,fromText},execSQL);
        addCompsRows(new Component[]{toLabel,toText},execSQL);
        addCompsRows(new Component[]{cntLabel,cntText},execSQL);
        addCompsRows(new Component[]{scriptFileLabel,scriptFile},execSQL);
        addCompsRows(new Component[]{btnExec,outText},execSQL);

        return  execSQL;
    }


    private JPanel createT4()
    {
        JLabel orderLabel = new JLabel("Номер заявки");
        JTextField orderNText = new JTextField();
        orderNText.setText("43351231534");

        JLabel scriptFileLabel = new JLabel("Запрос");
        JTextField scriptFile = new JTextField(25);
        scriptFile.setText(DEF_T_PATHS[3][0]);

//        JTextField outComment = new JTextField(60);
//        outComment.setEditable(false);

        JTextArea outText = new JTextArea(10, 75);
        outText.setEditable(false);
        JScrollPane scrollOut = new JScrollPane(outText);
        scrollOut.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        JButton btnExec = new JButton("Выполнить");

        final TitledBorder tileBorder = BorderFactory.createTitledBorder("Тест 4");
        final JPanel execSQL =new JPanel();

        btnExec.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    List<String> ll = ScriptProcessor.getSqlArray(scriptFile.getText());
                    String header=ll.remove(0);
                    String headerShort=ll.remove(0);
                    tileBorder.setTitle(headerShort);
                    String sql=ll.get(0);
                    {
                        String state ="";
                        StringBuilder states=new StringBuilder();

                        Map<String,String> mp = new HashMap<>();
                        String s = orderNText.getText();
                        mp.put("APP_NUM",DbUtils.wrapQT(s));
                        sql=DbUtils.replacePlaceHolders(sql,mp);
                        System.out.print(":" + sql);
                        List<ColumnHeadBean> listOfColumn = new LinkedList<>();
                        Object[][] res=DbUtils.execSQL(getConnection(),sql, listOfColumn,true);
                        StringBuilder sb = new StringBuilder();
                        for (ColumnHeadBean columnHeadBean : listOfColumn)
                        {
                            sb.append(columnHeadBean.getName()).append(" | ");
                        }
                        sb.append("\n");
                        if (res.length==0)
                        {
                            state="Заявка не входила в систему";
                        }
                        else
                        {
                            boolean nullIt = false;
                            states.append("Заявка " +s + " зашла в этапы:");
                            for (Object[] re : res) {
                                for (Object o : re)
                                {
                                    if (o == null && !nullIt)
                                        nullIt = true;
                                    sb.append(o).append(" | ");
                                }
                                states.append(re[0]).append(" | ");
                                if (nullIt)
                                {
                                    state = " \nи находится на этапе " + re[0] + " с " + re[1];
                                }
                                else
                                {
                                    state = " \nпоследний этап " + re[0] + " в " + re[1] + " вышла " + re[2] + " с кодом результата " + re[3];
                                }
                                sb.append("\n");
                            }
                        }
                        states.append(" ").append(state).append("\n\n");
                        outText.setText(sb.insert(0,states).toString());
                        System.out.println(sb);
                        System.out.println(" OK");
                    }
                    execSQL.repaint();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        setYLayout(execSQL,tileBorder);
        addCompsRows(new Component[]{orderLabel,orderNText},execSQL);
        addCompsRows(new Component[]{scriptFileLabel,scriptFile},execSQL);

        JPanel groupLayout = new JPanel();
        GridBagLayout layout = new GridBagLayout();
        groupLayout.setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();


        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
//        groupLayout.add(outComment,gbc);
//
//        gbc.gridx = 1;
//        gbc.gridy = 0;
        groupLayout.add(btnExec,gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;
        groupLayout.add(scrollOut,gbc);

        execSQL.add(groupLayout);

        return  execSQL;
    }

        private JPanel createT3()
    {
        JLabel daysLabel = new JLabel("Кол-во суток");
        JTextField daysText = new JTextField();
        daysText.setText("3");

        JLabel scriptFileLabel = new JLabel("Запрос");
        JTextField scriptFile = new JTextField(25);
        scriptFile.setText(DEF_T_PATHS[2][0]);

        JTextField outText = new JTextField(25);
        outText.setEditable(false);

        JButton btnExec = new JButton("Выполнить");

        final TitledBorder tileBorder = BorderFactory.createTitledBorder("Тест 3");
        final JPanel execSQL =new JPanel();

        btnExec.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    List<String> ll = ScriptProcessor.getSqlArray(scriptFile.getText());
                    String header=ll.remove(0);
                    String headerShort=ll.remove(0);

                    String sql=ll.get(0);
                    {
                        Map<String,String> mp = new HashMap<>();
                        String s = daysText.getText();
                        try {
                            Integer.parseInt(s);
                            headerShort=headerShort.replace("N",s);
                            tileBorder.setTitle(headerShort);
                            mp.put("NUM_DAY",s);

                            sql=DbUtils.replacePlaceHolders(sql,mp);
                            System.out.print(":" + sql);
                            Object[][] res=DbUtils.execSQL(getConnection(),sql,new LinkedList<>(),true);
                            System.out.println(" OK");
                            StringBuilder sb = new StringBuilder();


                            if (res.length>0)
                            {
                                for (Object[] re : res)
                                    sb.append(re[0]).append(" | ");
                            }
                           else
                            {
                                sb.append("Нет заявок на этапе принятия решения более "+s+" суток");
                            }
                            outText.setText(sb.toString());
                        } catch (NumberFormatException ex) {
                            outText.setText(s+" не является целым кол-вом суток");
                        }
                    }
                    execSQL.repaint();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        setYLayout(execSQL,tileBorder);
        addCompsRows(new Component[]{daysLabel,daysText},execSQL);
        addCompsRows(new Component[]{scriptFileLabel,scriptFile},execSQL);
        addCompsRows(new Component[]{btnExec,outText},execSQL);

        return  execSQL;
    }

    private JPanel createT5()
    {
        final TitledBorder tileBorder = BorderFactory.createTitledBorder("Удаление дубликатов заявок");
        final JPanel execSQL =new JPanel();

        JLabel addFileLabel = new JLabel("Добавление");
        JTextField addFile = new JTextField(25);
        addFile.setText(DEF_T_PATHS[4][0]);

        JTextField addOutText = new JTextField(25);
        addOutText.setEditable(false);
        JButton addExec = new JButton("Добавить");

        addExec.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i=0;
                int totCount=0;
                try
                {
                    List<String> ll = ScriptProcessor.getSqlArray(addFile.getText());
                    String header=ll.remove(0);
                    String headerShort=ll.remove(0);
                    tileBorder.setTitle(headerShort);
                    totCount = ll.size();
                    for (String sql : ll)
                    {
                        System.out.print(":" + sql);
                        DbUtils.execSQL(getConnection(),sql,true);
                        System.out.println(" OK");
                        i++;
                    }
                    addOutText.setText("Добавлено:"+ll.size());
                    execSQL.repaint();

                } catch (Exception ex) {
                    addOutText.setText("Ошибка : Добавлено:"+i+" из "+totCount);
                    ex.printStackTrace();
                }
            }
        });


        JLabel checkFileLabel = new JLabel("Проверка кол-ва дубликатов");
        JTextField checkFile = new JTextField(25);
        checkFile.setText(DEF_T_PATHS[4][1]);

        JTextField checkOutText = new JTextField(25);
        checkOutText.setEditable(false);
        JButton checkExec = new JButton("Проверить");
        checkExec.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    List<String> ll = ScriptProcessor.getSqlArray(checkFile.getText());
                    String header=ll.remove(0);
                    String headerShort=ll.remove(0);
                    tileBorder.setTitle(headerShort);
                    String sql=ll.get(0);
                    {
                            System.out.print(":" + sql);
                            Object[][] res=DbUtils.execSQL(getConnection(),sql,new LinkedList<>(),true);
                            System.out.println(" OK");


                            StringBuilder sb = new StringBuilder();
                            if (res.length==0)
                                sb.append("Нет дубликатов");
                            if (res.length>0)
                                for (Object[] re : res)
                                {
                                    sb.append(re[0]).append(" : ").append(re[1]).append(" | ");
                                }
                            checkOutText.setText(sb.toString());
                    }
                    execSQL.repaint();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        JLabel delFileLabel = new JLabel("Удаление");

        JComboBox delFile = new JComboBox<String>();
        delFile.addItem(DEF_T_PATHS[4][2]);
        delFile.addItem(DEF_T_PATHS[4][3]);
        delFile.setEditable(true);
        delFile.setSelectedIndex(0);

//        JTextField delFile = new JTextField(25);
//        delFile.setText(DEF_T_PATHS[4][2]);

        JTextField delOutText = new JTextField(25);
        delOutText.setEditable(false);

        JButton delExec = new JButton("Удалить");
        delExec.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    List<String> ll = ScriptProcessor.getSqlArray(delFile.getSelectedItem().toString());
                    String header=ll.remove(0);
                    String headerShort=ll.remove(0);
                    tileBorder.setTitle(headerShort);
                    String sql=ll.get(0);
                    {
//                        Map<String,String> mp = new HashMap<>();
//                        String s = paramTemplText.getSelectedItem().toString();
//                        mp.put("SUBPRODUCT",DbUtils.wrapQT(s));
//                        sql=DbUtils.replacePlaceHolders(sql,mp);
                        System.out.print(":" + sql);
                        DbUtils.execSQL(getConnection(),sql,true);
                        delOutText.setText("OK");
                        System.out.println(" OK");
                    }
                    execSQL.repaint();

                } catch (Exception ex) {
                    delOutText.setText(ex.toString());
                    ex.printStackTrace();
                }
            }
        });

        setYLayout(execSQL,tileBorder);
        addCompsRows(new Component[]{addFileLabel,addFile},execSQL);
        addCompsRows(new Component[]{addExec,addOutText},execSQL);
        addCompsRows(new Component[]{checkFileLabel,checkFile},execSQL);
        addCompsRows(new Component[]{checkExec,checkOutText},execSQL);

        addCompsRows(new Component[]{delFileLabel,delFile},execSQL);
        addCompsRows(new Component[]{delExec,delOutText},execSQL);

        return  execSQL;
    }


    private JPanel addDbCreateControls()
    {
        int cntOrder = DEF_ORDER_CNTER;

        JLabel scriptTempLabel = new JLabel("Шаблон");
        JTextField scriptTemplText = new JTextField(25);
        scriptTemplText.setText(DEF_TEMPL_PATH);

        JLabel cntLabel = new JLabel("Вставить кол-во заявок");
        JTextField cntOrders = new JTextField(3);
        JTextField firstOrderN = new JTextField(25);
        firstOrderN.setEditable(false);
        firstOrderN.setText(Long.toString(OrderGenerator.SDEF_TART_ORDER_NUM));
        cntOrders.setText(Integer.toString(cntOrder));
        JButton scriptTemplButton = new JButton("Сгенерировать");

        JLabel scriptLabel = new JLabel("Скрипт");
        JTextField scriptText = new JTextField(25);
        scriptText.setText(DEF_CDB_PATH);
        JButton scriptButton = new JButton("Создать");

        JPanel execSQL =new JPanel();
        execSQL.setBorder(BorderFactory.createTitledBorder("Создание БД"));

        JTextField outText= new JTextField();
        outText.setEditable(false);


        setYLayout(execSQL,null);

        addCompsRows(new Component[]{cntLabel,cntOrders, firstOrderN},execSQL);
        addCompsRows(new Component[]{scriptTempLabel, scriptTemplText,scriptTemplButton},execSQL);
        addCompsRows(new Component[]{scriptLabel, scriptText, scriptButton},execSQL);
        addCompsRows(new Component[]{ outText},execSQL);

        scriptTemplButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    String stringPath = scriptTemplText.getText();
                    java.util.List<String> ll = ScriptProcessor.getSqlArray(stringPath);
                    String scntOrder = cntOrders.getText();
                    int cntOrder = DEF_ORDER_CNTER;
                    try {
                        cntOrder = Integer.parseInt(scntOrder);
                    } catch (NumberFormatException ex)
                    {
                        cntOrders.setText(Integer.toString(cntOrder));
                        ex.printStackTrace();

                    }

                    List<String> ll1 = new OrderGenerator().generate(cntOrder);
                    PrintStream ps = new PrintStream(new FileOutputStream(scriptText.getText()));
                    for (String s : ll) {
                        ps.println(s+";");
                    }
                    ps.println();
                    for (String s : ll1) {
                        ps.println(s);
                    }
                    ps.flush();
                    ps.close();
                    outText.setText("Файл наполнения БД " + scriptText.getText()+ " создан ");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    outText.setText(ex.toString());
                }

            }
        });

        scriptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try
                {
                    String stringPath = scriptText.getText();
                    java.util.List<String> ll = ScriptProcessor.getSqlArray(stringPath);

                    String header = ll.remove(0);
                    int cntData = 0;
                    for (String sql : ll)
                    {
                        try {
                            System.out.print(":" + sql);
                            DbUtils.execSQL(getConnection(),sql,true);
                            System.out.println(" OK");
                            cntData++;
                        }
                        catch (Exception ex)
                        {
                            System.out.println(" FAIL: "+ex.getMessage());
                            ex.printStackTrace();
                        }
                        finally {
                            conn.commit();
                        }
                    }
                    outText.setText(" БД из " + scriptText.getText()+ " наполнена,успешно:" + cntData+" из"+ll.size());
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        });
        return execSQL;
    }

    private Connection getConnection() throws Exception {
        if (conn==null)
        {
            System.out.println();
            loadDriver();
            setConnection();
        }
        return conn;
    }

    private void setConnection() throws ClassNotFoundException, SQLException {
        String url = urlField.getText();
        if (url==null) url = DEF_URL;
        conn=DbUtils.getConnection(url);
        System.out.println("Set Connection OK");
    }

    private void loadDriver() throws Exception {
        String driver = driverField.getText();
        if (driver==null) driver=DEF_DRIVER;
        DbUtils.loadDriver(driver);
        System.out.println("Set Driver OK");

    }

    private JPanel addDbConnectControls()
    {
        JLabel driverL =new JLabel("Драйвер");
        JLabel urlConnectionL =new JLabel("Url");
        driverField =new JTextField(25);
        driverField.setText(DEF_DRIVER);

        JTextField outText= new JTextField();
        outText.setEditable(false);

        JButton loadDriver = new JButton("Загрузить");
        loadDriver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {

                try {
                    closeConnection();
                    loadDriver();
                    outText.setText("Загрузка драйвера ОК");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    outText.setText(ex.toString());
                }
            }
        });
        urlField =new JTextField(25);
        JButton connectToDb = new JButton("Соединиться");
        connectToDb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    closeConnection();
                    loadDriver();
                    setConnection();
                    outText.setText("Соединение ОК");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    outText.setText(ex.toString());
                }

            }
        });
        urlField.setText(DEF_URL);

        JPanel execSQL =new JPanel();
        execSQL.setBorder(BorderFactory.createTitledBorder("Установка БД"));
        setYLayout(execSQL,null);
        addCompsRows(new Component[]{driverL, driverField, loadDriver},execSQL);
        addCompsRows(new Component[]{urlConnectionL, urlField, connectToDb},execSQL);
        addCompsRows(new Component[]{outText},execSQL);

        return execSQL;
    }

    private JPanel addCompsRows(
            Component[] components,JPanel panelToAdd
    )
    {
        JPanel groupLayout = getRowLayOut(components.length);
        for (Component component : components) {
            groupLayout.add(component);
        }
        if (panelToAdd!=null)
            panelToAdd.add(groupLayout);
        return groupLayout;
    }

    private JPanel getRowLayOut(int rows) {
        JPanel groupLayout = new JPanel();
        groupLayout.setLayout(new GridLayout(1, rows, 2, 2));
        groupLayout.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(0, true), 1, false)));
        return groupLayout;
    }

    private void closeConnection() {
        try {
            if (conn!=null)
                conn.close();
        } catch (SQLException ex) {
        }
    }

    public static void main(String[] arg) {
        new TestVM().createComponents();
    }
}
