package server;

import com.google.common.base.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class ServiceFactory {

    private static Logger log = LogManager.getLogger(ServiceFactory.class);

    private static Map<String, Service> services;

    static {
        Properties props = new Properties();
        try (InputStream in = ServiceFactory.class.getResourceAsStream("/server.properties")) {
            props.load(in);
            in.close();
        } catch (IOException e) {
            log.error("Failed to load server configuration: " + e.getMessage());
        }

        // don't we better do this lazily
        services = props.stringPropertyNames().parallelStream()
            .filter(n -> !Strings.isNullOrEmpty(props.getProperty(n)))
            .map(n -> new Service(n, props.getProperty(n)))
            .filter(Service::isReady)
            .collect(Collectors.toMap(s -> s.name, s -> s));
    }

    public static Service get(String name) {
        return services.get(name);
    }
}
