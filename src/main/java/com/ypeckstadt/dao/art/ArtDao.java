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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ArtDao {

    private static final String TABLE_NAME = "art";
    private static final String NAMESPACE = "artDemo";
    private static final String PRIMARY_KEY_ID = "account_id";
    private static final String CLUSTERING_KEY_ID = "art_id";
    private static final String COL_NAME_PRICE = "price";
    private static final String COL_NAME_CREATED_AT = "created_at";
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public boolean exists(String accountId, String artId, DistributedStorage storage) throws DaoException {
        Get get = createGetWith(accountId, artId);
        try {
            return storage.get(get).isPresent();
        } catch (ExecutionException e) {
            String errorMsg = "error GET " + artId;
            throw new DaoException(errorMsg, e);
        }
    }

    public boolean exists(String accountId, String artId, DistributedTransaction transaction) throws DaoException {
        Get get = createGetWith(accountId, artId);
        try {
            return transaction.get(get).isPresent();
        } catch (CrudException e) {
            String errorMsg = "error GET " + artId;
            throw new DaoException(errorMsg, e);
        }
    }

    public void delete(String accountId, String artId, DistributedStorage storage) throws  DaoException {
        Delete delete = createDeleteWith(accountId, artId);
        try {
            storage.delete(delete);
        } catch (ExecutionException e) {
            String errorMsg = "error DELETE : account" + accountId + " art " + artId;
            throw new DaoException(errorMsg, e);
        }
    }

    public void delete(String accountId, String artId, DistributedTransaction transaction) throws  DaoException {
        Delete delete = createDeleteWith(accountId, artId);
        try {
            transaction.delete(delete);
        } catch (CrudException e) {
            String errorMsg = "error DELETE : account" + accountId + " art " + artId;
            throw new DaoException(errorMsg, e);
        }
    }

    public void put(String artId, String accountId, int price, long createdAt, DistributedStorage storage) throws DaoException {

        Put put = createPutWith(accountId, artId, price, createdAt);
        try {
            storage.put(put);
        } catch (ExecutionException e) {
            String errorMsg = "error PUT : artId" + artId;
            throw new DaoException(errorMsg, e);
        }
        log.info("PUT completed for artId " + artId);
    }

    public void put(String artId, String accountId, int price, long createdAt, DistributedTransaction transaction) throws DaoException {
        Put put = createPutWith(accountId, artId, price, createdAt);
        try {
            transaction.put(put);
        } catch (CrudException e) {
            String errorMsg = "error PUT : artId" + artId;
            throw new DaoException(errorMsg, e);
        }
        log.info("PUT completed for artId " + artId);
    }

    public ArtRecord get(String accountId, String artId, DistributedStorage storage) throws DaoException {
        ArtRecord record = null;
        Get get = createGetWith(accountId, artId);
        try {
            Optional<Result> optResult = storage.get(get);

            if (optResult.isPresent()) {
                Result result = optResult.get();
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

    public ArtRecord get(String accountId, String artId, DistributedTransaction transaction) throws DaoException {
        ArtRecord record = null;
        Get get = createGetWith(accountId, artId);
        try {
            Optional<Result> optResult = transaction.get(get);

            if (optResult.isPresent()) {
                Result result = optResult.get();

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

    /**
     * List all art owned by an account
     *
     * @param accountId
     * @param storage
     * @return
     * @throws DaoException
     */
    public List<ArtRecord> list(String accountId, DistributedStorage storage)
            throws DaoException {
        List<ArtRecord> art = new ArrayList<>();
        Scan scan = createScanWith(accountId);
        try {
            Scanner scanner = storage.scan(scan);
            for (Result result : scanner) {
                art.add(mapResult(result, accountId));
            }
        } catch (ExecutionException e) {
            throw new DaoException("error SCAN " + accountId, e);
        }
        log.info("SCAN completed for " + accountId);
        return art;
    }

    private Scan createScanWith(String accountId) {
        Scan scan =
                new Scan(
                        new Key(new TextValue(PRIMARY_KEY_ID, accountId)))
                        .forNamespace(NAMESPACE)
                        .forTable(TABLE_NAME)
                        .withOrdering(
                                new Scan.Ordering(CLUSTERING_KEY_ID, Scan.Ordering.Order.ASC));
        return scan;
    }

    private ArtRecord mapResult(Result result, String accountId) {
        String artId = ((TextValue) result.getValue(CLUSTERING_KEY_ID).get()).getString().get();
        int price = ((IntValue) result.getValue(COL_NAME_PRICE).get()).get();
        long createdAt = ((BigIntValue) result.getValue(COL_NAME_CREATED_AT).get()).get();
        return new ArtRecord(artId, accountId, price, createdAt);
    }

    private Get createGetWith(String accountId, String artId) {
        return new Get(new Key(new TextValue(PRIMARY_KEY_ID, accountId)), new Key(new TextValue(CLUSTERING_KEY_ID, artId)))
                .forNamespace(NAMESPACE)
                .forTable(TABLE_NAME);
    }

    private Put createPutWith(String accountId, String artId, int price, long createdAt) {
        return new Put(new Key(new TextValue(PRIMARY_KEY_ID, accountId)), new Key(new TextValue(CLUSTERING_KEY_ID, artId)))
                .forNamespace(NAMESPACE)
                .forTable(TABLE_NAME)
                .withValue(new IntValue(COL_NAME_PRICE, price))
                .withValue(new BigIntValue(COL_NAME_CREATED_AT, createdAt));
    }

    private Delete createDeleteWith(String accountId, String artId) {
        return new Delete(new Key(new TextValue(PRIMARY_KEY_ID, accountId)), new Key(new TextValue(CLUSTERING_KEY_ID, artId)))
                .forNamespace(NAMESPACE)
                .forTable(TABLE_NAME);
    }
}
