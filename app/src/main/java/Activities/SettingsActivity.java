package Activities;

import androidx.appcompat.app.AppCompatActivity;
import com.example.familymapclient.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import Data.DataCache;
import Model.Event;

public class SettingsActivity extends AppCompatActivity {

    private Switch lifeStorySwitch;
    private Switch familyTreeSwitch;
    private Switch spouseSwitch;
    private Switch motherSideSwitch;
    private Switch fatherSideSwitch;
    private Switch femaleEventsSwitch;
    private Switch maleEventsSwitch;
    private TableLayout logout;
    DataCache dataCache;

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
        setContentView(R.layout.activity_settings);

        lifeStorySwitch = findViewById(R.id.life_storyLine_switch);
        familyTreeSwitch = findViewById(R.id.family_treeLine_switch);
        spouseSwitch = findViewById(R.id.spouseLine_switch);
        motherSideSwitch = findViewById(R.id.motherSide_switch);
        fatherSideSwitch = findViewById(R.id.fatherSide_switch);
        femaleEventsSwitch = findViewById(R.id.femaleEvents_switch);
        maleEventsSwitch = findViewById(R.id.maleEvents_switch);
        logout = findViewById(R.id.logout);

        dataCache = DataCache.getInstance();
        showCurrentSettings();

        lifeStorySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                dataCache.setShowLifeStory(!dataCache.isShowLifeStory());
                if (dataCache.isShowLifeStory()) {
                    Toast.makeText(getBaseContext(),"Lines ON",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getBaseContext(),"Lines OFF",Toast.LENGTH_SHORT).show();
                }
            }
        });

        familyTreeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                dataCache.setShowFamilyTree(!dataCache.isShowFamilyTree());
                if (dataCache.isShowFamilyTree()) {
                    Toast.makeText(getBaseContext(),"Lines ON",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getBaseContext(),"Lines OFF",Toast.LENGTH_SHORT).show();
                }
            }
        });

        spouseSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                dataCache.setShowSpouse(!dataCache.isShowSpouse());
                if (dataCache.isShowSpouse()) {
                    Toast.makeText(getBaseContext(),"Lines ON",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getBaseContext(),"Lines OFF",Toast.LENGTH_SHORT).show();
                }
            }
        });

        motherSideSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                dataCache.setFilterByMotherSide(!dataCache.isFilterByMotherSide());
                if (dataCache.isFilterByMotherSide()) {
                    Toast.makeText(getBaseContext(),"Filter ON",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getBaseContext(),"Filter OFF",Toast.LENGTH_SHORT).show();
                }
                updateCurrentDisplayingEvents();
            }
        });

        fatherSideSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                dataCache.setFilterByFatherSide(!dataCache.isFilterByFatherSide());
                if (dataCache.isFilterByFatherSide()) {
                    Toast.makeText(getBaseContext(),"Filter ON",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getBaseContext(),"Filter OFF",Toast.LENGTH_SHORT).show();
                }
                updateCurrentDisplayingEvents();
            }
        });

        femaleEventsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                dataCache.setFilterByFemaleEvents(!dataCache.isFilterByFemaleEvents());
                if (dataCache.isFilterByFemaleEvents()) {
                    Toast.makeText(getBaseContext(),"Filter ON",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getBaseContext(),"Filter OFF",Toast.LENGTH_SHORT).show();
                }
                updateCurrentDisplayingEvents();
            }
        });

        maleEventsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                dataCache.setFilterByMaleEvents(!dataCache.isFilterByMaleEvents());
                if (dataCache.isFilterByMaleEvents()) {
                    Toast.makeText(getBaseContext(),"Filter ON",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getBaseContext(),"Filter OFF",Toast.LENGTH_SHORT).show();
                }
                updateCurrentDisplayingEvents();
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataCache.setAuthToken(null);
                Toast.makeText(getBaseContext(),"Logging OUT",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void showCurrentSettings() {
        lifeStorySwitch.setChecked(dataCache.isShowLifeStory());
        familyTreeSwitch.setChecked(dataCache.isShowFamilyTree());
        spouseSwitch.setChecked(dataCache.isShowSpouse());
        motherSideSwitch.setChecked(dataCache.isFilterByMotherSide());
        fatherSideSwitch.setChecked(dataCache.isFilterByFatherSide());
        femaleEventsSwitch.setChecked(dataCache.isFilterByFemaleEvents());
        maleEventsSwitch.setChecked(dataCache.isFilterByMaleEvents());
    }

    private void updateCurrentDisplayingEvents() {
        Map<String, Event> allEvents = dataCache.getEventMapByEventID();
        Set<String> malePersons = dataCache.getMalePersonsInPersonID();
        Set<String> femalePersons = dataCache.getFemalePersonsInPersonID();
        Set<String> paternalAncestors = dataCache.getPaternalAncestorsInPersonID();
        Set<String> maternalAncestors = dataCache.getMaternalAncestorsInPersonID();
        ArrayList<Event> updatedEventsToDisplay = new ArrayList<>(allEvents.values());

        if (!dataCache.isFilterByMaleEvents()) {
            for (String id : malePersons) {
                for (Event event : allEvents.values()) {
                    if (event.getPersonID().equals(id)) {
                        updatedEventsToDisplay.remove(event);
                    }
                }
            }
        }

        if (!dataCache.isFilterByFemaleEvents()) {
            for (String id : femalePersons) {
                for (Event event : allEvents.values()) {
                    if (event.getPersonID().equals(id)) {
                        updatedEventsToDisplay.remove(event);
                    }
                }
            }
        }

        if (!dataCache.isFilterByFatherSide()) {
            for (String id : paternalAncestors) {
                for (Event event : allEvents.values()) {
                    if (event.getPersonID().equals(id) &&
                            !event.getPersonID().equals(dataCache.getCurrUserPersonID())) {
                        updatedEventsToDisplay.remove(event);
                    }
                }
            }
        }

        if (!dataCache.isFilterByMotherSide()) {
            for (String id : maternalAncestors) {
                for (Event event : allEvents.values()) {
                    if (event.getPersonID().equals(id) &&
                            !event.getPersonID().equals(dataCache.getCurrUserPersonID())) {
                        updatedEventsToDisplay.remove(event);
                    }
                }
            }
        }

        dataCache.setCurrentDisplayingEvents(updatedEventsToDisplay);
    }
}