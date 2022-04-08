package Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.familymapclient.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Data.DataCache;
import Model.Event;
import Model.Person;

public class SearchActivity extends AppCompatActivity {

    private static final int PERSON_ITEM_VIEW_TYPE = 0;
    private static final int EVENT_ITEM_VIEW_TYPE = 1;
    DataCache dataCache = DataCache.getInstance();

    List<Person> personList;
    List<Event> eventList;

    RecyclerView recyclerView;
    EditText searchEditText;

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
        setContentView(R.layout.activity_search);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        searchEditText = findViewById(R.id.what_to_search);
        searchEditText.addTextChangedListener(textWatcher);
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
        @Override
        public void afterTextChanged(Editable editable) {
            displayItems();
        }
    };

    private void displayItems() {
        String searchText = searchEditText.getText().toString().toLowerCase();
        personList = new ArrayList<>();
        eventList = new ArrayList<>();

        ArrayList<Event> events = dataCache.getCurrentDisplayingEvents();
        Map<String, Person> personMap = dataCache.getPersonMapByPersonID();
        ArrayList<Person> persons = new ArrayList<>();
        persons.addAll(personMap.values());

        // get person items to display
        for (Person person : persons) {
            if (person.getFirstName().toLowerCase().contains(searchText)) {
                personList.add(person);
            }
            else if (person.getLastName().toLowerCase().contains(searchText)) {
                personList.add(person);
            }
        }

        // get event items to display
        for (Event event : events) {
            if (event.getCountry().toLowerCase().contains(searchText)) {
                eventList.add(event);
            }
            else if (event.getCity().toLowerCase().contains(searchText)) {
                eventList.add(event);
            }
            else if (event.getEventType().toLowerCase().contains(searchText)) {
                eventList.add(event);
            }
            else if (Integer.toString(event.getYear()).contains(searchText)) {
                eventList.add(event);
            }
        }

        SearchAdapter adapter = new SearchAdapter(personList, eventList);
        recyclerView.setAdapter(adapter);
    }


    private class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {
        private final List<Person> personList;
        private final List<Event> eventList;

        SearchAdapter(List<Person> personList, List<Event> eventList) {
            this.personList = personList;
            this.eventList = eventList;
        }

        @Override
        public int getItemViewType(int position) {
            return position < personList.size() ? PERSON_ITEM_VIEW_TYPE : EVENT_ITEM_VIEW_TYPE;
        }

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;

            if(viewType == PERSON_ITEM_VIEW_TYPE) {
                view = getLayoutInflater().inflate(R.layout.person_item, parent, false);
            } else {
                view = getLayoutInflater().inflate(R.layout.event_item, parent, false);
            }

            return new SearchViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
            if(position < personList.size()) {
                holder.bind(personList.get(position));
            } else {
                holder.bind(eventList.get(position - personList.size()));
            }
        }

        @Override
        public int getItemCount() { return personList.size() + eventList.size(); }
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView personName;
        private final TextView personRelation;
        private final TextView eventItemInfo;
        private final TextView eventNameInfo;
        private final ImageView genderIcon;

        private final int viewType;
        private Person person;
        private Event event;

        SearchViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;

            itemView.setOnClickListener(this);

            if(viewType == PERSON_ITEM_VIEW_TYPE) {
                personName = itemView.findViewById(R.id.personList_name);
                personRelation = itemView.findViewById(R.id.personList_relation);
                genderIcon = itemView.findViewById(R.id.gender_icon);
                eventItemInfo = null;
                eventNameInfo = null;
            } else {
                eventItemInfo = itemView.findViewById(R.id.eventItem_info);
                eventNameInfo = itemView.findViewById(R.id.eventPerson_info);
                genderIcon = null;
                personName = null;
                personRelation = null;
            }
        }

        private void bind(Person person) {
            this.person = person;
            String name = person.getFirstName() + " " + person.getLastName();
            personName.setText(name);
            personRelation.setText("");
            if (person.getGender().equalsIgnoreCase("m")) {
                genderIcon.setImageResource(R.drawable.ic_male_person);
            }
            else {
                genderIcon.setImageResource(R.drawable.ic_female_person);
            }
        }

        private void bind(Event event) {
            this.event = event;
            String info = event.getEventType().toUpperCase() + ": " +
                            event.getCity() + ", " + event.getCountry() + " (" +
                            event.getYear() + ")";
            eventItemInfo.setText(info);
            String name = dataCache.getPersonMapByPersonID().get(event.getPersonID()).getFirstName()
                            + " " + dataCache.getPersonMapByPersonID().get(event.getPersonID()).getLastName();
            eventNameInfo.setText(name);
        }

        @Override
        public void onClick(View view) {
            if(viewType == PERSON_ITEM_VIEW_TYPE) {
                Intent intent = new Intent(SearchActivity.this, PersonActivity.class);
                DataCache dataCache = DataCache.getInstance();
                dataCache.setSelectedPersonID(person.getPersonID());
                startActivity(intent);

            } else {
                Intent intent = new Intent(SearchActivity.this, EventActivity.class);
                DataCache dataCache = DataCache.getInstance();
                dataCache.setSelectedEventID(event.getEventID());
                startActivity(intent);
            }
        }
    }
}