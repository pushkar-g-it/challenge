package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Transfer;

public interface ITransferService {
	public void transferAmount(Transfer transfer, String transferDescription);
}
