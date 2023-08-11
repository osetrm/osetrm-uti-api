package org.osetrm.api.uti.generator;

import io.quarkus.runtime.Startup;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;

@Singleton
@Startup
public class UtiGeneratorService {

    private static final Logger logger = LoggerFactory.getLogger(UtiGeneratorService.class);
    public static final String ENV_MACHINE_UNIQUE_ID = "MACHINE_UNIQUE_ID";
    private static final String EMIR_PREFIX = "E02";
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddhhmmssSS");
    private final String machineUniqueId;

    /*
        There needs to be something at a global scope that creates a unique number within a particular Time instant.
        Event at 2 subsecond places, the load test generated duplicates. Using the static AtomicInteger gives us an
        incrementer that supports the load at 200 concurrent clients.
    */
    private static final AtomicInteger appendingIncrementer = new AtomicInteger(1);

    /*
        The value produces by the atomic integer needs to stay within 3 chars.
    */
    private static final IntUnaryOperator appendingIncrementerOperator = x -> {
        if (x > 999) {
            return 1;
        }
        return x + 1;
    };

    public UtiGeneratorService() throws UnknownHostException {
        if (System.getenv().containsKey(ENV_MACHINE_UNIQUE_ID)) {
            this.machineUniqueId = System.getenv().get(ENV_MACHINE_UNIQUE_ID);
            logger.info("MachineUniqueId set using env var at MACHINE_UNIQUE_ID. Value: {}", this.machineUniqueId);
        } else {
            InetAddress inetAddress = InetAddress.getLocalHost();
            int i = Integer.parseInt(inetAddress.getHostAddress().substring(inetAddress.getHostAddress().lastIndexOf('.') + 1));
            this.machineUniqueId = String.format("%03d", i);
            logger.info("MachineUniqueId set using ipAddress[{}]. Value: {}", inetAddress.getHostAddress(), this.machineUniqueId);
        }
    }

    public String generate(RegulatoryRegime regulatoryRegime, String lei) {
        StringBuilder stringBuilder = new StringBuilder();
        if (regulatoryRegime.equals(RegulatoryRegime.EMIR)) {
            stringBuilder.append(EMIR_PREFIX);
        }
        stringBuilder.append(lei);
        stringBuilder.append(machineUniqueId);
        stringBuilder.append(ZonedDateTime.now(ZoneId.of("UTC")).format(dateTimeFormatter));
        stringBuilder.append(String.format("%03d", appendingIncrementer.getAndUpdate(appendingIncrementerOperator)));
        //logger.debug("Generated uti: {}", stringBuilder.toString());
        return stringBuilder.toString();
    }

}
