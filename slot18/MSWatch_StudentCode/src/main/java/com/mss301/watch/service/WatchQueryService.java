package com.mss301.watch.service;
import com.mss301.watch.dto.WatchResponse;
import com.mss301.watch.repository.WatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class WatchQueryService {
    private final WatchRepository watches;
    public WatchQueryService(WatchRepository watches) { this.watches = watches; }
    @Transactional(readOnly = true) public List<WatchResponse> findAll() { return watches.findAllByOrderByWatchIdDesc().stream().map(WatchMapper::toResponse).toList(); }
    public void validateReleaseDate(LocalDate releaseDate) {
        if (!LocalDate.now().equals(releaseDate)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ReleaseDate must be the current date");
    }
}

