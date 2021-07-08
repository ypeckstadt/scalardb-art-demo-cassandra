package com.ypeckstadt.service.account;

import com.scalar.db.api.DistributedStorage;
import com.ypeckstadt.dao.DaoException;
import com.ypeckstadt.dao.ScalarDbManager;
import com.ypeckstadt.dao.account.AccountDao;
import com.ypeckstadt.dao.account.AccountRecord;

public class AccountServiceForStorage extends AccountService {

    private final DistributedStorage storage;

    public AccountServiceForStorage(AccountDao accountDao, ScalarDbManager scalarDbManager) {
        super(accountDao);
        this.storage = scalarDbManager.getDistributedStorage();
    }

    @Override
    public AccountRecord create(String id) throws Exception {

        // Check if account exists
        if(accountDao.exists(id, storage)) {
            throw new Exception("the account already exists");
        }

        // Save new account
        accountDao.put(id, 0, System.currentTimeMillis(), storage);

        // Retrieve record and return
        return accountDao.get(id, storage);
    }

    @Override
    public AccountRecord charge(String id, int amount) throws Exception {

        AccountRecord accountRecord;

        // Retrieve account
        try {
            accountRecord = accountDao.get(id, storage);
        } catch (DaoException e) {
            throw new Exception("the account was not found");
        }

        // add funds to account
        int newBalance = accountRecord.getBalance() + amount;

        // update account
        try {
            accountDao.put(accountRecord.getId(), newBalance, accountRecord.getCreatedAt(), storage);
        } catch (DaoException e) {
            throw new Exception("something went wrong while trying to update the account");
        }

        // Retrieve updated record from database
        accountRecord = accountDao.get(id, storage);

        return accountRecord;
    }

    @Override
    public AccountRecord view(String id) throws Exception {
        try {
            AccountRecord accountRecord = accountDao.get(id, storage);
            if (accountRecord == null) {
                throw new Exception("the account could not be found");
            }
            return accountRecord;
        } catch (DaoException e) {
            throw  new Exception("something went wrong while trying to load the account");
        }
    }
}
