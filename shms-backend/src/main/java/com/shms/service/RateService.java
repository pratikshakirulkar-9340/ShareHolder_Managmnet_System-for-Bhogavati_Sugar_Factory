package com.shms.service;

import com.shms.dto.RateRequest;
import com.shms.dto.RateResponse;
import com.shms.entity.Rate;

import java.time.LocalDate;
import java.util.List;

public interface RateService {
    RateResponse create(RateRequest request, String actorUsername);
    RateResponse getCurrent();
    List<RateResponse> getHistory();

    /** Used internally by other services to resolve the rate that applied on a given date. */
    Rate resolveEffectiveRate(LocalDate date);
}
