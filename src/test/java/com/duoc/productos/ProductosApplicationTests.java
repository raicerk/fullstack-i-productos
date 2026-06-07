package com.duoc.productos;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ProductosApplicationTests {

	@Test
	@DisplayName("El contexto de Spring carga correctamente")
	void contextLoads() {
	}

}
