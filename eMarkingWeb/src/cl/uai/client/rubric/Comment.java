package cl.uai.client.rubric;

import java.util.Comparator;
import java.util.Map;

public class Comment {

	private int id;
	private String text;
	private int format;
	private long lastUsed;
	private int timesUsed;
	private int markerId;

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the format
	 */
	public int getFormat() {
		return format;
	}

	/**
	 * @param format the format to set
	 */
	public void setFormat(int format) {
		this.format = format;
	}

	/**
	 * @return the lastUsed
	 */
	public long getLastUsed() {
		return lastUsed;
	}

	/**
	 * @param lastUsed the lastUsed to set
	 */
	public void setLastUsed(long lastUsed) {
		this.lastUsed = lastUsed;
	}

	/**
	 * @return the timesUsed
	 */
	public int getTimesUsed() {
		return timesUsed;
	}

	/**
	 * @param timesUsed the timesUsed to set
	 */
	public void setTimesUsed(int timesUsed) {
		this.timesUsed = timesUsed;
	}

	/**
	 * @return the markerId
	 */
	public int getMarkerId() {
		return markerId;
	}

	/**
	 * @param markerId the markerId to set
	 */
	public void setMarkerId(int markerId) {
		this.markerId = markerId;
	}

	public Comment(int id, String text, int format, int markerid, int used, long lastused){
		this.id = id;
		this.text = text;
		this.format = format;
		this.markerId = markerid;
		this.timesUsed = used;
		this.lastUsed = lastused;
	}

	public static Comment createFromMap(Map<String, String> values) {

		Comment comment = null;
		try {
			int id = Integer.parseInt(values.get("id"));
			String text = values.get("text").trim();
			int format = Integer.parseInt(values.get("format"));
			int markerId = Integer.parseInt(values.get("markerid"));
			int timesUsed = Integer.parseInt(values.get("used"));
			long lastUsed = Long.parseLong(values.get("lastused"));

			comment = new Comment(id, text, format, markerId, timesUsed, lastUsed);
		} catch (Exception e) {
			return null;
		}

		return comment;
	}

	public static Comparator<Comment> CommentTextComparator  = new Comparator<Comment>() {

		public int compare(Comment comment1, Comment comment2) {

			String text1 = comment1.getText().toUpperCase();
			String text2 = comment2.getText().toUpperCase();

			return text1.compareTo(text2);

		}
	};

	public static Comparator<Comment> CommentLastUsedComparator  = new Comparator<Comment>() {

		public int compare(Comment comment1, Comment comment2) {
			Long lastused1 = comment1.getLastUsed();
			Long lastused2 = comment2.getLastUsed();
			//Inverted compare to make latest appear first
			return lastused2.compareTo(lastused1);
		}

	};

	public static Comparator<Comment> CommentTimesUsedComparator  = new Comparator<Comment>() {

		public int compare(Comment comment1, Comment comment2) {

			Integer timesused1 = comment1.getTimesUsed();
			Integer timesused2 = comment2.getTimesUsed();
			//Inverted compare to make most used appear first
			return timesused2.compareTo(timesused1);
		}
	};	
}
