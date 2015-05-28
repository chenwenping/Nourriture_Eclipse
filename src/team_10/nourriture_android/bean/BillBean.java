package team_10.nourriture_android.bean;

import java.io.Serializable;
import java.util.Date;

public class BillBean implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 45357925575834720L;
	
	private String _id;
    private String from;
    private String to;
    private String dish;
	
	private String restaurantName;
	private String name;
	private int price;
	private Date date;
	
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
	public String getRestaurantName() {
		return restaurantName;
	}
	public void setRestaurantName(String restaurantName) {
		this.restaurantName = restaurantName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
}
