package websocket.messages;

public class NotificationsMessage extends ServerMessage {

    private final String message;

    public NotificationsMessage(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }

    public String getNotificationMessage() {
        return message;
    }
}
