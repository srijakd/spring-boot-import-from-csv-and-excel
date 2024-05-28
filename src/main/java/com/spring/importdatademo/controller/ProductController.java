package com.spring.importdatademo.controller;

import com.spring.importdatademo.service.IProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/product")
public class ProductController {
    private final IProductService productService;

    public ProductController(IProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/import/excel")
    public String importProductsFromExcel(@RequestParam("file") MultipartFile file) {
        return productService.importProductsFromExcel(file);
    }

    @PostMapping("/import/csv")
    public String importProductsFromCsv(@RequestParam("file") MultipartFile file) {
        return productService.importProductsFromCsv(file);
    }

    @GetMapping("/")
    public ResponseEntity<?> getAll() {
        return new ResponseEntity<>(productService.getAllProduct(), HttpStatus.OK);
    }
}
