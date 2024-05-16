package ru.job4j.cash;

import java.util.HashMap;
import java.util.Optional;

public class AccountStorage {
    private final HashMap<Integer, Account> accounts = new HashMap<>();

    public synchronized boolean add(Account account) {
        if (account == null) {
            throw new NullPointerException("Для добавления в список аккаунтов переданно null значение");
        }
        return accounts.putIfAbsent(account.id(), account) == null;
    }

    public synchronized boolean update(Account account) {
        if (accounts.get(account.id()) == null) {
            throw new IllegalArgumentException(String.format("аккаунт с ID %d и счетом %d не найден", account.id(), account.amount()));
        }
        accounts.put(account.id(), account);
        return true;
    }

    public synchronized void delete(int id) {
        if (accounts.get(id) == null) {
            throw new IllegalArgumentException(String.format("аккаунт с ID %d не найден", id));
        }
        accounts.remove(id);
    }

    public synchronized Optional<Account> getById(int id) {
        if (accounts.containsKey(id)) {
            return Optional.of(accounts.get(id));
        } else {
            return Optional.empty();
        }
    }

    public synchronized boolean transfer(int fromId, int toId, int amount) {
        if (accounts.get(fromId) == null || accounts.get(toId) == null) {
            throw new IllegalArgumentException(String.format("Аккаунт с ID %d или с ID %d не найден", fromId, toId));
        }
        if (accounts.get(fromId).amount() < amount) {
            throw new IllegalArgumentException(String.format("На счете с ID %d недостаточно баланса на счету для перевода", fromId));
        }
        update(new Account(toId, accounts.get(fromId).amount() + amount));
        update(new Account(fromId, accounts.get(fromId).amount() - amount));
        return false;
    }

}
