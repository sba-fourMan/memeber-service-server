package org.indoles.memberserviceserver.core.dto.request;

public record TransferPointRequestWrapper(
        SignInfoRequest signInfoRequest,
        TransferPointRequest transferPointRequest
) {
}

