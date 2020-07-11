package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Transfer;

public interface TransferRepository {

	  void transferAmount(Transfer transaction,String transferDescription);
}
