package com.develop.datajpa.entity;

public class MatchType {

    public enum TeamType {
        ALL,
        SAMSUNG,
        SSG,
        KT,
        LG,
        NC,
        DOOSAN,
        KIWOOM,
        KIA;
    }

    public enum MatchResult {
        INITIAL,
        HOME_WIN,
        AWAY_WIN,
        DRAW,
        CANCELED;

        public static MatchResult getResult(int home, int away) {
            if (home == away) {
                return DRAW;
            } else if (home > away) {
                return HOME_WIN;
            } else {
                return AWAY_WIN;
            }
        }
    }

}
