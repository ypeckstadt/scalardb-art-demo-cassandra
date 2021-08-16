package com.ypeckstadt.service.account;

import com.ypeckstadt.dao.account.AccountDao;
import com.ypeckstadt.dao.account.AccountRecord;

public abstract class AccountService {

    protected final AccountDao accountDao;

    public AccountService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public abstract AccountRecord create(String id) throws Exception;
    public abstract AccountRecord charge(String id, int amount) throws Exception;
    public abstract AccountRecord view(String id) throws Exception;
}
