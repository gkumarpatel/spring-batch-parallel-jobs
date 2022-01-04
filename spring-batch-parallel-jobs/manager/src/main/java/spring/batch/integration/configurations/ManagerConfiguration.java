package spring.batch.integration.configurations;

import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.integration.partition.RemotePartitioningManagerStepBuilderFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConsumerProperties;

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
		ProducerFactory springBatchProducerFactory,
		DirectChannel outgoingRequestsToWorkers,
		KafkaProperties kafkaProperties) {

		return IntegrationFlows
			.from(outgoingRequestsToWorkers)
			.handle(Kafka.outboundChannelAdapter(springBatchProducerFactory)
				.topic("spring-batch-integration-requests"))
			.get();
	}


	@Bean
	public DirectChannel incomingRepliesFromWorkers() {
		return new DirectChannel();
	}

	@Bean
	public IntegrationFlow inboundFlowFromWorker(
		ConsumerFactory springBatchConsumerFactory,
		DirectChannel incomingRepliesFromWorkers,
		KafkaProperties kafkaProperties) {

		ConsumerProperties consumerProperties = new ConsumerProperties("spring-batch-integration-replies");
			return IntegrationFlows
			.from(Kafka.inboundChannelAdapter(springBatchConsumerFactory, consumerProperties))
			.channel(incomingRepliesFromWorkers)
			.get();
	}

	@Bean
	public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
		// this ensures all predefined Jobs are registered at startup
		JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
		jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
		return jobRegistryBeanPostProcessor;
	}

	@Bean
	public Step importUsageJobManager(
		RemotePartitioningManagerStepBuilderFactory managerStepBuilderFactory,
		//StepExecutionListener stepExecutionListener,
		DirectChannel outgoingRequestsToWorkers,
		DirectChannel incomingRepliesFromWorkers,
		ObjectProvider<Partitioner> partitionerProvider) {

		return managerStepBuilderFactory.get("manager")
			.<String, String>partitioner("worker", partitionerProvider.getIfAvailable())
			//.gridSize(usageJobProperties.getMonthly().getGridSize())
			.outputChannel(outgoingRequestsToWorkers)
			.inputChannel(incomingRepliesFromWorkers)
			//.listener(stepExecutionListener)
			.build();
	}

	@Bean
	public Job monthlyUsageJob(
		JobBuilderFactory jobBuilderFactory,
		Step importUsageJobManager
		//,
		//JobExecutionListener jobExecutionListener
	) {

		return jobBuilderFactory.get("Parallel Job")
			.incrementer(new RunIdIncrementer())
			.flow(importUsageJobManager)
			.end()
			//.listener(jobExecutionListener)
			.build();
	}
}
