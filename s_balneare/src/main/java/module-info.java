module com.example.s_balneare {
    requires java.sql;

    exports com.example.s_balneare;
    exports com.example.s_balneare.domain.user;
    exports com.example.s_balneare.domain.beach;
    exports com.example.s_balneare.domain.layout;
    exports com.example.s_balneare.domain.booking;
    exports com.example.s_balneare.domain.review;
    exports com.example.s_balneare.domain.moderation;

    exports com.example.s_balneare.application.auth;
    exports com.example.s_balneare.application.beach;
    exports com.example.s_balneare.application.booking;
    exports com.example.s_balneare.application.review;
    exports com.example.s_balneare.application.moderation;
}