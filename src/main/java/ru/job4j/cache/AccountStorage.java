package ru.job4j.cache;

import java.util.HashMap;
import java.util.Optional;

public class AccountStorage {
    private final HashMap<Integer, Account> accounts = new HashMap<>();

    public synchronized boolean add(Account account) {
        return accounts.putIfAbsent(account.id(), account) == null;
    }

    public synchronized boolean update(Account account) {
        return accounts.replace(account.id(), accounts.get(account.id()), account);
    }

    public synchronized void delete(int id) {
        accounts.remove(id);
    }

    public synchronized Optional<Account> getById(int id) {
        return Optional.ofNullable(accounts.get(id));
    }


    public synchronized boolean transfer(int fromId, int toId, int amount) {
        Optional<Account> accauntFrom = getById(fromId);
        Optional<Account> accauntTo = getById(toId);
        if (accauntFrom.isPresent() && accauntTo.isPresent() && accauntFrom.get().amount() >= amount) {
            update(new Account(toId, accounts.get(fromId).amount() + amount));
            update(new Account(fromId, accounts.get(fromId).amount() - amount));
            return true;
        }
        return false;
    }

}
