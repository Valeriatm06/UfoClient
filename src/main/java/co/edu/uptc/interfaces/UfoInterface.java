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

        // public void startGame(int ufoNumber, double speed, int appearance);

        // public boolean isRunning();

        // public List<Ufo> getUfosList();

        // public Ufo selectUfoAtPosition(int x, int y);

        // public void changeSelectedUfoSpeed(int delta);

        // public Ufo getSelectedUfo();

        // public void addTrajectoryPointToSelectedUfo(Point point);

        // public void startUfoMovement(Ufo ufo);

        // public boolean allUfosStopped();

    }

    public interface Presenter {
        public void setModel(Model model);

        public void setView(View view);

        public void startConnection(String ip, int port, String userName) throws UnknownHostException, IOException;

        // public int[] areaSize();

        // public int[] destinationAreaSize();

        // public int[] ufoSize();

        // public void startGame(int ufoNumber, double speed,int appearance);

        // public boolean isRunning();

        // public List<Ufo> getUfos();

        // public void updateUfos(List<Ufo> ufos);

        // public void updateScore(int crashedUfoCount);

        // public void updateArrival(int arrivedCount);

        // public void countMovingUfos(int movingCount);

        // public void updateSpeed(double newSpeed);

        // public Ufo selectUfoAtPosition(int x, int y);

        // public void changeSelectedUfoSpeed(int delta);

        // public void addTrajectoryPointToUfo(Ufo ufo, Point point);

        // public void startUfoMovement(Ufo ufo);

        // public boolean allUfosStopped();

        // public int getUfoNumber();

        public String getIP();

        public int getPort();
    }

    public interface View {
        public void setPresenter(Presenter presenter);

        public void begin();

        public String getIP();

        public int getPort();

        // public int[] areaSize();

        // public int[] destinationAreaSize();

        // public int[] ufoSize();

        // public void updateUfoDisplay(List<Ufo> ufos);

        // public void updateScoreDisplay(int crashedCount);

        // public void updateArrivalDisplay(int arrivedCount);

        // public void updateMovingCount(int crashedCount);

        // public void refresh();

        // public int getUfoNumber();

    }
}
