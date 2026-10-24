package com.shms.service;

import com.shms.dto.TonnesSugarRequest;
import com.shms.dto.TonnesSugarResponse;

import java.time.LocalDate;
import java.util.List;

public interface TonnesSugarService {
    TonnesSugarResponse recordSupply(TonnesSugarRequest request, String actorUsername);
    List<TonnesSugarResponse> getByMemberId(String memberId);
    List<TonnesSugarResponse> getByDateRange(LocalDate from, LocalDate to);
}
