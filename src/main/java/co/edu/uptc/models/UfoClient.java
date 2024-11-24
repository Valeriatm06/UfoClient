package co.edu.uptc.models;

import javax.swing.*;
import co.edu.uptc.interfaces.UfoInterface;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class UfoClient implements UfoInterface.Model {

    private Socket client;
    private String userName;
    private UfoInterface.Presenter presenter;
    private ObjectOutputStream writer;
    private ObjectInputStream reader;

    @Override
    public void setPresenter(UfoInterface.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void startConnection(String ip, int port, String userName) throws UnknownHostException, IOException{
        this.userName = userName;
        client = new Socket(ip, port);
    } 
}
