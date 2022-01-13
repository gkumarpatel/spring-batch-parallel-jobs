package spring.batch.integration.configuration;

import java.util.Collection;

import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Configuration
public class SpringBatchKafkaConfiguration {

	public static final String SPRING_BATCH_INTEGRATION_REQUESTS = "spring-batch-integration-requests";
	public static final String SPRING_BATCH_INTEGRATION_REPLIES = "spring-batch-integration-replies";

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
