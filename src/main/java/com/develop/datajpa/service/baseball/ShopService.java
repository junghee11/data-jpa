package com.develop.datajpa.service.baseball;

import com.develop.datajpa.dto.shop.OrderDto;
import com.develop.datajpa.dto.user.LoginInfo;
import com.develop.datajpa.entity.User;
import com.develop.datajpa.entity.baseball.MatchType.TeamCode;
import com.develop.datajpa.entity.shop.Cart;
import com.develop.datajpa.entity.shop.Goods;
import com.develop.datajpa.entity.shop.GoodsReview;
import com.develop.datajpa.entity.shop.GoodsReviewType.State;
import com.develop.datajpa.entity.shop.OrderMenuRepository;
import com.develop.datajpa.entity.shop.QOrderMenu;
import com.develop.datajpa.entity.shop.QReceipt;
import com.develop.datajpa.entity.shop.ReceiptRepository;
import com.develop.datajpa.entity.shop.Wish;
import com.develop.datajpa.repository.CartRepository;
import com.develop.datajpa.repository.UserRepository;
import com.develop.datajpa.repository.shop.GoodsRepository;
import com.develop.datajpa.repository.shop.GoodsReviewRepository;
import com.develop.datajpa.repository.shop.WishRepository;
import com.develop.datajpa.request.baseball.GetGoodsListRequest;
import com.develop.datajpa.request.shop.AddCartRequest;
import com.develop.datajpa.request.shop.LeaveGoodsReviewRequest;
import com.develop.datajpa.request.shop.ModifyGoodsReviewRequest;
import com.develop.datajpa.request.shop.PurchaseGoodsRequest;
import com.develop.datajpa.response.ClientException;
import com.develop.datajpa.service.user.UserService;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.develop.datajpa.service.security.JwtProvider.resolveToken;
import static com.develop.datajpa.service.security.JwtProvider.validateToken;
import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final GoodsRepository goodsRepository;
    private final UserService userService;
    private final WishRepository wishRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ReceiptRepository receiptRepository;
    private final OrderMenuRepository orderMenuRepository;
    private final GoodsReviewRepository goodsReviewRepository;

    @Autowired
    EntityManager em;

    public Map<String, Object> getGoodsList(GetGoodsListRequest request) {
        List<Goods> goodsList;
        if (TeamCode.ALL.name().equals(request.getTeam().toUpperCase())) {
            goodsList = goodsRepository.findByOnSale
                (true, PageRequest.of(request.getPage() - 1, 10, Sort.by("idx").descending()));
        } else {
            goodsList = goodsRepository.findByTeamAndOnSale
                (request.getTeam(), true, PageRequest.of(request.getPage() - 1,
                    10, Sort.by("idx").descending()));
        }

        return Map.of(
            "result", goodsList
        );
    }

    public Map<String, Object> getGoodsInfo(String token, String id) {
        Goods goods = goodsRepository.findByGoodsCodeAndOnSaleOrderByCreatedAt(id, true)
            .orElseThrow(() -> new ClientException("판매중이 아니거나 존재하지 않는 상품입니다."));

        if (validateToken(token)) {
            User user = userService.checkUser(resolveToken(token).getUserId());

            Optional<Wish> wish = wishRepository.findByGoodsCodeAndUserId(id, user.getUserId());

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

    public Map<String, Object> toggleWish(LoginInfo loginInfo, String id) {
        userService.checkUser(loginInfo.getUserId());

        goodsRepository.findByGoodsCode(id).orElseThrow(() -> {
            throw new ClientException("상품 정보가 확인되지 않습니다.");
        });

        Optional<Wish> wish = wishRepository.findByGoodsCodeAndUserId(id, loginInfo.getUserId());
        if (wish.isPresent()) {
            wishRepository.delete(wish.get());

            return Map.of(
                "message", "찜 목록에서 삭제되었습니다."
            );
        } else {
            Wish newWish = Wish.builder()
                .userId(loginInfo.getUserId())
                .goodsCode(id)
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

    @Transactional
    public Map<String, Object> PurchaseGoods(LoginInfo loginInfo, PurchaseGoodsRequest request) {
        User user = userService.checkUser(loginInfo.getUserId());

        Goods goods = goodsRepository.findById(request.getId())
            .orElseThrow(() -> new ClientException("제품 정보가 확인되지 않습니다."));

        String orderCode = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmSS")) + loginInfo.getUserId();
        long price = goods.getPrice() * request.getCount();
        if (goods.isOnSale()) {
            price = (long) (price * ((100 - goods.getDiscountRate()) / 100));
        }

//        Receipt order = Receipt.builder()
//            .receiptCode(orderCode)
//            .userId(loginInfo.getUserId())
//            .payment(request.getPayType().getValue())
//            .payId(request.getPayId()) // TODO : 결제 api 연결 후 재점검 필요..
//            .totalPrice(price)
//            .build();
//        receiptRepository.save(order);
//
//        OrderMenu orderMenu = OrderMenu.builder()
//            .goodIdx(goods.getIdx())
//            .price(price / request.getCount())
//            .count(request.getCount())
//            .build();
//        orderMenuRepository.save(orderMenu);
//
//        if (goods.getPointRate() > 0) {
//            user.updatePoint((long) (price * goods.getPointRate()));
//            userRepository.save(user);
//        }
//
//        goods.updateStock(-request.getCount());
//        goodsRepository.save(goods);

        return Map.of(
            "message", "결제가 완료되었습니다"
        );
    }

    @Transactional
    public Map<String, Object> orderShoppingCart(LoginInfo loginInfo, long id) {
        userService.checkUser(loginInfo.getUserId());

        // TODO : PurchaseGoods api 완성 후 작성하기

        return Map.of(
            "message", "결제가 완료되었습니다"
        );
    }

    @Transactional
    public Map<String, Object> leaveReview(LoginInfo loginInfo, LeaveGoodsReviewRequest request) {
        userService.checkUser(loginInfo.getUserId());

        goodsRepository.findById(request.getId())
            .orElseThrow(() -> new ClientException("제품 정보가 확인되지 않습니다."));

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QReceipt r = QReceipt.receipt;
        QOrderMenu o = QOrderMenu.orderMenu;

        OrderDto order = queryFactory.select(Projections.constructor(OrderDto.class, r, o))
            .from(r).join(o).on(r.receiptCode.eq(o.receiptCode))
            .where(r.userId.eq(loginInfo.getUserId())
                .and(o.goodIdx.eq(request.getId()))
                .and(o.review.eq(false)))
            .fetchOne();
        if (isNull(order)) {
            throw new ClientException("구입 기록이 확인되지 않습니다");
        }

        queryFactory.update(o)
            .set(o.review, true)
            .where(o.orderMenuIdx.eq(order.getOrderMenuIdx()))
            .execute();

        GoodsReview review = GoodsReview.builder()
            .goodsId(request.getId())
            .star(request.getStar())
            .content(request.getContent())
            .userId(loginInfo.getUserId())
            .build();
        goodsReviewRepository.save(review);

        // TODO : batch 모듈 추가하면 리뷰 평점 재평균 내주는 job 추가하가

        return Map.of(
            "message", review
        );
    }


    @Transactional
    public Map<String, Object> modifyGoodsReview(LoginInfo loginInfo, ModifyGoodsReviewRequest request) {
        userService.checkUser(loginInfo.getUserId());

        GoodsReview review = goodsReviewRepository.findById(request.getId())
            .orElseThrow(() -> new ClientException("리뷰가 확인되지 않습니다."));

        review.setStar(request.getStar());
        review.setContent(request.getContent());
        goodsReviewRepository.save(review);

        return Map.of(
            "message", "수정이 완료되었습니다"
        );
    }

    @Transactional
    public Map<String, Object> deleteReview(LoginInfo loginInfo, long id) {
        userService.checkUser(loginInfo.getUserId());

        GoodsReview review = goodsReviewRepository.findById(id)
            .orElseThrow(() -> new ClientException("리뷰가 확인되지 않습니다."));

        review.setState(State.REMOVED.ordinal());
        goodsReviewRepository.save(review);

        return Map.of(
            "message", "리뷰가 삭제되었습니다"
        );
    }

}
