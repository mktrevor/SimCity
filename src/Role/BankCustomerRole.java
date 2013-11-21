package Role;

import test.mock.LoggedEvent;

public class BankCustomerRole extends Role{

	public enum state {arrived, wihdraw, openaccount, deposit, leave};
	public int bankaccountnumber;
	state bankcustomerstate;
	BankTellerRole mybankteller;
	public BankCustomerRole()
	{
		
		bankcustomerstate = state.arrived;
		stateChanged();
	
	}
	
	
	public void msgAssignMeBankTeller(BankTellerRole assignbankteller)
	{
		mybankteller = assignbankteller;
		stateChanged();
	
	}
	
	public void msgWithDrawFund(double withdrawal)
	{
		bankcustomerstate = state.wihdraw;
		stateChanged();
		
	}
	
	public void msgOpenAccount()
	{
		
		bankcustomerstate = state.openaccount;
		stateChanged();
		
	}
	
	public void msgDepositIntoAccount(double deposit)
	{
		bankcustomerstate = state.deposit;
		stateChanged();
		
	}
	
	public void msgLeaveBank()
	{
		bankcustomerstate = state.leave;
		stateChanged();
		
	}

	public void msgOpenAccountDone() {
		// TODO Auto-generated method stub
		
	}

	public void msgHereIsYourWithdrawal(double withdrawal) {
		// TODO Auto-generated method stub
		
	}

	public void msgWithdrawalFailed() {
		// TODO Auto-generated method stub
		
	}

	public void msgCannotGetLoan(double loan) {
		// TODO Auto-generated method stub
		
	}

	public void msgLoanBorrowed(double loan) {
		// TODO Auto-generated method stub
		
	}
	
	public void msgLoanPaid(double loanamount, double lendtime,double interestrate) {
		Do("Successfully paid off loan of:" + loanamount + " lendtime: " + lendtime +" days" + " interestrate: " + interestrate);
		
	}
	
	

	public boolean pickAndExecuteAnAction() {
		if(bankcustomerstate == state.openaccount)
		{
			mybankteller.msgOpenAccount();
			return true;
		}
		
		if(bankcustomerstate == state.deposit)
		{
			mybankteller.msgDepositIntoAccount(50);
			return true;
		}
		
		
		
		
		
		
		
		
		
		
		return false;
	}


	public void msgDepositIntoAccountDone() {
		// TODO Auto-generated method stub
		
	}


	
	
	
	
	
	
	
	
}
