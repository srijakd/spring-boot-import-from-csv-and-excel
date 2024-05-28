package com.spring.importdatademo.service;

import com.spring.importdatademo.entity.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IProductService {
    List<Product> getAllProduct();

    String importProductsFromExcel(MultipartFile file);

    String importProductsFromCsv(MultipartFile file);
}
