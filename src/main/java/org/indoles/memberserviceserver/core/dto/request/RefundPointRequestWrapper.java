package org.indoles.memberserviceserver.core.dto.request;

public record RefundPointRequestWrapper
        (SignInfoRequest signInfoRequest,
         RefundRequest refundPointRequest
        ) {
}
