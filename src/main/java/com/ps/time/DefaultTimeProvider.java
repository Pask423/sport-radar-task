package com.ps.time;

import java.time.OffsetDateTime;

public class DefaultTimeProvider implements TimeProvider {

    public OffsetDateTime now() {
        return OffsetDateTime.now();
    }
}
