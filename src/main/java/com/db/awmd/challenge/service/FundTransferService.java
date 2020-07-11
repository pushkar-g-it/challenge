package com.db.awmd.challenge.service;

import org.springframework.stereotype.Service;

import com.db.awmd.challenge.domain.Transfer;
import com.db.awmd.challenge.repository.TransferRepository;
@Service
public class FundTransferService implements ITransferService{
    
	private TransferRepository transferRepository;
	public FundTransferService(TransferRepository transferRepository) {
		this.transferRepository=transferRepository;
	}
	@Override
	public void transferAmount(Transfer transfer, String transferDescription) {
        
		transferRepository.transferAmount(transfer,transferDescription);
		
}

}