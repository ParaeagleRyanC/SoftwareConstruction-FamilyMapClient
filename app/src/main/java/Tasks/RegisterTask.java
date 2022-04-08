package Tasks;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import Data.DataCache;
import Model.Person;
import Proxy.ServerProxy;
import Request.RegisterRequest;
import Results.RegisterResult;

public class RegisterTask implements Runnable {

    private final Handler handler;
    private final RegisterRequest request;
    private final String serverHost;
    private final String serverPort;

    public RegisterTask(Handler handler, RegisterRequest request, String host, String port) {
        this.handler = handler;
        this.request = request;
        this.serverHost = host;
        this.serverPort = port;
    }

    @Override
    public void run() {
        ServerProxy proxy = new ServerProxy();
        RegisterResult registerResult = proxy.register(request, serverHost, serverPort);

        if (registerResult.isSuccess()) {
            DataCache dataCache = DataCache.getInstance();
            dataCache.setCurrUserPersonID(registerResult.getPersonID());
            dataCache.setAuthToken(registerResult.getAuthToken());
            dataCache.setCurrUsername(registerResult.getUsername());
            dataCache.setServerHost(serverHost);
            dataCache.setServerPort(serverPort);

            // store data
            dataCache.storePersonsData(proxy);
            dataCache.storeEventsData(proxy);

            Person loginUser = dataCache.getPersonMapByPersonID().get(registerResult.getPersonID());
            sendMessage("Hi! " + loginUser.getFirstName() + " " + loginUser.getLastName());
        }
        else {
            sendMessage("Register Failed!");
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
