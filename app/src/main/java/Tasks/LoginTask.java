package Tasks;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import Data.DataCache;
import Model.Person;
import Proxy.ServerProxy;
import Request.LoginRequest;
import Results.LoginResult;

public class LoginTask implements Runnable {

    private final Handler handler;
    private final LoginRequest request;
    private final String serverHost;
    private final String serverPort;

    public LoginTask(Handler handler, LoginRequest request, String host, String port) {
        this.handler = handler;
        this.request = request;
        this.serverHost = host;
        this.serverPort = port;
    }

    @Override
    public void run() {
        ServerProxy proxy = new ServerProxy();
        LoginResult loginResult = proxy.login(request, serverHost, serverPort);

        if (loginResult.isSuccess()) {
            DataCache dataCache = DataCache.getInstance();
            dataCache.setCurrUserPersonID(loginResult.getPersonID());
            dataCache.setAuthToken(loginResult.getAuthToken());
            dataCache.setCurrUsername(loginResult.getUsername());
            dataCache.setServerHost(serverHost);
            dataCache.setServerPort(serverPort);

            // store data
            dataCache.storePersonsData(proxy);
            dataCache.storeEventsData(proxy);

            Person loginUser = dataCache.getPersonMapByPersonID().get(loginResult.getPersonID());
            sendMessage("Hi! " + loginUser.getFirstName() + " " + loginUser.getLastName());
        }

        else {
            sendMessage("Login Failed!");
        }
    }

    private void sendMessage(String msg) {
        Message message = Message.obtain();
        Bundle messageBundle = new Bundle();
        messageBundle.putString("message_key", msg);
        message.setData(messageBundle);
        handler.sendMessage(message);
    }
}
