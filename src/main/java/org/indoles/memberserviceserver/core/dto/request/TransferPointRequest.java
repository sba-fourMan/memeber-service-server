package org.indoles.memberserviceserver.core.dto.request;

import static org.indoles.memberserviceserver.core.dto.validateDto.ValidateMemberDto.*;

public record TransferPointRequest(
        Long receiverId,
        Long amount
) {

    public TransferPointRequest {
        validateNotNull(receiverId, "수신자 ID");
        validateReceiverId(receiverId);
        validateAmount(amount);
    }
}
