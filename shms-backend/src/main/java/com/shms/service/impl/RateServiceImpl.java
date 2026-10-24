package com.shms.service.impl;

import com.shms.dto.RateRequest;
import com.shms.dto.RateResponse;
import com.shms.entity.Rate;
import com.shms.entity.enums.AuditAction;
import com.shms.exception.ResourceNotFoundException;
import com.shms.mapper.RateMapper;
import com.shms.repository.RateRepository;
import com.shms.repository.UserRepository;
import com.shms.service.AuditLogService;
import com.shms.service.RateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RateServiceImpl implements RateService {

    private final RateRepository rateRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public RateResponse create(RateRequest request, String actorUsername) {
        LocalDate effectiveFrom = request.getEffectiveFrom() != null ? request.getEffectiveFrom() : LocalDate.now();

        // Close out whatever rate is currently active, so history stays consistent.
        rateRepository.findByEffectiveToIsNull().ifPresent(current -> {
            current.setEffectiveTo(effectiveFrom.minusDays(1));
            rateRepository.save(current);
        });

        Rate rate = new Rate();
        rate.setSugarFactoryName(request.getSugarFactoryName());
        rate.setSharePurchaseAmount(request.getSharePurchaseAmount());
        rate.setPerMonthShareSugar(request.getPerMonthShareSugar());
        rate.setRateOfShareSugar(request.getRateOfShareSugar());
        rate.setRateOfTonnesSugar(request.getRateOfTonnesSugar());
        rate.setEffectiveFrom(effectiveFrom);

        userRepository.findByUsername(actorUsername).ifPresent(u -> rate.setCreatedBy(u.getId()));

        Rate saved = rateRepository.save(rate);
        auditLogService.log(actorUsername, AuditAction.RATE_CREATED, "Rate", saved.getId(),
                "New rate created, effective " + effectiveFrom);

        return RateMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public RateResponse getCurrent() {
        Rate current = rateRepository.findByEffectiveToIsNull()
                .orElseThrow(() -> new ResourceNotFoundException("No active rate configured yet. Create one via POST /api/rates."));
        return RateMapper.toResponse(current);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RateResponse> getHistory() {
        return rateRepository.findAll().stream().map(RateMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Rate resolveEffectiveRate(LocalDate date) {
        return rateRepository.findEffectiveOn(date)
                .orElseGet(() -> rateRepository.findByEffectiveToIsNull()
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "No rate is configured for " + date + ". Create a rate via POST /api/rates first.")));
    }
}
