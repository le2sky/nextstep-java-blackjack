package blackjack.card;

public enum FaceShape {

    KING("K"),
    JACK("J"),
    QUEEN("Q");

    private final String value;

    FaceShape(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
