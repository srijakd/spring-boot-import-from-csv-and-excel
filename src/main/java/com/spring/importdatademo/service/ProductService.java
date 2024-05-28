package com.spring.importdatademo.service;

import com.spring.importdatademo.entity.Product;
import com.spring.importdatademo.repository.ProductRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.spring.importdatademo.utilities.ExcelUtils.processExcelData;

@Service
public class ProductService implements IProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

    @Override
    public String importProductsFromExcel(MultipartFile file) {
        try {
            List<Product> products = new ArrayList<>();
            processExcelData(file, (row, headerMap) -> {
                Product product = new Product();
                Cell nameCell = row.getCell(headerMap.get("NAME"));
                Cell priceCell = row.getCell(headerMap.get("PRICE"));
                Cell quantityCell = row.getCell(headerMap.get("QUANTITY"));

                if (nameCell != null && priceCell != null && quantityCell != null) {
                    String name = nameCell.getStringCellValue().trim();
                    double price = 0.0;
                    if (priceCell.getCellType() == CellType.NUMERIC) {
                        price = priceCell.getNumericCellValue();
                    }
                    int quantity = 0;
                    if (quantityCell.getCellType() == CellType.NUMERIC) {
                        quantity = (int) quantityCell.getNumericCellValue();
                    }
                    if (!name.isEmpty()) {
                        product.setName(name);
                        product.setPrice(price);
                        product.setQuantity(quantity);
                        products.add(product);
                    }
                }
            });

            if (!products.isEmpty()) {
                productRepository.saveAll(products);
                return "Data imported successfully from Excel.";
            } else {
                return "No valid data found in the Excel file.";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error importing data from Excel.";
        }
    }

    @Override
    public String importProductsFromCsv(MultipartFile file) {
        List<Product> products = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            Map<String, Integer> headerMap = new HashMap<>();
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");

                if (isFirstLine) {
                    for (int i = 0; i < fields.length; i++) {
                        headerMap.put(fields[i].trim(), i);
                    }
                    isFirstLine = false;
                    continue;
                }

                if (headerMap.containsKey("NAME") && headerMap.containsKey("PRICE") && headerMap.containsKey("QUANTITY")) {
                    String name = fields[headerMap.get("NAME")].trim();
                    double price = 0.0;
                    int quantity = 0;

                    try {
                        price = Double.parseDouble(fields[headerMap.get("PRICE")].trim());
                        quantity = Integer.parseInt(fields[headerMap.get("QUANTITY")].trim());
                    } catch (NumberFormatException e) {
                        // Handle parsing error if needed
                        continue;
                    }

                    if (!name.isEmpty()) {
                        Product product = new Product(name, price, quantity);
                        products.add(product);
                    }
                }
            }

            if (!products.isEmpty()) {
                productRepository.saveAll(products);
                return "Data imported successfully from CSV.";
            } else {
                return "No valid data found in the CSV file.";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error importing data from CSV.";
        }
    }
}
