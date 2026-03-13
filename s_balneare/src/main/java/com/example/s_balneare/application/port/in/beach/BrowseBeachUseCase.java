package com.example.s_balneare.application.port.in.beach;

import com.example.s_balneare.application.port.out.beach.BeachSummary;

import java.util.List;

public interface BrowseBeachUseCase {
    List<BeachSummary> getActiveBeaches();
    List<BeachSummary> searchActiveBeaches(String keyword);
}