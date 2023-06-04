package kr.co._29cm.homework.domain;

/**
 * 재고수량 속성을 갖고있는 '상품'을 추상화/캡슐화 한 Entity 객체
 * ProductVM ───────generalization/specialization──────▷ Product
 */
public class Product {
    private int id;                                     // 시퀀스번호

    private int productId;                              // 상품번호

    private String productName;                         // 상품명

    private String productPrice;                        // 판매가격

    private int productQuantity;                        // 재고수량

    public Product() {
    }

    public Product(int productId, int productQuantity) {
        this.productId = productId;
        this.productQuantity = productQuantity;
    }

    public Product(int id, int productId, String productName, String productPrice, int productQuantity) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productQuantity = productQuantity;
    }

    public Product(int productId) {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", productPrice='" + productPrice + '\'' +
                ", productQuantity=" + productQuantity +
                '}';
    }
}
