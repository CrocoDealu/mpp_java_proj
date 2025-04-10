package org.example.network;

import org.example.model.Entity;

public interface Publisher<E extends Subscriber> {
    public void subscribe(E subscriber);
    public void unsubscribe(E subscriber);
    public void notifySubscribers(String notification);
}
