package Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.familymapclient.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import Data.DataCache;
import Model.Event;
import Model.Person;

public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {

    private GoogleMap map;
    private ArrayList<Event> eventsToDisplay;
    private ArrayList<Polyline> listOfLines;
    private TextView eventInfo;
    private ImageView eventIcon;
    private final DataCache dataCache = DataCache.getInstance();
    private final int spouseLineColor = Color.RED;
    private final int lifeStoryLineColor = Color.GREEN;
    private final int familyTreeLineFatherColor = Color.WHITE;
    private final int familyTreeLineMotherColor = Color.GRAY;

    @Override
    public void onResume() {
        super.onResume();
        if (map != null) {
            map.clear();
            displayMarkers(map);
            displayLines();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        eventInfo = view.findViewById(R.id.event_info);
        eventIcon = view.findViewById(R.id.info_icon);
        listOfLines = new ArrayList<>();

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapLoadedCallback(this);
        displayMarkers(googleMap);
        if (dataCache.isEventActivity()) {
            centerMarker();
            removeLines();
            listOfLines.clear();
            displayLines();
        }
    }

    private void centerMarker() {
        Event event = dataCache.getEventMapByEventID().get(dataCache.getSelectedEventID());
        map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(event.getLatitude(), event.getLongitude())));
        displayEventInfo(event);
    }

    @Override // not needed
    public void onMapLoaded() {}

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        if (!dataCache.isEventActivity()) {
            super.onCreateOptionsMenu(menu, inflater);
            inflater.inflate(R.menu.menu, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_searchID:
                startActivity(new Intent(getActivity(), SearchActivity.class));
                return true;
            case R.id.menu_settingsID:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void displayMarkers(GoogleMap map) {
        eventsToDisplay = dataCache.getCurrentDisplayingEvents();

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                Event event = (Event) marker.getTag();
                dataCache.setSelectedEventID(event.getEventID());
                centerMarker();
                removeLines();
                listOfLines.clear();
                displayLines();
                return true;
            }
        });

        for (Event event : eventsToDisplay) {
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(event.getLatitude(), event.getLongitude()))
                    .icon(BitmapDescriptorFactory.defaultMarker(colorPicker(event.getEventType()))));
            marker.setTag(event);
        }
    }

    private float colorPicker(String eventType) {
        List<String> types = dataCache.getListOfEventTypes();
        int index = 0;
        for (int i = 0; i < types.size(); i++) {
            if (types.get(i).equals(eventType.toLowerCase())) {
                index = i;
                break;
            }
        }
        int color = index % 10;
        switch (color) {
            case 0 : return BitmapDescriptorFactory.HUE_RED;
            case 1 : return BitmapDescriptorFactory.HUE_BLUE;
            case 2 : return BitmapDescriptorFactory.HUE_GREEN;
            case 3 : return BitmapDescriptorFactory.HUE_CYAN;
            case 4 : return BitmapDescriptorFactory.HUE_MAGENTA;
            case 5 : return BitmapDescriptorFactory.HUE_ORANGE;
            case 6 : return BitmapDescriptorFactory.HUE_AZURE;
            case 7 : return BitmapDescriptorFactory.HUE_ROSE;
            case 8 : return BitmapDescriptorFactory.HUE_VIOLET;
            case 9 : return BitmapDescriptorFactory.HUE_YELLOW;
            default : return BitmapDescriptorFactory.HUE_YELLOW;
        }
    }

    private void displayEventInfo(Event event) {
        Person person = dataCache.getPersonMapByPersonID().get(event.getPersonID());
        String info = person.getFirstName() + " " + person.getLastName()
                        + "\n" + event.getEventType().toUpperCase() + ": " + event.getCity()
                        + ", " + event.getCountry() + " (" + event.getYear() + ")";

        if (person.getGender().equalsIgnoreCase("m")) {
            eventIcon.setImageResource(R.drawable.ic_male_person);
        }
        else {
            eventIcon.setImageResource(R.drawable.ic_female_person);
        }
        eventInfo.setText(info);
        eventInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PersonActivity.class);
                dataCache.setSelectedPersonID(person.getPersonID());
                startActivity(intent);
            }
        });
    }

    private void removeLines() {
        if (listOfLines.size() == 0) { return; }
        for (Polyline line : listOfLines) {
            line.remove();
        }
    }

    private void drawLine(Event startEvent, Event endEvent, int lineColor, int lineWidth) {
        LatLng startLocation = new LatLng(startEvent.getLatitude(), startEvent.getLongitude());
        LatLng endLocation = new LatLng(endEvent.getLatitude(), endEvent.getLongitude());

        PolylineOptions options = new PolylineOptions().add(startLocation).add(endLocation).
                                    color(lineColor).width(lineWidth);

        Polyline line = map.addPolyline(options);
        listOfLines.add(line);
    }

    private void displayLines() {
        drawSpouseLine();
        drawLifeStoryLines();
        drawFamilyTreeLines();
    }

    private void drawSpouseLine() {
        if (!dataCache.isShowSpouse()) { return; }

        Event selectedEvent = dataCache.getEventMapByEventID().get(dataCache.getSelectedEventID());
        Person selectedPerson = dataCache.getPersonMapByPersonID().get(selectedEvent.getPersonID());
        if (selectedPerson.getSpouseID() == null) { return; }

        Person spouse = dataCache.getPersonMapByPersonID().get(selectedPerson.getSpouseID());
        List<Event> spouseTempEvents = dataCache.getEventsListMapByPersonID().get(spouse.getPersonID());
        List<Event> spouseEventList = sortEventsByYear(spouseTempEvents);
        if (spouseEventList.isEmpty()) { return; }

        Event spouseEvent = spouseEventList.get(0);
        drawLine(selectedEvent, spouseEvent, spouseLineColor, 20);
    }

    private void drawLifeStoryLines() {
        if (!dataCache.isShowLifeStory()) { return; }

        Event selectedEvent = dataCache.getEventMapByEventID().get(dataCache.getSelectedEventID());
        List<Event> eventTempList = dataCache.getEventsListMapByPersonID().get(selectedEvent.getPersonID());
        List<Event> lifeStoryEventList = sortEventsByYear(eventTempList);
        if (lifeStoryEventList.isEmpty()) { return; }

        for (int index = 0; index < lifeStoryEventList.size() - 1; index++) {
            Event startEvent = lifeStoryEventList.get(index);
            Event endEvent = lifeStoryEventList.get(index + 1);
            drawLine(startEvent, endEvent, lifeStoryLineColor, 15);
        }
    }

    private void drawFamilyTreeLines() {
        if (!dataCache.isShowFamilyTree()) { return; }
        Event selectedEvent = dataCache.getEventMapByEventID().get(dataCache.getSelectedEventID());
        familyTreeLineHelper(selectedEvent, 50);
    }

    private void familyTreeLineHelper(Event event, int lineWidth) {
        // father side
        String fatherID = dataCache.getPersonMapByPersonID().get(event.getPersonID()).getFatherID();
        if (fatherID == null) { return; }
        List<Event> fatherEventTempList = dataCache.getEventsListMapByPersonID().get(fatherID);
        List<Event> fatherEventList = sortEventsByYear(fatherEventTempList);
        if (fatherEventList.isEmpty()) { return; }

        Event fatherEndEvent = fatherEventList.get(0);
        drawLine(event, fatherEndEvent, familyTreeLineFatherColor, lineWidth);
        familyTreeLineHelper(fatherEndEvent, lineWidth - 12);

        // mother side
        String motherID = dataCache.getPersonMapByPersonID().get(event.getPersonID()).getMotherID();
        if (motherID == null) { return; }
        List<Event> motherEventTempList = dataCache.getEventsListMapByPersonID().get(motherID);
        List<Event> motherEventList = sortEventsByYear(motherEventTempList);
        if (motherEventList.isEmpty()) { return; }

        Event motherEndEvent = motherEventList.get(0);
        drawLine(event, motherEndEvent, familyTreeLineMotherColor, lineWidth);
        familyTreeLineHelper(motherEndEvent, lineWidth - 12);
    }

    private List<Event> sortEventsByYear(List<Event> unsortedList) {
        List<Event> sortedList = new ArrayList<>();
        unsortedList.removeIf(e -> !eventsToDisplay.contains(e));
        Map<String, Integer> unsortedEvents = new TreeMap<>();
        for (Event e : unsortedList) {
            unsortedEvents.put(e.getEventID(), e.getYear());
        }

        LinkedHashMap<String, Integer> sortedEvents = new LinkedHashMap<>();
        unsortedEvents.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .forEachOrdered(x -> sortedEvents.put(x.getKey(), x.getValue()));

        for (String id : sortedEvents.keySet()) {
            for (Event e : unsortedList) {
                if (e.getEventID().equals(id)) {
                    sortedList.add(e);
                }
            }
        }
        return sortedList;
    }

}