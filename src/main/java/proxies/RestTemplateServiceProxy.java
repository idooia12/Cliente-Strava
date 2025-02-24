package proxies;

import java.net.URLEncoder;import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import data.*;

@Service
public class RestTemplateServiceProxy {
	
    /*
    @Value("${api.base.url}")
    private String apiBaseUrl; //Configurada en application.properties
    */
    private final String apiBaseUrl = "http://localhost:8082";
    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(RestTemplateServiceProxy.class);

    public RestTemplateServiceProxy(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
 
    
    
    //--> Authorization Controller
    
    public String login(String email, String password) {
       String url = apiBaseUrl + "/api/auth/login?Email=" + email + "&Password=" + password;
        logger.info("URL: " + url);
        try {
            String token = restTemplate.postForObject(url,null, String.class);
            logger.info("Token: " + token);
            return token;
        } catch (HttpStatusCodeException e) {
            logger.error("Error al iniciar sesión: Código {}, Respuesta: {}", 
                    e.getStatusCode(), 
                    e.getResponseBodyAsString());
            switch (e.getStatusCode().value()) {
                case 401 -> throw new RuntimeException("Login failed: Invalid credentials.");
                default -> throw new RuntimeException("Login failed: " + e.getStatusText());
            }
        }
    }
        
    public void logout(String token) {
        String url = apiBaseUrl + "/api/auth/logout?Token=" + token;
        try {
            restTemplate.postForObject(url, token, Void.class);
        } catch (HttpStatusCodeException e) {
            switch (e.getStatusCode().value()) {
                case 401 -> throw new RuntimeException("Logout failed: Invalid token.");
                default -> throw new RuntimeException("Logout failed: " + e.getStatusText());
            }
        }
    }
    
    //TODO REGISTRO
    
    // --> Entrenamiento Controller
    @SuppressWarnings("unchecked")
	public List<Entrenamiento> getAllEntrenamientos(String token) {
        String url = apiBaseUrl + "/api/entrenamientos/listar?Token=" + token;
        logger.info("URL: " + url);
        try {
            return restTemplate.getForObject(url, List.class);
        } catch (HttpStatusCodeException e) {
            switch (e.getStatusCode().value()) {
                case 404 -> throw new RuntimeException("Enrenamientos no encontrados");
                default -> throw new RuntimeException("Fallo al buscar entrenamientos: " + e.getStatusText());
            }
        }
    }
    
	public void crearEntrenamiento(String token, String titulo, String deporte, int distancia, LocalDate fechaInicio, int duracion) {
		String url = apiBaseUrl + "/api/entrenamientos/crear";
        logger.info("URL: " + url);
		try {
            Entrenamiento entrenamiento = new Entrenamiento(titulo, deporte, distancia, fechaInicio, duracion);
            restTemplate.postForObject(url, entrenamiento, Void.class);
        } catch (HttpStatusCodeException e) {
            switch (e.getStatusCode().value()) {
                case 400 -> throw new RuntimeException("Datos de entrenamiento inválidos.");
                case 401 -> throw new RuntimeException("Token inválido. No autorizado.");
                case 500 -> throw new RuntimeException("Error interno al crear el entrenamiento.");
                default -> throw new RuntimeException("Error desconocido: " + e.getStatusText());
            }
        }
    }
	
	// --> Reto Controller
	public void crearReto(String token, String nombre, LocalDate fechaInicio, LocalDate fechaFin, int objetivo, String deporte) {
	    String url = apiBaseUrl + "/api/retos/crear" +
	                 "?Nombre=" + nombre +
	                 "&Fecha%20de%20Inicio=" + fechaInicio +
	                 "&Fecha%20de%20Fin=" + fechaFin +
	                 "&Objetivo%20del%20Reto=" + objetivo +
	                 "&Deporte=" + deporte;
        logger.info("URL: " + url);
	    try {
	        restTemplate.postForObject(url, token, Void.class);
	    } catch (HttpStatusCodeException e) {
	        switch (e.getStatusCode().value()) {
	            case 401 -> throw new RuntimeException("Token inválido");
	            case 409 -> throw new RuntimeException("El reto ya existe");
	            case 400 -> throw new RuntimeException("Datos del reto inválidos: " + e.getResponseBodyAsString());
	            case 500 -> throw new RuntimeException("Error interno del servidor");
	            default -> throw new RuntimeException("Error al crear el reto. Código de estado: " + e.getStatusCode());
	        }
	    }
	}
	
	 @SuppressWarnings("unchecked")
	    public List<Reto> obtenerRetosActivos(String token) {
	        String url = apiBaseUrl + "/api/retos/activos?Token=" + token;
	        logger.info("URL: " + url);
	        try {
	            return restTemplate.getForObject(url, List.class);
	        } catch (HttpStatusCodeException e) {
	            switch (e.getStatusCode().value()) {
	                case 401 -> throw new RuntimeException("Token inválido");
	                case 204 -> throw new RuntimeException("No hay retos activos");
	                default -> throw new RuntimeException("Error al obtener retos activos: " + e.getStatusText());
	            }
	        }
	    }
	 
	 public void aceptarReto(String token, String nombreReto) {
	        String url = apiBaseUrl + "/api/retos/aceptar?Token=" + token + "&retoNombre=" + nombreReto;
	        logger.info("URL: " + url);
	        try {
	            restTemplate.postForObject(url, null, String.class);
	        } catch (HttpStatusCodeException e) {
	            switch (e.getStatusCode().value()) {
	                case 401 -> throw new RuntimeException("Token inválido");
	                case 404 -> throw new RuntimeException("Reto no encontrado");
	                default -> throw new RuntimeException("Error al aceptar el reto: " + e.getStatusText());
	            }
	        }
	    }
	 
	 @SuppressWarnings("unchecked")
	    public List<Reto> consultarRetosAceptados(String token) {
	        String url = apiBaseUrl + "/api/retos/aceptados?Token=" + token;
	        logger.info("URL: " + url);
	        try {
	            return restTemplate.getForObject(url,List.class);
	        } catch (HttpStatusCodeException e) {
	            switch (e.getStatusCode().value()) {
	                case 401 -> throw new RuntimeException("Token inválido");
	                case 204 -> throw new RuntimeException("No hay retos aceptados");
	                default -> throw new RuntimeException("Error al consultar retos aceptados: " + e.getStatusText());
	            }
	        }
	    }
}
