package it.unibo.oop.lab.exception2;

/**
 * Class modeling a BankAccount with strict policies: getting money is allowed
 * only with enough founds, and there are also a limited number of free ATM
 * transaction (this number is provided as a input in the constructor).
 * 
 */
public class StrictBankAccount implements BankAccount {

    private final int usrID;
    private double balance;
    private int nTransactions;
    private int nAtmTransactions;
    private final int nMaxATMTransactions;
    private static final double ATM_TRANSACTION_FEE = 1;
    private static final double MANAGEMENT_FEE = 5;
    private static final double TRANSACTION_FEE = 0.1;

    /**
     * 
     * @param usrID
     *            user id
     * @param balance
     *            initial balance
     * @param nMaxATMTransactions
     *            max no of ATM transactions allowed
     */
    public StrictBankAccount(final int usrID, final double balance, final int nMaxATMTransactions) {
        this.usrID = usrID;
        this.balance = balance;
        this.nMaxATMTransactions = nMaxATMTransactions;
    }

    /**
     * 
     * @param usrID
     *            id of the user requesting this operation
     * @param amount
     *            amount to be credited
     * 
     * @throws WrongAccountHolderException
     *             if an unauthorized user tries to withdraw
     */
    public void deposit(final int usrID, final double amount) {
        if (checkUser(usrID)) {
            this.balance += amount;
            incTransactions();
        } else {
            throw new WrongAccountHolderException();
        }
    }

    /**
     * @param usrID
     *            id of the user requesting this operation
     * @param amount
     *            amount to be withdrawn
     * 
     * @throws WrongAccountHolderException
     *             if an unauthorized user tries to withdraw
     * @throws NotEnoughFoundsException
     *             if the balance is less than the amount to withdraw
     */
    public void withdraw(final int usrID, final double amount) {
        if (checkUser(usrID)) {
            if (isWithdrawAllowed(amount)) {
                this.balance -= amount;
                incTransactions();
            } else {
                throw new NotEnoughFoundsException();
            }

        } else {
            throw new WrongAccountHolderException();
        }
    }

    /**
     * 
     * @param usrID
     *            id of the user requesting this opera
     * @param amount
     *            amount to be deposited via ATM
     * 
     * @throws WrongAccountHolderException
     *             if an unauthorized user tries to withdraw
     * @throws TransactionsOverQuotaException
     *             max no. of ATM transaction reached
     */
    public void depositFromATM(final int usrID, final double amount) {
        if (nAtmTransactions < nMaxATMTransactions) {
            this.deposit(usrID, amount - StrictBankAccount.ATM_TRANSACTION_FEE);
            nAtmTransactions++;
        } else {
            throw new TransactionsOverQuotaException();
        }
    }

    /**
     * 
     * @param usrID
     *            id of the user requesting this opera
     * @param amount
     *            amount to be withdrawn via AT
     * 
     * @throws WrongAccountHolderException
     *             if an unauthorized user tries to withdraw
     * @throws TransactionsOverQuotaException
     *             max no. of ATM transaction reached
     * @throws NotEnoughFoundsException
     *             if not enough funds are available
     */
    public void withdrawFromATM(final int usrID, final double amount) {
        if (nAtmTransactions < nMaxATMTransactions) {
            this.withdraw(usrID, amount + StrictBankAccount.ATM_TRANSACTION_FEE);
            nAtmTransactions++;
        } else {
            throw new TransactionsOverQuotaException();
        }
    }

    /**
     * 
     * @return The current balance
     */
    public double getBalance() {
        return this.balance;
    }

    /**
     * 
     * @return The total number of transaction for the account
     */
    public int getNTransactions() {
        return nTransactions;
    }

    /**
     * 
     * @return The total number of ATM transactions for the account
     */
    public int getNAtmTransactions() {
        return nAtmTransactions;
    }

    /**
     * 
     * @param usrID
     *            id of the user related to these fees
     */
    public void computeManagementFees(final int usrID) {
        final double feeAmount = MANAGEMENT_FEE + (nTransactions * StrictBankAccount.TRANSACTION_FEE);
        if (checkUser(usrID) && isWithdrawAllowed(feeAmount)) {
            balance -= MANAGEMENT_FEE + nTransactions * StrictBankAccount.TRANSACTION_FEE;
            nTransactions = 0;
            nAtmTransactions = 0;
        }
    }

    private boolean checkUser(final int id) {
        return this.usrID == id;
    }

    private boolean isWithdrawAllowed(final double amount) {
        return balance > amount;
    }

    private void incTransactions() {
        nTransactions++;
    }

}
