package com.ypeckstadt.dao.art;

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

public class ArtDao {

    private static final String TABLE_NAME = "art";
    private static final String NAMESPACE = "artDemo";
    private static final String PRIMARY_KEY_ID = "art_id";
    private static final String COL_NAME_ACCOUNT = "account_id";
    private static final String COL_NAME_PRICE = "price";
    private static final String COL_NAME_CREATED_AT = "created_at";
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public boolean exists(String artId, DistributedStorage storage) throws DaoException {
        Get get = createGetWith(artId);
        try {
            return storage.get(get).isPresent();
        } catch (ExecutionException e) {
            String errorMsg = "error GET " + artId;
            throw new DaoException(errorMsg, e);
        }
    }

    public boolean exists(String artId, DistributedTransaction transaction) throws DaoException {
        Get get = createGetWith(artId);
        try {
            return transaction.get(get).isPresent();
        } catch (CrudException e) {
            String errorMsg = "error GET " + artId;
            throw new DaoException(errorMsg, e);
        }
    }

    public void put(String artId, String accountId, int price, long createdAt, DistributedStorage storage) throws DaoException {
        Put put = createPutWith(artId, accountId, price, createdAt);
        try {
            storage.put(put);
        } catch (ExecutionException e) {
            String errorMsg = "error PUT : artId" + artId;
            throw new DaoException(errorMsg, e);
        }
        log.info("PUT completed for artId " + artId);
    }

    public void put(String artId, String accountId, int price, long createdAt, DistributedTransaction transaction) throws DaoException {
        Put put = createPutWith(artId, accountId, price, createdAt);
        try {
            transaction.put(put);
        } catch (CrudException e) {
            String errorMsg = "error PUT : artId" + artId;
            throw new DaoException(errorMsg, e);
        }
        log.info("PUT completed for artId " + artId);
    }

    public ArtRecord get(String artId, DistributedStorage storage) throws DaoException {
        ArtRecord record = null;
        Get get = createGetWith(artId);
        try {
            Optional<Result> optResult = storage.get(get);

            if (optResult.isPresent()) {
                Result result = optResult.get();

                String accountId = ((TextValue) result.getValue(COL_NAME_ACCOUNT).get()).getString().get();
                int price = ((IntValue) result.getValue(COL_NAME_PRICE).get()).get();
                long createdAt = ((BigIntValue) result.getValue(COL_NAME_CREATED_AT).get()).get();
                record = new ArtRecord(artId, accountId, price, createdAt);
            }
        } catch (ExecutionException e) {
            String errorMsg = "error GET " + artId;
            throw new DaoException(errorMsg, e);
        }
        log.info("GET completed for" + record);
        return record;
    }

    public ArtRecord get(String artId, DistributedTransaction transaction) throws DaoException {
        ArtRecord record = null;
        Get get = createGetWith(artId);
        try {
            Optional<Result> optResult = transaction.get(get);

            if (optResult.isPresent()) {
                Result result = optResult.get();

                String accountId = ((TextValue) result.getValue(COL_NAME_ACCOUNT).get()).getString().get();
                int price = ((IntValue) result.getValue(COL_NAME_PRICE).get()).get();
                long createdAt = ((BigIntValue) result.getValue(COL_NAME_CREATED_AT).get()).get();
                record = new ArtRecord(artId, accountId, price, createdAt);
            }
        } catch (CrudException e) {
            String errorMsg = "error GET " + artId;
            throw new DaoException(errorMsg, e);
        }
        log.info("GET completed for" + record);
        return record;
    }

    private Get createGetWith(String id) {
        Get get =
                new Get(new Key(new TextValue(PRIMARY_KEY_ID, id)))
                        .forNamespace(NAMESPACE)
                        .forTable(TABLE_NAME);
        return get;
    }

    private Put createPutWith(String artId, String accountId, int price, long createdAt) {
        Put put =
                new Put(new Key(new TextValue(PRIMARY_KEY_ID, artId)))
                        .forNamespace(NAMESPACE)
                        .forTable(TABLE_NAME)
                        .withValue(new TextValue(COL_NAME_ACCOUNT, accountId))
                        .withValue(new IntValue(COL_NAME_PRICE, price))
                        .withValue(new BigIntValue(COL_NAME_CREATED_AT, createdAt));

        return put;
    }
}
