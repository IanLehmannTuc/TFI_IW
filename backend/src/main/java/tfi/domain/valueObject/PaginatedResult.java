package tfi.domain.valueObject;

import java.util.Collections;
import java.util.List;

/**
 * Value Object que representa un resultado paginado.
 * Es independiente de frameworks externos (no depende de Spring).
 * 
 * Siguiendo Clean Architecture:
 * - El dominio no debe depender de frameworks
 * - Esta abstracción permite retornar resultados paginados sin depender de Spring Data
 */
public class PaginatedResult<T> {
    private final List<T> content;
    private final int totalElements;
    private final int totalPages;
    private final int page;
    private final int size;

    /**
     * Constructor para crear un resultado paginado.
     * 
     * @param content Lista de elementos en la página actual
     * @param totalElements Total de elementos en todas las páginas
     * @param page Número de página actual (0-indexed)
     * @param size Tamaño de página
     * @throws IllegalArgumentException si los parámetros son inválidos
     */
    public PaginatedResult(List<T> content, int totalElements, int page, int size) {
        if (content == null) {
            throw new IllegalArgumentException("El contenido no puede ser nulo");
        }
        if (totalElements < 0) {
            throw new IllegalArgumentException("El total de elementos no puede ser negativo");
        }
        if (page < 0) {
            throw new IllegalArgumentException("El número de página debe ser mayor o igual a 0");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("El tamaño de página debe ser mayor a 0");
        }
        this.content = Collections.unmodifiableList(content);
        this.totalElements = totalElements;
        this.page = page;
        this.size = size;
        this.totalPages = (int) Math.ceil((double) totalElements / size);
    }

    public List<T> getContent() {
        return content;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    /**
     * Verifica si hay una página siguiente.
     * 
     * @return true si hay página siguiente, false en caso contrario
     */
    public boolean hasNext() {
        return page < totalPages - 1;
    }

    /**
     * Verifica si hay una página anterior.
     * 
     * @return true si hay página anterior, false en caso contrario
     */
    public boolean hasPrevious() {
        return page > 0;
    }

    /**
     * Verifica si es la primera página.
     * 
     * @return true si es la primera página, false en caso contrario
     */
    public boolean isFirst() {
        return page == 0;
    }

    /**
     * Verifica si es la última página.
     * 
     * @return true si es la última página, false en caso contrario
     */
    public boolean isLast() {
        return page >= totalPages - 1;
    }

    /**
     * Obtiene el número de elementos en la página actual.
     * 
     * @return número de elementos en la página actual
     */
    public int getNumberOfElements() {
        return content.size();
    }

    @Override
    public String toString() {
        return "PaginatedResult{" +
                "content.size=" + content.size() +
                ", totalElements=" + totalElements +
                ", totalPages=" + totalPages +
                ", page=" + page +
                ", size=" + size +
                '}';
    }
}

