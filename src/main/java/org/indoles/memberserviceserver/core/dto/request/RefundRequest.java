package org.indoles.memberserviceserver.core.dto.request;

public record RefundRequest(
        Long receiverId,
        Long amount
) {
}
