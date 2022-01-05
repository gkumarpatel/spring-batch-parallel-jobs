package spring.batch.integration.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class SimpleItemReader implements ItemStreamReader<String> {
	@Override
	public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		Thread.sleep(60000);
		return null;
	}

	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
	log.info("ExecutionContext={}, index={}", executionContext,  executionContext.get("index"));
	}

	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {

	}

	@Override
	public void close() throws ItemStreamException {

	}
}
