package com.shms.service;

import com.shms.dto.ShareSugarRequest;
import com.shms.dto.ShareSugarResponse;

import java.time.LocalDate;
import java.util.List;

public interface ShareSugarService {
    ShareSugarResponse recordSupply(ShareSugarRequest request, String actorUsername);
    List<ShareSugarResponse> getByMemberId(String memberId);
    List<ShareSugarResponse> getByDateRange(LocalDate from, LocalDate to);
}
