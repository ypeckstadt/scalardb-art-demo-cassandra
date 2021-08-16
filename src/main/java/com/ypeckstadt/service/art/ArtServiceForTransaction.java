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

import java.util.List;

public class ArtServiceForTransaction extends ArtService {

    private final DistributedTransactionManager transactionManager;

    public ArtServiceForTransaction(ArtDao artDao, AccountDao accountDao, ScalarDbManager scalarDbManager) {
        super(artDao, accountDao);
        this.transactionManager = scalarDbManager.getDistributedTransactionManager();
    }

    @Override
    public ArtRecord create(String artId, String accountId, int price) throws Exception {

        // start the transaction
        DistributedTransaction transaction = transactionManager.start();

        try {
            // Check if art exists
            if (artDao.exists(accountId, artId, transaction)) {
                transaction.abort();
                throw new Exception("the art already exists");
            }

            // Check if account exists
            if (!accountDao.exists(accountId, transaction)) {
                transaction.abort();
                throw new Exception("the account does not exist");
            }

            // Save new art
            artDao.put(artId, accountId, price, System.currentTimeMillis(), transaction);

            // Commit transaction
            transaction.commit();
        } catch (CommitException | DaoException e) {
            transaction.abort();
            throw new ServiceException("Could not insert insert account " + artId + " in database", e);
        } catch (UnknownTransactionStatusException e) {
            throw new UnknownTransactionStatusException(
                    "Error : the transaction to insert the account is in an unknown state", e);
        }

        // Retrieve record and return
        return view(accountId, artId);
    }

    @Override
    public ArtRecord changeOwner(String currentOwnerAccountId, String newOwnerAccountId, String artId) throws Exception {

        // start the transaction
        DistributedTransaction transaction = transactionManager.start();

        try {
            // check if the art exists
            ArtRecord artRecord = artDao.get(currentOwnerAccountId, artId, transaction);
            if (artRecord == null) {
                transaction.abort();
                throw new Exception("the art does not exist");
            }

            // check if new owner exists
            if (!accountDao.exists(newOwnerAccountId, transaction)) {
                transaction.abort();
                throw new Exception("the account of the new owner does not exist");
            }

            // remove current key (art partition key is the account id)
            artDao.delete(currentOwnerAccountId, artId, transaction);

            // update the art
            artDao.put(artId, newOwnerAccountId, artRecord.getPrice(), artRecord.getCreatedAt(), transaction);

            // Commit transaction
            transaction.commit();
        } catch (CommitException | DaoException e) {
            transaction.abort();
            throw new ServiceException("Could not insert insert account " + artId + " in database", e);
        } catch (UnknownTransactionStatusException e) {
            throw new UnknownTransactionStatusException(
                    "Error : the transaction to insert the account is in an unknown state", e);
        }

        return view(newOwnerAccountId, artId);
    }

    @Override
    public ArtRecord view(String accountId, String artId) throws Exception {
        DistributedTransaction transaction = transactionManager.start();

        ArtRecord artRecord;
        try {
            artRecord = artDao.get(accountId, artId, transaction);
            if (artRecord == null) {
                transaction.abort();
                throw new Exception("the art does not exist");
            }
            transaction.commit();
        } catch (CommitException | DaoException e) {
            transaction.abort();
            throw new ServiceException("Could not retrieve data of art " + artId, e);
        } catch (UnknownTransactionStatusException e) {
            throw new UnknownTransactionStatusException(
                    "Error : the transaction to retrieve the art is in an unknown state", e);
        }

        return artRecord;
    }

    @Override
    public ArtRecord purchase(String artId, String buyerAccountId, String sellerAccountId) throws Exception {

        // start the transaction
        DistributedTransaction transaction = transactionManager.start();

        try {

            // check if the art exists
            ArtRecord artRecord = artDao.get(sellerAccountId, artId, transaction);
            if (artRecord == null) {
                transaction.abort();
                throw new Exception("the art does not exist");
            }

            // check if new owner exists
            AccountRecord accountRecord = accountDao.get(buyerAccountId, transaction);
            if (accountRecord == null) {
                transaction.abort();
                throw new Exception("the account of the new owner does not exist");
            }

            // check if account has sufficient funds on balance
            if (accountRecord.getBalance() < artRecord.getPrice()) {
                transaction.abort();
                throw new Exception("the buyer's account has insufficient funds");
            }

            // remove current key (art partition key is the account id)
            artDao.delete(sellerAccountId, artId, transaction);

            // update art, change owner
            artDao.put(artId, buyerAccountId, artRecord.getPrice(), artRecord.getCreatedAt(), transaction);

            // update account, update balance
            accountDao.put(buyerAccountId, accountRecord.getBalance() - artRecord.getPrice(), accountRecord.getCreatedAt(), transaction);

            // Commit transaction
            transaction.commit();
        } catch (CommitException | DaoException e) {
            transaction.abort();
            throw new ServiceException("Could not insert insert account " + artId + " in database", e);
        } catch (UnknownTransactionStatusException e) {
            throw new UnknownTransactionStatusException(
                    "Error : the transaction to insert the account is in an unknown state", e);
        }
        return view(buyerAccountId, artId);
    }

    @Override
    public List<ArtRecord> list(String accountId) throws Exception {
        return null;
    }
}
