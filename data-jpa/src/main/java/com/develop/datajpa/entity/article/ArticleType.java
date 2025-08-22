package com.develop.datajpa.entity.article;

public class ArticleType {

    public enum Category {
        ALL,
        NOTICE,
        FOOD,
        GOODS;
        public static final int[] USER_ARTICLE = {FOOD.ordinal(), GOODS.ordinal()};

    }

    public enum ArticleState {
        ACTIVE,
        REMOVED,
        BLOCKED

    }

    public enum CommentState {
        ACTIVE,
        REMOVED,
        BLOCKED

    }

    public enum Recommend {
        UP,
        DOWN

    }

}
