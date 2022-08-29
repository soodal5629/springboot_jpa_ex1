package jpabook.jpashop;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JpashopApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpashopApplication.class, args);
	}

	@Bean
	Hibernate5Module hibernate5Module(){ // JSON 생성 시점에 LAZY 설정인 것은 다 무시 -> BUT DB 조회 가능할 수 있는 옵션 있긴 함
		return new Hibernate5Module();
		// hibernate5Module.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true); --> LAZY 로딩 애들 다 데리고 옴
	}
}
