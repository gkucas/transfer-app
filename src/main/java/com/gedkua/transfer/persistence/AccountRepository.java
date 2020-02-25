package com.gedkua.transfer.persistence;

import com.gedkua.transfer.api.TransferException;
import com.gedkua.transfer.api.TransferRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;

public class AccountRepository {

  private final EntityManagerFactory entityManagerFactory;

  public AccountRepository(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory = entityManagerFactory;
  }

  public Optional<TransferException> transfer(TransferRequest request) {
    EntityManager entityManager = entityManagerFactory.createEntityManager();

    entityManager.getTransaction().begin();
    TypedQuery<Account> query = entityManager.createQuery("from Account where id = :to or id= " +
        ":from", Account.class);
    query.setParameter("from", request.getFrom().getId());
    query.setParameter("to", request.getTo().getId());
    query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
    query.setMaxResults(2);

    List<Account> resultList = query.getResultList();

    if (resultList.size() != 2) {
      return Optional.of(new TransferException("Cannot find party accounts"));
    }

    Optional<TransferException> result = resultList.stream().map(account -> {
      account.setBalance(account.getId().equals(request.getFrom().getId()) ?
          account.getBalance().subtract(request.getAmount()) :
          account.getBalance().add(request.getAmount()));
      return account;
    }).map(account -> {
      if (account.getBalance().compareTo(BigDecimal.ZERO) < 0) {
        return Optional.of(new TransferException(String.format("Insufficient account %s balance",
            account.getName())));
      } else {
        entityManager.persist(account);
        return Optional.<TransferException>empty();
      }
    }).reduce(Optional.empty(), (o, o2) -> o.isPresent() ? o : o2);

    entityManager.getTransaction().commit();

    return result;
  }

  public Account create(Account account) {
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    entityManager.getTransaction().begin();
    entityManager.persist(account);
    entityManager.getTransaction().commit();
    return account;
  }

  public Account findById(Long id) {
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    TypedQuery<Account> query = entityManager.createQuery("from Account where id = :id",
        Account.class);
    query.setParameter("id", id);
    return query.getSingleResult();
  }


}
