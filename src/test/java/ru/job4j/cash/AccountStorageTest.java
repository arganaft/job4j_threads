package ru.job4j.cash;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountStorageTest {

    @Test
    void whenAdd() {
        var storage = new AccountStorage();
        storage.add(new Account(1, 100));
        var firstAccount = storage.getById(1)
                .orElseThrow(() -> new IllegalStateException("Not found account by id = 1"));
        assertThat(firstAccount.amount()).isEqualTo(100);
    }

    @Test
    void whenNullAdd() {
        var storage = new AccountStorage();
        assertThatThrownBy(() -> {
            storage.add(null);
        }).isInstanceOf(NullPointerException.class)
                .hasMessage("Для добавления в список аккаунтов переданно null значение");
    }

    @Test
    void whenWrongUpdate() {
        var storage = new AccountStorage();
        storage.add(new Account(1, 100));
        assertThatThrownBy(() -> {
            storage.update(new Account(2, 370));
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("аккаунт с ID 2 и счетом 370 не найден");
    }

    @Test
    void whenUpdate() {
        var storage = new AccountStorage();
        storage.add(new Account(1, 100));
        storage.update(new Account(1, 200));
        var firstAccount = storage.getById(1)
                .orElseThrow(() -> new IllegalStateException("Not found account by id = 1"));
        assertThat(firstAccount.amount()).isEqualTo(200);
    }

    @Test
    void whenDelete() {
        var storage = new AccountStorage();
        storage.add(new Account(1, 100));
        storage.delete(1);
        assertThat(storage.getById(1)).isEmpty();
    }

    @Test
    void whenWrongDelete() {
        var storage = new AccountStorage();
        storage.add(new Account(1, 100));
        assertThatThrownBy(() -> {
            storage.delete(3);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("аккаунт с ID 3 не найден");
    }

    @Test
    void whenTransfer() {
        var storage = new AccountStorage();
        storage.add(new Account(1, 100));
        storage.add(new Account(2, 100));
        storage.transfer(1, 2, 100);
        var firstAccount = storage.getById(1)
                .orElseThrow(() -> new IllegalStateException("Not found account by id = 1"));
        var secondAccount = storage.getById(2)
                .orElseThrow(() -> new IllegalStateException("Not found account by id = 1"));
        assertThat(firstAccount.amount()).isEqualTo(0);
        assertThat(secondAccount.amount()).isEqualTo(200);
    }

    @Test
    void whenInsufficientBalanceForTransfer() {
        var storage = new AccountStorage();
        storage.add(new Account(1, 100));
        storage.add(new Account(2, 100));
        assertThatThrownBy(() -> {
            storage.transfer(1, 2, 200);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("На счете с ID 1 недостаточно баланса на счету для перевода");
    }

    @Test
    void whenNullAccauntToTransfer() {
        var storage = new AccountStorage();
        storage.add(new Account(1, 100));
        storage.add(new Account(2, 100));
        assertThatThrownBy(() -> {
            storage.transfer(3, 4, 100);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Аккаунт с ID 3 или с ID 4 не найден");
    }
}