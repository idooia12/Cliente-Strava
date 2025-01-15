package data;

import java.time.LocalDate;

public record Entrenamiento(
        String titulo,
        String deporte,
        LocalDate fechaInicio,
        int duracion // Duración en minutos
) {}
