package com.december.friends.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@AllArgsConstructor
public class Friend {

    private UUID uuid;
    private String userName;
    private long addedAt;


}
