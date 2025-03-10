/*package ro.unibuc.careerquest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import ro.unibuc.careerquest.data.UserEntity;
import ro.unibuc.careerquest.data.UserRepository;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EnableMongoRepositories(basePackageClasses = UserRepository.class)
public class CareerQuestApplication {

	@Autowired
	private UserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(CareerQuestApplication.class, args);
	}

	@PostConstruct
	public void runAfterObjectCreated() {
		userRepository.deleteAll();
	}
}*/