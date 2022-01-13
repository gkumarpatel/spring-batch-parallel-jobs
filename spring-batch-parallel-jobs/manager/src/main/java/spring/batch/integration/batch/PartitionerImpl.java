package spring.batch.integration.batch;

import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor
@Configuration
@StepScope
public class PartitionerImpl implements Partitioner {

	@Value("#{stepExecution}")
	private StepExecution stepExecution;

	public Map<String, ExecutionContext> partition(int gridSize) {
		log.info("### Partitioning for JobExecutionId:{}", stepExecution.getJobExecutionId());
		Map<String, ExecutionContext> partitions = new HashMap<String, ExecutionContext>();
		//Number of partitions = 3
		for (int i = 0; i < 1; i++) {
			ExecutionContext executionContext = new ExecutionContext();
			executionContext.putInt("index", (i + 1));
			partitions.put("partition" + (i + 1), executionContext);
		}
		log.info("### Partitions: {} created for jobId: {}", partitions, stepExecution.getJobExecutionId());
		return partitions;
	}
}
