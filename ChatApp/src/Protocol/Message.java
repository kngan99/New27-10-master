package Protocol;

import java.io.Serializable;

public class Message implements Serializable {
	private static final long serialVersionUID = 1L;
    public String type, sender, content1, content2, recipient;
    
    public Message(String type, String sender, String content1, String content2, String recipient){
        this.type = type; this.sender = sender; this.content1 = content1; this.content2 = content2; this.recipient = recipient;
    }
    
    @Override
    public String toString() {
        return "{type='"+type+"', sender='"+sender+"', content1='"+content1+"', content2='"+content2+"', recipient='"+recipient+"'}";
    }

	public static Message sendRequestChat(String friendName, String friendIP) {
		// TODO Auto-generated method stub
		return  new Message("Request Chat", "", "" ,friendIP, friendName);
	}

	public static Message sendTextMessage(String message) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static Message disconnect(String message, String sender, String recipient) {
		return new Message(message, sender, "disconnect", "", recipient);
	}
}
