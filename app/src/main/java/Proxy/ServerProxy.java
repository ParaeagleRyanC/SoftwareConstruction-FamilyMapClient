package Proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.*;

import Request.LoginRequest;
import Request.RegisterRequest;
import Results.ClearResult;
import Results.EventAllFromAllMemberResult;
import Results.LoginResult;
import Results.PersonAllFamilyMemberResult;
import Results.RegisterResult;

public class ServerProxy {

    public LoginResult login(LoginRequest request, String serverHost, String serverPort) {

        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/login");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");

            // Indicate that this request will contain an HTTP request body
            http.setDoOutput(true);

            // Specify that we would like to receive the server's response in JSON
            //http.addRequestProperty("Accept", "application/json");

            Gson gson = new Gson();
            String reqData = gson.toJson(request);
            OutputStream reqBody = http.getOutputStream();
            writeString(reqData, reqBody);
            reqBody.close();

            // Connect to the server and send the HTTP request
            http.connect();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                respBody.close();
                return gson.fromJson(respData, LoginResult.class);
            }
            else {
                InputStream respBody = http.getErrorStream();
                String respData = readString(respBody);
                respBody.close();
                return gson.fromJson(respData, LoginResult.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new LoginResult("Login failed!", false, null, null, null);
        }
    }

    public RegisterResult register(RegisterRequest request, String serverHost, String serverPort) {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/register");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.addRequestProperty("Accept", "application/json");

            Gson gson = new Gson();
            String reqData = gson.toJson(request);
            OutputStream reqBody = http.getOutputStream();
            writeString(reqData, reqBody);
            reqBody.close();

            http.connect();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                respBody.close();
                return gson.fromJson(respData, RegisterResult.class);
            }
            else {
                InputStream respBody = http.getErrorStream();
                String respData = readString(respBody);
                respBody.close();
                return gson.fromJson(respData, RegisterResult.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new RegisterResult("Register failed!", false, null, null, null);
        }
    }

    public PersonAllFamilyMemberResult getPeople(String serverHost, String serverPort, String authToken) {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/person");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(false);
            http.addRequestProperty("Authorization", authToken);
            http.addRequestProperty("Accept", "application/json");
            http.connect();

            Gson gson = new Gson();
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                respBody.close();
                return gson.fromJson(respData, PersonAllFamilyMemberResult.class);
            }
            else {
                InputStream respBody = http.getErrorStream();
                String respData = readString(respBody);
                respBody.close();
                return gson.fromJson(respData, PersonAllFamilyMemberResult.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new PersonAllFamilyMemberResult("Failed to retrieve all family members!", false, null);
        }
    }

    public EventAllFromAllMemberResult getEvents(String serverHost, String serverPort, String authToken) {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/event");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(false);
            http.addRequestProperty("Authorization", authToken);
            http.addRequestProperty("Accept", "application/json");
            http.connect();

            Gson gson = new Gson();
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                respBody.close();
                return gson.fromJson(respData, EventAllFromAllMemberResult.class);
            }
            else {
                InputStream respBody = http.getErrorStream();
                String respData = readString(respBody);
                respBody.close();
                return gson.fromJson(respData, EventAllFromAllMemberResult.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new EventAllFromAllMemberResult("Failed to retrieve all event data!", false, null);
        }
    }


    private static String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

    private static void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }

    public ClearResult clearDatabase(String serverHost, String serverPort) {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/clear");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.connect();

            Gson gson = new Gson();
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                respBody.close();
                return gson.fromJson(respData, ClearResult.class);
            }
            else {
                InputStream respBody = http.getErrorStream();
                String respData = readString(respBody);
                respBody.close();
                return gson.fromJson(respData, ClearResult.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ClearResult("Clear failed!", false);
        }

    }
}
