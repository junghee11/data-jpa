package com.develop.datajpa.service.mypage;

import com.develop.domain.dto.shop.MyCartDto;
import com.develop.domain.dto.shop.MyPurchaseDto;
import com.develop.domain.dto.user.LoginInfo;
import com.develop.domain.entity.article.Article;
import com.develop.domain.entity.article.ArticleType.ArticleState;
import com.develop.domain.entity.article.ArticleType.CommentState;
import com.develop.domain.entity.article.Comment;
import com.develop.domain.entity.baseball.Player;
import com.develop.domain.entity.baseball.Stadium;
import com.develop.domain.entity.baseball.Team;
import com.develop.domain.entity.shop.Goods;
import com.develop.domain.entity.shop.GoodsType;
import com.develop.domain.entity.shop.OrderMenu;
import com.develop.domain.entity.shop.OrderMenuRepository;
import com.develop.domain.entity.shop.Receipt;
import com.develop.domain.entity.shop.ReceiptRepository;
import com.develop.domain.entity.shop.Wish;
import com.develop.domain.entity.user.User;
import com.develop.domain.repository.article.ArticleRepository;
import com.develop.domain.repository.article.CommentRepository;
import com.develop.domain.repository.baseball.PlayerRepository;
import com.develop.domain.repository.baseball.StadiumRepository;
import com.develop.domain.repository.baseball.TeamRepository;
import com.develop.domain.repository.shop.CartRepository;
import com.develop.domain.repository.shop.GoodsRepository;
import com.develop.domain.repository.shop.WishRepository;
import com.develop.domain.repository.user.UserRepository;
import com.develop.datajpa.request.mypage.SelectMyTeamRequest;
import com.develop.core.exception.ClientException;
import com.develop.datajpa.service.image.ImageService;
import com.develop.datajpa.service.user.UserService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class MypageService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final StadiumRepository stadiumRepository;
    private final PlayerRepository playerRepository;
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final GoodsRepository goodsRepository;
    private final CartRepository cartRepository;
    private final WishRepository wishRepository;
    private final OrderMenuRepository orderMenuRepository;
    private final ReceiptRepository receiptRepository;
    private final ImageService imageService;

    @Autowired
    EntityManager em;

    public Map<String, Object> getMyTeamInfo(LoginInfo loginInfo) {
        User user = userService.checkUser(loginInfo.getUserId());
        if (isNull(user.getTeam())) {
            throw new ClientException("응원하는 팀이 없습니다. 응원하는 팀을 선택해주세요!");
        }

        Team myTeam = teamRepository.findByTeamCode(user.getTeam())
            .orElseThrow(() -> new ClientException("팀정보가 확인되지 않습니다."));

        return Map.of(
            "result", myTeam
        );
    }

    public Map<String, Object> selectMyTeam(LoginInfo loginInfo, SelectMyTeamRequest request) {
        User user = userService.checkUser(loginInfo.getUserId());
        if (nonNull(user.getTeam()) && user.getTeam() == request.getTeam()) {
            throw new ClientException("이미 응원하는 중이신 팀이에요!ㅎㅎ");
        }

        teamRepository.findByTeamCode(request.getTeam())
            .orElseThrow(() -> new ClientException("팀정보가 확인되지 않습니다."));

        user.setTeam(request.getTeam());
        userRepository.save(user);

        return Map.of(
            "message", "응원팀 설정이 완료되었습니다"
        );
    }

    public Map<String, Object> getMyStadiumList(LoginInfo loginInfo) {
        User user = userService.checkUser(loginInfo.getUserId());

        List<Stadium> myStadiumList = stadiumRepository.findByIdxIn(user.getStadium());

        return Map.of(
            "result", myStadiumList
        );
    }

    public Map<String, Object> toggleStadium(LoginInfo loginInfo, int id) {
        User user = userService.checkUser(loginInfo.getUserId());

        stadiumRepository.findByIdx(id).orElseThrow(() -> new ClientException("경기장 정보가 확인되지 않습니다."));

        boolean toggle = true;

        if (isNull(user.getStadium())) {
            user.setStadium(new int[]{id});
        } else {
            Set<Integer> stadiumSet = Arrays.stream(user.getStadium())
                .boxed()
                .collect(Collectors.toCollection(HashSet::new));

            if (!stadiumSet.add(id)) {
                toggle = !toggle;
                stadiumSet.remove(id);
            }

            user.setStadium(stadiumSet.stream().mapToInt(Integer::intValue).toArray());
        }

        userRepository.save(user);

        return Map.of(
            "toggle", toggle
        );
    }

    public Map<String, Object> getMyPlayerList(LoginInfo loginInfo) {
        User user = userService.checkUser(loginInfo.getUserId());

        List<Player> myPlayerList = playerRepository.findByIdxIn(user.getPlayer());

        return Map.of(
            "result", myPlayerList
        );
    }

    public Map<String, Object> togglePlayer(LoginInfo loginInfo, int id) {
        User user = userService.checkUser(loginInfo.getUserId());

        playerRepository.findByIdx(id).orElseThrow(() -> new ClientException("선수 정보가 확인되지 않습니다."));

        boolean toggle = true;

        if (isNull(user.getPlayer())) {
            user.setPlayer(new int[]{id});
        } else {
            Set<Integer> playerSet = Arrays.stream(user.getPlayer())
                .boxed()
                .collect(Collectors.toCollection(HashSet::new));

            if (!playerSet.add(id)) {
                toggle = false;
                playerSet.remove(id);
            }

            user.setPlayer(playerSet.stream().mapToInt(Integer::intValue).toArray());
        }

        userRepository.save(user);

        return Map.of(
            "toggle", toggle
        );
    }

    public Map<String, Object> getMyArticleList(LoginInfo loginInfo) {
        User user = userService.checkUser(loginInfo.getUserId());

        List<Article> myArticleList = articleRepository.findByUserIdAndStateOrderByCreatedAtDesc
            (user.getUserId(), ArticleState.ACTIVE.ordinal());

        return Map.of(
            "result", myArticleList
        );
    }

    public Map<String, Object> getMyCommentList(LoginInfo loginInfo) {
        User user = userService.checkUser(loginInfo.getUserId());

        List<Comment> myCommentList = commentRepository.findByUserIdAndStateOrderByCreatedAtDesc
            (user.getUserId(), CommentState.ACTIVE.ordinal());

        return Map.of(
            "result", myCommentList
        );
    }

    public Map<String, Object> getMyCart(LoginInfo loginInfo) {
        userService.checkUser(loginInfo.getUserId());

        List<String> cartGoodsCodeList = new ArrayList<>();
        HashMap<String, Integer> cartMap = new HashMap<>();
        cartRepository.findByUserId(loginInfo.getUserId()).forEach(cart -> {
            cartGoodsCodeList.add(cart.getGoodsCode());
            cartMap.put(cart.getGoodsCode(), cart.getCount());
        });

        List<MyCartDto> myCartList = goodsRepository.findByGoodsCodeInAndGoodsState
            (cartGoodsCodeList, GoodsType.State.NORMAL).stream().map(goods -> {
                return new MyCartDto(goods, cartMap.get(goods.getGoodsCode()));
            }).toList();

        return Map.of(
            "result", myCartList
        );
    }

    public Map<String, Object> getMyWishList(LoginInfo loginInfo) {
        userService.checkUser(loginInfo.getUserId());

        List<String> wishGoodsCodeList = wishRepository.findByUserId
            (loginInfo.getUserId()).stream().map(Wish::getGoodsCode).toList();

        List<Goods> myWishList = goodsRepository.findByGoodsCodeInAndGoodsState
            (wishGoodsCodeList, GoodsType.State.NORMAL);

        return Map.of(
            "result", myWishList
        );
    }

    public Map<String, Object> getMyPurchaseList(LoginInfo loginInfo) {
       userService.checkUser(loginInfo.getUserId());

        List<Receipt> receipts = receiptRepository.findByUserIdAndStatus(loginInfo.getUserId(), true);

        return Map.of(
            "result", receipts
        );
    }

    public Map<String, Object> getMyPurchaseDetail(LoginInfo loginInfo, String receiptCode) {
        userService.checkUser(loginInfo.getUserId());

        receiptRepository.findByReceiptCode(receiptCode)
            .orElseThrow(() -> new ClientException("구매정보가 확인되지 않습니다."));

        List<String> purchaseGoodsCodeList = new ArrayList<>();
        HashMap<String, OrderMenu> orderMenuMap = new HashMap<>();
        orderMenuRepository.findByReceiptCode(receiptCode).forEach(menu -> {
            purchaseGoodsCodeList.add(menu.getGoodsCode());
            orderMenuMap.put(menu.getGoodsCode(), menu);
        });

        List<MyPurchaseDto> result = goodsRepository.findByGoodsCodeInAndGoodsState
            (purchaseGoodsCodeList, GoodsType.State.NORMAL).stream().map(goods -> {
            return new MyPurchaseDto(goods, orderMenuMap.get(goods.getGoodsCode()));
        }).toList();

        return Map.of(
            "result", result
        );
    }

    public Map<String, Object> changeProfileImage(LoginInfo loginInfo, MultipartFile file) {
        User user = userService.checkUser(loginInfo.getUserId());

        String userImgCategory = "user/profile/";
        String imgUrl = imageService.upload(userImgCategory, file);

        user.setProfileImgUrl(imgUrl);
        userRepository.save(user);

        return Map.of(
            "message", "프로필 이미지가 변경되었습니다",
            "imageUrl", imgUrl
        );
    }
}
