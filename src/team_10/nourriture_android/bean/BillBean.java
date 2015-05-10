package team_10.nourriture_android.bean;

import java.io.Serializable;

public class BillBean implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 45357925575834720L;
	
	private String _id;
    private String from;
    private String to;
    private String dish;
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getDish() {
		return dish;
	}
	public void setDish(String dish) {
		this.dish = dish;
	}
	
	private String dish_name;
	private int dish_count;
	private int dish_price;
	
}
