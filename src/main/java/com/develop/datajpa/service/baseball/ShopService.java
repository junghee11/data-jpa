package com.develop.datajpa.service.baseball;

import com.develop.datajpa.dto.user.LoginInfo;
import com.develop.datajpa.entity.Cart;
import com.develop.datajpa.entity.Goods;
import com.develop.datajpa.entity.MatchType.TeamCode;
import com.develop.datajpa.entity.QReview;
import com.develop.datajpa.entity.Restaurants;
import com.develop.datajpa.entity.Review;
import com.develop.datajpa.entity.User;
import com.develop.datajpa.entity.Wish;
import com.develop.datajpa.repository.CartRepository;
import com.develop.datajpa.repository.GoodsRepository;
import com.develop.datajpa.repository.RestaurantsRepository;
import com.develop.datajpa.repository.ReviewRepository;
import com.develop.datajpa.repository.WishRepository;
import com.develop.datajpa.request.baseball.AddCartRequest;
import com.develop.datajpa.request.baseball.LeaveReviewRequest;
import com.develop.datajpa.response.ClientException;
import com.develop.datajpa.service.user.UserService;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.develop.datajpa.service.security.JwtProvider.resolveToken;
import static com.develop.datajpa.service.security.JwtProvider.validateToken;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final GoodsRepository goodsRepository;
    private final RestaurantsRepository restaurantsRepository;
    private final UserService userService;
    private final ReviewRepository reviewRepository;
    private final WishRepository wishRepository;
    private final CartRepository cartRepository;

    @Autowired
    EntityManager em;

    public Map<String, Object> getGoodsList(TeamCode team) {
        List<Goods> goodsList = goodsRepository.findByTeamAndOnSaleOrderByIdx(team, true);

        return Map.of(
            "result", goodsList
        );
    }

    public Map<String, Object> getGoodsInfo(String token, long idx) {
        Goods goods = goodsRepository.findByIdxAndOnSaleOrderByIdx(idx, true)
            .orElseThrow(() -> new ClientException("판매중이 아니거나 존재하지 않는 상품입니다."));

        if (validateToken(token)) {
            User user = userService.checkUser(resolveToken(token).getUserId());

            Optional<Wish> wish = wishRepository.findByGoodsIdxAndUserId(idx, user.getUserId());

            return Map.of(
                "wish", wish.isPresent(),
                "result", goods
            );

        } else {
            return Map.of(
                "result", goods
            );
        }
    }

    public Map<String, Object> leaveReview(LoginInfo loginInfo, LeaveReviewRequest request) {
        userService.checkUser(loginInfo.getUserId());

        Restaurants restaurants = restaurantsRepository.findById((long) request.getId())
            .orElseThrow(() -> new ClientException("식당 정보가 확인되지 않습니다."));

        Review review = Review.builder()
            .restaurantsId(request.getId())
            .star(request.getStar())
            .content(request.getContent())
            .userId(loginInfo.getUserId())
            .build();
        reviewRepository.save(review);

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QReview r = QReview.review;

        double avg = queryFactory.select(r.star.avg())
            .from(r)
            .where(r.restaurantsId.eq(request.getId()).and(r.state.eq(0)))
            .fetchOne();

        restaurants.setStar(avg);
        restaurantsRepository.save(restaurants);

        return Map.of(
            "message", review
        );
    }

    public Map<String, Object> toggleWish(LoginInfo loginInfo, long id) {
        userService.checkUser(loginInfo.getUserId());

        goodsRepository.findById(id).orElseThrow(() -> {
            throw new ClientException("상품 정보가 확인되지 않습니다.");
        });

        Optional<Wish> wish = wishRepository.findById(id);
        if (wish.isPresent()) {
            wishRepository.delete(wish.get());

            return Map.of(
                "message", "찜 목록에서 삭제되었습니다."
            );
        } else {
            Wish newWish = Wish.builder()
                .userId(loginInfo.getUserId())
                .goodsIdx(id)
                .build();
            wishRepository.save(newWish);

            return Map.of(
                "message", "찜 목록에 추가되었습니다."
            );
        }
    }

    public Map<String, Object> addCart(LoginInfo loginInfo, AddCartRequest request) {
        userService.checkUser(loginInfo.getUserId());

        Optional<Goods> goods = goodsRepository.findById(request.getId());
        if (goods.isEmpty() || goods.get().isOnSale()) {
            throw new ClientException("판매중인 상품이 아닙니다.");
        } else if (goods.get().getStock() <= 0) {
            throw new ClientException("상품의 재고가 부족합니다입니다.");
        }

        Optional<Cart> cart = cartRepository.findById(request.getId());
        if (cart.isPresent()) {
            int count = cart.get().getCount() + request.getCount();

            if (count > 10) throw new ClientException("동시구입 최대 갯수는 10개입니다.");

            cart.get().setCount(count);
            cartRepository.save(cart.get());
        } else {
            Cart newCart = Cart.builder()
                .userId(loginInfo.getUserId())
                .goodsIdx(request.getId())
                .count(request.getCount())
                .build();
            cartRepository.save(newCart);
        }

        return Map.of(
            "message", "장바구니에 추가했습니다."
        );
    }

    public Map<String, Object> removeCart(LoginInfo loginInfo, long id) {
        userService.checkUser(loginInfo.getUserId());

        Optional<Goods> goods = goodsRepository.findById(id);
        if (goods.isEmpty() || goods.get().isOnSale()) {
            throw new ClientException("판매중인 상품이 아닙니다.");
        } else if (goods.get().getStock() <= 0) {
            throw new ClientException("상품의 재고가 부족합니다입니다.");
        }

        Cart cart = cartRepository.findById(id).orElseThrow(() -> {
            throw new ClientException("이미 삭제된 항목입니다.");
        });

        cartRepository.delete(cart);

        return Map.of(
            "message", "장바구니에서 삭제되었습니다."
        );
    }

    public Map<String, Object> clearCart(LoginInfo loginInfo) {
        userService.checkUser(loginInfo.getUserId());

        List<Cart> cartList = cartRepository.findByUserId(loginInfo.getUserId());
        if (cartList.isEmpty()) {
            throw new ClientException("장바구니가 비어있습니다.");
        }

        cartRepository.deleteAll(cartList);

        return Map.of(
            "message", "장바구니를 비웠습니다."
        );
    }

}
