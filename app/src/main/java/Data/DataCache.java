package Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Model.Event;
import Model.Person;
import Proxy.ServerProxy;
import Results.EventAllFromAllMemberResult;
import Results.PersonAllFamilyMemberResult;

// Singleton Class to allow only one instance of the class
public class DataCache {

    // make DataCache a Singleton
    private DataCache() {}
    private static DataCache instance;
    public static DataCache getInstance() {
        if (instance == null) { instance = new DataCache(); }
        return instance;
    }

    // data fields
    private String currUserPersonID;
    private String currUsername;
    private String authToken;
    private String serverHost;
    private String serverPort;

    private Map<String, Person> personMapByPersonID;
    private Map<String, Event> eventMapByEventID;
    private Map<String, List<Event>> eventsListMapByPersonID;
    private Set<String> paternalAncestorsInPersonID = new HashSet<>();
    private Set<String> maternalAncestorsInPersonID = new HashSet<>();
    private Set<String> malePersonsInPersonID;
    private Set<String> femalePersonsInPersonID;
    private List<String> listOfEventTypes;
    private ArrayList<Event> currentDisplayingEvents;

    private String selectedPersonID;
    private String selectedEventID;
    private boolean isEventActivity;

    // setting switches
    private boolean showLifeStory;
    private boolean showFamilyTree;
    private boolean showSpouse;
    private boolean filterByMotherSide;
    private boolean filterByFatherSide;
    private boolean filterByFemaleEvents;
    private boolean filterByMaleEvents;

    // getters and setters
    public String getCurrUserPersonID() { return currUserPersonID; }
    public void setCurrUserPersonID(String currUserPersonID) { this.currUserPersonID = currUserPersonID; }
    public String getCurrUsername() { return currUsername; }
    public void setCurrUsername(String currUsername) { this.currUsername = currUsername; }
    public String getSelectedPersonID() { return selectedPersonID; }
    public void setSelectedPersonID(String selectedPersonID) { this.selectedPersonID = selectedPersonID; }
    public String getSelectedEventID() { return selectedEventID; }
    public void setSelectedEventID(String selectedEventID) { this.selectedEventID = selectedEventID; }
    public boolean isEventActivity() { return isEventActivity; }
    public void setEventActivity(boolean eventActivity) { isEventActivity = eventActivity; }

    public String getAuthToken() { return authToken; }
    public void setAuthToken(String authToken) { this.authToken = authToken; }
    public String getServerHost() { return serverHost; }
    public void setServerHost(String serverHost) { this.serverHost = serverHost; }
    public String getServerPort() { return serverPort; }
    public void setServerPort(String serverPort) { this.serverPort = serverPort; }

    public Map<String, Person> getPersonMapByPersonID() { return personMapByPersonID; }
    public void setPersonMapByPersonID(Map<String, Person> personMapByPersonID) { this.personMapByPersonID = personMapByPersonID; }
    public Map<String, Event> getEventMapByEventID() { return eventMapByEventID; }
    public void setEventMapByEventID(Map<String, Event> eventMapByEventID) { this.eventMapByEventID = eventMapByEventID; }
    public List<String> getListOfEventTypes() { return listOfEventTypes; }
    public void setListOfEventTypes(List<String> listOfEventTypes) { this.listOfEventTypes = listOfEventTypes; }
    public ArrayList<Event> getCurrentDisplayingEvents() { return currentDisplayingEvents; }
    public void setCurrentDisplayingEvents(ArrayList<Event> eventsToDisplayByEventID) { this.currentDisplayingEvents = eventsToDisplayByEventID; }
    public Map<String, List<Event>> getEventsListMapByPersonID() { return eventsListMapByPersonID; }
    public void setEventsListMapByPersonID(Map<String, List<Event>> eventsListMapByPersonID) { this.eventsListMapByPersonID = eventsListMapByPersonID; }
    public Set<String> getPaternalAncestorsInPersonID() { return paternalAncestorsInPersonID; }
    public void setPaternalAncestorsInPersonID(Set<String> paternalAncestorsInPersonID) { this.paternalAncestorsInPersonID = paternalAncestorsInPersonID; }
    public Set<String> getMaternalAncestorsInPersonID() { return maternalAncestorsInPersonID; }
    public void setMaternalAncestorsInPersonID(Set<String> maternalAncestorsInPersonID) { this.maternalAncestorsInPersonID = maternalAncestorsInPersonID; }
    public Set<String> getMalePersonsInPersonID() { return malePersonsInPersonID; }
    public void setMalePersonsInPersonID(Set<String> malePersonsInPersonID) { this.malePersonsInPersonID = malePersonsInPersonID; }
    public Set<String> getFemalePersonsInPersonID() { return femalePersonsInPersonID; }
    public void setFemalePersonsInPersonID(Set<String> femalePersonsInPersonID) { this.femalePersonsInPersonID = femalePersonsInPersonID; }

    public boolean isShowLifeStory() { return showLifeStory; }
    public void setShowLifeStory(boolean showLifeStory) { this.showLifeStory = showLifeStory; }
    public boolean isShowFamilyTree() { return showFamilyTree; }
    public void setShowFamilyTree(boolean showFamilyTree) { this.showFamilyTree = showFamilyTree; }
    public boolean isShowSpouse() { return showSpouse; }
    public void setShowSpouse(boolean showSpouse) { this.showSpouse = showSpouse; }
    public boolean isFilterByMotherSide() { return filterByMotherSide; }
    public void setFilterByMotherSide(boolean filterByMotherSide) { this.filterByMotherSide = filterByMotherSide; }
    public boolean isFilterByFatherSide() { return filterByFatherSide; }
    public void setFilterByFatherSide(boolean filterByFatherSide) { this.filterByFatherSide = filterByFatherSide; }
    public boolean isFilterByFemaleEvents() { return filterByFemaleEvents; }
    public void setFilterByFemaleEvents(boolean filterByFemaleEvents) { this.filterByFemaleEvents = filterByFemaleEvents; }
    public boolean isFilterByMaleEvents() { return filterByMaleEvents; }
    public void setFilterByMaleEvents(boolean filterByMaleEvents) { this.filterByMaleEvents = filterByMaleEvents; }

    ////////////////////////////
    // actual methods start here

    private void defaultFilterSettings() {
        setShowLifeStory(true);
        setShowFamilyTree(true);
        setShowSpouse(true);
        setFilterByMotherSide(true);
        setFilterByFatherSide(true);
        setFilterByFemaleEvents(true);
        setFilterByMaleEvents(true);
    }

    public void storePersonsData(ServerProxy proxy) {
        PersonAllFamilyMemberResult allPeopleResult = proxy.getPeople(serverHost, serverPort, authToken);
        ArrayList<Person> listOfPeople = allPeopleResult.getData();
        Map<String, Person> personMap = new HashMap<>();
        for (int index = 0; index < listOfPeople.size(); index++) {
            String personID = listOfPeople.get(index).getPersonID();
            personMap.put(personID, listOfPeople.get(index));
        }
        setPersonMapByPersonID(personMap);
        storePaternalAncestor(personMapByPersonID.get(currUserPersonID));
        storeMaternalAncestor(personMapByPersonID.get(currUserPersonID));
        storeFemaleAndMalePersons();
    }

    public void storeEventsData(ServerProxy proxy) {
        EventAllFromAllMemberResult allEventsResult = proxy.getEvents(serverHost, serverPort, authToken);
        ArrayList<Event> listOfEvents = allEventsResult.getAllEventData();
        Map<String, Event> eventMap = new HashMap<>();
        for (int index = 0; index < listOfEvents.size(); index++) {
            String eventID = listOfEvents.get(index).getEventID();
            eventMap.put(eventID, listOfEvents.get(index));
        }
        setEventMapByEventID(eventMap);

        ArrayList<Event> currUserEvents = new ArrayList<>();
        for (Event event : eventMap.values()) {
            if (event.getAssociatedUsername().equals(currUsername)) {
                currUserEvents.add(event);
            }
        }
        setCurrentDisplayingEvents(currUserEvents);
        storeEventListMapByPersonID();
        storeListOfEventType();
        defaultFilterSettings();
        setEventActivity(false);
    }

    private void storeEventListMapByPersonID() {
        Map<String, List<Event>> eventListByPerson = new HashMap<>();
        Map<String, Event> eventMap = getEventMapByEventID();
        Set<String> personIDs = new HashSet<>();
        List<Event> eventList;

        for (Event event : eventMap.values()) {
            personIDs.add(event.getPersonID());
        }

        for (String id : personIDs) {
            eventList = new ArrayList<>();
            for (Event event : eventMap.values()) {
                if (event.getPersonID().equals(id)) {
                    eventList.add(event);
                }
            }
            eventListByPerson.put(id, eventList);
        }
        setEventsListMapByPersonID(eventListByPerson);
    }


    private void storePaternalAncestor(Person person) {
        if (!person.getPersonID().equals(currUserPersonID)) {
            for (Person p : personMapByPersonID.values()) {
                if (p.getPersonID().equals(person.getMotherID())) {
                    paternalAncestorsInPersonID.add(p.getPersonID());
                    storePaternalAncestor(p);
                }
                if (p.getPersonID().equals(person.getFatherID())) {
                    paternalAncestorsInPersonID.add(p.getPersonID());
                    storePaternalAncestor(p);
                }
            }
        }
        else {
            for (Person p : personMapByPersonID.values()) {
                if (p.getPersonID().equals(person.getFatherID())) {
                    paternalAncestorsInPersonID.add(p.getPersonID());
                    paternalAncestorsInPersonID.add(currUserPersonID);
                    storePaternalAncestor(p);
                }
            }
        }
    }


    private void storeMaternalAncestor(Person person) {
        if (!person.getPersonID().equals(currUserPersonID)) {
            for (Person p : personMapByPersonID.values()) {
                if (p.getPersonID().equals(person.getMotherID())) {
                    maternalAncestorsInPersonID.add(p.getPersonID());
                    storeMaternalAncestor(p);
                }
                if (p.getPersonID().equals(person.getFatherID())) {
                    maternalAncestorsInPersonID.add(p.getPersonID());
                    storeMaternalAncestor(p);
                }
            }
        }
        else {
            for (Person p : personMapByPersonID.values()) {
                if (p.getPersonID().equals(person.getMotherID())) {
                    maternalAncestorsInPersonID.add(p.getPersonID());
                    maternalAncestorsInPersonID.add(currUserPersonID);
                    storeMaternalAncestor(p);
                }
            }
        }
    }

    private void storeFemaleAndMalePersons() {
        Set<String> femaleTempSet = new HashSet<>();
        Set<String> maleTempSet = new HashSet<>();
        for (Person person : personMapByPersonID.values()) {
            if (person.getGender().equalsIgnoreCase("f")) {
                femaleTempSet.add(person.getPersonID());
            }
            else {
                maleTempSet.add(person.getPersonID());
            }
        }
        setFemalePersonsInPersonID(femaleTempSet);
        setMalePersonsInPersonID(maleTempSet);
    }

    private void storeListOfEventType() {
        Set<String> eventTypes = new HashSet<>();
        for (Event event : eventMapByEventID.values()) {
            eventTypes.add(event.getEventType().toLowerCase());
        }
        List<String> eventTypeList = new ArrayList<>(eventTypes);
        setListOfEventTypes(eventTypeList);
    }
}
