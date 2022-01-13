package spring.batch.integration.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor
@Configuration
@StepScope
public class SimpleItemProcessor implements ItemProcessor<String, String> {
	@Override
	public String process(String item) throws Exception {
		log.info("Processing item={}", item);
		return item;
	}
}
