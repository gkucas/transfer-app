package com.gedkua.transfer.persistence;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;


import com.gedkua.transfer.api.TransferRequest;
import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.junit.jupiter.api.Test;

class AccountRepositoryTest {

  private final EntityManagerFactory entityManagerFactory =
      Persistence.createEntityManagerFactory(
          "com.gedkua.transfer.persistence-test");
  private AccountRepository accountRepository = new AccountRepository(entityManagerFactory);

  @Test
  void transferMultithreaded() throws InterruptedException {
    Account a = accountRepository.create(new Account(null, "a", new BigDecimal(1000000)));
    Account b = accountRepository.create(new Account(null, "b", new BigDecimal(1000000)));
    BigDecimal amount = new BigDecimal(1);
    TransferRequest transferRequestA = new TransferRequest(a, b, amount);
    TransferRequest transferRequestB = new TransferRequest(b, a, amount);
    CountDownLatch countDownLatch = new CountDownLatch(2);
    long repeats = 1000L;
    new Thread(new TransferRunnable(transferRequestA, repeats, countDownLatch)).start();
    new Thread(new TransferRunnable(transferRequestB, repeats, countDownLatch)).start();
    countDownLatch.await();

    Account finalA = accountRepository.findById(a.getId());
    Account finalB = accountRepository.findById(b.getId());

    assertThat(finalB.getBalance().subtract(finalA.getBalance()).compareTo(BigDecimal.ZERO), is(0));
  }

  @Test
  void transferWithGreaterBalance() {
    Account a = accountRepository.create(new Account(null, "a", new BigDecimal(10)));
    Account b = accountRepository.create(new Account(null, "b", new BigDecimal(10)));
    BigDecimal amount = new BigDecimal(1);
    TransferRequest transferRequest = new TransferRequest(a, b, amount);
    assertThat(accountRepository.transfer(transferRequest).isEmpty(), is(true));
  }

  @Test
  void transferWithSmallBalance() {
    Account a = accountRepository.create(new Account(null, "a", new BigDecimal(0)));
    Account b = accountRepository.create(new Account(null, "b", new BigDecimal(0)));
    BigDecimal amount = new BigDecimal(1);
    TransferRequest transferRequest = new TransferRequest(a, b, amount);
    assertThat(accountRepository.transfer(transferRequest).isEmpty(), is(false));
  }

  @Test
  void getAccount() {
    Account saved = accountRepository.create(new Account(null, "test", new BigDecimal(0)));
    assertThat(saved, is(accountRepository.findById(saved.getId())));
  }

  @Test
  void create() {
    Account saved = accountRepository.create(new Account(null, "test", new BigDecimal(0)));
    assertThat(saved.getId(), is(not(0)));
  }

  class TransferRunnable implements Runnable{

    private final TransferRequest transferRequest;
    private final Long repeats;
    private final CountDownLatch latch;

    TransferRunnable(TransferRequest transferRequest, Long repeats, CountDownLatch latch) {
      this.transferRequest = transferRequest;
      this.repeats = repeats;
      this.latch = latch;
    }

    @Override
    public void run() {
      Long counter = repeats;
      while (counter > 0) {
        accountRepository.transfer(transferRequest);
        counter--;
      }
      latch.countDown();
    }
  }
}