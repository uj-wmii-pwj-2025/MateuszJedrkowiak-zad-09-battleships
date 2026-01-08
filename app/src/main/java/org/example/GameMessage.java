package org.example;

public record GameMessage(Message status, String coordinate) {
    public static final String SEPARATOR = ";";

    public static GameMessage fromString(String raw) {
        if (raw == null || !raw.contains(SEPARATOR)) return null;
        String[] parts = raw.split(SEPARATOR, 2);
        if (parts.length < 2) return null;

        Message msgStatus = Message.convertStringToMessage(parts[0]);
        if (msgStatus == null) return null;

        return new GameMessage(msgStatus, parts[1]);
    }

    public static GameMessage isInvalid(String msg) {
        if (msg == null || !msg.contains(SEPARATOR)) return null;

        String[] parts = msg.split(";");
        if (parts.length != 2) return null;

        Message status = Message.convertStringToMessage(parts[0]);
        if(status == null) return null;
        return new GameMessage(status, parts[1]);
    }

    @Override
    public String toString() {
        return status.getMessage() + SEPARATOR + coordinate;
    }
}