package edu.eci.arsw;

import edu.eci.arsw.service.GameServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import javax.annotation.PostConstruct;


@SpringBootApplication
@EnableRedisRepositories
@ComponentScan(basePackages = {"edu.eci.arsw"})
public class EciPixelsApplication {

	@Autowired
	private GameServices gameServices;


	public static void main(String[] args) {
		SpringApplication.run(EciPixelsApplication.class, args);
	}
	@PostConstruct
	public void initGame(){
		gameServices.createNewGame();
	}

}