package com.ypeckstadt.dao.account;

import com.scalar.db.api.*;
import com.scalar.db.exception.storage.ExecutionException;
import com.scalar.db.exception.transaction.CrudException;
import com.scalar.db.io.BigIntValue;
import com.scalar.db.io.IntValue;
import com.scalar.db.io.Key;
import com.scalar.db.io.TextValue;
import com.ypeckstadt.dao.DaoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class AccountDao {

    private static final String TABLE_NAME = "account";
    private static final String NAMESPACE = "artDemo";
    private static final String PRIMARY_KEY_ID = "account_id";
    private static final String COL_NAME_BALANCE = "balance";
    private static final String COL_NAME_CREATED_AT = "created_at";
    private final Logger log = LoggerFactory.getLogger(this.getClass());


    public void put(String accountId, int balance, long createdAt, DistributedStorage storage) throws DaoException {
        Put put = createPutWith(accountId, balance, createdAt);
        try {
            storage.put(put);
        } catch (ExecutionException e) {
            String errorMsg = "error PUT : accountId" + accountId;
            throw new DaoException(errorMsg, e);
        }
        log.info("PUT completed for accountId " + accountId);
    }

    public void put(String accountId, int balance, long createdAt, DistributedTransaction transaction) throws DaoException {
        Put put = createPutWith(accountId, balance, createdAt);
        try {
            transaction.put(put);
        } catch (CrudException e) {
            String errorMsg = "error PUT : accountId" + accountId;
            throw new DaoException(errorMsg, e);
        }
        log.info("PUT completed for accountId " + accountId);
    }


    public AccountRecord get(String accountId, DistributedStorage storage) throws DaoException {
        AccountRecord record = null;
        Get get = createGetWith(accountId);
        try {
            Optional<Result> optResult = storage.get(get);

            if (optResult.isPresent()) {
                Result result = optResult.get();

                int balance = ((IntValue) result.getValue(COL_NAME_BALANCE).get()).get();
                long createdAt = ((BigIntValue) result.getValue(COL_NAME_CREATED_AT).get()).get();
                record = new AccountRecord(accountId, balance, createdAt);
            }
        } catch (ExecutionException e) {
            String errorMsg = "error GET " + accountId;
            throw new DaoException(errorMsg, e);
        }
        log.info("GET completed for" + record);
        return record;
    }

    public AccountRecord get(String accountId, DistributedTransaction transaction) throws DaoException {
        AccountRecord record = null;
        Get get = createGetWith(accountId);
        try {
            Optional<Result> optResult = transaction.get(get);

            if (optResult.isPresent()) {
                Result result = optResult.get();

                int balance = ((IntValue) result.getValue(COL_NAME_BALANCE).get()).get();
                long createdAt = ((BigIntValue) result.getValue(COL_NAME_CREATED_AT).get()).get();
                record = new AccountRecord(accountId, balance, createdAt);
            }
        } catch (CrudException e) {
            String errorMsg = "error GET " + accountId;
            throw new DaoException(errorMsg, e);
        }
        log.info("GET completed for" + record);
        return record;
    }

    public boolean exists(String accountId, DistributedStorage storage) throws DaoException {
        Get get = createGetWith(accountId);
        try {
            return storage.get(get).isPresent();
        } catch (ExecutionException e) {
            String errorMsg = "error GET " + accountId;
            throw new DaoException(errorMsg, e);
        }
    }

    public boolean exists(String accountId, DistributedTransaction transaction) throws DaoException {
        Get get = createGetWith(accountId);
        try {
            return transaction.get(get).isPresent();
        } catch (CrudException e) {
            String errorMsg = "error GET " + accountId;
            throw new DaoException(errorMsg, e);
        }
    }

    private Get createGetWith(String email) {
        Get get =
                new Get(new Key(new TextValue(PRIMARY_KEY_ID, email)))
                        .forNamespace(NAMESPACE)
                        .forTable(TABLE_NAME);
        return get;
    }

    private Put createPutWith(String accountId, int balance, long createdAt) {
        Put put =
                new Put(new Key(new TextValue(PRIMARY_KEY_ID, accountId)))
                        .forNamespace(NAMESPACE)
                        .forTable(TABLE_NAME)
                        .withValue(new IntValue(COL_NAME_BALANCE, balance))
                        .withValue(new BigIntValue(COL_NAME_CREATED_AT, createdAt));

        return put;
    }
}
