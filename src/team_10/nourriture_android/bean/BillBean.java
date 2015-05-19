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
	private String restaurant_name;
	public String getDish_name() {
		return dish_name;
	}
	public void setDish_name(String dish_name) {
		this.dish_name = dish_name;
	}
	public int getDish_count() {
		return dish_count;
	}
	public void setDish_count(int dish_count) {
		this.dish_count = dish_count;
	}
	public int getDish_price() {
		return dish_price;
	}
	public void setDish_price(int dish_price) {
		this.dish_price = dish_price;
	}
	public String getRestaurant_name() {
		return restaurant_name;
	}
	public void setRestaurant_name(String restaurant_name) {
		this.restaurant_name = restaurant_name;
	}
	
}
