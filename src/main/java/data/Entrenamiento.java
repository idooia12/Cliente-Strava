package data;

import java.time.LocalDate;

public record Entrenamiento(
        String titulo,
        String deporte,
        int distanciaKm,
        LocalDate fechaInicio,
        int duracion // Duración en minutos
) {}
