package com.shms.service;

import com.shms.dto.SugarAllocationRequest;
import com.shms.dto.SugarAllocationResponse;

import java.util.List;

public interface SugarAllocationService {
    SugarAllocationResponse applyBonus(SugarAllocationRequest request, String actorUsername);
    List<SugarAllocationResponse> getHistory();
}
