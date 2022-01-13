package spring.batch.integration.configuration;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.CommonClientConfigs.CLIENT_ID_CONFIG;
import static org.apache.kafka.clients.CommonClientConfigs.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.ACKS_CONFIG;
import static spring.batch.integration.configuration.SpringBatchKafkaConfiguration.SPRING_BATCH_INTEGRATION_REPLIES;
import static spring.batch.integration.configuration.SpringBatchKafkaConfiguration.SPRING_BATCH_INTEGRATION_REQUESTS;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.integration.partition.RemotePartitioningWorkerStepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConsumerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;

import spring.batch.integration.kafka.KafkaConfigurationOptionsProvider;
import spring.batch.integration.kafka.KafkaProperties;

@Slf4j
@EnableBatchProcessing
@EnableBatchIntegration
@Configuration
public class WorkerConfiguration {

	@Bean
	public DirectChannel incomingRequestsFromManager() {
		return new DirectChannel();
	}

	@Bean
	public IntegrationFlow inboundFlowFromManager(
		ConsumerFactory requestConsumerFactory,
		DirectChannel incomingRequestsFromManager,
		KafkaProperties kafkaProperties) {

		ConsumerProperties consumerProperties = new ConsumerProperties(kafkaProperties.getOptions().get(SPRING_BATCH_INTEGRATION_REQUESTS).getTopic());
		return IntegrationFlows
			.from(Kafka.inboundChannelAdapter(requestConsumerFactory, consumerProperties))
			.channel(incomingRequestsFromManager)
			.get();
	}

	@Bean
	public DirectChannel outgoingRepliesToManager() {
		return new DirectChannel();
	}

	@Bean
	public IntegrationFlow outboundFlowToManager(
		ProducerFactory repliesProducerFactory,
		DirectChannel outgoingRepliesToManager,
		KafkaProperties kafkaProperties) {

		return IntegrationFlows
			.from(outgoingRepliesToManager)
			.handle(Kafka.outboundChannelAdapter(repliesProducerFactory)
				.topic(kafkaProperties.getOptions().get(SPRING_BATCH_INTEGRATION_REPLIES).getTopic()))
			.get();
	}

	@Bean(name = "workerStep")
	public Step worker(
		RemotePartitioningWorkerStepBuilderFactory workerStepBuilderFactory,
		ObjectProvider<Tasklet> taskletProvider,
		ObjectProvider<ItemStreamReader> itemStreamReaderObjectProvider,
		ObjectProvider<ItemProcessor> itemProcessorProvider,
		ObjectProvider<ItemWriter> itemWriterProvider,
		ObjectProvider<RetryPolicy> retryPolicyProvider,
		ObjectProvider<SkipPolicy> skipPolicyProvider,
		ObjectProvider<BackOffPolicy> backoffPolicyProvider
	) {
		log.info("Creating worker step");

		SimpleStepBuilder simpleStepBuilder = workerStepBuilderFactory.get("workerStep")
			//.listener(defaultStepExecutionListener)
			.inputChannel(incomingRequestsFromManager())
			.outputChannel(outgoingRepliesToManager())
			.<String, String>chunk(5)
			.reader(itemStreamReaderObjectProvider.getObject())
			.processor(itemProcessorProvider.getObject())
			.writer(itemWriterProvider.getObject());
		return simpleStepBuilder.build();
	}


	@Bean
	public ProducerFactory repliesProducerFactory(
		KafkaProperties kafkaProperties,
		KafkaConfigurationOptionsProvider kafkaConfigurationOptionsProvider,
		JsonSerializer stepExecutionSerializer
	) {
		KafkaProperties.Options sender = kafkaProperties.getOptions().get(SPRING_BATCH_INTEGRATION_REPLIES);
		Map<String, Object> producerProps = new HashMap<>();
		producerProps.put(BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
		producerProps.put(CLIENT_ID_CONFIG, kafkaConfigurationOptionsProvider.getClientId(sender));
		producerProps.put(ACKS_CONFIG, kafkaProperties.getAcks());

		producerProps.putAll(kafkaConfigurationOptionsProvider.kafkaSecurityConfig());

		return new DefaultKafkaProducerFactory(producerProps, new StringSerializer(), stepExecutionSerializer);
	}

	@Bean
	public ConsumerFactory requestConsumerFactory(KafkaProperties kafkaProperties, KafkaConfigurationOptionsProvider kafkaConfigurationOptionsProvider, JsonDeserializer stepExecutionDeSerializer) {

		KafkaProperties.Options receiver = kafkaProperties.getOptions().get(SPRING_BATCH_INTEGRATION_REQUESTS);

		Map<String, Object> consumerProps = new HashMap<>();
		consumerProps.put(BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
		consumerProps.put(CLIENT_ID_CONFIG, kafkaConfigurationOptionsProvider.getClientId(receiver));
		consumerProps.put(GROUP_ID_CONFIG, kafkaConfigurationOptionsProvider.getGroupId(receiver));

		consumerProps.putAll(kafkaConfigurationOptionsProvider.kafkaSecurityConfig());

		return new DefaultKafkaConsumerFactory(consumerProps, new StringDeserializer(), stepExecutionDeSerializer);
	}
}
