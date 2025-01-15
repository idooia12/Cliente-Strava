package swing;


import data.Credentials;
import data.Entrenamiento;
import data.Reto;
import proxies.RestTemplateServiceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * SwingClientController class acts as a Controller in the Model-View-Controller (MVC) 
 * architectural pattern. It manages the interaction between the SwingClient (View) 
 * and the RestTemplateServiceProxy (Model).
 * 
 * This class encapsulates logic for user authentication (login/logout), 
 * managing trainings and challenges, and providing an abstraction layer between 
 * the GUI and the backend services.
 */
@Component
public class SwingClientController {

    private final RestTemplateServiceProxy serviceProxy;
    private String token; // Token de sesión para autenticación

    @Autowired
    public SwingClientController(RestTemplateServiceProxy serviceProxy) {
        this.serviceProxy = serviceProxy;
    }

    /**
     * Inicia sesión con las credenciales proporcionadas.
     *
     * @param email    Correo del usuario.
     * @param password Contraseña del usuario.
     * @return Verdadero si el inicio de sesión fue exitoso.
     */
    public boolean login(String email, String password) {
        try {
            Credentials credentials = new Credentials(email, password);
            token = serviceProxy.login(credentials);
            return true;
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al iniciar sesión: " + e.getMessage());
        }
    }

    /**
     * Cierra la sesión actual.
     */
    public void logout() {
        if (token == null) {
            throw new IllegalStateException("No hay sesión activa para cerrar.");
        }
        token = null; // Simula el cierre de sesión.
    }

    /**
     * Obtiene todos los entrenamientos disponibles.
     *
     * @return Lista de entrenamientos.
     */
    public List<Entrenamiento> getEntrenamientos() {
        return serviceProxy.getAllEntrenamientos(token);
    }

    /**
     * Crea un nuevo entrenamiento.
     *
     * @param titulo       Título del entrenamiento.
     * @param deporte      Deporte asociado al entrenamiento.
     * @param fechaInicio  Fecha de inicio.
     * @param duracion     Duración del entrenamiento en minutos.
     */
    public void crearEntrenamiento(String titulo, String deporte, LocalDate fechaInicio, int duracion) {
        serviceProxy.crearEntrenamiento(token, titulo, deporte, fechaInicio, duracion);
    }

    /**
     * Obtiene una lista de retos activos.
     *
     * @return Lista de retos activos.
     */
    public List<Reto> getRetosActivos() {
        return serviceProxy.obtenerRetosActivos(token);
    }

    /**
     * Acepta un reto específico.
     *
     * @param nombreReto Nombre del reto.
     */
    public void aceptarReto(String nombreReto) {
        serviceProxy.aceptarReto(token, nombreReto);
    }

    /**
     * Obtiene una lista de retos aceptados.
     *
     * @return Lista de retos aceptados.
     */
    public List<Reto> getRetosAceptados() {
        return serviceProxy.consultarRetosAceptados(token);
    }

    /**
     * Crea un nuevo reto.
     *
     * @param nombre       Nombre del reto.
     * @param fechaInicio  Fecha de inicio.
     * @param fechaFin     Fecha de fin.
     * @param objetivo     Objetivo del reto.
     * @param deporte      Deporte asociado al reto.
     */
    public void crearReto(String nombre, LocalDate fechaInicio, LocalDate fechaFin, int objetivo, String deporte) {
        serviceProxy.crearReto(token, nombre, fechaInicio, fechaFin, objetivo, deporte);
    }
    
}


