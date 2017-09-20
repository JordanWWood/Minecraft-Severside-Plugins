package network.marble.moderationslave.utils;

import lombok.Getter;

public enum Time {
    WEEK(604800000),
    DAY(86400000),
    HOUR(3600000),
    MINUTE(60000),
    SECOND(1000);

    @Getter private final long millis;

    Time(long millis){
        this.millis = millis;
    }
}
