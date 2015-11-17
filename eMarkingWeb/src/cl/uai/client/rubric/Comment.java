package cl.uai.client.rubric;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Comment {

	private int id;
	private String text;
	private int format;
	private long lastUsed;
	private int timesUsed;
	private List<Integer> markerIds;
	private int markid;
	private boolean ownComment;
	/**
	 * @return the ownComment
	 */
	public boolean isOwnComment() {
		return ownComment;
	}

	/**
	 * @param ownComment the ownComment to set
	 */
	public void setOwnComment(boolean ownComment) {
		this.ownComment = ownComment;
	}

	/**
	 * @return the pages
	 */
	public List<Integer> getPages() {
		return pages;
	}

	/**
	 * @param pages the pages to set
	 */
	public void setPages(int pages) {
		if(!this.pages.contains(pages)) {
			this.pages.add(pages);
		}
	}

	private List<Integer> pages;
	private List<Integer> criteriaIds;

	/**
	 * @return the criteriaIds
	 */
	public List<Integer> getCriteriaIds() {
		return criteriaIds;
	}

	/**
	 * @param criteriaIds the criteriaIds to set
	 */
	public void setCriteriaIds(int criteriaIds) {
		if(!this.criteriaIds.contains(criteriaIds)) {
			this.criteriaIds.add(criteriaIds);
		}
	}

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
	public List<Integer> getMarkerId() {
		return markerIds;
	}

	/**
	 * @param markerId the markerId to set
	 */
	public void setMarkerId(int markerId) {
		if(!this.markerIds.contains(markerId)) {
			this.markerIds.add(markerId);
		}
	}

	public Comment(int id, String text, int format, List<Integer> markerid, int used, long lastused, List<Integer> pages, boolean ownComment, List<Integer> criteriaIds) {
		this.id = id;
		this.text = text;
		this.format = format;
		this.markerIds = markerid;
		this.timesUsed = used;
		this.lastUsed = lastused;
		this.pages = pages;
		this.ownComment = ownComment;
		this.criteriaIds = criteriaIds;
	}

	public static Comment createFromMap(Map<String, String> values) {

		Comment comment = null;
		try {
			int id = Integer.parseInt(values.get("id"));
			String text = values.get("text").trim();
			int format = Integer.parseInt(values.get("format"));
			int timesUsed = Integer.parseInt(values.get("used"));
			long lastUsed = Long.parseLong(values.get("lastused"));
			int owncomment = Integer.parseInt(values.get("owncomment"));
			boolean isown = owncomment > 0;

			String markersids = values.get("markerids");
			String[] mids = markersids.split("-");
			List<Integer> markerId = new ArrayList<Integer>();
			for(int i=0; i<mids.length; i++) {
				markerId.add(Integer.parseInt(mids[i]));				
			}

			String pages = values.get("pages");
			String[] pids = pages.split("-");
			List<Integer> pagesIds = new ArrayList<Integer>();
			for(int i=0; i<pids.length; i++) {
				pagesIds.add(Integer.parseInt(pids[i]));				
			}

			String criteria = values.get("criteria");
			String[] cids = criteria.split("-");
			List<Integer> criteriaIds = new ArrayList<Integer>();
			for(int i=0; i<cids.length; i++) {
				int cid = Integer.parseInt(cids[i]);
				if(cid > 0) {
					criteriaIds.add(cid);
				}
			}

			comment = new Comment(id, text, format, markerId, timesUsed, lastUsed, pagesIds, isown, criteriaIds);
		} catch (Exception e) {
			return null;
		}

		return comment;
	}

	/**
	 * @return the markid
	 */
	public int getMarkid() {
		return markid;
	}

	/**
	 * @param markid the markid to set
	 */
	public void setMarkid(int markid) {
		this.markid = markid;
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
