package com.example.firstapp_luismunoz.utils;

/** One-shot LiveData payload (toasts, navigation) so it isn't replayed after rotation. */
public class Event<T> {

    private final T content;
    private boolean handled = false;

    public Event(T content) {
        this.content = content;
    }

    /** @return the content the first time it is called, null afterwards. */
    public synchronized T getContentIfNotHandled() {
        if (handled) return null;
        handled = true;
        return content;
    }

    public T peekContent() {
        return content;
    }
}
