package com.shms.util;

import com.shms.entity.enums.ShareClass;
import com.shms.repository.ShareholderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Generates the legacy-style human-facing member ID: a sequential number suffixed
 * with the class letter (e.g. "55753A", "55784C"), matching the original .NET app's convention.
 */
@Component
@RequiredArgsConstructor
public class MemberIdGenerator {

    private final ShareholderRepository shareholderRepository;

    public synchronized String generateForClass(ShareClass shareClass) {
        String suffix = switch (shareClass) {
            case A -> "A";
            case C -> "C";
            case UNASSIGNED -> "U";
        };
        long nextSeq = shareholderRepository.count() + 1;
        String candidate = nextSeq + suffix;
        // Guard against collisions if IDs were ever assigned out of band.
        while (shareholderRepository.existsByMemberId(candidate)) {
            nextSeq++;
            candidate = nextSeq + suffix;
        }
        return candidate;
    }
}
