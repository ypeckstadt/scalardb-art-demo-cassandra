package com.ypeckstadt.service.account;

import com.google.protobuf.ServiceException;
import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.exception.transaction.CommitException;
import com.scalar.db.exception.transaction.UnknownTransactionStatusException;
import com.ypeckstadt.dao.DaoException;
import com.ypeckstadt.dao.ScalarDbManager;
import com.ypeckstadt.dao.account.AccountDao;
import com.ypeckstadt.dao.account.AccountRecord;

public class AccountServiceForTransaction extends AccountService {

    private final DistributedTransactionManager transactionManager;

    public AccountServiceForTransaction(AccountDao accountDao, ScalarDbManager scalarDbManager) {
        super(accountDao);
        this.transactionManager = scalarDbManager.getDistributedTransactionManager();
    }

    @Override
    public AccountRecord create(String id) throws Exception {

        // start the transaction
        DistributedTransaction transaction = transactionManager.start();

        try {
            // Check if account exists
            if (accountDao.exists(id, transaction)) {
                transaction.abort();
                throw new Exception("the account already exists");
            }

            // Save new account
            accountDao.put(id, 0, System.currentTimeMillis(), transaction);

            // Commit transaction
            transaction.commit();
        } catch (CommitException e) {
            transaction.abort();
            throw new ServiceException("Could not insert insert account " + id + " in database", e);
        } catch (UnknownTransactionStatusException e) {
            throw new UnknownTransactionStatusException(
                    "Error : the transaction to insert the account is in an unknown state", e);
        }

        // Retrieve record and return
        return view(id);
    }

    @Override
    public AccountRecord charge(String id, int amount) throws Exception {

        // start the transaction
        DistributedTransaction transaction = transactionManager.start();

        try {
            // Retrieve account
            AccountRecord accountRecord = accountDao.get(id, transaction);
            if (accountRecord == null) {
                transaction.abort();
                throw new Exception("the account was not found");
            }
            // add funds to account
            int newBalance = accountRecord.getBalance() + amount;

            // update account
            accountDao.put(accountRecord.getId(), newBalance, accountRecord.getCreatedAt(), transaction);

            // Commit transaction
            transaction.commit();
        } catch (CommitException e) {
            transaction.abort();
            throw new ServiceException("Could not charge account " + id + " in database", e);
        } catch (UnknownTransactionStatusException e) {
            throw new UnknownTransactionStatusException(
                    "Error : the transaction to charge the account is in an unknown state", e);
        }

        // retrieve and return
        return view(id);
    }

    @Override
    public AccountRecord view(String id) throws Exception {
        DistributedTransaction transaction = transactionManager.start();

        AccountRecord accountRecord;
        try {
            accountRecord = accountDao.get(id, transaction);
            if (accountRecord == null) {
                transaction.abort();
                throw new Exception("the account does not exist");
            }
            transaction.commit();
        } catch (CommitException | DaoException e) {
            transaction.abort();
            throw new ServiceException("Could not retrieve data of account " + id, e);
        } catch (UnknownTransactionStatusException e) {
            throw new UnknownTransactionStatusException(
                    "Error : the transaction to retrieve the account is in an unknown state", e);
        }

        return accountRecord;
    }
}
