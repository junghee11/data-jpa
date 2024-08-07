package com.develop.datajpa.service.admin;

import com.develop.datajpa.entity.MatchSchedule;
import com.develop.datajpa.entity.MatchType.MatchResult;
import com.develop.datajpa.repository.MatchScheduleRepository;
import com.develop.datajpa.request.admin.RecordMatchResultRequest;
import com.develop.datajpa.response.ClientException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final MatchScheduleRepository matchScheduleRepository;

    public Map<String, Object> recordMatchResult(RecordMatchResultRequest request) {
        MatchSchedule match = matchScheduleRepository.findById(request.getIdx())
            .orElseThrow(() -> new ClientException("경기정보가 확인되지 않습니다."));

        LocalDateTime matchTime = LocalDateTime.of(match.getMatchDate(), match.getMatchTime());
        if (LocalDateTime.now().isBefore(matchTime)) {
            throw new ClientException("경기가 아직 시작되지 않았습니다.");
        }

        match.setHomeScore(request.getHome());
        match.setAwayScore(request.getAway());
        match.setMatchResult(MatchResult.getResult(request.getHome(), request.getAway()));

        matchScheduleRepository.save(match);

        return Map.of(
            "message", "경기결과가 입력되었습니다."
        );
    }

    public Map<String, Object> cancelMatch(long id) {
        MatchSchedule match = matchScheduleRepository.findByIdxAndMatchResult(id, MatchResult.INITIAL)
            .orElseThrow(() -> new ClientException("이미 처리되었거나 존재하지 않은 경기번호 입니다."));

        match.setMatchResult(MatchResult.CANCELED);
        matchScheduleRepository.save(match);

        return Map.of(
            "message", "경기결과가 입력되었습니다."
        );
    }
}
