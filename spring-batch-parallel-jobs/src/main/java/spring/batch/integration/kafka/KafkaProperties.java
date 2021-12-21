package spring.batch.integration.kafka;

import java.util.Map;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "kafka")
public class KafkaProperties {

  private boolean confluent;
  private String acks;
  private String bootstrapServers;
  private String securityProtocol;
  private String user;
  private String password;
  private Sasl sasl;
  private Map<String, Options> options;
  private SchemaRegistry schemaRegistry;

  @Data
  public static class Options {

    private String topic;
    private int retries;
    private String ack;
    private int usageItemsCountInPayload;
    private int maxUsageItemsInPayload = 1000;
    private Class keySerializer;
    private Class valueSerializer;
    private Class keyDeserializer;
    private Class valueDeserializer;
    private String valueTargetClassName;
    private boolean enabledMagicByte;
    private String groupId;
    private String clientId;
    private boolean publishToAllConsumer = false;

    public int getUsageItemsCountInKafkaMessage() {
      return Math.min(usageItemsCountInPayload, maxUsageItemsInPayload);
    }
  }

  @Data
  public static class SchemaRegistry {
    private boolean enabled;
    private String url;
    private boolean autoRegister;
    private boolean useLatestVersion;
    private Class nameStrategy;
  }

  @Data
  public static class TrustStore {

    private String location;
    private String password;
    private String type;
  }

  @Data
  public static class Sasl {

    private String mechanism;
    private String jaasConfig;
  }
}
