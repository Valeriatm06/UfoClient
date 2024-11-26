package co.edu.uptc.views.UfoMainFrame;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import co.edu.uptc.utilities.DesignButton;
import co.edu.uptc.views.GlobalView;
import lombok.Getter;
import lombok.Setter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Getter
@Setter
public class InitClientPanel extends JPanel {

    private JPanel backgroundPanel;
    private JPanel inputPanel;
    private JLabel ipLabel;
    private JLabel portLabel;
    private JLabel usernameLabel;
    private JTextField ipTextField;
    private JTextField portTextField;
    private JTextField usernameTextField;
    private UfoMainView ufoMainView;
    private DesignButton okButton;
    private boolean isCorrect;
    private boolean isReady;
    private JLabel titleLabel;

    public InitClientPanel(UfoMainView owner) {
        this.ufoMainView = owner;
        isReady = true;
        setLayout(new BorderLayout());
        initBackgroundPanel();
        initTitleLabel();
        initInputPanel();
        initOkButton();
        setPreferredSize(new Dimension(500, 300));
        setOpaque(false);
        add(backgroundPanel, BorderLayout.CENTER);
    }

    private void initBackgroundPanel() {
        backgroundPanel = new JPanel(new BorderLayout());
        backgroundPanel.setBackground(GlobalView.OPTIONS_BACKGROUND);
        backgroundPanel.setBorder(new EmptyBorder(0, 10, 0, 10));
    }

    private void initTitleLabel() {
        titleLabel = new JLabel("Bienvenid@", SwingConstants.CENTER);
        titleLabel.setFont(GlobalView.TITLE_FONT);
        titleLabel.setForeground(GlobalView.TITLE_TEXT);
        titleLabel.setBorder(new EmptyBorder(10, 0, 20, 0));
        backgroundPanel.add(titleLabel, BorderLayout.NORTH);
    }

    private void initInputPanel() {
        inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        inputPanel.setOpaque(false);
        inputPanel.setBorder(new EmptyBorder(20, 50, 20, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        initInputUsernameField(gbc);
        initInputIPFields(gbc);
        initInputPortFields(gbc);

        backgroundPanel.add(inputPanel, BorderLayout.CENTER);
    }

    private JTextField createInputField(JTextField textField) {
        textField = new JTextField();
        textField.setPreferredSize(new Dimension(200, 30));
        textField.setFont(GlobalView.ALL_TEXT_FONT);
        textField.setForeground(GlobalView.TITLE_TEXT);
        textField.setBackground(GlobalView.OPTIONS_BACKGROUND);
        textField.setBorder(createUnderLineBorder());
        textField.setHorizontalAlignment(SwingConstants.CENTER);
        return textField;
    }

    private Border createUnderLineBorder() {
        return new MatteBorder(0, 0, 2, 0, GlobalView.TITLE_TEXT);
    }

    private void initInputUsernameField(GridBagConstraints gbc) {
        usernameLabel = new JLabel("Nombre de usuario: ");
        usernameLabel.setFont(GlobalView.TITLE_FONT_MEDIUM);
        usernameLabel.setForeground(GlobalView.TITLE_TEXT);
        usernameTextField = createInputField(usernameTextField);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        inputPanel.add(usernameLabel, gbc);
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        inputPanel.add(usernameTextField, gbc);
    }

    private void initInputIPFields(GridBagConstraints gbc) {
        ipLabel = new JLabel("IP: ");
        ipLabel.setFont(GlobalView.TITLE_FONT_MEDIUM);
        ipLabel.setForeground(GlobalView.TITLE_TEXT);
        ipTextField = createInputField(ipTextField);
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(ipLabel, gbc);
        gbc.gridx = 2;
        inputPanel.add(ipTextField, gbc);
    }

    private void initInputPortFields(GridBagConstraints gbc) {
        portLabel = new JLabel("Puerto: ");
        portLabel.setFont(GlobalView.TITLE_FONT_MEDIUM);
        portLabel.setForeground(GlobalView.TITLE_TEXT);
        portTextField = createInputField(portTextField);
        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(portLabel, gbc);
        gbc.gridx = 2;
        inputPanel.add(portTextField, gbc);
    }

    public void initOkButton() {
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setBorder(new EmptyBorder(10, 0, 30, 0));
        okButton = new DesignButton("Ingresar al juego", true);
        okButton.setBackground(GlobalView.DEFAULT_BTN_BACKGROUND);
        okButton.setPreferredSize(new Dimension(200, 40));
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ufoMainView.getPresenter().startConnection(ipTextField.getText(),Integer.parseInt(portTextField.getText()),usernameTextField.getText());
                    ufoMainView.switchToMainPanel();
                    ufoMainView.sendMessage();
                } catch (Exception e1) {
                    showErrorDialog("Error al conectar: Verifique los campos");
                }
            }
        });
        buttonsPanel.add(okButton);
        backgroundPanel.add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void showErrorDialog(String errorMessage) {
        JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }

}
