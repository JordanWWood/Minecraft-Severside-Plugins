package network.marble.dataaccesslayer.managers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import network.marble.dataaccesslayer.base.DataAccessLayer;
import network.marble.dataaccesslayer.entities.analytics.Event;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AnalyticsManager {
    private static AnalyticsManager instance;
    private MongoClient mongoClient;
    private final ConcurrentLinkedQueue<Event> queue = new ConcurrentLinkedQueue<>();
    private final UUID timerUUID;

    public AnalyticsManager() {
        String address = DataAccessLayer.getEnvironmentalVariable("MONGO_HOST");
        if (address.isEmpty()) address = System.getProperty("MONGO_HOST");
        if (address == null) throw new IllegalArgumentException("Analytics Manager: Mongo address could not be found");

        mongoClient = MongoClients.create("mongodb://"+address);
        timerUUID = TimerManager.getInstance().runEvery((timer, bool) -> pushEventsToDb(), 1, TimeUnit.SECONDS);
    }

    private List<Document> eventsToDocs(List<Event> events) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        List<Document> docs = events.stream().map(event -> {
            try {
                return Document.parse(mapper.writeValueAsString(event));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());

        return docs;
    }

    private void pushEventsToDb() {
        List<Event> events = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        for (Event event = queue.poll();
             event != null && (System.currentTimeMillis() - startTime) < 5000;
             event = queue.poll()) {
            events.add(event);
        }

        if (events.size() == 0) return;

        List<Document> docs = eventsToDocs(events);

        if (docs.size() == 0) return;
        if (docs.size() != events.size()) {
            DataAccessLayer.instance.logger.severe("Failed to convert all events to documents, number failed: " + (events.size() - docs.size()));
        }
        getEventCollection().insertMany(docs, (final Void result, final Throwable t) -> {
            if (t != null) {
                DataAccessLayer.instance.logger.severe("Event Push Failed for: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    protected MongoDatabase getAnalyticsDatabase() {
        return mongoClient.getDatabase("Analytics");
    }

    protected MongoCollection<Document> getEventCollection() {
        return getAnalyticsDatabase().getCollection("Events");
    }

    /**
     * Submits the event to the Mongo. The events time code will automatically get set here.
     *
     * @param event The given event
     */
    public <T extends Event> boolean addEvent(T event) {
        // Set the time when the event is submitted rather than when the batch is sent
        event.setTimeCode(System.currentTimeMillis());
        if (!queue.offer(event)) {
            DataAccessLayer.instance.logger.severe("Failed to added event to queue");
            return false;
        }

        return true;
    }

    public void cleanUp() {
        TimerManager.getInstance().stopTimer(timerUUID);
        pushEventsToDb();
    }

    public static AnalyticsManager getInstance() {
        if (instance == null) instance = new AnalyticsManager();
        return instance;
    }
}
