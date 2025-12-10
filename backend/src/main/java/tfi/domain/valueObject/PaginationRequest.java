package tfi.domain.valueObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Value Object que representa una solicitud de paginación.
 * Es independiente de frameworks externos (no depende de Spring).
 * 
 * Siguiendo Clean Architecture:
 * - El dominio no debe depender de frameworks
 * - Esta abstracción permite paginación sin depender de Spring Data
 */
public class PaginationRequest {
    private final int page;
    private final int size;
    private final List<SortOrder> sortOrders;

    /**
     * Constructor para crear una solicitud de paginación.
     * 
     * @param page Número de página (0-indexed, debe ser >= 0)
     * @param size Tamaño de página (debe ser > 0)
     * @param sortOrders Lista de órdenes de clasificación (puede ser vacía)
     * @throws IllegalArgumentException si los parámetros son inválidos
     */
    public PaginationRequest(int page, int size, List<SortOrder> sortOrders) {
        if (page < 0) {
            throw new IllegalArgumentException("El número de página debe ser mayor o igual a 0");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("El tamaño de página debe ser mayor a 0");
        }
        if (sortOrders == null) {
            throw new IllegalArgumentException("La lista de órdenes no puede ser nula");
        }
        this.page = page;
        this.size = size;
        this.sortOrders = new ArrayList<>(sortOrders);
    }

    /**
     * Constructor simplificado sin ordenamiento.
     * 
     * @param page Número de página (0-indexed)
     * @param size Tamaño de página
     */
    public PaginationRequest(int page, int size) {
        this(page, size, new ArrayList<>());
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public List<SortOrder> getSortOrders() {
        return new ArrayList<>(sortOrders);
    }

    /**
     * Calcula el offset (desplazamiento) para la consulta SQL.
     * 
     * @return offset calculado
     */
    public int getOffset() {
        return page * size;
    }

    /**
     * Verifica si hay criterios de ordenamiento.
     * 
     * @return true si hay órdenes, false en caso contrario
     */
    public boolean hasSorting() {
        return !sortOrders.isEmpty();
    }

    @Override
    public String toString() {
        return "PaginationRequest{" +
                "page=" + page +
                ", size=" + size +
                ", sortOrders=" + sortOrders +
                '}';
    }
}

