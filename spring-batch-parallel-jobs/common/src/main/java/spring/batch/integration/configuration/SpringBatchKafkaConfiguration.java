package spring.batch.integration.configuration;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.CommonClientConfigs.CLIENT_ID_CONFIG;
import static org.apache.kafka.clients.CommonClientConfigs.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.ACKS_CONFIG;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import spring.batch.integration.kafka.KafkaConfigurationOptionsProvider;
import spring.batch.integration.kafka.KafkaProperties;

@Slf4j
@Configuration
public class SpringBatchKafkaConfiguration {

  public static final String SPRING_BATCH_INTEGRATION_REQUESTS= "spring-batch-integration-requests";
  public static final String SPRING_BATCH_INTEGRATION_REPLIES= "spring-batch-integration-replies";

  private String[] trustedPackages = {
    "org.springframework.batch.integration.partition",
    "org.springframework.batch.core"
  };

  @Bean
  public ObjectMapper springBatchObjectMapper() {
    // to avoid infinite recursion due to bi-directional relationship
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.addMixIn(ExitStatus.class, ExitStatusMixin.class);
    objectMapper.addMixIn(StepExecution.class, StepExecutionsMixin.class);
    objectMapper.addMixIn(JobExecution.class, JobExecutionMixin.class);
    return objectMapper;
  }

  @Bean
  public JsonSerializer stepExecutionSerializer(ObjectMapper springBatchObjectMapper) {
    return new JsonSerializer(springBatchObjectMapper);
  }

  @Bean
  public JsonDeserializer stepExecutionDeSerializer(ObjectMapper springBatchObjectMapper) {
    JsonDeserializer jsonDeserializer = new JsonDeserializer(springBatchObjectMapper);
    // so that "org.springframework.batch.integration.partition.StepExecutionRequest"
    // and "org.springframework.batch.core.StepExecution" can be de-serialized
    jsonDeserializer.addTrustedPackages(trustedPackages);
    return jsonDeserializer;
  }

  @Bean
  public ProducerFactory springBatchProducerFactory(
    KafkaProperties kafkaProperties,
    KafkaConfigurationOptionsProvider kafkaConfigurationOptionsProvider,
    JsonSerializer stepExecutionSerializer
  ) {
    KafkaProperties.Options sender = kafkaProperties.getOptions().get(SPRING_BATCH_INTEGRATION_REQUESTS);
    Map<String, Object> producerProps = new HashMap<>();
    producerProps.put(BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
    producerProps.put(CLIENT_ID_CONFIG, kafkaConfigurationOptionsProvider.getClientId(sender));
    producerProps.put(ACKS_CONFIG, kafkaProperties.getAcks());

    producerProps.putAll(kafkaConfigurationOptionsProvider.kafkaSecurityConfig());

    return new DefaultKafkaProducerFactory(producerProps, new StringSerializer(), stepExecutionSerializer);
  }

  @Bean
  public ConsumerFactory springBatchConsumerFactory(KafkaProperties kafkaProperties, KafkaConfigurationOptionsProvider kafkaConfigurationOptionsProvider, JsonDeserializer stepExecutionDeSerializer) {

    KafkaProperties.Options receiver = kafkaProperties.getOptions().get(SPRING_BATCH_INTEGRATION_REPLIES);
//    if (receiver == null) {
//      throw missingKafkaOptions();
//    }

    Map<String, Object> consumerProps = new HashMap<>();
    consumerProps.put(BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
    consumerProps.put(CLIENT_ID_CONFIG, kafkaConfigurationOptionsProvider.getClientId(receiver));
    consumerProps.put(GROUP_ID_CONFIG, kafkaConfigurationOptionsProvider.getGroupId(receiver));

    consumerProps.putAll(kafkaConfigurationOptionsProvider.kafkaSecurityConfig());

    return new DefaultKafkaConsumerFactory(consumerProps, new StringDeserializer(), stepExecutionDeSerializer);
  }

  public static abstract class JobExecutionMixin {
    @JsonManagedReference
    private Collection<StepExecution> stepExecutions;
  }

  public static abstract class StepExecutionsMixin {
    @JsonCreator
    public StepExecutionsMixin(@JsonProperty("stepName") String stepName, @JsonProperty("jobExecution") JobExecution jobExecution) {
    }

    @JsonBackReference
    private JobExecution jobExecution;
  }

  static abstract class ExitStatusMixin {
    @JsonCreator
    public ExitStatusMixin(@JsonProperty("exitCode") String exitCode, @JsonProperty("exitDescription") String exitDescription) {
    }
  }

}
