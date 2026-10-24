package com.shms.service;

import com.shms.dto.AllotmentRequest;
import com.shms.dto.AllotmentResponse;

import java.util.List;

public interface ShareAllotmentService {
    AllotmentResponse allot(AllotmentRequest request, String actorUsername);
    List<AllotmentResponse> getByMemberId(String memberId);
}
