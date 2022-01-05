package spring.batch.integration.configurations;

import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.step.builder.FaultTolerantStepBuilder;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.core.step.builder.TaskletStepBuilder;
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
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConsumerProperties;
import org.springframework.lang.Nullable;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;

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
		ConsumerFactory springBatchConsumerFactory,
		DirectChannel incomingRequestsFromManager,
		KafkaProperties kafkaProperties) {

		ConsumerProperties consumerProperties = new ConsumerProperties("spring-batch-integration-requests");
		return IntegrationFlows
			.from(Kafka.inboundChannelAdapter(springBatchConsumerFactory, consumerProperties))
			.channel(incomingRequestsFromManager)
			.get();
	}

	@Bean
	public DirectChannel outgoingRepliesToManager() {
		return new DirectChannel();
	}

	@Bean
	public IntegrationFlow outboundFlowToManager(
		ProducerFactory springBatchProducerFactory,
		DirectChannel outgoingRepliesToManager) {

		return IntegrationFlows
			.from(outgoingRepliesToManager)
			.handle(Kafka.outboundChannelAdapter(springBatchProducerFactory)
				.topic("spring-batch-integration-replies"))
			.get();
	}

	@Bean(name = "workerStep")
	public Step worker(
		RemotePartitioningWorkerStepBuilderFactory workerStepBuilderFactory,
//		UsageJobProperties usageJobProperties,
		DefaultStepExecutionListener defaultStepExecutionListener,
		@Nullable TimerStepExecutionListener timerStepExecutionListener,
		ObjectProvider<Tasklet> taskletProvider,
		ObjectProvider<ItemStreamReader> itemStreamReaderProvider,
		ObjectProvider<ItemProcessor> itemProcessorProvider,
		ObjectProvider<ItemWriter> itemWriterProvider,
		ObjectProvider<RetryPolicy> retryPolicyProvider,
		ObjectProvider<SkipPolicy> skipPolicyProvider,
		ObjectProvider<BackOffPolicy> backoffPolicyProvider,
		@Nullable ReaderTimeListener readerTimeListener,
		@Nullable WriterTimeListener writerTimeListener,
		@Nullable ProcessorTimeListener processorTimeListener) {

		log.info("Creating worker step");

		TaskletStepBuilder taskletStepBuilder;

		if (usageJobProperties.getMonthly().isTaskletBased()) {
			taskletStepBuilder = workerStepBuilderFactory.get(MONTHLY_JOB_WORKER_STEP_NAME)
				.listener(defaultStepExecutionListener)
				.inputChannel(incomingRequestsFromManager())
				.outputChannel(outgoingRepliesToManager())
				.tasklet(taskletProvider.getObject());
			if (timerStepExecutionListener != null) {
				taskletStepBuilder.listener(timerStepExecutionListener);
			}
			return taskletStepBuilder.build();
		}

		SimpleStepBuilder simpleStepBuilder = workerStepBuilderFactory.get(MONTHLY_JOB_WORKER_STEP_NAME)
			.listener(defaultStepExecutionListener)
			.inputChannel(incomingRequestsFromManager())
			.outputChannel(outgoingRepliesToManager())
			.<String, String>chunk(usageJobProperties.getMonthly().getChunkSize())
			.reader(itemStreamReaderProvider.getObject())
			.processor(itemProcessorProvider.getObject())
			.writer(itemWriterProvider.getObject())
			.listener(writerTimeListener)
			.listener(readerTimeListener)
			.listener(processorTimeListener);

		if (timerStepExecutionListener != null) {
			simpleStepBuilder.listener(timerStepExecutionListener);
		}
		return simpleStepBuilder.build();
//		return faultTolerantStepBuilder(usageJobProperties,
//			simpleStepBuilder, retryPolicyProvider, skipPolicyProvider, backoffPolicyProvider)
//			.build();
	}

	private FaultTolerantStepBuilder faultTolerantStepBuilder(
		UsageJobProperties usageJobProperties,
		SimpleStepBuilder simpleStepBuilder,
		ObjectProvider<RetryPolicy> retryPolicyProvider,
		ObjectProvider<SkipPolicy> skipPolicyProvider,
		ObjectProvider<BackOffPolicy> backoffPolicyProvider) {

		UsageJobProperties.Monthly monthlyJob = usageJobProperties.getMonthly();

		FaultTolerantStepBuilder faultTolerantStepBuilder = simpleStepBuilder.faultTolerant()
			.retryLimit(monthlyJob.getRetryLimit())
			.retryPolicy(retryPolicyProvider.getIfAvailable(() -> new SimpleRetryPolicy()))
			.skipLimit(monthlyJob.getSkipLimit())
			.skipPolicy(skipPolicyProvider.getIfAvailable())
			.backOffPolicy(backoffPolicyProvider.getIfAvailable());

		if (!isEmpty(monthlyJob.getRetryableExceptionClasses())) {
			for (String exClass : monthlyJob.getRetryableExceptionClasses()) {
				faultTolerantStepBuilder = faultTolerantStepBuilder.retry(uncheckedClassForName(exClass));
			}
		}
		if (!isEmpty(monthlyJob.getNonRetryableExceptionClasses())) {
			for (String exClass : monthlyJob.getNonRetryableExceptionClasses()) {
				faultTolerantStepBuilder = faultTolerantStepBuilder.noRetry(uncheckedClassForName(exClass));
			}
		}
		if (!isEmpty(monthlyJob.getSkippableExceptionClasses())) {
			for (String exClass : monthlyJob.getSkippableExceptionClasses()) {
				faultTolerantStepBuilder = faultTolerantStepBuilder.skip(uncheckedClassForName(exClass));
			}
		}
		if (!isEmpty(monthlyJob.getNonSkippableExceptionClasses())) {
			for (String exClass : monthlyJob.getNonSkippableExceptionClasses()) {
				faultTolerantStepBuilder = faultTolerantStepBuilder.noSkip(uncheckedClassForName(exClass));
			}
		}
		if (!isEmpty(monthlyJob.getNoRollbackExceptionClasses())) {
			for (String exClass : monthlyJob.getNoRollbackExceptionClasses()) {
				faultTolerantStepBuilder = faultTolerantStepBuilder.noRollback(uncheckedClassForName(exClass));
			}
		}

		return faultTolerantStepBuilder;
	}

	private Class uncheckedClassForName(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Could not find class: " + className);
		}
	}
}