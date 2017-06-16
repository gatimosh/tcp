package config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;

@Sources({ "classpath:config/gt.properties" })
public interface Gt extends Config {

    @Key("server.host")
    @DefaultValue("localhost")
    @Deprecated
    String host();

    @Key("server.port")
    @DefaultValue("1111")
    int port();

    @Key("pool.size")
    @DefaultValue("16")
    int poolSize();

}

