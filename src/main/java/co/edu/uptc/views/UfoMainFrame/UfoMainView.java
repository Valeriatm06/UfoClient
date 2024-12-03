package co.edu.uptc.views.UfoMainFrame;

import javax.swing.JFrame;
import java.awt.CardLayout;
import java.util.List;

import co.edu.uptc.interfaces.UfoInterface;
import co.edu.uptc.pojos.Ufo;
import co.edu.uptc.utilities.MusicPlayer;
import co.edu.uptc.utilities.PropertiesService;
import lombok.Getter;

@Getter
public class UfoMainView extends JFrame implements UfoInterface.View{

    private UfoInterface.Presenter presenter;
    private PropertiesService propertiesService;
    private MusicPlayer musicPlayer;
    private MainPanel mainPanel;
    private GamePanel gamePanel;
    private InitClientPanel initClientPanel;
    private OptionsDialog optionsDialog;
    private GameFinishedDialog gameFinishedDialog;
    private HowToPlayDialog howToPlayDialog;
    private int ufoCount; 
    private int appearanceTime;
    private int speed;
    private int ufoType;
    private int movingCount;

    public UfoMainView(){   
        propertiesService = new PropertiesService();
        musicPlayer = new MusicPlayer();
        initValues();
        optionsDialog = new OptionsDialog(this, ufoCount, appearanceTime, speed, ufoType);
        howToPlayDialog = new HowToPlayDialog(this);
        initFrame();
        initClientPanel();
        initMainPanel();
        initGamePanel();
        buttonsEvent();
    }

    @Override
    public void begin() {
        setVisible(true);
    }

    public void initValues(){
        this.ufoCount = 5;
        this.appearanceTime=1000;
        this.speed = propertiesService.getIntValue("ufoMediumSpeed");
        this.ufoType = 1;
    }

    public void initFrame(){
        setTitle("Viaje Espacial");
        setLayout(new CardLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(true);
        setLocationRelativeTo(null);
        musicPlayer.playMusic(propertiesService.getKeyValue("songPath"));
    }

    public void initClientPanel(){
        initClientPanel = new InitClientPanel(this);
        add(initClientPanel, "ClientPanel");
    }
    

    public void initMainPanel(){
        mainPanel = new MainPanel();
        add(mainPanel, "MainPanel"); 
    }
    
    public void initGamePanel(){
        gamePanel = new GamePanel(this);
        add(gamePanel, "GamePanel"); 
        gamePanel.setVisible(false); 
    }
    

    public void buttonsEvent() {
        mainPanel.getOptionsButton().addActionListener(e -> showOptionsDialog());
        mainPanel.getExitButton().addActionListener(e -> exitGame());
        mainPanel.getPlayButton().addActionListener(e -> startGameFromMainPanel());
        mainPanel.getHowToPlayButton().addActionListener(e -> showHowToPlayDialog());
    }
    
    public void exitGame() {
        System.exit(0);
    }
    
    private void startGameFromMainPanel() {
        switchToGamePanel();
        updateOptionsFromDialog();
        startGame();
    }
    
    private void switchToGamePanel() {
        CardLayout layout = (CardLayout) getContentPane().getLayout();
        layout.show(getContentPane(), "GamePanel");
    }

    private void showOptionsDialog() {
        if (optionsDialog == null) {
            optionsDialog = new OptionsDialog(this, ufoCount, appearanceTime, speed, ufoType);
        }
        optionsDialog.setVisible(true);
    }

    private void showHowToPlayDialog() {
        if (howToPlayDialog == null) {
            howToPlayDialog = new HowToPlayDialog(this);
        }
        howToPlayDialog.setVisible(true);
        
    }
    
    public void updateOptionsFromDialog() {
        ufoCount = optionsDialog.getUfoCount();
        appearanceTime = optionsDialog.getAppearanceTime();
        speed = optionsDialog.getSpeed();
        ufoType = optionsDialog.getSelectedUfoType();

        gamePanel.updateufoType(ufoType);
        gamePanel.getInfoArea().updateufoType(ufoType);
    }

    @Override
    public void sendMessage(){
        if(presenter.sendIsFirstClient()==false){
            mainPanel.getOptionsButton().setVisible(false);
        }
        presenter.sendUfoCount(ufoCount);
        presenter.sendSpeed(speed);
        presenter.sendAppearanceTime(appearanceTime);
        presenter.sendUserName(initClientPanel.getUsernameTextField().getText());
    }


    public void startGame() {
        resetCountersInView();
        gamePanel.setVisible(true);
        gamePanel.startUfoGame();
    }
    
    public void switchToMainPanel() {
        CardLayout layout = (CardLayout) getContentPane().getLayout();
        layout.show(getContentPane(), "MainPanel");
    }
    
    
    public void resetCountersInView() {
        gamePanel.getInfoArea().updatCrashedUfoCount(0);  
        gamePanel.getInfoArea().upDateArrivalUfoCount(0); 
        gamePanel.getInfoArea().upDateMovingUfoCount(0);
    }

    @Override
    public void updateUfoDisplay(List<Ufo> ufos) {
        gamePanel.updateUfos(ufos);
    }

    @Override
    public void updateScoreDisplay(int crashedCount) {
        gamePanel.getInfoArea().updatCrashedUfoCount(crashedCount);
    }

    @Override
    public void updateMovingCount(int movingCount) {
        gamePanel.getInfoArea().upDateMovingUfoCount(movingCount);
        this.movingCount = movingCount;
    }

    @Override
    public void updateArrivalDisplay(int arrivedCount) {
        gamePanel.getInfoArea().upDateArrivalUfoCount(arrivedCount);
    }


    @Override
    public void setPresenter(UfoInterface.Presenter presenter) {
       this.presenter = presenter;
    }

    @Override
    public void refresh() {
        gamePanel.getUfoAreaPanel().repaint();
    }

    @Override
    public void updateUserNameList(List<String> userNameList) {
        gamePanel.getInfoArea().updateUserList(userNameList);
    }
    
}
