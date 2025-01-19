package web;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import data.Entrenamiento;
import data.Reto; // Asegúrate de importar la clase Reto
import proxies.RestTemplateServiceProxy;

@Controller
@SessionAttributes("token")
public class WebClientController { //Clase con todo el redireccionamiento y control de la web. 
	
    private final RestTemplateServiceProxy restTemplateServiceProxy; //Se comunica con el restTemplateServiceProxy praa q este mande ejecutar los métodos en el servidor Strava.
    private static final Logger logger = LoggerFactory.getLogger(WebClientController.class);


    @Autowired
    public WebClientController(RestTemplateServiceProxy restTemplateServiceProxy) {
        this.restTemplateServiceProxy = restTemplateServiceProxy;
    }

    @ModelAttribute("token")
    public String getToken() {
        return "";
    }
    
    private void validateToken(String token) {
        if (token == null || token.isEmpty()) {
        	logger.error("Token inválido o no proporcionado");
            throw new RuntimeException("Token inválido o no proporcionado");
        }
    }


    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        return "login";
    }

    @PostMapping("/login")
    public String performLogin(@RequestParam("email") String email,
                               @RequestParam("password") String password,
                               Model model) {
        try {
            String token = restTemplateServiceProxy.login(email, password);
            model.addAttribute("token", token);
            return "redirect:/home";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Login failed: " + e.getMessage());
            return "login";
        }
    }

    @GetMapping("/home")
    public String home(@ModelAttribute("token") String token, Model model) {
        validateToken(token);
        return "home";
    }

    @GetMapping("/entrenamientos/crear")
    public String getAllEntrenamientos(@ModelAttribute("token") String token, Model model) {
        validateToken(token);
        logger.info("Redirecciona para crear entrenamiento");
        return "entrenamiento";
    }

    @PostMapping("/entrenamientos/crear")
    public String createTraining(@ModelAttribute("token") String token,
                                 @RequestParam("titulo") String titulo,
                                 @RequestParam("deporte") String deporte,
                                 @RequestParam("distanciaKm") int distanciaKm,
                                 @RequestParam("fecha_inicio") LocalDate fechaInicio,
                                 @RequestParam("duracion") int duracion,
                                 Model model) {
        validateToken(token);
        try {
            restTemplateServiceProxy.crearEntrenamiento(token, titulo, deporte,distanciaKm, fechaInicio, duracion);
            logger.info("Entrenamiento creado con éxito");
            return "redirect:/home";
        } catch (RuntimeException e) {
            logger.error("Error al crear Entrenamiento: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Error al crear el entrenamiento: " + e.getMessage());
            return "entrenamiento";
        }
    }

    @GetMapping("/entrenamiento/ver") // Ruta actualizada
    public String showTrainings(Model model, @ModelAttribute("token") String token) {
        validateToken(token);
        try {
            List<Entrenamiento> entrenamientos = restTemplateServiceProxy.getAllEntrenamientos(token);
            model.addAttribute("entrenamientos", entrenamientos);
            return "verEntrenamientos"; // Muestra la plantilla de entrenamientos
        } catch (Exception e) {
            model.addAttribute("errorMessage", "No se pudieron cargar los entrenamientos: " + e.getMessage());
            return "home";
        }
    }

    @GetMapping("/ruta/ver") // Ruta actualizada
    public String showRetos(Model model, @ModelAttribute("token") String token) {
        validateToken(token);
        try {
            List<Reto> retos = restTemplateServiceProxy.obtenerRetosActivos(token);
            model.addAttribute("retos", retos);
            return "retos";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "No se pudieron cargar los retos: " + e.getMessage());
            return "home";
        }
    }

    public String logout(@ModelAttribute("token") String token, SessionStatus sessionStatus) {
        validateToken(token);
    	try {
            restTemplateServiceProxy.logout(token);
        } catch (RuntimeException e) {
            logger.warn("Error al cerrar sesión en el backend: {}", e.getMessage());
        }
        sessionStatus.setComplete();
        logger.info("Sesión cerrada con éxito");
        return "redirect:/login";
    }

}


