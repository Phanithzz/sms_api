package com.sms.smsApi.job;

import com.sms.smsApi.repository.TokenRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;

@Component
public class TokenJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenJob.class);

    @Autowired
    private TokenRepository tokenRepository;

    @Scheduled(cron = "0 * * * * *")
    // runs every minute daily
    @Transactional
    public void purgeExpiredTokens() {
        LOGGER.info("Purging expired/revoked tokens");
        tokenRepository.deleteExpiredOrRevokedTokens(); // <-- better method
    }
}
