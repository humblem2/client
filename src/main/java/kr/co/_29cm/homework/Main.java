package kr.co._29cm.homework;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co._29cm.homework.common.Constant;
import kr.co._29cm.homework.common.ServerEndPoint;
import kr.co._29cm.homework.common.ServerURI;
import kr.co._29cm.homework.domain.Product;
import kr.co._29cm.homework.domain.vm.OrderVM;
import kr.co._29cm.homework.domain.vm.ProductVM;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;


public class Main {
    private static final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build(); // HTTP & HTTP2
    private static final ObjectMapper objectMapper = new ObjectMapper(); // 바인딩
    private static final String url = ServerEndPoint.LOCALHOST.getUrl(); // URL

    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("입력(o[order]: 주문, q[quit]: 종료) : ");
            String input = scanner.nextLine().trim();

            if ("q".equalsIgnoreCase(input)) {
                System.out.println("고객님의 주문 감사합니다.");
                break;
            } else if ("o".equalsIgnoreCase(input)) {
                /* 상품 목록 조회, GET */
                String uriProducts = ServerURI.PRODUCTS.getUri();
                HttpRequest request = HttpRequest.newBuilder()
                        .GET()
                        .uri(URI.create(url + uriProducts))
                        .build();

                /* 응답 */
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                /* JSON 응답 시리얼라이즈, 바인딩 */
                List<Product> products = Arrays.asList(objectMapper.readValue(response.body(), Product[].class));

                /* 상품 목록 전시 */
                System.out.println(String.format("%-10s %-65s %-30s %-20s"
                                                    , "상품번호"
                                                    , "상품명"
                                                    , "판매가격"
                                                    , "재고수"));
                for (Product product : products) {
                    /* 판매가격에서 숫자만 추출 (24,000원 -> 24000) */
                    String priceStr = product.getProductPrice().replaceAll("[^0-9]", "");
                    int price = Integer.parseInt(priceStr);

                    System.out.println(String.format("%-10d %-65s %-30d %-20d"
                                                    , product.getProductId()
                                                    , product.getProductName()
                                                    , price
                                                    , product.getProductQuantity()));
                }

                System.out.println("");

                /* 고객이 주문하는 목록을 보관하는 변수 초기화 */
                List<ProductVM> orders = new ArrayList<>();
                while (true) {
                    System.out.print("상품번호: ");
                    String productId = scanner.nextLine().trim();

                    System.out.print("수량: ");
                    String quantityStr = scanner.nextLine().trim();

                    /* 상품번호와 수량 둘 다 스페이스바 혹은 공백일 경우에 서버 요청 */
                    if (productId.isEmpty() && quantityStr.isEmpty()) {

                        /** [유효성 검사 1] 장바구니에 담은 상품이 최소 1개 이상이어야 주문 가능함 */
                        if (orders.size() == 0) {
                            System.out.println("상품번호, 수량을 최소 1건 입력해야 합니다. 다시 시도 해주세요.\n");
                            continue;
                        }

                        /* 서버에 보낼 데이터를 JSON 형식으로 시리얼라이즈, 바인딩 */
                        String jsonOrders = objectMapper.writeValueAsString(orders);

                        /* HTTP POST 요청 생성 */
                        String uriOrders = ServerURI.ORDERS.getUri();
                        HttpRequest postRequest = HttpRequest.newBuilder()
                                .POST(HttpRequest.BodyPublishers.ofString(jsonOrders)) // payload
                                .uri(URI.create(url + uriOrders))
                                .header("Content-Type", "application/json")
                                .build();

                        /* 주문 생성 요청 & 주문 결과 목록 응답, POST */
                        HttpResponse<String> postResponse = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());

                        /* 응답 JSON 문자열을 OrderVM 타입으로 시리얼라이즈, 바인딩 */
                        OrderVM orderVM = objectMapper.readValue(postResponse.body(), OrderVM.class);

                        /* 주문 결과 목록 전시 */
                        List<ProductVM> orderList = orderVM.getOrderList();
                        Collections.reverse(orderList); // 장바구니 담긴 순서의 역순 (요구사항명세서)
                        if (orderList.size() > 1) {
                            /**
                             * 다중 주문 경우 - 재고가 부족한 일부 상품 제외한 나머지는 자동 주문/결제 진행
                             */
                            System.out.println("주문 내역: ");
                            System.out.println("-------------------------------------------");

                            for (ProductVM productVM : orderList) {
                                String orderComplteStatus = productVM.getOrderStatus();
                                if (orderComplteStatus.equals(Constant.ORDER_SUCCESS_CODE)) {
                                    System.out.println(productVM.getProductName()
                                            + " - "
                                            + productVM.getOrderRequestQuantity() + "개");
                                } else if (orderComplteStatus.equals(Constant.ORDER_EXCEPTION_CODE)) {
                                    System.out.println(productVM.getProductName()
                                            + " - " + productVM.getOrderRequestQuantity()
                                            + "개 - (⚠ 재고가 부족하여 자동 취소) - "
                                            + "(" + productVM.getOrderMessage() + ")");
                                }
                            }
                        } else {
                            /**
                             * 단품 주문 경우
                             */

                            if (orderList.isEmpty()) { // IndexOutOfBoundsException
                                System.out.println("죄송합니다. 예기치 못한 오류입니다. 문제가 반복되면 고객센터로 연락부탁드립니다. 02-536-2929");
                                break;
                            }

                            ProductVM productVM = orderList.get(0);
                            String orderComplteStatus = productVM.getOrderStatus();
                            if (orderComplteStatus.equals(Constant.ORDER_SUCCESS_CODE)) {
                                System.out.println("주문 내역: ");
                                System.out.println("-------------------------------------------");
                                System.out.println(productVM.getProductName() + " - " + productVM.getOrderRequestQuantity() + "개");
                            } else if (orderComplteStatus.equals(Constant.ORDER_EXCEPTION_CODE)) {
                                System.out.println(productVM.getOrderMessage());
                                break;
                            }
                        }

                        System.out.println("-------------------------------------------");
                        
                        /* 무료배송 대상 여부에 따라 금액 전시 */
                        if (orderVM.isFreeDeliveryPrice()) { 
                            System.out.println("주문금액: " + orderVM.getOrderPrice());
                            System.out.println("-------------------------------------------");
                            System.out.println("지불금액: " + orderVM.getPaymentPrice());
                        } else {
                            System.out.println("주문금액: " + orderVM.getOrderPrice());
                            System.out.println("배송비: " + orderVM.getDeliveryPrice());
                            System.out.println("-------------------------------------------");
                            System.out.println("지불금액: " + orderVM.getPaymentPrice());
                        }

                        System.out.println("-------------------------------------------\n");

                        break;
                    }

                    /** [유효성 검사 2] 상품번호'만' 공백일 경우 */
                    if (productId.isEmpty()) {
                        System.out.println("상품번호를 입력해야 합니다. 다시 시도해주세요.");
                        continue;
                    }

                    /** [유효성 검사 3] 상품번호 유효성 검사: 숫자 형식이어야 함 */
                    try {
                        int productIdNum = Integer.parseInt(productId);
                    } catch (NumberFormatException e) {
                        System.out.println("상품번호는 숫자 형식이어야 합니다. 다시 입력해주세요.");
                        continue;
                    }

                    /** [유효성 검사 4] 존재하는 상품번호 유효성 검사: 조회한 상품 목록에 있는 상품번호여야 함 */
                    boolean exists = false;
                    for (Product product : products) {
                        if (String.valueOf(product.getProductId()).equals(productId)) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        System.out.println("상품번호 " + productId + "는 존재하지 않습니다. 다시 입력해주세요.");
                        continue;
                    }

                    /** [유효성 검사 5] 수량 유효성 검사: 숫자 형식이어야 함 */
                    int quantity;
                    try {
                        quantity = Integer.parseInt(quantityStr);
                    } catch (NumberFormatException e) {
                        System.out.println("수량은 숫자 형식이어야 합니다. 다시 입력해주세요.");
                        continue;
                    }

                    /** [유효성 검사 6] 수량은 0보다 커야 함 */
                    if (quantity <= 0) {
                        System.out.println("수량은 0보다 커야 합니다. 다시 입력해주세요.");
                        continue;
                    }
                    
                    /* 장바구니에 상품 담기 */
                    ProductVM order = new ProductVM(Integer.parseInt(productId), quantity);
                    orders.add(order);
                }
            } else {
                /** [유효성 검사 7] o 와 q 만 입력 가능 */
                System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
            }
        }

        /* 자원 종료 */
        scanner.close();
    }
}
