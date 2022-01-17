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
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.integration.partition.MessageChannelPartitionHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.core.MessagingTemplate;
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

import spring.batch.integration.kafka.KafkaConfigurationOptionsProvider;
import spring.batch.integration.kafka.KafkaProperties;

@Slf4j
@EnableBatchProcessing
@EnableBatchIntegration
@Configuration
public class ManagerConfiguration {

	@Bean
	public DirectChannel outgoingRequestsToWorkers() {
		return new DirectChannel();
	}

	@Bean
	public IntegrationFlow outboundFlowToWorker(
		ProducerFactory requestProducerFactory,
		DirectChannel outgoingRequestsToWorkers,
		KafkaProperties kafkaProperties) {

		return IntegrationFlows
			.from(outgoingRequestsToWorkers)
			.handle(Kafka.outboundChannelAdapter(requestProducerFactory)
				.topic(kafkaProperties.getOptions().get(SPRING_BATCH_INTEGRATION_REQUESTS).getTopic()))
			.get();
	}

	@Bean
	public DirectChannel incomingRepliesFromWorkers() {
		return new DirectChannel();
	}

	@Bean
	public IntegrationFlow inboundFlowFromWorker(
		ConsumerFactory repliesConsumerFactory,
		DirectChannel incomingRepliesFromWorkers,
		KafkaProperties kafkaProperties) {

		ConsumerProperties consumerProperties = new ConsumerProperties(kafkaProperties.getOptions().get(SPRING_BATCH_INTEGRATION_REPLIES).getTopic());
		return IntegrationFlows
			.from(Kafka.inboundChannelAdapter(repliesConsumerFactory, consumerProperties))
			.channel(incomingRepliesFromWorkers)
			.get();
	}

	@Bean
	public QueueChannel incomingRepliesQueue() {
		return new QueueChannel();
	}

	@Bean
	public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
		// this ensures all predefined Jobs are registered at startup
		JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
		jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
		return jobRegistryBeanPostProcessor;
	}

	@Bean
	@StepScope
	public PartitionHandler partitionHandler(DirectChannel outgoingRequestsToWorkers,
																					 QueueChannel incomingRepliesQueue,
																					 @Value("#{stepExecution}") StepExecution stepExecution
	) {
		MessageChannelPartitionHandler parallelJobPartitionHandler = new MessageChannelPartitionHandler();
		parallelJobPartitionHandler.setStepName("workerStep");
		parallelJobPartitionHandler.setGridSize(6);
		MessagingTemplate template = new MessagingTemplate();
		template.setDefaultChannel(outgoingRequestsToWorkers);
		parallelJobPartitionHandler.setMessagingOperations(template);
		parallelJobPartitionHandler.setReplyChannel(incomingRepliesQueue);

		return parallelJobPartitionHandler;
	}

	@Bean
	public Step importUsageJobManager(
		StepBuilderFactory managerStepBuilderFactory,
		//StepExecutionListener stepExecutionListener,
		DirectChannel outgoingRequestsToWorkers,
		DirectChannel incomingRepliesFromWorkers,
		ObjectProvider<Partitioner> partitionerProvider,
		PartitionHandler partitionHandler) {

		return managerStepBuilderFactory.get("managerStep")
			.<String, String>partitioner("workerStep", partitionerProvider.getIfAvailable())
			.partitionHandler(partitionHandler)
			//.gridSize(usageJobProperties.getMonthly().getGridSize())
			//.outputChannel(outgoingRequestsToWorkers)
			//inputChannel(incomingRepliesFromWorkers)
			//.listener(stepExecutionListener)
			.build();
	}

	@Bean
	public Job parallelJob(
		JobBuilderFactory jobBuilderFactory,
		Step importUsageJobManager
	) {

		return jobBuilderFactory.get("Parallel Job")
			.incrementer(new RunIdIncrementer())
			.flow(importUsageJobManager)
			.end()
			.build();
	}

	@Bean
	public ProducerFactory requestProducerFactory(
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
	public ConsumerFactory repliesConsumerFactory(KafkaProperties kafkaProperties, KafkaConfigurationOptionsProvider kafkaConfigurationOptionsProvider, JsonDeserializer stepExecutionDeSerializer) {

		KafkaProperties.Options receiver = kafkaProperties.getOptions().get(SPRING_BATCH_INTEGRATION_REPLIES);

		Map<String, Object> consumerProps = new HashMap<>();
		consumerProps.put(BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
		consumerProps.put(CLIENT_ID_CONFIG, kafkaConfigurationOptionsProvider.getClientId(receiver));
		consumerProps.put(GROUP_ID_CONFIG, kafkaConfigurationOptionsProvider.getGroupId(receiver));

		consumerProps.putAll(kafkaConfigurationOptionsProvider.kafkaSecurityConfig());

		return new DefaultKafkaConsumerFactory(consumerProps, new StringDeserializer(), stepExecutionDeSerializer);
	}
}
