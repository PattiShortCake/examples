package example.sleuth;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

@SpringBootApplication
public class ExampleApplication {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExampleApplication.class);
  public static void main(final String[] args) {
      SpringApplication.run(ExampleApplication.class);
  }

  @Bean
  public String displayGlobalFilters(final List<GlobalFilter> globalFilters) {

    for(final GlobalFilter filter : globalFilters)  {
      if (filter instanceof Ordered) {
        LOGGER.info("{} -> {}", filter.getClass().getName(), ((Ordered) filter).getOrder());
      }
    }

    return globalFilters.toString();
//    example.gateway.CustomGlobalFilter -> -1
//    org.springframework.cloud.gateway.filter.NettyWriteResponseFilter -> -1
//    org.springframework.cloud.gateway.filter.ForwardPathFilter -> 0
//    org.springframework.cloud.gateway.filter.RouteToRequestUrlFilter -> 10000
//    org.springframework.cloud.gateway.config.GatewayNoLoadBalancerClientAutoConfiguration$NoLoadBalancerClientFilter -> 10100
//    org.springframework.cloud.gateway.filter.WebsocketRoutingFilter -> 2147483646
//    org.springframework.cloud.gateway.filter.NettyRoutingFilter -> 2147483647
//    org.springframework.cloud.gateway.filter.ForwardRoutingFilter -> 2147483647
  }
}
