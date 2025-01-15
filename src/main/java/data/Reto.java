package data;

import java.time.LocalDate;

public record Reto(
		String nombre,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        int objetivo,
        String deporte) {}
