package spring.batch.integration.kafka;

import static org.apache.kafka.clients.CommonClientConfigs.SECURITY_PROTOCOL_CONFIG;
import static org.apache.kafka.common.config.SaslConfigs.SASL_JAAS_CONFIG;
import static org.apache.kafka.common.config.SaslConfigs.SASL_MECHANISM;
import static org.springframework.util.StringUtils.isEmpty;

import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import spring.batch.integration.kafka.KafkaProperties.Options;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConfigurationOptionsProvider {

  private final KafkaProperties kafkaProperties;

  public Map<String, String> kafkaSecurityConfig() {
    Map<String, String> securityProps = new HashMap<>();

    if (!isEmpty(kafkaProperties.getSecurityProtocol())) {
      securityProps.put(SECURITY_PROTOCOL_CONFIG, kafkaProperties.getSecurityProtocol());
    }

    KafkaProperties.Sasl sasl = kafkaProperties.getSasl();

    if (sasl != null) {
      securityProps.put(SASL_MECHANISM, sasl.getMechanism());

      StringBuilder jaasConfig = new StringBuilder();
      jaasConfig.append(sasl.getJaasConfig() + " required");
      jaasConfig.append(" username=\'" + kafkaProperties.getUser() + "\'");
      jaasConfig.append(" password=\'" + kafkaProperties.getPassword() + "\';");

      securityProps.put(SASL_JAAS_CONFIG, jaasConfig.toString());
    }

    log.debug("Kafka Security Props: {}", securityProps);
    return securityProps;
  }

  public String getClientId(Options options) {
    return options.getClientId();
  }

  public String getGroupId(Options options) {
    log.info("Value of Options :{}",options.toString());
    String grpId;
    log.info("HOSTNAME = {}",getHostName());

    grpId = options.getGroupId();

    log.info("Group_id = {}",grpId);
    return grpId;
  }

  private String getHostName() {
    String hostname = System.getenv("HOSTNAME");
    return StringUtils.isEmpty(hostname) ? "NO_HOST_NAME" : hostname;
  }
}
