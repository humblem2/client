package kr.co._29cm.homework.common;

/**
 * 리소스가 어디에 위치하는지를 나타내는 [URL] 을 추상화/캡슐화한 Enum
 * -------------------------------------------------------
 * Open Closed Principle(OCP)에 부합.
 * 클래스는 확장에는 열려 있어야 하지만 코드 변경에는 닫혀 있어야 한다는 원칙.
 * URI가 추가/변경 될 경우 enum 클래스만 수정하면 됨.
 * 다른 클래스에서는 코드 변경 없이 해당 enum을 사용 가능.
 * 유지보수 재사용 확장에 좋음.
 */
public enum ServerEndPoint {
    LOCALHOST("http://127.0.0.1:8080");

    private String url;

    ServerEndPoint(String url) {
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }
}
