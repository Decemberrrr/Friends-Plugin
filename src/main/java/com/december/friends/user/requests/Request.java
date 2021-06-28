package com.december.friends.user.requests;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Request {

    private UUID requestedUUID; // Player who added first
    private UUID targetUUID;

    public Request(UUID requestedUUID, UUID targetUUID) {
        this.requestedUUID = requestedUUID;
        this.targetUUID = targetUUID;
    }
}
