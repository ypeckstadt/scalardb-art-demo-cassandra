package com.ypeckstadt.service.art;

import com.ypeckstadt.dao.account.AccountDao;
import com.ypeckstadt.dao.art.ArtDao;
import com.ypeckstadt.dao.art.ArtRecord;

import java.util.List;

public abstract class ArtService {

    protected final ArtDao artDao;
    protected final AccountDao accountDao;

    public ArtService(ArtDao artDao, AccountDao accountDao) {
        this.artDao = artDao;
        this.accountDao = accountDao;
    }

    public abstract ArtRecord create(String artId, String accountId, int price) throws Exception;
    public abstract ArtRecord changeOwner(String currentOwnerAccountId, String newOwnerAccountId, String artId) throws Exception;
    public abstract ArtRecord view(String accountId, String artId) throws Exception;
    public abstract ArtRecord purchase(String artId, String buyerAccountId, String sellerAccountId) throws  Exception;
    public abstract List<ArtRecord> list(String accountId) throws  Exception;
}