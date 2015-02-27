package telecomlab3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Message {

    private int type, subType, size;
    private byte[] data;

    public static final int TYPE_EXIT = 20;
    public static final int TYPE_BAD_FORMAT = 21;
    public static final int TYPE_ECHO = 22;
    public static final int TYPE_LOGIN = 23;
    public static final int TYPE_LOGOFF = 24;
    public static final int TYPE_CREATE_USER = 25;
    public static final int TYPE_DELETE_USER = 26;
    public static final int TYPE_CREATE_STORE = 27;
    public static final int TYPE_SEND_MSG = 28;
    public static final int TYPE_QUERY_MSG = 29;

    private static final Logger logger = Logger.getLogger(Message.class.getName());
    
    String endl = System.getProperty("line.separator");
    
    public Message(int type, byte[] data) {
        this(type, 0, data);
    }

    public Message(int type, int subType, byte[] data) {
        assert (data.length <= 262144);
        this.type = type;
        this.subType = subType;
        size = data.length;
        this.data = data;
    }

    public Message() {
        this.type = 0;
        this.subType = 0;
        this.size = 0;
        this.data = null;
    }

    public Message(InputStream in) throws IOException, ClassNotFoundException {
        this.readFromStream(in);
    }
    
    public Message(int type, String data) throws UnsupportedEncodingException {
        this(type, data.getBytes("UTF-8"));
    }

    public int getType() {
        return type;
    }

    public int getSubType() {
        return subType;
    }

    public int getSize() {
        return size;
    }

    public byte[] getData() {
        return data;
    }

    public String getDataAsString() {
        return new String(data);
    }

    public void writeToStream(OutputStream out)
            throws IOException {
        ByteBuffer tempByteBuffer = ByteBuffer.allocate(12 + size);
        tempByteBuffer.putInt(type);
        tempByteBuffer.putInt(subType);
        tempByteBuffer.putInt(size);
        tempByteBuffer.put(data);

        out.write(tempByteBuffer.array());
    }

    public void readFromStream(InputStream in)
            throws IOException, ClassNotFoundException {

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
    public String toString()  {
        return "Type: " + type + endl + "Subtype: " + subType + endl + "Size: " + size + endl + "Content: " + getDataAsString();
    }
}
