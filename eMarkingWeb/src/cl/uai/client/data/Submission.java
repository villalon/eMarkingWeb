package cl.uai.client.data;

import java.util.Map;
import java.util.logging.Logger;

public class Submission {
	private static Logger logger = Logger.getLogger(Submission.class.getCanonicalName());
	private int id;
	private int assignedMarker;
	private float grade;
	
	public Submission(Map<String, String> map) {
		logger.fine("id");
		int submissionId = Integer.parseInt(map.get("id"));
		logger.fine("grade");
		float grade = Float.parseFloat(map.get("grade"));
		logger.fine("assigned marker");
		int assignedMarker = Integer.parseInt(map.get("teacher"));
		
		this.id = submissionId;
		this.grade = grade;
		this.assignedMarker = assignedMarker;
	}
	
	public float getGrade() {
		return grade;
	}
	public void setGrade(float grade) {
		this.grade = grade;
	}
	public int getAssignedmarker() {
		return assignedMarker;
	}
	public void setAssignedmarker(int assignedmarker) {
		this.assignedMarker = assignedmarker;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
}
