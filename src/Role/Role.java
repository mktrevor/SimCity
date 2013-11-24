package Role;

import java.util.concurrent.Semaphore;

import restaurant1.Restaurant1CashierRole;
import restaurant1.Restaurant1CookRole;
import restaurant1.Restaurant1CustomerRole;
import restaurant1.Restaurant1HostRole;
import restaurant1.Restaurant1WaiterRole;

import agent.StringUtil;
import city.PersonAgent;
import city.Restaurant2.Restaurant2CashierRole;
import city.Restaurant2.Restaurant2CookRole;
import city.Restaurant2.Restaurant2HostRole;
import city.Restaurant2.Restaurant2WaiterRole;

public abstract class Role {
	
	//Is this state change necessary if the role has no thread  ?

	public boolean isActive;
	public boolean inUse;

    protected Role() {
    	isActive = false;
    	inUse = false;
    }

    /**
     * Agents must implement this scheduler to perform any actions appropriate for the
     * current state.  Will be called whenever a state change has occurred,
     * and will be called repeated as long as it returns true.
     *
     * @return true iff some action was executed that might have changed the
     *         state.
     */
    public abstract boolean pickAndExecuteAnAction();

    /**
     * Return agent name for messages.  Default is to return java instance
     * name.
     */
    protected String getName() {
        return StringUtil.shortName(this);
    }

    /**
     * The simulated action code
     */
    protected void Do(String msg) {
        print(msg, null);
    }

    /**
     * Print message
     */
    protected void print(String msg) {
        print(msg, null);
    }

    /**
     * Print message with exception stack trace
     */
    protected void print(String msg, Throwable e) {
        StringBuffer sb = new StringBuffer();
        sb.append(getName());
        sb.append(": ");
        sb.append(msg);
        sb.append("\n");
        if (e != null) {
            sb.append(StringUtil.stackTraceString(e));
        }
        System.out.print(sb.toString());
    }
    
    /**
     * This function sets the role to active or not for use with the PersonAgent's scheduler
     */
    
    public void setActive(){
    	isActive = true;
    }
    
    public void setInactive(){
    	isActive = false;
    }
    
    public boolean isInUse(){
    	return inUse;
    }
    
	public static Role getNewRole(String type, PersonAgent p){
		if(type.equals("Restaurant2 Waiter")) return new Restaurant2WaiterRole(p.getName(), p);
		else if(type.equals("Restaurant2 Host")) return new Restaurant2HostRole(p.getName(), p);
		else if(type.equals("Restaurant2 Cook")) return new Restaurant2CookRole(p.getName(), p);
		else if(type.equals("Restaurant2 Cashier")) return new Restaurant2CashierRole(p.getName(), p);
		//else if(type.equals("Bank Manager")) return new BankManagerRole();
		else if(type.equals("Restaurant1 Customer")) return new Restaurant1CustomerRole(p.getName(), p);
		else if(type.equals("Restaurant1 Waiter")) return new Restaurant1WaiterRole(p.getName(), p);
		else if(type.equals("Restaurant1 Cook")) return new Restaurant1CookRole(p.getName(), p);
		else if(type.equals("Restaurant1 Host")) return new Restaurant1HostRole(p.getName(), p);
		else if(type.equals("Restaurant1 Cashier")) return new Restaurant1CashierRole(p.getName(), p);
		else return null;
	}
    
}
