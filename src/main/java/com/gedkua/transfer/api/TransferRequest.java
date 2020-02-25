package com.gedkua.transfer.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gedkua.transfer.persistence.Account;
import java.math.BigDecimal;

public class TransferRequest {

  private Account from;
  private Account to;
  private BigDecimal amount;

  @JsonCreator
  public TransferRequest(
      @JsonProperty(value = "from", required = true) Account from,
      @JsonProperty(value = "to", required = true) Account to,
      @JsonProperty(value = "amount", required = true) BigDecimal amount) {
    this.from = from;
    this.to = to;
    this.amount = amount;
  }

  public Account getFrom() {
    return from;
  }

  public void setFrom(Account from) {
    this.from = from;
  }

  public Account getTo() {
    return to;
  }

  public void setTo(Account to) {
    this.to = to;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }
}
