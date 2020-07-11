package com.db.awmd.challenge;

import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThat;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Transfer;
import com.db.awmd.challenge.exception.AccountDoesNotExistException;
import com.db.awmd.challenge.exception.InsufficientFundException;
import com.db.awmd.challenge.repository.TransferRepository;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.EmailNotificationService;
import com.db.awmd.challenge.service.ITransferService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FundTransferServiceTest {

	@Autowired
	TransferRepository transferRepository;
	@Autowired
	ITransferService fundTransferService;
	@Autowired
	private AccountsService accountsService;
	@MockBean
	private EmailNotificationService emailNotificationService;

	@Test
	public void testTransferAmount() throws Exception {

		Account accountFrom = new Account("Id-126");
		accountFrom.setBalance(new BigDecimal(1000));
		this.accountsService.createAccount(accountFrom);

		Account accountTo = new Account("Id-127");
		accountTo.setBalance(new BigDecimal(1000));
		this.accountsService.createAccount(accountTo);

		Transfer transfer = new Transfer(accountFrom.getAccountId(), accountTo.getAccountId(), new BigDecimal(999));
		this.fundTransferService.transferAmount(transfer, "message");
		Assert.assertEquals(1, (this.accountsService.getAccount(accountTo.getAccountId()).getBalance())
				.compareTo(this.accountsService.getAccount(accountFrom.getAccountId()).getBalance()));
		Assert.assertEquals(new BigDecimal(1),
				this.accountsService.getAccount(accountFrom.getAccountId()).getBalance());
	}

	@Test(expected = AccountDoesNotExistException.class)
	public void testTransferAmount_Negative_AccountDoesntExist() {
	    Transfer transfer=	new Transfer("ID-128", "ID-129", new BigDecimal(999));
        fundTransferService.transferAmount(transfer, "transfer message");
	}
	
	@Test
	public void testTransferAmount_HandleInsufficientBalance() {
        try {
		Account accountFrom = new Account("Id-128");
		accountFrom.setBalance(BigDecimal.ZERO);
		this.accountsService.createAccount(accountFrom);
		
		Account accountTo = new Account("Id-129");
		accountTo.setBalance(new BigDecimal(10));
		this.accountsService.createAccount(accountTo);
		
		Transfer transfer = new Transfer(accountFrom.getAccountId(), accountTo.getAccountId(), new BigDecimal(1000));

		this.fundTransferService.transferAmount(transfer, "message");
        }
        catch(InsufficientFundException ife) {
        	assertThat(ife.getMessage()).isEqualTo("You have insufficient funds.");
        }
	}
	
}