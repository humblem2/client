package kr.co._29cm.homework.domain.vm;


import kr.co._29cm.homework.domain.Product;

/**
 * Presentation Layer 용 VO
 * '서버로의 요청'과 '서버단에서의 응답'을 추상화/캡슐화 한 도메인 객체(VM: Value Management)
 * OrderVM ───────association──────> ProductVM ───────generalization/specialization──────▷ Product
 */
public class ProductVM extends Product {
    private int orderRequestQuantity;                   // 주문 요청 수량

    private String orderStatus;                         // 주문 요청 상태

    private String orderMessage;                        // 주문 요청 결과 메세지

    public ProductVM(){
    }

    public ProductVM(int productId, int orderRequestQuantity) {
        super();
        this.setProductId(productId);
        this.orderRequestQuantity = orderRequestQuantity;
    }

    public int getOrderRequestQuantity() {
        return orderRequestQuantity;
    }

    public void setOrderRequestQuantity(int orderRequestQuantity) {
        this.orderRequestQuantity = orderRequestQuantity;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderMessage() {
        return orderMessage;
    }

    public void setOrderMessage(String orderMessage) {
        this.orderMessage = orderMessage;
    }

    @Override
    public String toString() {
        return "ProductVM{" +
                "id=" + getId() +
                ", productId=" + getProductId() +
                ", productName='" + getProductName() + '\'' +
                ", productPrice='" + getProductPrice() + '\'' +
                ", productQuantity=" + getProductQuantity() +
                ", orderRequestQuantity=" + orderRequestQuantity +
                ", orderStatus='" + orderStatus + '\'' +
                ", orderMessage='" + orderMessage + '\'' +
                '}';
    }
}
