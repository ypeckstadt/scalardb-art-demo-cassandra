package com.ypeckstadt.service.art;

import com.scalar.db.api.DistributedStorage;
import com.ypeckstadt.dao.DaoException;
import com.ypeckstadt.dao.ScalarDbManager;
import com.ypeckstadt.dao.account.AccountDao;
import com.ypeckstadt.dao.account.AccountRecord;
import com.ypeckstadt.dao.art.ArtDao;
import com.ypeckstadt.dao.art.ArtRecord;

public class ArtServiceForStorage extends ArtService {

    private final DistributedStorage storage;

    public ArtServiceForStorage(ArtDao artDao, AccountDao accountDao, ScalarDbManager scalarDbManager) {
        super(artDao, accountDao);
        this.storage = scalarDbManager.getDistributedStorage();
    }

    @Override
    public ArtRecord create(String id, String accountId, int price) throws Exception {

        // Check if art exists
        if (artDao.exists(id, storage)) {
            throw new Exception("the art already exists");
        }

        // Check if account exists
        if (!accountDao.exists(accountId, storage)) {
            throw new Exception("the account does not exist");
        }

        // Save new art
        artDao.put(id, accountId, price, System.currentTimeMillis(), storage);

        // Retrieve record and return
        return artDao.get(id, storage);
    }

    @Override
    public ArtRecord changeOwner(String id, String accountId) throws Exception {
        try {
            // check if the art exists
            ArtRecord artRecord = artDao.get(id, storage);
            if (artRecord == null) {
                throw new Exception("the art does not exist");
            }

            // check if new owner exists
            if (!accountDao.exists(accountId, storage)) {
                throw new Exception("the account of the new owner does not exist");
            }

            // update the art
            artDao.put(id, accountId, artRecord.getPrice(), artRecord.getCreatedAt(), storage);

            // retrieve and return
            return artDao.get(id, storage);
        } catch (DaoException e) {
            throw new Exception("something went wrong while trying to change the art's owner");
        }
    }

    @Override
    public ArtRecord view(String id) throws Exception {
        try {
            ArtRecord artRecord = artDao.get(id, storage);
            if (artRecord == null) {
                throw new Exception("the art could not be found");
            }
            return artRecord;
        } catch (DaoException e) {
            throw  new Exception("something went wrong while trying to load the art");
        }
    }

    @Override
    public ArtRecord purchase(String id, String accountId) throws  Exception {

        try {
            // check if the art exists
            ArtRecord artRecord = artDao.get(id, storage);
            if (artRecord == null) {
                throw new Exception("the art does not exist");
            }

            // check if new owner exists
            AccountRecord accountRecord = accountDao.get(accountId, storage);
            if (accountRecord == null) {
                throw new Exception("the account of the new owner does not exist");
            }

            // check if account has sufficient funds on balance
            if (accountRecord.getBalance() < artRecord.getPrice()) {
                throw new Exception("the buyer's account has insufficient funds");
            }

            // update art, change owner
            artDao.put(id, accountId, artRecord.getPrice(), artRecord.getCreatedAt(), storage);

            // update account, update balance
            accountDao.put(accountId, accountRecord.getBalance() - artRecord.getPrice(), accountRecord.getCreatedAt(), storage);

            // retrieve and return art
            return artDao.get(id, storage);
        } catch (DaoException e) {
            throw  new Exception("something went wrong while trying to purchase the art");
        }
    }
}
