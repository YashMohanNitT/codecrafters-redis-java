public class Value {
    Long expireAt;
    String value;

    public Value(String value, Long expireAt) {
        this.value = value;
        this.expireAt = expireAt;
    }

    public Long getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(Long expireAt) {
        this.expireAt = expireAt;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
