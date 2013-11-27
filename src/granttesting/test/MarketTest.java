package granttesting.test;

import java.util.ArrayList;

import Role.MarketManager;
import Role.MarketManager.myMarketOrder;
import Role.MarketManager.myMarketWorker;
import Role.MarketManager.orderState;
import Role.MarketWorker;
import granttesting.test.mock.MockCook;
import granttesting.test.mock.MockMarketWorker;
import junit.framework.TestCase;
import city.MarketOrder;
import city.OrderItem;
import city.PersonAgent;
import city.PersonAgent.FoodState;

public class MarketTest extends TestCase{
        
    PersonAgent person;
    PersonAgent person2;
    MockCook restaurantCook;
    MarketManager marketMgr;
    MarketWorker marketWorker;
    myMarketWorker myMarketWorker;
    public ArrayList<OrderItem> testOrderItems;
    
    public void setUp() throws Exception{

            super.setUp();                
            person = new PersonAgent("Person");
            person2 = new PersonAgent("Person2");
            restaurantCook = new MockCook("MockCook");
            marketMgr = new MarketManager("MarketManager", person);
            marketWorker = new MarketWorker(person2);
         
            testOrderItems = new ArrayList<OrderItem>();
            
            
    }
    
    public void testNormativeMarketOrder(){
            
    	// MarketManager shouldn't be keeping track of any orders right now
    	assertEquals("MarketManager orderList should be empty", marketMgr.myOrders.size(), 0);
    	
    	// Try to submit order to the MarketManager of 5 chickens
    	OrderItem testItem = new OrderItem("Chicken", 5);
    	testOrderItems.add(testItem);
    	marketMgr.msgHereIsOrder(new MarketOrder(testOrderItems, person));
    	
    	// MarketManager should now have one order that it needs to process
    	assertEquals("MarketManager orderList should now have one order", marketMgr.myOrders.size(), 1);
    
    	// Add new worker who will be picking the order
        marketMgr.addWorker(marketWorker);
    	
    	// There should be one worker now added to assign the order picking process to
    	assertEquals("One marketWorker should now exist in the manager, is" + marketMgr.myWorkers.size(), marketMgr.myWorkers.size(), 1);
    	
    	// Process the first order that was just submitted by calling MarketManager's pickAndExecuteAnAction()
    	marketMgr.pickAndExecuteAnAction();
    	
    	// Order should now be in assignedToWorker state
    	assertTrue("First order in the list should now be assigned to a worker", marketMgr.myOrders.get(0).state == orderState.assignedToWorker);
    	
    	
    	
    	
    }  

}