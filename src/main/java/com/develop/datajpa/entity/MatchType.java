package com.develop.datajpa.entity;

public class MatchType {

    public enum TeamCode {
        ALL("ALL"),
        SAMSUNG("삼성 라이온즈"),
        SSG("신세계 랜더스"),
        KT("KT wiz"),
        LG("LG 트윈즈"),
        NC("NC 디아노스"),
        DOOSAN("두산 베어스"),
        KIWOOM("키움 히어로즈"),
        KIA("KIA 타이거즈");

        private final String value;

        TeamCode(String value) {
            this.value = value;
        }

        public String get() {
            return value;
        }
    }

    public enum MatchResult {
        INITIAL("경기 시작전"),
        HOME_WIN("홈팀 승"),
        AWAY_WIN("원정팀 승"),
        DRAW("무승부"),
        CANCELED("경기 취소");

        public static MatchResult getResult(int home, int away) {
            if (home == away) {
                return DRAW;
            } else if (home > away) {
                return HOME_WIN;
            } else {
                return AWAY_WIN;
            }
        }

        private final String value;

        MatchResult(String value) {
            this.value = value;
        }

        public String get() {
            return value;
        }
    }

}
