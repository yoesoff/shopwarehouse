package com.mhyusuf.service;

import com.mhyusuf.dto.SaleDto;
import com.mhyusuf.dto.SaleItemDto;
import com.mhyusuf.entity.Sale;
import com.mhyusuf.entity.SaleItem;
import com.mhyusuf.entity.Variant;
import com.mhyusuf.exception.BadRequestException;
import com.mhyusuf.exception.ResourceNotFoundException;
import com.mhyusuf.repository.SaleItemRepository;
import com.mhyusuf.repository.SaleRepository;
import com.mhyusuf.repository.VariantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SalesService
 * Demonstrates business logic coverage: creation, validation, and stock handling.
 */
class SalesServiceTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private SaleItemRepository saleItemRepository;

    @Mock
    private VariantRepository variantRepository;

    @Mock
    private StockService stockService;

    @InjectMocks
    private SalesService salesService;

    private Variant variant;
    private SaleDto saleDto;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);

        variant = Variant.builder()
                .id(UUID.randomUUID())
                .variantName("Boxing Gloves Medium")
                .price(new BigDecimal("75000"))
                .build();

        SaleItemDto item = SaleItemDto.builder()
                .variantId(variant.getId())
                .quantity(2)
                .build();

        saleDto = SaleDto.builder()
                .customerName("John Doe")
                .items(List.of(item))
                .build();
    }

    @Test
    @DisplayName("should create sale successfully and calculate totals")
    void shouldCreateSaleSuccessfully() {
        when(variantRepository.findById(variant.getId())).thenReturn(Optional.of(variant));
        when(saleRepository.save(any(Sale.class))).thenAnswer(inv -> {
            Sale s = inv.getArgument(0);
            s.setId(UUID.randomUUID());
            s.setCreatedAt(LocalDateTime.now());
            return s;
        });

        SaleDto result = salesService.createSale(saleDto);

        assertThat(result).isNotNull();
        assertThat(result.getCustomerName()).isEqualTo("John Doe");
        assertThat(result.getTotalAmount()).isEqualByComparingTo("150000");
        verify(stockService).decreaseStock(variant.getId(), 2);
        verify(saleRepository).save(any(Sale.class));
    }

    @Test
    @DisplayName("should throw ResourceNotFoundException when variant not found")
    void shouldThrowResourceNotFoundWhenVariantMissing() {
        when(variantRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> salesService.createSale(saleDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Variant not found");
    }

    @Test
    @DisplayName("should throw BadRequestException when quantity is zero or negative")
    void shouldThrowBadRequestWhenQuantityInvalid() {
        saleDto.getItems().get(0).setQuantity(0);
        when(variantRepository.findById(variant.getId())).thenReturn(Optional.of(variant));

        assertThatThrownBy(() -> salesService.createSale(saleDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Quantity must be greater than zero");
    }

    @Test
    @DisplayName("should throw BadRequestException when stock is not enough")
    void shouldThrowBadRequestWhenStockNotEnough() {
        when(variantRepository.findById(variant.getId())).thenReturn(Optional.of(variant));

        doThrow(new BadRequestException("Not enough stock for variant: " + variant.getVariantName()))
                .when(stockService).decreaseStock(variant.getId(), 2);

        assertThatThrownBy(() -> salesService.createSale(saleDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Not enough stock for variant");

        verify(stockService).decreaseStock(variant.getId(), 2);
        verify(saleRepository, never()).save(any(Sale.class));
    }

    @Test
    @DisplayName("should fetch all sales and map to DTOs correctly")
    void shouldReturnAllSales() {
        Sale sale = new Sale();
        sale.setId(UUID.randomUUID());
        sale.setCustomerName("John Doe");
        sale.setTotalAmount(new BigDecimal("150000"));
        sale.setCreatedAt(LocalDateTime.now());
        sale.setItems(List.of(
                SaleItem.builder()
                        .variant(variant)
                        .quantity(2)
                        .subtotal(new BigDecimal("150000"))
                        .build()
        ));

        when(saleRepository.findAll()).thenReturn(List.of(sale));

        List<SaleDto> result = salesService.getAllSales();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCustomerName()).isEqualTo("John Doe");
        assertThat(result.get(0).getItems()).hasSize(1);
    }

    @Test
    @DisplayName("should fetch sale by ID successfully")
    void shouldReturnSaleById() {
        UUID id = UUID.randomUUID();
        Sale sale = new Sale();
        sale.setId(id);
        sale.setCustomerName("Jane Smith");
        sale.setTotalAmount(new BigDecimal("100000"));
        sale.setCreatedAt(LocalDateTime.now());
        sale.setItems(List.of());

        when(saleRepository.findById(id)).thenReturn(Optional.of(sale));

        SaleDto result = salesService.getSaleById(id);

        assertThat(result).isNotNull();
        assertThat(result.getCustomerName()).isEqualTo("Jane Smith");
        verify(saleRepository).findById(id);
    }
}
