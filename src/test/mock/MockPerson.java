package test.mock;

import Role.LandlordRole;
import city.MarketOrder;
import city.transportation.BusAgent;
import city.transportation.CarAgent;
import interfaces.Person;


public class MockPerson extends Mock implements Person {
	public EventLog log= new EventLog();
	
	public MockPerson(String name) {
		super(name);
	}

	@Override
	public void msgImHungry() {
		log.add(new LoggedEvent("Recieved msgImHungry"));
		System.out.println("Recieved msgImHungry");
	}

	
	//FROM HOUSE
	@Override
	public void msgImBroken(String type) {
		log.add(new LoggedEvent("Recieved msgImBroken from house, " + type + " is the broken appliance."));
		System.out.println("Recieved msgImBroken from house, " + type + " is the broken appliance.");
	}

	@Override
	public void msgItemInStock(String type) {
		log.add(new LoggedEvent("Recieved msgItemInStock from house, I have at least one " + type + "in my fridge."));
		System.out.println("Recieved msgItemInStock from house, I have at least one " + type + "in my fridge.");
	}

	@Override
	public void msgDontHaveItem(String food) {
		log.add(new LoggedEvent("Recieved msgDontHaveItem from house, I dont have any " + food + "in my fridge."));
		System.out.println("Recieved msgDontHaveItem from house, I dont have any " + food + "in my fridge.");
	}

	@Override
	public void msgFoodDone(String food) {
		log.add(new LoggedEvent("Recieved msgFoodDone from house, " + food + "is done cooking now."));
		System.out.println("Recieved msgFoodDone from house, " + food + "is done cooking now.");
	}

	//FROM BUS
	@Override
	public void msgArrivedAtStop(int stop) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgPleasePayFare(BusAgent b, double fare) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgBusIsHere(BusAgent b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgArrived() {
		// TODO Auto-generated method stub
		
	}

	//FROM LANDLORD
	@Override
	public void msgFixed(String appliance) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgRentDue(LandlordRole r, double rate) {
		// TODO Auto-generated method stub
		
	}

	//FROM MARKET
	@Override
	public void msgHereIsYourOrder(CarAgent car) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHereIsYourOrder(MarketOrder order) {
		// TODO Auto-generated method stub
		
	}
	
}