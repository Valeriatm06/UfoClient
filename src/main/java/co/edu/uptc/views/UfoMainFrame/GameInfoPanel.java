package co.edu.uptc.views.UfoMainFrame;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import co.edu.uptc.utilities.PropertiesService;
import co.edu.uptc.views.GlobalView;
import lombok.Getter;

@Getter
public class GameInfoPanel extends JPanel {

    private UfoMainView ufoMainView;
    private PropertiesService propertiesService;
    private JLabel movingUfoLabel;
    private JLabel crashedUfoLabel;
    private JLabel arrivalUfoLabel;
    private JLabel ufoLabel;
    private JCheckBox showTrajectoryCheckBox;
    private int ufoType;
    private JList<String> usersList;
    private DefaultListModel<String> listModel; 

    public GameInfoPanel(UfoMainView ufoMainView) {
        this.ufoMainView = ufoMainView;
        propertiesService = new PropertiesService();
        ufoType = 1;
        setPreferredSize(new Dimension(270, 300)); 
        setBackground(GlobalView.OPTIONS_BACKGROUND);
        setLayout(new GridBagLayout());
        initComponents();
    }

    private void initComponents(){
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        initTitle(gbc);
        addHorizontalLine(gbc);
        initSelectionSection(gbc);
        addHorizontalLine(gbc);
        initTrajectoryCheckbox(gbc);
        addHorizontalLine(gbc);
        initUfoStatus(gbc);
        addHorizontalLine(gbc);
        initTitleUsers(gbc);
        initScrollPane(gbc);
    }

    private void initTitle(GridBagConstraints gbc) {
        JLabel titleLabel = new JLabel("Parametros");
        titleLabel.setFont(GlobalView.TITLE_FONT_SMALL);
        titleLabel.setForeground(GlobalView.TITLE_TEXT);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        gbc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gbc);
    }

    private void addHorizontalLine(GridBagConstraints gbc) {
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(Color.GRAY);
        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;
        add(separator, gbc);
        gbc.fill = GridBagConstraints.NONE;
    }

    private void initSelectionSection(GridBagConstraints gbc) {
        addUfoSelector(gbc);
        JLabel directionKeysLabel = createLabel("Usar las teclas:");
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(directionKeysLabel, gbc);
        addSpeedPanel(gbc, "upArrow", "+ Velocidad");
        addSpeedPanel(gbc, "downArrow", "- Velocidad");
    }
    
    
    private void addUfoSelector(GridBagConstraints gbc) {
        JLabel selectLabel = createLabel("Hacer click al OVNI:");
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(selectLabel, gbc);
        ufoLabel = new JLabel(new ImageIcon(getUfoPath(ufoType)));
        gbc.gridx = 1;
        add(ufoLabel, gbc);
    }
    
    private void addSpeedPanel(GridBagConstraints gbc, String arrowKey, String text) {
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel speedPanel = createArrowLabel(propertiesService.getKeyValue(arrowKey), text);
        add(speedPanel, gbc);
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(GlobalView.ALL_TEXT_FONT);
        label.setForeground(GlobalView.TITLE_TEXT);
        return label;
    }
    

    private JPanel createArrowLabel(String path, String text) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);
        JLabel arrowLabel = new JLabel(new ImageIcon(path));
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(GlobalView.ALL_TEXT_FONT);
        textLabel.setForeground(GlobalView.TITLE_TEXT);
        panel.add(arrowLabel);
        panel.add(textLabel);
        return panel;
    }

    private void initTrajectoryCheckbox(GridBagConstraints gbc) {
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        showTrajectoryCheckBox = new JCheckBox("Ver trayectoria");
        showTrajectoryCheckBox.setFont(GlobalView.ALL_TEXT_FONT);
        showTrajectoryCheckBox.setForeground(GlobalView.TITLE_TEXT);
        showTrajectoryCheckBox.setOpaque(false);
        gbc.gridy++;
        add(showTrajectoryCheckBox, gbc);
    }
    

    private void initUfoStatus(GridBagConstraints gbc) {
        initMovingUfoLabel(gbc);
        initCrashedUfoLabel(gbc);
        initArrivalUfoLabel(gbc);
    }

    private void initMovingUfoLabel(GridBagConstraints gbc) {
        movingUfoLabel = createStatusLabel("OVNIS en movimiento:");
        gbc.gridy++;
        add(movingUfoLabel, gbc);
    }

    private void initCrashedUfoLabel(GridBagConstraints gbc) {
        crashedUfoLabel = createStatusLabel("OVNIS estrellados:");
        gbc.gridy++;
        add(crashedUfoLabel, gbc);
    }

    private void initArrivalUfoLabel(GridBagConstraints gbc) {
        arrivalUfoLabel = createStatusLabel("OVNIS en llegada:");
        gbc.gridy++;
        add(arrivalUfoLabel, gbc);
    }

    private void initTitleUsers(GridBagConstraints gbc) {
        JLabel titleLabel = new JLabel("Usuarios");
        titleLabel.setFont(GlobalView.TITLE_FONT_SMALL);
        titleLabel.setForeground(GlobalView.TITLE_TEXT);
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        gbc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gbc);
    }

    private void initScrollPane(GridBagConstraints gbc) {
        usersList = createUserList();
        JScrollPane scrollPane = new JScrollPane(usersList);
        scrollPane.setPreferredSize(new Dimension(250, 100));
        scrollPane.setBorder(null);
        
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        add(scrollPane, gbc);
    }
    
    private JList<String> createUserList() {
        listModel = new DefaultListModel<>();
        JList<String> list = new JList<>(listModel);
        list.setFont(GlobalView.ALL_TEXT_FONT);
        list.setForeground(GlobalView.TITLE_TEXT);
        list.setBackground(GlobalView.OPTIONS_BACKGROUND);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return list;
    }
    

    public void addUser(String userName) {
        if (!listModel.contains(userName)) {
            listModel.addElement(userName);
        }
    }

    public void removeUser(String userName) {
        listModel.removeElement(userName);
    }

    public void clearUsers() {
        listModel.clear();
    }

    public void updateUserList(List<String> userList) {
        if (userList == null || userList.isEmpty()) {
            System.out.println("La lista de usuarios está vacía.");
            return;
        }
        clearUsers();
        for (String user : userList) {
            addUser(user);
        }
    }
    
    

    private JLabel createStatusLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(GlobalView.ALL_TEXT_FONT);
        label.setForeground(GlobalView.TITLE_TEXT);
        return label;
    }


    private String getUfoPath(int ufoType) {
        return propertiesService.getKeyValue("ufo" + ufoType + "Path");
    }

    public void updateufoType(int newufoType) {
        this.ufoType = newufoType;
        ufoLabel.setIcon(new ImageIcon(getUfoPath(newufoType)));
        revalidate();
        repaint();
    }

    public void upDateMovingUfoCount(int count) {
        movingUfoLabel.setText("OVNIS en movimiento: " + count);
    }

    public void updatCrashedUfoCount(int count) {
        crashedUfoLabel.setText("OVNIS estrellados: " + count);
    }

    public void upDateArrivalUfoCount(int count){
        arrivalUfoLabel.setText("OVNIS en llegada: " + count);
    }

    public boolean isTrajectoryVisible() {
        return showTrajectoryCheckBox.isSelected();
    }
}
