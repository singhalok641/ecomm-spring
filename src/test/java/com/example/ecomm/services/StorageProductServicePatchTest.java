package com.example.ecomm.services;

import com.example.ecomm.dtos.CategoryPatchDto;
import com.example.ecomm.dtos.ProductPatchDto;
import com.example.ecomm.models.Category;
import com.example.ecomm.models.Product;
import com.example.ecomm.models.State;
import com.example.ecomm.repository.CategoryRepository;
import com.example.ecomm.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // shared fixtures below are not used by every test
class StorageProductServicePatchTest {

    private static final UUID PRODUCT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID MISSING_ID = UUID.fromString("00000000-0000-0000-0000-000000000099");
    private static final UUID CATEGORY_ID = UUID.fromString("00000000-0000-0000-0000-000000000007");
    private static final UUID NEW_CATEGORY_ID = UUID.fromString("00000000-0000-0000-0000-000000000042");

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private StorageProductService service;
    private Product stored;
    private Category storedCategory;

    @BeforeEach
    void setUp() {
        service = new StorageProductService(productRepository, categoryRepository);

        storedCategory = new Category();
        storedCategory.setId(CATEGORY_ID);
        storedCategory.setName("beauty");
        storedCategory.setDescription("makeup");

        stored = new Product();
        stored.setId(PRODUCT_ID);
        stored.setName("Mascara");
        stored.setDescription("Volumising mascara");
        stored.setPrice(new BigDecimal("9.99"));
        stored.setImageUrl("mascara.png");
        stored.setCategory(storedCategory);
        stored.setState(State.ACTIVE);
        stored.setCreatedAt(new Date(1_000L));

        when(productRepository.findByIdAndState(PRODUCT_ID, State.ACTIVE)).thenReturn(Optional.of(stored));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    @DisplayName("only the field that is sent changes, everything else is kept")
    void patch_onlySentFieldChanges() {
        ProductPatchDto patch = new ProductPatchDto();
        patch.setPrice(Optional.of(new BigDecimal("12.50")));

        Product patched = service.patchProduct(patch, PRODUCT_ID);

        assertThat(patched.getPrice()).isEqualByComparingTo("12.50");
        assertThat(patched.getName()).isEqualTo("Mascara");
        assertThat(patched.getDescription()).isEqualTo("Volumising mascara");
        assertThat(patched.getImageUrl()).isEqualTo("mascara.png");
        assertThat(patched.getCategory()).isSameAs(storedCategory);
        assertThat(patched.getCreatedAt()).isEqualTo(new Date(1_000L));
        assertThat(patched.getLastUpdatedAt()).isAfterOrEqualTo(new Date(1_000L));
        verify(productRepository).save(stored);
    }

    @Test
    @DisplayName("a field sent as null is cleared")
    void patch_explicitNullClearsField() {
        ProductPatchDto patch = new ProductPatchDto();
        patch.setDescription(Optional.empty());
        patch.setImageUrl(Optional.empty());

        Product patched = service.patchProduct(patch, PRODUCT_ID);

        assertThat(patched.getDescription()).isNull();
        assertThat(patched.getImageUrl()).isNull();
        assertThat(patched.getName()).isEqualTo("Mascara");
    }

    @Test
    @DisplayName("an empty patch still bumps lastUpdatedAt and saves")
    void patch_emptyPatchOnlyTouchesTimestamp() {
        Product patched = service.patchProduct(new ProductPatchDto(), PRODUCT_ID);

        assertThat(patched.getName()).isEqualTo("Mascara");
        assertThat(patched.getLastUpdatedAt()).isNotNull();
        verify(productRepository).save(stored);
    }

    @Test
    @DisplayName("an unknown or inactive product returns null and is not saved")
    void patch_missingProduct_returnsNull() {
        when(productRepository.findByIdAndState(MISSING_ID, State.ACTIVE)).thenReturn(Optional.empty());

        assertThat(service.patchProduct(new ProductPatchDto(), MISSING_ID)).isNull();
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("a known category name reuses the existing category row")
    void patch_categoryByName_reusesExisting() {
        when(categoryRepository.findByName("beauty")).thenReturn(Optional.of(storedCategory));

        Product patched = service.patchProduct(categoryPatch(Optional.of("beauty"), null), PRODUCT_ID);

        assertThat(patched.getCategory()).isSameAs(storedCategory);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("an unknown category name creates the category")
    void patch_unknownCategory_createsIt() {
        when(categoryRepository.findByName("home")).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category created = invocation.getArgument(0);
            created.setId(NEW_CATEGORY_ID);
            return created;
        });

        Product patched = service.patchProduct(categoryPatch(Optional.of("home"), null), PRODUCT_ID);

        assertThat(patched.getCategory().getName()).isEqualTo("home");
        assertThat(patched.getCategory().getId()).isEqualTo(NEW_CATEGORY_ID);
    }

    @Test
    @DisplayName("only the category description can be patched without resending its name")
    void patch_categoryDescriptionOnly_updatesCurrentCategory() {
        when(categoryRepository.findByName("beauty")).thenReturn(Optional.of(storedCategory));
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product patched = service.patchProduct(categoryPatch(null, Optional.of("clean beauty")), PRODUCT_ID);

        assertThat(patched.getCategory()).isSameAs(storedCategory);
        assertThat(storedCategory.getDescription()).isEqualTo("clean beauty");
        verify(categoryRepository).save(storedCategory);
    }

    @Test
    @DisplayName("category sent as null detaches it from the product")
    void patch_categoryNull_detachesCategory() {
        ProductPatchDto patch = new ProductPatchDto();
        patch.setCategory(Optional.empty());

        Product patched = service.patchProduct(patch, PRODUCT_ID);

        assertThat(patched.getCategory()).isNull();
        verify(categoryRepository, never()).save(any(Category.class));
    }

    private ProductPatchDto categoryPatch(Optional<String> name, Optional<String> description) {
        CategoryPatchDto category = new CategoryPatchDto();
        category.setName(name);
        category.setDescription(description);

        ProductPatchDto patch = new ProductPatchDto();
        patch.setCategory(Optional.of(category));
        return patch;
    }
}
