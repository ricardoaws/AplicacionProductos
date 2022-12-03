package com.proyectotfg.rest.oauth2;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

@Configuration
// Anotación que sirve para habilitar el servidor de recursos para OAuth
@EnableResourceServer
public class OAuth2ResourceServer extends ResourceServerConfigurerAdapter {

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		// Indicamos el resourceID que definimos en el servidor de autorización
		resources.resourceId("oauth2-resource");
		
	}

	// Con este método la aplicación se conecta al IDP para verificar si el access token utilizado en cada request realizada
	// a cualquiera de los endpoints securizados, es un access token válido.
	@Bean
	public ResourceServerTokenServices tokenService(){
		RemoteTokenServices tokenServices = new RemoteTokenServices();
		tokenServices.setClientId("cliente");
		tokenServices.setClientSecret("123456");
		tokenServices.setCheckTokenEndpointUrl("http://46.101.31.212:30000/oauth/check_token");
		return tokenServices;

	}

	@Override
	public void configure(HttpSecurity http) throws Exception {

		http
		// Deshabilitamos el mécanismo csrf ya que con la configuración realizada en el proyecto, no lo necesitamos
		// Ya que no se podrá acceder desde una pagina maliciosa a una pagina legitima cuando ambas se abren en un mismo navegador
		// si nos hemos autenticado en la pagina legitima, que es de lo que nos protege csrf
		.csrf()
			.disable()
		// Establecemos una política de sin estado a nivel de sesiones, para forzar a no utilizar sesiones
		.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and()
		// Configuramos las reglas de autorización
		.authorizeRequests()
			// Sirve para habilitar la consola de H2
			.antMatchers("/h2-console/**").permitAll()
			// Para entender las reglas en este caso, solo los usuarios con Role "USER" pueden ejecutar un método GET sobre
			// el endpoint /producto/**
			.antMatchers(HttpMethod.GET, "/producto/**").hasRole("USER")
			.antMatchers(HttpMethod.POST, "/producto/**").hasRole("ADMIN")
			.antMatchers(HttpMethod.PUT, "/producto/**").hasRole("ADMIN")
			.antMatchers(HttpMethod.DELETE, "/producto/**").hasRole("ADMIN")
				// Para el resto de endpoints únicamente se solicita que esté autenticado
			.anyRequest().authenticated();

			// Sirve para habilitar la consola de H2
	        http.headers().frameOptions().disable();
	}
	

	
	
}
