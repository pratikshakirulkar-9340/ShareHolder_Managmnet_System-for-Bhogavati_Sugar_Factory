package com.shms.service;

import com.shms.dto.TransferDecisionRequest;
import com.shms.dto.TransferRequest;
import com.shms.dto.TransferResponse;

import java.util.List;

public interface ShareTransferService {
    TransferResponse request(TransferRequest request, String actorUsername);
    TransferResponse decide(Long transferId, TransferDecisionRequest decision, String actorUsername);
    List<TransferResponse> getAll();
    List<TransferResponse> getPending();
}
