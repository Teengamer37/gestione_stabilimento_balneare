package com.example.s_balneare.application.port.in;

//interfaccia da implementare in un service
public interface CreateBeachUseCase {
    int createBeach(CreateBeachCommand command);
}