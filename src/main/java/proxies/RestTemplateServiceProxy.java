package proxies;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import data.*;

@Service
public class RestTemplateServiceProxy {
	
    private final RestTemplate restTemplate;
    @Value("${api.base.url}")
    private String apiBaseUrl; //Configurada en application.properties
    
    public RestTemplateServiceProxy(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    
    //--> Authorization Controller
    
    public String login(Credentials credentials) {
        String url = apiBaseUrl + "/auth/login";
        
        try {
            return restTemplate.postForObject(url, credentials, String.class); // Lo mismo que HTTPreques
        } catch (HttpStatusCodeException e) {
            switch (e.getStatusCode().value()) {
                case 401 -> throw new RuntimeException("Login failed: Invalid credentials.");
                default -> throw new RuntimeException("Login failed: " + e.getStatusText());
            }
        }
    }
        
    public void logout(String token) {
        String url = apiBaseUrl + "/auth/logout";
        
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
        String url = apiBaseUrl + "/entrenamientos";
        try {
            return restTemplate.getForObject(url, List.class);
        } catch (HttpStatusCodeException e) {
            switch (e.getStatusCode().value()) {
                case 404 -> throw new RuntimeException("Enrenamientos no encontrados");
                default -> throw new RuntimeException("Fallo al buscar entrenamientos: " + e.getStatusText());
            }
        }
    }
    
	public void crearEntrenamiento(String token, String titulo, String deporte, LocalDate fechaInicio, int duracion) {
		String url = apiBaseUrl + "/entrenamientos/crear";

		try {
            Entrenamiento entrenamiento = new Entrenamiento(titulo, deporte, fechaInicio, duracion);
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
	



}
