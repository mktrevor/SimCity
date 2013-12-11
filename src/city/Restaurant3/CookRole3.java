package city.Restaurant3;

import Role.Role;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.concurrent.Semaphore;
import javax.swing.Timer;
import activityLog.ActivityLog;
import activityLog.ActivityTag;
import city.MarketOrder;
import city.OrderItem;
import city.PersonAgent;
import city.Restaurant3.Order.orderStatus;
import city.Restaurant3.OrderSpindle3;
import city.gui.Restaurant3.*;
import test.mock.EventLog;
import test.mock.LoggedEvent;


/**
 * Restaurant3 Cook Agent
 */
public class CookRole3 extends Role {
	
	// Variable Declarations
	private String name;
	private List<Order> currentOrders;
	//private List<MarketAgent> myMarkets;
	public Hashtable<String, FoodItem> allFood;
	private static final int REORDER_THRESHOLD = 2; // Once a food item has this many of itself left, a reorder request will automatically be placed
	private static final int MARKETS_NUM = 2;

	private Semaphore isAnimating = new Semaphore(0,true);
	private CookGui3 cookGui;
	
	public EventLog log;
	
	protected OrderSpindle3 oSpindle;
	
	PersonAgent person;
	ActivityTag tag = ActivityTag.RESTAURANT3COOK;
	
	String roleName = "Restaurant3CookRole";
	
	Timer spindleCheck;
	
	public CookRole3(String name, PersonAgent p) {

		super();
		this.name = name;
		
		currentOrders = Collections.synchronizedList(new ArrayList<Order>());
		//myMarkets = Collections.synchronizedList(new ArrayList<MarketAgent>());
		
		log = new EventLog();
		
		allFood = new Hashtable<String, FoodItem>();
		allFood.put("Chicken", new FoodItem("Chicken", 3000, 2));
		allFood.put("Mac & Cheese", new FoodItem("Mac & Cheese", 3000, 3));
		allFood.put("French Fries", new FoodItem("French Fries", 4000, 3));
		allFood.put("Pizza", new FoodItem("Pizza", 7000, 3));
		allFood.put("Pasta", new FoodItem("Pasta", 6000, 3));
		allFood.put("Cobbler", new FoodItem("Cobbler", 5000, 3));
		
		person = p;
		
		oSpindle = OrderSpindle3.returnSpindleInstance();
		
		spindleCheck = new Timer(2000,
				new ActionListener() { public void actionPerformed(ActionEvent event) {
					checkOrderSpindle();
					spindleCheck.restart();
		      }
		});
		spindleCheck.start();
		
	}
	
	// Messages
	public void hereIsOrder(String choice, WaiterRole3 waiter, int tableNum) {
		log("Cook has received an order of " + choice + " for table #" + tableNum + " via waiter " + waiter.getName() + ".");
		// Determine if there is enough inventory of this item to fulfill this order
		if (allFood.get(choice).quantity >= 1) { // Able to fulfill order, dock one from that item's inventory
			Order o = new Order();
			o.foodItem = choice;
			o.requestingWaiter = waiter;
			o.recipTable = tableNum;
			currentOrders.add(o);
			person.stateChanged();
		} else { // Unable to fulfill order, create it and have it marked as bounce back
			Order o = new Order();
			o.foodItem = choice;
			o.requestingWaiter = waiter;
			o.recipTable = tableNum;
			o.status = orderStatus.bounceBack;
			currentOrders.add(o);
			person.stateChanged();
		}
	}
	
	public void deliverFood(String incomingFood, int quantity) {
		log("Accepting order of " + quantity + " " + incomingFood + "(s) from market.");
		FoodItem f = allFood.get(incomingFood);
		if (quantity < f.requestedQuantity && f.searchMarket != MARKETS_NUM){
			f.searchMarket++;
		}
		int currentFoodQuantity = f.quantity;
		int newFoodQuantity  = currentFoodQuantity + quantity;
		f.quantity = newFoodQuantity;
		f.reorderSent = false;
		person.stateChanged();
	}
	
	public void hereIsYourOrder(MarketOrder o) {
		log("Accepting order of " + o.orders.get(0).quantity + " " + o.orders.get(0).name + "(s) from market.");
		FoodItem f = allFood.get(o.orders.get(0).name);
		if (o.orders.get(0).quantity < f.requestedQuantity && f.searchMarket != MARKETS_NUM){
			f.searchMarket++;
		}
		int currentFoodQuantity = f.quantity;
		int newFoodQuantity  = currentFoodQuantity + o.orders.get(0).quantity;
		f.quantity = newFoodQuantity;
		f.reorderSent = false;
		person.stateChanged();
	}
	
	
	public void pickedUpFood(String foodChoice){
		cookGui.platingFood.remove(foodChoice);
		person.stateChanged();
	}
	
	/* // Now done compeltely throguh citymap and cashier instead
	public void msgHereIsMarketBill(double billAmount, MarketManager manager){
		
	}
	*/
	
	// Scheduler
	public boolean pickAndExecuteAnAction() {
		if (!currentOrders.isEmpty()) {
			try {
				for (Order order : currentOrders) {
					if (order.getStatus() == orderStatus.waiting){
						prepareFood(order);
						return true;
					}
				}
				for (Order order : currentOrders) {
					if (order.getStatus() == orderStatus.ready) {
						orderDone(order);
						return true;
					}
				}
				for (Order order : currentOrders) {
					if (order.getStatus() == orderStatus.bounceBack) { // Item is out, send choice back to waiter
						orderOut(order);
						return true;
					}
				}
			} catch (ConcurrentModificationException schedulerComod) {
				return true;
			}
		}
		goHome();
		return false;
	}
	
	private void goHome(){
		// Go to cook home
		cookGui.setDestination(225, 445);
		cookGui.beginAnimate();
		try {
			isAnimating.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// Actions
	private void prepareFood(Order o){ // Begins cooking the specified order and starts a timer based on the food item class' set cooking time
		log("Beginning to prepare food " + o.getFoodName() + ".");
		
		// Get from fridge
	    cookGui.setDestination(350, 445);
		cookGui.beginAnimate();
		try {
			isAnimating.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		String carryText = "";
		switch(o.getFoodName()){
		case "Chicken":
			carryText = "CHK";
			break;
		case "Mac & Cheese":
			carryText = "M&C";
			break;
		case "French Fries":
			carryText = "FRF";
			break;
		case "Pizza":
			carryText = "PZA";
			break;
		case "Pasta":
			carryText = "PST";
			break;
		case "Cobbler":
			carryText = "CBL";
			break;
		}
		cookGui.setCarryText(carryText);
		
		// Move to put food on grill
		cookGui.setDestination(225, 495);
		cookGui.beginAnimate();
			try {
				isAnimating.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
		}
			
		// Place food on grill
		cookGui.cookingFood.add(carryText);
		
		cookGui.setCarryText("");
		
		o.status = orderStatus.preparing;
		o.setCooking(allFood.get(o.getFoodName()).cookingTime);
		allFood.get(o.foodItem).decrementQuantity(); // After preparing this order, there is one less of this item available
		if (allFood.get(o.foodItem).quantity <= REORDER_THRESHOLD && allFood.get(o.foodItem).reorderSent == false){
			int orderQuantity = allFood.get(o.foodItem).maxCapacity - allFood.get(o.foodItem).quantity;
			// myMarkets.get(allFood.get(o.foodItem).searchMarket).orderFood(this, o.foodItem, orderQuantity);
			List<OrderItem> orderForMarket = new ArrayList<OrderItem>();
			OrderItem oItem = new OrderItem(o.foodItem, orderQuantity);
			orderForMarket.add(oItem);
			MarketOrder finalOrder = new MarketOrder(orderForMarket, "rest3", person);
			person.getCityMap().msgMarketHereIsTruckOrder(3, finalOrder);
			allFood.get(o.foodItem).requestedQuantity = orderQuantity;
		}
		
	}

	private void orderDone(Order o){ // Tells the specific waiter that their customer's order is done and removes that order from the cook's list of orders
		
		log("Notifying waiter that " + o.getFoodName() + " for table #" + o.recipTable + "is done.");
		
		// Go to grill to get order and remove it
		cookGui.setDestination(225, 495);
		cookGui.beginAnimate();
			try {
				isAnimating.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
		}
			
		String carryText = "";
		switch(o.getFoodName()){
		case "Chicken":
			carryText = "CHK";
			break;
		case "Mac & Cheese":
			carryText = "M&C";
			break;
		case "French Fries":
			carryText = "FRF";
			break;
		case "Pizza":
			carryText = "PZA";
			break;
		case "Pasta":
			carryText = "PST";
			break;
		case "Cobbler":
			carryText = "CBL";
			break;
		}
		cookGui.setCarryText(carryText);
		
		// Pick up food from grilling area
		cookGui.cookingFood.remove(carryText);
		
		// Go to plating area
		cookGui.setDestination(225, 390);
		cookGui.beginAnimate();
		try {
			isAnimating.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Drop off food on plating area
		cookGui.platingFood.add(carryText);
		
		cookGui.setCarryText("");
		
		o.getWaiter().hereIsFood(o.recipTable, o.foodItem);
		
		currentOrders.remove(o);
		
	}
	
	private void orderOut(Order o){ // Tells the specific waiter that their customer's order cannot be fulfilled
		log("Notifying waiter that " + o.getFoodName() + " is out of stock and the customer who ordered it needs to rechoose.");
		o.getWaiter().needNewChoice(o.recipTable, o.foodItem);
		currentOrders.remove(o);
	}
	
	//public void addMarket(MarketAgent m){
	//	myMarkets.add(m);
	//}
	
	private void checkOrderSpindle(){
		while(!oSpindle.isSpindleEmpty()){
			Order o = new Order();
			o = oSpindle.removeOrder();
			if (allFood.get(o.foodItem).quantity >= 1) { // Able to fulfill order, dock one from that item's inventory
				o.status = orderStatus.waiting;
			} else {
				o.status = orderStatus.bounceBack;
			}
			currentOrders.add(o);
			person.stateChanged();
		}
	}
	
	public class FoodItem {
		
		String foodItem;
		int cookingTime;
		public int quantity;
		boolean reorderSent;
		int searchMarket;
		int requestedQuantity;
		int maxCapacity;
		
		public FoodItem(String foodName, int cookTime, int initialInventory){
			foodItem = foodName;
			cookingTime = cookTime;
			quantity = initialInventory;
			reorderSent = false;
			searchMarket = 0;
			requestedQuantity = 0;
			maxCapacity = initialInventory;
		}
		
		public void decrementQuantity(){
			quantity--;
		}
		
	}
	
	public void releaseSemaphore(){
		isAnimating.release();
	}
	
	// Accessors
	public String getName() {
		return name;
	}

	public List<Order> getOrders() {
		return currentOrders;
	}
	
	public void setGui(CookGui3 cg) {
		cookGui = cg;
	}
	
	private void log(String msg){
		print(msg);
        ActivityLog.getInstance().logActivity(tag, msg, name, false);
        log.add(new LoggedEvent(msg));
	}

	public String getRoleName() {
		return roleName;
	}

	public PersonAgent getPerson() {
		return person;
	}

	public void emptyStock() {
		allFood.put("Chicken", new FoodItem("Chicken", 3000, 0));
		allFood.put("Mac & Cheese", new FoodItem("Mac & Cheese", 3000, 0));
		allFood.put("French Fries", new FoodItem("French Fries", 4000, 0));
		allFood.put("Pizza", new FoodItem("Pizza", 7000, 0));
		allFood.put("Pasta", new FoodItem("Pasta", 6000, 0));
		allFood.put("Cobbler", new FoodItem("Cobbler", 5000, 2));
	}

}