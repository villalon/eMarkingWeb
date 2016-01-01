/**
 * 
 */
package cl.uai.client.data;

/**
 * @author Jorge Villalón
 *
 */
public class Level {

	/**
	 * @param id
	 * @param description
	 */
	public Level(Criterion criterion, int id, String description, float score) {
		super();
		this.id = id;
		this.description = description;
		this.score = score;
		this.criterion = criterion;
	}
	
	private int id;
	private String description;
	private float score;
	private float bonus = 0;
	private int page = 0;
	private int markId = 0;
	private Criterion criterion = null;
	
	/**
	 * @return the criterion
	 */
	public Criterion getCriterion() {
		return criterion;
	}
	/**
	 * @return the score
	 */
	public float getScore() {
		return score;
	}
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @return the bonus
	 */
	public float getBonus() {
		return bonus;
	}
	/**
	 * @param bonus the bonus to set
	 */
	public void setBonus(float bonus) {
		this.bonus = bonus;
	}
	/**
	 * @return the page
	 */
	public int getPage() {
		return page;
	}
	/**
	 * @param page the page to set
	 */
	public void setPage(int page) {
		this.page = page;
	}
	/**
	 * @return the markId
	 */
	public int getMarkId() {
		return markId;
	}
	/**
	 * @param markId the markId to set
	 */
	public void setMarkId(int markId) {
		this.markId = markId;
	}
}
