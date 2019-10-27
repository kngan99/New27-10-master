package Protocol;

import java.io.Serializable;

import Protocol.Tags;

@SuppressWarnings("serial")
public class DFile implements Serializable{

	@SuppressWarnings("unused")
	private String openTags = Tags.FILE_DATA_OPEN_TAG;
	@SuppressWarnings("unused")
	private String closeTags = Tags.FILE_DATA_CLOSE_TAG;
	public byte[] data;
	
	public DFile() {
		data = new byte[Tags.MAX_MSG_SIZE];
	}
	
	public DFile(int size) {
		data = new byte[size];
	}
}
