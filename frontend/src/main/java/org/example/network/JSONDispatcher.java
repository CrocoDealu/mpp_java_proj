package org.example.network;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class JSONDispatcher {
    private final Map<Integer, CompletableFuture<JSONObject>> pendingRequests = new ConcurrentHashMap<>();
    private final Map<String, List<Consumer<JSONObject>>> eventListeners = new ConcurrentHashMap<>();

    public JSONDispatcher() {
    }

    public void dispatch(JSONObject message) {
        if (message.has("messageId")) {
            int id = message.getInt("messageId");
            CompletableFuture<JSONObject> future = pendingRequests.remove(id);
            if (future != null) {
                future.complete(message);
            }
        } else {
            notifyListeners(message);
        }
    }

    private void notifyListeners(JSONObject message) {
        String eventType = message.getString("type");
        if (!eventListeners.containsKey(eventType)) {
            return;
        }
        for (Consumer<JSONObject> listener : eventListeners.get(eventType)) {
            listener.accept(message);
        }
    }

    public CompletableFuture<JSONObject> addPendingRequest(JSONObject request) {
        CompletableFuture<JSONObject> future = new CompletableFuture<>();
        int id = request.getInt("messageId");
        pendingRequests.put(id, future);
        return future;
    }

    public void onEvent(String eventType, Consumer<JSONObject> listener) {
        eventListeners.computeIfAbsent(eventType, key -> new ArrayList<>()).add(listener);
    }
}
