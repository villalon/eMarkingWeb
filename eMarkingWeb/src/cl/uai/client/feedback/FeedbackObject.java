package cl.uai.client.feedback;

public class FeedbackObject {
	
	private int id = -1;
	private String name = null;
	private String link = null;
	private String nameOER = null;
	
	public FeedbackObject(int id, String name, String link, String nameOER){
		this.id = id;
		this.name = name;
		this.link = link;
		this.nameOER = nameOER;
	}
	
	public FeedbackObject(String name, String link, String nameOER){
		this.name = name;
		this.link = link;
		this.nameOER = nameOER;
	}
	
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getLink() {
		return link;
	}
	
	public String getNameOER() {
		return nameOER;
	}
	
	public void setId(int id){
		this.id = id;
	}
}


