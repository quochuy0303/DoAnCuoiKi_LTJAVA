package com.hutech.VoTranQuocHuy430.service;

import com.hutech.VoTranQuocHuy430.model.Product;
import com.hutech.VoTranQuocHuy430.model.Category;
import com.hutech.VoTranQuocHuy430.model.Manufacturer;
import com.hutech.VoTranQuocHuy430.model.Brand;
import com.hutech.VoTranQuocHuy430.repository.BrandRepository;
import com.hutech.VoTranQuocHuy430.repository.CategoryRepository;
import com.hutech.VoTranQuocHuy430.repository.ManufacturerRepository;
import com.hutech.VoTranQuocHuy430.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import jakarta.validation.constraints.NotNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ManufacturerRepository manufacturerRepository;

    @Autowired
    private BrandRepository brandRepository;

    public List<Product> getTop3Products() {
        return productRepository.findTop3ByOrderByOrderDetailsDesc();
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public Page<Product> getAllProductsPageable(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public Product update(@NotNull Product product) {
        Product existingProduct = productRepository.findById(product.getId())
                .orElseThrow(() -> new IllegalStateException("Product with ID " +
                        product.getId() + " does not exist."));
        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setImage(product.getImage());
        return productRepository.save(existingProduct);
    }

    public void deleteProductById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalStateException("Product with ID " + id + " does not exist.");
        }
        productRepository.deleteById(id);
    }

    public Page<Product> searchProducts(String search, String category, Double priceFrom, Double priceTo, Pageable pageable) {
        return productRepository.searchProducts(search, category, priceFrom, priceTo, pageable);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Manufacturer> getAllManufacturers() {
        return manufacturerRepository.findAll();
    }

    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    public List<String> findProductNamesByKeyword(String keyword) {
        return productRepository.findProductNamesByKeyword(keyword);
    }
}
