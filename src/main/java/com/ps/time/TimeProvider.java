package com.ps.time;

import java.time.OffsetDateTime;

public interface TimeProvider {

    OffsetDateTime now();
}