package dev.ellyon.sistemanotas.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String entity, Long id) {
        super(String.format("%s não encontrado(a) com ID: %d", entity, id));
    }

    public EntityNotFoundException(String entity, String field, Object value) {
        super(String.format("%s não encontrado(a) com %s: %s", entity, field, value));
    }
}