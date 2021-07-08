package com.ypeckstadt.service.art;

import com.google.protobuf.ServiceException;
import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.exception.transaction.CommitException;
import com.scalar.db.exception.transaction.UnknownTransactionStatusException;
import com.ypeckstadt.dao.DaoException;
import com.ypeckstadt.dao.ScalarDbManager;
import com.ypeckstadt.dao.account.AccountDao;
import com.ypeckstadt.dao.account.AccountRecord;
import com.ypeckstadt.dao.art.ArtDao;
import com.ypeckstadt.dao.art.ArtRecord;

public class ArtServiceForTransaction extends ArtService {

    private final DistributedTransactionManager transactionManager;

    public ArtServiceForTransaction(ArtDao artDao, AccountDao accountDao, ScalarDbManager scalarDbManager) {
        super(artDao, accountDao);
        this.transactionManager = scalarDbManager.getDistributedTransactionManager();
    }

    @Override
    public ArtRecord create(String id, String accountId, int price) throws Exception {

        // start the transaction
        DistributedTransaction transaction = transactionManager.start();

        try {
            // Check if art exists
            if (artDao.exists(id, transaction)) {
                transaction.abort();
                throw new Exception("the art already exists");
            }

            // Check if account exists
            if (!accountDao.exists(accountId, transaction)) {
                transaction.abort();
                throw new Exception("the account does not exist");
            }

            // Save new art
            artDao.put(id, accountId, price, System.currentTimeMillis(), transaction);

            // Commit transaction
            transaction.commit();
        } catch (CommitException | DaoException e) {
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
    public ArtRecord changeOwner(String id, String accountId) throws Exception {

        // start the transaction
        DistributedTransaction transaction = transactionManager.start();

        try {
            // check if the art exists
            ArtRecord artRecord = artDao.get(id, transaction);
            if (artRecord == null) {
                transaction.abort();
                throw new Exception("the art does not exist");
            }

            // check if new owner exists
            if (!accountDao.exists(accountId, transaction)) {
                transaction.abort();
                throw new Exception("the account of the new owner does not exist");
            }

            // update the art
            artDao.put(id, accountId, artRecord.getPrice(), artRecord.getCreatedAt(), transaction);

            // Commit transaction
            transaction.commit();
        } catch (CommitException | DaoException e) {
            transaction.abort();
            throw new ServiceException("Could not insert insert account " + id + " in database", e);
        } catch (UnknownTransactionStatusException e) {
            throw new UnknownTransactionStatusException(
                    "Error : the transaction to insert the account is in an unknown state", e);
        }

        return view(id);
    }

    @Override
    public ArtRecord view(String id) throws Exception {
        DistributedTransaction transaction = transactionManager.start();

        ArtRecord artRecord;
        try {
            artRecord = artDao.get(id, transaction);
            if (artRecord == null) {
                transaction.abort();
                throw new Exception("the art does not exist");
            }
            transaction.commit();
        } catch (CommitException | DaoException e) {
            transaction.abort();
            throw new ServiceException("Could not retrieve data of art " + id, e);
        } catch (UnknownTransactionStatusException e) {
            throw new UnknownTransactionStatusException(
                    "Error : the transaction to retrieve the art is in an unknown state", e);
        }

        return artRecord;
    }

    @Override
    public ArtRecord purchase(String id, String accountId) throws Exception {

        // start the transaction
        DistributedTransaction transaction = transactionManager.start();

        try {

            // check if the art exists
            ArtRecord artRecord = artDao.get(id, transaction);
            if (artRecord == null) {
                transaction.abort();
                throw new Exception("the art does not exist");
            }

            // check if new owner exists
            AccountRecord accountRecord = accountDao.get(accountId, transaction);
            if (accountRecord == null) {
                transaction.abort();
                throw new Exception("the account of the new owner does not exist");
            }

            // check if account has sufficient funds on balance
            if (accountRecord.getBalance() < artRecord.getPrice()) {
                transaction.abort();
                throw new Exception("the buyer's account has insufficient funds");
            }

            // update art, change owner
            artDao.put(id, accountId, artRecord.getPrice(), artRecord.getCreatedAt(), transaction);

            // update account, update balance
            accountDao.put(accountId, accountRecord.getBalance() - artRecord.getPrice(), accountRecord.getCreatedAt(), transaction);

            // Commit transaction
            transaction.commit();
        } catch (CommitException | DaoException e) {
            transaction.abort();
            throw new ServiceException("Could not insert insert account " + id + " in database", e);
        } catch (UnknownTransactionStatusException e) {
            throw new UnknownTransactionStatusException(
                    "Error : the transaction to insert the account is in an unknown state", e);
        }
        return view(id);
    }
}
