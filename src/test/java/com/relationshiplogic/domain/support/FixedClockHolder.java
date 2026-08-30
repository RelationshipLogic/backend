package com.relationshiplogic.domain.support;

import java.time.Duration;
import java.time.LocalDateTime;

public class FixedClockHolder implements ClockHolder {

    private LocalDateTime now;

    public FixedClockHolder(LocalDateTime now) {
        this.now = now;
    }

    @Override
    public LocalDateTime now() {
        return now;
    }

    public void advance(Duration duration) {
        this.now = this.now.plus(duration);
    }
}
