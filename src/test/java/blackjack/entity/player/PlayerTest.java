package blackjack.entity.player;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import blackjack.entity.card.Card;
import blackjack.entity.common.Money;
import blackjack.entity.common.Point;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PlayerTest {

    @DisplayName("플레이어는 초기 베팅 금액과 자신의 이름을 알고 있다.")
    @Test
    void playerInformation() {
        Player player = Player.of("name", 100);
        Player other = Player.of("name", 100);

        assertThat(player).isEqualTo(other);
    }

    @DisplayName("플레이어의 초기 베팅 금액은 0원 이상이어야 한다.")
    @Test
    void checkBetAmount() {
        assertThatThrownBy(() -> Player.of("name", -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("초기 베팅 금액은 적어도 0 이상이어야 합니다.");
    }

    @DisplayName("플레이어는 카드를 받아서 덱에 넣을 수 있다.")
    @Test
    void deal() {
        Player player = Player.of("lee", 100);

        player.deal(createCard());

        assertThat(player.calculateTotalPoint()).isEqualTo(5);
    }

    @DisplayName("플레이어의 상태가 스탠드나 버스트가 아닐 경우에만 신규 카드를 받을 수 있다.")
    @Test
    void checkPlayable() {
        Player player = Player.of("lee", 100);
        player.deal(createCard());
        player.deal(createCard());
        player.deal(createCard());
        player.deal(createCard());
        player.deal(createCard());

        assertThatThrownBy(() -> player.deal(createCard()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("스탠드나 버스트 상태에서는 신규 카드를 받을 수 없습니다.");
    }

    @DisplayName("플레이어는 새로 카드를 받을 수 있는데, 21점을 넘기면 버스트 상태로 변경된다.")
    @Test
    void bust() {
        Player player = Player.of("lee", 100);
        player.deal(createCard());
        player.deal(createCard());
        player.deal(createCard());
        player.deal(createCard());
        player.deal(createCard());

        assertThat(player.calculateTotalPoint()).isEqualTo(25);
        assertThat(player.isPlayable()).isFalse();
    }

    @DisplayName("플레이어의 상태를 스탠드로 변경한다")
    @Test
    void stand() {
        Player player = Player.of("lee", 100);
        player.deal(createCard());
        assertThat(player.isPlayable()).isTrue();

        player.stand();

        assertThat(player.isPlayable()).isFalse();
        assertThatThrownBy(() -> player.deal(createCard()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("스탠드나 버스트 상태에서는 신규 카드를 받을 수 없습니다.");
    }

    @DisplayName("사용자의 카드 목록을 문자열로 반환한다.")
    @Test
    void showCard() {
        Player player = Player.of("lee", 100);
        player.deal(createCard());
        player.deal(createCard());

        String result = player.showCard();

        assertThat(result).isEqualTo("5스페이드, 5스페이드");
    }

    @DisplayName("다른 사용자에게 돈을 전송한다.")
    @Test
    void transfer() {
        Player player = Player.of("lee", 1000);
        Player other = Player.of("kim", 10000);

        player.transfer(other, Money.from(1000));

        assertThat(player.getMoney()).isEqualTo(0);
        assertThat(player.getRevenue()).isEqualTo(-1000);
        assertThat(other.getMoney()).isEqualTo(11000);
        assertThat(other.getRevenue()).isEqualTo(1000);
    }

    @DisplayName("알 수 없는 사용자(null)에게 돈을 전송할 수 없다.")
    @Test
    void checkIsTransferPlayerNonNull() {
        Player player = Player.of("lee", 1000);

        assertThatThrownBy(() -> player.transfer(null, Money.from(100)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("알 수 없는 사용자에게 돈을 전송할 수 없습니다.");
    }

    @DisplayName("알 수 없는 금액(null)을 전송할 수 없다.")
    @Test
    void checkIsMoneyNonNull() {
        Player player = Player.of("lee", 1000);
        Player other = Player.of("kim", 10000);

        assertThatThrownBy(() -> player.transfer(other, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("알 수 없는 금액입니다.");
    }

    private Card createCard() {
        return new Card() {
            @Override
            public Point calculatePoint() {
                return Point.from(5);
            }

            @Override
            public String getFullName() {
                return "5스페이드";
            }
        };
    }
}
