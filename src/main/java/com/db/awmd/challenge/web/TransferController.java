package com.db.awmd.challenge.web;

import java.math.BigDecimal;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.db.awmd.challenge.domain.Transfer;
import com.db.awmd.challenge.exception.AccountDoesNotExistException;
import com.db.awmd.challenge.exception.InsufficientFundException;
import com.db.awmd.challenge.exception.InvalidAmountNotAllowed;
import com.db.awmd.challenge.service.ITransferService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/transfer")
@Slf4j
public class TransferController {

	private String TRANSACTION_MESSAGE = "Successfully transferred _AMOUNT to _ACCOUNTTO.";

	private ITransferService fundTransferService;

	public TransferController(ITransferService fundTransferService) {
		this.fundTransferService = fundTransferService;
	}

	@PostMapping
	public ResponseEntity<Object> transferAmount(@RequestBody @Valid Transfer transfer) {

		log.info("transfer amount {} to {}", transfer.getAmount(), transfer.getToAccountId());
		try {

			invalidTransferAmountCheck(transfer);
			fundTransferService.transferAmount(transfer,
					TRANSACTION_MESSAGE.replace("_AMOUNT", transfer.getAmount().toString()).replace("_ACCOUNTTO",
							transfer.getToAccountId()));
		} catch (AccountDoesNotExistException adne) {
			return new ResponseEntity<>(adne.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (InsufficientFundException ife) {
			return new ResponseEntity<>(ife.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
		} catch (InvalidAmountNotAllowed nan) {
			return new ResponseEntity<>(nan.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
		}
		return new ResponseEntity<>(HttpStatus.OK);

	}

	private void invalidTransferAmountCheck(Transfer transfer) {
		if (transfer.getAmount().compareTo(BigDecimal.ZERO) == -1 || transfer.getAmount().compareTo(BigDecimal.ZERO) == 0 ) {
			throw new InvalidAmountNotAllowed("transfer amount should be greater than 0.");
		}
	}
}
