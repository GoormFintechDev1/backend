package com.example.backend.scheduler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CrawlingScheduler {
	@Value("${card.py.file.path:card_crawler.py}")
	private String pythonScriptPath; // Pythen 파일 경로
	private String pythonCommand = "python"; // 시스템에 설치된 Python 실행 경로

	@Scheduled(cron = "0 0 9 * * Mon") // 매주 월요일 오전 9시
	public void runPythonScript() {
		
		try {
			ProcessBuilder processBuilder = new ProcessBuilder(pythonCommand, pythonScriptPath);
			processBuilder.redirectErrorStream(true);
			
			Process process = processBuilder.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			
			System.out.println("Python 스크립트 실행 중...");
			while((line = reader.readLine()) != null) {
				System.out.println(line); // Python 스크립트의 출력 로그를 Spring 로그에 표
			}
			
			int exitCode = process.waitFor();
			if (exitCode == 0) {
				System.out.println("Python 스크립트가 성공적으로 실행되었습니다.");
			} else {
				System.err.println("Python 스크립트 실행 실패. 종료 코드: " + exitCode);
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
