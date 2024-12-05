package com.example.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.backend.scheduler.CrawlingScheduler;

@SpringBootTest
public class CrawlingSchedulerTest {

	@Autowired
	CrawlingScheduler crawlingScheduler;
	
	@Test
	public void testRunCrawlingScript() {
		crawlingScheduler.runPythonScript();
	}
}
