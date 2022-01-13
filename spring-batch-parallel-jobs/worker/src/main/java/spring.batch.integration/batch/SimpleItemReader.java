package spring.batch.integration.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor
@Configuration
@StepScope
public class SimpleItemReader implements ItemStreamReader<String> {
	private String index;
	private int itemCount = 0;

	@Value("#{stepExecution}")
	private StepExecution stepExecution;

	@Override
	public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		log.info("Reading item={} for index={}", itemCount, index);
		while (itemCount < 5) {
			Thread.sleep(10000);
			itemCount++;
			return index + itemCount;
		}
		log.info("Reached at the end of reading");
		return null;
	}

	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		index = executionContext.get("index").toString();
		JobParameters jobParameters = stepExecution.getJobParameters();
		log.info("jobParameters={}", jobParameters);
		log.info("ExecutionContext={}, index={}", executionContext, index);
	}

	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {

	}

	@Override
	public void close() throws ItemStreamException {

	}
}
