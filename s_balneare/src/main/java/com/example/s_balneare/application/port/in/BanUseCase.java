package com.example.s_balneare.application.port.in;

import com.example.s_balneare.domain.beach.Beach;
import com.example.s_balneare.domain.moderation.Ban;

import java.util.List;

public interface BanUseCase {
    public Integer createBan(CreateBanCommand command);
    public boolean isUserBannedFromApp(Integer customerId); // COntrolla se un owner o user è bannato
    public boolean isCustomerBannedFromBeach(Integer customerId, Integer beachId); // controlla se un customer è bannato da una spiaggia
}
