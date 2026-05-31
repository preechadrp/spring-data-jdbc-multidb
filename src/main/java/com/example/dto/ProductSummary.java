package com.example.dto;

//สร้างไว้ที่ไหนก็ได้ (เช่น ในโฟลเดอร์ dto หรือวางไว้ใต้ Product.java ก็ได้)
public record ProductSummary(Long id, String name) {
}