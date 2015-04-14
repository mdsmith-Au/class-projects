package telecomclient;

/**
 * Data structure for a client message request.
 * Used to determine whether or not to use the leaky bucket and what traffic
 * source to use for the connection.
 * @author Kevin Dam
 */
public class RequestPacket {
    private final byte trafficType;
    private final byte activateFlag;

    /**
     * Constructor for byte arguments.
     * @param trafficType the traffic source type to use (constant or bursty)
     * @param activateFlag whether or not to use the leaky bucket
     */
    public RequestPacket(byte trafficType, byte activateFlag) {
        this.trafficType = trafficType;
        this.activateFlag = activateFlag;
    }

    /**
     * Constructor for String arguments.
     * @param trafficType the traffic source type to use (constant or bursty)
     * @param activateFlag whether or not to use the leaky bucket
     */
    public RequestPacket (String trafficType, String activateFlag) {
        this.trafficType = trafficStringToByte(trafficType);
        this.activateFlag = activateStringToByte(activateFlag);
    }

    public byte getTrafficType() {
        return trafficType;
    }

    public byte getActivateFlag() {
        return activateFlag;
    }

    /**
     * Converts the given trafficType string and returns the byte equivalent.
     * @param trafficType string for "constant" or "burst" traffic
     * @return the byte equivalent representing constant or bursty traffic
     */
    private byte trafficStringToByte(String trafficType) {
        byte retVal = Byte.MAX_VALUE;
        if (trafficType.equals("constant")) {
            retVal = 0;
        }
        else if (trafficType.equals("burst")) {
            retVal = 1;
        }
        return retVal;
    }

    /**
     * Converts the given activationFlag string and returns the byte equivalent.
     * @param activateFlag string for "true" or "false" (to activate the leaky bucket or not)
     * @return the byte equivalent representing the activation of the leaky bucket
     */
    private byte activateStringToByte(String activateFlag) {
        byte retVal = Byte.MAX_VALUE;
        if (activateFlag.equals("false")) {
            retVal = 0;
        }
        else if (activateFlag.equals("true")) {
            retVal = 1;
        }
        return retVal;
    }
}
