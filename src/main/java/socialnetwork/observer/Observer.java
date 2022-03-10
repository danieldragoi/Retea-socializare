package socialnetwork.observer;

import socialnetwork.events.Event;

public interface Observer<E extends Event> {
    void update(E e);
}