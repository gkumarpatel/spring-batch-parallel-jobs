package spring.batch.integration.batch;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor
@Configuration
@StepScope
public class SimpleItemWriter implements ItemWriter<String> {
	@Override
	public void write(List<? extends String> items) throws Exception {
		items.forEach(item -> log.info("Writing item={}", item));
	}
}
