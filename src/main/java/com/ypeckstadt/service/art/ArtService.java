package com.ypeckstadt.service.art;

import com.ypeckstadt.dao.account.AccountDao;
import com.ypeckstadt.dao.art.ArtDao;
import com.ypeckstadt.dao.art.ArtRecord;

public abstract class ArtService {

    protected final ArtDao artDao;
    protected final AccountDao accountDao;

    public ArtService(ArtDao artDao, AccountDao accountDao) {
        this.artDao = artDao;
        this.accountDao = accountDao;
    }

    public abstract ArtRecord create(String id, String accountId, int price) throws Exception;
    public abstract ArtRecord changeOwner(String id, String accountId) throws Exception;
    public abstract ArtRecord view(String id) throws Exception;
    public abstract ArtRecord purchase(String id, String accountId) throws  Exception;
}