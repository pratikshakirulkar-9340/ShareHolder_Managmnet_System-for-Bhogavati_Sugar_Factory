package com.shms.service;

import com.shms.dto.ShareholderRequest;
import com.shms.dto.ShareholderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ShareholderService {
    ShareholderResponse register(ShareholderRequest request, String actorUsername);
    ShareholderResponse getByMemberId(String memberId);
    ShareholderResponse update(String memberId, ShareholderRequest request, String actorUsername);
    void delete(String memberId, String actorUsername);
    Page<ShareholderResponse> search(String memberId, String name, String village, String shareClass,
                                      String status, Pageable pageable);
}
