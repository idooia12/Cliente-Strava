package console;


	import java.time.LocalDate;
import java.util.List;
	import org.slf4j.Logger;
	import org.slf4j.LoggerFactory;
	
	import proxies.RestTemplateServiceProxy;
	import data.Credentials;
	import data.Reto;
	import data.Entrenamiento;

	/**
	 * La clase ClienteConsola implementa un cliente básico para el servicio RetoService 
	 * en un entorno de consola. Esta clase maneja interacciones con el usuario a través 
	 * de la consola, realizando operaciones como iniciar sesión, cargar retos activos 
	 * y validar tokens. Utiliza la interfaz IRetoServiceProxy para interactuar con la 
	 * capa de servicio.
	 */
	public class ConsoleClient {
	   private RestTemplateServiceProxy proxyServicio;
		
		public ConsoleClient(RestTemplateServiceProxy proxyServicio) {
		        this.proxyServicio = proxyServicio;
		 }
	    // Token de sesión
	    private String token;
	    // Correo y contraseña predeterminados
	    private String correoPorDefecto = "manu@deusto.es";
	    private String contraseñaPorDefecto = "pass";

	    private static final Logger logger = LoggerFactory.getLogger(ConsoleClient.class);

	    public static void main(String[] args) {
	        RestTemplateServiceProxy proxy = new RestTemplateServiceProxy(new org.springframework.web.client.RestTemplate());
	        ConsoleClient cliente = new ConsoleClient(proxy);

	        if (!cliente.iniciarSesion()) {
	            logger.info("Saliendo de la aplicación por error en el inicio de sesión.");
	            return;
	        }
	       
	
	    }
	    
	    //Sesiones
	    public boolean iniciarSesion() {
	        try {
	            Credentials credenciales = new Credentials(correoPorDefecto, contraseñaPorDefecto);

	            token = proxyServicio.login(credenciales);
	            logger.info("Inicio de sesión exitoso. Token: {}", token);

	            return true;
	        } catch (RuntimeException e) {
	            logger.error("Error al iniciar sesión: {}", e.getMessage());
	            return false;
	        }
	    }
	    
	    public boolean cerrarSesion() {
	        try {
	           // Credentials credenciales = new Credentials(correoPorDefecto, contraseñaPorDefecto);
	            proxyServicio.logout(token);
	            logger.info("Cierre de sesión exitoso. Token: {}", token);
	            token = null;
	            return true;
	        } catch (RuntimeException e) {
	            logger.error("Error al cerrar sesión: {}", e.getMessage());
	            return false;
	        }
	    }
	    
	    //Entrenamientos
	    public List<Entrenamiento> getAllEntrenamientos(String token) {
	        try {
	            return proxyServicio.getAllEntrenamientos(token);
	        } catch (RuntimeException e) {
	            System.err.println("Error al obtener los entrenamientos: " + e.getMessage());
	            return List.of(); // Retornar una lista vacía para manejar el error
	        }
	    }

	    public void crearEntrenamiento(String token, String titulo, String deporte, LocalDate fechaInicio, int duracion) {
	        try {
	        	proxyServicio.crearEntrenamiento(token, titulo, deporte, fechaInicio, duracion);
	            System.out.println("Entrenamiento creado con éxito.");
	        } catch (RuntimeException e) {
	            System.err.println("Error al crear el entrenamiento: " + e.getMessage());
	        }
	    }
	    
	    //Retos
	    public void crearReto(String token, String nombre, LocalDate fechaInicio, LocalDate fechaFin, int objetivo, String deporte) {
	        try {
	        	proxyServicio.crearReto(token, nombre, fechaInicio, fechaFin, objetivo, deporte);
	            System.out.println("Reto creado con éxito.");
	        } catch (RuntimeException e) {
	            System.err.println("Error al crear el reto: " + e.getMessage());
	        }
	    }
	    
	    public List<Reto> obtenerRetosActivos(String token) {
	        try {
	            return proxyServicio.obtenerRetosActivos(token);
	        } catch (RuntimeException e) {
	            System.err.println("Error al obtener los retos activos: " + e.getMessage());
	            return List.of(); // Retornar lista vacía para manejar el error
	        }
	    }

	    public void aceptarReto(String token, String nombreReto) {
	        try {
	        	proxyServicio.aceptarReto(token, nombreReto);
	            System.out.println("Reto aceptado con éxito.");
	        } catch (RuntimeException e) {
	            System.err.println("Error al aceptar el reto: " + e.getMessage());
	        }
	    }

	    public List<Reto> consultarRetosAceptados(String token) {
	        try {
	            return proxyServicio.consultarRetosAceptados(token);
	        } catch (RuntimeException e) {
	            System.err.println("Error al consultar los retos aceptados: " + e.getMessage());
	            return List.of(); // Retornar lista vacía para manejar el error
	        }
	    }

	    
	}


