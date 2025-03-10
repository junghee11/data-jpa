package com.develop.batch.tasklet;

import com.develop.datajpa.dto.shop.GoodsReviewStarDto;
import com.develop.datajpa.entity.shop.Goods;
import com.develop.datajpa.entity.shop.GoodsType;
import com.develop.datajpa.repository.shop.GoodsRepository;
import com.develop.datajpa.repository.shop.GoodsReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Component
@Slf4j
@RequiredArgsConstructor
public class UpdateGoodsScoreTasklet implements Tasklet {

    private final GoodsRepository goodsRepository;
    private final GoodsReviewRepository goodsReviewRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        LocalDateTime date = contribution.getStepExecution().getJobExecution().getJobParameters().getLocalDateTime("date");
        log.info("[UPDATE_GOODS_SCORE_JOB] job start at : {}", date);

        List<Goods> goodsList = goodsRepository.findByOnSaleAndGoodsState(true, GoodsType.State.NORMAL);
        Map<String, Double> reviewSummary = goodsReviewRepository.findAverageStarsByGoods().stream()
            .collect(Collectors.toMap(GoodsReviewStarDto::getGoodsCode, GoodsReviewStarDto::getStar));

        goodsList.stream().forEach(goods -> {
            if (nonNull(reviewSummary.get(goods.getGoodsCode()))) {
                goods.setStar(reviewSummary.get(goods.getGoodsCode()));
            }
        });

        goodsRepository.saveAll(goodsList);

        log.info("[UPDATE_GOODS_SCORE_JOB] 평점 업데이트 된 상품 갯수 : {}", reviewSummary.size());

        return RepeatStatus.FINISHED;
    }

    // TODO : gitignore 안먹힘 처리 필요

}
