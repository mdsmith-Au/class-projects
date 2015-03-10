package telecomlab3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * Defines the contents of a message.
 */
public class Message {
    // First 3 fields as defined by spec specifying message type, subtype
    // and size of data byte array to follow
    private int type, subType, size;
    // The byte array containing the message contents.
    private byte[] data;

    // All possible types as defined by spec
    /**
     *
     */
    public static final int TYPE_EXIT = 20;

    /**
     *
     */
    public static final int TYPE_BAD_FORMAT = 21;

    /**
     *
     */
    public static final int TYPE_ECHO = 22;

    /**
     *
     */
    public static final int TYPE_LOGIN = 23;

    /**
     *
     */
    public static final int TYPE_LOGOFF = 24;

    /**
     *
     */
    public static final int TYPE_CREATE_USER = 25;

    /**
     *
     */
    public static final int TYPE_DELETE_USER = 26;

    /**
     *
     */
    public static final int TYPE_CREATE_STORE = 27;

    /**
     *
     */
    public static final int TYPE_SEND_MSG = 28;

    /**
     *
     */
    public static final int TYPE_QUERY_MSG = 29;

    // All possible subtypes as defined by spec
    /**
     *
     */
    public static final int SUBTYPE_DELETE_USER_SUCCESS = 0;

    /**
     *
     */
    public static final int SUBTYPE_DELETE_USER_NOT_LOG_IN = 1;

    /**
     *
     */
    public static final int SUBTYPE_DELETE_USER_ERROR = 2;

    /**
     *
     */
    public static final int SUBTYPE_CREATE_USER_SUCCESS = 0;

    /**
     *
     */
    public static final int SUBTYPE_CREATE_USER_EXISTS = 1;

    /**
     *
     */
    public static final int SUBTYPE_CREATE_USER_LOGGED_IN = 2;

    /**
     *
     */
    public static final int SUBTYPE_CREATE_USER_BAD_FORMAT = 3;

    /**
     *
     */
    public static final int SUBTYPE_CREATE_STORE_SUCCESS = 0;

    /**
     *
     */
    public static final int SUBTYPE_CREATE_STORE_EXISTS = 1;

    /**
     *
     */
    public static final int SUBTYPE_CREATE_STORE_NOT_LOG_IN = 2;

    /**
     *
     */
    public static final int SUBTYPE_LOGIN_SUCCESS = 0;

    /**
     *
     */
    public static final int SUBTYPE_LOGIN_ALREADY_LOG_IN = 1;

    /**
     *
     */
    public static final int SUBTYPE_LOGIN_BAD_CREDENTIAL = 2;

    /**
     *
     */
    public static final int SUBTYPE_LOGIN_BAD_FORMAT = 3;

    /**
     *
     */
    public static final int SUBTYPE_LOGOFF_SUCCESS = 0;

    /**
     *
     */
    public static final int SUBTYPE_LOGOFF_NOT_LOG_IN = 1;

    /**
     *
     */
    public static final int SUBTYPE_LOGOFF_SESSION_EXPIRED = 2;

    /**
     *
     */
    public static final int SUBTYPE_SEND_MSG_SUCCESS = 0;

    /**
     *
     */
    public static final int SUBTYPE_SEND_MSG_FAIL_DATA_STORE = 1;

    /**
     *
     */
    public static final int SUBTYPE_SEND_MSG_USER_NOT_EXIST = 2;

    /**
     *
     */
    public static final int SUBTYPE_SEND_MSG_USER_NOT_LOGIN = 3;

    /**
     *
     */
    public static final int SUBTYPE_SEND_MSG_BAD_FORMAT = 4;

    /**
     *
     */
    public static final int SUBTYPE_QUERY_MSG_NO_MSG = 0;

    /**
     *
     */
    public static final int SUBTYPE_QUERY_MSG_MESSAGES = 1;
    // Not documented in PDF; on discussion board

    /**
     *
     */
    public static final int SUBTYPE_QUERY_MSG_NOT_LOG_IN = 2;

    // End line definintion for toString conversion
    String endl = System.getProperty("line.separator");

    /**
     * Basic constructor for a new message to send.
     *
     * @param type Message type. Subtype set to 0.
     * @param data Message data.
     */
    public Message(int type, byte[] data) {
        this(type, 0, data);
    }

    /**
     * Constructor for a fully defined message, either sending or receiving.
     * Message size must be less than or equal to 262144 bytes.
     *
     * @param type Message type.
     * @param subType Message subtype.
     * @param data Message data.
     */
    public Message(int type, int subType, byte[] data) {
        assert (data.length <= 262144);
        this.type = type;
        this.subType = subType;
        size = data.length;
        this.data = data;
    }

    /**
     * Constructor for an empty message with type and subtype 0.
     */
    public Message() {
        this.type = 0;
        this.subType = 0;
        this.size = 0;
        this.data = null;
    }

    /**
     * Constructor for a message received from an InputStream.
     *
     * @param in The input stream, generally from the server.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Message(InputStream in) throws IOException, ClassNotFoundException {
        this.readFromStream(in);
    }

    /**
     * Constructor for use when sending a message.
     *
     * @param type Message type to send. Subtype is set to 0.
     * @param data The UTF-8 encoded string to send. Conversion is done
     * automatically to a byte array for transmission.
     * @throws UnsupportedEncodingException
     */
    public Message(int type, String data) throws UnsupportedEncodingException {
        this(type, data.getBytes("UTF-8"));
    }

    /**
     * Getter for the message type.
     *
     * @return The message type.
     */
    public int getType() {
        return type;
    }

    /**
     * Getter for the message sub type.
     *
     * @return The message sub type.
     */
    public int getSubType() {
        return subType;
    }

    /**
     * Getter for the message size.
     *
     * @return The message size.
     */
    public int getSize() {
        return size;
    }

    /**
     * Getter for the message contents.
     *
     * @return The byte[] array of data.
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Getter for the message contents, except formatted as a String.
     *
     * @return The message data as a String.
     */
    public String getDataAsString() {
        return new String(data);
    }

    /**
     * Writes the message in its entirety to an OutputStream, generally for
     * transmission to a remote server.
     *
     * @param out The output stream to write to.
     * @throws IOException
     */
    public void writeToStream(OutputStream out)
            throws IOException {

        /*
         Note that calling .write() on an OutputStream writes only 1 byte if
         .write() is called on an integer, so we need to create a byte array
         representing the entire message to send it correctly.  The ByteBuffer class
         provides this functionality easily, as it correctly formats all data types.
         */
        ByteBuffer tempByteBuffer = ByteBuffer.allocate(12 + size);
        tempByteBuffer.putInt(type);
        tempByteBuffer.putInt(subType);
        tempByteBuffer.putInt(size);
        tempByteBuffer.put(data);

        out.write(tempByteBuffer.array());
    }

    /**
     * Fills in all message parameters and data from an InputStream, generally
     * from a remote server.
     *
     * @param in The input stream.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readFromStream(InputStream in)
            throws IOException, ClassNotFoundException {

        /*
         * See the writeToStream method for details on why a ByteBuffer is used.
         * We allocate 12 bytes because that is the size of the first 3 ints we know
         * are present in the message.
         */
        ByteBuffer tempByteBuffer = ByteBuffer.allocate(12);

        in.read(tempByteBuffer.array(), 0, 12);

        type = tempByteBuffer.getInt();
        subType = tempByteBuffer.getInt();
        size = tempByteBuffer.getInt();

        data = new byte[size];
        int bytesRead = in.read(data, 0, size);
        assert (bytesRead == size);
    }

    @Override
    public String toString() {
        return "Type: " + type + endl + "Subtype: " + subType + endl + "Size: " + size + endl + "Content: " + getDataAsString();
    }
}
