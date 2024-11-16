package org.indoles.memberserviceserver.core.dto.request;

public record TransferPointRequest (
        Long receiverId,
        Long amount
) {
}
