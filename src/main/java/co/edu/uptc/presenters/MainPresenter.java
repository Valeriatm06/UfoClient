package co.edu.uptc.presenters;

import java.awt.Point;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import co.edu.uptc.interfaces.UfoInterface;
import co.edu.uptc.pojos.Ufo;
import lombok.Getter;

@Getter
public class MainPresenter implements UfoInterface.Presenter{

    private UfoInterface.Model model;
    private UfoInterface.View view;

    @Override
    public void setModel(UfoInterface.Model model) {
        this.model = model;
    }

    @Override
    public void setView(UfoInterface.View view) {
       this.view = view;
    }

    @Override
    public void startConnection(String ip, int port, String userName) throws UnknownHostException, IOException {
        model.startConnection(ip, port, userName);
    }

    @Override
    public void sendUfoCount(int ufoCount) {
        model.sendUfoCount(ufoCount);
    }

    @Override
    public void sendSpeed(int speed) {
        model.sendDefaultSpeed(speed);
    }

    @Override
    public void sendAppearanceTime(int appearanceTime) {
        model.sendAppearanceTime(appearanceTime);
    }

    @Override
    public void startGame() {
       model.startGame();
    }

    @Override
    public boolean isRunning() {
        return model.isRunning();
    }

    @Override
    public List<Ufo> getUfos() {
       return model.getUfosList();
    }

    @Override
    public void updateUfos(List<Ufo> ufos) {
        view.updateUfoDisplay(ufos);
    }

    @Override
    public void updateScore(int crashedCount) {
        view.updateScoreDisplay(crashedCount);
    }

    @Override
    public void updateArrival(int arrivedCount) {
        view.updateArrivalDisplay(arrivedCount);
    }

    @Override
    public void countMovingUfos(int movingNumber) {
       view.updateMovingCount(movingNumber);
    }

    @Override
    public void sendSelectedUfoSpeed(int delta) {
        model.sendSelectedUfoSpeed(delta);
    }

    @Override
    public void changeSelectedUfoSpeed() {
        model.changeSelectedUfoSpeed();;
    }

    @Override
    public void sendXPosition(int x) {
        model.sendXPosition(x);
    }

    @Override
    public void sendYPosition(int y) {
        model.sendYPosition(y);
    }

    @Override
    public Ufo selectUfoAtPosition() {
        return model.selectUfoAtPosition();
    }

    @Override
    public void startUfoMovement() {
        model.startUfoMovement();
    }

    @Override
    public void sendSelectedUfo(Ufo selectedUfo) {
        model.sendSelectedUfo(selectedUfo);
    }

    @Override
    public boolean allUfosStopped() {
       return model.allUfosStopped();
    }

    @Override
    public void sendSelectedUfoTrayectory(List<Point> selectedUfoTrayectory) {
        model.sendSelectedUfoTrayectory(selectedUfoTrayectory);
    }

    @Override
    public boolean sendIsFirstClient() {
        return model.sendIsFirstClient();
    }

    @Override
    public void sendUserName(String text) {
        model.sendUserName(text);
    }

    @Override
    public void updateUserNameList(List<String> userNameList) {
        view.updateUserNameList(userNameList);
    }

    @Override
    public void closeEverything() {
        view.exitGame();
    }
}
