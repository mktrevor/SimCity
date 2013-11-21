package city.Restaurant2;

import interfaces.Restaurant2Cashier;
import interfaces.Restaurant2Customer;
import interfaces.Restaurant2Host;
import interfaces.Restaurant2Waiter;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import city.Menu;
import city.gui.restaurant2.Restaurant2CustomerGui;
import Role.Role;

public class Restaurant2CustomerRole extends Role implements Restaurant2Customer{

	private String name;
	private int hungerLevel = 100;        // determines length of meal
	Timer timer = new Timer();
	private Restaurant2CustomerGui customerGui;
	
	private int tableNumber;
	Timer t = new Timer();
	private int waiterNum;

	// agent correspondents
	private Restaurant2Host host;
	private Restaurant2Cashier cashier;
	private Restaurant2Waiter waiter;
	
	private Menu menu;
	private Menu reMenu;
	
	double wallet;
	double check;

	//    private boolean isHungry = false; //hack for gui
	public enum AgentState {initial, WaitingInRestaurant, BeingSeated, Seated, Ordering, Ordered, Eating, 
		DoneEating, PayingCheck, Leaving, ReadyToOrder, Gone, reordered};
	private AgentState state = AgentState.initial;//The start state

	public enum AgentEvent 
	{none, gotHungry, tablesFull, followWaiter, seated, readyToOrder, ordering, recievedFood,
		reorder, doneEating, doneLeaving, gotCheck, leaving};
	AgentEvent event = AgentEvent.none;
	
	private Semaphore atDestination = new Semaphore(0,true);

	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public Restaurant2CustomerRole(String name){
		super();
		this.name = name;
		tableNumber = -1;
		if(name.equals("flake") || name.equals("honest")){
			wallet = 5.00;
		}
		else if(name.equals("reorderleave") || name.equals("cheapest")){
			wallet = 5.99;
		}
		else{
		wallet = 20.00;
		}
	}
	
	public Restaurant2CustomerRole(){
		super();
		this.name = name;
		tableNumber = -1;
		if(name.equals("flake") || name.equals("honest")){
			wallet = 5.00;
		}
		else if(name.equals("reorderleave") || name.equals("cheapest")){
			wallet = 5.99;
		}
		else{
		wallet = 20.00;
		}
	}

	/**
	 * hack to establish connection to Host agent.
	 */
	public void setHost(Restaurant2Host host) {
		this.host = host;
	}
	
	public void setWaiter(Restaurant2Waiter w, int num){
		waiter = w;
		waiterNum = num;
	}

	public String getCustomerName() {
		return name;
	}

	
	// Messages

	public void gotHungry() {//from animation
		print("I'm hungry");
		event = AgentEvent.gotHungry;
		stateChanged();
	}
	
	public void msgAtDestination(){
		atDestination.release();
	}
	
	public void msgTablesAreFull(){
		event = AgentEvent.tablesFull;
		stateChanged();
	}

	public void msgFollowMeToTable(Restaurant2Waiter w, Menu m, int num, int waiterNum) {
		print("Received msgSitAtTable");
		event = AgentEvent.followWaiter;
		setWaiter(w, waiterNum);
		menu = m;
		tableNumber = num;
		stateChanged();
	}

	public void msgAnimationFinishedGoToSeat() {
		//from animation
		event = AgentEvent.seated;
		stateChanged();
	}
	
	public void msgAnimationFinishedLeaveRestaurant() {
		//from animation
		//event = AgentEvent.doneLeaving;
		state = AgentState.Gone;
		stateChanged();
	}
	
	public void ReadyToOrder(){
		//from animation
		print("Recieved msReadyToOrder");
		event = AgentEvent.readyToOrder;
		stateChanged();
	}
	
	public void msgWhatDoYouWant(){
		print("Recieved msg What Do You Want");
		event = AgentEvent.ordering;
		stateChanged();
	}

	
	public void msgHereIsYourFood(String food){
		print("Recieved food " + food);
		event = AgentEvent.recievedFood;
		stateChanged();
	}
	
	public void msgPleaseReorder(String m){
		print("Recieved msg Reorder food");
		event = AgentEvent.reorder;
		reMenu = menu.remove(m);
		stateChanged();
	}
	
	public void msgHereIsYourCheck(String food, double price, Restaurant2Cashier c){
		print("Recieved msg here is your check");
		event = AgentEvent.gotCheck;
		cashier = c;
		stateChanged();
	}
	
	public void msgHereIsYourChange(double change){
		print("Recieved change " + change);
		event = AgentEvent.leaving;
		wallet = wallet + change;
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		//	CustomerAgent is a finite state machine

		if ((state == AgentState.initial || state == AgentState.Gone) && event == AgentEvent.gotHungry){
			state = AgentState.WaitingInRestaurant;
			goToRestaurant();
			return true;
		}
		if(state == AgentState.WaitingInRestaurant && event == AgentEvent.tablesFull){
			tablesFull();
			event = AgentEvent.none;
			return true;
		}
		if (state == AgentState.WaitingInRestaurant && event == AgentEvent.followWaiter){
			state = AgentState.BeingSeated;
			ChooseAction(tableNumber);
			//SitDown(tableNumber);
			return true;
		}
		if (state == AgentState.Seated && event == AgentEvent.readyToOrder){
			state = AgentState.ReadyToOrder;
			callWaiter();
			return true;
		}
		if(state == AgentState.ReadyToOrder && event == AgentEvent.ordering){
			state = AgentState.Ordered;
			orderFood();
			return true;
		}
		if(state == AgentState.Ordered && event == AgentEvent.reorder){
			reOrderFood();
			state = AgentState.reordered;
			return true;
		}
		if((state == AgentState.Ordered || state == AgentState.reordered) && event == AgentEvent.recievedFood){
			state = AgentState.Eating;
			EatFood();
			return true;
		}
		if (state == AgentState.Eating && event == AgentEvent.doneEating){
			state = AgentState.DoneEating;
			getCheck();
			return true;
		}
		if(state == AgentState.DoneEating && event == AgentEvent.gotCheck){
			state = AgentState.PayingCheck;
			payCheck();
		}
		if(state == AgentState.PayingCheck && event == AgentEvent.leaving){
			leaveRestaurant();
			state = AgentState.Gone;
		}
		
		return false;
	}

	// ACTIONS

	private void goToRestaurant() {
		Do("Going to restaurant");
		host.msgIWantFood(this);//send our instance, so he can respond to us
	}
	
	private void tablesFull(){
		if(this.getName().equals("full")){
			host.msgNoTablesLeaving(this);
			print("The restaurant is too full, I'm going to leave.");
			wallet = wallet - 20.00; //to counteract money replenish that happens when leaving
			customerGui.setEnabled();
		}
		else{
			print("That's okay, I'll wait.");
			host.msgIllStay(this);
		}
	}
	
	private void ChooseAction(int tableNum){
		if(this.getName().equals("flake")){	//need to have this otherwise the customer will leave the restuarant
			SitDown(tableNum);
			waiter.msgReadyToBeSeated(this);
		}
		else if(!menu.doIHaveEnough(wallet)){
			print("No thanks, I don't have enough money to pay");
			waiter.msgLeavingNoMoney(this);
		}
		else{
			SitDown(tableNum);
			waiter.msgReadyToBeSeated(this);
		}
	}

	private void SitDown(int seatNumber) {
		Do("Being seated. Going to table");
		customerGui.DoGoToWaiter(waiterNum);
		try{
			atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		customerGui.DoGoToSeat(seatNumber);
		state = AgentState.Seated;
		
		//Hack until the Customer GUI is updated to call the waiter
		t.schedule(new TimerTask() {
			public void run() {
				state = AgentState.ReadyToOrder;
				callWaiter();
			}
		},
		8000);
		
	}
	
	private void callWaiter(){
		Do("Calling waiter");
		waiter.msgReadyToOrder(this);
	}
	
	private void orderFood(){
		String choice;
		Do("Ordering food");
		if(this.getName().equals("reorderleave")){
			choice = "Salad";
		}
		else if(this.getName().equals("cheapest")){
			choice = "Salad";
		}
		else if(this.getName().equals("pizza")){
			choice = "Pizza";
		}
		else if(this.getName().equals("chicken")){
			choice = "Chicken";
		}
		else if(this.getName().equals("steak")){
			choice = "Steak";
		}
		else if(this.getName().equals("salad")){
			choice = "Salad";
		}
		else{
			choice = menu.chooseFood();
			//print("not recognizing name.");
			//choice = "pizza";
		}
		print("I would like to order the " + choice);
		waiter.msgHereIsMyChoice(this, choice);
		customerGui.setFoodOrdered(choice);
	}
	
	private void reOrderFood(){
		print("Reordering food");
		Do("Ordering food");
		if(this.getName().equals("reorderleave") || this.getName().equals("cheapest")){
			String choice = reMenu.chooseFood();
			if(reMenu.doIHaveEnoughFor(wallet, choice)){
				//String choice = "Chicken";
				print("I would like to order the " + choice);
				waiter.msgHereIsMyChoice(this, choice);
				customerGui.setFoodOrdered(choice);
			}
			else{
				waiter.msgLeavingNoMoney(this);
				leaveRestaurant();
			}
		}
		else{
			String choice = reMenu.chooseFood();
			//String choice = "Chicken";
			print("I would like to order the " + choice);
			waiter.msgHereIsMyChoice(this, choice);
			customerGui.setFoodOrdered(choice);
		}
	}

	private void EatFood() {
		customerGui.setHasFood();
		Do("Eating Food");
		//This next complicated line creates and starts a timer thread.
		//We schedule a deadline of getHungerLevel()*1000 milliseconds.
		//When that time elapses, it will call back to the run routine
		//located in the anonymous class created right there inline:
		//TimerTask is an interface that we implement right there inline.
		//Since Java does not all us to pass functions, only objects.
		//So, we use Java syntactic mechanism to create an
		//anonymous inner class that has the public method run() in it.
		timer.schedule(new TimerTask() {
			Object cookie = 1;
			public void run() {
				print("Done eating, cookie=" + cookie);
				event = AgentEvent.doneEating;
				customerGui.setDoneEating();
				//isHungry = false;
				stateChanged();
			}
		},
		5000);//getHungerLevel() * 1000);//how long to wait before running task
	}
	
	private void getCheck(){
		print("Asking for the check");
		waiter.msgGetCheck(this);
	}
	
	private void payCheck(){
		print("Going to go pay my check");
		customerGui.DoGoToCashier();
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		cashier.msgHereIsPayment(this, wallet);
	}

	private void leaveRestaurant() {
		Do("Leaving restaurant.");
		waiter.msgDoneEatingNowLeaving(this);
		customerGui.DoExitRestaurant();
		wallet = wallet + 20.00; //replenish their money supply
	}

	// Accessors, etc.

	public String getName() {
		return name;
	}
	
	public int getHungerLevel() {
		return hungerLevel;
	}

	public void setHungerLevel(int hungerLevel) {
		this.hungerLevel = hungerLevel;
		//could be a state change. Maybe you don't
		//need to eat until hunger lever is > 5?
	}

	public String toString() {
		return "customer " + getName();
	}

	public void setGui(Restaurant2CustomerGui g) {
		customerGui = g;
	}

	public Restaurant2CustomerGui getGui() {
		return customerGui;
	}
	
	public int getTableNum(){
		return tableNumber;
	}
}
