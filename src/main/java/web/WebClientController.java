package web;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import proxies.RestTemplateServiceProxy;

public class WebClientController {

	private RestTemplateServiceProxy restTemplateServiceProxy;
	private String token;
	
	 //Anade current URL y token a todas las vistas
    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {
        String currentUrl = ServletUriComponentsBuilder.fromRequestUri(request).toUriString();
        model.addAttribute("currentUrl", currentUrl); 
        model.addAttribute("token", token);
    }
}
