import java.util.ArrayList;
import java.util.List;
import java.util.InputMismatchException;
import java.util.Scanner;

// ============================
// TODO: Define Custom Exception Classes
// ============================

// Depositing a negative amount should raise an exception.
class NegativeDepositException extends Exception { 
    public NegativeDepositException(String errorMessage) {
        super(errorMessage);
    }
}
// Users should not be allowed to withdraw more than the available balance.
class OverdrawException extends Exception { 
    public OverdrawException(String errorMessage) {
        super(errorMessage);
    }
}
// Transactions on closed accounts should not be allowed.
class InvalidAccountOperationException extends Exception { 
    public InvalidAccountOperationException(String errorMessage) {
        super(errorMessage);
    }
}

// ============================
// Observer Pattern - Define Observer Interface
// ============================

interface Observer {
    void update(String message);
}

// TODO: Implement TransactionLogger class (Concrete Observer)
class TransactionLogger implements Observer {
    public void update(String message) {
        System.out.println(message);
    }
}

// ============================
// BankAccount (Subject in Observer Pattern)
// ============================
class BankAccount {
    protected String accountNumber;
    protected double balance;
    protected boolean isActive;
    private List<Observer> observers = new ArrayList<>();

    public BankAccount(String accNum, double initialBalance) {
        this.accountNumber = accNum;
        this.balance = initialBalance;
        this.isActive = true;
    }

    // Attach observer
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    // Notify observers (TODO: Implement in methods)
    private void notifyObservers(String message) {
        // TODO: Notify all observers when a transaction occurs
        for (Observer observer : this.observers) {
            observer.update(message);
        }
    }

    // Public method to notify observers
    protected void logTransaction(String message) {
        this.notifyObservers(message);
    }

    public void deposit(double amount) throws NegativeDepositException {
        // TODO: Implement exception handling for negative deposits
        try {
            if (amount < 0) {
                throw new NegativeDepositException("Cannot make a negative deposit! Please enter a positive amount.");
            }
            this.balance += amount;
            this.notifyObservers("Transaction made: +" + amount);
        }
        catch (NegativeDepositException e) {
            System.out.println(e.getMessage());
        }
    }

    public void withdraw(double amount) throws OverdrawException, InvalidAccountOperationException {
        // TODO: Implement exception handling for overdrawing and closed accounts
        try {
            if (amount > balance) {
                throw new OverdrawException("Withdrawal amount exceeds account balance! Please try again with a smaller amount.");
            }
            if (!this.isActive) {
                throw new InvalidAccountOperationException("Account is not active.");
            }
            this.balance -= amount;
            this.notifyObservers("Transaction made: -" + amount);
        }
        catch (OverdrawException e) {
            System.out.println(e.getMessage());
        }
        catch (InvalidAccountOperationException e) {
            System.out.println(e.getMessage());
        }
    }

    public double getBalance() {
        return balance;
    }

    public void closeAccount() {
        // TODO: Prevent further transactions when the account is closed
        this.isActive = false;
    }
}

// ============================
// Decorator Pattern - Define SecureBankAccount Class
// ============================

abstract class BankAccountDecorator extends BankAccount {
    protected BankAccount decoratedAccount;

    public BankAccountDecorator(BankAccount account) {
        super(account.accountNumber, account.getBalance());
        this.decoratedAccount = account;
    }
}

// TODO: Implement SecureBankAccount (Concrete Decorator)
class SecureBankAccount extends BankAccountDecorator {
    public SecureBankAccount(BankAccount account) {
        super(account);
    }

    public void withdraw(double amount) throws OverdrawException, InvalidAccountOperationException {
        // TODO: Implement exception handling for overdrawing and closed accounts
        try {
            if (amount > balance) {
                throw new OverdrawException("Withdrawal amount exceeds account balance! Please try again with a smaller amount.");
            }
            if (!super.isActive) {
                throw new InvalidAccountOperationException("Account is not active.");
            }
            if (amount > 500) {
                System.out.println("Cannot withdraw >500 dollars in one transaction! Please try again with a smaller amount.");
                return;
            }
            super.balance -= amount;
            super.logTransaction("Transaction made: -" + amount);
        }
        catch (OverdrawException e) {
            System.out.println(e.getMessage());
        }
        catch (InvalidAccountOperationException e) {
            System.out.println(e.getMessage());
        }
    }
}


// ============================
// Main Program
// ============================

public class BankAccountTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            // TODO: Ask the user to enter an initial balance and create a BankAccount object
            // Example: System.out.print("Enter initial balance: ");
            //          double initialBalance = scanner.nextDouble();
            //          BankAccount account = new BankAccount("123456", initialBalance);
            System.out.print("Enter initial balance: ");
            double initialBalance = scanner.nextDouble();
            BankAccount account = new BankAccount("123456", initialBalance);
            System.out.println("Bank Account Created: #123456");

            // TODO: Create a TransactionLogger and attach it to the account
            TransactionLogger logger = new TransactionLogger();
            account.addObserver(logger);

            // TODO: Wrap account in SecureBankAccount decorator
            SecureBankAccount secureAccount = new SecureBankAccount(account);

            // TODO: Allow the user to enter deposit and withdrawal amounts
            // deposit
            System.out.println("Enter amount to deposit: ");
            double depositAmount = scanner.nextDouble();
            secureAccount.deposit(depositAmount);
            // withdrawal
            System.out.println("Enter amount to withdraw: ");
            double withdrawalAmount = scanner.nextDouble();
            secureAccount.withdraw(withdrawalAmount);

            // TODO: Display the final balance
            System.out.println("Final Balance: " + secureAccount.getBalance());

        } catch (InputMismatchException e) {
            System.out.println("Please check your input and try again (needs to be a number).");
        } catch (Exception e) {
            System.out.println("An unforeseen error has occurred: " + e);
        } finally {
            scanner.close();
        }
    }
}