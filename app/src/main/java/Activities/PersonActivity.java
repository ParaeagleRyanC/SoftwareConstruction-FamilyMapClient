package Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.familymapclient.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import Data.DataCache;
import Model.Event;
import Model.Person;

public class PersonActivity extends AppCompatActivity {

    List<Person> personList;
    List<Event> eventList;
    Person selectedPerson;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent= new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        ExpandableListView expandableListView = findViewById(R.id.expandableListView);
        getPersonsEventsLists();
        expandableListView.setAdapter(new ExpandableListAdapter(personList, eventList));

        TextView personFirstName = findViewById(R.id.person_firstName);
        personFirstName.setText(selectedPerson.getFirstName());

        TextView personLastName = findViewById(R.id.person_lastName);
        personLastName.setText(selectedPerson.getLastName());

        TextView personGender = findViewById(R.id.person_gender);
        if (selectedPerson.getGender().equalsIgnoreCase("f")) {
            personGender.setText("Female");
        }
        else {
            personGender.setText("Male");
        }
    }

    private void getPersonsEventsLists() {
        personList = new ArrayList<>();
        eventList = new ArrayList<>();
        DataCache dataCache = DataCache.getInstance();

        // get person
        Map<String, Person> personMap = dataCache.getPersonMapByPersonID();
        selectedPerson = personMap.get(dataCache.getSelectedPersonID());
        for (Person person : personMap.values()) {
            // get father
            if (selectedPerson.getFatherID() != null) {
                if (person.getPersonID().equals(selectedPerson.getFatherID())) {
                    personList.add(person);
                }
            }

            // get mother
            if (selectedPerson.getMotherID() != null) {
                if (person.getPersonID().equals(selectedPerson.getMotherID())) {
                    personList.add(person);
                }
            }

            // get spouse
            if (selectedPerson.getSpouseID() != null) {
                if (person.getPersonID().equals(selectedPerson.getSpouseID())) {
                    personList.add(person);
                }
            }

            // get child
            if (person.getFatherID() != null && person.getMotherID() != null) {
                if (selectedPerson.getPersonID().equals(person.getFatherID()) ||
                        selectedPerson.getPersonID().equals(person.getMotherID())) {
                    personList.add(person);
                }
            }
        }

        // get events
        List<Event> tempList;
        tempList = dataCache.getEventsListMapByPersonID().get(selectedPerson.getPersonID());
        ArrayList<Event> events = dataCache.getCurrentDisplayingEvents();
        tempList.removeIf(e -> !events.contains(e));
        Map <String, Integer> unsortedEvents = new TreeMap<>();
        for (Event event : tempList) {
            unsortedEvents.put(event.getEventID(), event.getYear());
        }
        LinkedHashMap<String, Integer> sortedEvents = new LinkedHashMap<>();
        unsortedEvents.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .forEachOrdered(x -> sortedEvents.put(x.getKey(), x.getValue()));

        for (String id : sortedEvents.keySet()) {
            for (Event event : tempList) {
                if (event.getEventID().equals(id)) {
                    eventList.add(event);
                }
            }
        }
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {

        private static final int PERSON_LIST_GROUP_POSITION = 0;
        private static final int EVENT_LIST_GROUP_POSITION = 1;

        private final List<Person> personList;
        private final List<Event> eventList;

        ExpandableListAdapter(List<Person> personList, List<Event> eventList) {
            this.personList = personList;
            this.eventList = eventList;
        }

        @Override
        public int getGroupCount() { return 2; }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch (groupPosition) {
                case PERSON_LIST_GROUP_POSITION:
                    return personList.size();
                case EVENT_LIST_GROUP_POSITION:
                    return eventList.size();
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_group, parent, false);
            }

            TextView titleView = convertView.findViewById(R.id.listTitle);

            switch (groupPosition) {
                case PERSON_LIST_GROUP_POSITION:
                    titleView.setText(R.string.family_title);
                    break;
                case EVENT_LIST_GROUP_POSITION:
                    titleView.setText(R.string.life_events_title);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView;

            switch(groupPosition) {
                case PERSON_LIST_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.person_item, parent, false);
                    initializePersonListView(itemView, childPosition);
                    break;
                case EVENT_LIST_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.event_item, parent, false);
                    initializeEventListView(itemView, childPosition);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return itemView;
        }

        private void initializePersonListView(View personItemView, final int childPosition) {
            TextView personNameView = personItemView.findViewById(R.id.personList_name);
            String name = personList.get(childPosition).getFirstName() + " " + personList.get(childPosition).getLastName();
            personNameView.setText(name);

            String relation = "";
            if (selectedPerson.getFatherID() != null) {
                if (personList.get(childPosition).getPersonID().equals(selectedPerson.getFatherID()))
                    relation = "Father";
            }
            if (selectedPerson.getMotherID() != null) {
                if (personList.get(childPosition).getPersonID().equals(selectedPerson.getMotherID()))
                    relation = "Mother";
            }
            if (selectedPerson.getSpouseID() != null) {
                if (personList.get(childPosition).getPersonID().equals(selectedPerson.getSpouseID()))
                    relation = "Spouse";
            }
            if (personList.get(childPosition).getFatherID() != null) {
                if (personList.get(childPosition).getFatherID().equals(selectedPerson.getPersonID()) ||
                        personList.get(childPosition).getMotherID().equals(selectedPerson.getPersonID())) {
                    relation = "Child";
                }
            }
            TextView personRelationView = personItemView.findViewById(R.id.personList_relation);
            personRelationView.setText(relation);

            ImageView genderIcon = personItemView.findViewById(R.id.gender_icon);
            if (personList.get(childPosition).getGender().equalsIgnoreCase("m")) {
                genderIcon.setImageResource(R.drawable.ic_male_person);
            }
            else {
                genderIcon.setImageResource(R.drawable.ic_female_person);
            }

            personItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PersonActivity.this, PersonActivity.class);
                    DataCache dataCache = DataCache.getInstance();
                    dataCache.setSelectedPersonID(personList.get(childPosition).getPersonID());
                    startActivity(intent);
                }
            });
        }

        private void initializeEventListView(View eventItemView, final int childPosition) {
            TextView eventInfoView = eventItemView.findViewById(R.id.eventItem_info);
            String info = eventList.get(childPosition).getEventType().toUpperCase() +
                        ": " + eventList.get(childPosition).getCity() + ", " +
                        eventList.get(childPosition).getCountry() + " (" +
                        eventList.get(childPosition).getYear() + ")";
            eventInfoView.setText(info);

            TextView eventPersonView = eventItemView.findViewById(R.id.eventPerson_info);
            String name = selectedPerson.getFirstName() + " " + selectedPerson.getLastName();
            eventPersonView.setText(name);

            eventItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // new Event Activity
                    Intent intent = new Intent(PersonActivity.this, EventActivity.class);
                    DataCache dataCache = DataCache.getInstance();
                    dataCache.setSelectedEventID(eventList.get(childPosition).getEventID());
                    startActivity(intent);
                }
            });
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) { return true; }

        @Override // Not used
        public Object getGroup(int groupPosition) { return null; }

        @Override // Not used
        public Object getChild(int groupPosition, int childPosition) {return null; }

        @Override
        public long getGroupId(int groupPosition) { return groupPosition; }

        @Override
        public long getChildId(int groupPosition, int childPosition) { return childPosition; }

        @Override
        public boolean hasStableIds() { return false; }
    }
}