package com.prothsync.prothsync.service;

import java.time.Duration;

public interface TokenBlacklistService {

    void addToBlackList(String token, Duration expirationTime);

    boolean isBlackListed(String token);
}
