package com.example.familymapclient;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Proxy.ServerProxy;
import Request.LoginRequest;
import Request.RegisterRequest;
import Results.ClearResult;
import Results.EventAllFromAllMemberResult;
import Results.LoginResult;
import Results.PersonAllFamilyMemberResult;
import Results.RegisterResult;

public class ServerProxyTest {

    private final String serverHost = "localhost";
    private final String serverPort = "8080";
    private final ServerProxy proxy = new ServerProxy();

    @BeforeEach
    public void clearDataBase() {
        proxy.clearDatabase(serverHost,serverPort);
    }

    @Test
    public void loginPass() {
        RegisterRequest registerRequest = new RegisterRequest("ryanLogin", "pass",
                "email", "RyanLogin", "ChiangLogin", "m");
        RegisterResult registerResult = proxy.register(registerRequest, serverHost, serverPort);
        Assertions.assertTrue(registerResult.isSuccess());
        LoginRequest request = new LoginRequest("ryanLogin", "pass");
        LoginResult result = proxy.login(request, serverHost, serverPort);
        Assertions.assertTrue(result.isSuccess());
    }

    @Test
    public void loginFail() {
        RegisterRequest registerRequest = new RegisterRequest("kateLogin", "pass",
                "email", "KateLogin", "ChangLogin", "f");
        RegisterResult registerResult = proxy.register(registerRequest, serverHost, serverPort);
        Assertions.assertTrue(registerResult.isSuccess());
        LoginRequest request = new LoginRequest("ryanLogin", "pass");
        LoginResult result = proxy.login(request, serverHost, serverPort);
        Assertions.assertFalse(result.isSuccess());
    }

    @Test
    public void registerPass() {
        RegisterRequest request = new RegisterRequest("ryan", "pass",
                                "email", "Ryan", "Chiang", "m");
        RegisterResult result = proxy.register(request, serverHost, serverPort);
        Assertions.assertTrue(result.isSuccess());
    }

    @Test
    public void registerFail() {
        RegisterRequest request = new RegisterRequest("kate", "pass",
                "email", "Kate", "Chang", "f");
        RegisterResult result = proxy.register(request, serverHost, serverPort);
        Assertions.assertTrue(result.isSuccess());
        RegisterResult failResult = proxy.register(request, serverHost, serverPort);
        Assertions.assertFalse(failResult.isSuccess());
    }

    @Test
    public void retrievePeoplePass() {
        RegisterRequest registerRequest = new RegisterRequest("ryanPeople", "pass",
                "email", "RyanPeople", "ChiangPeople", "m");
        RegisterResult registerResult = proxy.register(registerRequest, serverHost, serverPort);
        Assertions.assertTrue(registerResult.isSuccess());
        String authToken = registerResult.getAuthToken();
        PersonAllFamilyMemberResult peopleResult = proxy.getPeople(serverHost, serverPort, authToken);
        Assertions.assertTrue(peopleResult.isSuccess());
    }

    @Test
    public void retrievePeopleFail() {
        String authToken = "badToken";
        PersonAllFamilyMemberResult peopleResult = proxy.getPeople(serverHost, serverPort, authToken);
        Assertions.assertFalse(peopleResult.isSuccess());
    }

    @Test
    public void retrieveEventPass() {
        RegisterRequest registerRequest = new RegisterRequest("katePeople", "pass",
                "email", "KatePeople", "ChangPeople", "m");
        RegisterResult registerResult = proxy.register(registerRequest, serverHost, serverPort);
        Assertions.assertTrue(registerResult.isSuccess());
        String authToken = registerResult.getAuthToken();
        EventAllFromAllMemberResult eventResult = proxy.getEvents(serverHost, serverPort, authToken);
        Assertions.assertTrue(eventResult.isSuccess());
    }

    @Test
    public void retrieveEventFail() {
        String authToken = "badToken";
        EventAllFromAllMemberResult eventResult = proxy.getEvents(serverHost, serverPort, authToken);
        Assertions.assertFalse(eventResult.isSuccess());
    }

    @Test
    public void clearPass() {
        RegisterRequest registerRequest = new RegisterRequest("ryanPeople", "pass",
                "email", "RyanPeople", "ChiangPeople", "m");
        RegisterResult registerResult = proxy.register(registerRequest, serverHost, serverPort);
        Assertions.assertTrue(registerResult.isSuccess());
        ClearResult clearResult = proxy.clearDatabase(serverHost,serverPort);
        Assertions.assertTrue(clearResult.isSuccess());
    }

    @Test
    public void clearFail() {
        RegisterRequest registerRequest = new RegisterRequest("ryanPeople", "pass",
                "email", "RyanPeople", "ChiangPeople", "m");
        RegisterResult registerResult = proxy.register(registerRequest, serverHost, serverPort);
        Assertions.assertTrue(registerResult.isSuccess());
        ClearResult clearResult = proxy.clearDatabase("badHost",serverPort);
        Assertions.assertFalse(clearResult.isSuccess());
    }
}