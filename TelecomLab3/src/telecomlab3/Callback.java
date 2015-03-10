package telecomlab3;

/**
 * An interface that classes that desire callbacks must implement. The
 * {@link #handleResponse(telecomlab3.Message) handleResponse} will always be
 * called.
 */
public interface Callback {
    /**
     * Will be called to handle callbacks for received messages.
     *
     * @param msg The message received.
     */
    void handleResponse(Message msg);
}
