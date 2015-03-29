package telecomclient;

public class RequestPacket {
    private final byte trafficType;
    private final byte activateFlag;

    public RequestPacket(byte trafficType, byte activateFlag) {
        this.trafficType = trafficType;
        this.activateFlag = activateFlag;
    }

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
