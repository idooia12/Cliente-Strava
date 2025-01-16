package web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import proxies.RestTemplateServiceProxy;

@Controller
public class WebClientController {

	private final RestTemplateServiceProxy restTemplateServiceProxy;
	private String token;
	
    @Autowired
    public WebClientController(RestTemplateServiceProxy restTemplateServiceProxy) {
        this.restTemplateServiceProxy = restTemplateServiceProxy;
    }
    
	 //Anade current URL y token a todas las vistas
    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {
        String currentUrl = ServletUriComponentsBuilder.fromRequestUri(request).toUriString();
        model.addAttribute("currentUrl", currentUrl); 
        model.addAttribute("token", token);
    }
    
    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login";
    }
    //Mostrar plantilla login.html
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        return "login";
    }

    
    @PostMapping("/login")
    public String performLogin(@RequestParam("email") String email, 
                               @RequestParam("password") String password,
                               @RequestParam(value = "redirectUrl", required = false) String redirectUrl,
                               Model model) {
        try {
        	token = restTemplateServiceProxy.login(email, password);
            if (redirectUrl != null && !redirectUrl.isEmpty()) {
                return "redirect:" + redirectUrl;
            } else {
                return "redirect:/home";
            }
        }  catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Login failed: " + e.getMessage());
            return "login"; // Return to login page with error message
        }
    }
    
    //Mostrar plantilla home
    @GetMapping("/home")
    public String home(Model model) {
    	if (token != null) {
            // Si ya está autenticado, redirige a home
            return "redirect:/home";
        } else {
            // Si no está autenticado, muestra la página de login
            return "login";
        }
    }
}
