package com.develop.datajpa.entity;

public class ArticleType {

    public enum Category {
        NOTICE,
        FOOD,
        GOODS;
        public static final int[] USER_ARTICLE = {FOOD.ordinal(), GOODS.ordinal()};

    }

    public enum State {
        ACTIVE,
        REMOVED;

    }

}
