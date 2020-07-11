package com.db.awmd.challenge.repository;

import java.math.BigDecimal;

import org.springframework.stereotype.Repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Transfer;
import com.db.awmd.challenge.exception.AccountDoesNotExistException;
import com.db.awmd.challenge.exception.InsufficientFundException;
import com.db.awmd.challenge.exception.InvalidAmountNotAllowed;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.EmailNotificationService;

@Repository
public class TransferRepositoryInMemory implements TransferRepository {
	private final EmailNotificationService emailNotificationService;
	private final AccountsService accountsService;

	public TransferRepositoryInMemory(AccountsService accountsService,
			EmailNotificationService emailNotificationService) {
		this.accountsService = accountsService;
		this.emailNotificationService = emailNotificationService;

	}

	@Override
	public void transferAmount(Transfer transfer, String transferDescription) {
		synchronized (this) {
			Account accountFrom = accountsService.getAccount(transfer.getFromAccountId());
			Account accountTo = accountsService.getAccount(transfer.getToAccountId());

			if (null != accountFrom && null != accountTo) {

				BigDecimal transferAmount = transfer.getAmount();
				BigDecimal currentBalanceWithSender = accountFrom.getBalance();

				insufficientFundCheck(currentBalanceWithSender, transferAmount);

				BigDecimal currentBalanceWithReciever = accountTo.getBalance();

				performTransfer(transfer, transferDescription, accountFrom, accountTo, transferAmount,
						currentBalanceWithSender, currentBalanceWithReciever);

			} else {
				throw new AccountDoesNotExistException(
						"Please check the account number(s) entered. Account(s) does not exist.");
			}
		}
	}

	

	private void insufficientFundCheck(BigDecimal balance, BigDecimal transferAmount) {
		if (balance.compareTo(BigDecimal.ZERO) == 0 || balance.compareTo(transferAmount) == -1)
			throw new InsufficientFundException("You have insufficient funds.");
	}

	private void performTransfer(Transfer transfer, String transferDescription, Account accountFrom, Account accountTo,
			BigDecimal transferAmount, BigDecimal currentBalanceWithSender, BigDecimal currentBalanceWithReciever) {

		accountFrom.setBalance(currentBalanceWithSender.subtract(transferAmount));
		accountTo.setBalance(currentBalanceWithReciever.add(transferAmount));
		accountsService.updateAccount(accountFrom.getAccountId(), accountFrom);
		accountsService.updateAccount(accountTo.getAccountId(), accountTo);
		emailNotificationService.notifyAboutTransfer(accountsService.getAccount(transfer.getFromAccountId()),
				transferDescription);
		emailNotificationService.notifyAboutTransfer(accountsService.getAccount(transfer.getToAccountId()),
				transferDescription);

	}

}
