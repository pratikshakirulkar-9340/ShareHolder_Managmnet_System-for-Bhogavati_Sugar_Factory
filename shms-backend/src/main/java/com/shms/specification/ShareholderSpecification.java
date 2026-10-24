package com.shms.specification;

import com.shms.entity.Shareholder;
import com.shms.entity.enums.ShareClass;
import com.shms.entity.enums.ShareholderStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

/** Builds a dynamic WHERE clause for GET /api/shareholders/search from optional query params. */
public final class ShareholderSpecification {

    private ShareholderSpecification() {}

    public static Specification<Shareholder> search(String memberId, String name, String village,
                                                      ShareClass shareClass, ShareholderStatus status) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (StringUtils.hasText(memberId)) {
                predicates = cb.and(predicates, cb.like(cb.lower(root.get("memberId")), "%" + memberId.toLowerCase() + "%"));
            }
            if (StringUtils.hasText(name)) {
                predicates = cb.and(predicates, cb.like(cb.lower(root.get("memberName")), "%" + name.toLowerCase() + "%"));
            }
            if (StringUtils.hasText(village)) {
                predicates = cb.and(predicates, cb.like(cb.lower(root.get("village")), "%" + village.toLowerCase() + "%"));
            }
            if (shareClass != null) {
                predicates = cb.and(predicates, cb.equal(root.get("shareClass"), shareClass));
            }
            if (status != null) {
                predicates = cb.and(predicates, cb.equal(root.get("status"), status));
            }
            return predicates;
        };
    }
}
