package com.vivu.api; // Đảm bảo đúng package của bạn

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling; // Import annotation này

@SpringBootApplication
@EnableAsync
@EnableScheduling // Thêm annotation này để bật tính năng Scheduling
public class VivuApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(VivuApiApplication.class, args);
		// Có thể thêm log để biết ứng dụng đã khởi động thành công trên cổng nào
		// Port sẽ đọc từ application.properties
		System.out.println("Vivu API application started successfully!");
	}

}