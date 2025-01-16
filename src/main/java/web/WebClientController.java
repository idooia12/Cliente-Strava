package web;

import java.time.LocalDate;
import java.util.List;

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
public class WebClientController {

    private final RestTemplateServiceProxy restTemplateServiceProxy;

    @Autowired
    public WebClientController(RestTemplateServiceProxy restTemplateServiceProxy) {
        this.restTemplateServiceProxy = restTemplateServiceProxy;
    }

    // Inicializa el token si no está presente
    @ModelAttribute("token")
    public String getToken() {
        return "";
    }

    // Página principal
    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login";
    }

    // Página de login
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        return "login";
    }

    // Procesa el login
    @PostMapping("/login")
    public String performLogin(@RequestParam("email") String email,
                               @RequestParam("password") String password,
                               Model model) {
        try {
            String token = restTemplateServiceProxy.login(email, password);
            model.addAttribute("token", token); // Guarda el token en la sesión
            return "redirect:/home";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Login failed: " + e.getMessage());
            return "login";
        }
    }

    // Página principal después del login
    @GetMapping("/home")
    public String home(@ModelAttribute("token") String token, Model model) {
        if (token == null || token.isEmpty()) {
            return "redirect:/login";
        }
        return "home";
    }

    // Página para crear un nuevo entrenamiento
    @GetMapping("/entrenamientos/crear")
    public String getAllEntrenamientos(@ModelAttribute("token") String token, Model model) {
        if (token == null || token.isEmpty()) {
            return "redirect:/login";
        }
        return "entrenamiento";
    }

    // Procesa la creación de un entrenamiento
    @PostMapping("/entrenamientos/crear")
    public String createTraining(@ModelAttribute("token") String token,
                                 @RequestParam("titulo") String titulo,
                                 @RequestParam("deporte") String deporte,
                                 @RequestParam("distanciaKm") int distanciaKm,
                                 @RequestParam("fecha_inicio") LocalDate fechaInicio,
                                 @RequestParam("duracion") int duracion,
                                 Model model) {
        if (token == null || token.isEmpty()) {
            return "redirect:/login";
        }

        try {
            restTemplateServiceProxy.crearEntrenamiento(token, titulo, deporte, fechaInicio, duracion);
            return "redirect:/home";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Error al crear el entrenamiento: " + e.getMessage());
            return "entrenamiento";
        }
    }

    // Página para crear un nuevo reto
    @GetMapping("/retos/crear")
    public String getCrearRetoPage(@ModelAttribute("token") String token, Model model) {
        if (token == null || token.isEmpty()) {
            return "redirect:/login";
        }
        return "crearReto"; // Nombre de la plantilla para crear retos
    }

    // Procesa la creación de un reto
    @PostMapping("/retos/crear")
    public String createReto(@ModelAttribute("token") String token,
                             @RequestParam("nombre") String nombre,
                             @RequestParam("fechaInicio") LocalDate fechaInicio,
                             @RequestParam("fechaFin") LocalDate fechaFin,
                             @RequestParam("objetivo") int objetivo,
                             @RequestParam("deporte") String deporte,
                             Model model) {
        if (token == null || token.isEmpty()) {
            return "redirect:/login";
        }

        try {
            Reto nuevoReto = new Reto(nombre, fechaInicio, fechaFin, objetivo, deporte);
            restTemplateServiceProxy.crearReto(token, nombre, fechaInicio, fechaFin, objetivo, deporte); // Método en el proxy para guardar retos
            return "redirect:/home";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Error al crear el reto: " + e.getMessage());
            return "crearReto";
        }
    }

    @GetMapping("/entrenamientos")
    public String showTrainings(Model model, @ModelAttribute("token") String token) {
        if (token == null || token.isEmpty()) {
            return "redirect:/login";
        }

        try {
            List<Entrenamiento> entrenamientos = restTemplateServiceProxy.getAllEntrenamientos(token);
            model.addAttribute("entrenamientos", entrenamientos);
            return "verEntrenamientos"; // Muestra la plantilla de entrenamientos
        } catch (Exception e) {
            model.addAttribute("errorMessage", "No se pudieron cargar los entrenamientos: " + e.getMessage());
            return "home";
        }
    }
    
 // Página para ver los retos existentes
    @GetMapping("/retos")
    public String showRetos(Model model, @ModelAttribute("token") String token) {
        if (token == null || token.isEmpty()) {
            return "redirect:/login";
        }

        try {
            List<Reto> retos = restTemplateServiceProxy.obtenerRetosActivos(token); // Llama al proxy para obtener los retos
            model.addAttribute("retos", retos); // Agrega la lista de retos al modelo
            return "retos"; // Nombre de la plantilla para mostrar los retos
        } catch (Exception e) {
            model.addAttribute("errorMessage", "No se pudieron cargar los retos: " + e.getMessage());
            return "home"; // Redirige a home si ocurre un error
        }
    }

    // Cierra sesión
    @GetMapping("/logout")
    public String logout(SessionStatus sessionStatus) {
        sessionStatus.setComplete(); // Limpia la sesión
        return "redirect:/login";
    }
}

