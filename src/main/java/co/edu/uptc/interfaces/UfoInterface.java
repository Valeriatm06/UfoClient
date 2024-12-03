package co.edu.uptc.interfaces;
import java.awt.Point;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import co.edu.uptc.pojos.Ufo;

public interface UfoInterface {
    
    public interface Model {
        public void setPresenter(Presenter presenter);

       public void startConnection(String ip, int port, String userName) throws UnknownHostException, IOException;

       public void sendUfoCount(int ufoCount);

        public void sendDefaultSpeed(int speed);

        public void sendAppearanceTime(int appearanceTime);

        public void startGame();

        public boolean isRunning();

        public List<Ufo> getUfosList();

        public void sendXPosition(int x);

        public void sendYPosition(int y);

        public Ufo selectUfoAtPosition();

        public void sendSelectedUfoSpeed(int delta);

        public void changeSelectedUfoSpeed();

        public boolean sendIsFirstClient();

        public void sendSelectedUfo(Ufo selectedUfo);

        public void startUfoMovement();

        public boolean allUfosStopped();

        public void sendSelectedUfoTrayectory(List<Point> selectedUfoTrayectory);

        public void sendUserName(String text);

    }

    public interface Presenter {
        public void setModel(Model model);

        public void setView(View view);

        public void startConnection(String ip, int port, String userName) throws UnknownHostException, IOException;

        public void sendUfoCount(int ufoCount);

        public void sendSpeed(int speed);

        public void sendAppearanceTime(int appearanceTime);

        public boolean sendIsFirstClient();

        public void startGame();

        public boolean isRunning();

        public List<Ufo> getUfos();

        public void updateUfos(List<Ufo> ufos);

        public void updateScore(int crashedUfoCount);

        public void updateArrival(int arrivedCount);

        public void countMovingUfos(int movingCount);

        public void sendXPosition(int x);

        public void sendYPosition(int y);

        public Ufo selectUfoAtPosition();

        public void sendSelectedUfoSpeed(int delta);

        public void changeSelectedUfoSpeed();

        public void sendSelectedUfo(Ufo selectedUfo);

        public void sendSelectedUfoTrayectory(List<Point> selectedUfoTrayectory);

        public void startUfoMovement();

        public boolean allUfosStopped();

        public void sendUserName(String text);

        public void updateUserNameList(List<String> userNameList);

        public void closeEverything();
    }

    public interface View {
        public void setPresenter(Presenter presenter);

        public void begin();

        public void sendMessage();

        public void updateUfoDisplay(List<Ufo> ufos);

        public void updateScoreDisplay(int crashedCount);

        public void updateArrivalDisplay(int arrivedCount);

        public void updateMovingCount(int crashedCount);

        public void refresh();

        public void updateUserNameList(List<String> userNameList);

        public void exitGame();

    }
}
