package Role;
import test.mock.EventLog;
import test.mock.LoggedEvent;
import city.Bank;
import city.account;
import city.gui.Bank.BankTellerRoleGui;
import Role.BankCustomerRole;
import city.PersonAgent;

public class BankTellerRole extends Role {
        

                public BankCustomerRole currentcustomer;//since bank teller serves one customer at a time. no list is necessary
                //BankTellerRole banktellerrole;
                public int currentcustomeraccountnumber;
                String name;
                public double deposit;
                public double loan;
                public double withdrawal;
                double paybackloan;
                public BankManagerRole bankmanager;
                enum state {openaccount, depositintoaccount, withdrawfromaccount, getloan, paybackloan, customerleft};
                state banktellerstate;
                BankTellerRoleGui gui;
                public EventLog log = new EventLog();
                PersonAgent person;
                
                
                public BankTellerRole(/*BankTellerRole assignbanktellerrole,*/ BankManagerRole assignbankmanager)
                {
                        super();
                        
                        //this.banktellerrole = assignbanktellerrole;
                        this.bankmanager = assignbankmanager;
                
                }
                
                                
                public void msgAssignMeCustomer(BankCustomerRole customer)
                {
                        currentcustomer = customer;
                        currentcustomeraccountnumber = currentcustomer.bankaccountnumber;
                        person.stateChanged();
                }

                public void msgOpenAccount() 
                {
                        
                        log.add(new LoggedEvent("msgOpenAccount"));
                        banktellerstate = state.openaccount;
                        person.stateChanged();
                }

                public void msgDepositIntoAccount(double deposit)
                {
                        log.add(new LoggedEvent("msgDepositIntoAccount"));
                        this.deposit = deposit;
                        banktellerstate = state.depositintoaccount;
                        person.stateChanged();
                }

                public void msgWithdrawFromAccount(double withdrawal)
                {
                        log.add(new LoggedEvent("msgWithdrawFromAccount"));
                        this.withdrawal = withdrawal;
                        banktellerstate = state.withdrawfromaccount;
                        person.stateChanged();
                }

                public void msgGetLoan(double loan)
                {
                        log.add(new LoggedEvent("msgGetLoan"));
                        this.loan = loan;
                        banktellerstate = state.getloan;
                        person.stateChanged();
                }
                
                public void msgPayBackLoan(double paybackloan)
                {
                        this.paybackloan = paybackloan;
                        banktellerstate = state.paybackloan;
                        person.stateChanged();
                }
                
                public void msgBankCustomerLeaving()
                {
                        log.add(new LoggedEvent("msgBankCustomerLeaving"));
                        banktellerstate = state.customerleft;
                        person.stateChanged();
                }


        public boolean pickAndExecuteAnAction() {
                
                
                if(banktellerstate == state.openaccount)
                {
                        
                    bankmanager.bank.accounts.add(new account(currentcustomer, bankmanager.bank.uniqueaccountnumber));
                    currentcustomeraccountnumber = bankmanager.bank.uniqueaccountnumber;
                    currentcustomer.msgOpenAccountDone(currentcustomeraccountnumber);
                    bankmanager.bank.uniqueaccountnumber++;
                        return true;
                }

                if(banktellerstate == state.depositintoaccount)
                {
                        for(account findaccount: bankmanager.bank.accounts)
                        {
                                if(findaccount.accountnumber == currentcustomeraccountnumber)
                                {        
                                        //System.out.println("accout number = "+currentcustomeraccountnumber);
                                        //System.out.println("amount to deposit ="+this.deposit);
                                        log.add(new LoggedEvent("deposit!"));
                                        findaccount.balance += this.deposit;
                                        currentcustomer.msgDepositIntoAccountDone(this.deposit);
                                        break;
                                }
                        }
                        return true;
                }

                if(banktellerstate == state.withdrawfromaccount)
                {
                        for(account findaccount: bankmanager.bank.accounts)
                        {
                                if(findaccount.accountnumber == currentcustomeraccountnumber)
                                {        
                                        if(!(findaccount.balance < withdrawal))
                                        {
                                                findaccount.balance -= this.withdrawal;
                                                currentcustomer.msgHereIsYourWithdrawal(withdrawal);
                                        break;
                                        }
                                        else
                                        {
                                                currentcustomer.msgWithdrawalFailed();
                                        }
                                        
                                }
                        }
                        return true;
                }


                if(banktellerstate == state.getloan)
                {
                        for(account findaccount: bankmanager.bank.accounts)
                        {
                                if(findaccount.accountnumber == currentcustomeraccountnumber)
                                {        
                                
                                        if(findaccount.loan + loan > 50)
                                        {
                                                
                                                currentcustomer.msgCannotGetLoan(loan);
                                        }
                                        else
                                        {
                                                findaccount.loan += this.loan;
                                                currentcustomer.msgLoanBorrowed(loan);
                                        }
                                        
                        
                                }
                        }
                        return true;
                        
                        /*
                        for(account findaccount: bank.accounts)
                        {
        
                                if(findaccount.accountnumber == currentcustomeraccountnumber)
                                {        
                                        if(!(findaccount.calculatetotalloan() > 60))
                                        {
                                                findaccount.addloan(loan);
                                                currentcustomer.msgLoanBorrowed(loan);
                                                break;
                                        }
                                        else
                                        {
                                                currentcustomer.msgCannotGetLoan(loan);
                                        }
                                }
                                
                        }
                        */
                                
                }
                
                
                if(banktellerstate == state.paybackloan)
                {
                        for(account findaccount: bankmanager.bank.accounts)
                        {
                                if(findaccount.accountnumber == currentcustomeraccountnumber)
                                {        
                                        double oldestloanamount;
                                        double subtotal;
                                        
                                        //60, loan 1 = 20, loan 2 30;
                                        do
                                        {
                                                
                                                oldestloanamount = findaccount.loans.get(0).loanamount;
                                                subtotal = oldestloanamount - paybackloan;
                                                if(subtotal <= 0)
                                                {
                                                        findaccount.loans.remove(0);
                                                        currentcustomer.msgLoanPaid(findaccount.loans.get(0).loanamount,findaccount.loans.get(0).lendtime, findaccount.loans.get(0).interestrate);
                                                }
                                                subtotal *= -1;
                                                paybackloan = subtotal;        
                                                        
                                        }while(paybackloan == 0 || findaccount.loans.size() == 0);
                        
                                }
                        }
                        
                }
                
                
                if(banktellerstate == state.customerleft)
                {
                        bankmanager.msgCustomerLeft(currentcustomer, this);
                        this.currentcustomer = null;
                        return true;
                        
                }
                
                
                
                return false;
}
        

		public void setGui(BankTellerRoleGui setgui) {
			
			this.gui = setgui;
		}

		public String getName() {
			return this.name;
		}
        
		public void setPerson(PersonAgent setperson) {
			this.person = setperson;
		}
        
        
        
        
}